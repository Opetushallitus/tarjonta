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

public class MassCopyProcess implements ProcessDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(MassCopyProcess.class);

    public static final String PROCESS_STEP_TYPE = "process_step";
    public static final String PROCESS_SKIP_STEP = "step";

    public static final String COMMIT = "COMMIT";
    public static final String PREPARE = "PREPARE";
    public static final String DONE = "DONE";


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

    @Autowired(required = true)
    private MassPepareProcess prepare;

    @Autowired(required = true)
    private MassCommitProcess commit;

    private boolean isPrepare = true;

    private boolean completed=false;

    public MassCopyProcess() {
        super();
    }
    
    @Override
    public ProcessV1RDTO getState() {
        ProcessV1RDTO state = isPrepare ? prepare.getState() : commit.getState();
        return state;
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
        final String fromOid = getState().getParameters().get(SELECTED_HAKU_OID);
        final String skipTestparam = getState().getParameters().get(PROCESS_SKIP_STEP);
        LOG.info("MassCopyProcess.run(), params haku oid : '{}', process id '{}'", fromOid, getState().getId());

        try {
            if (skipTestparam == null || skipTestparam.isEmpty()) {
                runPrepareProcess();
                runCommitProcess();
            } else {
                if (skipTestparam.equals(COMMIT)) {
                    runCommitProcess();
                } else if (skipTestparam.equals(PREPARE)) {
                    runPrepareProcess();
                } else {
                    LOG.info("Received an unknown precess step '{}'.", PROCESS_SKIP_STEP);
                }
            }
            getState().getParameters().put("result", "success");
        } catch (Throwable ex) {
            LOG.error("Copy failed", ex);
            getState().setMessageKey("my.test.process.error");
            getState().getParameters().put("result", ex.getMessage());
        } finally {
            completed=true;
            getState().getParameters().put(PROCESS_STEP_TYPE, DONE);
        }

        LOG.info("run()... done.");
    }

    @Override
    public boolean canStop() {
        return true;
    }

    @Override
    public boolean isCompleted() {
        return prepare.isCompleted() && commit.isCompleted();
    }

    /**
     * Get process definition that can run this process.
     *
     * @param toOid haku oid to copy to
     * @param step skip process steps
     * @return
     */
    public static ProcessV1RDTO getDefinition(final String toOid, final String step) {
        ProcessV1RDTO processV1RDTO = ProcessV1RDTO.generate();
        processV1RDTO.setProcess("massCopyProcess");
        processV1RDTO.getParameters().put(MassCopyProcess.SELECTED_HAKU_OID, toOid);
        processV1RDTO.getParameters().put(MassCopyProcess.PROCESS_SKIP_STEP, step);
        return processV1RDTO;
    }
}
