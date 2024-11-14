package fi.vm.sade.tarjonta.service.impl.resources.v1;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.V1TestHelper.TARJOAJA1;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.V1TestHelper.mockKoodi;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.KomoV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.KoodiService;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@Service
@ActiveProfiles("embedded-solr")
public class AmmattitutkintoV1Test {

  @Autowired OrganisaatioService organisaatioService;

  @Autowired PermissionChecker permissionChecker;

  @Autowired OidService oidService;

  @Autowired OidServiceMock oidServiceMock;

  @Autowired KoulutusV1Resource koulutusResourceV1;

  @Autowired V1TestHelper helper;

  @Autowired KomoV1Resource komoV1Resource;

  @Autowired KoulutusmoduuliDAO koulutusmoduuliDAO;

  @Autowired KoodiService koodiService;

  @Autowired KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;

  private static final String KOULUTUSKOODI = "koulutus_ammattitutkinto";
  private static final String KOMO_OID = "ammattitutkintoKomoOid";
  private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

  @Before
  public void init() {
    helper.init();

    Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(KOMO_OID);
    if (komo == null) {
      komo = new Koulutusmoduuli();
      komo.setOid(KOMO_OID);
      komo.setModuuliTyyppi(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO);
      komo.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATTITUTKINTO);
      koulutusmoduuliDAO.insert(komo);
    }

    List<ModuuliTuloksetV1RDTO> komos = new ArrayList<ModuuliTuloksetV1RDTO>();
    komos.add(new ModuuliTuloksetV1RDTO(komo.getOid(), null, null, null, null, null, null));
    when(komoV1Resource.searchModule(
            ToteutustyyppiEnum.AMMATTITUTKINTO,
            KoulutusmoduuliTyyppi.TUTKINTO,
            KOULUTUSKOODI,
            null,
            null))
        .thenReturn(new ResultV1RDTO<List<ModuuliTuloksetV1RDTO>>(komos));

    when(koodiService.searchKoodis(
            new SearchKoodisCriteriaType() {
              {
                getKoodiUris().add(ToteutustyyppiEnum.AMMATTITUTKINTO.uri());
              }
            }))
        .thenReturn(
            Lists.<KoodiType>newArrayList(
                new KoodiType() {
                  {
                    setKoodiUri(ToteutustyyppiEnum.AMMATTITUTKINTO.uri());
                    setVersio(1);
                  }
                }));

    when(koodiService.listKoodiByRelation(
            new KoodiUriAndVersioType() {
              {
                this.setKoodiUri(ToteutustyyppiEnum.AMMATTITUTKINTO.uri());
                this.setVersio(1);
              }
            },
            false,
            SuhteenTyyppiType.SISALTYY))
        .thenReturn(
            Lists.newArrayList(
                mockKoodi(KoulutusImplicitDataPopulator.KOULUTUSLAJI),
                mockKoodi(KoulutusImplicitDataPopulator.POHJAKOULUTUSVAATIMUS_TOINEN_ASTE)));

    List<KoodiType> mockedCodes = new ArrayList<KoodiType>();
    mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.KOULUTUSALAOPH2002));
    mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.OPINTOALAOPH2002));
    when(koodiService.listKoodiByRelation(
            new KoodiUriAndVersioType() {
              {
                this.setKoodiUri(KOULUTUSKOODI);
                this.setVersio(1);
              }
            },
            false,
            SuhteenTyyppiType.SISALTYY))
        .thenReturn(mockedCodes);
  }

  @Test
  @Transactional
  public void testCreatePuutteellinen() throws OIDCreationException {
    when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(oidServiceMock.getOid());
    when(oidService.get(TarjontaOidType.KOMO)).thenReturn(oidServiceMock.getOid());
    AmmattitutkintoV1RDTO dto = baseDto();
    dto.setTila(TarjontaTila.PUUTTEELLINEN);

    ResultV1RDTO<AmmattitutkintoV1RDTO> result =
        (ResultV1RDTO<AmmattitutkintoV1RDTO>)
            koulutusResourceV1.postKoulutus(dto, request).getEntity();
    assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
  }

  private static AmmattitutkintoV1RDTO baseDto() {
    AmmattitutkintoV1RDTO dto = new AmmattitutkintoV1RDTO();
    dto.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA1));
    dto.setTila(TarjontaTila.LUONNOS);
    dto.setKoulutuksenAlkamisvuosi(2015);
    dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_k", 1, "Kevät"));
    dto.setKoulutuskoodi(new KoodiV1RDTO(KOULUTUSKOODI, 1, ""));

    return dto;
  }
}
