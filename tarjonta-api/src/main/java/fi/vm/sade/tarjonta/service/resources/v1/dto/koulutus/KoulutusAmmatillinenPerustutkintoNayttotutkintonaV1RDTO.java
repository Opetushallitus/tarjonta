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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 *
 * @author jani
 */
public class KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO extends KoulutusAmmatillinenPerustutkintoV1RDTO {

    @ApiModelProperty(value = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi", required = false)
    private Double hinta;

    @ApiModelProperty(value = "Valitaan opintojen maksullisuuden (false=koulutus ei vaadi maksua)")
    private Boolean opintojenMaksullisuus;

    @ApiModelProperty(value = "Koulutuksen ammattinimikkeet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO ammattinimikkeet;

    @ApiModelProperty(value = "Tarjoaja tai organisaation johon koulutus on liitetty", required = true)
    private OrganisaatioV1RDTO jarjestavaOrganisaatio;

    @ApiModelProperty(value = "Valmistavan koulutukseen tarvittavat tiedot", required = false)
    private KoulutusValmistavaV1RDTO valmistavaKoulutus;

    public KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO() {
        super(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA);
    }

    protected KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO(ToteutustyyppiEnum toteutustyyppi) {
        super(toteutustyyppi);
    }

    /**
     * @return the hinta
     */
    public Double getHinta() {
        return hinta;
    }

    /**
     * @param hinta the hinta to set
     */
    public void setHinta(Double hinta) {
        this.hinta = hinta;
    }

    /**
     * @return the valmistavaKoulutus
     */
    public KoulutusValmistavaV1RDTO getValmistavaKoulutus() {
        return valmistavaKoulutus;
    }

    /**
     * @param valmistavaKoulutus the valmistavaKoulutus to set
     */
    public void setValmistavaKoulutus(KoulutusValmistavaV1RDTO valmistavaKoulutus) {
        this.valmistavaKoulutus = valmistavaKoulutus;
    }

    /**
     * @return the ammattinimikkeet
     */
    public KoodiUrisV1RDTO getAmmattinimikkeet() {
        if (ammattinimikkeet == null) {
            ammattinimikkeet = new KoodiUrisV1RDTO();
        }

        return ammattinimikkeet;
    }

    /**
     * @param ammattinimikkeet the ammattinimikkeet to set
     */
    public void setAmmattinimikkeet(KoodiUrisV1RDTO ammattinimikkeet) {
        this.ammattinimikkeet = ammattinimikkeet;
    }

    /**
     * @return the jarjestavaOrganisaatio
     */
    public OrganisaatioV1RDTO getJarjestavaOrganisaatio() {
        return jarjestavaOrganisaatio;
    }

    /**
     * @param jarjestavaOrganisaatio the jarjestavaOrganisaatio to set
     */
    public void setJarjestavaOrganisaatio(OrganisaatioV1RDTO jarjestavaOrganisaatio) {
        this.jarjestavaOrganisaatio = jarjestavaOrganisaatio;
    }

    /**
     * @return the opintojenMaksullisuus
     */
    public Boolean getOpintojenMaksullisuus() {
        return opintojenMaksullisuus;
    }

    /**
     * @param opintojenMaksullisuus the opintojenMaksullisuus to set
     */
    public void setOpintojenMaksullisuus(Boolean opintojenMaksullisuus) {
        this.opintojenMaksullisuus = opintojenMaksullisuus;
    }

}
