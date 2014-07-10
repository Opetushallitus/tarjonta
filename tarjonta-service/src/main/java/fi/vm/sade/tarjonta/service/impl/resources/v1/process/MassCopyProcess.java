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
import org.springframework.transaction.annotation.Transactional;

public class MassCopyProcess implements ProcessDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(MassCopyProcess.class);
    public static final String SELECTED_HAKU_OID = "haku.oid.from";
    public static final String SELECTED_PROCESS_COPY_ID = "process.copy.id";
    private static final TarjontaTila[] COPY_TILAS = {TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU, TarjontaTila.VALMIS};

    private ProcessV1RDTO state;
    private long startTs = 0L;
    private int howManySecondsToRun = 0;

    @Autowired(required = true)
    private HakukohdeDAO hakukohdeDAO;

    @Autowired(required = true)
    private OidService oidService;

    @Autowired(required = true)
    private MassakopiointiDAO massakopiointiDAO;

    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    public MassCopyProcess() {
        super();

    }

    @Override
    public ProcessV1RDTO getState() {
        return state;
    }

    @Override
    public void setState(ProcessV1RDTO state) {
        this.state = state;
    }

    @Transactional(readOnly = false)
    @Override
    public void run() {
        final String fromOid = getState().getParameters().get(SELECTED_HAKU_OID);
        LOG.info("MassCopyProcess.run(), params haku oid : {}", fromOid);
        long rowCount = massakopiointiDAO.rowCount(fromOid);
        LOG.info("items found {}", rowCount);
        if (rowCount > 0) {
            LOG.info("delete all object by haku oid {}", fromOid);
            massakopiointiDAO.deleteAllByHakuOid(fromOid);
        }

        try {
            startTs = System.currentTimeMillis();
            LOG.info("start()... {}", startTs);

            final List<Long> hakukohdeIds = hakukohdeDAO.searchHakukohteetByHakuOid(Lists.<String>newArrayList(fromOid), COPY_TILAS);
            final Set<Long> komotoIds = Sets.newHashSet(koulutusmoduuliToteutusDAO.searchKomotoIdsByHakukohdesId(hakukohdeIds, COPY_TILAS));

            for (Long komotoId : komotoIds) {
                LOG.info("convert komoto by id : {}", komotoId);

                KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.read(komotoId);
                Preconditions.checkNotNull(komoto, "Komoto entity cannot be null!");

                MetaObject metaObject = new MetaObject();
                for (Hakukohde hk : komoto.getHakukohdes()) {
                    metaObject.addHakukohdeOid(hk.getOid());
                }

                metaObject.setOriginalHakuOid(fromOid);
                metaObject.setOriginalKomoOid(komoto.getKoulutusmoduuli().getOid());
                metaObject.setOriginalKomotoOid(komoto.getOid());
                metaObject.setNewKomotoOid(oidService.get(TarjontaOidType.KOMOTO));

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

            for (Long hakukohdeId : hakukohdeIds) {
                LOG.info("convert hakukohde by hakukohdeId : {}", hakukohdeId);
                Hakukohde hakukohde = hakukohdeDAO.read(hakukohdeId);
                Preconditions.checkNotNull(hakukohde, "Hakukohde entity cannot be null!");

                MetaObject metaObject = new MetaObject();
                for (KoulutusmoduuliToteutus hk : hakukohde.getKoulutusmoduuliToteutuses()) {
                    metaObject.addKomotoOid(hk.getOid());
                }
                metaObject.setNewHakukohdeOid(oidService.get(TarjontaOidType.KOMOTO));
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

    @Override
    public boolean canStop() {
        return true;
    }

    @Override
    public boolean isCompleted() {
        return getState().getState() == 100.0;
    }
}
