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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.valmistava.ValmistavaV1RDTO;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 *
 * @author jani
 */
public abstract class NayttotutkintoV1RDTO extends KoulutusV1RDTO {

    @ApiModelProperty(value = "Tutkintonimike", required = true)
    private KoodiV1RDTO tutkintonimike;

    @ApiModelProperty(value = "HTTP-linkki opetussuunnitelmaan", required = false)
    private String linkkiOpetussuunnitelmaan;

    @ApiModelProperty(value = "Koulutuslaji-koodi", required = true)
    private KoodiV1RDTO koulutuslaji;

    @ApiModelProperty(value = "Tarjoaja tai organisaation johon koulutus on liitetty", required = true)
    private OrganisaatioV1RDTO jarjestavaOrganisaatio;

    @ApiModelProperty(value = "Valmistavan koulutukseen tarvittavat tiedot", required = false)
    private ValmistavaV1RDTO valmistavaKoulutus;

    @ApiModelProperty(value = "Osaamisalan tarkenne", required = false)
    private String tarkenne;

    protected NayttotutkintoV1RDTO(ToteutustyyppiEnum toteutustyyppi, ModuulityyppiEnum moduulityyppi) {
        super(toteutustyyppi, moduulityyppi);
    }

    /**
     * @return the valmistavaKoulutus
     */
    public ValmistavaV1RDTO getValmistavaKoulutus() {
        return valmistavaKoulutus;
    }

    /**
     * @param valmistavaKoulutus the valmistavaKoulutus to set
     */
    public void setValmistavaKoulutus(ValmistavaV1RDTO valmistavaKoulutus) {
        this.valmistavaKoulutus = valmistavaKoulutus;
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
     * @return the tutkintonimike
     */
    public KoodiV1RDTO getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(KoodiV1RDTO tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }

    /**
     * @return the koulutuslaji
     */
    public KoodiV1RDTO getKoulutuslaji() {
        return koulutuslaji;
    }

    /**
     * @param koulutuslaji the koulutuslaji to set
     */
    public void setKoulutuslaji(KoodiV1RDTO koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    /**
     * @return the linkkiOpetussuunnitelmaan
     */
    public String getLinkkiOpetussuunnitelmaan() {
        return linkkiOpetussuunnitelmaan;
    }

    /**
     * @param linkkiOpetussuunnitelmaan the linkkiOpetussuunnitelmaan to set
     */
    public void setLinkkiOpetussuunnitelmaan(String linkkiOpetussuunnitelmaan) {
        this.linkkiOpetussuunnitelmaan = linkkiOpetussuunnitelmaan;
    }

    /**
     * @return the tarkenne
     */
    public String getTarkenne() {
        return tarkenne;
    }

    /**
     * @param tarkenne the tarkenne to set
     */
    public void setTarkenne(String tarkenne) {
        this.tarkenne = tarkenne;
    }

}
