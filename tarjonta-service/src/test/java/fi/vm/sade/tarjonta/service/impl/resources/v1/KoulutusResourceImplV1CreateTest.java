package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class KoulutusResourceImplV1CreateTest {

    @Mock
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Mock
    private PublicationDataService publicationDataService;

    @InjectMocks
    private KoulutusResourceImplV1 koulutusResourceV1;

    @Test
    public void thatAjankohtaIsValidWithNewKoulutus() {
        KoulutusV1RDTO koulutusDTO = createNewKoulutusDTO();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = koulutusResourceV1.postKoulutus(koulutusDTO);

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatAjankohtaIsValidWithEmptyHakukohdes() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithEmptyHakukohdes());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = koulutusResourceV1.postKoulutus(koulutusDTO);

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatAjankohtaIsValidWithHakukohdesThatDoNotContainAjankohtaInformation() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithoutAjankohtas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = koulutusResourceV1.postKoulutus(koulutusDTO);

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatAjankohtaIsValidWithHakukohdesWithValidAjankohdas() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithValidAjankohta();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = koulutusResourceV1.postKoulutus(koulutusDTO);

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatAjankohtaIsInvalidWithWrongSeason() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithInvalidAjankohta();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = koulutusResourceV1.postKoulutus(koulutusDTO);

        assertThat(resultDTO.getErrors(), hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla")));
    }

    @Test
    public void thatAjankohtaIsInvalidWithHakukohdesWithOneValidAndOneInvalidAjankohta() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithOneValidAndOneInvalidAjankohta();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = koulutusResourceV1.postKoulutus(koulutusDTO);

        assertThat(resultDTO.getErrors(), hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla")));
    }

    @Test
    public void thatKausiAndYearComboIsValidWithHakukohdesWithValidAjankohta() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithValidKausiAndYear();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = koulutusResourceV1.postKoulutus(koulutusDTO);

        assertThat(resultDTO.getErrors(), not(hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla"))));
    }

    @Test
    public void thatKausiAndYearComboIsInvalidWithHakukohdesWithInvalidAjankohta() {
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3")).thenReturn(createKomotoWithHakukohdesWithValidAjankohdas());
        when(publicationDataService.isValidStatusChange(any(Tila.class))).thenReturn(true);

        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTOWithInvalidKausiAndYear();

        ResultV1RDTO<KoulutusV1RDTO> resultDTO = koulutusResourceV1.postKoulutus(koulutusDTO);

        assertThat(resultDTO.getErrors(), hasItem(getErrorDTOElementMatcher("koulutus.error.alkamispvm.ajankohtaerikuinhaulla")));
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
        koulutusDTO.getKoulutuksenAlkamisPvms().add(datetime.toDate());
        return koulutusDTO;
    }

    private KoulutusV1RDTO createExistingKoulutusDTOWithOneValidAndOneInvalidAjankohta() {
        KoulutusV1RDTO koulutusDTO = createExistingKoulutusDTO();

        DateTime datetime = new DateTime();
        datetime = datetime.withYear(2015);
        datetime = datetime.withMonthOfYear(8);
        koulutusDTO.getKoulutuksenAlkamisPvms().add(datetime.toDate());

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
        koulutusDTO.getKoulutuksenAlkamisPvms().add(datetime.toDate());
        return koulutusDTO;
    }

    private KoulutusmoduuliToteutus createKomotoWithHakukohdesWithValidAjankohdas() {
        KoulutusmoduuliToteutus komoto = createKomotoWithEmptyHakukohdes();

        Haku haku = new Haku();
        haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
        haku.setKoulutuksenAlkamisVuosi(2015);

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
        return koulutusDTO;
    }

    private KoulutusV1RDTO createExistingKoulutusDTO() {
        KoulutusV1RDTO koulutusDTO = new KoulutusKorkeakouluV1RDTO();
        koulutusDTO.setOid("1.2.3");
        koulutusDTO.setTila(TarjontaTila.LUONNOS);
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
