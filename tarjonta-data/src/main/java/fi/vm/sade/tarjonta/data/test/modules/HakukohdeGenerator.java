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

import fi.vm.sade.tarjonta.data.util.KoodistoURIHelper;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import com.google.common.base.Preconditions;
import static fi.vm.sade.tarjonta.data.test.modules.AbstractGenerator.UPDATED_BY_USER;
import fi.vm.sade.tarjonta.data.util.KoodistoUtil;
import fi.vm.sade.tarjonta.service.types.AjankohtaTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
@Component
@Configurable(preConstruction = false)
public class HakukohdeGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeGenerator.class);
    private static final Date DATE = new DateTime(2020, 1, 1, 1, 1).toDate();
    private static final Date EXAM_START_DATE = new DateTime(2013, 1, 1, 1, 1).toDate();
    private static final Date EXAM_END_DATE = new DateTime(2022, 1, 1, 1, 1).toDate();
    private static final int MAX_ATTACHMENTS = 10;
    private static final int MAX_EXAMS = 10;
    private static final int MAX_EXAMS_DAYS = 10;
    private static final Integer[] HAKUKOHTEET_KOODISTO_ARVO = new Integer[]{
        582, 498, 490, 186, 977, 600, 597, 601, 858, 414, 910, 886
    };
    private static final String OID_TYPE = "AO_";
    private TarjontaAdminService tarjontaAdminService;

    public HakukohdeGenerator() {
        super(OID_TYPE);
    }

    public HakukohdeGenerator(TarjontaAdminService tarjontaAdminServce) {
        super(OID_TYPE);
        this.tarjontaAdminService = tarjontaAdminServce;
    }

    public void create(final String hakuOid, String komotoOid) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");

        for (Integer koodiarvo : HAKUKOHTEET_KOODISTO_ARVO) {
            HakukohdeTyyppi hakukohde = createHakukohde(hakuOid, komotoOid, koodiarvo);
            tarjontaAdminService.lisaaHakukohde(hakukohde);

            final String hakukohdeOid = hakukohde.getOid();
            tarjontaAdminService.tallennaLiitteitaHakukohteelle(hakukohdeOid, createLiittees(hakuOid, komotoOid));
            tarjontaAdminService.tallennaValintakokeitaHakukohteelle(hakukohdeOid, createValintakoes());
        }
    }

    private HakukohdeTyyppi createHakukohde(final String hakuOid, final String komotoOid, final Integer koodiarvo) {
        Preconditions.checkNotNull(koodiarvo, "Koodisto hakukohde code value cannot be null.");
        LOG.debug("bind the hakukohde to haku OID {} and tutkinto OID {}", hakuOid, komotoOid);

        HakukohdeTyyppi tyyppi = new HakukohdeTyyppi();
        tyyppi.setHakukohteenTila(TarjontaTila.JULKAISTU);

        tyyppi.getHakukohteenKoulutusOidit().add(komotoOid);
        tyyppi.setHakukohteenHakuOid(hakuOid);
        tyyppi.setHakukohteenKoulutusaste(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        tyyppi.setHakukohdeNimi(KoodistoUtil.toKoodiUri(KoodistoURIHelper.KOODISTO_HAKUKOHDE_URI, koodiarvo.toString()));
        tyyppi.setOid(generateOid());

        tyyppi.setLiitteidenToimitusOsoite(createPostiosoite());
        tyyppi.setLisatiedot(createKoodiUriLorem());
        tyyppi.setValinnanAloituspaikat(1000);
        tyyppi.setAloituspaikat(1000);
        tyyppi.setKaytetaanHaunPaattymisenAikaa(Boolean.TRUE);
        tyyppi.setHakukohdeKoodistoNimi(tyyppi.getHakukohteenHakuOid() + " " + koodiarvo);
        tyyppi.setAlinHyvaksyttavaKeskiarvo(BigDecimal.ZERO);

        tyyppi.setLisatiedot(createKoodiUriLorem());
        tyyppi.setHakukohteenHaunNimi(createMonikielinenTekstiTyyppi(hakuOid + " " + komotoOid));
        tyyppi.setSahkoinenToimitusOsoite(createUri(hakuOid, komotoOid));

        tyyppi.setValintaperustekuvausKoodiUri(KoodistoUtil.toKoodiUri(KoodistoURIHelper.KOODISTO_HAKUKOHDE_URI, "4"));
        tyyppi.setSoraKuvausKoodiUri(KoodistoUtil.toKoodiUri(KoodistoURIHelper.KOODISTO_HAKUKOHDE_URI, "1"));
        tyyppi.setViimeisinPaivittajaOid(UPDATED_BY_USER);
        //tyyppi.setViimeisinPaivitysPvm(UPDATED_DATE);

        return tyyppi;
    }

    private List<HakukohdeLiiteTyyppi> createLiittees(final String hakuOid, final String komotoOid) {
        List<HakukohdeLiiteTyyppi> types = new ArrayList<HakukohdeLiiteTyyppi>();

        for (int i = 0; i < MAX_ATTACHMENTS; i++) {
            HakukohdeLiiteTyyppi tyyppi = new HakukohdeLiiteTyyppi();
            tyyppi.setLiitteenKuvaus(createKoodiUriLorem());
            tyyppi.setLiitteenToimitusOsoite(createPostiosoite());
            tyyppi.setLiitteenTyyppi(KoodistoUtil.toKoodiUri(KoodistoURIHelper.KOODISTO_LIITTEEN_TYYPPI_URI, "1"));
            tyyppi.setSahkoinenToimitusOsoite(createUri(hakuOid, komotoOid));
            tyyppi.setLiitteenTyyppiKoodistoNimi("???");
            tyyppi.setToimitettavaMennessa(DATE);

            tyyppi.setViimeisinPaivittajaOid(UPDATED_BY_USER);
            tyyppi.setViimeisinPaivitysPvm(UPDATED_DATE);
            types.add(tyyppi);
        }

        return types;
    }

    private List<ValintakoeTyyppi> createValintakoes() {
        List<ValintakoeTyyppi> types = new ArrayList<ValintakoeTyyppi>();

        for (int i = 0; i < MAX_EXAMS; i++) {
            ValintakoeTyyppi tyyppi = new ValintakoeTyyppi();
            tyyppi.setKuvaukset(createKoodiUriLorem());
            tyyppi.setLisaNaytot(createKoodiUriLorem());
            // hakukohdeLiiteTyyppi.setValintakokeenTunniste(hakuOid + " " + komotoOid); //long ID not String OID
            tyyppi.setValintakokeenTyyppi("???");
            tyyppi.setViimeisinPaivittajaOid(UPDATED_BY_USER);
            tyyppi.setViimeisinPaivitysPvm(UPDATED_DATE);

            for (int indexDays = 0; indexDays < MAX_EXAMS_DAYS; indexDays++) {
                AjankohtaTyyppi ajankohtaTyyppi = new AjankohtaTyyppi();
                ajankohtaTyyppi.setAlkamisAika(EXAM_START_DATE);
                ajankohtaTyyppi.setPaattymisAika(EXAM_END_DATE);
                ajankohtaTyyppi.setKuvaus(LOREM.substring(0, 30));
                ajankohtaTyyppi.setValintakoeAjankohtaOsoite(createPostiosoite());
                tyyppi.getAjankohdat().add(ajankohtaTyyppi);
            }
            
            types.add(tyyppi);
        }

        return types;
    }

    private String createUri(final String hakuOid, final String komotoOid) {
        return "www.oph.fi/" + hakuOid + "/" + komotoOid + "/loremipsumdolorsitametconsecteturadipiscingelitintegersitametodioegetmetusporttitorrhoncusvitaeatnisi";
    }
}
