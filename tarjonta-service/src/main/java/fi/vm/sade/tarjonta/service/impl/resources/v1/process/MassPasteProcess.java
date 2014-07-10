/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1.process;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.MassakopiointiDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCopyProcess.COUNT_HAKUKOHDE;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCopyProcess.COUNT_KOMOTO;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCopyProcess.TOTAL_HAKUKOHDE;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCopyProcess.TOTAL_KOMOTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class MassPasteProcess implements ProcessDefinition {

    private static final int FLUSH_SIZE = 100;
    private static final Logger LOG = LoggerFactory.getLogger(MassPasteProcess.class);
    public static final String TARGET_HAKU_OID = "haku.oid.to";
    public static final String SELECTED_PROCESS_COPY_ID = "process.copy.id";

    private ProcessV1RDTO state;

    private long startTs = 0L;
    private int howManySecondsToRun = 0;

    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired(required = true)
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired(required = true)
    private MassakopiointiDAO massakopiointi;

    @Autowired(required = true)
    private HakukohdeDAO hakukohdeDAO;

    @Autowired(required = true)
    private HakuDAO hakuDAO;

    private int countHakukohde = 0;
    private int countKomoto = 0;
    private int countTotalHakukohde = 0;
    private int countTotalKomoto = 0;

    public MassPasteProcess() {
        super();

    }

    @Override
    public ProcessV1RDTO getState() {
        state.getParameters().put(COUNT_HAKUKOHDE, countHakukohde + "");
        state.getParameters().put(COUNT_KOMOTO, countKomoto + "");
        state.getParameters().put(TOTAL_HAKUKOHDE, countTotalHakukohde + "");
        state.getParameters().put(TOTAL_KOMOTO, countTotalKomoto + "");
        state.setState(calcPercentage());
        return state;
    }

    @Override
    public void setState(ProcessV1RDTO state) {
        this.state = state;
    }

    @Override
    public void run() {
        final String targetHakuOid = getState().getParameters().get(TARGET_HAKU_OID);
        final String processId = getState().getParameters().get(SELECTED_PROCESS_COPY_ID);
        LOG.info("MassPasteProcess.run(), params target haku oid : '{}', process id '{}'", targetHakuOid, processId);

        try {
            startTs = System.currentTimeMillis();
            LOG.info("start()... {}", startTs);
            List<String> oldKomotoOids = massakopiointi.searchOids(
                    new MassakopiointiDAO.SearchCriteria(null, null, null,
                            Massakopiointi.Tyyppi.KOMOTO_ENTITY,
                            processId,
                            Massakopiointi.KopioinninTila.READY_FOR_COPY));

            List<String> oldHakukohdeOids = massakopiointi.searchOids(
                    new MassakopiointiDAO.SearchCriteria(null, null, null,
                            Massakopiointi.Tyyppi.HAKUKOHDE_ENTITY,
                            processId,
                            Massakopiointi.KopioinninTila.READY_FOR_COPY));

            countTotalKomoto = oldKomotoOids.size();
            countTotalHakukohde = oldHakukohdeOids.size();

            /*
             * KOMOTO INSERT 
             */
            Set<String> oidBatch = Sets.<String>newHashSet();
            for (String oldKomotoOid : oldKomotoOids) {
                if (countKomoto % FLUSH_SIZE == 0 || oldKomotoOids.size() - 1 == countKomoto) {
                    insertKomotoBatch(processId, oidBatch);
                    oidBatch = Sets.<String>newHashSet();
                }

                oidBatch.add(oldKomotoOid);
                countKomoto++;
            }

            /*
             * HAKUKOHDE INSERT 
             */
            oidBatch = Sets.<String>newHashSet();
            for (String oldHakukohdeOid : oldHakukohdeOids) {
                if (countHakukohde % FLUSH_SIZE == 0 || oldKomotoOids.size() - 1 == countHakukohde) {
                    insertHakukohdeBatch(processId, oidBatch, targetHakuOid);
                    oidBatch = Sets.<String>newHashSet();
                }

                oidBatch.add(oldHakukohdeOid);
                countHakukohde++;
            }

            getState().getParameters().put("result", "success");
        } catch (Throwable ex) {
            LOG.error("Copy failed", ex);

            getState().setMessageKey("my.test.process.error");
            getState().getParameters().put("result", ex.getMessage());
        } finally {
            getState().setState(100.0);
        }

        LOG.info("run()... done.");
    }

    @Autowired
    private PlatformTransactionManager tm;

    @Transactional(readOnly = false)
    private void insertKomotoBatch(final String processId, final Set<String> oldOids) {
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                insertKomotos(oldOids, processId);
            }
        });
    }

    @Transactional(readOnly = false)
    private void insertHakukohdeBatch(final String processId, final Set<String> oldOids, final String targetHakuOid) {
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                insertHakukohdes(oldOids, processId, targetHakuOid);
            }
        });
    }

    private void executeInTransaction(final Runnable runnable) {
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                runnable.run();
                return null;
            }
        });
    }

    private void insertKomotos(final Set<String> oldOids, final String processId) {
        final Date processing = new Date();
        LOG.info("insert koulutus batch size of : {}/{}", oldOids.size(), countKomoto);

        for (final String oldKomoOid : oldOids) {
            try {
                massakopiointi.updateTila(processId, oldKomoOid, Massakopiointi.KopioinninTila.PROSESSING, processing);
                Pair<Object, MetaObject> find = massakopiointi.find(processId, oldKomoOid, KoulutusmoduuliToteutus.class);
                KoulutusmoduuliToteutus komoto = (KoulutusmoduuliToteutus) find.getFirst();
                final MetaObject meta = find.getSecond();

                LOG.debug("convert json to entity by oid : {}, new oid : {}", oldKomoOid, meta.getNewKomotoOid());
                komoto.setOid(meta.getNewKomotoOid());
                komoto.setTila(TarjontaTila.KOPIOITU);
                komoto.setKoulutusmoduuli(koulutusmoduuliDAO.findByOid(meta.getOriginalKomoOid()));
                komoto.setUlkoinenTunniste(processId);

                Set<Date> koulutuksenAlkamisPvms = komoto.getKoulutuksenAlkamisPvms();
                if (koulutuksenAlkamisPvms != null) {
                    Set<Date> plusYears = Sets.<Date>newHashSet();
                    for (Date orgDate : koulutuksenAlkamisPvms) {
                        plusYears.add(dateToNext(orgDate));
                        komoto.setAlkamisVuosi(IndexDataUtils.parseYearInt(orgDate));
                        komoto.setAlkamiskausiUri(IndexDataUtils.parseKausiKoodi(orgDate));
                    }
                    komoto.setKoulutuksenAlkamisPvms(plusYears);
                }

                koulutusmoduuliToteutusDAO.insert(komoto);
                massakopiointi.updateTila(processId, oldKomoOid, Massakopiointi.KopioinninTila.COPIED, processing);
            } catch (Exception e) {
                massakopiointi.updateTila(processId, oldKomoOid, Massakopiointi.KopioinninTila.ERROR, processing);
            }
        }
    }

    private void insertHakukohdes(Set<String> oldOids, String processId, String targetHakuOid) {
        final Date processing = new Date();
        LOG.info("insert hakukohde batch size of : {}/{}", oldOids.size(), countHakukohde);
        Haku targetHaku = hakuDAO.findByOid(targetHakuOid);

        for (String oldHakukohdeOid : oldOids) {
            try {
                massakopiointi.updateTila(processId, oldHakukohdeOid, Massakopiointi.KopioinninTila.PROSESSING, processing);
                Pair<Object, MetaObject> pair = massakopiointi.find(processId, oldHakukohdeOid, Hakukohde.class);
                Hakukohde hk = (Hakukohde) pair.getFirst();

                hk.setHaku(targetHaku);
                targetHaku.addHakukohde(hk);

                final MetaObject meta = pair.getSecond();

                LOG.debug("convert json to entity by oid : {}, new oid : {}", oldHakukohdeOid, meta.getNewHakukohdeOid());
                hk.setOid(meta.getNewHakukohdeOid());
                hk.setTila(TarjontaTila.KOPIOITU);
                hk.setUlkoinenTunniste(processId);
                List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(Lists.<String>newArrayList(meta.getKomotoOids()));

                for (KoulutusmoduuliToteutus k : komotos) {
                    hk.addKoulutusmoduuliToteutus(k);
                    k.addHakukohde(hk);
                }

                if (hk.getHakuaikaAlkuPvm() != null) {
                    hk.setHakuaikaAlkuPvm(dateToNext(hk.getHakuaikaAlkuPvm()));
                }

                if (hk.getHakuaikaLoppuPvm() != null) {
                    hk.setHakuaikaLoppuPvm(dateToNext(hk.getHakuaikaLoppuPvm()));
                }

                hakukohdeDAO.insert(hk);
                massakopiointi.updateTila(processId, oldHakukohdeOid, Massakopiointi.KopioinninTila.COPIED, processing);
            } catch (Exception e) {
                massakopiointi.updateTila(processId, oldHakukohdeOid, Massakopiointi.KopioinninTila.ERROR, processing);
            }
        }
    }

    private Date dateToNext(Date date) {
        DateTime dateTime = new DateTime(date);
        dateTime.plusYears(1);
        return dateTime.toDate();
    }

    @Override
    public boolean canStop() {
        return true;
    }

    @Override
    public boolean isCompleted() {
        return getState().getState() == 100.0;
    }

    private double calcPercentage() {
        if (countTotalHakukohde + countTotalKomoto > 0) {
            return (countHakukohde + countKomoto * 100 / countTotalHakukohde + countTotalKomoto);
        }
        return 0;
    }
}
