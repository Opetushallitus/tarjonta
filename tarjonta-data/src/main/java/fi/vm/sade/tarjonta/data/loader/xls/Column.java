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
package fi.vm.sade.tarjonta.data.loader.xls;

/**
 * @author Jani Wilén
 */
public class Column {
    private String key;
    private String title;
    private InputColumnType type;

    public Column(final String key, final String title, final InputColumnType type) {
        this.key = key;
        this.type = type;
        this.title = title;
    }

    public InputColumnType getType() {
        return this.type;
    }

    public String getKey() {
        return this.key;
    }

    public String getTitle() {
        return this.title;
    }
}