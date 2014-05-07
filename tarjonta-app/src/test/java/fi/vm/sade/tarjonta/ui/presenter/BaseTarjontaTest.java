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
package fi.vm.sade.tarjonta.ui.presenter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.NimettyMonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.ui.helper.conversion.ConversionUtils;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.joda.time.DateTime;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Jani Wilén
 */
public class BaseTarjontaTest {

    protected static final String ORGANISATION_NAME = "organisation name";
    protected static final String WEB_LINK = "http://localhost:8080/";
    protected static final String LAAJUUS_ARVO = "laajuus_arvo";
    protected static final String LAAJUUS_YKSIKKO = "laajuus_tyyppi";
    protected static final String KOULUTUSALA = "koulutusala";
    protected static final String KOULUTUSKOODI = "koulutuskoodi";
    protected static final String KOULUTUSASTE = "koulutusaste";
    protected static final String TUTKINTONIMIKE = "tutkintonimike";
    protected static final String KOULUTUSLAJI = "koulutuslaji";
    protected static final String LUKIOLINJA = "lukiolinja";
    protected static final String OPINTOALA = "opintoala";
    protected static final String TUTKINNON_TAVOITTEET = "tutkinnon_tavoitteet";
    protected static final String JATKOOPINTOMAHDOLLISUUDET = "jatko-opintomahdollisuudet";
    protected static final String KOULUTUKSEN_RAKENNE = "koulutuksen_rakenne";
    protected static final int VERSION = 1;
    protected static final String KOMO_OID = "komo.234234.234.2.342.34";
    protected static final String KOMOTO_OID = "komoto.1321321.321.321";
    protected static final String LANGUAGE_FI = "fi";
    protected static final String TEXT = "text";
    protected static final String ORGANISAATIO_OID = "organisaatio.1.2.3.4.5.6.7";
    protected static final Date DATE = new DateTime(2013, 1, 1, 10, 12).toDate();

    protected void assertKuvailevatTiedot(KoulutusTyyppi koulutus) {
        assertEquals(1, koulutus.getLukiodiplomit().size());
        assertEquals(2, koulutus.getMuutKielet().size());
        assertEquals("A1 A2", 3, koulutus.getA1A2Kieli().size());
        assertEquals(0, koulutus.getAmmattinimikkeet().size());
        assertEquals("B1", 4, koulutus.getB1Kieli().size());
        assertEquals("B2", 5, koulutus.getB2Kieli().size());
        assertEquals("B3", 6, koulutus.getB3Kieli().size());
    }

    /*
     * Helper methods
     */
    protected MonikielinenTekstiModel createMonikielinenTeksti(String koodiUri) {
        MonikielinenTekstiModel mtm = new MonikielinenTekstiModel();
        mtm.getKielikaannos().add(new KielikaannosViewModel(LANGUAGE_FI, TEXT));
        createKoodiModel(mtm, koodiUri);

        return mtm;
    }

    protected KoodiModel createKoodiModel(String koodiUri) {
        KoodiModel koodiModel = new KoodiModel();
        createKoodiModel(koodiModel, koodiUri);
        return koodiModel;
    }

    protected KoulutuskoodiModel createKoulutuskoodiModel(String koodiUri) {
        KoulutuskoodiModel km = new KoulutuskoodiModel();
        createKoodiModel(km, koodiUri);
        return km;
    }

    protected KoulutusKoodistoModel createKoodiModel(KoulutusKoodistoModel kkm, String fieldName) {
        if (kkm == null) {
            kkm = new KoodiModel();
        }

        final String koodiUri = fieldName + "_uri";

        kkm.setKielikoodi(LANGUAGE_FI);
        kkm.setKoodi(fieldName);
        kkm.setKoodistoUri(koodiUri);
        kkm.setKoodistoUriVersio(createUri(fieldName, VERSION));
        kkm.setKoodistoVersio(1);
        kkm.setKuvaus(fieldName + " kuvaus");
        kkm.setNimi(fieldName + " nimi");

        return kkm;
    }

    protected static String createUri(String fieldName) {
        return createUri(fieldName, VERSION);
    }

    protected static String createUri(String fieldName, int version) {
        return fieldName + "_uri#" + version;
    }

    protected List<String> createList(final int max, final String value) {
        List<String> list = new ArrayList<String>(max);

        for (int i = 0; i < max; i++) {
            list.add(value + "_" + i);
        }
        return list;
    }

    protected Set<String> createSet(final int max, final String value) {
        Set<String> list = new HashSet<String>(max);

        for (int i = 0; i < max; i++) {
            list.add(value + "_" + i);
        }
        return list;
    }

    protected MonikielinenTekstiTyyppi convertToMonikielinenTekstiTyyppi(final String languageCode, final String text) {
        MonikielinenTekstiTyyppi tyyppi = new MonikielinenTekstiTyyppi();
        tyyppi.getTeksti().add(KoulutusConveter.convertToMonikielinenTekstiTyyppi(languageCode, text));
        return tyyppi;
    }

    protected void clearTeksti(List<NimettyMonikielinenTekstiTyyppi> tekstit, Object tunniste) {
        NimettyMonikielinenTekstiTyyppi old = ConversionUtils.getTeksti(tekstit, tunniste);
        if (old != null) {
            tekstit.remove(old);
        }
    }

    protected void setTeksti(List<NimettyMonikielinenTekstiTyyppi> tekstit, Object tunniste, String languageCode, String text) {
        clearTeksti(tekstit, tunniste);

        NimettyMonikielinenTekstiTyyppi tyyppi = new NimettyMonikielinenTekstiTyyppi();
        tyyppi.setTunniste(String.valueOf(tunniste));
        tyyppi.getTeksti().add(KoulutusConveter.convertToMonikielinenTekstiTyyppi(languageCode, text));

        tekstit.add(tyyppi);
    }

    protected KoodistoKoodiTyyppi createKoodistoKoodiTyyppi(final String fieldName) {
        return KoulutusConveter.createKoodi(createUri(fieldName), false, fieldName);
    }

    protected KoodiType createKoodiType(final String fieldName) {
        return createKoodiType(fieldName, null);
    }

    protected KoodiType createKoodiType(final String fieldName, final String koodistoUri) {
        KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri(fieldName + "_uri");
        koodiType.setVersio(VERSION);
        koodiType.setKoodiArvo(fieldName);
        koodiType.setTila(TilaType.HYVAKSYTTY);

        KoodiMetadataType meta = new KoodiMetadataType();
        meta.setKuvaus(fieldName + " kuvaus");
        meta.setKieli(KieliType.FI);
        koodiType.getMetadata().add(meta);

        KoodistoItemType koodistoItemType = new KoodistoItemType();

        if (koodistoUri == null) {
            koodistoItemType.setKoodistoUri(createKoodistoUri(fieldName));
        } else {
            koodistoItemType.setKoodistoUri(koodistoUri);
        }
        koodiType.setKoodisto(koodistoItemType);

        return koodiType;
    }

    protected List<KoodiType> createKoodiTypes(final String... fieldNames) {
        List<KoodiType> types = Lists.<KoodiType>newArrayList();
        for (String s : fieldNames) {
            types.add(createKoodiType(s));
        }
        return types;
    }

    protected List<KoodiType> createKoodiTypeList(final KoodiType type) {
        List<KoodiType> types = Lists.<KoodiType>newArrayList();
        types.add(type);
        return types;
    }

    protected String createKoodistoUri(String koodistoUri) {
        Preconditions.checkNotNull(koodistoUri, "Koodisto uri cannot be null");
        return "koodisto_" + koodistoUri + "_uri";
    }
}
