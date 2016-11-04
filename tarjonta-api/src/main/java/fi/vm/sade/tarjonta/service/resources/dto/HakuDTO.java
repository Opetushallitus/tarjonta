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
package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Haku as REST DTO.
 *
 * @author mlyly
 */
public class HakuDTO extends BaseRDTO {

    private String _hakukausiUri;
    private int _hakukausiVuosi;
    private String _hakulomakeUrl;
    private String _hakutapaUri;
    private String _hakutyyppiUri;
    private String _haunTunniste;
    private String _kohdejoukkoUri;
    private int _koulutuksenAlkamisVuosi;
    private String _koulutuksenAlkamiskausiUri;
    private String _tila;
    private boolean _sijoittelu;
    private int maxHakukohdes = 0;
    private boolean tunnistusKaytossa;

    private List<HakuaikaRDTO> _hakuaikas;

    private Map<String, String> _nimi;

    public void addNimi(String kieli, String v) {
        getNimi().put(kieli, v);
    }


    // Getters and setters

    public String getHakukausiUri() {
        return _hakukausiUri;
    }

    public void setHakukausiUri(String hakukausiUri) {
        this._hakukausiUri = hakukausiUri;
    }

    public int getHakukausiVuosi() {
        return _hakukausiVuosi;
    }

    public void setHakukausiVuosi(int hakukausiVuosi) {
        this._hakukausiVuosi = hakukausiVuosi;
    }

    public String getHakulomakeUrl() {
        return _hakulomakeUrl;
    }

    public void setHakulomakeUrl(String hakulomakeUrl) {
        this._hakulomakeUrl = hakulomakeUrl;
    }

    public String getHakutapaUri() {
        return _hakutapaUri;
    }

    public void setHakutapaUri(String hakutapaUri) {
        this._hakutapaUri = hakutapaUri;
    }

    public String getHakutyyppiUri() {
        return _hakutyyppiUri;
    }

    public void setHakutyyppiUri(String hakutyyppiUri) {
        this._hakutyyppiUri = hakutyyppiUri;
    }

    public String getHaunTunniste() {
        return _haunTunniste;
    }

    public void setHaunTunniste(String haunTunniste) {
        this._haunTunniste = haunTunniste;
    }

    public String getKohdejoukkoUri() {
        return _kohdejoukkoUri;
    }

    public void setKohdejoukkoUri(String kohdejoukkoUri) {
        this._kohdejoukkoUri = kohdejoukkoUri;
    }

    public int getKoulutuksenAlkamisVuosi() {
        return _koulutuksenAlkamisVuosi;
    }

    public void setKoulutuksenAlkamisVuosi(int koulutuksenAlkamisVuosi) {
        this._koulutuksenAlkamisVuosi = koulutuksenAlkamisVuosi;
    }

    public String getKoulutuksenAlkamiskausiUri() {
        return _koulutuksenAlkamiskausiUri;
    }

    public void setKoulutuksenAlkamiskausiUri(String koulutuksenAlkamiskausiUri) {
        this._koulutuksenAlkamiskausiUri = koulutuksenAlkamiskausiUri;
    }

    public String getTila() {
        return _tila;
    }

    public void setTila(String tila) {
        this._tila = tila;
    }

    public Map<String, String> getNimi() {
        if (_nimi == null) {
            _nimi = new HashMap<String, String>();
        }
        return _nimi;
    }

    public void setNimi(Map<String, String> _nimi) {
        this._nimi = _nimi;
    }

    public void setSijoittelu(boolean _sijoittelu) {
        this._sijoittelu = _sijoittelu;
    }

    public boolean isSijoittelu() {
        return _sijoittelu;
    }

    public void setTunnistusKaytossa(boolean tunnistusKaytossa) {
        this.tunnistusKaytossa = tunnistusKaytossa;
    }

    public boolean isTunnistusKaytossa() {
        return tunnistusKaytossa;
    }

    public List<HakuaikaRDTO> getHakuaikas() {
        if (_hakuaikas == null) {
            _hakuaikas = new ArrayList<HakuaikaRDTO>();
        }
        return _hakuaikas;
    }

    public void setHakuaikas(List<HakuaikaRDTO> _hakuaikas) {
        this._hakuaikas = _hakuaikas;
    }

    public int getMaxHakukohdes() {
        return maxHakukohdes;
    }

    public void setMaxHakukohdes(int maxHakukohdes) {
        this.maxHakukohdes = maxHakukohdes;
    }

}
