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
import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.data.dto.KoodiRelaatio;
import fi.vm.sade.tarjonta.data.dto.YhteishakuKooditDTO;
import fi.vm.sade.tarjonta.data.util.DataUtils;
import java.io.IOException;
import java.util.*;

import fi.vm.sade.tarjonta.data.util.TarjontaDataKoodistoHelper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fi.vm.sade.tarjonta.data.dto.Koodi;

/**
 *
 * @author Jani Wilén
 */
@Service
public class UploadKoodistoData {

    //TODO : clean these Strings and make main-class which calls this class

    private static final String KOULUTUS_OHJELMA_KOODISTO_URI = "koulutusohjelmat";
    private static final String HAKUKOHDE_OHJELMA_KOODISTO_URI = "Hakukohde";

    //private static final String BASE_GROUP_URI_FOR_KOODISTO = "baseuri";
    private static final String BASE_GROUP_URI_FOR_KOODISTO = "http://ryhma";
    private static final String KOODISTO_VALINTAKUVAUSRYHMA_URI = "uri:valintakuvausryhmäTesti";
    private static final String KOODISTO_VALINTAKUVAUSRYHMA_NIMI = "ValintakuvausryhmäTesti";

    private static final String KOODISTO_HAKUKOHDE_URI = "hakukohdeTestiTuomas";
    private static final String KOODISTO_TUTKINTO_URI = "tutkintoTestiTuomas";

    //private static final String KOODISTO_HAKUKOHDE_URI = "uri:hakukohdeTesti";
    private static final String KOODISTO_HAKUKOHDE_NIMI = "HakukohdeTesti";
    //private static final String ORGANISAATIO_NIMI = "omistaja";
    private static final String ORGANISAATIO_NIMI = "Espoon kaupunki";
    //private static final String ORGANISAATIO_OID = "xxxxxxxxxx.xxx.x.x.x";
    private static final String ORGANISAATIO_OID = "1.2.246.562.10.10108401950";
    private static final Date ACTIVATED_DATE = new DateTime(2013, 1, 1, 1, 1).toDate();
    @Autowired
    private KoodistoAdminService koodistoAdminService;
    @Autowired
    private KoodiAdminService koodiAdminService;
    @Autowired
    private KoodiService koodiService;
    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private TarjontaDataKoodistoHelper koodistoHelper;

    private HakukohdeData data;
    private List<KoodiType> hakukohdeCodes;


    private HashMap<String,KoodiType> ylaKoodistoArvot;
    private HashMap<String,KoodiType> alaKoodistoArvot;

    private final Logger log = LoggerFactory.getLogger(UploadKoodistoData.class);


    public void startFullImport() throws IOException, ExceptionMessage {
//        data = new HakukohdeData();
        List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(BASE_GROUP_URI_FOR_KOODISTO);
        koodistoHelper.setOrganisaatioNimi(ORGANISAATIO_NIMI);
        koodistoHelper.setOrganisaatioOid(ORGANISAATIO_OID);

        String relaatioPath = "C:\\KoodistoImportTesti\\koulutusOhjelmaHakukohdeRelaatio.xls";

       /* KoodiRelaatioData koodiRelaatioData = new KoodiRelaatioData(relaatioPath);

        Set<KoodiRelaatio> koodiRelaatios = koodiRelaatioData.getKoodiRelaatios();
        for (KoodiRelaatio relaatio:koodiRelaatios) {
            relaatio.setYlaArvoKoodisto(KOULUTUS_OHJELMA_KOODISTO_URI);
            relaatio.setAlaArvoKoodisto(HAKUKOHDE_OHJELMA_KOODISTO_URI);
        }
        createKoodiRelations(koodiRelaatios);*/
       /* final String uniqueId = "-" + new Date().getTime();

        String hakukohdeExcelPath = "C:\\KoodistoImportTesti\\koodistoHakukohdeImportTest.xls";
        String tutkintoExcelPath = "C:\\KoodistoImportTesti\\koodistoTutkintoImportTest.xls";


        CreateKoodistoDataType firstKoodisto = koodistoHelper.addCodeGroup(ryhmaUris,KOODISTO_HAKUKOHDE_URI,"Hakukohde testi");
        CreateKoodistoDataType secondKoodisto = koodistoHelper.addCodeGroup(ryhmaUris,KOODISTO_TUTKINTO_URI,"Tutkinto testi");



        CommonKoodiData firstKoodis = new CommonKoodiData(hakukohdeExcelPath);
        CommonKoodiData secondKoodis = new CommonKoodiData(tutkintoExcelPath);
        ylaKoodistoArvot = loadKoodisToKoodisto(secondKoodis.getLoadedKoodis(),KOODISTO_TUTKINTO_URI);
        alaKoodistoArvot =  loadKoodisToKoodisto(firstKoodis.getLoadedKoodis(),KOODISTO_HAKUKOHDE_URI);

        KoodiRelaatioData koodiRelaatioData = new KoodiRelaatioData(relaatioPath);

        addKoodistoRelations(koodiRelaatioData.getKoodiRelaatios());*/

        /*createHakukohdeKoodisto(ryhmaUris, uniqueId);
        createValintaryhmaKoodisto(ryhmaUris, uniqueId);*/
    }

