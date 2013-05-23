package fi.vm.sade.tarjonta.data.tarjontauploader;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.KoodiBaseSearchCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.*;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TarjontaHandlerTest {
    private TarjontaPublicService tarjontaPublicService;
    private TarjontaAdminService tarjontaAdminService;
    private KoodiService koodiService;
    private OrganisaatioService organisaatioService;

    private TarjontaHandler handler;

    @Before
    public void setup() {
        tarjontaPublicService = mock(TarjontaPublicService.class);

        tarjontaAdminService = mock(TarjontaAdminService.class);

        koodiService = mock(KoodiService.class);
        final SearchKoodisByKoodistoCriteriaType searchKoodi = new SearchKoodisByKoodistoCriteriaType();
        searchKoodi.setKoodistoUri("hakukohteet");
        searchKoodi.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.LATEST);
        final KoodiBaseSearchCriteriaType koodi = new KoodiBaseSearchCriteriaType();
        koodi.setKoodiArvo("892");
        searchKoodi.setKoodiSearchCriteria(koodi);
        final KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri("hakukohteet_892");
        koodiType.setVersio(1);
        when(koodiService.searchKoodisByKoodisto(searchKoodi)).thenReturn(Collections.singletonList(koodiType));

        organisaatioService = mock(OrganisaatioService.class);
        final OrganisaatioPerustietoType oppilaitos = new OrganisaatioPerustietoType();
        oppilaitos.setOid("1.3.3");
        when(organisaatioService.searchBasicOrganisaatios(any(OrganisaatioSearchCriteriaDTO.class))).thenReturn(Collections.singletonList(oppilaitos));
        final OrganisaatioDTO toimipiste = new OrganisaatioDTO();
        final OsoiteDTO osoite = new OsoiteDTO();
        osoite.setOsoite("Katu 1");
        osoite.setPostinumero("00100");
        osoite.setPostitoimipaikka("Helsinki");
        osoite.setOsoiteTyyppi(OsoiteTyyppi.POSTI);
        toimipiste.getYhteystiedot().add(osoite);
        toimipiste.setOpetuspisteenJarjNro("01");
        when(organisaatioService.findChildrenTo("1.3.3")).thenReturn(Collections.singletonList(toimipiste));

        handler = new TarjontaHandler(tarjontaPublicService, tarjontaAdminService, koodiService, organisaatioService);
    }

    @Test
    public void testAddKoulutusmoduuliToteutusSuccessfully() {
        handler.addKoulutus(new Koulutus());
    }

    @Test
    public void testAddHakukohdeSuccessfully() {
        final Hakukohde hakukohde = new Hakukohde();
        hakukohde.setAlkamiskausi("2014");
        hakukohde.setAlkamiskausi("K");
        hakukohde.setHakutyyppi("Varsinainen haku");
        hakukohde.setYhkoulu("0115");
        hakukohde.setOppilaitosnumero("01164");
        hakukohde.setToimipisteJno("01");
        hakukohde.setHakukohdekoodi("892");
        hakukohde.setValinnanAloituspaikka(22);
        hakukohde.setAloituspaikka(20);
        hakukohde.setValintakoe("T");

        handler.addHakukohde(hakukohde, "1.2.3.4");

        // tarkista hakukohteen arvot rajapinnasta
        final ArgumentCaptor<HakukohdeTyyppi> hakukohdeTyyppi = ArgumentCaptor.forClass(HakukohdeTyyppi.class);
        verify(tarjontaAdminService, times(1)).lisaaHakukohde(hakukohdeTyyppi.capture());
        assertEquals("1.2.246.562.5.01164_01_892_0115", hakukohdeTyyppi.getValue().getOid());
        assertEquals("1.2.3.4", hakukohdeTyyppi.getValue().getHakukohteenHakuOid());
        assertEquals(new Integer(22), hakukohdeTyyppi.getValue().getValinnanAloituspaikat());
        assertEquals(new Integer(20), hakukohdeTyyppi.getValue().getAloituspaikat());
        assertEquals("hakukohteet_892#1", hakukohdeTyyppi.getValue().getHakukohdeNimi());

        // tarkista kutsutaanko valintokokeen lisäystä
        verify(tarjontaAdminService, times(1)).tallennaValintakokeitaHakukohteelle(eq("1.2.246.562.5.01164_01_892_0115"), anyList());
    }
}
