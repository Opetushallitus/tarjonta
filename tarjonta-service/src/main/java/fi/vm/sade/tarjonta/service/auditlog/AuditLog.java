package fi.vm.sade.tarjonta.service.auditlog;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Operation;
import fi.vm.sade.auditlog.Target;
import fi.vm.sade.auditlog.User;
import fi.vm.sade.javautils.http.HttpServletRequestUtils;
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

    public static void log(Operation operation, TarjontaResource tarjontaResource, String targetOid, Changes changes, HttpServletRequest request, @NotNull Map<String, String> additionalInfo) {
        User user = getUser(request);
        Target.Builder target = getTarget(tarjontaResource, targetOid);
        additionalInfo.forEach(target::setField);
        AUDITLOG.log(user, operation, target.build(), changes);
    }

    public static void log(Operation operation, TarjontaResource tarjontaResource, String targetOid, Changes changes, HttpServletRequest request) {
        log(operation, tarjontaResource, targetOid, changes, request, Maps.newHashMap());
    }

    public static <T extends BaseV1RDTO> void create(TarjontaResource resource, String oid, T newDto, HttpServletRequest request) {
        log(CREATE, resource, oid, Changes.addedDto(newDto), request);
    }

    public static <T extends BaseV1RDTO> void update(TarjontaResource resource, String oid, T dtoAfterOperation, T dtoBeforeOperation, HttpServletRequest request) {
        log(UPDATE, resource, oid, Changes.updatedDto(dtoAfterOperation, dtoBeforeOperation), request);
    }

    public static <T extends BaseV1RDTO> void copy(TarjontaResource resource, String oid, T dtoAfterOperation, HttpServletRequest request, String copyFromOid) {
        log(COPY, resource, oid, Changes.addedDto(dtoAfterOperation), request, ImmutableMap.of("copyFromOid", copyFromOid));
    }

    public static <T extends BaseV1RDTO> void delete(TarjontaResource resource, String oid, T dtoBeforeOperation, HttpServletRequest request) {
        log(DELETE, resource, oid, Changes.deleteDto(dtoBeforeOperation), request);
    }

    public static <T extends BaseV1RDTO> void stateChange(TarjontaResource resource, String oid, TarjontaTila tila, T dtoAfterOperation, T dtoBeforeOperation, HttpServletRequest request, @Nullable final Map<String, String> additionalInfo) {
        Map<String, String> _additionalInfo = additionalInfo != null ? Maps.newHashMap(additionalInfo) : Maps.newHashMap();
        Changes changes = Changes.updatedDto(dtoAfterOperation, dtoBeforeOperation);
        switch(tila) {
            case PERUTTU:
                log(UNPUBLISH, resource, oid, changes, request, _additionalInfo);
                break;
            case JULKAISTU:
                log(PUBLISH, resource, oid, changes, request, _additionalInfo);
                break;
            default:
                _additionalInfo.put("newTila", tila.name());
                log(STATE_CHANGE, resource, oid, changes, request, _additionalInfo);
                break;
        }
    }

    public static void massCopy(HakuV1RDTO hakuV1RDTO, String oldHakuOid, String userOid, InetAddress ip, String session, String userAgent) {
        User user = getUser(userOid, ip, session, userAgent);
        Target.Builder target = getTarget(HAKU, hakuV1RDTO.getOid());
        target.setField("copyFromOid", oldHakuOid);
        target.setField("userOid", userOid);
        AUDITLOG.log(user, COPY, target.build(), Changes.addedDto(hakuV1RDTO));
    }

    private static Target.Builder getTarget(TarjontaResource tarjontaResource, String targetOid) {
        return new Target.Builder()
                .setField("type", tarjontaResource.name())
                .setField("oid", targetOid);
    }

    private static User getUser(@Nonnull HttpServletRequest request) {
        String userAgent = getUserAgentHeader(request);
        String session = getSession(request);
        InetAddress ip = getInetAddress(request);
        String userOid = getUserOidFromSession();
        return getUser(userOid, ip, session, userAgent);
    }

    private static String getUserAgentHeader(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getSession(HttpServletRequest request) {
        return request.getSession(false).getId();
    }

    public static InetAddress getInetAddress(HttpServletRequest request) {
        try {
            return InetAddress.getByName(HttpServletRequestUtils.getRemoteAddress(request));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
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
        return new User(
            getOid(userOid),
            ip,
            session,
            userAgent);
    }
}
