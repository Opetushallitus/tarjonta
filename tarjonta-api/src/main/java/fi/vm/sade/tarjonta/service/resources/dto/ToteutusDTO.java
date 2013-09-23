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

package fi.vm.sade.tarjonta.service.resources.dto;

import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Jani Wilén
 */

public abstract class ToteutusDTO extends BaseRDTO {

    private static final long serialVersionUID = 1L;
    private String komoOid;
    private TarjontaTila tila;
    private UiListDTO koulutuskoodi;
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;

    private UiDTO koulutusaste;
    private UiDTO koulutusala;
    private UiDTO opintoala;
    private UiDTO tutkinto;
    private UiDTO tutkintonimike;
    private UiDTO eqf;
    private Map<KomoTeksti, UiListDTO> tekstis;

    public ToteutusDTO() {
    }
    
    
    
    /*
     * Contact persons
     */
    private Set<YhteyshenkiloTyyppi> yhteyshenkilos;

    /**
     * @return the tila
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    /**
     * @return the tekstis
     */
    public Map<KomoTeksti, UiListDTO> getTekstis() {
        if (tekstis == null) {
            tekstis = new EnumMap<KomoTeksti, UiListDTO>(KomoTeksti.class);
        }
        return tekstis;
    }

    /**
     * @param tekstis the tekstis to set
     */
    public void setTekstis(Map<KomoTeksti, UiListDTO> tekstis) {
        this.tekstis = tekstis;
    }

    /**
     * @return the komoOid
     */
    public String getKomoOid() {
        return komoOid;
    }

    /**
     * @param komoOid the komoOid to set
     */
    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    /**
     * @return the koulutusmoduuliTyyppi
     */
    public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    /**
     * @param koulutusmoduuliTyyppi the koulutusmoduuliTyyppi to set
     */
    public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }

    /**
     * @return the koulutuskoodi
     */
    public UiListDTO getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * @param koulutuskoodi the koulutuskoodi to set
     */
    public void setKoulutuskoodi(UiListDTO koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    /**
     * @return the koulutusaste
     */
    public UiDTO getKoulutusaste() {
        return koulutusaste;
    }

    /**
     * @param koulutusaste the koulutusaste to set
     */
    public void setKoulutusaste(UiDTO koulutusaste) {
        this.koulutusaste = koulutusaste;
    }

    /**
     * @return the koulutusala
     */
    public UiDTO getKoulutusala() {
        return koulutusala;
    }

    /**
     * @param koulutusala the koulutusala to set
     */
    public void setKoulutusala(UiDTO koulutusala) {
        this.koulutusala = koulutusala;
    }

    /**
     * @return the opintoala
     */
    public UiDTO getOpintoala() {
        return opintoala;
    }

    /**
     * @param opintoala the opintoala to set
     */
    public void setOpintoala(UiDTO opintoala) {
        this.opintoala = opintoala;
    }

    /**
     * @return the tutkinto
     */
    public UiDTO getTutkinto() {
        return tutkinto;
    }

    /**
     * @param tutkinto the tutkinto to set
     */
    public void setTutkinto(UiDTO tutkinto) {
        this.tutkinto = tutkinto;
    }

    /**
     * @return the tutkintonimike
     */
    public UiDTO getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(UiDTO tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }

    /**
     * @return the eqf
     */
    public UiDTO getEqf() {
        return eqf;
    }

    /**
     * @param eqf the eqf to set
     */
    public void setEqf(UiDTO eqf) {
        this.eqf = eqf;
    }

    /**
     * @return the yhteyshenkilos
     */
    public Set<YhteyshenkiloTyyppi> getYhteyshenkilos() {
        if (yhteyshenkilos == null) {
            yhteyshenkilos = new HashSet<YhteyshenkiloTyyppi>();
        }
        return yhteyshenkilos;
    }

    /**
     * @param yhteyshenkilos the yhteyshenkilos to set
     */
    public void setYhteyshenkilos(Set<YhteyshenkiloTyyppi> yhteyshenkilos) {
        this.yhteyshenkilos = yhteyshenkilos;
    }

 
}
