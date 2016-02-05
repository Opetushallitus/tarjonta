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
package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * DTO representing Haku.
 *
 * @author mlyly
 */
@ApiModel(value = "Haku REST-api malli (rajapinnan versio V1)")
public class HakuV1RDTO extends BaseV1RDTO {

    @ApiModelProperty(value = "Hakukauden koodisto uri", required = true)
    private String hakukausiUri;

    @ApiModelProperty(value = "Hakukauden vuosi", required = true)
    private int hakukausiVuosi;

    @ApiModelProperty(value = "Hakutapa koodisto uri", required = true)
    private String hakutapaUri;

    @ApiModelProperty(value = "Hakulomakkeen www-osoite", required = false)
    private String hakulomakeUri;

    @ApiModelProperty(value = "Hakutyypin koodisto uri", required = true)
    private String hakutyyppiUri;

    @ApiModelProperty(value = "Haun kohdejoukko koodisto uri", required = true)
    private String kohdejoukkoUri;

    @ApiModelProperty(value = "Haun kohdejoukon tarkenne", required = false)
    private String kohdejoukonTarkenne;

    @ApiModelProperty(value = "Koulutuksen alkamisvuosi", required = true)
    private int koulutuksenAlkamisVuosi;

    @ApiModelProperty(value = "Koulutuksen alkamiskausi koodisto uri", required = true)
    private String koulutuksenAlkamiskausiUri;

    @ApiModelProperty(value = "Haun tila (LUONNOS, JULKAISTU, VALMIS, ...)", required = true)
    private String tila;

    @ApiModelProperty(value = "Ylioppilastutkinto antaa hakukelpoisuuden", required = false)
    private Boolean ylioppilastutkintoAntaaHakukelpoisuuden;

    @ApiModelProperty(value = "Lista hakukohteista joilla ylioppilastutkinto antaa hakukelpoisuuden")
    private List<String> hakukohdeOidsYlioppilastutkintoAntaaHakukelpoisuuden;

    @ApiModelProperty(value = "Käytetäänkö järjestelmän sijoittelupalvelua", required = true)
    private boolean sijoittelu;

    @ApiModelProperty(value = "Käytetäänkö järjestelmän hakulomaketta", required = true)
    private boolean jarjestelmanHakulomake;

    @ApiModelProperty(value = "Lista haun hakuaikoja", required = true)
    private List<HakuaikaV1RDTO> hakuaikas;

    @ApiModelProperty(value = "Haun hakukohdehteiden OID lista", required = true)
    private List<String> hakukohdeOids;

    @ApiModelProperty(value = "Haun tunniste", required = true)
    private String haunTunniste;

    @ApiModelProperty(value = "Haun monikielinen nimi", required = true)
    private Map<String, String> nimi = new HashMap<String, String>();

    @ApiModelProperty(value = "Koodiston avattua metadataa", required = false)
    private Map<String, KoodiV1RDTO> koodiMeta;

    @ApiModelProperty(value = "Maksimi hakukohteiden lukumäärä, ei rajoita tarjontaa vaan kouutusinformaatiossa käytössä", required = true)
    private int maxHakukohdes;

    @ApiModelProperty(value = "Tarjoaja organisatio oidit. Hakukohteita liittävät.", required = false)
    private String[] organisaatioOids;

    @ApiModelProperty(value = "Tarjoaja organisatio oidit. Muokkaajat.", required = false)
    private String[] tarjoajaOids;

    @ApiModelProperty(value = "Hakukohteet järjestettävä prioriteettijärjestykseen.", required = false)
    private boolean usePriority;

    @ApiModelProperty(value = "Isäntähaku, johon haku mahdollisesti linkittyy", required = false)
    private String parentHakuOid;

    @ApiModelProperty(value = "Sisältyvät haut", required = false)
    private Set<String> sisaltyvatHaut = new HashSet<String>();

    @ApiModelProperty(value = "Hakuun liittyvät organisaatioryhmät", required = false)
    private List<String> organisaatioryhmat;

    @ApiModelProperty(value = "Haun koulutusmoduulin tyyppi")
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;

    @ApiModelProperty(value = "Mihin asti haku näytetään opintopolussa", required = false)
    private Date opintopolunNayttaminenLoppuu;

    @ApiModelProperty(value = "Maksumuuri käytössä", required = false)
    private boolean maksumuuriKaytossa = false;

    @ApiModelProperty(value = "Onko yhden paikan sääntö voimassa haulle ja miksi", required = true)
    private YhdenPaikanSaanto yhdenPaikanSaanto;

    public void addKoodiMeta(KoodiV1RDTO koodi) {
        if (koodi == null) {
            return;
        }

        if (getKoodiMeta() == null) {
            setKoodiMeta(new HashMap<String, KoodiV1RDTO>());
        }
        getKoodiMeta().put(koodi.getUri(), koodi);
    }

