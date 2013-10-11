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

import java.util.*;

/*
* @author: Tuomas Katva 10/11/13
*/
public class HakukohdeRDTO  extends BaseRDTO {

    private String hakukohteenNimi;

    private List<TekstiRDTO> hakukohteenNimet;

    private Set<String> tarjoajaOids;

    private String hakuOid;

    private List<String> hakukelpoisuusvaatimusUris;

    private List<String> hakukohdeKoulutusOids;

    private double alinHyvaksyttavaKeskiarvo;

    private int alinValintaPistemaara;

    private int aloituspaikatLkm;

    private int edellisenVuodenHakijatLkm;

    private int valintojenAloituspaikatLkm;

    private int ylinValintapistemaara;

    private String sahkoinenToimitusOsoite;

    private String soraKuvausKoodiUri;

    private String tila;

    private String valintaperustekuvausKoodiUri;

    private Date liitteidenToimitusPvm;

    private List<TekstiRDTO> lisatiedot;

    private List<TekstiRDTO> valintaperusteKuvaukset;

    private boolean kaytetaanJarjestelmanValintaPalvelua;

    private boolean kaytetaanHaunPaattymisenAikaa;

    private boolean kaytetaanHakukohdekohtaistaHakuaikaa;

    private List<HakukohdeLiiteDTO> hakukohteenLiitteet;

    private List<ValintakoeRDTO> hakukohteenValintakokeet;

    private OsoiteRDTO liitteidenToimitusOsoite;

    private List<ValintakoeRDTO> valintakokeet;

    private List<HakukohdeLiiteDTO> liitteet;


    public String getHakukohteenNimi() {
        return hakukohteenNimi;
    }

    public void setHakukohteenNimi(String hakukohteenNimi) {
        this.hakukohteenNimi = hakukohteenNimi;
    }

    public List<TekstiRDTO> getHakukohteenNimet() {
        if (hakukohteenNimet == null) {
            hakukohteenNimet = new ArrayList<TekstiRDTO>();
        }
        return hakukohteenNimet;
    }

