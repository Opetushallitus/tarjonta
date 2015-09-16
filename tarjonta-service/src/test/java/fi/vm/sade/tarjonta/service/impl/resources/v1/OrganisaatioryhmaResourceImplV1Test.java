package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.generic.service.exception.NotAuthorizedException;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Ryhmaliitos;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.RyhmaliitosV1RDTO;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganisaatioryhmaResourceImplV1Test {

    @Mock
    private HakukohdeDAO hakukohdeDAO;

    @Mock
    private PermissionChecker permissionChecker;

    @InjectMocks
    private OrganisaatioryhmaResourceImplV1 organisaatioryhmaResource;

    @Test
    public void thatRyhmaliitosIsAdded() {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("1.1.1");
        when(hakukohdeDAO.findHakukohdeByOid("1.1.1")).thenReturn(hakukohde);

        ResultV1RDTO result = organisaatioryhmaResource.addRyhmaliitokset("1.2.3", getRyhmaliitosDTOs());

        verify(hakukohdeDAO).update(argThat(new AddRyhmaliitoksetMatcher()));
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    }

    @Test
    public void thatRyhmaliitosIsUpdated() {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("1.1.1");

        Ryhmaliitos ryhmaliitos = new Ryhmaliitos();
        ryhmaliitos.setRyhmaOid("1.2.3");
        ryhmaliitos.setPrioriteetti(0);
        ryhmaliitos.setHakukohde(hakukohde);
        hakukohde.addRyhmaliitos(ryhmaliitos);

        when(hakukohdeDAO.findHakukohdeByOid("1.1.1")).thenReturn(hakukohde);

        ResultV1RDTO result = organisaatioryhmaResource.addRyhmaliitokset("1.2.3", getRyhmaliitosDTOs());

        verify(hakukohdeDAO).update(argThat(new AddRyhmaliitoksetMatcher()));
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    }

    @Test
    public void thatRyhmaliitosIsRemoved() {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("1.1.1");

        Ryhmaliitos ryhmaliitos = new Ryhmaliitos();
        ryhmaliitos.setRyhmaOid("1.2.3");
        ryhmaliitos.setHakukohde(hakukohde);
        hakukohde.addRyhmaliitos(ryhmaliitos);

        when(hakukohdeDAO.findHakukohdeByOid("1.1.1")).thenReturn(hakukohde);

        ResultV1RDTO result = organisaatioryhmaResource.removeRyhmaliitokset("1.2.3", new HashSet<String>(Arrays.asList("1.1.1")));

        verify(hakukohdeDAO).update(argThat(new RemoveRyhmaliitoksetMatcher()));
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    }

    @Test(expected = NotAuthorizedException.class)
    public void thatUpdateFailsWithoutPermissions() {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("1.1.1");

        when(hakukohdeDAO.findHakukohdeByOid("1.1.1")).thenReturn(hakukohde);
        doThrow(new NotAuthorizedException()).
                when(permissionChecker).checkUpdateHakukohdeAndIgnoreParametersWhileChecking("1.1.1");

        organisaatioryhmaResource.addRyhmaliitokset("1.2.3", getRyhmaliitosDTOs());
    }

    @Test(expected = NotAuthorizedException.class)
    public void thatRemoveFailsWithoutPermissions() {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("1.1.1");

        Ryhmaliitos ryhmaliitos = new Ryhmaliitos();
        ryhmaliitos.setRyhmaOid("1.2.3");
        ryhmaliitos.setHakukohde(hakukohde);
        hakukohde.addRyhmaliitos(ryhmaliitos);

        when(hakukohdeDAO.findHakukohdeByOid("1.1.1")).thenReturn(hakukohde);
        doThrow(new NotAuthorizedException()).
                when(permissionChecker).checkUpdateHakukohdeAndIgnoreParametersWhileChecking("1.1.1");

        organisaatioryhmaResource.removeRyhmaliitokset("1.2.3", new HashSet<String>(Arrays.asList("1.1.1")));
    }

    private Set<RyhmaliitosV1RDTO> getRyhmaliitosDTOs() {
        Set<RyhmaliitosV1RDTO> ryhmaliitosDTOs = new HashSet<RyhmaliitosV1RDTO>();

        RyhmaliitosV1RDTO ryhmaliitosDTO = new RyhmaliitosV1RDTO();
        ryhmaliitosDTO.setHakukohdeOid("1.1.1");
        ryhmaliitosDTO.setPrioriteetti(1);
        ryhmaliitosDTOs.add(ryhmaliitosDTO);

        return ryhmaliitosDTOs;
    }

    class AddRyhmaliitoksetMatcher extends BaseMatcher<Hakukohde> {

        private String failure;

        @Override
        public boolean matches(Object argument) {
            Hakukohde hakukohde = (Hakukohde) argument;
            if (hakukohde.getRyhmaliitokset().size() != 1) {
                failure = "hakukohde.getRyhmaliitokset().size() != 1";
                return false;
            }

            Ryhmaliitos ryhmaliitos = hakukohde.getRyhmaliitokset().iterator().next();
            if (!ryhmaliitos.getHakukohde().getOid().equals("1.1.1")) {
                failure = "ryhmaliitos.getHakukohde().getOid().equals(1.1.1)";
                return false;
            }
            if (!ryhmaliitos.getRyhmaOid().equals("1.2.3")) {
                failure = "ryhmaliitos.getRyhmaOid().equals(1.2.3)";
                return false;
            }
            if (ryhmaliitos.getPrioriteetti() != 1) {
                failure = "ryhmaliitos.getPrioriteetti() != 1";
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(failure);
        }

    }

    class RemoveRyhmaliitoksetMatcher extends BaseMatcher<Hakukohde> {

        private String failure;

        @Override
        public boolean matches(Object argument) {
            Hakukohde hakukohde = (Hakukohde) argument;
            if (!hakukohde.getRyhmaliitokset().isEmpty()) {
                failure = "hakukohde.getRyhmaliitokset().isEmpty()";
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(failure);
        }

    }
}
