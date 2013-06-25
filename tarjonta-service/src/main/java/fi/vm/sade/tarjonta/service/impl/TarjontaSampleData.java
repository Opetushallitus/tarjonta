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
package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Alustaa tarjontaan esimerkki dataa. Poistetaan kun Koodistosta saadaan tarvittava data.
 */
@Component
public class TarjontaSampleData {

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    private Random random = new Random(System.currentTimeMillis());

    public void init() {

        Koulutusmoduuli moduuli;
        KoulutusmoduuliToteutus toteutus;

        //
        // Moduuli KM#1
        //
        // Artesaani, käsi- ja taideteollisuusalan perustutkinto
        // Ympäristön suunnittelun ja rakentamisen koulutusohjelma (onko tämä oikea ko ??)
        //
        moduuli = createKoulutusmoduuli();
        moduuli.setKoulutusKoodi("uri: Muu koulutus 20898");
        moduuli.setKoulutusohjelmaKoodi("uri: Tohtorintutkinto 20969");
        moduuli.setNimi(createTeksti("Muu koulutus, Tohtorintutkinto (fi)",
            "Muu koulutus, Tohtorintutkinto (sv)",
            "Muu koulutus, Tohtorintutkinto (en)"));

        koulutusmoduuliDAO.insert(moduuli);

        //
        // Toteutus KM#1
        //
        toteutus = createKoulutusmoduuliToteutus();
        toteutus.setKoulutusmoduuli(moduuli);
        toteutus.setTarjoaja("1.2.246.562.5.73962414165"); //Kontulan peruskoulu luokalla
        toteutus = koulutusmoduuliToteutusDAO.insert(toteutus);


        //
        // Moduuli KM#2
        //
        //
        moduuli = createKoulutusmoduuli();
        moduuli.setKoulutusKoodi("uri: Kulttuuriala 20902");
        moduuli.setKoulutusohjelmaKoodi("uri: Ylempi korkeakoulututkinto 20973");
        moduuli.setNimi(createTeksti("Kulttuuriala, Ylempi korkeakoulututkinto",
            "Kulttuuriala, Ylempi korkeakoulututkinto (sv)",
            "Kulttuuriala, Ylempi korkeakoulututkinto (en)"));

        koulutusmoduuliDAO.insert(moduuli);

        //
        // Toteutus KM#2
        //
        toteutus = createKoulutusmoduuliToteutus();
        toteutus.setKoulutusmoduuli(moduuli);
        toteutus.setTarjoaja("1.2.246.562.5.58521633737");//Markuksen koulu luokalla
        toteutus = koulutusmoduuliToteutusDAO.insert(toteutus);


        //
        // Yhteishaku
        //
        Haku haku = createHaku("123", "Yhteishaku");
        hakuDAO.insert(haku);


        //
        // Hakukohde jossa yksi koulutus
        //
        Hakukohde hakukohde = createHakukohde("Artesaani, käsi- ja taideteollisuusalan perustutkinto");
        hakukohde.setHakukohdeKoodistoNimi("Artesaani, käsi- ja taideteollisuusalan perustutkinto");
        hakukohde.setHaku(haku);
        
        hakukohde.addKoulutusmoduuliToteutus(toteutus);
        hakukohde = hakukohdeDAO.insert(hakukohde);

        toteutus.addHakukohde(hakukohde);
        koulutusmoduuliToteutusDAO.update(toteutus);

    }

    private Haku createHaku(String tunniste, String nimi) {

        Haku h = new Haku();
        h.setHakukausiUri(randomKoodiUri("koulutuskausi"));
        h.setHakukausiVuosi(2013);
        h.setHakutapaUri(randomKoodiUri("hakutapa"));
        h.setHakutyyppiUri(randomKoodiUri("hakutyyppi"));
        h.setHaunTunniste(tunniste);
        h.setKohdejoukkoUri(randomKoodiUri("haunkohdejoukko"));
        h.setKoulutuksenAlkamisVuosi(2014);
        h.setKoulutuksenAlkamiskausiUri(randomKoodiUri("koulutuskausi"));
        h.setNimiFi(nimi);
        h.setOid(randomOid("haku"));
        h.setSijoittelu(true);
        h.setTila(TarjontaTila.JULKAISTU);

        return h;

    }

    private Hakukohde createHakukohde(String nimi) {

        Hakukohde h = new Hakukohde();
        h.setAlinValintaPistemaara(30);
        h.setAloituspaikatLkm(100);
        h.setEdellisenVuodenHakijat(1200);
        h.setHakukelpoisuusvaatimus(randomKoodiUri("koulutustaso"));
        //h.setValintaperusteKuvaus(createTeksti("Valintaperustekuvaus...", null, "Selection criterion..."));
        h.setHakukohdeNimi(nimi);
        h.setOid(randomOid("hakukohde"));
        h.setYlinValintaPistemaara(90);
        h.setTila(TarjontaTila.JULKAISTU);

        // Hakukohteella Valintakoe

        Valintakoe v1 = new Valintakoe();
        v1.setTyyppiUri(randomKoodiUri("valintakoe"));
        v1.setKuvaus(createTeksti("Haastattelu", null, "Interview"));

        // Valintakokeella 1 ajankohta

        ValintakoeAjankohta a1 = new ValintakoeAjankohta();
        a1.setAlkamisaika(date(30));
        a1.setPaattymisaika(date(31));

        // Ajankohdalla 1 osoite




        v1.addAjankohta(a1);

        h.addValintakoe(v1);

        return h;

    }

