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
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Conversion services for REST api.
 *
 * @author mlyly
 */
public class KoulutusmoduuliToKomoConverter extends BaseRDTOConverter<Koulutusmoduuli, KomoDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusmoduuliToKomoConverter.class);

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Override
    public KomoDTO convert(Koulutusmoduuli s) {
        // LOG.debug("convert({}) --> Komo", s);

        if (s == null) {
            return null;
        }

        KomoDTO t = new KomoDTO();

        // TODO convert, but efficiently! t.setAlaModuulit(null);

        t.setCreated(null);
        t.setCreatedBy(null);
        t.setEqfLuokitusUri(s.getEqfLuokitus());
        t.setJatkoOpintoMahdollisuudet(convertMonikielinenTekstiToMap(s.getJatkoOpintoMahdollisuudet()));
        t.setKoulutuksenRakenne(convertMonikielinenTekstiToMap(s.getKoulutuksenRakenne()));
        t.setKoulutusAlaUri(s.getKoulutusala());
        t.setKoulutusAsteUri(s.getKoulutusAste());
        // TODO t.setKoulutusLuokitusKoodiUri(s.get); ??? waat
        t.setKoulutusOhjelmaKoodiUri(s.getKoulutusohjelmaKoodi());
        t.setKoulutusTyyppiUri(s.getKoulutustyyppi()); // TODO onko uri?
        t.setLaajuusArvo(s.getLaajuusArvo());
        t.setLaajuusYksikkoUri(s.getLaajuusYksikko());
        t.setLukiolinjaUri(s.getLukiolinja()); // TODO onko?
        t.setModuuliTyyppi(s.getModuuliTyyppi() != null ? s.getModuuliTyyppi().name() : null);
        t.setNimi(convertMonikielinenTekstiToMap(s.getNimi()));
        t.setNqfLuokitusUri(s.getNqfLuokitus());
        t.setOid(s.getOid());
        t.setOpintoalaUri(s.getOpintoala());

        // ? Does KOMO have a "owner" other that OPH?
        t.setOrganisaatioOid(s.getOmistajaOrganisaatioOid());
        t.setTarjoajaOid(s.getOmistajaOrganisaatioOid());

        t.setTavoitteet(convertMonikielinenTekstiToMap(s.getTavoitteet()));
        t.setTila(s.getTila() != null ? s.getTila().name() : null);
        t.setTutkintoOhjelmanNimiUri(s.getTutkintoOhjelmanNimi());
        t.setTutkintonimikeUri(s.getTutkintonimike());
        t.setUlkoinenTunniste(s.getUlkoinenTunniste());
        t.setModifiedBy(null);
        t.setModified(s.getUpdated());
        t.setVersion(s.getVersion() == null ? 0 : s.getVersion().intValue());

        t.setKoulutusKoodiUri(s.getKoulutusKoodi());

        //
        // TODO check the efficiency of this... :(
        //

        List<String> ylaModuleOIDs = new ArrayList<String>();
        List<String> alaModuleOIDs = new ArrayList<String>();

        // TODO multiple parents?
        Koulutusmoduuli parent = koulutusmoduuliDAO.findParentKomo(s);
        if (parent != null) {
            ylaModuleOIDs.add(parent.getOid());
        }

        // Get children
        for (Koulutusmoduuli child : s.getAlamoduuliList()) {
            alaModuleOIDs.add(child.getOid());
        }

        t.setYlaModuulit(ylaModuleOIDs);
        t.setAlaModuulit(alaModuleOIDs);

        // LOG.debug("  --> {}", t);

        return t;
    }




}
