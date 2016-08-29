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
import fi.vm.sade.auditlog.tarjonta.TarjontaOperation;
import fi.vm.sade.auditlog.tarjonta.TarjontaResource;
import fi.vm.sade.tarjonta.dao.*;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.copy.EntityToJsonHelper;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusUtilService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KorkeakouluOpintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

import static fi.vm.sade.tarjonta.service.AuditHelper.AUDIT;
import static fi.vm.sade.tarjonta.service.AuditHelper.builder;

@Component
@Scope("prototype")
public class MassCommitProcess {

    private static final Logger LOG = LoggerFactory.getLogger(MassCommitProcess.class);

    private ProcessV1RDTO state;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private MassakopiointiDAO massakopiointi;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private IndexerResource indexerResource;

    @Autowired
    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;

    @Autowired
    private KoulutusUtilService koulutusUtilService;

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private OidService oidService;

    private int countHakukohde = 0;
    private int countKomoto = 0;
    private int countTotalHakukohde = 0;
    private int countTotalKomoto = 0;
    private String targetHakuoid = null;

    private Set<Long> indexHakukohdeIds = Sets.newHashSet();
    private Set<Long> indexKomotoIds = Sets.newHashSet();
    private boolean completed = false;

    private Map<Long, Hakuaika> hakuaikas = Maps.newHashMap();

    public ProcessV1RDTO getState() {
        state.getParameters().put(MassCopyProcess.COMMIT_COUNT_HAKUKOHDE, countHakukohde + "");
        state.getParameters().put(MassCopyProcess.COMMIT_COUNT_KOMOTO, countKomoto + "");
        state.getParameters().put(MassCopyProcess.COMMIT_TOTAL_HAKUKOHDE, countTotalHakukohde + "");
        state.getParameters().put(MassCopyProcess.COMMIT_TOTAL_KOMOTO, countTotalKomoto + "");
        state.setState(MassCopyBatchSizeCalculator.calcPercentage(countKomoto, countTotalKomoto, countHakukohde, countTotalHakukohde));
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

        try {
            List<String> oldKomotoOids = getOldKomotoOids(processId);
            List<String> oldKomoOids = getOldKomoOids(processId);
            List<String> oldHakukohdeOids = getOldHakukohdeOids(processId);

            countTotalKomoto = oldKomotoOids.size();
            countTotalHakukohde = oldHakukohdeOids.size();

            insertHaku(fromHakuOid);
            handleKomotos(processId, oldKomotoOids);
            handleSisaltyvyydet(processId, oldKomoOids);
            handleHakukohdes(processId, oldHakukohdeOids);

            removeBatch(processId, fromHakuOid);
            setIndexedDatesToNull();

            getState().getParameters().put("result", "success");
        } catch (Exception ex) {
            LOG.error("Copy failed", ex);

            getState().setMessageKey("my.test.process.error");
            getState().getParameters().put("result", ex.getMessage());
        } finally {
            completed = true;
        }
    }

