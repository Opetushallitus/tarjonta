package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.TestMockBase;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusDTOConverterToEntityTest;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class KoulutusResourceImplV1CreateTest extends TestMockBase {

    @InjectMocks
    private KoulutusResourceImplV1 koulutusResourceV1;

    @Mock
    private KoulutusImplicitDataPopulator implicitDataPopulator;

    @Before
    public void setUp() {
        when(organisaatioService.findByOid(anyString())).thenReturn(
                new OrganisaatioRDTO(){{
                    setOid("1.2.3.4");
                    setNimi(ImmutableMap.of("fi", "test"));
                }}
        );

        KoulutusImplicitDataPopulator populator = new KoulutusImplicitDataPopulator();
        Whitebox.setInternalState(populator, "koodiService", KoulutusDTOConverterToEntityTest.mockKoodiService(null));
        Whitebox.setInternalState(populator, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(koulutusResourceV1, "koulutusImplicitDataPopulator", populator);

        KoulutusValidator validatorMock = mock(KoulutusValidator.class);
        when(validatorMock.validateOrganisation(
                any(OrganisaatioV1RDTO.class), any(ResultV1RDTO.class), any(KoulutusValidationMessages.class),
                any(KoulutusValidationMessages.class))
        ).thenReturn(true);
        Whitebox.setInternalState(koulutusResourceV1, "koulutusValidator", validatorMock);
    }

    @Test
    public void thatAjankohtaIsValidWithNewKoulutus() {
        KoulutusV1RDTO koulutusDTO = createNewKoulutusDTO();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO).getEntity();

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatAjankohtaIsValidWithEmptyHakukohdes() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithEmptyHakukohdes());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO).getEntity();

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatAjankohtaIsValidWithHakukohdesThatDoNotContainAjankohtaInformation() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithoutAjankohtas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO).getEntity();

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatAjankohtaIsValidWithHakukohdesWithValidAjankohdas() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithValidAjankohta();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO).getEntity();

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatAjankohtaIsInvalidWithWrongSeason() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithInvalidAjankohta();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO).getEntity();

        assertThat(resultDTO.getErrors(), hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla")));
    }

    @Test
    public void thatAjankohtaIsInvalidWithHakukohdesWithOneValidAndOneInvalidAjankohta() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithOneValidAndOneInvalidAjankohta();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO).getEntity();

        assertThat(resultDTO.getErrors(), hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla")));
    }

    @Test
    public void thatKausiAndYearComboIsValidWithHakukohdesWithValidAjankohta() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithValidKausiAndYear();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO).getEntity();

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatKausiAndYearComboIsInvalidWithHakukohdesWithInvalidAjankohta() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithInvalidKausiAndYear();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO).getEntity();

        assertThat(resultDTO.getErrors(), hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla")));
    }

    @Test
    public void thatAjankohtaIsValidWithHakukohdesWithJatkuvaHaku() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithJatkuvatHaku());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithValidAjankohta();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO).getEntity();

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    private KoulutusV1RDTO createExistingKoulutusDTOWithValidKausiAndYear() {
        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();
        koulutusDTO.setKoulutuksenAlkamisvuosi(2015);

        KoodiV1RDTO kausiDTO = new KoodiV1RDTO();
        kausiDTO.setUri("kausi_s");
        koulutusDTO.setKoulutuksenAlkamiskausi(kausiDTO);

        return koulutusDTO;
    }

    private KoulutusV1RDTO createExistingKoulutusDTOWithInvalidKausiAndYear() {
        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();
        koulutusDTO.setKoulutuksenAlkamisvuosi(2016);

        KoodiV1RDTO kausiDTO = new KoodiV1RDTO();
        kausiDTO.setUri("kausi_k");
        koulutusDTO.setKoulutuksenAlkamiskausi(kausiDTO);

        return koulutusDTO;
    }

    private KoulutusV1RDTO createExistingKoulutusDTOWithValidAjankohta() {
        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();
        DateTime datetime = new DateTime();
        datetime = datetime.withYear(2015);
        datetime = datetime.withMonthOfYear(8);
        koulutusDTO.setKoulutuksenAlkamisPvms(Sets.newHashSet(datetime.toDate()));
        return koulutusDTO;
    }

    private KoulutusV1RDTO createExistingKoulutusDTOWithOneValidAndOneInvalidAjankohta() {
        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();

        DateTime datetime = new DateTime();
        datetime = datetime.withYear(2015);
        datetime = datetime.withMonthOfYear(8);
        koulutusDTO.setKoulutuksenAlkamisPvms(Sets.newHashSet(datetime.toDate()));

        datetime = new DateTime();
        datetime = datetime.withYear(2016);
        datetime = datetime.withMonthOfYear(1);
        koulutusDTO.getKoulutuksenAlkamisPvms().add(datetime.toDate());

        return koulutusDTO;
    }

    private KoulutusV1RDTO createExistingKoulutusDTOWithInvalidAjankohta() {
        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();
        DateTime datetime = new DateTime();
        datetime = datetime.withYear(2015);
        datetime = datetime.withMonthOfYear(1);
        koulutusDTO.setKoulutuksenAlkamisPvms(Sets.newHashSet(datetime.toDate()));
        return koulutusDTO;
    }

    private KoulutusmoduuliToteutus createKomotoWithHakukohdesWithValidAjankohdas() {
        KoulutusmoduuliToteutus komoto = createKomotoWithEmptyHakukohdes();

        Haku haku = new Haku();
        haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
        haku.setKoulutuksenAlkamisVuosi(2015);
        haku.setHakutapaUri("hakutapa_02#1");

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setTila(TarjontaTila.LUONNOS);
        hakukohde.setHaku(haku);
        komoto.addHakukohde(hakukohde);

        return komoto;
    }

    private KoulutusmoduuliToteutus createKomotoWithHakukohdesWithoutAjankohtas() {
        KoulutusmoduuliToteutus komoto = createKomotoWithEmptyHakukohdes();

        Hakukohde hakukohde = new Hakukohde();
        Haku haku = new Haku();
        haku.setHakutapaUri("hakutapa_02#1");
        haku.setKoulutuksenAlkamisVuosi(2016);
        haku.setKoulutuksenAlkamiskausiUri("kausi_k");
        hakukohde.setHaku(haku);
        hakukohde.setTila(TarjontaTila.LUONNOS);
        komoto.addHakukohde(hakukohde);

        return komoto;
    }

    private KoulutusmoduuliToteutus createKomotoWithHakukohdesWithJatkuvatHaku() {
        KoulutusmoduuliToteutus komoto = createKomotoWithEmptyHakukohdes();

        Hakukohde hakukohde = new Hakukohde();
        Haku haku = new Haku();
        haku.setHakutapaUri("hakutapa_03#1");
        haku.setKoulutuksenAlkamisVuosi(2014);
        haku.setKoulutuksenAlkamiskausiUri("");
        hakukohde.setHaku(haku);
        hakukohde.setTila(TarjontaTila.LUONNOS);
        komoto.addHakukohde(hakukohde);

        return komoto;
    }

    private KoulutusmoduuliToteutus createKomotoWithEmptyHakukohdes() {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid("1.2.3");
        return komoto;
    }

    private KoulutusV1RDTO createNewKoulutusDTO() {
        KoulutusV1RDTO koulutusDTO = new KoulutusKorkeakouluV1RDTO();
        koulutusDTO.setTila(TarjontaTila.LUONNOS);
        koulutusDTO.setKoulutuskoodi(new KoodiV1RDTO(){{
            setUri("koulutus_1");
            setVersio(1);
        }});
        return koulutusDTO;
    }

    private KoulutusV1RDTO createExistingKoulutusDTO() {
        KoulutusV1RDTO koulutusDTO = new KoulutusKorkeakouluV1RDTO();
        koulutusDTO.setOid("1.2.3");
        koulutusDTO.setOrganisaatio(new OrganisaatioV1RDTO("1.2.3.4", "", null));
        koulutusDTO.setTila(TarjontaTila.LUONNOS);
        koulutusDTO.setKoulutuksenAlkamisvuosi(2016);
        koulutusDTO.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_k", 1, "Kevät"));
        return koulutusDTO;
    }

    private BaseMatcher<ErrorV1RDTO> getErrorDTOElementMatcher(final String key) {
        return new BaseMatcher<ErrorV1RDTO>() {
            @Override
            public boolean matches(Object o) {
                return ((ErrorV1RDTO) o).getErrorMessageKey().equals(key);
            }

            public void describeTo(Description description) {
            }
        };
    }
}
