package fi.vm.sade.tarjonta.service.auditlog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.vm.sade.auditlog.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.BaseV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Iterator;
import java.util.Map;

@Component
public final class AuditLog {
    public static final TarjontaOperation CREATE = new TarjontaOperation("CREATE");
    public static final TarjontaOperation UPDATE = new TarjontaOperation("UPDATE");
    public static final TarjontaOperation DELETE = new TarjontaOperation("DELETE");
    public static final TarjontaOperation MOVE = new TarjontaOperation("MOVE");
    public static final TarjontaOperation COPY = new TarjontaOperation("COPY");
    public static final TarjontaOperation PUBLISH = new TarjontaOperation("PUBLISH");
    public static final TarjontaOperation UNPUBLISH = new TarjontaOperation("UNPUBLISH");
    public static final TarjontaOperation STATE_CHANGE = new TarjontaOperation("STATE_CHANGE");
    public static final TarjontaOperation LINK_KOULUTUS = new TarjontaOperation("LINK_KOULUTUS");
    public static final TarjontaOperation UNLINK_KOULUTUS = new TarjontaOperation("UNLINK_KOULUTUS");
    public static final TarjontaOperation ADD_KOULUTUS_TO_HAKUKOHDE = new TarjontaOperation("ADD_KOULUTUS_TO_HAKUKOHDE");
    public static final TarjontaOperation REMOVE_KOULUTUS_FROM_HAKUKOHDE = new TarjontaOperation("REMOVE_KOULUTUS_FROM_HAKUKOHDE");
    public static final TarjontaOperation MODIFY_RYHMAT = new TarjontaOperation("MODIFY_RYHMAT");

    public static final TarjontaResource KOULUTUS = new TarjontaResource("KOULUTUS");
    public static final TarjontaResource HAKU = new TarjontaResource("HAKU");
    public static final TarjontaResource HAKUKOHDE = new TarjontaResource("HAKUKOHDE");
    public static final TarjontaResource VALINTAPERUSTE_SORA_KUVAUS = new TarjontaResource("VALINTAPERUSTE_SORA_KUVAUS");

    private static final Audit AUDITLOG = new Audit(new AuditLogger(), "tarjonta", ApplicationType.BACKEND);
    private static final Logger LOG = LoggerFactory.getLogger(AuditLog.class);
    private static final JsonParser parser = new JsonParser();
    static final int MAX_FIELD_LENGTH = 32766;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String UNKNOWN_USER_AGENT = "Unknown user agent";
    private static final String DUMMYOID_STR = "1.2.999.999.99.99999999999";
    private static final String UNKNOWN_SESSION = "Unknown session";
    private static final User ANONYMOUS_USER;
    private static Oid DUMMYOID;

    static {
        User anon = null;
        try {
            DUMMYOID = new Oid(DUMMYOID_STR);
            anon = new User(DUMMYOID, InetAddress.getByName(""), null, null);
        } catch(GSSException | UnknownHostException e) {
            LOG.error("Creating anonymous anon failed", e);
        }
        ANONYMOUS_USER = anon;
    }

    @PreDestroy
    public void destroy() {
        AUDITLOG.logStopped();
    }

    public static <T> void log(Operation operation, TarjontaResource tarjontaResource, String targetOid, T dtoAfterOperation, T dtoBeforeOperation, HttpServletRequest request, @NotNull Map<String, String> additionalInfo) {
        User user = getUser(request);
        Target.Builder target = getTarget(tarjontaResource, targetOid);
        additionalInfo.forEach(target::setField);

        Changes changes = getChanges(dtoAfterOperation, dtoBeforeOperation).build();
        AUDITLOG.log(user, operation, target.build(), changes);
    }

    public static <T> void log(Operation operation, TarjontaResource tarjontaResource, String targetOid, T dtoAfterOperation, T dtoBeforeOperation, HttpServletRequest request) {
        log(operation, tarjontaResource, targetOid, dtoAfterOperation, dtoBeforeOperation, request, Maps.newHashMap());
    }

    public static <T extends BaseV1RDTO> void create(TarjontaResource resource, String oid, T dtoAfterOperation, HttpServletRequest request) {
        log(CREATE, resource, oid, dtoAfterOperation, null, request);
    }

    public static <T extends BaseV1RDTO> void update(TarjontaResource resource, String oid, T dtoAfterOperation, T dtoBeforeOperation, HttpServletRequest request) {
        log(UPDATE, resource, oid, dtoAfterOperation, dtoBeforeOperation, request);
    }

    public static <T extends BaseV1RDTO> void copy(TarjontaResource resource, String oid, T dtoAfterOperation, HttpServletRequest request, String copyFromOid) {
        log(COPY, resource, oid, dtoAfterOperation, null, request, ImmutableMap.of("copyFromOid", copyFromOid));
    }

    public static <T extends BaseV1RDTO> void delete(TarjontaResource resource, String oid, T dtoBeforeOperation, HttpServletRequest request) {
        log(DELETE, resource, oid, null, dtoBeforeOperation, request);
    }

    public static <T extends BaseV1RDTO> void stateChange(TarjontaResource resource, String oid, TarjontaTila tila, T dtoAfterOperation, T dtoBeforeOperation, HttpServletRequest request, @Nullable final Map<String, String> additionalInfo) {
        Map<String, String> _additionalInfo = additionalInfo != null ? Maps.newHashMap(additionalInfo) : Maps.newHashMap();
        switch(tila) {
            case PERUTTU:
                log(UNPUBLISH, resource, oid, dtoAfterOperation, dtoBeforeOperation, request, _additionalInfo);
                break;
            case JULKAISTU:
                log(PUBLISH, resource, oid, dtoAfterOperation, dtoBeforeOperation, request, _additionalInfo);
                break;
            default:
                _additionalInfo.put("newTila", tila.name());
                log(STATE_CHANGE, resource, oid, dtoAfterOperation, dtoBeforeOperation, request, _additionalInfo);
                break;
        }
    }

