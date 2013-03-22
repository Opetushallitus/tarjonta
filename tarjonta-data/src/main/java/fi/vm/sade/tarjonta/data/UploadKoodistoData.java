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

import fi.vm.sade.koodisto.service.KoodiAdminService;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoAdminService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.data.dto.Koodi;
import fi.vm.sade.tarjonta.data.dto.KoodiRelaatio;
import fi.vm.sade.tarjonta.data.loader.xls.KoodistoRelaatioExcelReader;
import fi.vm.sade.tarjonta.data.util.CommonConstants;
import fi.vm.sade.tarjonta.data.util.DataUtils;
import fi.vm.sade.tarjonta.data.util.TarjontaDataKoodistoHelper;
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
    private static final String BASE_GROUP_URI_FOR_KOODISTO = "http://ryhma";

    @Autowired
    private CommonConstants commonConstants;

    @Autowired
    private TarjontaDataKoodistoHelper koodistoHelper;

    @Autowired
    private KoodistoRelaatioExcelReader koodistoRelaatioExcelReader;

    private final Logger log = LoggerFactory.getLogger(UploadKoodistoData.class);

    public void createKoodistoRelations(String pathToFile) throws IOException {
        List<KoodiRelaatio> relaatios = koodistoRelaatioExcelReader.readKoodiRelaatioExcel(pathToFile);
        if (relaatios != null) {
            Set<KoodiRelaatio> koodiRelaatios = new HashSet<KoodiRelaatio>(relaatios);
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

    private boolean createKoodisto(String koodistoNimi, String koodistoUri, String orgOid) throws ExceptionMessage {
        List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(commonConstants.getBaseGroupUri());


        try {
            CreateKoodistoDataType koodisto = koodistoHelper.addCodeGroup(ryhmaUris, koodistoUri, koodistoNimi);
            return true;
        } catch (Exception exp) {
            log.warn("Unable to create koodisto [{}], trying to remove it and create it again", koodistoUri);
            try {
                koodistoHelper.removeKoodisto(koodistoUri, orgOid);
                CreateKoodistoDataType koodisto = koodistoHelper.addCodeGroup(ryhmaUris, koodistoUri, koodistoNimi);
                return true;
            } catch (Exception exxp) {
                log.error("re-creation of koodisto failed [{}]", exxp.getMessage());

                throw new ExceptionMessage(exxp.getMessage());
            }

        }
    }

    public void loadKoodistoFromExcel(String pathToExcel, String koodistoNimi, String orgOid) throws IOException, ExceptionMessage {
        String koodistoUri = DataUtils.createKoodiUriFromName(koodistoNimi);
        if (createKoodisto(koodistoNimi, koodistoUri, orgOid)) {
            CommonKoodiData koodis = new CommonKoodiData(pathToExcel);
            if (koodis != null && koodis.getLoadedKoodis() != null && koodis.getLoadedKoodis().size() > 0) {
                loadKoodisToKoodisto(koodis.getLoadedKoodis(), koodistoUri);
            } else {
                log.warn("Loaded koodis was empty or null!");
            }
        }
    }

    private HashMap<String, KoodiType> loadKoodisToKoodisto(Set<Koodi> koodis, String koodistoName) {
        HashMap<String, KoodiType> koodiUriArvoPair = new HashMap<String, KoodiType>();

        for (Koodi koodi : koodis) {
            KoodiType koodiType = koodistoHelper.addCodeItem(koodi, DataUtils.createKoodiUriFromName(koodistoName));
        }

        return koodiUriArvoPair;
    }

    private void createKoodiRelations(Set<KoodiRelaatio> koodiRelaatios) {
        for (KoodiRelaatio koodiRelaatio : koodiRelaatios) {
            addKoodiRelation(koodiRelaatio);
        }
    }

    private void addKoodiRelation(KoodiRelaatio koodiRelaatio) {
        List<KoodiUriAndVersioType> alakoodis = new ArrayList<KoodiUriAndVersioType>();
        alakoodis.add(createKoodiUriVersio(koodiRelaatio.getKoodiAlaArvo(), koodiRelaatio.getAlaArvoKoodisto()));
        try {
            log.info("Trying to create relation with yla-arvo [{}], ala-arvo [{}]", koodiRelaatio.getKoodiYlaArvo(), koodiRelaatio.getKoodiAlaArvo());
            koodistoHelper.addRelationByAlakoodi(createKoodiUriVersio(koodiRelaatio.getKoodiYlaArvo(), koodiRelaatio.getYlaArvoKoodisto()), alakoodis, SuhteenTyyppiType.SISALTYY);
        } catch (Exception exp) {
            log.warn("Unable to create relation with arvos [{}], exception [{}]", koodiRelaatio.getKoodiYlaArvo() + " " + koodiRelaatio.getKoodiAlaArvo(), exp.toString());
        }
    }

    private KoodiUriAndVersioType createKoodiUriVersio(String koodiArvo, String koodistoUri) {
        List<KoodiType> koodiTypes = koodistoHelper.getKoodiByArvoAndKoodistoNimi(koodiArvo, koodistoUri);

        if (koodiTypes != null && koodiTypes.size() > 0) {

            return koodistoHelper.createKoodiUriAndVersioType(koodiTypes.get(0));
        } else {
            return new KoodiUriAndVersioType();
        }
    }
}
