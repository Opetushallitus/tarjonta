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
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.MassakopiointiDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class MassPepareProcess implements ProcessDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(MassPepareProcess.class);
    private static final int FLUSH_SIZE = 100;
    private static final TarjontaTila[] COPY_TILAS = {TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU, TarjontaTila.VALMIS};

    private ProcessV1RDTO state;
    private long startTs = 0L;

    @Autowired(required = true)
    private HakukohdeDAO hakukohdeDAO;

    @Autowired(required = true)
    private OidService oidService;

    @Autowired(required = true)
    private MassakopiointiDAO massakopiointiDAO;

    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    private int countHakukohde = 0;
    private int countKomoto = 0;
    private int countTotalHakukohde = 0;
    private int countTotalKomoto = 0;

    public MassPepareProcess() {
        super();
    }

    @Override
    public ProcessV1RDTO getState() {
        state.getParameters().put(MassCopyProcess.COUNT_HAKUKOHDE, countHakukohde + "");
        state.getParameters().put(MassCopyProcess.COUNT_KOMOTO, countKomoto + "");
        state.getParameters().put(MassCopyProcess.TOTAL_HAKUKOHDE, countTotalHakukohde + "");
        state.getParameters().put(MassCopyProcess.TOTAL_KOMOTO, countTotalKomoto + "");
        state.setState(calcPercentage());
        return state;
    }

    @Override
    public void setState(ProcessV1RDTO state) {
        this.state = state;
    }

    @Override
    public void run() {
        final String fromOid = getState().getParameters().get(MassCopyProcess.SELECTED_HAKU_OID);
        LOG.info("MassPrepareProcess.run(), params haku oid : {}", fromOid);
        long rowCount = massakopiointiDAO.rowCount(fromOid);
        LOG.info("items found {}", rowCount);
        if (rowCount > 0) {
            LOG.info("delete all object by haku oid {}", fromOid);
            deleteBatch(fromOid);
        }

        try {
            startTs = System.currentTimeMillis();
            final List<Long> hakukohdeIds = hakukohdeDAO.searchHakukohteetByHakuOid(Lists.<String>newArrayList(fromOid), COPY_TILAS);
            final Set<Long> komotoIds = Sets.newHashSet(koulutusmoduuliToteutusDAO.searchKomotoIdsByHakukohdesId(hakukohdeIds, COPY_TILAS));

            countTotalHakukohde = hakukohdeIds.size();
            countTotalKomoto = komotoIds.size();
            LOG.info("komoto rows total : {}", countTotalKomoto);

            Set<Long> batch = Sets.<Long>newHashSet();
            for (Long komotoId : komotoIds) {
                if (countKomoto % FLUSH_SIZE == 0 || komotoIds.size() - 1 == countKomoto) {
                    flushKoulutusBatch(fromOid, batch);
                    batch = Sets.<Long>newHashSet();
                }

                batch.add(komotoId);
                countKomoto++;
            }

            batch = Sets.<Long>newHashSet();
            LOG.info("hakukohde rows total : {}", countTotalHakukohde);

            for (Long komotoId : hakukohdeIds) {
                if (countHakukohde % FLUSH_SIZE == 0 || komotoIds.size() - 1 == countHakukohde) {
                    flushHakukohdeBatch(fromOid, batch);
                    batch = Sets.<Long>newHashSet();
                }

                batch.add(komotoId);
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
                LOG.info("insert koulutus batch size of : {}/{}", komotoIds.size(), countKomoto);
                for (Long komotoId : komotoIds) {
                    LOG.debug("convert {} komoto by id : {}", countKomoto, komotoId);

                    KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.read(komotoId);
                    Preconditions.checkNotNull(komoto, "Komoto entity cannot be null!");

                    MetaObject metaObject = new MetaObject();
                    for (Hakukohde hk : komoto.getHakukohdes()) {
                        metaObject.addHakukohdeOid(hk.getOid());
                    }

                    metaObject.setOriginalHakuOid(fromOid);
                    metaObject.setOriginalKomoOid(komoto.getKoulutusmoduuli().getOid());
                    metaObject.setOriginalKomotoOid(komoto.getOid());
                    try {
                        metaObject.setNewKomotoOid(oidService.get(TarjontaOidType.KOMOTO));
                    } catch (OIDCreationException ex) {
                        LOG.error("OID Service failed", fromOid);
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
    private void flushHakukohdeBatch(final String fromOid, final Set<Long> hakukohdeIds) throws OIDCreationException {
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                LOG.info("insert hakukohde batch size of : {}/{}", hakukohdeIds.size(), countHakukohde);
                for (Long hakukohdeId : hakukohdeIds) {
                    LOG.debug("convert {} hakukohde by id : {}", countHakukohde, hakukohdeId);
                    Hakukohde hakukohde = hakukohdeDAO.read(hakukohdeId);
                    Preconditions.checkNotNull(hakukohde, "Hakukohde entity cannot be null!");

                    MetaObject metaObject = new MetaObject();
                    for (KoulutusmoduuliToteutus hk : hakukohde.getKoulutusmoduuliToteutuses()) {
                        metaObject.addKomotoOid(hk.getOid());
                    }

                    try {
                        metaObject.setNewHakukohdeOid(oidService.get(TarjontaOidType.KOMOTO));
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
