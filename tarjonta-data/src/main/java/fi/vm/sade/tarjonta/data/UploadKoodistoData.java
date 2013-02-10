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
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.data.dto.YhteishakuKooditDTO;
import fi.vm.sade.tarjonta.data.util.DataUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jani Wilén
 */
@Service
public class UploadKoodistoData {

    private static final String BASE_GROUP_URI_FOR_KOODISTO = "baseuri";
    private static final String KOODISTO_VALINTAKUVAUSRYHMA_URI = "uri:valintakuvausryhmä";
    private static final String KOODISTO_VALINTAKUVAUSRYHMA_NIMI = "valintakuvausryhmä";
    private static final String KOODISTO_HAKUKOHDE_URI = "uri:hakukohde";
    private static final String KOODISTO_HAKUKOHDE_NIMI = "Hakukohde";
    private static final String ORGANISAATIO_NIMI = "omistaja";
    private static final String ORGANISAATIO_OID = "xxxxxxxxxx.xxx.x.x.x";
    private static final Date ACTIVATED_DATE = new DateTime(2013, 1, 1, 1, 1).toDate();
    @Autowired
    private KoodistoAdminService koodistoAdminService;
    @Autowired
    private KoodiAdminService koodiAdminService;
    @Autowired
    private KoodiService koodiService;
    @Autowired
    private KoodistoService koodistoService;
    private HakukohdeData data;
    private List<KoodiType> hakukohdeCodes;

    public void startFullImport() throws IOException, ExceptionMessage {
        data = new HakukohdeData();
        List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(BASE_GROUP_URI_FOR_KOODISTO);

        final String uniqueId = "-" + new Date().getTime();
        createHakukohdeKoodisto(ryhmaUris, uniqueId);
        createValintaryhmaKoodisto(ryhmaUris, uniqueId);
    }

    private String createHakukohdeKoodisto(List<String> baseUri, String uniqueId) {
        final String uri = KOODISTO_HAKUKOHDE_URI + uniqueId;
        final String name = KOODISTO_HAKUKOHDE_NIMI + uniqueId;

        KoodistoType created = getKoodistoByUri(addCodeGroup(baseUri, uri, name).getKoodistoUri());

        /*
         * Create the code group items.
         */
        int index = 1;

        for (YhteishakuKooditDTO dto : data.getLoadedData()) {
            final String uriSuffix = uniqueId + "/" + index;
            final String koodiUri = uri + "/" + dto.getHakukohdeKoodiArvo() + uriSuffix;
            final String koodiArvo = dto.getHakukohdeKoodiArvo() + uriSuffix;
            final String koodiNimi = dto.getHakukohteenNimi() + uriSuffix;

            addCodeItem(created.getKoodistoUri(), koodiUri, koodiArvo, koodiNimi);
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

        KoodistoType created = getKoodistoByUri(addCodeGroup(baseUri, uri, name).getKoodistoUri());
        KoodiType addCodeItem = addCodeItem(created.getKoodistoUri(), "valintauri" + uniqueId, "valinta" + uniqueId, "valinta" + uniqueId);

        /*
         * Add relatios for the code above.
         */
        List<KoodiUriAndVersioType> alakoodis = new ArrayList<KoodiUriAndVersioType>();
        for (KoodiType koodisto : hakukohdeCodes) {
            alakoodis.add(createKoodiUriAndVersioType(koodisto));
        }
        koodiAdminService.addRelationByAlakoodi(createKoodiUriAndVersioType(addCodeItem), alakoodis, SuhteenTyyppiType.SISALTYY);

        return uri;
    }

    private KoodiType addCodeItem(String koodistoUri, String koodiUri, String arvo, String name) {
        CreateKoodiDataType createKoodiDataType = DataUtils.createCreateKoodiDataType(koodiUri,
                arvo, TilaType.HYVAKSYTTY, ACTIVATED_DATE, null, name);
        return koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);
    }

    private CreateKoodistoDataType addCodeGroup(List<String> baseUri, String koodistoUri, String name) {
        CreateKoodistoDataType createKoodistoDataType = DataUtils.createCreateKoodistoDataType(
                koodistoUri, ORGANISAATIO_NIMI, ORGANISAATIO_OID, ACTIVATED_DATE, ACTIVATED_DATE,
                name);
        koodistoAdminService.createKoodisto(baseUri, createKoodistoDataType);

        return createKoodistoDataType;
    }

    private KoodistoType getKoodistoByUri(String koodistoUri) {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchType);
        if (koodistos.size() != 1) {
            throw new RuntimeException("Failing");
        }

        return koodistos.get(0);
    }

    private KoodiUriAndVersioType createKoodiUriAndVersioType(KoodiType koodi) {
        KoodiUriAndVersioType k = new KoodiUriAndVersioType();
        k.setKoodiUri(koodi.getKoodiUri());
        k.setVersio(koodi.getVersio());
        return k;
    }
}
