package fi.vm.sade.tarjonta.service.search.resolver;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.TestMockBase;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;


public class OppilaitostyyppiResolverTest extends TestMockBase {

    @InjectMocks
    private OppilaitostyyppiResolver oppilaitostyyppiResolver;

    @Test
    public void thatOppilaitostyyppiIsResolved() {
        OrganisaatioPerustieto org = createOrganisaatioPerustietoWithOppilaitostyyppi();

        String oppilaitostyyppi = oppilaitostyyppiResolver.resolve(org);

        assertEquals("oppilaitostyyppi_42", oppilaitostyyppi);
    }

    @Test
    public void thatOppilaitostyyppiIsResolvedFromParentOrg() {
        OrganisaatioPerustieto orgWithoutOppilaitostyyppi = createOrganisaatioPerustietoWithoutOppilaitostyyppi();
        OrganisaatioPerustieto orgWithOppilaitostyyppi = createOrganisaatioPerustietoWithOppilaitostyyppi();

        when(organisaatioSearchService.findByOidSet(new HashSet<String>(Arrays.asList("1.2.3"))))
                .thenReturn(Arrays.asList(orgWithoutOppilaitostyyppi));
        when(organisaatioSearchService.findByOidSet(new HashSet<String>(Arrays.asList("4.5.6"))))
                .thenReturn(Arrays.asList(orgWithOppilaitostyyppi));

        String oppilaitostyyppi = oppilaitostyyppiResolver.resolve(orgWithoutOppilaitostyyppi);

        assertEquals("oppilaitostyyppi_42", oppilaitostyyppi);
    }

    private OrganisaatioPerustieto createOrganisaatioPerustietoWithoutOppilaitostyyppi() {
        OrganisaatioPerustieto organisaatioPerustieto = new OrganisaatioPerustieto();
        organisaatioPerustieto.setOid("1.2.3");
        organisaatioPerustieto.setNimi("fi", "Organisaatio");
        organisaatioPerustieto.setKotipaikkaUri("kotipaikka");
        organisaatioPerustieto.setParentOidPath("/0.0.0/4.5.6/");
        return organisaatioPerustieto;
    }

    private OrganisaatioPerustieto createOrganisaatioPerustietoWithOppilaitostyyppi() {
        OrganisaatioPerustieto organisaatioPerustieto = new OrganisaatioPerustieto();
        organisaatioPerustieto.setOid("4.5.6");
        organisaatioPerustieto.setNimi("fi", "Organisaatio");
        organisaatioPerustieto.setKotipaikkaUri("kotipaikka");
        organisaatioPerustieto.setOppilaitostyyppi("oppilaitostyyppi_42");
        return organisaatioPerustieto;
    }
}
