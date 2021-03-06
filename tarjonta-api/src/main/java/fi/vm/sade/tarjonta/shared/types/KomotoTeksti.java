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
package fi.vm.sade.tarjonta.shared.types;

/**
 * Komoto lisätieto-avain.
 *
 * <p>
 * HUOM! Käytetään sekä tietokanta-avaimena että osana apia, eli älä muuta näitä
 * arvoja.</p>
 *
 * @author Timo Santasalo / Teknokala Ky
 */
public enum KomotoTeksti {
    LISATIEDOT,
    EDELTAVAT_OPINNOT,
    OPETUKSEN_AIKA_JA_PAIKKA,
    MAKSULLISUUS,
    ARVIOINTIKRITEERIT,
    LOPPUKOEVAATIMUKSET,
    PAINOTUS,
    KOULUTUSOHJELMAN_VALINTA,
    KUVAILEVAT_TIEDOT,
    SISALTO,
    SIJOITTUMINEN_TYOELAMAAN,
    KANSAINVALISTYMINEN,
    YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA,
    LISATIETOA_OPETUSKIELISTA,
    TUTKIMUKSEN_PAINOPISTEET,
    PAAAINEEN_VALINTA,
    OPPILAITOSKOHTAISET_OPPIAINEET_JA_KURSSIT,
    KOHDERYHMA, //aikuislukio + pervakot
    OPPIAINEET_JA_KURSSIT, //aikuislukio
    OSAAMISALAN_VALINTA, //amm nayttotutkintona
    NAYTTOTUTKINNON_SUORITTAMINEN, //amm nayttotutkintona
    OPISKELUN_HENKILOKOHTAISTAMINEN; //amm nayttotutkintona
}
