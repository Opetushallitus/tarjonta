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

import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidListType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchOidType;
import fi.vm.sade.tarjonta.data.util.KoodistoURIHelper;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.data.util.KoodistoUtil;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import java.util.Calendar;

/**
 *
 * @author Jani Wilén
 */
@Component
@Configurable(preConstruction = false)
public class HakukohdeGenerator extends AbstractGenerator {

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
            tarjontaAdminService.lisaaHakukohde(createHakukohde(hakuOid, komotoOid, koodiarvo));
        }
    }

    private HakukohdeTyyppi createHakukohde(final String hakuOid, String komotoOid, final Integer koodiarvo) {
        Preconditions.checkNotNull(koodiarvo, "Koodisto hakukohde code value cannot be null.");
        HakukohdeTyyppi tyyppi = new HakukohdeTyyppi();
        tyyppi.setHakukohteenTila(TarjontaTila.JULKAISTU);
        tyyppi.getHakukohteenKoulutusOidit().add(komotoOid);
        tyyppi.setHakukohteenHakuOid(hakuOid);
        tyyppi.setHakukohteenKoulutusaste(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        tyyppi.setHakukohdeNimi(KoodistoUtil.toKoodiUri(KoodistoURIHelper.KOODISTO_HAKUKOHDE_URI, koodiarvo.toString()));
        tyyppi.setOid(generateOid());

        OsoiteTyyppi osoite = new OsoiteTyyppi();
        osoite.setPostinumero("PL 6666");
        osoite.setPostitoimipaikka("ESPOON KAUPUNKI");
        tyyppi.setLiitteidenToimitusOsoite(osoite);
        tyyppi.setLisatiedot(createLorem());
        tyyppi.setValinnanAloituspaikat(1000);
        tyyppi.setKaytetaanHaunPaattymisenAikaa(Boolean.TRUE);
        tyyppi.setHakukohdeKoodistoNimi(tyyppi.getHakukohteenHakuOid() + " " + koodiarvo);

        return tyyppi;
    }
}
