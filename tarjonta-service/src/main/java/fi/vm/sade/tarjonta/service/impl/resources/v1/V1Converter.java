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
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusAmmattikorkeakouluRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusRDTO;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API V1 converters to/from model/domain.
 *
 * @author mlyly
 */
public class V1Converter {

    private static final Logger LOG = LoggerFactory.getLogger(V1Converter.class);
    // ----------------------------------------------------------------------
    // HAKU
    // ----------------------------------------------------------------------
    HakuDAO _hakuDao;

    public void setHakuDao(HakuDAO _hakuDao) {
        this._hakuDao = _hakuDao;
    }

    public HakuDAO getHakuDao() {
        if (_hakuDao == null) {
            throw new IllegalStateException("HAKUDAO == NULL");
        }
        return _hakuDao;
    }




    // ----------------------------------------------------------------------
    // HAKUKOHDE
    // ----------------------------------------------------------------------
    HakukohdeDAO _hakukohdeDao;

    public HakukohdeDAO getHakukohdeDao() {
        if (_hakukohdeDao == null) {
            throw new IllegalStateException("HAKUKOHDEDAO == NULL");
        }
        return _hakukohdeDao;
    }

    public void setHakukohdeDao(HakukohdeDAO _hakukohdeDao) {
        this._hakukohdeDao = _hakukohdeDao;
    }




    /**
     * Convert domain Hakukohde to REST HakukohdeRDTO.
     *
     * @param hakukohde
     * @return
     */
    public HakukohdeRDTO toHakukohdeRDTO(Hakukohde hakukohde) {
        LOG.info("toHakukohdeRDTO({})", hakukohde);

        HakukohdeRDTO t = new HakukohdeRDTO();


        LOG.info("  -> result = {}", t);
        return t;
    }

    /**
     * Convert from REST HakukohdeRDTO to domain Hakukohde.
     *
     * @param hakukohde
     * @return
     */
    public Hakukohde toHakukohde(HakukohdeRDTO hakukohde) {
        LOG.info("toHakukohde({})", hakukohde);
        Hakukohde t = null;

        LOG.info("  -> result = {}", t);
        return t;
    }


    // ----------------------------------------------------------------------
    // KOULUTUS
    // ----------------------------------------------------------------------

    KoulutusmoduuliDAO _komoDao;
    KoulutusmoduuliToteutusDAO _komotoDao;

    public KoulutusmoduuliDAO getKomoDao() {
        if (_komoDao == null) {
            throw new IllegalStateException("KOMODAO == NULL");
        }
        return _komoDao;
    }

    public KoulutusmoduuliToteutusDAO getKomotoDao() {
        if (_komotoDao == null) {
            throw new IllegalStateException("KOMOTODAO == NULL");
        }
        return _komotoDao;
    }

    public void setKomoDao(KoulutusmoduuliDAO _komoDao) {
        this._komoDao = _komoDao;
    }

    public void setKomotoDao(KoulutusmoduuliToteutusDAO _komotoDao) {
        this._komotoDao = _komotoDao;
    }

    public KoulutusRDTO fromKomotoToKoulutusRDTO(KoulutusmoduuliToteutus komoto) {
        LOG.warn("fromKomotoToKoulutusRDTO({}) -- ONLY PARTIALLY IMPLEMENTED!", komoto);

        // TODO implement me!

        KoulutusRDTO t = null;

        if (komoto != null) {
            // TODO TYYPPI!?
            KoulutusAmmattikorkeakouluRDTO k = new KoulutusAmmattikorkeakouluRDTO();

            k.setCreated(komoto.getUpdated());
            k.setCreatedBy(komoto.getLastUpdatedByOid());
            k.setModified(komoto.getUpdated());
            k.setModifiedBy(komoto.getLastUpdatedByOid());

            Koulutusmoduuli komo = komoto.getKoulutusmoduuli();

            k.setKomotoOid(komoto.getOid());
            k.setKomoOid(komo.getOid());


            t = k;
        }

        return t;
    }

    public KoulutusRDTO fromKomotoToKoulutusRDTO(String oid) {
        return fromKomotoToKoulutusRDTO(getKomotoDao().findByOid(oid));
    }

}
