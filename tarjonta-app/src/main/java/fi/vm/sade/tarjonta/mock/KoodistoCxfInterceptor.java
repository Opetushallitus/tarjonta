package fi.vm.sade.tarjonta.mock;

/*
 *
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
 * European Union Public Licence for more details.
 */

import org.apache.cxf.attachment.AttachmentDeserializer;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author: Tuomas Katva
 * Date: 27.2.2013
 */
public class KoodistoCxfInterceptor extends AbstractPhaseInterceptor<Message> {

    private final Logger log = LoggerFactory.getLogger(KoodistoCxfInterceptor.class);

    public KoodistoCxfInterceptor() {
        super(Phase.SEND);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Map<String, List> headers = (Map<String, List>) message.get(Message.PROTOCOL_HEADERS);
        try {
            headers.put("CasSecurityTicket", Collections.singletonList("oldDeprecatedSecurity_REMOVE"));
            headers.put("oldDeprecatedSecurity_REMOVE_username",Collections.singletonList("admin@oph.fi"));
        } catch (Exception exp) {
             log.warn("UNABLE TO SET HTTP HEADERS!");
        }
    }
}
