/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.data;

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.data.dto.KoodiRelaatio;
import fi.vm.sade.tarjonta.data.util.CommonConstants;
import fi.vm.sade.tarjonta.data.util.TarjontaDataKoodistoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Create Valintaperustekuvaus demo data to Koodisto service.
 *
 * @author Jani Wilén
 */
public class Valintaperustekuvaus {
    private final Logger log = LoggerFactory.getLogger(Valintaperustekuvaus.class);
    private static final String KOODISTO_HAKUKOHDE_URI = "Hakukohde";
    private static final String KOODISTO_HAKUKOHDE_NAME = "T2 hakukohde";
    private static final String KOODISTO_VALINTAPERUSTEKUVAUS_URI = "t2_valintaperustekuvaus";
    private static final String KOODISTO_VALINTAPERUSTEKUVAUS_NAME = "T2 valintaperustekuvaus";
    private static final String KOODISTO_SORA_URI = "t2_sora_vaatimukset";
    private static final String KOODISTO_SORA_NAME = "T2 SORA-vaatimukset";
    private static final String RELATION_VALINTA_HAKUKOHDE_FILE_PATH = "/koodistoValintaperustekuvausRelaatioImport.xls";
    private static final String FILE_HAKUKOHDE_PATH = "/koodistoHakukohdeImportTest.xls";
    private static final String FILE_VALINTAPERUSTEKUVAUS_PATH = "/koodistoValintaperutekuvausImport.xls";

    public Valintaperustekuvaus(final TarjontaDataKoodistoHelper koodistoHelper,
                                final CommonConstants constant) throws IOException, ExceptionMessage {
        final List ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(constant.getBaseGroupUri());

        koodistoHelper.setOrganisaatioNimi(constant.getOrganisaatioNimi());
        koodistoHelper.setOrganisaatioOid(constant.getOrganisaatioOid());

        if (!koodistoHelper.isKoodisto(KOODISTO_HAKUKOHDE_URI)) {
            //final URL hakukohdeExcelPath = this.getClass().getResource(FILE_HAKUKOHDE_PATH);

            //koodistoHelper.addKoodisto(ryhmaUris, KOODISTO_HAKUKOHDE_URI, KOODISTO_HAKUKOHDE_NAME);
            //log.debug("createdkoodisto : {}", KOODISTO_HAKUKOHDE_URI);

            throw new RuntimeException("Hakukohde koodisto was missing!");
        }

//        CommonKoodiData hakukohde = new CommonKoodiData(hakukohdeExcelPath.getPath());
//        koodistoHelper.loadKoodisToKoodisto(hakukohde.getLoadedKoodis(), KOODISTO_HAKUKOHDE_URI);
//        log.debug("Imported : {}", KOODISTO_HAKUKOHDE_URI);

        //NEW DATA
        addKoodistos(koodistoHelper, ryhmaUris);
        addKoodis(koodistoHelper);
        addKoodistoRelations(koodistoHelper);
    }

    private void addKoodistos(final TarjontaDataKoodistoHelper koodistoHelper,
                              final List<String> ryhmaUris) throws IOException, ExceptionMessage {
        if (!koodistoHelper.isKoodisto(KOODISTO_VALINTAPERUSTEKUVAUS_URI)) {
            koodistoHelper.addKoodisto(ryhmaUris, KOODISTO_VALINTAPERUSTEKUVAUS_NAME);
            log.debug("Koodisto created, koodisto uri : {}", KOODISTO_VALINTAPERUSTEKUVAUS_URI);
        } else {
            log.info("Koodisto service already has the koodisto.  Koodisto uri : {}", KOODISTO_VALINTAPERUSTEKUVAUS_URI);
        }
    }

    private void addKoodis(final TarjontaDataKoodistoHelper koodistoHelper) throws IOException, ExceptionMessage {
        final URL valintaperustekuvaus = this.getClass().getResource(FILE_VALINTAPERUSTEKUVAUS_PATH);
        final CommonKoodiData valinta = new CommonKoodiData(valintaperustekuvaus.getPath());
        koodistoHelper.loadKoodisToKoodisto(valinta.getLoadedKoodis(), KOODISTO_VALINTAPERUSTEKUVAUS_URI);
        log.debug("Koodis imported to koodisto uri : {}", KOODISTO_VALINTAPERUSTEKUVAUS_URI);
    }

    private void addKoodistoRelations(final TarjontaDataKoodistoHelper koodistoHelper) throws IOException, ExceptionMessage {
        final URL resource = this.getClass().getResource(RELATION_VALINTA_HAKUKOHDE_FILE_PATH);
        final KoodiRelaatioData koodiRelaatioData = new KoodiRelaatioData(resource.getPath());

        final Set<KoodiRelaatio> koodiRelaatios = koodiRelaatioData.getKoodiRelaatios();
        for (final KoodiRelaatio relaatio : koodiRelaatios) {
            relaatio.setYlaArvoKoodisto(KOODISTO_VALINTAPERUSTEKUVAUS_URI);
            relaatio.setAlaArvoKoodisto(KOODISTO_HAKUKOHDE_URI);
        }
        koodistoHelper.createKoodiRelations(koodiRelaatios);
        log.debug("Koodi relations import both ways {} <-> {}", KOODISTO_VALINTAPERUSTEKUVAUS_URI, KOODISTO_HAKUKOHDE_URI);
    }
}
