package fi.vm.sade.tarjonta.service.search.resolver;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.TestMockBase;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
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

        when(organisaatioService.findByUsingOrganisaatioCache(any())).thenReturn(Arrays.asList(orgWithOppilaitostyyppi));

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
