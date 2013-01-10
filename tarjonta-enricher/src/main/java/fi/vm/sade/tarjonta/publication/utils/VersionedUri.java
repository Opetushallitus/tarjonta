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
package fi.vm.sade.tarjonta.publication.utils;

/**
 * Helper class to parse and format an uri that contains optional version.
 *
 * @author Jukka Raanamo
 */
public class VersionedUri {

    public static final String SEPARATOR_TOKEN = "#";

    private String uri;

    private Integer version;

    private VersionedUri(String uri, Integer version) {
        this.uri = uri;
        this.version = version;
    }

    public VersionedUri(VersionedUri copyFrom) {
        this(copyFrom.getUri(), copyFrom.getVersio());
    }

    public VersionedUri(String specString) {
        this(parse(specString));
    }

    public static VersionedUri parse(String spec) {

        String uri;
        Integer versio = null;

        int sepIndex = spec.lastIndexOf(SEPARATOR_TOKEN);
        if (sepIndex != -1) {
            uri = spec.substring(0, sepIndex);
            versio = Integer.parseInt(spec.substring(sepIndex + 1));
        } else {
            uri = spec;
        }

        return new VersionedUri(uri, versio);
    }

    public String getUri() {
        return uri;
    }

    public Integer getVersio() {
        return version;
    }

    public final String toVersionedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(uri);
        if (version != null) {
            sb.append(SEPARATOR_TOKEN);
            sb.append(version);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toVersionedString();
    }

}

