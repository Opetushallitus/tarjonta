/*
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
package fi.vm.sade.tarjonta.service;

import javax.xml.ws.WebFault;

/**
 * @author Jukka Raanamo
 * @deprecated use classes generated from WSDL
 */
@WebFault(faultBean = "fi.vm.sade.tarjonta.service.FaultBean")
@Deprecated
public class NoSuchOIDException extends RuntimeException {

    private static final long serialVersionUID = 7288971281859758877L;

    private FaultBean faultBean;

    public NoSuchOIDException() {
    }

    public NoSuchOIDException(String message) {
        this(message, null);
    }

    public NoSuchOIDException(String message, FaultBean faultBean, Throwable throwable) {
        super(message, throwable);
        this.faultBean = faultBean;
    }

    public NoSuchOIDException(String message, FaultBean faultBean) {
        super(message);
        this.faultBean = faultBean;
    }

    public FaultBean getFaultBean() {
        return faultBean;
    }

}

