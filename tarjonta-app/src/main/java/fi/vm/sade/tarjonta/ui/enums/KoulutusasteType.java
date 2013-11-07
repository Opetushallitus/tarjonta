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
package fi.vm.sade.tarjonta.ui.enums;

/**
 *
 * @author jani
 */
public enum KoulutusasteType {
    UNAVAILABLE("-1", "-1"),
    TOINEN_ASTE_LUKIO("31", "30"), //show only lukio spesific components
    TOINEN_ASTE_AMMATILLINEN_KOULUTUS("32", "32"),//show only ammattikoulu spesific components
    PERUSOPETUKSEN_LISAOPETUS("22", "22"),//show only ammattikoulu spesific components
    TUNTEMATON("90", "90");//show only ammattikoulu spesific components
    private String koulutusaste; //TK code
    private String koulutuskoodi; //TODO: a quick hack for koodisto data filtering

    KoulutusasteType(String aste, String koodi) {
        this.koulutusaste = aste;
        this.koulutuskoodi = koodi;
    }

    public String getKoulutusaste() {
        return koulutusaste;
    }

    public String getKoulutuskoodiFilter() {
        return this.koulutuskoodi;
    }

    public static KoulutusasteType getByKoulutusaste(String koulutusasteKoodi) {
        for (KoulutusasteType t : KoulutusasteType.values()) {
            if (t.getKoulutusaste().equals(koulutusasteKoodi)) {
                return t;
            }
        }

        return null;
    }
}