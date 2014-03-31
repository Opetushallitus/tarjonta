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
package fi.vm.sade.tarjonta.koodisto;

import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import static org.junit.Assert.*;

import org.junit.Test;

import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidListType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchOidType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import java.util.List;
import org.powermock.reflect.Whitebox;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import org.junit.Before;

/**
 *
 * @author jani
 */
public class OppilaitosKoodiRelationsTest {

    private static final String CORRECT_KOODI = "koulutustyyppi_3";
    private static final String INVALID_KOODI = "koulutustyyppi_1234";

    private static final String OPH_OID = "1.2.246.562.10.00000000001";
    private static final String KOULUTUSTOIMIJA_OID = "1.2.246.562.10.70829532053";
    private static final String OPPILAITOS_OID = "1.2.246.562.10.33517818648";
    private static final String OTHER_OID = "1.2.246.562.10.xxxxxxx1";

    private static final String PATH_OPPILAITOS = "|" + OPH_OID + "|" + KOULUTUSTOIMIJA_OID + "|" + OPPILAITOS_OID + "|";
    private static final String PATH_OPH = "|" + OPH_OID + "|";
    private static final String PATH_KOULUTUSTOIMIJA = "|" + OPH_OID + "|" + KOULUTUSTOIMIJA_OID + "|";
    private static final String PATH_INCORRECT = "|" + OPH_OID + "|" + KOULUTUSTOIMIJA_OID + "|" + OTHER_OID + "|" + "|" + OPPILAITOS_OID + "|";

    private TarjontaKoodistoHelper tarjontaKoodistoHelperMock;
    private OrganisaatioService organisaatioServiceMock;
    private OppilaitosKoodiRelations instance;

    private OrganisaatioDTO orgOph;
    private OrganisaatioDTO orgOppilaitos;
    private OrganisaatioDTO orgKoulutustoimija;
    private OrganisaatioDTO orgOther;

