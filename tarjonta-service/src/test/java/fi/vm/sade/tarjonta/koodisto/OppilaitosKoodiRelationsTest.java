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
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * 
 * @author jani
 */
public class OppilaitosKoodiRelationsTest {

    private static final String OPPILAITOSTYYPPI_LUKIO = "oppilaitostyyppi_15";
    private static final String OPPILAITOSTYYPPI_AMK = "oppilaitostyyppi_41";

    private static final String OPH_OID = "1.2.246.562.10.00000000001";
    private static final String KOULUTUSTOIMIJA_OID = "1.2.246.562.10.70829532053";
    private static final String LUKIO_OID = "1.2.246.562.10.33517818648";
    private static final String LUKIO_OPETUSPISTE_OID = "1.2.246.562.10.33517818658";
    private static final String AMK_OID = "1.2.246.562.10.33517818649";
    private static final String OTHER_OID = "1.2.246.562.10.xxxxxxx1";
    private static final String KOULUTUSASTE_LUKIOKOULUTUS = "koulutusasteoph2002_31";
    private static final String KOULUTUSASTE_YLEMPIAMK = "koulutusasteoph2002_71";
    private static final String KOULUTUSASTE_ALEMPIKK = "koulutusasteoph2002_63";

    private static final String PATH_LUKIO = "|" + OPH_OID + "|"
            + KOULUTUSTOIMIJA_OID + "|" + LUKIO_OID + "|";
    private static final String PATH_AMK = "|" + OPH_OID + "|"
            + KOULUTUSTOIMIJA_OID + "|" + AMK_OID + "|";
    private static final String PATH_OPH = "|" + OPH_OID + "|";
    private static final String PATH_KOULUTUSTOIMIJA = "|" + OPH_OID + "|"
            + KOULUTUSTOIMIJA_OID + "|";
    private static final String PATH_INCORRECT = "|" + OPH_OID + "|"
            + KOULUTUSTOIMIJA_OID + "|" + OTHER_OID + "|" + "|" + LUKIO_OID
            + "|";
    private static final String PATH_LUKIO_OPETUSPISTE = "|" + OPH_OID + "|"
            + KOULUTUSTOIMIJA_OID + "|" + LUKIO_OID + "|"
            + LUKIO_OPETUSPISTE_OID + "|";;

    private OppilaitosKoodiRelations instance;

    @Before
    public void setUp() {
        KoodistoURI.KOODISTO_KOULUTUSASTE_URI = "koulutusasteoph2002";

        OrganisaatioRDTO OPH = new OrganisaatioRDTO();
        OPH.setOid(OPH_OID);
        OPH.setParentOidPath(PATH_OPH);
        OPH.getTyypit().add(OrganisaatioService.OrganisaatioTyyppi.MUU_ORGANISAATIO.value());
        OPH.setOppilaitosTyyppiUri("koulutustyyppi_yy");

        OrganisaatioRDTO KOULUTUSTOIMIJA = new OrganisaatioRDTO();
        KOULUTUSTOIMIJA.setOid(KOULUTUSTOIMIJA_OID);
        KOULUTUSTOIMIJA.setParentOidPath(PATH_KOULUTUSTOIMIJA);
        KOULUTUSTOIMIJA.getTyypit().add(OrganisaatioService.OrganisaatioTyyppi.KOULUTUSTOIMIJA.value());
        KOULUTUSTOIMIJA.setOppilaitosTyyppiUri("koulutustyyppi_xx");

        // amk
        OrganisaatioRDTO AMK = new OrganisaatioRDTO();
        AMK.setOid(AMK_OID);
        AMK.setParentOidPath(PATH_LUKIO);
        AMK.getTyypit().add(OrganisaatioService.OrganisaatioTyyppi.OPPILAITOS.value());
        AMK.setOppilaitosTyyppiUri(OPPILAITOSTYYPPI_AMK);

        // lukio
        OrganisaatioRDTO LUKIO = new OrganisaatioRDTO();
        LUKIO.setOid(LUKIO_OID);
        LUKIO.setParentOidPath(PATH_AMK);
        LUKIO.getTyypit().add(OrganisaatioService.OrganisaatioTyyppi.OPPILAITOS.value());
        LUKIO.setOppilaitosTyyppiUri(OPPILAITOSTYYPPI_LUKIO);

        // lukion opetuspiste
        OrganisaatioRDTO LUKIO_OPETUSPISTE = new OrganisaatioRDTO();
        LUKIO_OPETUSPISTE.setOid(LUKIO_OPETUSPISTE_OID);
        LUKIO_OPETUSPISTE.setParentOidPath(PATH_LUKIO_OPETUSPISTE);
        LUKIO_OPETUSPISTE.getTyypit().add(OrganisaatioService.OrganisaatioTyyppi.TOIMIPISTE.value());

        OrganisaatioRDTO orgOther = new OrganisaatioRDTO();
        orgOther.setOid(OTHER_OID);
        orgOther.setParentOidPath(PATH_INCORRECT);
        orgOther.getTyypit().add(OrganisaatioService.OrganisaatioTyyppi.MUU_ORGANISAATIO.value());

        OrganisaatioService organisaatioServiceMock = Mockito.mock(OrganisaatioService.class);
        TarjontaKoodistoHelper tarjontaKoodistoHelperMock = Mockito.mock(TarjontaKoodistoHelper.class);

        instance = new OppilaitosKoodiRelations();
        Whitebox.setInternalState(instance, "organisaatioService",
                organisaatioServiceMock);
        Whitebox.setInternalState(instance, "tarjontaKoodistoHelper",
                tarjontaKoodistoHelperMock);
        Whitebox.setInternalState(instance, "rootOphOid", OPH_OID);

        // stub data
        Mockito.stub(organisaatioServiceMock.findByOid(LUKIO_OID)).toReturn(LUKIO);
        Mockito.stub(organisaatioServiceMock.findByOid(LUKIO_OPETUSPISTE_OID)).toReturn(LUKIO_OPETUSPISTE);
        Mockito.stub(organisaatioServiceMock.findByOid(AMK_OID)).toReturn(AMK);
        Mockito.stub(organisaatioServiceMock.findByOid(OPH_OID)).toReturn(OPH);
        Mockito.stub(organisaatioServiceMock.findByOid(KOULUTUSTOIMIJA_OID))
                .toReturn(KOULUTUSTOIMIJA);
        
        Mockito.stub(organisaatioServiceMock.findChildrenOidsByOid(Mockito.anyString())).toAnswer(new Answer<Set<String>>() {
            @Override
            public Set<String> answer(InvocationOnMock invocation)
                    throws Throwable {
                Object[] args = invocation.getArguments();
                String oid = (String) args[0];
                if(AMK_OID.equals(oid)) {
                    return oidlist();
                }
                if(LUKIO_OID.equals(oid)) {
                    return oidlist(LUKIO_OPETUSPISTE_OID);
                }
                if(KOULUTUSTOIMIJA_OID.equals(oid)) {
                    return oidlist(AMK_OID, LUKIO_OID);
                }
                return oidlist(); //empty list
            }

            private Set<String> oidlist(String... oids) {
                return Sets.newHashSet(oids);
            }
        });
        
        Mockito.stub(tarjontaKoodistoHelperMock.getKoodistoRelations(
                OPPILAITOSTYYPPI_LUKIO,
                KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
                SuhteenTyyppiType.SISALTYY, false)).toReturn(
                createKoodis(KOULUTUSASTE_LUKIOKOULUTUS));
        
        Mockito.stub(
                tarjontaKoodistoHelperMock.getKoodistoRelations(
                        OPPILAITOSTYYPPI_AMK,
                        KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
                        SuhteenTyyppiType.SISALTYY, false)).toReturn(
                createKoodis(KOULUTUSASTE_YLEMPIAMK));

    }

