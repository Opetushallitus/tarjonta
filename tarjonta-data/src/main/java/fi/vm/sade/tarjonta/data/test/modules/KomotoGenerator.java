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
package fi.vm.sade.tarjonta.data.test.modules;

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.data.util.KoodistoURIHelper;
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
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
@Configurable(preConstruction = false)
public class KomotoGenerator extends AbstractGenerator {

    private static final String KOULUTUSASTE_AMMATTILLINEN_KOULUTUS = "32";
    private static final String OID_TYPE = "KOMOTO_";
    private static final Date DATE = new DateTime(2013, 1, 1, 1, 1).toDate();
    //private static final Random random = new Random(System.currentTimeMillis());
    private TarjontaAdminService tarjontaAdminService;
    private List< Map.Entry<String, String>> komoPairs = new ArrayList< Map.Entry<String, String>>();
    private int komoIndex = 0;

    public KomotoGenerator() {
        super(OID_TYPE);
    }

    public KomotoGenerator(TarjontaAdminService tarjontaAdminService, TarjontaPublicService tarjontaPublicService) {
        super(OID_TYPE);
        this.tarjontaAdminService = tarjontaAdminService;

        HaeKoulutusmoduulitKyselyTyyppi tyyppi = new HaeKoulutusmoduulitKyselyTyyppi();
        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        HaeKoulutusmoduulitVastausTyyppi haeKoulutusmoduulit = tarjontaPublicService.haeKoulutusmoduulit(tyyppi);
        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = haeKoulutusmoduulit.getKoulutusmoduuliTulos();

        //create all possible komo combinations
        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            addKomoPair(tulos.getKoulutusmoduuli());
        }
    }

    public String create(final String organisationOid) {
        Preconditions.checkNotNull(organisationOid, "Organisation OID cannot be null.");
        final LisaaKoulutusTyyppi createToteutus = createToteutus(organisationOid);
        tarjontaAdminService.lisaaKoulutus(createToteutus(organisationOid));
        return createToteutus.getOid();
    }

    private LisaaKoulutusTyyppi createToteutus(String organisationOid) {
        if (komoIndex >= komoPairs.size()) {
            komoIndex = 0;
        }

        LisaaKoulutusTyyppi tyyppi = new LisaaKoulutusTyyppi();
        Map.Entry<String, String> komoPair = komoPairs.get(komoIndex);
        tyyppi.setKoulutusKoodi(simpleKoodistoTyyppi(komoPair.getKey()));
        tyyppi.setKoulutusohjelmaKoodi(simpleKoodistoTyyppi(komoPair.getValue()));
        komoIndex++;

        tyyppi.setTila(TarjontaTila.JULKAISTU);
        tyyppi.setTarjoaja(organisationOid);
        tyyppi.setOid(generateOid());
        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        tyyppi.setKoulutusaste(KoodistoUtil.toKoodistoTyyppi(KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI, KOULUTUSASTE_AMMATTILLINEN_KOULUTUS));
        tyyppi.setKoulutuksenAlkamisPaiva(DATE);
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo("999");
        koulutuksenKestoTyyppi.setYksikko(KoodistoUtil.toKoodiUri(KoodistoURIHelper.KOODISTO_SUUNNITELTU_KESTO_URI, "1"));
        tyyppi.setPainotus(new MonikielinenTekstiTyyppi());
        tyyppi.setKesto(koulutuksenKestoTyyppi);
        tyyppi.setPohjakoulutusvaatimus(KoodistoUtil.toKoodistoTyyppi(KoodistoURIHelper.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI, "er"));

        tyyppi.getOpetusmuoto().add(KoodistoUtil.toKoodistoTyyppi(KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI, "im"));
        tyyppi.getOpetuskieli().add(KoodistoUtil.toKoodistoTyyppi(KoodistoURIHelper.KOODISTO_KIELI_URI, "FI"));
        tyyppi.getKoulutuslaji().add(KoodistoUtil.toKoodistoTyyppi(KoodistoURIHelper.KOODISTO_KOULUTUSLAJI_URI, "n"));

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
}
