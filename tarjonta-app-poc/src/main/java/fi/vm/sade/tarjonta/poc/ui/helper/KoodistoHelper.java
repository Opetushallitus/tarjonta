/*
 *
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
package fi.vm.sade.tarjonta.poc.ui.helper;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * 
 * @author Tuomas Katva
 */
@Configurable(preConstruction = true)
public class KoodistoHelper {

    @Autowired(required = true)
    private KoodiService koodiService;
    @Autowired(required = true)
    private KoodistoService koodistoService;

    public String tryGetKoodistoArvo(String arvo, String koodistoUri) {
        try {
            SearchKoodistosCriteriaType koodistoSearchCriteria = KoodistoServiceSearchCriteriaBuilder.latestValidAcceptedKoodistoByUri(koodistoUri);

            List<KoodistoType> koodistoResult = getKoodistoService().searchKoodistos(koodistoSearchCriteria);
            if (koodistoResult.size() != 1) {
                // FIXME: Throw something other than RuntimeException?
                throw new RuntimeException("No koodisto found for koodisto URI " + koodistoUri);
            }
            KoodistoType koodisto = koodistoResult.get(0);

            SearchKoodisByKoodistoCriteriaType koodiSearchCriteria = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUriAndKoodistoVersio(arvo,
                    koodistoUri, koodisto.getVersio());
            List<KoodiType> koodit = getKoodiService().searchKoodisByKoodisto(koodiSearchCriteria);
            if (koodit != null && koodit.size() > 0) {
                return koodit.get(0).getKoodiUri();
            } else {
                return arvo;
            }
        } catch (Exception exp) {
            return arvo;
        }
    }

    public List<KoodiType> getKoodisto(String koodistoUri) {
        KoodistoServiceSearchCriteriaBuilder.latestValidAcceptedKoodistoByUri(koodistoUri);
        SearchKoodistosCriteriaType koodistoSearchCriteria = KoodistoServiceSearchCriteriaBuilder.latestValidAcceptedKoodistoByUri(koodistoUri);

        List<KoodistoType> koodistoResult = getKoodistoService().searchKoodistos(koodistoSearchCriteria);
        if (koodistoResult.size() != 1) {
            // FIXME: Throw something other than RuntimeException?
            throw new RuntimeException("No koodisto found for koodisto URI " + koodistoUri);
        }
        KoodistoType koodisto = koodistoResult.get(0);
        SearchKoodisByKoodistoCriteriaType koodiSearchCriteria = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(koodistoUri,
                koodisto.getVersio());
        List<KoodiType> koodit = getKoodiService().searchKoodisByKoodisto(koodiSearchCriteria);
        return koodit;
    }

    public String tryGetArvoByKoodi(String koodi) {
        try {
            SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestValidAcceptedKoodiByUri(koodi);
            List<KoodiType> result = getKoodiService().searchKoodis(searchCriteria);
            if (result.size() != 1) {
                // FIXME: Throw something other than RuntimeException?
                throw new RuntimeException("No koodi found for koodi URI " + koodi);
            }

            KoodiType koodidto = result.get(0);
            return fi.vm.sade.koodisto.util.KoodistoHelper.getKoodiMetadataForLanguage(koodidto,
                    fi.vm.sade.koodisto.util.KoodistoHelper.getKieliForLocale(I18N.getLocale())).getNimi();
        } catch (Exception exp) {
            return koodi;
        }
    }

    /**
     * @return the koodiService
     */
    public KoodiService getKoodiService() {
        return koodiService;
    }

    /**
     * @param koodiService
     *            the koodiService to set
     */
    public void setKoodiService(KoodiService koodiService) {
        this.koodiService = koodiService;
    }

    /**
     * @return the koodistoService
     */
    public KoodistoService getKoodistoService() {
        return koodistoService;
    }

    /**
     * @param koodistoService
     *            the koodistoService to set
     */
    public void setKoodistoService(KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
    }
}
