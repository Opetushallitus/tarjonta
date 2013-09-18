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
package fi.vm.sade.tarjonta.service.resources.dto;

import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
public abstract class ToteutusDTO extends BaseRDTO {

    private static final long serialVersionUID = 1L;
    private String komoOid;
    private TarjontaTila tila;
    private KoodiUriDTO koulutuskoodi;
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;
    private Map<KomoTeksti, MonikielinenTekstiTyyppi> tekstis;
    /*
     * Contact person
     */
    private YhteyshenkiloTyyppi yhteyshenkilo;

    /**
     * @return the tila
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    /**
     * @return the koulutuskoodi
     */
    public KoodiUriDTO getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * @param koulutuskoodi the koulutuskoodi to set
     */
    public void setKoulutuskoodi(KoodiUriDTO koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    /**
     * @return the tekstis
     */
    public Map<KomoTeksti, MonikielinenTekstiTyyppi> getTekstis() {
        return tekstis;
    }

    /**
     * @param tekstis the tekstis to set
     */
    public void setTekstis(Map<KomoTeksti, MonikielinenTekstiTyyppi> tekstis) {
        this.tekstis = tekstis;
    }

    /**
     * @return the yhteyshenkilo
     */
    public YhteyshenkiloTyyppi getYhteyshenkilo() {
        return yhteyshenkilo;
    }

    /**
     * @return the komoOid
     */
    public String getKomoOid() {
        return komoOid;
    }

    /**
     * @param komoOid the komoOid to set
     */
    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    /**
     * @return the koulutusmoduuliTyyppi
     */
    public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    /**
     * @param koulutusmoduuliTyyppi the koulutusmoduuliTyyppi to set
     */
    public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }
}
