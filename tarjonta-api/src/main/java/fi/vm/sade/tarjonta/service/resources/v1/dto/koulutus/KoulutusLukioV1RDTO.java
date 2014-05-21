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
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.KoulutustyyppiUri;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mlyly
 */
public class KoulutusLukioV1RDTO extends KoulutusV1RDTO {

    private String komoParentOid;
    private String komotoParentOid;
    private List<String> komoChildOids;
    private List<String> komotoChildOids;

    @ApiModelProperty(value = "Pohjakoulutusvaatimus-koodi", required = true)
    private KoodiV1RDTO pohjakoulutusvaatimus;

    @ApiModelProperty(value = "Koulutuslaji-koodi", required = true)
    private KoodiV1RDTO koulutuslaji;

    @ApiModelProperty(value = "Kielivalikoimat", required = true)
    private KoodiValikoimaV1RDTO kielivalikoima;

    @ApiModelProperty(value = "Lukiodiplomit", required = true)
    private KoodiUrisV1RDTO lukiodiplomit;

    @ApiModelProperty(value = "Tutkintonimike", required = true)
    private KoodiV1RDTO tutkintonimike;

    @ApiModelProperty(value = "HTTP-linkki opetussuunnitelmaan")
    private String linkkiOpetussuunnitelmaan;

    public KoulutusLukioV1RDTO() {
        super(KoulutustyyppiUri.LUKIOKOULUTUS);
    }

    protected KoulutusLukioV1RDTO(KoulutustyyppiUri koulutustyyppiUri) {
        super(koulutustyyppiUri);
    }

    /**
     * @return the pohjakoulutusvaatimus
     */
    public KoodiV1RDTO getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    /**
     * @param pohjakoulutusvaatimus the pohjakoulutusvaatimus to set
     */
    public void setPohjakoulutusvaatimus(KoodiV1RDTO pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
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
     * @return the komoChildOids
     */
    public List<String> getKomoChildOids() {
        if (komoChildOids == null) {
            komoChildOids = new ArrayList<String>();
        }

        return komoChildOids;
    }

    /**
     * @param komoChildOids the komoChildOids to set
     */
    public void setKomoChildOids(List<String> komoChildOids) {
        this.komoChildOids = komoChildOids;
    }

    /**
     * @return the komotoChildOids
     */
    public List<String> getKomotoChildOids() {
        if (komotoChildOids == null) {
            komotoChildOids = new ArrayList<String>();
        }

        return komotoChildOids;
    }

    /**
     * @param komotoChildOids the komotoChildOids to set
     */
    public void setKomotoChildOids(List<String> komotoChildOids) {
        this.komotoChildOids = komotoChildOids;
    }

    /**
     * @return the komoParentOid
     */
    public String getKomoParentOid() {
        return komoParentOid;
    }

    /**
     * @param komoParentOid the komoParentOid to set
     */
    public void setKomoParentOid(String komoParentOid) {
        this.komoParentOid = komoParentOid;
    }

    /**
     * @return the komotoParentOid
     */
    public String getKomotoParentOid() {
        return komotoParentOid;
    }

    /**
     * @param komotoParentOid the komotoParentOid to set
     */
    public void setKomotoParentOid(String komotoParentOid) {
        this.komotoParentOid = komotoParentOid;
    }

    /**
     * @return the kielivalikoima
     */
    public KoodiValikoimaV1RDTO getKielivalikoima() {
        return kielivalikoima;
    }

    /**
     * @param kielivalikoima the kielivalikoima to set
     */
    public void setKielivalikoima(KoodiValikoimaV1RDTO kielivalikoima) {
        this.kielivalikoima = kielivalikoima;
    }

    /**
     * @return the lukiodiplomit
     */
    public KoodiUrisV1RDTO getLukiodiplomit() {
        if (lukiodiplomit == null) {
            lukiodiplomit = new KoodiUrisV1RDTO();
        }
        return lukiodiplomit;
    }

    /**
     * @param lukiodiplomit the lukiodiplomit to set
     */
    public void setLukiodiplomit(KoodiUrisV1RDTO lukiodiplomit) {
        this.lukiodiplomit = lukiodiplomit;
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

}
