package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.ImmutableMap;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class KoulutusResourceImplV1CreateTest extends TestMockBase {

    @InjectMocks
    private KoulutusResourceImplV1 koulutusResourceV1;

    @Mock
    private KoulutusImplicitDataPopulator implicitDataPopulator;

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);


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

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO, request).getEntity();

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.alkamiskauttamuutettu"))));
    }

    @Test
    public void thatAjankohtaIsValidWithEmptyHakukohdes() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithEmptyHakukohdes());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO, request).getEntity();

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.alkamiskauttamuutettu"))));
    }

    @Test
    public void thatAjankohtaIsValidIfNotChanged() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO, request).getEntity();

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.alkamiskauttamuutettu"))));
    }

    @Test
    public void thatAjankohtaIsInvalidIfChanged() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithInvalidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(koulutusDTO, request).getEntity();

        assertThat(resultDTO.getErrors(), hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.alkamiskauttamuutettu")));
    }

    private KoulutusmoduuliToteutus createKomotoWithHakukohdesWithValidAjankohdas() {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid("1.2.3");
        komoto.setAlkamisVuosi(2016);
        komoto.setAlkamiskausiUri("kausi_k#1");

        Haku haku = new Haku();
        haku.setKoulutuksenAlkamiskausiUri("kausi_k#1");
        haku.setKoulutuksenAlkamisVuosi(2016);
        haku.setHakutapaUri("hakutapa_02#1");

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setTila(TarjontaTila.LUONNOS);
        hakukohde.setHaku(haku);
        komoto.addHakukohde(hakukohde);

        return komoto;
    }

    private KoulutusmoduuliToteutus createKomotoWithHakukohdesWithInvalidAjankohdas() {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid("1.2.3");
        komoto.setAlkamisVuosi(2015);
        komoto.setAlkamiskausiUri("kausi_s#1");

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

    private KoulutusmoduuliToteutus createKomotoWithEmptyHakukohdes() {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid("1.2.3");
        komoto.setAlkamisVuosi(2015);
        komoto.setAlkamiskausiUri("kausi_s#1");
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