    private HashMap<String,KoodiType> loadKoodisToKoodisto(Set<Koodi> koodis, String koodistoName) {
        HashMap<String,KoodiType> koodiUriArvoPair = new HashMap<String, KoodiType>();

        for (Koodi koodi : koodis) {
            final String koodiUri = koodistoName + "/" + koodi.getKoodiArvo();
            KoodiType koodiType = koodistoHelper.addCodeItem(koodistoName, koodiUri, koodi.getKoodiArvo(), koodi.getKoodiNimiFi());

            koodiUriArvoPair.put(koodi.getKoodiArvo(),koodiType);

        }


        return koodiUriArvoPair;
    }

    private void createKoodiRelations(Set<KoodiRelaatio> koodiRelaatios) {
          for (KoodiRelaatio koodiRelaatio:koodiRelaatios) {
             addKoodiRelation(koodiRelaatio);
          }
    }

    private void addKoodiRelation(KoodiRelaatio koodiRelaatio) {
        List<KoodiUriAndVersioType> alakoodis = new ArrayList<KoodiUriAndVersioType>();
        alakoodis.add(createKoodiUriVersio(koodiRelaatio.getKoodiAlaArvo(),koodiRelaatio.getAlaArvoKoodisto()));
        try {
        log.info("Trying to create relation with yla-arvo : {} and ala-arvo : {}",koodiRelaatio.getKoodiYlaArvo(),koodiRelaatio.getKoodiAlaArvo());
        koodiAdminService.addRelationByAlakoodi(createKoodiUriVersio(koodiRelaatio.getKoodiYlaArvo(),koodiRelaatio.getYlaArvoKoodisto()),alakoodis,SuhteenTyyppiType.SISALTYY);
        } catch (Exception exp) {
            log.warn("Unable to create relation with arvos : {} exception : {}",koodiRelaatio.getKoodiYlaArvo()+ " " + koodiRelaatio.getKoodiAlaArvo(),exp.toString());
        }
    }

    private KoodiUriAndVersioType createKoodiUriVersio(String koodiArvo,String koodistoUri) {
        List<KoodiType> koodiTypes = koodistoHelper.getKoodiByArvoAndKoodistoNimi(koodiArvo,koodistoUri);

        if (koodiTypes != null && koodiTypes.size() > 0) {

        return koodistoHelper.createKoodiUriAndVersioType(koodiTypes.get(0));
        } else {
            return new KoodiUriAndVersioType();
        }
    }

    private void addKoodistoRelations(Set<KoodiRelaatio> relaatiot) {

        for (KoodiRelaatio koodiRelaatio:relaatiot) {
            List<KoodiUriAndVersioType> alakoodis = new ArrayList<KoodiUriAndVersioType>();

            alakoodis.add(koodistoHelper.createKoodiUriAndVersioType(alaKoodistoArvot.get(koodiRelaatio.getKoodiAlaArvo())));

            KoodiType koodi = ylaKoodistoArvot.get(koodiRelaatio.getKoodiYlaArvo());

            koodiAdminService.addRelationByAlakoodi(koodistoHelper.createKoodiUriAndVersioType(koodi), alakoodis, SuhteenTyyppiType.SISALTYY);
        }

    }

    private String createHakukohdeKoodisto(List<String> baseUri, String uniqueId) {
        final String uri = KOODISTO_HAKUKOHDE_URI + uniqueId;
        final String name = KOODISTO_HAKUKOHDE_NIMI + uniqueId;

        KoodistoType created = koodistoHelper.getKoodistoByUri(koodistoHelper.addCodeGroup(baseUri, uri, name).getKoodistoUri());

        /*
         * Create the code group items.
         */
        int index = 1;

        for (YhteishakuKooditDTO dto : data.getLoadedData()) {
            final String uriSuffix = uniqueId + "/" + index;
            final String koodiUri = uri + "/" + dto.getHakukohdeKoodiArvo() + uriSuffix;
            final String koodiArvo = dto.getHakukohdeKoodiArvo() + uriSuffix;
            final String koodiNimi = dto.getHakukohteenNimi() + uriSuffix;

            koodistoHelper.addCodeItem(created.getKoodistoUri(), koodiUri, koodiArvo, koodiNimi);
            index++;

            if (index > 10) {
                break;
            }
        }

        SearchKoodisByKoodistoCriteriaType criteria = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(created.getKoodistoUri(), created.getVersio());
        hakukohdeCodes = koodiService.searchKoodisByKoodisto(criteria);

        return uri;
    }

    private String createValintaryhmaKoodisto(List<String> baseUri, String uniqueId) {
        final String uri = KOODISTO_VALINTAKUVAUSRYHMA_URI + uniqueId;
        final String name = KOODISTO_VALINTAKUVAUSRYHMA_NIMI + uniqueId;

        /*
         * Create code
         */

        KoodistoType created = koodistoHelper.getKoodistoByUri(koodistoHelper.addCodeGroup(baseUri, uri, name).getKoodistoUri());
        KoodiType addCodeItem = koodistoHelper.addCodeItem(created.getKoodistoUri(), "valintauri" + uniqueId, "valinta" + uniqueId, "valinta" + uniqueId);

        /*
         * Add relatios for the code above.
         */
        List<KoodiUriAndVersioType> alakoodis = new ArrayList<KoodiUriAndVersioType>();
        for (KoodiType koodisto : hakukohdeCodes) {
            alakoodis.add(koodistoHelper.createKoodiUriAndVersioType(koodisto));
        }
        //koodiAdminService.addRelationByAlakoodi(createKoodiUriAndVersioType(addCodeItem), alakoodis, SuhteenTyyppiType.SISALTYY);


        return uri;
    }







}
