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
package fi.vm.sade.tarjonta.service.search;

/**
 * Solr index field names.
 */
public class SolrFields {

    /**
     * Field names for koulutus docs
     */
    public static class Koulutus {
        protected static final String OID = "id";
        protected static final String ORG_NAME_FI = "orgnamefi_s"; 
        protected static final String ORG_NAME_SV = "orgnamesv_s"; 
        protected static final String ORG_NAME_EN = "orgnameen_s";
        protected static final String ORG_OID = "orgoid_s";                                     //needed for permissions
        protected static final String ORG_PATH = "orgpath_ss";                                  //needed search (org restriction)
        protected static final String KOULUTUSOHJELMA_FI = "koulutusohjelmafi_t";               //this is used in search
        protected static final String KOULUTUSOHJELMA_SV = "koulutusohjelmasv_t";               //this is used in search
        protected static final String KOULUTUSOHJELMA_EN = "koulutusohjelmaen_t";
        protected static final String KOULUTUSOHJELMA_URI = "koulutusohjelmauri_t";             //this is used in search
        protected static final String KOULUTUSKOODI_FI = "koulutuskoodifi_t";                   //this is used in search
        protected static final String KOULUTUSKOODI_SV = "koulutuskoodisv_t";                   //this is used in search
        protected static final String KOULUTUSKOODI_EN = "koulutuskoodien_t";                   //this is used in search
        protected static final String KOULUTUSKOODI_URI = "koulutuskoodiuri_t";             //this is used in search
        protected static final String TUTKINTONIMIKE_FI = "tutkintonimikefi_t";                   //this is used in search
        protected static final String TUTKINTONIMIKE_SV = "tutkintonimikesv_t";                   //this is used in search
        protected static final String TUTKINTONIMIKE_EN = "tutkintonimikeien_t";                   //this is used in search
        protected static final String TUTKINTONIMIKE_URI = "tutkintonimikeuri_t";             //this is used in search
        protected static final String VUOSI_KOODI = "vuosikoodi_s";                             //this is used in search
        protected static final String KAUSI_KOODI = "kausi_s";                                  //this is used in search
        protected static final String TILA_EN = "tila_s";
        protected static final String KOULUTUSMODUULI_OID = "koulutusmoduuli_s";
        protected static final String KOULUTUSTYYPPI = "koulutustyyppi_s";
    }
    
    /**
     * Field names for hakukohde docs
     */
    public static class Hakukohde {
        protected static final String OID="id";
        protected static final String ORG_NAME_FI = "orgnamefi_s";
        protected static final String ORG_NAME_SV = "orgnamesv_s";
        protected static final String ORG_NAME_EN = "orgnameen_s";
        protected static final String ORG_OID = "orgoid_s";                                     //needed for permissions
        protected static final String ORG_PATH = "orgpath_ss";                                  //needed search (org restriction)
        protected static final String VUOSI_KOODI = "vuosikoodi_s";                             //this is used in search
        protected static final String KAUSI_KOODI= "kausi_s";
        protected static final String KAUSI_FI = "kausifi_s";    
        protected static final String KAUSI_SV = "kausisv_s";
        protected static final String KAUSI_EN = "kausien_s";    
        protected static final String HAKUTAPA_FI = "hakutapafi_s";
        protected static final String HAKUTAPA_SV = "hakutapasv_s";
        protected static final String HAKUTAPA_EN = "hakutapaen_s";
        protected static final String HAKUTAPA_URI = "hakutapauri_s";
        protected static final String ALOITUSPAIKAT = "aloituspaikat_s";
        protected static final String TILA = "tila_s";
        protected static final String HAKUKOHTEEN_NIMI_FI = "hakukohteennimifi_t";              //this is used in search
        protected static final String HAKUKOHTEEN_NIMI_SV = "hakukohteennimisv_t";              //this is used in search
        protected static final String HAKUKOHTEEN_NIMI_EN = "hakukohteennimien_t";              //this is used in search
        protected static final String HAKUKOHTEEN_NIMI_URI = "hakukohdeuri_t";
        protected static final String HAUN_ALKAMISPVM = "haunalkupvm_s";
        protected static final String HAUN_PAATTYMISPVM = "haunpaattymispvm_s";
    }
    
    /**
     * Field names for organisaatio docs
     */
    public static class Organisaatio {
    	protected static final String OID="id";
        protected static final String ORG_NAME_FI = "orgnamefi_s";
        protected static final String ORG_NAME_SV = "orgnamesv_s";
        protected static final String ORG_NAME_EN = "orgnameen_s";
    }
}