    @Transactional(readOnly = false)
    private void handleSisaltyvyydet(String processId, List<String> oldKomoOids) {
        for (String komoParts : oldKomoOids) {
            String[] oids = komoParts.split("_");
            String oldKomoOid = oids[0];
            Massakopiointi row = massakopiointi.find(processId, komoParts);
            Koulutusmoduuli newKomo = koulutusmoduuliDAO.findByOid(row.getNewOid());

            final KoulutusSisaltyvyys copy = new KoulutusSisaltyvyys();
            copy.setYlamoduuli(newKomo);
            copy.setValintaTyyppi(KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF);

            for (String childKomoOid : koulutusSisaltyvyysDAO.getChildren(oldKomoOid)) {
                Massakopiointi sisaltyvyysRow = massakopiointi.findFirstKomo(processId, childKomoOid);
                if (sisaltyvyysRow != null) {
                    copy.addAlamoduuli(koulutusmoduuliDAO.findByOid(sisaltyvyysRow.getNewOid()));
                }
            }

            if (!copy.getAlamoduuliList().isEmpty()) {
                executeInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        koulutusSisaltyvyysDAO.insert(copy);
                    }
                });
            }
        }
    }

    private void setIndexedDatesToNull() {
        setKomotoIndexedDateToNull(indexKomotoIds);
        setHakukohdeIndexedDateToNull(indexHakukohdeIds);
    }

    private void setHakukohdeIndexedDateToNull(Set<Long> hakukohdeIds) {
        for (final Long hakukohdeId : hakukohdeIds) {
            executeInTransaction(new Runnable() {
                @Override
                public void run() {
                    hakukohdeDAO.setViimIndeksointiPvmToNull(hakukohdeId);
                }
            });
        }
    }

    private void setKomotoIndexedDateToNull(Set<Long> komotoIds) {
        for (final Long komotoId : komotoIds) {
            executeInTransaction(new Runnable() {
                @Override
                public void run() {
                    koulutusmoduuliToteutusDAO.setViimIndeksointiPvmToNull(komotoId);
                }
            });
        }
    }

    private void handleKomotos(String processId, List<String> oldKomotoOids) {
        Set<String> oidBatch = Sets.newHashSet();
        for (String oldKomotoOid : oldKomotoOids) {
            if (MassCopyBatchSizeCalculator.shouldStartNewBatch(countKomoto)) {
                insertKomotoBatch(processId, oidBatch);
                oidBatch = Sets.newHashSet();
            }
            oidBatch.add(oldKomotoOid);
            countKomoto++;
        }
        insertKomotoBatch(processId, oidBatch);
    }

    private void handleHakukohdes(String processId, List<String> oldHakukohdeOids) {
        Set<String> oidBatch = Sets.newHashSet();
        for (String oldHakukohdeOid : oldHakukohdeOids) {
            if (MassCopyBatchSizeCalculator.shouldStartNewBatch(countHakukohde)) {
                insertHakukohdeBatch(processId, targetHakuoid, oidBatch);
                oidBatch = Sets.newHashSet();
            }
            oidBatch.add(oldHakukohdeOid);
            countHakukohde++;
        }
        insertHakukohdeBatch(processId, targetHakuoid, oidBatch);
    }

    private List<String> getOldHakukohdeOids(String processId) {
        return massakopiointi.searchOids(
                new MassakopiointiDAO.SearchCriteria(null, null, null,
                        Massakopiointi.Tyyppi.HAKUKOHDE_ENTITY,
                        processId,
                        Massakopiointi.KopioinninTila.READY_FOR_COPY));
    }

    private List<String> getOldKomotoOids(String processId) {
        return massakopiointi.searchOids(
                new MassakopiointiDAO.SearchCriteria(null, null, null,
                        Massakopiointi.Tyyppi.KOMOTO_ENTITY,
                        processId,
                        Massakopiointi.KopioinninTila.READY_FOR_COPY));
    }

    private List<String> getOldKomoOids(String processId) {
        return massakopiointi.searchOids(
                new MassakopiointiDAO.SearchCriteria(null, null, null,
                        Massakopiointi.Tyyppi.KOMO_ENTITY,
                        processId,
                        Massakopiointi.KopioinninTila.READY_FOR_COPY));
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
                }

                if (haku.getKoulutuksenAlkamisVuosi() != null) {
                    haku.setKoulutuksenAlkamisVuosi(haku.getKoulutuksenAlkamisVuosi() + 1);
                }

                if (haku.getHakukausiVuosi() != null) {
                    haku.setHakukausiVuosi(haku.getHakukausiVuosi() + 1);
                }

                targetHakuoid = haku.getOid();
                getState().getParameters().put(MassCopyProcess.TO_HAKU_OID, targetHakuoid);
                Date d = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                for (TekstiKaannos k : sourceHaku.getNimi().getKaannoksetAsList()) {
                    if (k.getArvo() != null) {
                        haku.getNimi().addTekstiKaannos(k.getKieliKoodi(), k.getArvo().concat(" (Kopioitu ").concat(sdf.format(d)).concat(")"));
                    }
                }
                hakuDAO.insert(haku);

                String userOid = state.getParameters().get(MassCopyProcess.USER_OID);
                AUDIT.log(builder(userOid)
                            .setOperation(TarjontaOperation.COPY)
                            .setResource(TarjontaResource.HAKU)
                            .setResourceOid(haku.getOid())
                            .add("sourceHaku", oldHakuOid).build());
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
        LOG.info("insert koulutus batch size of {}: {}/{}", oldOids.size(), countKomoto, countTotalKomoto);

        Set<Long> batchOfIndexIds = Sets.<Long>newHashSet();
        for (final String oldKomotoOid : oldOids) {
            try {
                massakopiointi.updateTila(processId, oldKomotoOid, Massakopiointi.KopioinninTila.PROSESSING, processing);
                Pair<Object, MetaObject> find = massakopiointi.find(processId, oldKomotoOid, KoulutusmoduuliToteutus.class);
                KoulutusmoduuliToteutus komoto = (KoulutusmoduuliToteutus) find.getFirst();
                final MetaObject meta = find.getSecond();
                komoto.setKoulutusmoduuli(koulutusmoduuliDAO.findByOid(meta.getOriginalKomoOid()));

                LOG.debug("convert json to entity by oid : {}, new oid : {}", oldKomotoOid, meta.getNewKomotoOid());

                if (komoto.getValmistavaKoulutus() != null) {
                    KoulutusmoduuliToteutus valmistava = komoto.getValmistavaKoulutus();
                    valmistava.setKoulutusmoduuli(komoto.getKoulutusmoduuli());
                    valmistava.setTila(TarjontaTila.KOPIOITU);
                    valmistava.setId(null);
                    valmistava.setHaunKopioinninTunniste(processId);
                    try {
                        valmistava.setOid(oidService.get(TarjontaOidType.KOMOTO));
                    }
                    catch (OIDCreationException e) {
                        LOG.error("OID Service failed for valmistava koulutus", meta.getOriginalKomotoOid());
                    }
                }

                if (ToteutustyyppiEnum.KORKEAKOULUTUS.equals(komoto.getToteutustyyppi())) {
                    komoto = koulutusUtilService.copyKomotoAndKomo(
                            komoto, komoto.getTarjoaja(), meta.getNewKomotoOid(), meta.getNewKomoOid(), false, KoulutusKorkeakouluV1RDTO.class
                    );
                }
                else if (ToteutustyyppiEnum.KORKEAKOULUOPINTO.equals(komoto.getToteutustyyppi())) {
                    komoto = koulutusUtilService.copyKomotoAndKomo(
                            komoto, komoto.getTarjoaja(), meta.getNewKomotoOid(), meta.getNewKomoOid(), false, KorkeakouluOpintoV1RDTO.class
                    );
                }
                else {
                    komoto.setOid(meta.getNewKomotoOid());
                    komoto = koulutusmoduuliToteutusDAO.insert(komoto);
                }

                komoto.setTila(TarjontaTila.KOPIOITU);
                komoto.setHaunKopioinninTunniste(processId);
                komoto.setLastUpdatedByOid("NA");
                komoto.setViimIndeksointiPvm(indexFutureDate);

                Set<Date> koulutuksenAlkamisPvms = komoto.getKoulutuksenAlkamisPvms();
                komoto.setAlkamisVuosi(komoto.getAlkamisVuosi() + 1);

                if (koulutuksenAlkamisPvms != null) {
                    Set<Date> plusYears = Sets.<Date>newHashSet();
                    for (Date orgDate : koulutuksenAlkamisPvms) {
                        plusYears.add(dateToNextYear(orgDate));
                    }
                    komoto.setKoulutuksenAlkamisPvms(plusYears);
                }

                massakopiointi.updateTila(processId, oldKomotoOid, Massakopiointi.KopioinninTila.COPIED, processing);
                batchOfIndexIds.add(komoto.getId());
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
        LOG.info("insert hakukohde batch size of {}: {}/{}", oldOids.size(), countHakukohde, countTotalHakukohde);
        Haku targetHaku = hakuDAO.findByOid(targetHakuOid);
        Set<Long> batchOfIndexIds = Sets.<Long>newHashSet();

        for (String oldHakukohdeOid : oldOids) {
            try {
                Hakukohde originalHakukohde = hakukohdeDAO.findHakukohdeByOid(oldHakukohdeOid);
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
                hk.setHaunKopioinninTunniste(processId);

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
                Set<HakukohdeLiite> liites = hk.getLiites();
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

                    Map<String, KoulutusmoduuliToteutusTarjoajatiedot>
                            tarjoajatiedotMap = new HashMap<String, KoulutusmoduuliToteutusTarjoajatiedot>();

                    for (KoulutusmoduuliToteutus kt : komotos) {
                        hk.addKoulutusmoduuliToteutus(kt);

                        // Need to manually update all hakukohdes for komoto, otherwise
                        // Hibernate only storede the last added hakukohde (why?)
                        Set<Hakukohde> allHakukohdes = Sets.newHashSet(kt.getHakukohdes());
                        allHakukohdes.add(hk);
                        for (Hakukohde hakukohde : allHakukohdes) {
                            kt.addHakukohde(hakukohde);
                        }

                        KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
                        tarjoajatiedot.setTarjoajaOids(kt.getTarjoajaOids());
                        tarjoajatiedotMap.put(kt.getOid(), tarjoajatiedot);
                    }

                    hk.setKoulutusmoduuliToteutusTarjoajatiedot(tarjoajatiedotMap);
                }

                if (hk.getHakuaikaAlkuPvm() != null) {
                    hk.setHakuaikaAlkuPvm(dateToNextYear(hk.getHakuaikaAlkuPvm()));
                }

                if (hk.getHakuaikaLoppuPvm() != null) {
                    hk.setHakuaikaLoppuPvm(dateToNextYear(hk.getHakuaikaLoppuPvm()));
                }

                for (Ryhmaliitos liitos : originalHakukohde.getRyhmaliitokset()) {
                    Ryhmaliitos liitosCopy = new Ryhmaliitos();
                    liitosCopy.setHakukohde(hk);
                    liitosCopy.setPrioriteetti(liitos.getPrioriteetti());
                    liitosCopy.setRyhmaOid(liitos.getRyhmaOid());
                    hk.addRyhmaliitos(liitosCopy);
                }

                hk.setLastUpdatedByOid("NA");

                // Katso KJOH-1013
                if (hk.getAloituspaikatKuvaus() != null) {
                    String tekstiAloituspaikka = hk.getAloituspaikatKuvaus().getFirstNonEmptyKaannos();

                    try {
                        hk.setAloituspaikatLkm(Integer.parseInt(tekstiAloituspaikka));
                    }
                    catch (NumberFormatException e) {
                        hk.setAloituspaikatLkm(0);
                    }

                    hk.setAloituspaikatKuvaus(null);
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
}
