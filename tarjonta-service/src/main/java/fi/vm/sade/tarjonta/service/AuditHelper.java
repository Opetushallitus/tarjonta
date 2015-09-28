package fi.vm.sade.tarjonta.service;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.tarjonta.LogMessage;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class AuditHelper {
    public static final Audit AUDIT = new Audit("tarjonta", ApplicationType.VIRKAILIJA);

    public static LogMessage.LogMessageBuilder builder() {
        return LogMessage.builder().id(getUsernameFromSession());
    }
    public static LogMessage.LogMessageBuilder builder(String username) {
        return LogMessage.builder().id(username);
    }
    public static String getUsernameFromSession() {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context != null) {
            Principal p = (Principal) context.getAuthentication();
            if(p != null) {
                return p.getName();
            }
        }
        return "Anonymous user";
    }
}
