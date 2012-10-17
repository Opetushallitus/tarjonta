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
        moduuli.setKoulutusKoodi("http://koodisto.oph.fi/koulutus/321101");
        moduuli.setKoulutusohjelmaKoodi("http://koodisto.oph.fi/koulutusohjelma/1603");
        koulutusmoduuliDAO.insert(moduuli);

        //
        // Toteutus KM#1
        //
        toteutus = createKoulutusmoduuliToteutus();
        toteutus.setKoulutusmoduuli(moduuli);
        koulutusmoduuliToteutusDAO.insert(toteutus);


        //
        // Yhteishaku
        //
        Haku haku = createHaku("123", "Yhteishaku");
        hakuDAO.insert(haku);


        //
        // Hakukohde jossa yksi koulutus
        //
        Hakukohde hakukohde = createHakukohde("Artesaani, käsi- ja taideteollisuusalan perustutkinto");
        hakukohde.setHaku(haku);
        hakukohdeDAO.insert(hakukohde);

    }


    private Haku createHaku(String tunniste, String nimi) {

        Haku h = new Haku();
        h.setHakukausiUri(randomKoodiUri("koulutuskausi"));
        h.setHakukausiVuosi(2013);
        h.setHakutapaUri(randomKoodiUri("hakutapa"));
        h.setHakutyyppiUri(randomKoodiUri("hakutyyppi"));
        h.setHaunTunniste("123");
        h.setKohdejoukkoUri(randomKoodiUri("haunkohdejoukko"));
        h.setKoulutuksenAlkamisVuosi(2014);
        h.setKoulutuksenAlkamiskausiUri(randomKoodiUri("koulutuskausi"));
        h.setNimiFi(nimi);
        h.setOid(randomOid("haku"));
        h.setSijoittelu(true);
        h.setTila(KoodistoContract.TarjontaTilat.VALMIS);

        return h;

    }

    private Hakukohde createHakukohde(String nimi) {

        Hakukohde h = new Hakukohde();
        h.setAlinValintaPistemaara(30);
        h.setAloituspaikatLkm(100);
        h.setHakukelpoisuusvaatimus(randomKoodiUri("koulutustaso"));
        h.setHakukohdeNimi(nimi);
        h.setOid(randomOid("hakukohde"));
        h.setYlinValintaPistemaara(90);

        return h;

    }

    private Koulutusmoduuli createKoulutusmoduuli() {

        Koulutusmoduuli m = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        m.setOid(randomOid("koulutusmoduuli"));
        m.setKoulutusKoodi("Artesaani, käsi- ja taideteollisuusalan perustutkinto 19369");
        m.setEqfLuokitus(randomKoodiUri("eqf"));
        m.setKoulutusAste(randomKoodiUri("koulutusaste"));
        m.setKoulutusala(randomKoodiUri("koulutusala"));
        m.setKoulutusohjelmaKoodi("koulutusohjelma/1603");
        m.setNqfLuokitus(randomKoodiUri("ngf"));
        m.setOmistajaOrganisaatioOid(randomOid("organisaatio"));
        m.setTila(KoodistoContract.TarjontaTilat.JULKAISTU);

        return m;

    }

    private KoulutusmoduuliToteutus createKoulutusmoduuliToteutus() {

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();

        t.setKoulutuksenAlkamisPvm(date(30 * 6));
        t.setKoulutusLaji(randomKoodiUri("koulutuslaji"));
        t.setOid(randomOid("koulutusmoduulitoteutus"));
        t.setTarjoaja(randomOid("organisaatio"));
        t.setTila(KoodistoContract.TarjontaTilat.JULKAISTU);
        t.addOpetuskieli(new KoodistoUri(randomKoodiUri("kieli")));
        t.addOpetusmuoto(new KoodistoUri(randomKoodiUri("opetusmuoto")));

        return t;

    }

    private Date date(int numberOfDaysInFuture) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, numberOfDaysInFuture);
        return cal.getTime();
    }

    private String randomKoodiUri(String namespace) {
        return "http://koodisto.oph.fi/" + namespace + "/" + random.nextLong();
    }

    private String randomOid(String namespace) {
        return "http://oid.oph.fi/" + namespace + "/" + random.nextLong();
    }

}

