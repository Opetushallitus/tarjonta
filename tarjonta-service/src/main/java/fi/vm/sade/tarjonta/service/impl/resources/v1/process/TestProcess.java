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

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Define process in ws-context.xml, "process" should be the name of the bean, scope "prototype".
 *
 * <pre>
 * {
 *  "process" : "processTest",
 *  "parameters" : {
 *    "time" : 13
 *  }
 * }
 * </pre>
 *
 * @author mlyly
 */
public class TestProcess implements ProcessDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(TestProcess.class);

    private ProcessV1RDTO state;

    @Autowired
    private HakuDAO hakuDao;

    public TestProcess() {
        super();
        LOG.info("TestProcess() - hakuDao = {}", hakuDao);
    }

    @Override
    public ProcessV1RDTO getState() {
        return state;
    }

    @Override
    public void setState(ProcessV1RDTO state) {
        this.state = state;
    }

    @Override
    public void run() {
        LOG.info("run()...");

        try {
            long startTs = System.currentTimeMillis();
            int howManySecondsToRun = Integer.parseInt(getState().getParameters().get("time"));

            getState().setMessageKey("my.test.process.starting");
            getState().getParameters().put("test", "Value!");


            while (System.currentTimeMillis() < (startTs + howManySecondsToRun * 1000)) {
                // Update state
                getState().getParameters().put("ts", "" + System.currentTimeMillis());

                int secondsRunning = (int) ((System.currentTimeMillis() - startTs) / 1000);
                getState().setState((secondsRunning * 100.0) / (1.0 * howManySecondsToRun));

                LOG.info("Running... id={} state = {} - secs = {}", getState().getId(), getState().getState(), secondsRunning);
                LOG.info("  hakuDao = {}", hakuDao);

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    LOG.info("INTERRUPTED!");
                }
            }

            getState().setMessageKey("my.test.process.complete");
            getState().getParameters().put("result", "success");

        } catch (Throwable ex) {
            getState().setMessageKey("my.test.process.error");
            getState().getParameters().put("result", ex.getMessage());
        } finally {
            getState().setState(100.0);
        }

        LOG.info("run()... done.");
    }

    @Override
    public boolean isCompleted() {
        return getState().getState() == 100.0;
    }
}
