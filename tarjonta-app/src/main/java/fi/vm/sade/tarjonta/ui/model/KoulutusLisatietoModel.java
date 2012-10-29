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
package fi.vm.sade.tarjonta.ui.model;

/**
 * Single language additional information for studies.
 *
 * @author mlyly
 */
public class KoulutusLisatietoModel extends BaseUIViewModel {

    // Language this information is given in
    String _languageUri;

    // Description
    String _kuvailevatTiedot;

    // Contents
    String _sisalto;

    // Worklife
    String _sijoittuminenTyoelamaan;

    // International
    String _kansainvalistyminen;

    // Co-operative work with others
    String _yhteistyoMuidenToimijoidenKanssa;

    public String getLanguageUri() {
        return _languageUri;
    }

    public void setLanguageUri(String _languageUri) {
        this._languageUri = _languageUri;
    }

    public String getKuvailevatTiedot() {
        return _kuvailevatTiedot;
    }

    public void setKuvailevatTiedot(String _kuvailevatTiedot) {
        this._kuvailevatTiedot = _kuvailevatTiedot;
    }

    public String getSisalto() {
        return _sisalto;
    }

    public void setSisalto(String _sisalto) {
        this._sisalto = _sisalto;
    }

    public String getSijoittuminenTyoelamaan() {
        return _sijoittuminenTyoelamaan;
    }

    public void setSijoittuminenTyoelamaan(String _sijoittuminenTyoelamaan) {
        this._sijoittuminenTyoelamaan = _sijoittuminenTyoelamaan;
    }

    public String getKansainvalistyminen() {
        return _kansainvalistyminen;
    }

    public void setKansainvalistyminen(String _kansainvalistyminen) {
        this._kansainvalistyminen = _kansainvalistyminen;
    }

    public String getYhteistyoMuidenToimijoidenKanssa() {
        return _yhteistyoMuidenToimijoidenKanssa;
    }

    public void setYhteistyoMuidenToimijoidenKanssa(String _yhteistyoMuidenToimijoidenKanssa) {
        this._yhteistyoMuidenToimijoidenKanssa = _yhteistyoMuidenToimijoidenKanssa;
    }
}