    @Before
    public void setUp() {
        KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI = "koulutustyyppi";

        orgOph = new OrganisaatioDTO();
        orgOph.setOid(OPH_OID);
        orgOph.setParentOidPath("");
        orgOph.getTyypit().add(OrganisaatioTyyppi.MUU_ORGANISAATIO);
        orgOph.setOppilaitosTyyppi("koulutustyyppi_yy");

        orgKoulutustoimija = new OrganisaatioDTO();
        orgKoulutustoimija.setOid(KOULUTUSTOIMIJA_OID);
        orgKoulutustoimija.setParentOidPath(PATH_OPH);
        orgKoulutustoimija.getTyypit().add(OrganisaatioTyyppi.KOULUTUSTOIMIJA);
        orgKoulutustoimija.setOppilaitosTyyppi("koulutustyyppi_xx");

        orgOppilaitos = new OrganisaatioDTO();
        orgOppilaitos.setOid(OPPILAITOS_OID);
        orgOppilaitos.setParentOidPath(PATH_KOULUTUSTOIMIJA);
        orgOppilaitos.getTyypit().add(OrganisaatioTyyppi.OPPILAITOS);
        orgOppilaitos.setOppilaitosTyyppi(CORRECT_KOODI);

        orgOther = new OrganisaatioDTO();
        orgOther.setOid(OTHER_OID);
        orgOther.setParentOidPath(PATH_INCORRECT);
        orgOther.getTyypit().add(OrganisaatioTyyppi.MUU_ORGANISAATIO);
        orgOther.setOppilaitosTyyppi(CORRECT_KOODI);

        organisaatioServiceMock = createMock(OrganisaatioService.class);
        tarjontaKoodistoHelperMock = createMock(TarjontaKoodistoHelper.class);

        instance = new OppilaitosKoodiRelations();
        Whitebox.setInternalState(instance, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(instance, "tarjontaKoodistoHelper", tarjontaKoodistoHelperMock);
        Whitebox.setInternalState(instance, "rootOphOid", OPH_OID);
    }

    @Test
    public void testSplitOrganisationPath() {
        List<String> splitOrganisationPath = OppilaitosKoodiRelations.splitOrganisationPath(PATH_OPPILAITOS);

        assertEquals(OPH_OID, splitOrganisationPath.get(0));
        assertEquals(KOULUTUSTOIMIJA_OID, splitOrganisationPath.get(1));
        assertEquals(OPPILAITOS_OID, splitOrganisationPath.get(2));

        assertEquals(3, splitOrganisationPath.size());

        splitOrganisationPath = OppilaitosKoodiRelations.splitOrganisationPath("|" + OPH_OID + "|");
        assertEquals(1, splitOrganisationPath.size());
    }

    @Test
    public void testIsKoulutusAllowedForOrganisationNotFound() {
        expect(organisaatioServiceMock.findByOid(OPPILAITOS_OID)).andReturn(null);

        replay(tarjontaKoodistoHelperMock);
        replay(organisaatioServiceMock);

        boolean result = instance.isKoulutusAllowedForOrganisation(OPPILAITOS_OID, KoulutusasteTyyppi.KORKEAKOULUTUS);

        verify(tarjontaKoodistoHelperMock);
        verify(organisaatioServiceMock);

        assertFalse(result);

        OrganisaatioDTO organisaatioDTO = new OrganisaatioDTO();
        organisaatioDTO.setOid(OPH_OID);
    }

    @Test
    public void testIsKoulutusAllowedForOrganisationSearchSuccessQuick() {
        expect(organisaatioServiceMock.findByOid(OPPILAITOS_OID)).andReturn(orgOppilaitos).times(2);
        expect(tarjontaKoodistoHelperMock.getKoodistoRelations(CORRECT_KOODI, KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI, SuhteenTyyppiType.SISALTYY, false)).andReturn(createKoodis(CORRECT_KOODI));

        replay(tarjontaKoodistoHelperMock);
        replay(organisaatioServiceMock);

        final boolean result = instance.isKoulutusAllowedForOrganisation(OPPILAITOS_OID, KoulutusasteTyyppi.KORKEAKOULUTUS);

        verify(tarjontaKoodistoHelperMock);
        verify(organisaatioServiceMock);

        assertTrue(result);
    }

    @Test
    public void testIsKoulutusAllowedForOrganisationSearchFailKoodiUri() {
        //not correct koodi result

        expect(organisaatioServiceMock.findByOid(OPPILAITOS_OID)).andReturn(orgOppilaitos).times(3);
        expect(organisaatioServiceMock.findByOid(KOULUTUSTOIMIJA_OID)).andReturn(orgKoulutustoimija).times(1);
        expect(organisaatioServiceMock.findChildrenOidsByOid(isA(OrganisaatioSearchOidType.class))).andReturn(new OrganisaatioOidListType());

        expect(tarjontaKoodistoHelperMock.getKoodistoRelations(CORRECT_KOODI, KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI, SuhteenTyyppiType.SISALTYY, false)).andReturn(createKoodis(INVALID_KOODI)).times(2);

        replay(tarjontaKoodistoHelperMock);
        replay(organisaatioServiceMock);

        final boolean result = instance.isKoulutusAllowedForOrganisation(OPPILAITOS_OID, KoulutusasteTyyppi.KORKEAKOULUTUS);

        verify(tarjontaKoodistoHelperMock);
        verify(organisaatioServiceMock);

        assertFalse(result);
    }

    @Test
    public void testIsKoulutusAllowedForOrganisationSearchFailInvalidKoulustuasteTyyppi() {
        //not correct koodi result

        expect(organisaatioServiceMock.findByOid(OPPILAITOS_OID)).andReturn(orgOppilaitos).times(3);
        expect(organisaatioServiceMock.findByOid(KOULUTUSTOIMIJA_OID)).andReturn(orgKoulutustoimija).times(1);
        expect(organisaatioServiceMock.findChildrenOidsByOid(isA(OrganisaatioSearchOidType.class))).andReturn(new OrganisaatioOidListType());

        expect(tarjontaKoodistoHelperMock.getKoodistoRelations(CORRECT_KOODI, KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI, SuhteenTyyppiType.SISALTYY, false)).andReturn(createKoodis(CORRECT_KOODI)).times(2);

        replay(tarjontaKoodistoHelperMock);
        replay(organisaatioServiceMock);

        final boolean result = instance.isKoulutusAllowedForOrganisation(OPPILAITOS_OID, KoulutusasteTyyppi.LUKIOKOULUTUS);

        verify(tarjontaKoodistoHelperMock);
        verify(organisaatioServiceMock);

        assertFalse(result);
    }

    @Test
    public void testIsKoulutusAllowedForOrganisationSearchSuccessKoulutustoimija() {
        expect(organisaatioServiceMock.findByOid(KOULUTUSTOIMIJA_OID)).andReturn(orgKoulutustoimija).times(2);
        //KOULUTUSTOIMIJA_OID
        OrganisaatioOidListType oids = new OrganisaatioOidListType();
        oids.getOrganisaatioOidList().add(new OrganisaatioOidType(OPPILAITOS_OID));
        expect(organisaatioServiceMock.findChildrenOidsByOid(isA(OrganisaatioSearchOidType.class))).andReturn(oids);

        expect(organisaatioServiceMock.findByOid(OPPILAITOS_OID)).andReturn(orgOppilaitos).times(1);;

        expect(tarjontaKoodistoHelperMock.getKoodistoRelations(CORRECT_KOODI, KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI, SuhteenTyyppiType.SISALTYY, false)).andReturn(createKoodis(CORRECT_KOODI));

        replay(tarjontaKoodistoHelperMock);
        replay(organisaatioServiceMock);

        final boolean result = instance.isKoulutusAllowedForOrganisation(KOULUTUSTOIMIJA_OID, KoulutusasteTyyppi.KORKEAKOULUTUS);

        verify(tarjontaKoodistoHelperMock);
        verify(organisaatioServiceMock);

        assertTrue(result);
    }

    @Test
    public void testIsKoulutusAllowedForOrganisationSearchSuccessKoulutustoimijaOppilaitos() {
        expect(organisaatioServiceMock.findByOid(OTHER_OID)).andReturn(orgOther).times(3);
        expect(organisaatioServiceMock.findByOid(KOULUTUSTOIMIJA_OID)).andReturn(orgKoulutustoimija);

        //return empty resault
        expect(organisaatioServiceMock.findChildrenOidsByOid(isA(OrganisaatioSearchOidType.class))).andReturn(new OrganisaatioOidListType());
        expect(organisaatioServiceMock.findByOid(OPPILAITOS_OID)).andReturn(orgOppilaitos).times(1);

        expect(tarjontaKoodistoHelperMock.getKoodistoRelations(CORRECT_KOODI, KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI, SuhteenTyyppiType.SISALTYY, false)).andReturn(createKoodis(CORRECT_KOODI));
        replay(tarjontaKoodistoHelperMock);
        replay(organisaatioServiceMock);

        final boolean result = instance.isKoulutusAllowedForOrganisation(OTHER_OID, KoulutusasteTyyppi.KORKEAKOULUTUS);

        verify(tarjontaKoodistoHelperMock);
        verify(organisaatioServiceMock);

        assertTrue(result);
    }

    private List<KoodiType> createKoodis(final String koodiUri) {
        List<KoodiType> koodis = Lists.<KoodiType>newArrayList();
        KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri(koodiUri);
        koodis.add(koodiType);
        return koodis;
    }
}