    public Map<String, KoodiV1RDTO> getKoodiMeta() {
        return koodiMeta;
    }

    public void setKoodiMeta(Map<String, KoodiV1RDTO> koodiMeta) {
        this.koodiMeta = koodiMeta;
    }

    public String getHakukausiUri() {
        return hakukausiUri;
    }

    public void setHakukausiUri(String hakukausiUri) {
        this.hakukausiUri = hakukausiUri;
    }

    public String getHakutapaUri() {
        return hakutapaUri;
    }

    public void setHakutapaUri(String hakutapaUri) {
        this.hakutapaUri = hakutapaUri;
    }

    public String getHakulomakeUri() {
        return hakulomakeUri;
    }

    public void setHakulomakeUri(String hakulomakeUri) {
        this.hakulomakeUri = hakulomakeUri;
    }

    public String getHakutyyppiUri() {
        return hakutyyppiUri;
    }

    public void setHakutyyppiUri(String hakutyyppiUri) {
        this.hakutyyppiUri = hakutyyppiUri;
    }

    public String getKohdejoukkoUri() {
        return kohdejoukkoUri;
    }

    public void setKohdejoukkoUri(String kohdejoukkoUri) {
        this.kohdejoukkoUri = kohdejoukkoUri;
    }

    public int getKoulutuksenAlkamisVuosi() {
        return koulutuksenAlkamisVuosi;
    }

    public void setKoulutuksenAlkamisVuosi(int koulutuksenAlkamisVuosi) {
        this.koulutuksenAlkamisVuosi = koulutuksenAlkamisVuosi;
    }

    public String getKoulutuksenAlkamiskausiUri() {
        return koulutuksenAlkamiskausiUri;
    }

    public void setKoulutuksenAlkamiskausiUri(String koulutuksenAlkamiskausiUri) {
        this.koulutuksenAlkamiskausiUri = koulutuksenAlkamiskausiUri;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    public boolean isSijoittelu() {
        return sijoittelu;
    }

    public void setSijoittelu(boolean sijoittelu) {
        this.sijoittelu = sijoittelu;
    }

    public List<HakuaikaV1RDTO> getHakuaikas() {
        if (hakuaikas == null) {
            hakuaikas = new ArrayList<HakuaikaV1RDTO>();
        }

        return hakuaikas;
    }

    public void setHakuaikas(List<HakuaikaV1RDTO> hakuaikas) {
        this.hakuaikas = hakuaikas;
    }

    public List<String> getHakukohdeOids() {
        if (hakukohdeOids == null) {
            hakukohdeOids = new ArrayList<String>();
        }
        return hakukohdeOids;
    }

    public void setHakukohdeOids(List<String> hakukohdeOids) {
        this.hakukohdeOids = hakukohdeOids;
    }

    public String getHaunTunniste() {
        return haunTunniste;
    }

    public void setHaunTunniste(String haunTunniste) {
        this.haunTunniste = haunTunniste;
    }

    public int getHakukausiVuosi() {
        return hakukausiVuosi;
    }

    public void setHakukausiVuosi(int hakukausiVuosi) {
        this.hakukausiVuosi = hakukausiVuosi;
    }

    public void setNimi(Map<String, String> nimi) {
        this.nimi = nimi;
    }

    public Map<String, String> getNimi() {
        return nimi;
    }

    public int getMaxHakukohdes() {
        return maxHakukohdes;
    }

    public void setMaxHakukohdes(int maxHakukohdes) {
        this.maxHakukohdes = maxHakukohdes;
    }

    public String[] getOrganisaatioOids() {
        return organisaatioOids;
    }

    public void setOrganisaatioOids(String[] organisaatioOids) {
        this.organisaatioOids = organisaatioOids;
    }

    public String[] getTarjoajaOids() {
        return tarjoajaOids;
    }

    public void setTarjoajaOids(String[] tarjoajaOids) {
        this.tarjoajaOids = tarjoajaOids;
    }

    public boolean isUsePriority() {
        return usePriority;
    }

    public void setUsePriority(boolean usePriority) {
        this.usePriority = usePriority;
    }

    public boolean isJarjestelmanHakulomake() {
        return jarjestelmanHakulomake;
    }

    public void setJarjestelmanHakulomake(boolean jarjestelmanHakulomake) {
        this.jarjestelmanHakulomake = jarjestelmanHakulomake;
    }

    public Set<String> getSisaltyvatHaut() {
        return sisaltyvatHaut;
    }

    public void setSisaltyvatHaut(Set<String> sisaltyvatHaut) {
        this.sisaltyvatHaut = sisaltyvatHaut;
    }

    public String getParentHakuOid() {
        return parentHakuOid;
    }

    public void setParentHakuOid(String parentHakuOid) {
        this.parentHakuOid = parentHakuOid;
    }

    public List<String> getOrganisaatioryhmat() {
        return organisaatioryhmat;
    }

    public void setOrganisaatioryhmat(List<String> organisaatioryhmat) {
        this.organisaatioryhmat = organisaatioryhmat;
    }

    public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }

