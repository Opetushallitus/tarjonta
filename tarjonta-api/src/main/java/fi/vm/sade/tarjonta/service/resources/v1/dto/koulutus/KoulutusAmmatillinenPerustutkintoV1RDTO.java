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
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.Map;

/**
 *
 * @author mlyly
 */
public class KoulutusAmmatillinenPerustutkintoV1RDTO extends KoulutusV1RDTO {

    @ApiModelProperty(value = "Pohjakoulutusvaatimus-koodi", required = true)
    private KoodiV1RDTO pohjakoulutusvaatimus;

    @ApiModelProperty(value = "Tutkintonimike", required = true)
    private KoodiV1RDTO tutkintonimike;

    @ApiModelProperty(value = "HTTP-linkki opetussuunnitelmaan", required = false)
    private String linkkiOpetussuunnitelmaan;

    @ApiModelProperty(value = "Koulutuslaji-koodi", required = true)
    private KoodiV1RDTO koulutuslaji;

    @ApiModelProperty(value = "Koulutuksen-tavoitteet", required = false)
    private Map<String, String> koulutuksenTavoitteet;

    public KoulutusAmmatillinenPerustutkintoV1RDTO() {
        super(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
    }

    public KoulutusAmmatillinenPerustutkintoV1RDTO(ToteutustyyppiEnum toteutustyyppiEnum, ModuulityyppiEnum moduulityyppiEnum) {
        super(toteutustyyppiEnum, moduulityyppiEnum);
    }

    protected KoulutusAmmatillinenPerustutkintoV1RDTO(ToteutustyyppiEnum koulutustyyppiUri) {
        super(koulutustyyppiUri, ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
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
     * @param pohjakoulutusvaatimus the pohjakoulutusvaatimus to set
     */
    public void setPohjakoulutusvaatimus(KoodiV1RDTO pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }

    /**
     * @return the pohjakoulutusvaatimus
     */
    public KoodiV1RDTO getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    public Map<String, String> getKoulutuksenTavoitteet() {
        return koulutuksenTavoitteet;
    }

    public void setKoulutuksenTavoitteet(Map<String, String> koulutuksenTavoitteet) {
        this.koulutuksenTavoitteet = koulutuksenTavoitteet;
    }
}
