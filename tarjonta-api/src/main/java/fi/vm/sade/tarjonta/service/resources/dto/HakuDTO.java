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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mlyly
 */
public class HakuDTO implements Serializable {

    private String _oid;
    private int _version;

    private String _hakukausiUri;
    private int _hakukausiVuosi;
    private String _hakulomakeUrl;
    private String _hakutapaUri;
    private String _hakutyyppiUri;
    private String _haunTunniste;
    private String _kohdejoukkoUri;
    private int _koulutuksenAlkamisVuosi;
    private String _koulutuksenAlkamiskausiUri;
    private String _udatedByOid;
    private Date _updated;
    private String _tila;
    private boolean _sijoittelu;

    private Map<String, String> _nimi;

    public void addNimi(String kieli, String v) {
        getNimi().put(kieli, v);
    }


    // Getters and setters

    public String getOid() {
        return _oid;
    }

    public void setOid(String _oid) {
        this._oid = _oid;
    }

    public int getVersion() {
        return _version;
    }

    public void setVersion(int _version) {
        this._version = _version;
    }

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

    public String getUdatedByOid() {
        return _udatedByOid;
    }

    public void setUdatedByOid(String udatedByOid) {
        this._udatedByOid = udatedByOid;
    }

    public Date getUpdated() {
        return _updated;
    }

    public void setUpdated(Date updated) {
        this._updated = updated;
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

}