    public void setHakukohteenNimet(List<TekstiRDTO> hakukohteenNimet) {
        this.hakukohteenNimet = hakukohteenNimet;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public List<String> getHakukelpoisuusvaatimusUris() {
        if (hakukelpoisuusvaatimusUris == null) {
            hakukelpoisuusvaatimusUris = new ArrayList<String>();
        }

        return hakukelpoisuusvaatimusUris;
    }

    public void setHakukelpoisuusvaatimusUris(List<String> hakukelpoisuusvaatimusUris) {
        this.hakukelpoisuusvaatimusUris = hakukelpoisuusvaatimusUris;
    }

    public double getAlinHyvaksyttavaKeskiarvo() {
        return alinHyvaksyttavaKeskiarvo;
    }

    public void setAlinHyvaksyttavaKeskiarvo(double alinHyvaksyttavaKeskiarvo) {
        this.alinHyvaksyttavaKeskiarvo = alinHyvaksyttavaKeskiarvo;
    }

    public int getAlinValintaPistemaara() {
        return alinValintaPistemaara;
    }

    public void setAlinValintaPistemaara(int alinValintaPistemaara) {
        this.alinValintaPistemaara = alinValintaPistemaara;
    }

    public int getAloituspaikatLkm() {
        return aloituspaikatLkm;
    }

    public void setAloituspaikatLkm(int aloituspaikatLkm) {
        this.aloituspaikatLkm = aloituspaikatLkm;
    }

    public int getEdellisenVuodenHakijatLkm() {
        return edellisenVuodenHakijatLkm;
    }

    public void setEdellisenVuodenHakijatLkm(int edellisenVuodenHakijatLkm) {
        this.edellisenVuodenHakijatLkm = edellisenVuodenHakijatLkm;
    }

    public int getValintojenAloituspaikatLkm() {
        return valintojenAloituspaikatLkm;
    }

    public void setValintojenAloituspaikatLkm(int valintojenAloituspaikatLkm) {
        this.valintojenAloituspaikatLkm = valintojenAloituspaikatLkm;
    }

    public int getYlinValintapistemaara() {
        return ylinValintapistemaara;
    }

    public void setYlinValintapistemaara(int ylinValintapistemaara) {
        this.ylinValintapistemaara = ylinValintapistemaara;
    }

    public String getSahkoinenToimitusOsoite() {
        return sahkoinenToimitusOsoite;
    }

    public void setSahkoinenToimitusOsoite(String sahkoinenToimitusOsoite) {
        this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
    }

    public String getSoraKuvausKoodiUri() {
        return soraKuvausKoodiUri;
    }

    public void setSoraKuvausKoodiUri(String soraKuvausKoodiUri) {
        this.soraKuvausKoodiUri = soraKuvausKoodiUri;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    public String getValintaperustekuvausKoodiUri() {
        return valintaperustekuvausKoodiUri;
    }

    public void setValintaperustekuvausKoodiUri(String valintaperustekuvausKoodiUri) {
        this.valintaperustekuvausKoodiUri = valintaperustekuvausKoodiUri;
    }

    public Date getLiitteidenToimitusPvm() {
        return liitteidenToimitusPvm;
    }

    public void setLiitteidenToimitusPvm(Date liitteidenToimitusPvm) {
        this.liitteidenToimitusPvm = liitteidenToimitusPvm;
    }

    public List<TekstiRDTO> getLisatiedot() {

        if (lisatiedot == null) {
            lisatiedot = new ArrayList<TekstiRDTO>();
        }

        return lisatiedot;
    }

    public void setLisatiedot(List<TekstiRDTO> lisatiedot) {
        this.lisatiedot = lisatiedot;
    }

    public List<TekstiRDTO> getValintaperusteKuvaukset() {
        if (valintaperusteKuvaukset == null){
            valintaperusteKuvaukset = new ArrayList<TekstiRDTO>();
        }
        return valintaperusteKuvaukset;
    }

    public void setValintaperusteKuvaukset(List<TekstiRDTO> valintaperusteKuvaukset) {
        this.valintaperusteKuvaukset = valintaperusteKuvaukset;
    }

    public boolean isKaytetaanJarjestelmanValintaPalvelua() {
        return kaytetaanJarjestelmanValintaPalvelua;
    }

    public void setKaytetaanJarjestelmanValintaPalvelua(boolean kaytetaanJarjestelmanValintaPalvelua) {
        this.kaytetaanJarjestelmanValintaPalvelua = kaytetaanJarjestelmanValintaPalvelua;
    }

    public boolean isKaytetaanHaunPaattymisenAikaa() {
        return kaytetaanHaunPaattymisenAikaa;
    }

    public void setKaytetaanHaunPaattymisenAikaa(boolean kaytetaanHaunPaattymisenAikaa) {
        this.kaytetaanHaunPaattymisenAikaa = kaytetaanHaunPaattymisenAikaa;
    }

    public boolean isKaytetaanHakukohdekohtaistaHakuaikaa() {
        return kaytetaanHakukohdekohtaistaHakuaikaa;
    }

    public void setKaytetaanHakukohdekohtaistaHakuaikaa(boolean kaytetaanHakukohdekohtaistaHakuaikaa) {
        this.kaytetaanHakukohdekohtaistaHakuaikaa = kaytetaanHakukohdekohtaistaHakuaikaa;
    }

    public List<HakukohdeLiiteDTO> getHakukohteenLiitteet() {
        if (hakukohteenLiitteet == null) {
            hakukohteenLiitteet = new ArrayList<HakukohdeLiiteDTO>();
        }
        return hakukohteenLiitteet;
    }

    public void setHakukohteenLiitteet(List<HakukohdeLiiteDTO> hakukohteenLiitteet) {
        this.hakukohteenLiitteet = hakukohteenLiitteet;
    }

    public List<ValintakoeRDTO> getHakukohteenValintakokeet() {
        if (hakukohteenValintakokeet == null) {
            hakukohteenValintakokeet = new ArrayList<ValintakoeRDTO>();
        }
        return hakukohteenValintakokeet;
    }

    public void setHakukohteenValintakokeet(List<ValintakoeRDTO> hakukohteenValintakokeet) {
        this.hakukohteenValintakokeet = hakukohteenValintakokeet;
    }

    public OsoiteRDTO getLiitteidenToimitusOsoite() {
        return liitteidenToimitusOsoite;
    }

    public void setLiitteidenToimitusOsoite(OsoiteRDTO liitteidenToimitusOsoite) {
        this.liitteidenToimitusOsoite = liitteidenToimitusOsoite;
    }

    public List<String> getHakukohdeKoulutusOids() {
        if (hakukohdeKoulutusOids == null) {
            hakukohdeKoulutusOids = new ArrayList<String>();
        }
        return hakukohdeKoulutusOids;
    }

    public void setHakukohdeKoulutusOids(List<String> hakukohdeKoulutusOids) {
        this.hakukohdeKoulutusOids = hakukohdeKoulutusOids;
    }

    public Set<String> getTarjoajaOids() {
        if (tarjoajaOids == null) {
            tarjoajaOids = new HashSet<String>();
        }
        return tarjoajaOids;
    }

    public void setTarjoajaOids(Set<String> tarjoajaOids) {
        this.tarjoajaOids = tarjoajaOids;
    }

    public List<ValintakoeRDTO> getValintakokeet() {
        if (valintakokeet == null) {
            valintakokeet = new ArrayList<ValintakoeRDTO>();
        }
        return valintakokeet;
    }

    public void setValintakokeet(List<ValintakoeRDTO> valintakokeet) {
        this.valintakokeet = valintakokeet;
    }

    public List<HakukohdeLiiteDTO> getLiitteet() {
        return liitteet;
    }

    public void setLiitteet(List<HakukohdeLiiteDTO> liitteet) {
        this.liitteet = liitteet;
    }
}