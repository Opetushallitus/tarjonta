package fi.vm.sade.tarjonta.service.impl.resources.v1;

import static org.mockito.Mockito.when;

import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpTestHelper {
    public final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    public final HttpSession session = Mockito.mock(HttpSession.class);

    public HttpTestHelper() {
        when(request.getSession(false)).thenReturn(session);
        when(request.getHeader("X-Real-IP")).thenReturn("10.0.0.1");
    }
}