    public Boolean getYlioppilastutkintoAntaaHakukelpoisuuden() {
        return ylioppilastutkintoAntaaHakukelpoisuuden;
    }

    public void setYlioppilastutkintoAntaaHakukelpoisuuden(Boolean ylioppilastutkintoAntaaHakukelpoisuuden) {
        this.ylioppilastutkintoAntaaHakukelpoisuuden = ylioppilastutkintoAntaaHakukelpoisuuden;
    }

    public List<String> getHakukohdeOidsYlioppilastutkintoAntaaHakukelpoisuuden() {
        return hakukohdeOidsYlioppilastutkintoAntaaHakukelpoisuuden;
    }

    public void setHakukohdeOidsYlioppilastutkintoAntaaHakukelpoisuuden(List<String> hakukohdeOidsYlioppilastutkintoAntaaHakukelpoisuuden) {
        this.hakukohdeOidsYlioppilastutkintoAntaaHakukelpoisuuden = hakukohdeOidsYlioppilastutkintoAntaaHakukelpoisuuden;
    }

    public Date getOpintopolunNayttaminenLoppuu() {
        return opintopolunNayttaminenLoppuu;
    }

    public void setOpintopolunNayttaminenLoppuu(Date opintopolunNayttaminenLoppuu) {
        this.opintopolunNayttaminenLoppuu = opintopolunNayttaminenLoppuu;
    }

    public String getKohdejoukonTarkenne() {
        return kohdejoukonTarkenne;
    }

    public void setKohdejoukonTarkenne(String kohdejoukonTarkenne) {
        this.kohdejoukonTarkenne = kohdejoukonTarkenne;
    }

    public boolean isMaksumuuriKaytossa() {
        return isKorkeakouluHaku()
                && KoulutusmoduuliTyyppi.TUTKINTO.equals(getKoulutusmoduuliTyyppi())
                && StringUtils.isEmpty(getKohdejoukonTarkenne())
                && (
                    getKoulutuksenAlkamisVuosi() > 2016
                    || (
                        getKoulutuksenAlkamisVuosi() == 2016
                        && StringUtils.defaultString(getKoulutuksenAlkamiskausiUri()).startsWith("kausi_s#")
                    )
                );
    }

    private boolean isKorkeakouluHaku() {
        return StringUtils.defaultString(getKohdejoukkoUri()).startsWith("haunkohdejoukko_12#");
    }

    public YhdenPaikanSaanto  getYhdenPaikanSaanto() {
        return YhdenPaikanSaanto.from(this);
    }

    @ApiModel(value = "Yhden paikan säännön voimassaolotieto haulle")
    public static class YhdenPaikanSaanto {
        @ApiModelProperty(value = "Yhden paikan sääntö voimassa", required = true)
        private boolean voimassa;
        @ApiModelProperty(value = "Yhden paikan säännön perustelu", required = true)
        private String syy;

        private static final String JATKOTUTKINTOHAKU_URI = "haunkohdejoukontarkenne_3#";
        private static final List<String> TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO = Collections.singletonList(JATKOTUTKINTOHAKU_URI);

        public static YhdenPaikanSaanto from(HakuV1RDTO haku) {
            if (!haku.isKorkeakouluHaku()) {
                return new YhdenPaikanSaanto(false, "Ei korkeakouluhaku");
            }
            String haunKohdeJoukonTarkenne = haku.getKohdejoukonTarkenne();
            if (StringUtils.isBlank(haunKohdeJoukonTarkenne)) {
                return new YhdenPaikanSaanto(true, "Korkeakouluhaku ilman kohdejoukon tarkennetta");
            }
            for (String tarkenne : TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO) {
                if (haunKohdeJoukonTarkenne.startsWith(tarkenne)) {
                    return new YhdenPaikanSaanto(true, String.format("Kohdejoukon tarkenne on '%s'", haunKohdeJoukonTarkenne));
                }
            }
            return new YhdenPaikanSaanto(false, String.format("Kohdejoukon tarkenne on '%s', sääntö on voimassa tarkenteille %s",
                haunKohdeJoukonTarkenne, TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO));
        }

        public YhdenPaikanSaanto() {
        }

        private YhdenPaikanSaanto(boolean voimassa, String syy) {
            this.voimassa = voimassa;
            this.syy = syy;
        }

        public boolean isVoimassa() {
            return voimassa;
        }

        public void setVoimassa(boolean voimassa) {
            this.voimassa = voimassa;
        }

        public String getSyy() {
            return syy;
        }

        public void setSyy(String syy) {
            this.syy = syy;
        }
    }
}
