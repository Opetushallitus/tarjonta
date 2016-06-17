package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.SecurityAwareTestBase;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KorkeakouluOpintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.search.it.TarjontaSearchServiceTest;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@Transactional
public class KoulutusResourceImplV1CopyTest extends SecurityAwareTestBase {
    @Autowired
    @Spy
    private OrganisaatioService organisaatioService;

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Before
    @Override
    public void before() {
        MockitoAnnotations.initMocks(this);

        OrganisaatioRDTO organisaatioDTO = new OrganisaatioRDTO();
        organisaatioDTO.setOid("1.2.3.4");
        organisaatioDTO.setOppilaitosTyyppiUri(OrganisaatioService.OrganisaatioTyyppi.OPPILAITOS.value());
        organisaatioDTO.getTyypit().add(OrganisaatioService.OrganisaatioTyyppi.OPPILAITOS.value());

        Mockito.doReturn(organisaatioDTO).when(organisaatioService).findByOid(Matchers.anyString());

        stubKorkeakoulutusConvertKoodis(koodiService);

        Mockito.when(tarjontaKoodistoHelper.getKoodiByUri(Matchers.anyString())).thenReturn(
                new KoodiType(){{
                    setKoodiArvo("arvo");
                    setKoodiUri("uri");
                    setVersio(1);
                }}
        );

        super.before();
    }

    public static void stubKorkeakoulutusConvertKoodis(KoodiService koodiService) {
        stubKoodi(koodiService, "Oppilaitos", "FI");
        stubKoodi(koodiService, "koulutusaste-uri", "FI");
        stubKoodi(koodiService, "koulutustyyppi_3", "FI");
        stubKoodi(koodiService, "koulutusala", "FI");
        stubKoodi(koodiService, "koulutus", "FI");
        stubKoodi(koodiService, "opintoala", "FI");
        stubKoodi(koodiService, "tutkintonimike", "FI");
        stubKoodi(koodiService, "opetuskieli", "FI");
        stubKoodi(koodiService, "http://opetusmuodot/lahiopetus", "FI");

        KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri("koulutusaste");
        koodiType.setVersio(0);
        koodiType.setKoodiArvo("koulutusaste");
        KoodistoItemType koodistoType = new KoodistoItemType();
        koodistoType.setKoodistoUri("${koodisto-uris.koulutusaste}");
        koodiType.setKoodisto(koodistoType);
        Mockito.stub(koodiService.listKoodiByRelation(Matchers.any(KoodiUriAndVersioType.class), Matchers.anyBoolean(), Matchers.any(SuhteenTyyppiType.class)))
                .toReturn(Lists.newArrayList(koodiType));
    }

    public static void stubKoodi(KoodiService koodiService, String uri, String arvo) {
        List<KoodiType> vastaus = Lists.newArrayList(TarjontaSearchServiceTest.getKoodiType(uri, arvo));
        Mockito.stub(
                koodiService.searchKoodis(
                        Matchers.argThat(new TarjontaSearchServiceTest.KoodistoCriteriaMatcher(uri))))
                .toReturn(vastaus);
    }

    public static Koulutusmoduuli getKorkeakoulutusKomo(TarjontaFixtures fixtures) {
        Koulutusmoduuli komo = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo.setKoulutusasteUri("koulutusaste");
        komo.setTila(TarjontaTila.LUONNOS);
        komo.setKoulutusalaUri("koulutusala");
        komo.setKoulutusUri("koulutus");
        komo.setOpintoalaUri("opintoala");
        komo.setTutkintonimikeUri("tutkintonimike");
        return komo;
    }

