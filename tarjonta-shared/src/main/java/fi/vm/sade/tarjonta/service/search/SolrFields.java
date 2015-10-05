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

    public static final String RYHMA_PRIORITEETTI_EI_MAARITELTY = "RYHMA_PRIORITEETTI_EI_MAARITELTY";
    
    /**
     * Field names for koulutus docs
     */
    public static class Koulutus {

        protected static final String NIMET = "nimi_ss";
        protected static final String NIMIEN_KIELET = "nimikieli_ss";
        protected static final String OID = "id";
        protected static final String ORG_OID = "orgoid_ss";                                     //needed for permissions
        protected static final String ORG_PATH = "orgpath_ss";                                  //needed search (org restriction)
        protected static final String JARJESTAJA_PATH = "jarjestajapath_ss";
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
        protected static final String KAUSI_FI = "kausifi_s";
        protected static final String KAUSI_SV = "kausisv_s";
        protected static final String KAUSI_EN = "kausien_s";
        protected static final String KAUSI_URI = "kausiuri_s";                                  //this is used in search
        protected static final String TILA = "tila_s";
        protected static final String KOULUTUSMODUULI_OID = "koulutusmoduuli_s";
        protected static final String KOULUTUSASTETYYPPI_ENUM = "koulutustyyppi_s";
        protected static final String KOULUTUSTYYPPI_URI = "koulutustyyppiuri_s";
        protected static final String TOTEUTUSTYYPPI_ENUM = "toteutustyyppi_s";
        protected static final String HAKUTAPA_URIS = "hakutapauri_ss";
        protected static final String HAKUTYYPPI_URIS = "hakutyyppiuri_ss";
        protected static final String KOULUTUSMODUULITYYPPI_ENUM = "koulutusmoduulityyppi_s";
        protected static final String KOULUTUKSEN_TARJOAJA_KOMOTO = "koulutuksentarjoajakomoto_s";

        protected static final String POHJAKOULUTUSVAATIMUS_URI = "pohjakoulutusvaatimusuri_s";
        protected static final String POHJAKOULUTUSVAATIMUS_FI = "pohjakoulutusvaatimusfi_s";
        protected static final String POHJAKOULUTUSVAATIMUS_EN = "pohjakoulutusvaatimussv_s";
        protected static final String POHJAKOULUTUSVAATIMUS_SV = "pohjakoulutusvaatimusen_s";
        protected static final String TEKSTIHAKU = "tekstihaku_tnws";        //this is used in search
        protected static final String HAKUKOHDE_OIDS = "hakukohdeoids_ss";        //this is used in search
        protected static final String HAKU_OIDS = "hakuoids_ss";
        protected static final String KOULUTUSLAJI_URIS = "koulutuslajiuri_ss";
        protected static final String KOULUTUSLAJI_FI = "koulutuslajifi_s";
        protected static final String KOULUTUSLAJI_SV = "koulutuslajisv_s";
        protected static final String KOULUTUSLAJI_EN = "koulutuslajien_s";

        protected static final String KOULUTUALKAMISPVM_MIN = "koulutusalkamispvm_min_dt";
        protected static final String KOULUTUALKAMISPVM_MAX = "koulutusalkamispvm_max_dt";
        protected static final String KOHDEJOUKKO_URIS = "kohdejoukkouri_ss";
        protected static final String OPPILAITOSTYYPPI_URIS = "oppilaitostyyppiuri_ss";
        protected static final String KUNTA_URIS = "kuntauri_ss";
        protected static final String OPETUSKIELI_URIS = "opetuskieliuri_ss";

        protected static final String OPINTOALA_URI = "opintoalauri_ss";
        protected static final String KOULUTUSALA_URI = "koulutusalauri_ss";

        protected static final String PARENT_KOULUTUSMODUULI_OID = "parentkoulutusmoduuli_s";
        protected static final String SIBLING_KOMOTOS = "siblingkomotos_ss";
    }

    /**
     * Field names for hakukohde docs
     */
    public static class Hakukohde {

        protected static final String NIMET = "nimi_ss";
        protected static final String NIMIEN_KIELET = "nimikieli_ss";
        protected static final String OID = "id";
        protected static final String ORG_OID = "orgoid_ss";                                     //needed for permissions
        protected static final String ORG_PATH = "orgpath_ss";                                  //needed search (org restriction)
        protected static final String ORG_NIMI = "orgnimi_s";
        protected static final String ORG_NIMI_LOWERCASE = "orgnimi_lowercase_s";
        protected static final String VUOSI_KOODI = "vuosikoodi_s";                             //this is used in search
        protected static final String POHJAKOULUTUSVAATIMUS_URI = "pohjakoulutusvaatimusuri_s";
        protected static final String POHJAKOULUTUSVAATIMUS_FI = "pohjakoulutusvaatimusfi_s";
        protected static final String POHJAKOULUTUSVAATIMUS_EN = "pohjakoulutusvaatimussv_s";
        protected static final String POHJAKOULUTUSVAATIMUS_SV = "pohjakoulutusvaatimusen_s";
        protected static final String KOULUTUSLAJI_FI = "koulutuslajifi_s";
        protected static final String KOULUTUSLAJI_SV = "koulutuslajisv_s";
        protected static final String KOULUTUSLAJI_EN = "koulutuslajen_s";
        protected static final String KOULUTUSLAJI_URI = "koulutuslajiuri_s";
        protected static final String KOULUTUSLAJI_URIS = "koulutuslajiuri_ss";
        protected static final String KAUSI_URI = "kausiuri_s";
        protected static final String KAUSI_FI = "kausifi_s";
        protected static final String KAUSI_SV = "kausisv_s";
        protected static final String KAUSI_EN = "kausien_s";
        protected static final String HAKUTAPA_FI = "hakutapafi_s";
        protected static final String HAKUTAPA_SV = "hakutapasv_s";
        protected static final String HAKUTAPA_EN = "hakutapaen_s";
        protected static final String HAKUTAPA_URI = "hakutapauri_s";
        protected static final String ALOITUSPAIKAT = "aloituspaikat_s";
        protected static final String ALOITUSPAIKAT_KUVAUKSET = "aloituspaikatnimi_ss";
        protected static final String ALOITUSPAIKAT_KIELET = "aloituspaikatkieli_ss";
        protected static final String TILA = "tila_s";
        protected static final String HAKUKOHTEEN_NIMI_FI = "hakukohteennimifi_t";              //this is used in search
        protected static final String HAKUKOHTEEN_NIMI_SV = "hakukohteennimisv_t";              //this is used in search
        protected static final String HAKUKOHTEEN_NIMI_EN = "hakukohteennimien_t";              //this is used in search
        protected static final String HAKUKOHTEEN_NIMI_URI = "hakukohdeuri_s";
        protected static final String HAUN_ALKAMISPVM = "haunalkupvm_s";
        protected static final String HAUN_PAATTYMISPVM = "haunpaattymispvm_s";
        protected static final String HAKUAIKA_STRING = "hakuaika_s";
        protected static final String HAKUAIKA_RYHMA = "hakuaikaryhma_s";
        protected static final String HAUN_OID = "hakuoid_s";
        protected static final String TEKSTIHAKU = "tekstihaku_tnws";        //this is used in search
        protected static final String KOULUTUS_OIDS = "koulutusoids_ss";        //this is used in search
        protected static final String HAKUTYYPPI_URI = "hakutyyppiuri_s";
        protected static final String KOULUTUSASTETYYPPI = "koulutusastetyyppi_s";
        protected static final String ORGANISAATIORYHMAOID = "organisaatioryhmaoid_ss";
        protected static final String KOHDEJOUKKO_URI = "kohdejoukkouri_s";
        protected static final String OPPILAITOSTYYPPI_URIS = "oppilaitostyyppiuri_ss";
        protected static final String KUNTA_URIS = "kuntauri_ss";
        protected static final String OPETUSKIELI_URIS = "opetuskieliuri_ss";
        protected static final String KOULUTUSTYYPPI_URI = "koulutustyyppiuri_s";
        protected static final String TOTEUTUSTYYPPI_ENUM = "toteutustyyppi_s";
        protected static final String KOULUTUSMODUULITYYPPI_ENUM = "koulutusmoduulityyppi_s";
        protected static final String RYHMA_OIDS = "ryhmaoid_ss";
        protected static final String RYHMA_PRIORITEETIT = "ryhmaprioriteetti_ss";
    }

}
