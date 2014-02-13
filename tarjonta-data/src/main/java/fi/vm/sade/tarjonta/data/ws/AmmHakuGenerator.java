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

import static fi.vm.sade.tarjonta.data.ws.AbstractGenerator.UPDATED_BY_USER;
import fi.vm.sade.tarjonta.data.util.KoodistoUtil;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HaunNimi;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.KoodistoURI;

import java.util.Date;
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
public class AmmHakuGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(AmmHakuGenerator.class);

    private static final String OID_TYPE = "AS_";
    private static final Date DATE_HAKUAIKA_BEGIN = new DateTime(2013, 1, 1, 1, 1).toDate();
    private static final Date DATE_HAKUAIKA_END = new DateTime(2020, 1, 1, 1, 1).toDate();
    private TarjontaAdminService tarjontaAdminService;

    public AmmHakuGenerator() {
        super(OID_TYPE);
    }

    public AmmHakuGenerator(TarjontaAdminService tarjontaAdminServce) {
        super(OID_TYPE);
        this.tarjontaAdminService = tarjontaAdminServce;
    }

    public String create() {
        HakuTyyppi createToteutus = createHaku();
        tarjontaAdminService.lisaaHaku(createToteutus);
        LOG.info("hakukohde created : {}", createToteutus.getOid());
        return createToteutus.getOid();
    }

    private HakuTyyppi createHaku() {
        HakuTyyppi tyyppi = new HakuTyyppi();
        tyyppi.setOid(generateOid());
        tyyppi.setHaunTila(TarjontaTila.JULKAISTU);
        tyyppi.setHakuVuosi(2013);
        tyyppi.setHakukausiUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_ALKAMISKAUSI_URI, "k"));
        tyyppi.setKoulutuksenAlkamisKausiUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_ALKAMISKAUSI_URI, "k"));
        tyyppi.setHakutapaUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_HAKUTAPA_URI, "01"));
        tyyppi.setHakutyyppiUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_HAUN_KOHDEJOUKKO_URI, "03"));
        tyyppi.setKohdejoukkoUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_HAKUTAPA_URI, "11"));
        tyyppi.setHaunTunniste(tyyppi.getOid());
        tyyppi.setHaunAlkamisPvm(DATE_HAKUAIKA_BEGIN);
        tyyppi.setHaunAlkamisPvm(DATE_HAKUAIKA_END);
        tyyppi.setHakulomakeUrl(null);
        tyyppi.setSijoittelu(true);
        tyyppi.setKoulutuksenAlkamisVuosi(2013);
        tyyppi.getHaunKielistetytNimet().add(createHakunimi(tyyppi.getOid(), "fi"));
        tyyppi.getHaunKielistetytNimet().add(createHakunimi(tyyppi.getOid(), "sv"));
        tyyppi.getHaunKielistetytNimet().add(createHakunimi(tyyppi.getOid(), "en"));
        SisaisetHakuAjat sisaisetHakuAjat = new SisaisetHakuAjat();
        sisaisetHakuAjat.setHakuajanKuvaus("HakuajanKuvaus " + tyyppi.getOid());
        sisaisetHakuAjat.setSisaisenHaunAlkamisPvm(DATE_HAKUAIKA_BEGIN);
        sisaisetHakuAjat.setSisaisenHaunPaattymisPvm(DATE_HAKUAIKA_END);
        tyyppi.getSisaisetHakuajat().add(sisaisetHakuAjat);

        tyyppi.setViimeisinPaivittajaOid(UPDATED_BY_USER);
      
        return tyyppi;
    }

    /*
     * Language code is the real language code, not a koodi uri!
     */
    private HaunNimi createHakunimi(String nimi, String lang) {
        HaunNimi haunNimi = new HaunNimi();
        haunNimi.setKielikoodi(lang);
        haunNimi.setNimi(nimi + " " + lang);

        return haunNimi;
    }
}
