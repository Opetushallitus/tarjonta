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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.service.copy.CopyConverter;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MassPasteProcess implements ProcessDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(MassPasteProcess.class);
    public static final String SELECTED_HAKU_OID = "haku.oid.from";
    public static final String SELECTED_PROCESS_COPY_ID = "process.copy.id";

    private ProcessV1RDTO state;

    private long startTs = 0L;
    private int howManySecondsToRun = 0;

    @Autowired(required = true)
    private CopyConverter copyConverter;

    public MassPasteProcess() {
        super();

    }

    @Override
    public ProcessV1RDTO getState() {
        return state;
    }

    @Override
    public void setState(ProcessV1RDTO state) {
        this.state = state;
    }

    @Transactional(readOnly = false)
    @Override
    public void run() {
        final String targetHakuOid = getState().getParameters().get(SELECTED_HAKU_OID);
        final String processId = getState().getParameters().get(SELECTED_PROCESS_COPY_ID);
        LOG.info("MassPasteProcess.run(), params target haku oid : '{}', process id '{}'", targetHakuOid, processId);

        try {
            startTs = System.currentTimeMillis();
            LOG.info("start()... {}", startTs);
            copyConverter.convert(processId, targetHakuOid); 
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
}
