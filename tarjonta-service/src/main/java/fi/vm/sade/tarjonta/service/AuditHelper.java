package fi.vm.sade.tarjonta.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.flipkart.zjsonpatch.JsonDiff;
import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.tarjonta.LogMessage;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Iterator;

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
        return getDelta(k1, k2);
    }

    public static String getHakukohdeDelta(HakukohdeV1RDTO h1, HakukohdeV1RDTO h2) {
        return getDelta(h1, h2);
    }

    public static String getHakuDelta(HakuV1RDTO h1, HakuV1RDTO h2) {
        return getDelta(h1, h2);
    }

    public static String getKuvausDelta(KuvausV1RDTO k1, KuvausV1RDTO k2) {
        return getDelta(k1, k2);
    }

    public static <T> String getDelta(T v1, T v2) {
        try {
            if (v2 == null) {
                return mapper.writeValueAsString(v1);
            }
            JsonNode v1Json = mapper.valueToTree(v1);
            JsonNode v2Json = mapper.valueToTree(v2);
            traverseAndTruncate(v1Json);
            traverseAndTruncate(v2Json);
            final JsonNode patchNode = JsonDiff.asJson(v2Json, v1Json);
            return patchNode.toString();
        }
        catch (Exception e) {
            return "diff calculation failed: " + e.toString();
        }
    }

    static void traverseAndTruncate(JsonNode data) {
        if (data.isObject()) {
            ObjectNode object = (ObjectNode) data;
            Iterator<String> fieldNames = data.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode child = object.get(fieldName);
                if (child.isTextual()) {
                    object.set(fieldName, truncate((TextNode) child));
                } else {
                    traverseAndTruncate(child);
                }
            }
        } else if (data.isArray()) {
            ArrayNode array = (ArrayNode) data;
            for (int i = 0; i < array.size(); i++) {
                JsonNode child = array.get(i);
                if (child.isTextual()) {
                    array.set(i, truncate((TextNode) child));
                } else {
                    traverseAndTruncate(child);
                }
            }
        }
    }

    private static TextNode truncate(TextNode data) {
        if (data.textValue().length() <= Audit.MAX_FIELD_LENGTH) {
            return data;
        } else {
            String truncated = (new Integer(data.textValue().hashCode())).toString();
            return TextNode.valueOf(truncated);
        }
    }
}
