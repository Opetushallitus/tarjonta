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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.MassakopiointiDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.List;
import java.util.Set;

import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
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
public class MassPepareProcess {

    private static final Logger LOG = LoggerFactory.getLogger(MassPepareProcess.class);
    private static final TarjontaTila[] COPY_TILAS = {TarjontaTila.JULKAISTU};
    private static final List<TarjontaTila> COPY_TILAS_AS_LIST = Lists.newArrayList(COPY_TILAS);

    private ProcessV1RDTO state;
    private long startTs = 0L;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    private MassakopiointiDAO massakopiointiDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    private int countHakukohde = 0;
    private int countKomoto = 0;
    private int countTotalHakukohde = 0;
    private int countTotalKomoto = 0;
    private boolean completed = false;

    public ProcessV1RDTO getState() {
        state.getParameters().put(MassCopyProcess.PREPARE_COUNT_HAKUKOHDE, countHakukohde + "");
        state.getParameters().put(MassCopyProcess.PREPARE_COUNT_KOMOTO, countKomoto + "");
        state.getParameters().put(MassCopyProcess.PREPARE_TOTAL_HAKUKOHDE, countTotalHakukohde + "");
        state.getParameters().put(MassCopyProcess.PREPARE_TOTAL_KOMOTO, countTotalKomoto + "");
        state.setState(calcPercentage());
        return state;
    }

    public void setState(ProcessV1RDTO state) {
        this.state = state;
    }

