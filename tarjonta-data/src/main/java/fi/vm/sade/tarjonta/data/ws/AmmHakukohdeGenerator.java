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

import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import com.google.common.base.Preconditions;
import static fi.vm.sade.tarjonta.data.ws.AbstractGenerator.UPDATED_BY_USER;
import fi.vm.sade.tarjonta.data.util.KoodistoUtil;
import fi.vm.sade.tarjonta.service.types.AjankohtaTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
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
public class AmmHakukohdeGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(AmmHakukohdeGenerator.class);
    private static final Date DATE = new DateTime(2020, 1, 1, 1, 1).toDate();
    private static final Date EXAM_START_DATE = new DateTime(2013, 1, 1, 1, 1).toDate();
    private static final Date EXAM_END_DATE = new DateTime(2022, 1, 1, 1, 1).toDate();
    private static final int MAX_ATTACHMENTS = 2;
    private static final int MAX_EXAMS = 2;
    private static final int MAX_EXAMS_DAYS = 2;
    public static final Integer[] HAKUKOHTEET_KOODISTO_ARVO = new Integer[]{
        582, 498, 490, 186
    };
    private static final String OID_TYPE = "AO_";
    private TarjontaAdminService tarjontaAdminService;

    public AmmHakukohdeGenerator() {
        super(OID_TYPE);
    }

    public AmmHakukohdeGenerator(TarjontaAdminService tarjontaAdminServce) {
        super(OID_TYPE);
        this.tarjontaAdminService = tarjontaAdminServce;
    }

    public void create(final String hakuOid, final Integer hakukohdeKoodiarvo, final List<String> komotoOids) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");
        Preconditions.checkNotNull(komotoOids, "List of KOMOTO OID cannot be null.");

        HakukohdeTyyppi hakukohde = createHakukohde(hakuOid, komotoOids, hakukohdeKoodiarvo);
        tarjontaAdminService.lisaaHakukohde(hakukohde);

        final String hakukohdeOid = hakukohde.getOid();
        tarjontaAdminService.tallennaLiitteitaHakukohteelle(hakukohdeOid, createLiittees(hakuOid));
        tarjontaAdminService.tallennaValintakokeitaHakukohteelle(hakukohdeOid, createValintakoes());

    }

    private HakukohdeTyyppi createHakukohde(final String hakuOid, final List<String> komotoOids, final Integer koodiarvo) {
        Preconditions.checkNotNull(koodiarvo, "Koodisto hakukohde code value cannot be null.");
        LOG.debug("bind the hakukohde to haku OID {} and tutkinto OIDs {}", hakuOid, komotoOids);

        HakukohdeTyyppi tyyppi = new HakukohdeTyyppi();
        tyyppi.setHakukohteenTila(TarjontaTila.JULKAISTU);

        tyyppi.getHakukohteenKoulutusOidit().addAll(komotoOids);
        tyyppi.setHakukohteenHakuOid(hakuOid);
        tyyppi.setHakukohteenKoulutusaste(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        tyyppi.setHakukohdeNimi(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_HAKUKOHDE_URI, koodiarvo.toString()));
        tyyppi.setOid(generateOid());

        tyyppi.setLiitteidenToimitusOsoite(createPostiosoite());
        tyyppi.setLisatiedot(createKoodiUriLorem());
        tyyppi.setValinnanAloituspaikat(1000);
        tyyppi.setAloituspaikat(1000);
        tyyppi.setKaytetaanHaunPaattymisenAikaa(Boolean.TRUE);
        tyyppi.setHakukohdeKoodistoNimi(tyyppi.getHakukohteenHakuOid() + " " + koodiarvo);
        tyyppi.setAlinHyvaksyttavaKeskiarvo(BigDecimal.ZERO);

        tyyppi.setLisatiedot(createKoodiUriLorem());
        tyyppi.setHakukohteenHaunNimi(createMonikielinenTekstiTyyppi(hakuOid));
        tyyppi.setSahkoinenToimitusOsoite(createUri(hakuOid));

        tyyppi.setValintaperustekuvausKoodiUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_HAKUKOHDE_URI, "4"));
        tyyppi.setSoraKuvausKoodiUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_HAKUKOHDE_URI, "1"));
        tyyppi.setViimeisinPaivittajaOid(UPDATED_BY_USER);
        //tyyppi.setViimeisinPaivitysPvm(UPDATED_DATE);

        return tyyppi;
    }

    private List<HakukohdeLiiteTyyppi> createLiittees(final String hakuOid) {
        List<HakukohdeLiiteTyyppi> types = new ArrayList<HakukohdeLiiteTyyppi>();

        for (int i = 0; i < MAX_ATTACHMENTS; i++) {
            HakukohdeLiiteTyyppi tyyppi = new HakukohdeLiiteTyyppi();
            tyyppi.setLiitteenKuvaus(createKoodiUriLorem());
            tyyppi.setLiitteenToimitusOsoite(createPostiosoite());
            tyyppi.setLiitteenTyyppi(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_LIITTEEN_TYYPPI_URI, "1"));
            tyyppi.setSahkoinenToimitusOsoite(createUri(hakuOid));
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

    private String createUri(final String hakuOid) {
        return "www.oph.fi/" + hakuOid + "/loremipsumdolorsitametconsecteturadipiscingelitintegersitametodioegetmetusporttitorrhoncusvitaeatnisi";
    }
}
