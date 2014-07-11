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
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.service.impl.resources.v1.process.ProcessDefinition;
import fi.vm.sade.tarjonta.service.resources.v1.ProcessResourceV1;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mlyly
 */
public class ProcessResourceV1Impl implements ProcessResourceV1 {

    // TODO put the process data to replicated cache... so that this works in clustered environment
    private static final Logger LOG = LoggerFactory.getLogger(ProcessResourceV1Impl.class);

    private final Executor _executor = Executors.newFixedThreadPool(5);

    @Autowired
    private ApplicationContext _applicationContext;

    // TODO clustered?
    // List processes here
    private List<ProcessDefinition> _processes = new ArrayList<ProcessDefinition>();

    @Override
    @Transactional
    public ProcessV1RDTO start(ProcessV1RDTO processParameters) {
        LOG.info("start({})", processParameters);

        if (processParameters == null || processParameters.getProcess() == null) {
            throw new IllegalStateException("{ process : 'XXXX', ... } REQUIRED!");
        }

        try {
            // { "process" : "processTest", "parameters" : {"time" : "13" }}

            // Make sure to define process beans as prototype beans... see "ws-context.xml" and "processTest" bean.
            ProcessDefinition pd = (ProcessDefinition) _applicationContext.getBean(processParameters.getProcess());

            // Set state and parameters
            pd.setState(processParameters);

            // Add process to memory storage for access
            _processes.add(pd);

            // Start it
            _executor.execute(pd);

            return pd.getState();
        } catch (Throwable ex) {
            LOG.error("Failed", ex);
            throw new IllegalStateException("Failed to start process: " + processParameters.getProcess());
        }
    }

    @Override
    public List<ProcessV1RDTO> list() {
        LOG.info("list()");
        List<ProcessV1RDTO> result = new ArrayList<ProcessV1RDTO>();

        for (ProcessDefinition processDefinition : _processes) {
            result.add(processDefinition.getState());
        }

        removeOldProcesses();

        return result;
    }

    @Override
    public ProcessV1RDTO get(String id) {
        LOG.info("get({})", id);
        ProcessDefinition pd = getProcessById(id);

        removeOldProcesses();

        return pd != null ? pd.getState() : null;
    }

    @Override
    public ProcessV1RDTO stop(String id) {
        LOG.info("stop({})", id);

        ProcessDefinition pd = getProcessById(id);

        removeOldProcesses();

        if (pd != null) {
            if (pd.canStop()) {
                // TODO kill it, how?
            }
            return pd.getState();
        }

        return null;
    }


    private ProcessDefinition getProcessById(String id) {
        for (ProcessDefinition processDefinition : _processes) {
            if (id.equals(processDefinition.getState().getId())) {
                return processDefinition;
            }
        }

        return null;
    }

    private void removeOldProcesses() {

        List<ProcessDefinition> pdsToBeRemoved = new ArrayList<ProcessDefinition>();

        for (ProcessDefinition processDefinition : _processes) {
            if (processDefinition.isCompleted()) {
                pdsToBeRemoved.add(processDefinition);
            }
        }

        for (ProcessDefinition processDefinition : pdsToBeRemoved) {
            _processes.remove(processDefinition);
        }
    }

}
