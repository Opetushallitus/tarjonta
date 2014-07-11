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

public class MassCopyProcess implements ProcessDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(MassCopyProcess.class);

    public static final String PROCESS_STEP_TYPE = "process.step";
    public static final String PROCESS_SKIP_STEP = "skip.step";

    public static final String COMMIT = "COMMIT";
    public static final String PREPARE = "PREPARE";

    public static final String COUNT_HAKUKOHDE = "count.hakukohde.processed";
    public static final String COUNT_KOMOTO = "count.komoto.processed";
    public static final String TOTAL_HAKUKOHDE = "count.total.hakukohde";
    public static final String TOTAL_KOMOTO = "count.total.komoto";

    public static final String SELECTED_HAKU_OID = "haku.oid.from";
    public static final String TO_HAKU_OID = "haku.oid.to";
    public static final String SELECTED_PROCESS_COPY_ID = "process.copy.id";

    private final MassPepareProcess prepare = new MassPepareProcess();
    private final MassCommitProcess commit = new MassCommitProcess();

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
        final String fromOid = getState().getParameters().get(SELECTED_HAKU_OID);
        LOG.info("MassCopyProcess.run(), params haku oid : '{}', process id '{}'", fromOid, getState().getId());

        try {
            if (PROCESS_SKIP_STEP == null || PROCESS_SKIP_STEP.isEmpty()) {
                runPrepareProcess();
                runCommitProcess();
            } else {
                if (PROCESS_SKIP_STEP.equals(COMMIT)) {
                    runCommitProcess();
                } else if (PROCESS_SKIP_STEP.equals(PREPARE)) {
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
            getState().setState(100.0);
        }

        LOG.info("run()... done.");
    }

    @Override
    public boolean canStop() {
        return true;
    }

    @Override
    public boolean isCompleted() {
        return getState().getState() == 100.0;
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
