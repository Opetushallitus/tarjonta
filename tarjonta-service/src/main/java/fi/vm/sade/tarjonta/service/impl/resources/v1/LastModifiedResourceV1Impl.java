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
import fi.vm.sade.tarjonta.service.resources.v1.LastModifiedV1Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Search for modified resources.
 *
 * @author mlyly
 */
public class LastModifiedResourceV1Impl implements LastModifiedV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(LastModifiedResourceV1Impl.class);

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Override
    public Map<String, List<String>> lastModified(long lastModifiedTs, Boolean deleted) {
        fi.vm.sade.tarjonta.shared.types.TarjontaTila tarjontaTila = null;
        TarjontaTila hakukohdeTarjontaTila = null;
        // If negative, look back that time
        if (lastModifiedTs < 0) {
            lastModifiedTs = new Date().getTime() + lastModifiedTs;
        }

        // If not specified, 5 minutes by default
        if (lastModifiedTs == 0) {
            lastModifiedTs = new Date().getTime() - (1000 * 60 * 5);
        }



        Date ts = new Date(lastModifiedTs);
        String tsFmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(ts);

        LOG.debug("lastModified({}) - search changes since: {}", lastModifiedTs, tsFmt);

        Map<String, List<String>> result = new HashMap<String, List<String>>();

        result.put("koulutusmoduuli", koulutusmoduuliDAO.findOIDsBy(null, 0, 0, null, ts));
        result.put("koulutusmoduuliToteutus", koulutusmoduuliToteutusDAO.findOIDsBy(null, 0, 0, null, ts));
        result.put("haku", hakuDAO.findOIDsBy(null, 0, 0, null, ts));
        result.put("hakukohde", hakukohdeDAO.findOIDsBy(null, 0, 0, null, ts, true));

        if (deleted) {
            List<String> deletedResults = hakukohdeDAO.findOIDsBy(TarjontaTila.POISTETTU, 0, 0, null, ts, true);
            List<String> results = result.get("hakukohde");
            for(String hakukohde : deletedResults) {
                if (!results.contains(hakukohde)) {
                        results.add(hakukohde);
                }
            }
            result.put("hakukohde", results);
        }

        // Add used timestamp to the result
        List<String> tmp = new ArrayList<String>();
        tmp.add("" + lastModifiedTs);
        tmp.add(tsFmt);
        result.put("lastModifiedTs", tmp);

        return result;
    }

}
