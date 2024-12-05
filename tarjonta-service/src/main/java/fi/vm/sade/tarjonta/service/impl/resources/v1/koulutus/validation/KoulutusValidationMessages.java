/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation;

public enum KoulutusValidationMessages {

  // date
  KOULUTUS_ALKAMISPVM_INVALID("koulutuksenAlkamisPvms"),
  KOULUTUS_ALKAMISPVM_MISSING("koulutuksenAlkamisPvms"),
  KOULUTUS_ALKAMISPVM_SUCCESS("koulutuksenAlkamisPvms"),
  KOULUTUS_ALKAMISPVM_WRONGTYPEAFTER_31_01_2018("koulutuksenAlkamisPvmsAfter"),
  KOULUTUS_ALKAMISPVM_KAUSI_INVALID("koulutuksenAlkamiskausi"),
  KOULUTUS_ALKAMISPVM_KAUSI_MISSING("koulutuksenAlkamiskausi"),
  KOULUTUS_ALKAMISPVM_VUOSI_MISSING("koulutuksenAlkamisvuosi"),
  KOULUTUS_ALKAMISPVM_VUOSI_INVALID("koulutuksenAlkamisvuosi"),
  KOULUTUS_KAUSI_VUOSI_SUCCESS("koulutuksenAlkamisvuosi"),
  // sets
  KOULUTUS_OPETUSMUOTO_MISSING("opetusmuodos"),
  KOULUTUS_OPETUSMUOTO_INVALID("opetusmuodos"),
  KOULUTUS_OPETUSAIKA_MISSING("opetusaikas"),
  KOULUTUS_OPETUSAIKA_INVALID("opetusaikas"),
  KOULUTUS_TEEMAT_AIHEET_MISSING("aihees"),
  KOULUTUS_TEEMAT_AIHEET_INVALID("aihees"),
  KOULUTUS_OPETUSPAIKKA_MISSING("opetusPaikkas"),
  KOULUTUS_OPETUSPAIKKA_INVALID("opetusPaikkas"),
  KOULUTUS_OPETUSKIELI_MISSING("opetuskielis"),
  KOULUTUS_OPETUSKIELI_INVALID("opetuskielis"),
  // relations
  KOULUTUS_POHJAKOULUTUSVAATIMUS_MISSING("pohjakoulutusvaatimus"),
  KOULUTUS_POHJAKOULUTUSVAATIMUS_INVALID("pohjakoulutusvaatimus"),
  KOULUTUS_KOULUTUSLAJI_MISSING("koulutuslaji"),
  KOULUTUS_KOULUTUSLAJI_INVALID("koulutuslaji"),
  KOULUTUS_KOULUTUSKOODI_MISSING("koulutuskoodi"),
  KOULUTUS_KOULUTUSKOODI_INVALID("koulutuskoodi"),
  KOULUTUS_KOULUTUSALA_MISSING("koulutusala"),
  KOULUTUS_KOULUTUSALA_INVALID("koulutusala"),
  KOULUTUS_OPINTOALA_MISSING("opintoala"),
  KOULUTUS_OPINTOALA_INVALID("opintoala"),
  KOULUTUS_TUTKINTO_MISSING("tutkinto"),
  KOULUTUS_TUTKINTO_INVALID("tutkinto"),
  KOULUTUS_KOULUTUSTYYPPI_MISSING("koulutustyyppi"),
  KOULUTUS_KOULUTUSTYYPPI_INVALID("koulutustyyppi"),
  KOULUTUS_TUTKINTONIMIKE_MISSING("tutkintonimike"),
  KOULUTUS_TUTKINTONIMIKE_INVALID("tutkintonimike"),
  KOULUTUS_EQF_MISSING("eqf"),
  KOULUTUS_EQF_INVALID("eqf"),
  KOULUTUS_NQF_MISSING("nqf"),
  KOULUTUS_NQF_INVALID("nqf"),
  KOULUTUS_OPINTOJENLAAJUUSARVO_MISSING("opintojenLaajuusarvo"),
  KOULUTUS_OPINTOJENLAAJUUSARVO_INVALID("opintojenLaajuusarvo"),
  KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_MISSING("opintojenLaajuusyksikko"),
  KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_INVALID("opintojenLaajuusyksikko"),
  KOULUTUS_KOULUTUSASTE_MISSING("koulutusaste"),
  KOULUTUS_KOULUTUSASTE_INVALID("koulutusaste"),
  // name
  KOULUTUS_LUKIOLINJA_MISSING("lukiolinja"),
  KOULUTUS_OSAAMISALA_MISSING("osaamisala"),
  KOULUTUS_KOULUTUSOHJELMA_MISSING("koulutusohjelma"),
  KOULUTUS_KOULUTUSOHJELMA_INVALID("koulutusohjelma"),
  KOULUTUS_KOULUTUSOHJELMA_INVALID_VALUE("koulutusohjelma"),
  KOULUTUS_KOULUTUSOHJELMA_NAME_MISSING("koulutusohjelma"),
  KOULUTUS_TUTKINTO_OHJELMA_URI_REQUIRED(""),

