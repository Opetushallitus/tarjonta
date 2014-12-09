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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakuaikaDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.MassakopiointiDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.copy.EntityToJsonHelper;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class MassCommitProcess {

    private static final int BATCH_KOMOTO_SIZE = 100;
    private static final int BATCH_HAKUKOHDE_SIZE = 50;
    private static final Logger LOG = LoggerFactory.getLogger(MassCommitProcess.class);

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
    private HakuaikaDAO hakuaikaDAO;

    @Autowired(required = true)
    private IndexerResource indexerResource;

    @Autowired(required = true)
    private HakuDAO hakuDAO;

    @Autowired(required = true)
    private OidService oidService;

    private int countHakukohde = 0;
    private int countKomoto = 0;
    private int countTotalHakukohde = 0;
    private int countTotalKomoto = 0;
    private String targetHakuoid = null;

    private Set<Long> indexHakukohdeIds = Sets.<Long>newHashSet();
    private Set<Long> indexKomotoIds = Sets.<Long>newHashSet();
    private boolean completed = false;

    //hakuajat
    private Map<Long, Hakuaika> hakuaikas = Maps.newHashMap();

    public ProcessV1RDTO getState() {
        state.getParameters().put(MassCopyProcess.COMMIT_COUNT_HAKUKOHDE, countHakukohde + "");
        state.getParameters().put(MassCopyProcess.COMMIT_COUNT_KOMOTO, countKomoto + "");
        state.getParameters().put(MassCopyProcess.COMMIT_TOTAL_HAKUKOHDE, countTotalHakukohde + "");
        state.getParameters().put(MassCopyProcess.COMMIT_TOTAL_KOMOTO, countTotalKomoto + "");
        state.setState(calcPercentage());
        return state;
    }

    public void setState(ProcessV1RDTO state) {
        this.state = state;
    }

    public void run() {
        countHakukohde = 0;
        countKomoto = 0;
        countTotalHakukohde = 0;
        countTotalKomoto = 0;

        final String fromHakuOid = getState().getParameters().get(MassCopyProcess.SELECTED_HAKU_OID);
        final String processId = getState().getId();
        LOG.info("MassCommitProcess.run(), params target haku oid : '{}', process id '{}'", fromHakuOid, processId);

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

            insertHaku(fromHakuOid);

            /*
             * KOMOTO INSERT
             */
            Set<String> oidBatch = Sets.<String>newHashSet();
            for (String oldKomotoOid : oldKomotoOids) {
                if (countKomoto % BATCH_KOMOTO_SIZE == 0 || oldKomotoOids.size() - 1 == countKomoto) {
                    insertKomotoBatch(processId, oidBatch);
                    oidBatch = Sets.<String>newHashSet();
                }

                oidBatch.add(oldKomotoOid);
                countKomoto++;
            }
            insertKomotoBatch(processId, oidBatch);

            /*
             * HAKUKOHDE INSERT
             */
            oidBatch = Sets.<String>newHashSet();
            for (String oldHakukohdeOid : oldHakukohdeOids) {
                if (countHakukohde % BATCH_HAKUKOHDE_SIZE == 0 || oldKomotoOids.size() - 1 == countHakukohde) {
                    insertHakukohdeBatch(processId, targetHakuoid, oidBatch);
                    oidBatch = Sets.<String>newHashSet();
                }

                oidBatch.add(oldHakukohdeOid);
                countHakukohde++;
            }
            insertHakukohdeBatch(processId, targetHakuoid, oidBatch);
            removeBatch(processId, fromHakuOid);

            indexerResource.indexKoulutukset(Lists.newArrayList(indexKomotoIds));
            indexerResource.indexHakukohteet(Lists.newArrayList(indexHakukohdeIds));

            getState().getParameters().put("result", "success");
        } catch (Throwable ex) {
            LOG.error("Copy failed", ex);

            getState().setMessageKey("my.test.process.error");
            getState().getParameters().put("result", ex.getMessage());
        } finally {
            completed = true;
        }

        LOG.info("run()... done.");
    }

    @Autowired
    private PlatformTransactionManager tm;

    @Transactional(readOnly = false)
    private void insertHaku(final String oldHakuOid) {
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                final Haku sourceHaku = hakuDAO.findByOid(oldHakuOid);
                String hakuJson = EntityToJsonHelper.convertToJson(sourceHaku);

                final Haku haku = (Haku) EntityToJsonHelper.convertToEntity(hakuJson, Haku.class);
                haku.setTila(TarjontaTila.KOPIOITU);
                try {
                    haku.setOid(oidService.get(TarjontaOidType.HAKU));
                } catch (OIDCreationException ex) {
                    LOG.error("OidService failed", ex);
                }


                for (Hakuaika hakuaika : haku.getHakuaikas()) {
                    hakuaikas.put(hakuaika.getId(), hakuaika);
                    hakuaika.setId(null);
                    hakuaika.setHaku(haku);
                    if (hakuaika.getPaattymisPvm() != null) {
                        hakuaika.setPaattymisPvm(dateToNextYear(hakuaika.getPaattymisPvm()));
                    }

                    if (hakuaika.getAlkamisPvm() != null) {
                        hakuaika.setAlkamisPvm(dateToNextYear(hakuaika.getAlkamisPvm()));
                    }

                    if (haku.getKoulutuksenAlkamisVuosi() != null) {
                        haku.setKoulutuksenAlkamisVuosi(haku.getKoulutuksenAlkamisVuosi() + 1);
                    }

                    if (haku.getHakukausiVuosi() != null) {
                        haku.setHakukausiVuosi(haku.getHakukausiVuosi() + 1);
                    }
                }

                targetHakuoid = haku.getOid();
                getState().getParameters().put(MassCopyProcess.TO_HAKU_OID, targetHakuoid);
                Date d = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                for (TekstiKaannos k : sourceHaku.getNimi().getKaannoksetAsList()) {
                    if(k.getArvo() != null) {
                        haku.getNimi().addTekstiKaannos(k.getKieliKoodi(), k.getArvo().concat(" (Kopioitu ").concat(sdf.format(d)).concat(")"));
                    }
                }
                hakuDAO.insert(haku);
            }
        });
    }

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
    private void removeBatch(final String processId, final String hakuOid) {
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                long rowCount = massakopiointi.rowCount(processId, hakuOid);
                LOG.info("items found {}", rowCount);
                if (rowCount > 0) {
                    LOG.info("change status of all objects to copied. haku oid {}, process id : {}", hakuOid, processId);
                    massakopiointi.updateTila(processId, hakuOid, Massakopiointi.KopioinninTila.COPIED, new Date());
                }
            }
        });
    }

    @Transactional(readOnly = false)
    private void insertHakukohdeBatch(final String processId, final String targetHakuOid, final Set<String> oldOids) {
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
        DateTime dateTime = new DateTime(processing);
        Date indexFutureDate = dateTime.plusHours(5).toDate();
        LOG.info("insert koulutus batch size of : {}/{}", oldOids.size(), countKomoto);

        Set<Long> batchOfIndexIds = Sets.<Long>newHashSet();
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
                komoto.setViimIndeksointiPvm(indexFutureDate);

                for (KoulutusOwner koulutusOwner : komoto.getOwners()) {
                    koulutusOwner.setId(null);
                }

                Set<Date> koulutuksenAlkamisPvms = komoto.getKoulutuksenAlkamisPvms();
                komoto.setAlkamisVuosi(komoto.getAlkamisVuosi() + 1);

                if (koulutuksenAlkamisPvms != null) {
                    Set<Date> plusYears = Sets.<Date>newHashSet();
                    for (Date orgDate : koulutuksenAlkamisPvms) {
                        plusYears.add(dateToNextYear(orgDate));
                    }
                    komoto.setKoulutuksenAlkamisPvms(plusYears);
                }
                KoulutusmoduuliToteutus insert = koulutusmoduuliToteutusDAO.insert(komoto);
                massakopiointi.updateTila(processId, oldKomoOid, Massakopiointi.KopioinninTila.COPIED, processing);
                batchOfIndexIds.add(insert.getId());
            } catch (Exception e) {
                LOG.error("Insert failed, batch rollback, oids : " + oldOids.toArray(), e);
            }
        }
        indexKomotoIds.addAll(batchOfIndexIds);
    }

    private void insertHakukohdes(Set<String> oldOids, String processId, String targetHakuOid) {
        final Date processing = new Date();
        DateTime dateTime = new DateTime(processing);
        Date indexFutureDate = dateTime.plusHours(5).toDate();
        LOG.info("insert hakukohde batch size of : {}/{}", oldOids.size(), countHakukohde);
        Haku targetHaku = hakuDAO.findByOid(targetHakuOid);
        Set<Long> batchOfIndexIds = Sets.<Long>newHashSet();

        for (String oldHakukohdeOid : oldOids) {
            try {
                massakopiointi.updateTila(processId, oldHakukohdeOid, Massakopiointi.KopioinninTila.PROSESSING, processing);
                Pair<Object, MetaObject> pair = massakopiointi.find(processId, oldHakukohdeOid, Hakukohde.class);
                Hakukohde hk = (Hakukohde) pair.getFirst();
                hk.setViimIndeksointiPvm(indexFutureDate);
                hk.setHaku(targetHaku);
                targetHaku.addHakukohde(hk);

                final MetaObject meta = pair.getSecond();

                LOG.debug("convert json to entity by oid : {}, new oid : {}", oldHakukohdeOid, meta.getNewHakukohdeOid());
                hk.setOid(meta.getNewHakukohdeOid());
                hk.setTila(TarjontaTila.KOPIOITU);
                hk.setUlkoinenTunniste(processId);
                /*
                 * HAKUAIKA
                 */
                if (hk.getHakuaika() != null) {
                    final Hakuaika ha = hakuaikas.get(hk.getHakuaika().getId());
                    hk.setHakuaika(ha);
                }

                /*
                 * LIITE
                 */
                List<HakukohdeLiite> liites = hk.getLiites();
                for (HakukohdeLiite l : liites) {
                    l.setHakukohde(hk);

                    if (l.getErapaiva() != null) {
                        l.setErapaiva(dateToNextYear(l.getErapaiva()));
                    }
                }

                /*
                 * VALINTAKOE
                 */
                for (Valintakoe v : hk.getValintakoes()) {
                    v.setHakukohde(hk);

                    if (v.getAjankohtas() != null) {
                        for (ValintakoeAjankohta va : v.getAjankohtas()) {
                            if (va.getAlkamisaika() != null) {
                                va.setAlkamisaika(dateToNextYear(va.getAlkamisaika()));
                            }

                            if (va.getPaattymisaika() != null) {
                                va.setPaattymisaika(dateToNextYear(va.getPaattymisaika()));
                            }
                        }
                    }
                }

                //XXX korjaa tama tekemalla kunnon testidata
                if (meta != null && meta.getKomotoOids() != null && !meta.getKomotoOids().isEmpty()) {
                    List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(Lists.<String>newArrayList(meta.getKomotoOids()));
                    for (KoulutusmoduuliToteutus kt : komotos) {
                        hk.addKoulutusmoduuliToteutus(kt);
                        kt.addHakukohde(hk);
                    }
                }

                if (hk.getHakuaikaAlkuPvm() != null) {
                    hk.setHakuaikaAlkuPvm(dateToNextYear(hk.getHakuaikaAlkuPvm()));
                }

                if (hk.getHakuaikaLoppuPvm() != null) {
                    hk.setHakuaikaLoppuPvm(dateToNextYear(hk.getHakuaikaLoppuPvm()));
                }
                Hakukohde insert = hakukohdeDAO.insert(hk);
                batchOfIndexIds.add(insert.getId());
                massakopiointi.updateTila(processId, oldHakukohdeOid, Massakopiointi.KopioinninTila.COPIED, processing);
            } catch (Exception e) {
                LOG.error("Insert failed, batch rollback, oids : " + oldOids.toArray(), e);
            }

        }

        indexHakukohdeIds.addAll(batchOfIndexIds);
    }

    private static Date dateToNextYear(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(1).toDate();
    }

    public boolean canStop() {
        return true;
    }

    public boolean isCompleted() {
        return completed;
    }

    private double calcPercentage() {
        if (countTotalHakukohde + countTotalKomoto > 0) {
            return ((countHakukohde + countKomoto) * 100 / (countTotalHakukohde + countTotalKomoto));
        }
        return 0;
    }
}
