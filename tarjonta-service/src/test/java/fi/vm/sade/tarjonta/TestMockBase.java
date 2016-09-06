package fi.vm.sade.tarjonta;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.dao.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.ConverterV1;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusImplicitDataPopulator;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.KoodistoValidator;
import fi.vm.sade.tarjonta.service.search.resolver.OppilaitostyyppiResolver;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
abstract public class TestMockBase {
    @Mock
    protected KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Mock
    protected HakukohdeDAO hakukohdeDAO;

    @Mock
    protected OidService oidService;

    @Mock
    protected KoodiService koodiService;

    @Mock
    protected PublicationDataService publicationDataService;

    @Mock
    protected OrganisaatioSearchService organisaatioSearchService;

    @Mock
    protected OppilaitostyyppiResolver oppilaitostyyppiResolver;

    @Mock
    protected KoodistoValidator koodistoValidator;

    @Mock
    protected PermissionChecker permissionChecker;

    protected TarjontaKoodistoHelper tarjontaKoodistoHelper = new TarjontaKoodistoHelper();

    @Mock
    protected ConverterV1 converterV1;

    @Mock
    protected HakuDAO hakuDAO;

    @Mock
    protected KoulutusPermissionService koulutusPermissionService;

    @Mock
    protected KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Mock
    protected ConverterV1 converter;

    @Mock
    protected KuvausDAO kuvausDAO;

    @Mock
    protected OrganisaatioService organisaatioService;

    @Mock
    protected ContextDataService contextDataService;
}
