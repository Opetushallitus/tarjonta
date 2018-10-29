package fi.vm.sade.tarjonta;

import fi.vm.sade.tarjonta.service.business.IndexService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.YhdenPaikanSaantoBuilder;
import fi.vm.sade.tarjonta.shared.KoodiService;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.security.OrganisationHierarchyAuthorizer;
import fi.vm.sade.tarjonta.dao.*;
import fi.vm.sade.tarjonta.dao.impl.KoulutusSisaltyvyysDAOImpl;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusUtilService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCopyProcess;
import fi.vm.sade.tarjonta.service.resources.HakuResource;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.LinkingV1Resource;
import fi.vm.sade.tarjonta.service.search.HakukohdeSearchService;
import fi.vm.sade.tarjonta.service.search.KoulutusSearchService;
import fi.vm.sade.tarjonta.service.search.SolrServerFactory;
import fi.vm.sade.tarjonta.service.tasks.KoulutusPermissionSynchronizer;
import fi.vm.sade.tarjonta.shared.ParameterServices;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
abstract public class TestUtilityBase {
    @Autowired
    protected TarjontaFixtures tarjontaFixtures;

    @Autowired
    protected KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    protected KoulutusSisaltyvyysDAOImpl koulutusSisaltyvyysDao;

    @Autowired
    protected LinkingV1Resource linkingResource;

    @Autowired
    protected TarjontaFixtures fixtures;

    @Autowired
    protected KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    protected HakukohdeDAO hakukohdeDAO;

    @Autowired
    protected HakuDAO hakuDAO;

    @Autowired
    protected KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;

    @Autowired
    protected KoodiService koodiService;

    @Autowired
    protected OidService oidService;

    @Autowired
    protected HakuDAO hakuDao;

    @Autowired
    protected HakukohdeV1Resource hakukohdeResource;

    @Autowired
    protected HakukohdeDAO hakukohdeDao;

    @Autowired
    protected KoulutusPermissionDAO koulutusPermissionDAO;

    @Autowired
    protected KoulutusPermissionSynchronizer koulutusPermissionSynchronizer;

    @Autowired
    protected KoulutusPermissionService koulutusPermissionService;

    @Autowired
    protected HakuResource hakuResource;

    @Autowired
    protected TarjontaPublicService service;

    @Autowired
    protected TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Autowired
    protected OrganisationHierarchyAuthorizer authorizer;

    @Autowired
    protected KoulutusUtilService koulutusUtilService;

    @Autowired
    protected OrganisaatioService organisaatioService;

    @Autowired
    protected TarjontaPublicService publicService;

    @Autowired
    protected TarjontaAdminService adminService;

    @Autowired
    protected ParameterServices parameterService;

    @Autowired
    protected KoulutusV1Resource koulutusResource;

    @Autowired
    protected SolrServerFactory solrServerFactory;

    @Autowired
    protected HakukohdeSearchService hakukohdeSearchService;

    @Autowired
    protected KoulutusSearchService koulutusSearchService;

    @Autowired
    protected KoulutusSisaltyvyysDAO KoulutusSisaltyvyysDAO;

    @Autowired
    protected OppiaineDAO oppiaineDAO;

    @Autowired
    protected KoulutusmoduuliToteutusDAO komotoDao;

    @Autowired
    protected KuvausDAO kuvausDAO;

    @Autowired
    protected MassCopyProcess copyProcess;

    @Autowired
    protected KoulutusSisaltyvyysDAO sisaltyvyysDAO;

    @Autowired
    protected KoulutusmoduuliDAO koulutusDAO;

    @Autowired
    protected MonikielinenMetadataDAO monikielinenMetadataDAO;

    @Autowired
    protected PublicationDataService dataService;

    @Autowired
    protected KoulutusBusinessService koulutusBusinessService;

    @Autowired
    protected PermissionChecker permissionChecker;

    @Autowired
    protected ParameterServices parameterServices;

    @Autowired
    protected YhdenPaikanSaantoBuilder yhdenPaikanSaantoBuilder;

    @Autowired
    @Qualifier("indexservice")
    protected IndexService indexService;
}
