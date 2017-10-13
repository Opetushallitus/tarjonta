/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1.process;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Map;

import static fi.vm.sade.tarjonta.service.auditlog.AuditLog.getInetAddress;
import static fi.vm.sade.tarjonta.service.auditlog.AuditLog.getSession;
import static fi.vm.sade.tarjonta.service.auditlog.AuditLog.getUserOidFromSession;

public class MassCopyProcess implements ProcessDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(MassCopyProcess.class);

    public static final String PROCESS_STEP_TYPE = "process_step";
    public static final String PROCESS_SKIP_STEP = "step";

    public static final String COMMIT = "COMMIT";
    public static final String PREPARE = "PREPARE";
    public static final String DONE = "DONE";

    public static final String USER_OID = "USER_OID";

    public static final String COMMIT_COUNT_HAKUKOHDE = "commit_hakukohde_processed";
    public static final String COMMIT_TOTAL_HAKUKOHDE = "commit_hakukohde_total";
    public static final String COMMIT_COUNT_KOMOTO = "commit_komoto_processed";
    public static final String COMMIT_TOTAL_KOMOTO = "commit_komoto_total";

    public static final String PREPARE_COUNT_HAKUKOHDE = "prepare_hakukohde_processed";
    public static final String PREPARE_TOTAL_HAKUKOHDE = "prepare_hakukohde_total";
    public static final String PREPARE_COUNT_KOMOTO = "prepare_komoto_processed";
    public static final String PREPARE_TOTAL_KOMOTO = "prepare_komoto_total";


    public static final String SELECTED_HAKU_OID = "haku.oid.from";
    public static final String TO_HAKU_OID = "haku.oid.to";
    public static final String SELECTED_PROCESS_COPY_ID = "process.copy.id";

    @Autowired
    private MassPepareProcess prepare;

    @Autowired
    private MassCommitProcess commit;

    private boolean isPrepare = true;

    public MassCopyProcess() {
        super();
    }

    @Override
    public ProcessV1RDTO getState() {
        return isPrepare ? prepare.getState() : commit.getState();
    }

    @Override
    public void setState(ProcessV1RDTO state) {
        prepare.setState(state);
        commit.setState(state);
    }

    private void runCommitProcess() {
        isPrepare = false;
        getState().getParameters().put(PROCESS_STEP_TYPE, COMMIT);
        commit.run();

    }

    private void runPrepareProcess() {
        getState().getParameters().put(PROCESS_STEP_TYPE, PREPARE);
        prepare.run();
    }

    @Override
    public void run() {
        Map<String, String> parameters = getState().getParameters();
        final String fromOid = parameters.get(SELECTED_HAKU_OID);
        final String skipTestparam = parameters.get(PROCESS_SKIP_STEP);
        LOG.info("MassCopyProcess.run(), params haku oid : '{}', process id '{}'", fromOid, getState().getId());

        try {
            if (skipTestparam == null || skipTestparam.isEmpty()) {
                runPrepareProcess();
                runCommitProcess();
            } else {
                switch(skipTestparam) {
                    case COMMIT:
                        runCommitProcess();
                        break;
                    case PREPARE:
                        runPrepareProcess();
                        break;
                    default:
                        LOG.info("Received an unknown precess step '{}'.", PROCESS_SKIP_STEP);
                        break;
                }
            }
            parameters.put("result", "success");
        } catch(Throwable ex) {
            LOG.error("Copy failed", ex);
            getState().setMessageKey("my.test.process.error");
            parameters.put("result", ex.getMessage());
        } finally {
            parameters.put(PROCESS_STEP_TYPE, DONE);
        }

        LOG.info("run()... done.");
    }

    @Override
    public boolean isCompleted() {
        return prepare.isCompleted() && commit.isCompleted();
    }

    /**
     * Get process definition that can run this process.
     *
     * @param toOid haku oid to copy to
     * @param step  skip process steps
     * @param request
     * @return
     */
    public static ProcessV1RDTO getDefinition(final String toOid, final String step, HttpServletRequest request) {
        ProcessV1RDTO processV1RDTO = ProcessV1RDTO.generate();
        processV1RDTO.setProcess("massCopyProcess");
        processV1RDTO.getParameters().put(MassCopyProcess.SELECTED_HAKU_OID, toOid);
        processV1RDTO.getParameters().put(MassCopyProcess.PROCESS_SKIP_STEP, step);
        processV1RDTO.getParameters().put(MassCopyProcess.USER_OID, getUserOidFromSession());
        processV1RDTO.setUserAgent(request.getHeader("User-Agent"));
        processV1RDTO.setSession(getSession(request));
        processV1RDTO.setIp(getInetAddress(request));

        return processV1RDTO;
    }

}