  // other & common
  KOULUTUS_TOTEUTUSTYYPPI_ENUM_MISSING("toteutustyyppi"),
  KOULUTUS_MODUULITYYPPI_ENUM_MISSING("moduulityyppi"),
  KOULUTUS_KOULUTUSASTETYYPPI_ENUM_MISSING("koulutusasteTyyppi"),
  KOULUTUS_TILA_ENUM_MISSING("tila"),
  KOULUTUS_INPUT_OBJECT_MISSING(""),
  KOULUTUS_INPUT_PARAM_MISSING(""), // generic data error
  KOULUTUS_KOMOTO_MISSING("komoto"),
  KOULUTUS_KOMO_MISSING("komo"),
  KOULUTUS_NIMI_MISSING(""),
  KOULUTUS_TARJOAJA_MISSING("organisaatio.oid"),
  KOULUTUS_TARJOAJA_INVALID("organisaatio.oid"),
  KOULUTUS_JARJESTAJA_MISSING("organisaatio.oid"),
  KOULUTUS_JARJESTAJA_INVALID("organisaatio.oid"),
  KOULUTUS_JARJESTAJA_NOT_ALLOWED("organisaatio.oid"),
  KOULUTUS_INVALID_TRANSITION(""),
  // kesto
  KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING("suunniteltuKestoArvo"),
  KOULUTUS_SUUNNITELTU_KESTO_VALUE_INVALID("suunniteltuKestoArvo"),
  KOULUTUS_SUUNNITELTU_KESTO_TYPE_INVALID("suunniteltuKestoTyyppi"),
  KOULUTUS_SUUNNITELTU_KESTO_TYPE_MISSING("suunniteltuKestoTyyppi"),
  KOULUTUS_RELATION_KOMOTO_HAKUKOHDE_REMOVE_LINK(""),
  KOULUTUS_RELATION_KOMO_CHILD_REMOVE_LINK(""),
  KOULUTUS_RELATION_KOMO_PARENT_REMOVE_LINK(""),
  KOULUTUS_RELATION_KOMO_REMOVE_KOMOTO(""),
  KOULUTUS_DELETED(""),
  // tunniste
  KOULUTUS_TUNNISTE_LENGTH(""),
  // komo
  KOULUTUS_OPPILAITOSTYYPPI_MISSING("oppilaitostyyppi"),
  KOULUTUS_OPPILAITOSTYYPPI_INVALID("oppilaitostyyppi"),
  KOULUTUS_MODUULI_TYYPPI_MISSING("moduuliTyyppi"),
  KOULUTUS_MODUULI_TYYPPI_INVALID("moduuliTyyppi"),
  KOULUTUS_IMPORT_INVALID_GROUP("koulutuskoodi"),
  KOULUTUS_IMPORT_NON_UNIQUE_KOULUTUSOHJELMA("koulutusohjelma"),
  KOULUTUS_IMPORT_NON_UNIQUE_OSAAMISALA("osaamisala"),
  KOULUTUS_IMPORT_NON_UNIQUE_LUKIOLINJA("lukiolinja"),
  KOULUTUS_IMPORT_INVALID_MODULE_TUTKINTO_("koulutusmoduuliTyyppi"),
  KOULUTUS_IMPORT_INVALID_TUTKINTO("oid"),
  KOULUTUS_IMPORT_INVALID_DATA(""),
  KOULUTUS_IMPORT_FAILED("oid"),
  KOULUTUS_IMPORT_INVALID_COUNT(""),
  KOULUTUS_IMPORT_INVALID_TUTKINTO_COUNT(""),
  KOULUTUS_MODULE_NOT_FOUND("komoOid");

  private String fieldName;

  private KoulutusValidationMessages(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return this.fieldName;
  }

  @Override
  public String toString() {
    return this.name().toLowerCase();
  }

  public String lower() {
    return this.name().toLowerCase();
  }
}
