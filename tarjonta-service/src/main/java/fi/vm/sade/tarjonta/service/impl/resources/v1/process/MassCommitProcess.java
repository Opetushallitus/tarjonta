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
import fi.vm.sade.tarjonta.dao.*;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auditlog.AuditLog;
import fi.vm.sade.tarjonta.service.copy.EntityToJsonHelper;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import fi.vm.sade.tarjonta.service.impl.resources.v1.ConverterV1;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusUtilService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private ConverterV1 converterV1;


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
        final InetAddress ip = getState().getIp();
        final String session = getState().getSession();
        final String userAgent = getState().getUserAgent();
        final String processId = getState().getId();

        try {
            List<String> oldKomotoOids = getOldKomotoOids(processId);
            List<String> oldKomoOids = getOldKomoOids(processId);
            List<String> oldHakukohdeOids = getOldHakukohdeOids(processId);

            countTotalKomoto = oldKomotoOids.size();
            countTotalHakukohde = oldHakukohdeOids.size();

            LOG.info("countTotalKomoto: {}", countTotalKomoto);
            LOG.info("countTotalHakukohde: {}", countTotalHakukohde);

            insertHaku(fromHakuOid, ip, session, userAgent);
            handleKomotos(processId, oldKomotoOids);
            handleSisaltyvyydet(processId, oldKomoOids);
            handleHakukohdes(processId, oldHakukohdeOids);

            removeBatch(processId, fromHakuOid);
            setIndexedDatesToNull();

            getState().getParameters().put("result", "success");
            LOG.info("Commit succeeded");
        } catch (Throwable ex) {
            LOG.error("Copy failed", ex);

            getState().setMessageKey("my.test.process.error");
            getState().getParameters().put("result", ex.getMessage());

            throw new RuntimeException(ex);
        } finally {
            completed = true;
        }
    }

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
                executeInTransaction(() -> koulutusSisaltyvyysDAO.insert(copy));
            }
        }
    }

    private void setIndexedDatesToNull() {
        LOG.info("setIndexedDatesToNull()");
        setKomotoIndexedDateToNull(indexKomotoIds);
        setHakukohdeIndexedDateToNull(indexHakukohdeIds);
    }

    private void setHakukohdeIndexedDateToNull(Set<Long> hakukohdeIds) {
        for (final Long hakukohdeId : hakukohdeIds) {
            executeInTransaction(() -> hakukohdeDAO.setViimIndeksointiPvmToNull(hakukohdeId));
        }
    }

    private void setKomotoIndexedDateToNull(Set<Long> komotoIds) {
        for (final Long komotoId : komotoIds) {
            executeInTransaction(() -> koulutusmoduuliToteutusDAO.setViimIndeksointiPvmToNull(komotoId));
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

    private void insertHaku(final String oldHakuOid, InetAddress ip, String session, String userAgent) {
        executeInTransaction(() -> {
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
            LOG.info("new haku oid: {}", targetHakuoid);
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
            HakuV1RDTO hakuV1RDTO = converterV1.fromHakuToHakuRDTO(haku, true);
            AuditLog.massCopy(hakuV1RDTO, oldHakuOid, userOid, ip, session, userAgent);
        });
    }

    private void insertKomotoBatch(final String processId, final Set<String> oldOids) {
        executeInTransaction(() -> insertKomotos(oldOids, processId));
    }

    private void removeBatch(final String processId, final String hakuOid) {
        executeInTransaction(() -> {
            long rowCount = massakopiointi.rowCount(processId, hakuOid);
            LOG.info("items found {}", rowCount);
            if (rowCount > 0) {
                LOG.info("change status of all objects to copied. haku oid {}, process id : {}", hakuOid, processId);
                massakopiointi.updateTila(processId, hakuOid, Massakopiointi.KopioinninTila.COPIED, new Date());
            }
        });
    }

    private void insertHakukohdeBatch(final String processId, final String targetHakuOid, final Set<String> oldOids) {
        executeInTransaction(() -> insertHakukohdes(oldOids, processId, targetHakuOid));
    }

    private void executeInTransaction(final Runnable runnable) {
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(status -> {
            runnable.run();
            return null;
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

                cleanKomotoForReformi2018(komoto, meta);

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

    // 2018 reformia edeltävien koulutusten kopiointi: poistetaan aikuiskoulutus, näyttötutkinnon järjestäjä, ja valmistava koulutus
    private void cleanKomotoForReformi2018(KoulutusmoduuliToteutus komoto, MetaObject meta) {
        List<ToteutustyyppiEnum> toteutustyypitJoiltaPoistettavaAikuiskoulutusLaji = Arrays.asList(
                ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018,
                ToteutustyyppiEnum.AMMATTITUTKINTO,
                ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO);
        boolean poistetaanAikuiskoulutusLaji = toteutustyypitJoiltaPoistettavaAikuiskoulutusLaji.contains(komoto.getToteutustyyppi());
        HashSet<KoodistoUri> newKoulutuslajis = new HashSet<>();
        for (KoodistoUri koulutuslaji : komoto.getKoulutuslajis()) {
            String koodiUri = koulutuslaji.getKoodiUri();
            if (poistetaanAikuiskoulutusLaji && koodiUri.startsWith("koulutuslaji_a#")) {
                LOG.info("Removing koulutuslaji {} from copied komoto {} (original oid {}) with toteutustyyppi {}", koodiUri, meta.getNewKomotoOid(), meta.getOriginalKomotoOid(), komoto.getToteutustyyppi());
            } else {
                newKoulutuslajis.add(koulutuslaji);
            }
        }
        komoto.setKoulutuslajis(newKoulutuslajis);

        List<ToteutustyyppiEnum> nayttotutkintoToteutustyypit = Arrays.asList(
                ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018,
                ToteutustyyppiEnum.AMMATTITUTKINTO,
                ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO);
        boolean onNayttotutkinto = nayttotutkintoToteutustyypit.contains(komoto.getToteutustyyppi());
        if (onNayttotutkinto && komoto.getJarjesteja() != null) {
            LOG.info("Setting jarjesteja to null in copied komoto {} (original oid {}) with toteutustyyppi {}, jarjesteja was {}", meta.getNewKomotoOid(), meta.getOriginalKomotoOid(), komoto.getToteutustyyppi(), komoto.getJarjesteja());
            komoto.setJarjesteja(null);
        }

        List<ToteutustyyppiEnum> toteutustyypitJoiltaPoistettavaValmistavaKoultuus = Arrays.asList(
                ToteutustyyppiEnum.AMMATTITUTKINTO,
                ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO);
        boolean onPoistettavaValmistavaKoulutus = toteutustyypitJoiltaPoistettavaValmistavaKoultuus.contains(komoto.getToteutustyyppi());
        if (komoto.getValmistavaKoulutus() != null) {
            LOG.info("Removing valmistavaKoulutus {} from copied komoto {} (original oid {}) with toteutustyyppi {}", komoto.getValmistavaKoulutus().getOid(), meta.getNewKomotoOid(), meta.getOriginalKomotoOid(), komoto.getToteutustyyppi());
            komoto.setValmistavaKoulutus(null);
        }
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
