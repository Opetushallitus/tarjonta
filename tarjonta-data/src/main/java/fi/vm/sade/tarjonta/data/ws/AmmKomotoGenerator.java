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
package fi.vm.sade.tarjonta.data.ws;

import com.google.common.base.Preconditions;
import static fi.vm.sade.tarjonta.data.ws.AbstractGenerator.UPDATED_BY_USER;
import fi.vm.sade.tarjonta.data.util.KoodistoUtil;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import java.util.AbstractMap;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
@Configurable(preConstruction = false)
public class AmmKomotoGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(AmmKomotoGenerator.class);
    private static final String KOULUTUSASTE_AMMATTILLINEN_KOULUTUS = "32";
    private static final String OID_TYPE = "LOI_";
    private static final Date DATE = new DateTime(2013, 1, 1, 1, 1).toDate();
    //private static final Random random = new Random(System.currentTimeMillis());
    private TarjontaAdminService tarjontaAdminService;
    private List< Map.Entry<String, String>> komoPairs = new ArrayList< Map.Entry<String, String>>();
    private int komoIndex = 0;
    private String threadName;

    public AmmKomotoGenerator() {
        super(OID_TYPE);
    }

    public AmmKomotoGenerator(String threadName, TarjontaAdminService tarjontaAdminService, TarjontaPublicService tarjontaPublicService) {
        super(OID_TYPE);
        this.tarjontaAdminService = tarjontaAdminService;
        this.threadName = threadName;

        HaeKoulutusmoduulitKyselyTyyppi tyyppi = new HaeKoulutusmoduulitKyselyTyyppi();
        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        HaeKoulutusmoduulitVastausTyyppi haeKoulutusmoduulit = tarjontaPublicService.haeKoulutusmoduulit(tyyppi);
        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = haeKoulutusmoduulit.getKoulutusmoduuliTulos();

        if (koulutusmoduuliTulos.isEmpty()) {
            throw new RuntimeException("No KOMOS, you need to create base KOMO data to koulutusmoduuli table.");
        }

        //create all possible komo combinations
        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            addKomoPair(tulos.getKoulutusmoduuli());
        }
    }

    public String create(final String organisationOid) {
        Preconditions.checkNotNull(organisationOid, "Organisation OID cannot be null.");
        final LisaaKoulutusTyyppi createToteutus = createToteutus(organisationOid);
        tarjontaAdminService.lisaaKoulutus(createToteutus);
        LOG.info("{} created by thread {}.", createToteutus.getOid(), threadName);
        return createToteutus.getOid();
    }

    private LisaaKoulutusTyyppi createToteutus(final String organisationOid) {

        if (komoIndex >= komoPairs.size()) {
            komoIndex = 0;
        }

        LisaaKoulutusTyyppi tyyppi = new LisaaKoulutusTyyppi();
        Map.Entry<String, String> komoPair = getNextKomoPair();
        tyyppi.setKoulutusKoodi(simpleKoodistoTyyppi(komoPair.getKey()));
        tyyppi.setKoulutusohjelmaKoodi(simpleKoodistoTyyppi(komoPair.getValue()));

        tyyppi.setTila(TarjontaTila.JULKAISTU);
        tyyppi.setTarjoaja(organisationOid);
        tyyppi.setOid(generateOid());

        LOG.debug("Insert komoto with OID {}, komoto pair {}", tyyppi.getOid(), komoPair);

        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        tyyppi.setKoulutusaste(KoodistoUtil.toKoodistoTyyppi(KoodistoURI.KOODISTO_KOULUTUSASTE_URI, KOULUTUSASTE_AMMATTILLINEN_KOULUTUS));
        tyyppi.setKoulutuksenAlkamisPaiva(DATE);
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo("999");
       // koulutuksenKestoTyyppi.setYksikko(KoodistoUtil.toKoodiUri(KoodistoURIHelper.KOODISTO_SUUNNITELTU_KESTO_URI, "02"));
        //FIXME tyyppi.setPainotus(new MonikielinenTekstiTyyppi());
        tyyppi.setKesto(koulutuksenKestoTyyppi);
        tyyppi.setPohjakoulutusvaatimus(KoodistoUtil.toKoodistoTyyppi(KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI, "er"));

        tyyppi.getOpetusmuoto().add(KoodistoUtil.toKoodistoTyyppi(KoodistoURI.KOODISTO_OPETUSMUOTO_URI, "im"));
        tyyppi.getOpetuskieli().add(KoodistoUtil.toKoodistoTyyppi(KoodistoURI.KOODISTO_KIELI_URI, LANGUAGE_FI));
        tyyppi.getKoulutuslaji().add(KoodistoUtil.toKoodistoTyyppi(KoodistoURI.KOODISTO_KOULUTUSLAJI_URI, "n"));

        /*
        FIXME
        tyyppi.setKansainvalistyminen(createKoodiUriLorem());
        tyyppi.setKoulutusohjelmanValinta(createKoodiUriLorem());
        tyyppi.setKuvailevatTiedot(createKoodiUriLorem());
        tyyppi.setSijoittuminenTyoelamaan(createKoodiUriLorem());
        tyyppi.setYhteistyoMuidenToimijoidenKanssa(createKoodiUriLorem());
        tyyppi.setSisalto(createKoodiUriLorem());
        */

        tyyppi.setViimeisinPaivittajaOid(UPDATED_BY_USER);
        // tyyppi.setViimeisinPaivitysPvm(UPDATED_DATE);

        KoodistoUtil.addToKoodiToKoodistoTyyppi(KoodistoURI.KOODISTO_AMMATTINIMIKKEET_URI, new String[]{"1", "2", "3", "4", "5", "6"}, tyyppi.getAmmattinimikkeet());

        return tyyppi;
    }

    private static KoodistoKoodiTyyppi simpleKoodistoTyyppi(String uri) {
        KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(uri);
        koodi.setArvo(null);

        return koodi;
    }

    public void addKomoPair(KoulutusmoduuliKoosteTyyppi komo) {

        final String koulutusohjelmakoodiUri = komo.getKoulutusohjelmakoodiUri();
        if (koulutusohjelmakoodiUri == null) {
            return;
        }

        Map.Entry e = new AbstractMap.SimpleEntry<String, String>(komo.getKoulutuskoodiUri(), koulutusohjelmakoodiUri);
        komoPairs.add(e);
    }

    private Map.Entry<String, String> getNextKomoPair() {
        Map.Entry<String, String> komoPair = komoPairs.get(komoIndex);
        komoIndex++;

        return komoPair;
    }
}
