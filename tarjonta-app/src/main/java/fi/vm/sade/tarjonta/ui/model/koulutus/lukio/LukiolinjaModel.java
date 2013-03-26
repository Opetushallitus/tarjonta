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
package fi.vm.sade.tarjonta.ui.model.koulutus.lukio;

import fi.vm.sade.tarjonta.ui.model.koulutus.*;

/**
 *
 * @author Jani Wilén
 */
public class LukiolinjaModel extends MonikielinenTekstiModel {

    private static final long serialVersionUID = -5207611570035520551L;
    public KoodiModel koulutuslaji;
    public KoodiModel pohjakoulutusvaatimus;

    public LukiolinjaModel() {
    }

    /**
     * @return the koulutuslaji
     */
    public KoodiModel getKoulutuslaji() {
        return koulutuslaji;
    }

    /**
     * @param koulutuslaji the koulutuslaji to set
     */
    public void setKoulutuslaji(KoodiModel koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    /**
     * @return the pohjakoulutusvaatimus
     */
    public KoodiModel getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    /**
     * @param pohjakoulutusvaatimus the pohjakoulutusvaatimus to set
     */
    public void setPohjakoulutusvaatimus(KoodiModel pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }
}