    public static void massCopy(HakuV1RDTO hakuV1RDTO, String oldHakuOid, String userOid, InetAddress ip, String session, String userAgent) {
        User user = getUser(userOid, ip, session, userAgent);
        Target.Builder target = getTarget(HAKU, hakuV1RDTO.getOid());
        target.setField("copyFromOid", oldHakuOid);
        target.setField("userOid", userOid);
        Changes changes = getChanges(hakuV1RDTO, null).build();
        AUDITLOG.log(user, COPY, target.build(), changes);
    }

    private static Target.Builder getTarget(TarjontaResource tarjontaResource, String targetOid) {
        return new Target.Builder()
                .setField("type", tarjontaResource.name())
                .setField("oid", targetOid);
    }

    private static User getUser(@Nonnull HttpServletRequest request) {
        try {
            String userAgent = getUserAgentHeader(request);
            String session = getSession(request);
            InetAddress ip = getInetAddress(request);
            String userOid = getUserOidFromSession();
            return getUser(userOid, ip, session, userAgent);
        } catch(Exception e) {
            LOG.error("Recording anonymous user", e);
            return ANONYMOUS_USER;
        }

    }

    private static String getUserAgentHeader(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getSession(HttpServletRequest request) {
        try {
            return request.getSession(false).getId();
        } catch(Exception e) {
            LOG.error("Couldn't log session for requst {}", request);
            return null;
        }
    }

    public static InetAddress getInetAddress(HttpServletRequest request) {
        try {
            return InetAddress.getByName(request.getRemoteAddr());
        } catch(Exception e) {
            LOG.error("Couldn't log InetAddress for log entry", e);
            return null;
        }
    }

    public static Oid getOid(String usernameFromSession) {
        try {
            return new Oid(usernameFromSession);
        } catch(Exception e) {
            LOG.error("Couldn't log oid {} for log entry", usernameFromSession, e);
            return null;
        }
    }

    public static String getUserOidFromSession() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Principal p = context.getAuthentication();
            if (p != null) {
                return p.getName();
            }
        }
        LOG.error("Returning null user oid");
        return null;
    }

    private static User getUser(String userOid, InetAddress ip, String session, String userAgent) {
        Oid oid;
        try {
            oid = getOid(userOid);
        } catch(Exception e) {
            LOG.error("Recording anonymous user", e);
            oid = DUMMYOID;
        }
        return new User(
                oid,
                ip != null ? ip : InetAddress.getLoopbackAddress(),
                session != null ? session : UNKNOWN_SESSION,
                userAgent != null ? userAgent : UNKNOWN_USER_AGENT
        );

    }

    static Changes.Builder jsonDiffToChanges(Changes.Builder builder, JsonNode beforeJson, JsonNode afterJson) {
        traverseAndTruncate(afterJson);
        traverseAndTruncate(beforeJson);
        final ArrayNode patchArray = (ArrayNode) JsonDiff.asJson(beforeJson, afterJson);
        patchArray.forEach(patch -> {
            JsonNode operation = patch.get("op");
            JsonNode path = patch.get("path");
            JsonNode value = patch.get("value");
            //remove first / symbol from the path beginning and replace all subsequent ones with dots
            String prettyPath = path.asText().substring(1).replaceAll("/", ".");
            switch (operation.asText()) {
                case "add": {
                    builder.added(prettyPath, value.asText());
                    break;
                }
                case "remove": {
                    JsonNode oldValue = beforeJson.at(path.asText());
                    builder.removed(prettyPath, oldValue.asText());
                    break;
                }
                case "replace": {
                    JsonNode oldValue = beforeJson.at(path.asText());
                    builder.updated(prettyPath, oldValue.asText(), value.asText());
                    break;
                }
            }
        });
        return builder;
    }

    private static <T> Changes.Builder getChanges(@Nullable T afterOperation, @Nullable T beforeOperation) {
        Changes.Builder builder = new Changes.Builder();
        try {
            if (afterOperation == null && beforeOperation != null) {
                builder.removed("change", toGson(mapper.valueToTree(beforeOperation)));
            } else if (afterOperation != null && beforeOperation == null) {
                builder.added("change", toGson(mapper.valueToTree(afterOperation)));
            } else if (afterOperation != null) {
                JsonNode afterJson = mapper.valueToTree(afterOperation);
                JsonNode beforeJson = mapper.valueToTree(beforeOperation);
                builder = jsonDiffToChanges(builder, beforeJson, afterJson);
            }
        } catch(Exception e) {
            LOG.error("diff calculation failed", e);
        }
        return builder;
    }

    private static JsonObject toGson(@NotNull JsonNode json) {
        return parser.parse(json.toString()).getAsJsonObject();
    }

    private static JsonArray toGsonArray(@NotNull JsonNode json) {
        return parser.parse(json.toString()).getAsJsonArray();
    }

    static void traverseAndTruncate(JsonNode data) {
        if (data.isObject()) {
            ObjectNode object = (ObjectNode) data;
            for (Iterator<String> it = data.fieldNames(); it.hasNext(); ) {
                String fieldName = it.next();
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
        int maxLength = MAX_FIELD_LENGTH / 10; // Assume only a small number of fields can be extremely long
        if (data.textValue().length() <= maxLength) {
            return data;
        } else {
            String truncated = (new Integer(data.textValue().hashCode())).toString();
            return TextNode.valueOf(truncated);
        }
    }

}
