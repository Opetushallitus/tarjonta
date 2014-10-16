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

        protected static final String NIMET = "nimi_ss";
        protected static final String NIMIEN_KIELET = "nimikieli_ss";
        protected static final String OID = "id";
        protected static final String ORG_OID = "orgoid_ss";                                     //needed for permissions
        protected static final String ORG_PATH = "orgpath_ss";                                  //needed search (org restriction)
        protected static final String KOULUTUSOHJELMA_FI = "koulutusohjelmafi_t";               //this is used in search
        protected static final String KOULUTUSOHJELMA_SV = "koulutusohjelmasv_t";               //this is used in search
        protected static final String KOULUTUSOHJELMA_EN = "koulutusohjelmaen_t";
        protected static final String KOULUTUSOHJELMA_URI = "koulutusohjelmauri_s";             //this is used in search
        protected static final String KOULUTUSKOODI_FI = "koulutuskoodifi_t";                   //this is used in search
        protected static final String KOULUTUSKOODI_SV = "koulutuskoodisv_t";                   //this is used in search
        protected static final String KOULUTUSKOODI_EN = "koulutuskoodien_t";                   //this is used in search
        protected static final String KOULUTUSKOODI_URI = "koulutuskoodiuri_s";             //this is used in search
        protected static final String TUTKINTONIMIKE_FI = "tutkintonimikefi_t";                   //this is used in search
        protected static final String TUTKINTONIMIKE_SV = "tutkintonimikesv_t";                   //this is used in search
        protected static final String TUTKINTONIMIKE_EN = "tutkintonimikeen_t";                   //this is used in search
        protected static final String TUTKINTONIMIKE_URI = "tutkintonimikeuri_s";             //this is used in search
        protected static final String VUOSI_KOODI = "vuosikoodi_s";                             //this is used in search
        protected static final String KAUSI_URI = "kausiuri_s";                                  //this is used in search
        protected static final String TILA = "tila_s";
        protected static final String KOULUTUSMODUULI_OID = "koulutusmoduuli_s";
        protected static final String KOULUTUSASTETYYPPI_ENUM = "koulutustyyppi_s";
        protected static final String KOULUTUSTYYPPI_URI = "koulutustyyppiuri_s";
        protected static final String TOTEUTUSTYYPPI_ENUM = "toteutustyyppiuri_s";

        protected static final String POHJAKOULUTUSVAATIMUS_URI = "pohjakoulutusvaatimusuri_s";
        protected static final String POHJAKOULUTUSVAATIMUS_FI = "pohjakoulutusvaatimusfi_s";
        protected static final String POHJAKOULUTUSVAATIMUS_EN = "pohjakoulutusvaatimussv_s";
        protected static final String POHJAKOULUTUSVAATIMUS_SV = "pohjakoulutusvaatimusen_s";
        protected static final String TEKSTIHAKU = "tekstihaku_tnws";        //this is used in search
        protected static final String HAKUKOHDE_OIDS = "hakukohdeoids_ss";        //this is used in search
        protected static final String KOULUTUSLAJI_URIS = "koulutuslajiuri_ss";
        protected static final String KOULUTUSLAJI_FI = "koulutuslajifi_s";
        protected static final String KOULUTUSLAJI_SV = "koulutuslajisv_s";
        protected static final String KOULUTUSLAJI_EN = "koulutuslajien_s";

//        protected static final String KOULUTUALKAMISPVM = "koulutusalkamispvm_dt";
        protected static final String KOULUTUALKAMISPVM_MIN = "koulutusalkamispvm_min_dt";
        protected static final String KOULUTUALKAMISPVM_MAX = "koulutusalkamispvm_max_dt";
    }

    /**
     * Field names for hakukohde docs
     */
    public static class Hakukohde {

        protected static final String NIMET = "nimi_ss";
        protected static final String NIMIEN_KIELET = "nimikieli_ss";
        protected static final String OID = "id";
        protected static final String ORG_OID = "orgoid_s";                                     //needed for permissions
        protected static final String ORG_PATH = "orgpath_ss";                                  //needed search (org restriction)
        protected static final String VUOSI_KOODI = "vuosikoodi_s";                             //this is used in search
        protected static final String POHJAKOULUTUSVAATIMUS_URI = "pohjakoulutusvaatimusuri_s";
        protected static final String POHJAKOULUTUSVAATIMUS_FI = "pohjakoulutusvaatimusfi_s";
        protected static final String POHJAKOULUTUSVAATIMUS_EN = "pohjakoulutusvaatimussv_s";
        protected static final String POHJAKOULUTUSVAATIMUS_SV = "pohjakoulutusvaatimusen_s";
        protected static final String KOULUTUSLAJI_FI = "koulutuslajifi_s";
        protected static final String KOULUTUSLAJI_SV = "koulutuslajisv_s";
        protected static final String KOULUTUSLAJI_EN = "koulutuslajen_s";
        protected static final String KOULUTUSLAJI_URI = "koulutuslajiuri_s";
        protected static final String KAUSI_URI = "kausiuri_s";
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
        protected static final String HAKUKOHTEEN_NIMI_URI = "hakukohdeuri_s";
        protected static final String HAUN_ALKAMISPVM = "haunalkupvm_s";
        protected static final String HAUN_PAATTYMISPVM = "haunpaattymispvm_s";
        protected static final String HAUN_OID = "hakuoid_s";
        protected static final String TEKSTIHAKU = "tekstihaku_tnws";        //this is used in search
        protected static final String KOULUTUS_OIDS = "koulutusoids_ss";        //this is used in search
        protected static final String HAKUTYYPPI_URI = "hakutyyppiuri_s";
        protected static final String KOULUTUSASTETYYPPI = "koulutusastetyyppi_s";
        protected static final String ORGANISAATIORYHMAOID = "organisaatioryhmaoid_ss";
        protected static final String TOTEUTUSTYYPPI_ENUM = "toteutustyyppiuri_s";
    }

}