    public void run() {
        final String fromOid = getState().getParameters().get(MassCopyProcess.SELECTED_HAKU_OID);
        final String processId = getState().getId();
        countHakukohde = 0;
        countKomoto = 0;
        countTotalHakukohde = 0;
        countTotalKomoto = 0;

        //tyhjää vanha data
        deleteBatch(fromOid);

        try {
            startTs = System.currentTimeMillis();
            final List<Long> hakukohdeIds = hakukohdeDAO.searchHakukohteetByHakuOid(Lists.<String>newArrayList(fromOid), COPY_TILAS);
            final Set<Long> komotoIds = Sets.newHashSet(koulutusmoduuliToteutusDAO.searchKomotoIdsByHakukohdesId(hakukohdeIds, COPY_TILAS));

            countTotalHakukohde = hakukohdeIds.size();
            countTotalKomoto = komotoIds.size();
            LOG.info("komoto rows total : {}", countTotalKomoto);

            Set<Long> batch = Sets.<Long>newHashSet();
            for (Long komotoId : komotoIds) {
                if (countKomoto % MassCopyProcess.BATCH_KOMOTO_SIZE == 0 || komotoIds.size() - 1 == countKomoto) {
                    flushKoulutusBatch(fromOid, batch);
                    batch = Sets.<Long>newHashSet();
                }

                batch.add(komotoId);
                countKomoto++;
            }
            flushKoulutusBatch(fromOid, batch);

            batch = Sets.<Long>newHashSet();
            LOG.info("hakukohde rows total : {}", countTotalHakukohde);

            for (Long hakukohdeId : hakukohdeIds) {
                if (countHakukohde % MassCopyProcess.BATCH_HAKUKOHDE_SIZE == 0 || hakukohdeIds.size() - 1 == countHakukohde) {
                    flushHakukohdeBatch(processId, fromOid, batch);
                    batch = Sets.<Long>newHashSet();
                }

                batch.add(hakukohdeId);
                countHakukohde++;
            }
            flushHakukohdeBatch(processId, fromOid, batch);

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

    @Autowired
    private PlatformTransactionManager tm;

    @Transactional(readOnly = false)
    private void deleteBatch(final String fromOid) {
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                massakopiointiDAO.deleteAllByHakuOid(fromOid);
            }
        });
    }

    @Transactional(readOnly = false)
    private void flushKoulutusBatch(final String fromOid, final Set<Long> komotoIds) throws OIDCreationException {
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                LOG.info("prepare koulutus batch size of : {}/{}", komotoIds.size(), countKomoto);
                for (Long komotoId : komotoIds) {
                    LOG.debug("convert {} komoto by id : {}", countKomoto, komotoId);

                    KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.read(komotoId);
                    Preconditions.checkNotNull(komoto, "Komoto entity cannot be null!");

                    // Tarkista, onko koulutus jo aiemmin massakopioitu
                    // jonkun toisen haun kopioinnin yhteydessä
                    Pair<Object, MetaObject> prevCopyMeta = massakopiointiDAO.find(null, komoto.getOid(), KoulutusmoduuliToteutus.class);
                    if (prevCopyMeta != null) {
                        continue;
                    }

                    MetaObject metaObject = new MetaObject();
                    for (Hakukohde hk : komoto.getHakukohdes()) {
                        if (COPY_TILAS_AS_LIST.contains(hk.getTila())) {
                            metaObject.addHakukohdeOid(hk.getOid());
                        }
                    }

                    metaObject.setOriginalHakuOid(fromOid);
                    metaObject.setOriginalKomoOid(komoto.getKoulutusmoduuli().getOid());
                    metaObject.setOriginalKomotoOid(komoto.getOid());

                    String newKomoOid;
                    try {
                        metaObject.setNewKomotoOid(oidService.get(TarjontaOidType.KOMOTO));
                        newKomoOid = oidService.get(TarjontaOidType.KOMO);
                        metaObject.setNewKomoOid(newKomoOid);
                    } catch (OIDCreationException ex) {
                        LOG.error("OID Service failed", fromOid);
                        continue;
                    }

                    // KOMON tiedot
                    if (ToteutustyyppiEnum.KORKEAKOULUTUS.equals(komoto.getToteutustyyppi())) {
                        massakopiointiDAO.saveEntityAsJson(
                                fromOid,
                                komoto.getKoulutusmoduuli().getOid() + "_" + komoto.getOid(),
                                newKomoOid,
                                state.getId(),
                                Massakopiointi.Tyyppi.KOMO_ENTITY,
                                Koulutusmoduuli.class,
                                null,
                                null
                        );
                    }

                    massakopiointiDAO.saveEntityAsJson(
                            fromOid,
                            komoto.getOid(),
                            metaObject.getNewKomotoOid(),
                            state.getId(),
                            Massakopiointi.Tyyppi.KOMOTO_ENTITY,
                            KoulutusmoduuliToteutus.class,
                            komoto,
                            metaObject);
                }
            }
        });
    }

    @Transactional(readOnly = false)
    private void flushHakukohdeBatch(final String processId, final String fromOid, final Set<Long> hakukohdeIds) throws OIDCreationException {
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                LOG.info("prepare hakukohde batch size of : {}/{}", hakukohdeIds.size(), countHakukohde);
                for (Long hakukohdeId : hakukohdeIds) {
                    LOG.info("convert {} hakukohde by id : {}", countHakukohde, hakukohdeId);
                    Hakukohde hakukohde = hakukohdeDAO.findHakukohdeById(hakukohdeId);
                    Preconditions.checkNotNull(hakukohde, "Hakukohde entity cannot be null!");

                    MetaObject metaObject = new MetaObject();
                    for (KoulutusmoduuliToteutus kt : hakukohde.getKoulutusmoduuliToteutuses()) {
                        //add only the new oid to set of komotos
                        if (COPY_TILAS_AS_LIST.contains(kt.getTila())) {
                            metaObject.addKomotoOid(massakopiointiDAO.findNewOid(processId, kt.getOid()));
                        }
                    }

                    // Skip hakukohde if it has no koulutus
                    if (metaObject.hasNoKomotos()) {
                        continue;
                    }

                    try {
                        metaObject.setNewHakukohdeOid(oidService.get(TarjontaOidType.HAKUKOHDE));
                    } catch (OIDCreationException ex) {
                        LOG.error("OID Service failed", fromOid);
                    }

                    metaObject.setOriginalHakuOid(fromOid);
                    massakopiointiDAO.saveEntityAsJson(
                            fromOid,
                            hakukohde.getOid(), //from hakukohde oid
                            metaObject.getNewHakukohdeOid(), //to hakukohde oid
                            state.getId(),
                            Massakopiointi.Tyyppi.HAKUKOHDE_ENTITY,
                            Hakukohde.class,
                            hakukohde,
                            metaObject);
                }
            }
        });
    }

    public boolean canStop() {
        return true;
    }

    public boolean isCompleted() {
        return completed;
    }

    public double calcPercentage() {
        if (countTotalHakukohde + countTotalKomoto > 0) {
            return ((countHakukohde + countKomoto) * 100 / (countTotalHakukohde + countTotalKomoto));
        }
        return 0;
    }
}