    public static KoulutusmoduuliToteutus getKorkeakoulutusKomoto(TarjontaFixtures fixtures, Koulutusmoduuli komo) {
        KoulutusmoduuliToteutus komoto = fixtures.createTutkintoOhjelmaToteutus("1.2.3");
        komoto.setTarjoaja("TEST_TARJOAJA");
        komoto.setKoulutusmoduuli(komo);
        komoto.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);
        KoodistoUri opetuskieli = new KoodistoUri("opetuskieli");
        komoto.setOpetuskieli(Sets.newHashSet(opetuskieli));
        return komoto;
    }

    @Test
    public void thatKorkeakoulutusIsCopied() {
        try {
            Mockito.stub(oidService.get(TarjontaOidType.KOMO)).toReturn("new-komo-oid");
            Mockito.stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn("new-komoto-oid");
        } catch (OIDCreationException e1) {
        }

        Koulutusmoduuli originalKomo = koulutusmoduuliDAO.insert(getKorkeakoulutusKomo(fixtures));
        KoulutusmoduuliToteutus originalKomoto = koulutusmoduuliToteutusDAO.insert(getKorkeakoulutusKomoto(fixtures, originalKomo));

        List<Koulutusmoduuli> komos = koulutusmoduuliDAO.findAllKomos();
        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findAll();
        int komoCountBeforeCopy = komos.size();
        int komotoCountBeforeCopy = komotos.size();

        KoulutusmoduuliToteutus newKomoto = koulutusUtilService.copyKomotoAndKomo(originalKomoto, originalKomoto.getTarjoaja(), null, null, false, KoulutusKorkeakouluV1RDTO.class);

        komos = koulutusmoduuliDAO.findAllKomos();
        komotos = koulutusmoduuliToteutusDAO.findAll();

        assertEquals(komoCountBeforeCopy + 1, komos.size());
        assertEquals(komotoCountBeforeCopy + 1, komotos.size());

        assertEquals("new-komoto-oid", newKomoto.getOid());
        assertEquals("new-komo-oid", newKomoto.getKoulutusmoduuli().getOid());

        assertEquals(originalKomo.getKoulutuksenTunnisteOid(), newKomoto.getKoulutusmoduuli().getKoulutuksenTunnisteOid());
    }

    public static Koulutusmoduuli getKorkeakouluopintoKomo(TarjontaFixtures fixtures) {
        Koulutusmoduuli komo = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS);
        komo.setKoulutusasteUri("koulutusaste");
        komo.setTila(TarjontaTila.LUONNOS);
        komo.setKoulutusalaUri("koulutusala");
        komo.setKoulutusUri("koulutus");
        return komo;
    }

    public static KoulutusmoduuliToteutus getKorkeakouluopintoKomoto(TarjontaFixtures fixtures, Koulutusmoduuli komo) {
        KoulutusmoduuliToteutus komoto = fixtures.createTutkintoOhjelmaToteutus("1.2.3");
        komoto.setTarjoaja("TEST_TARJOAJA");

        KoulutusOwner owner = new KoulutusOwner();
        owner.setOwnerOid("jarjestaja1");
        owner.setOwnerType(KoulutusOwner.JARJESTAJA);
        komoto.getOwners().add(owner);

        komoto.setKoulutusmoduuli(komo);
        komoto.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUOPINTO);
        KoodistoUri opetuskieli = new KoodistoUri("opetuskieli");
        komoto.setOpetuskieli(Sets.newHashSet(opetuskieli));
        return komoto;
    }

    @Test
    public void thatKorkeakouluopintoIsCopied() {
        try {
            Mockito.stub(oidService.get(TarjontaOidType.KOMO)).toReturn("korkeakouluopinto-komo-oid");
            Mockito.stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn("korkeakouluopinto-komoto-oid");
        } catch (OIDCreationException e1) {
        }

        Koulutusmoduuli originalKomo = koulutusmoduuliDAO.insert(getKorkeakouluopintoKomo(fixtures));
        KoulutusmoduuliToteutus originalKomoto = koulutusmoduuliToteutusDAO.insert(getKorkeakouluopintoKomoto(fixtures, originalKomo));

        List<Koulutusmoduuli> komos = koulutusmoduuliDAO.findAllKomos();
        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findAll();
        int komoCountBeforeCopy = komos.size();
        int komotoCountBeforeCopy = komotos.size();

        KoulutusmoduuliToteutus newKomoto = koulutusUtilService.copyKomotoAndKomo(originalKomoto, originalKomoto.getTarjoaja(), null, null, false, KorkeakouluOpintoV1RDTO.class);

        komos = koulutusmoduuliDAO.findAllKomos();
        komotos = koulutusmoduuliToteutusDAO.findAll();

        assertEquals(komoCountBeforeCopy + 1, komos.size());
        assertEquals(komotoCountBeforeCopy + 1, komotos.size());

        assertEquals("korkeakouluopinto-komoto-oid", newKomoto.getOid());
        assertEquals("korkeakouluopinto-komo-oid", newKomoto.getKoulutusmoduuli().getOid());

        assertEquals(originalKomo.getKoulutuksenTunnisteOid(), newKomoto.getKoulutusmoduuli().getKoulutuksenTunnisteOid());

        boolean matchingJarjestajaFound = false;
        for (KoulutusOwner owner : newKomoto.getOwners()) {
            if (owner.getOwnerType().equals(KoulutusOwner.JARJESTAJA) && owner.getOwnerOid().equals("jarjestaja1")) {
                matchingJarjestajaFound = true;
                break;
            }
        }
        assertTrue(matchingJarjestajaFound);
    }

}
