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
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author mlyly
 */
public class ProcessResourceV1Impl implements ProcessResourceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessResourceV1Impl.class);

    private final Executor executor = Executors.newFixedThreadPool(5);

    private final List<ProcessDefinition> processes = new ArrayList<>();

    @Autowired
    private ApplicationContext applicationContext;

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
            ProcessDefinition pd = (ProcessDefinition) applicationContext.getBean(processParameters.getProcess());

            // Set state and parameters
            pd.setState(processParameters);

            // Add process to memory storage for access
            processes.add(pd);

            // Start it
            executor.execute(pd);

            return pd.getState();
        } catch(Throwable ex) {
            LOG.error("Failed", ex);
            throw new IllegalStateException("Failed to start process: " + processParameters.getProcess());
        }
    }

    @Override
    public List<ProcessV1RDTO> list() {
        removeOldProcesses();
        return processes.stream()
                .map(ProcessDefinition::getState)
                .collect(Collectors.toList());
    }

    @Override
    public ProcessV1RDTO get(String id) {
        removeOldProcesses();
        return processes.stream()
                .filter(p -> id.equals(p.getState().getId()))
                .map(ProcessDefinition::getState)
                .findAny().orElse(null);
    }

    @Override
    public ProcessV1RDTO stop(String id) {
        LOG.warn("stop({}) never implemented.", id);
        throw new NotImplementedException("Stop process");
    }

    private void removeOldProcesses() {
        List<ProcessDefinition> pdsToBeRemoved = processes.stream()
                .filter(this::isOverFiveMinutesOld)
                .collect(Collectors.toList());
        LOG.info("Removing process: " + pdsToBeRemoved.stream().map(p -> p.getState().getId()).collect(Collectors.toList()));
        processes.removeAll(pdsToBeRemoved);
    }

    private boolean isOverFiveMinutesOld(ProcessDefinition processDefinition) {
        return processDefinition.isCompleted()
                && (System.currentTimeMillis() - processDefinition.getState().getStarted()) > 1000 * 60 * 5;
    }

}
