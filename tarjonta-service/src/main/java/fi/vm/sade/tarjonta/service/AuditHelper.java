package fi.vm.sade.tarjonta.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.tarjonta.LogMessage;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class AuditHelper {
    public static final Audit AUDIT = new Audit("tarjonta", ApplicationType.VIRKAILIJA);
    private final static ObjectMapper mapper = new ObjectMapper();

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

    public static String getKomotoDelta(KoulutusV1RDTO k1, KoulutusV1RDTO k2) {
        try {
            if (k2 == null) {
                return mapper.writeValueAsString(k1);
            }
            JsonNode k1Json = mapper.valueToTree(k1);
            JsonNode k2Json = mapper.valueToTree(k2);
            final JsonNode patchNode = JsonDiff.asJson(k2Json, k1Json);
            return patchNode.toString();
        }
        catch (Exception e) {
            return "diff calculation failed: " + e.toString();
        }
    }

    public static String getHakukohdeDelta(HakukohdeV1RDTO h1, HakukohdeV1RDTO h2) {
        try {
            if (h2 == null) {
                return mapper.writeValueAsString(h1);
            }
            JsonNode h1Json = mapper.valueToTree(h1);
            JsonNode h2Json = mapper.valueToTree(h2);
            final JsonNode patchNode = JsonDiff.asJson(h2Json, h1Json);
            return patchNode.toString();
        }
        catch (Exception e) {
            return "diff calculation failed: " + e.toString();
        }
    }
}
