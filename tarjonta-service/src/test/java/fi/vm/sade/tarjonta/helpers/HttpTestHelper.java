package fi.vm.sade.tarjonta.helpers;

import static org.mockito.Mockito.when;

import fi.vm.sade.tarjonta.helpers.FakeAuthenticationInitialiser;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpTestHelper {
    public final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    public final HttpSession session = Mockito.mock(HttpSession.class);

    public HttpTestHelper(boolean putFakeAuthenticationToSecurityContext) {
        when(request.getSession(false)).thenReturn(session);
        when(request.getHeader("X-Real-IP")).thenReturn("10.0.0.1");
        if (putFakeAuthenticationToSecurityContext) {
            FakeAuthenticationInitialiser.fakeAuthentication();
        }
    }
}