    @Test
    public void testSplitOrganisationPath() {
        List<String> splitOrganisationPath = OppilaitosKoodiRelations
                .splitOrganisationPath(PATH_LUKIO);

        assertEquals(OPH_OID, splitOrganisationPath.get(0));
        assertEquals(KOULUTUSTOIMIJA_OID, splitOrganisationPath.get(1));
        assertEquals(LUKIO_OID, splitOrganisationPath.get(2));

        assertEquals(3, splitOrganisationPath.size());

        splitOrganisationPath = OppilaitosKoodiRelations
                .splitOrganisationPath("|" + OPH_OID + "|");
        assertEquals(1, splitOrganisationPath.size());
    }

    @Test
    public void testIsKoulutusAllowedForOrganisationNotFound() {
        assertFalse(instance.isKoulutusAllowedForOrganisation("FOO",
                KOULUTUSASTE_ALEMPIKK));
    }

    @Test
    public void testCorrectTypeOppilaitos() {
        assertTrue(instance.isKoulutusAllowedForOrganisation(
                AMK_OID, KOULUTUSASTE_YLEMPIAMK));
    }

    @Test
    public void testIncorrectTypeOppilaitos() {
        assertFalse(instance.isKoulutusAllowedForOrganisation(
                LUKIO_OID, KOULUTUSASTE_ALEMPIKK));
        assertFalse(instance.isKoulutusAllowedForOrganisation(
                LUKIO_OID, KOULUTUSASTE_YLEMPIAMK));
        assertFalse(instance.isKoulutusAllowedForOrganisation(
                AMK_OID, KOULUTUSASTE_ALEMPIKK));
}

    @Test
    public void testCorrectTypeOpetuspiste() {
        assertTrue(instance.isKoulutusAllowedForOrganisation(
                LUKIO_OPETUSPISTE_OID, KOULUTUSASTE_LUKIOKOULUTUS));
    }

    @Test
    public void testIncorrectTypeOpetuspiste() {
        assertFalse(instance.isKoulutusAllowedForOrganisation(
                LUKIO_OPETUSPISTE_OID, KOULUTUSASTE_ALEMPIKK));
    }

    @Test
    public void testCorrectTypeKoulutustoimija() {
        assertTrue(instance.isKoulutusAllowedForOrganisation(
                KOULUTUSTOIMIJA_OID, KOULUTUSASTE_LUKIOKOULUTUS));
        assertTrue(instance.isKoulutusAllowedForOrganisation(KOULUTUSTOIMIJA_OID,
                KOULUTUSASTE_YLEMPIAMK));
    }

    @Test
    public void testIncorrectTypeKoulutustoimija() {
        assertFalse(instance.isKoulutusAllowedForOrganisation(
                KOULUTUSTOIMIJA_OID, "foo"));
    }

    private List<KoodiType> createKoodis(final String koodiUri) {
        List<KoodiType> koodis = Lists.newArrayList();
        KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri(koodiUri);
        koodis.add(koodiType);
        return koodis;
    }
}
