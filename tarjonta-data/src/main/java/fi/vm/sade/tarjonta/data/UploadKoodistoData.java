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

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.data.dto.Koodi;
import fi.vm.sade.tarjonta.data.dto.KoodiRelaatio;
import fi.vm.sade.tarjonta.data.loader.xls.KoodistoRelaatioExcelReader;
import fi.vm.sade.tarjonta.data.util.CommonConstants;
import fi.vm.sade.tarjonta.data.util.DataUtils;
import fi.vm.sade.tarjonta.data.util.TarjontaDataKoodistoHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * @author Jani Wilén
 */
@Service
public class UploadKoodistoData {
    @Autowired
    private CommonConstants commonConstants;

    @Autowired
    private TarjontaDataKoodistoHelper koodistoHelper;

    @Autowired
    private KoodistoRelaatioExcelReader koodistoRelaatioExcelReader;

    private final Logger log = LoggerFactory.getLogger(UploadKoodistoData.class);

    public void createKoodistoRelations(final String pathToFile) throws IOException {
        final List<KoodiRelaatio> relaatios = koodistoRelaatioExcelReader.readKoodiRelaatioExcel(pathToFile);
        if (relaatios != null) {
            final Set<KoodiRelaatio> koodiRelaatios = new HashSet<KoodiRelaatio>(relaatios);
            createKoodiRelations(koodiRelaatios);
        } else {
            log.warn("No koodisto relations read from [{}]", pathToFile);
        }
    }

    @PostConstruct
    private void loadOrgInfoToHelper() {
        koodistoHelper.setOrganisaatioOid(commonConstants.getOrganisaatioOid());
        koodistoHelper.setOrganisaatioNimi(commonConstants.getOrganisaatioNimi());
    }

    private KoodistoType createKoodisto(final String koodistoRyhmaUri, final String koodistoNimi,
                                        final String koodistoUri, final String orgOid) throws ExceptionMessage {
        final List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(commonConstants.getBaseGroupUri());
        if (StringUtils.isNotBlank(koodistoRyhmaUri)) {
            ryhmaUris.add(koodistoRyhmaUri);
        }

        try {
            return koodistoHelper.addKoodisto(ryhmaUris, koodistoUri, koodistoNimi);
        } catch (final Exception exp) {
            log.warn("Unable to create koodisto [{}], trying to remove it and create it again", koodistoUri);
            try {
                koodistoHelper.removeKoodisto(koodistoUri, orgOid);
                return koodistoHelper.addKoodisto(ryhmaUris, koodistoUri, koodistoNimi);
            } catch (final Exception exxp) {
                log.error("re-creation of koodisto failed [{}]", exxp.getMessage());

                throw new ExceptionMessage(exxp.getMessage());
            }

        }
    }

    public void loadKoodistoFromExcel(final String pathToExcel, final String koodistoRyhmaUri, final String koodistoNimi,
                                      final String orgOid) throws IOException, ExceptionMessage {
        final String koodistoUri = DataUtils.createKoodiUriFromName(koodistoNimi);

        final KoodistoType createdKoodisto = createKoodisto(koodistoRyhmaUri, koodistoNimi, koodistoUri, orgOid);
        if (createdKoodisto != null) {
            final CommonKoodiData koodis = new CommonKoodiData(pathToExcel);
            if (koodis != null && koodis.getLoadedKoodis() != null && koodis.getLoadedKoodis().size() > 0) {
                loadKoodisToKoodisto(koodis.getLoadedKoodis(), koodistoUri);
            } else {
                log.warn("Loaded koodis was empty or null!");
            }

            // change koodisto tila to HYVAKSYTTY: this should also approve all sketched koodis in the koodisto
            // NOTE! takes a long time if there are lots of koodis
            final KoodistoType approvedKoodisto = koodistoHelper.approveKoodisto(createdKoodisto);
        }
    }

    private HashMap<String, KoodiType> loadKoodisToKoodisto(final Set<Koodi> koodis, final String koodistoName) {
        final HashMap<String, KoodiType> koodiUriArvoPair = new HashMap<String, KoodiType>();

        for (final Koodi koodi : koodis) {
            final KoodiType koodiType = koodistoHelper.addKoodi(koodi, DataUtils.createKoodiUriFromName(koodistoName));
        }

        return koodiUriArvoPair;
    }

    private void createKoodiRelations(final Set<KoodiRelaatio> koodiRelaatios) {
        for (final KoodiRelaatio koodiRelaatio : koodiRelaatios) {
            addKoodiRelation(koodiRelaatio);
        }
    }

    private void addKoodiRelation(final KoodiRelaatio koodiRelaatio) {
        final List<KoodiUriAndVersioType> alakoodis = new ArrayList<KoodiUriAndVersioType>();
        alakoodis.add(createKoodiUriVersio(koodiRelaatio.getKoodiAlaArvo(), koodiRelaatio.getAlaArvoKoodisto()));
        koodistoHelper.addRelaatioByAlakoodi(createKoodiUriVersio(koodiRelaatio.getKoodiYlaArvo(), koodiRelaatio.getYlaArvoKoodisto()), alakoodis, SuhteenTyyppiType.SISALTYY);
    }

    private KoodiUriAndVersioType createKoodiUriVersio(final String koodiArvo, final String koodistoUri) {
        final List<KoodiType> koodiTypes = koodistoHelper.getKoodiByArvoAndKoodistoNimi(koodiArvo, koodistoUri);

        if (koodiTypes != null && koodiTypes.size() > 0) {

            return koodistoHelper.createKoodiUriAndVersioType(koodiTypes.get(0));
        } else {
            return new KoodiUriAndVersioType();
        }
    }
}