    private Koulutusmoduuli createKoulutusmoduuli() {

        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setKoulutustyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA.name());
        m.setOid(randomOid("koulutusmoduuli"));
        m.setNimi(createTeksti("Nimi (fi)", "Nimi (sv)", "Nimi (en)"));
        m.setKoulutusKoodi(randomKoodiUri("koulutus"));
        m.setEqfLuokitus(randomKoodiUri("eqf"));
        m.setKoulutusAste(randomKoodiUri("koulutusaste"));
        m.setKoulutusala(randomKoodiUri("koulutusala"));
        m.setKoulutusohjelmaKoodi(randomKoodiUri("koulutusohjelma"));
        m.setNqfLuokitus(randomKoodiUri("ngf"));
        m.setOmistajaOrganisaatioOid(randomOid("organisaatio"));
        m.setTila(TarjontaTila.JULKAISTU);
        m.setNimi(createTeksti("Koulutus (fi)", "Koulutus (sv)", "Koulutus (en)"));
        m.setLaajuus(randomKoodiUri("opintojenlaajuus"), "20");
        m.setTutkintonimike(randomKoodiUri("tutkintonimike"));
        m.setKoulutuksenRakenne(createTeksti("Koulutuksen rakenne...", null, "Structure of education..."));
        m.setJatkoOpintoMahdollisuudet(createTeksti("Jatko-opintomahdollisuudet", null, "Access to further studies..."));
        m.setUlkoinenTunniste("KM5024623.4");

        return m;

    }

    private KoulutusmoduuliToteutus createKoulutusmoduuliToteutus() {

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();

        t.setKoulutuksenAlkamisPvm(date(30 * 6));
        t.addKoulutuslaji(randomKoodiUri("koulutuslaji"));
        t.setOid(randomOid("koulutusmoduulitoteutus"));
        t.setTarjoaja(randomOid("organisaatio"));
        t.setTila(TarjontaTila.JULKAISTU);
        t.addOpetuskieli(new KoodistoUri(randomKoodiUri("kieli")));
        t.addOpetusmuoto(new KoodistoUri(randomKoodiUri("opetusmuoto")));
        t.addAmmattinimike(new KoodistoUri(randomKoodiUri("ammattinimike")));
        t.addAmmattinimike(new KoodistoUri(randomKoodiUri("ammattinimike")));
        t.addAvainsana(new KoodistoUri(randomKoodiUri("avainsana")));
        t.addAvainsana(new KoodistoUri(randomKoodiUri("avainsana")));
        t.addAvainsana(new KoodistoUri(randomKoodiUri("avainsana")));
        t.setUlkoinenTunniste("KMT637832.3");
        t.setPohjakoulutusvaatimus(randomKoodiUri("pohjakoulutusvaatimus"));
        t.setSuunniteltuKesto(randomKoodiUri("koulutuskesto"), "6+2");
        t.setArviointikriteerit(createTeksti("Arviointikriteerit...", null, "Assessments..."));
        t.setLoppukoeVaatimukset(createTeksti("Loppukoevaatimukset", null, "Final Examination..."));
        t.addLinkki(createWebLinkki("kotisivut", null, "http://www.oppilaitosX.fi"));
        t.addLinkki(createWebLinkki("facebook", null, "http://www.facebook.com"));
        t.addLinkki(createWebLinkki("multimedia", "fi", "http://www.youtube.com?id=12345"));

        return t;

    }

    private static WebLinkki createWebLinkki(String tyyppi, String kieli, String url) {

        return new WebLinkki(tyyppi, kieli, url);

    }

    private Date date(int numberOfDaysInFuture) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, numberOfDaysInFuture);
        return cal.getTime();
    }

    private String randomKoodiUri(String namespace) {
        return "http://koodisto.oph.fi/" + namespace + "/" + Math.abs(random.nextLong());
    }

    private String randomOid(String namespace) {
        return "http://oid.oph.fi/" + namespace + "/" + Math.abs(random.nextLong());
    }

    private static MonikielinenTeksti createTeksti(String fiTeksti, String svTeskti, String enTeksti) {

        MonikielinenTeksti t = new MonikielinenTeksti();
        if (fiTeksti != null) {
            t.addTekstiKaannos("fi", fiTeksti);
        }
        if (enTeksti != null) {
            t.addTekstiKaannos("en", enTeksti);
        }
        if (svTeskti != null) {
            t.addTekstiKaannos("sv", svTeskti);
        }
        return t;

    }

}

