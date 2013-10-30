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
package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;

/**
 *
 * @author mlyly
 */
public class GenericSearchParamsV1RDTO implements Serializable {

    private int _startIndex = 0;
    private int _count = 100;

    @Override
    public String toString() {
        return "GenericSearchParamsRDTO[startIndex=" + getStartIndex() + ", count=" + getCount() + "]";
    }

    public int getStartIndex() {
        return _startIndex;
    }

    public void setStartIndex(int _startIndex) {
        this._startIndex = _startIndex;
    }

    public int getCount() {
        return _count;
    }

    public void setCount(int _count) {
        this._count = _count;
    }



}
