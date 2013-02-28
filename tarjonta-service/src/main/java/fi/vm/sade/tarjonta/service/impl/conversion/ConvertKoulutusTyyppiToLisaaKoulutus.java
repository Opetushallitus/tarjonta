package fi.vm.sade.tarjonta.service.impl.conversion;/*
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

import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;

/**
 * @author: Tuomas Katva
 * Date: 28.2.2013
 */
public class ConvertKoulutusTyyppiToLisaaKoulutus {

    public static LisaaKoulutusTyyppi convert(KoulutusTyyppi koulutusTyyppi) {
        LisaaKoulutusTyyppi lisaaKoulutusTyyppi = new LisaaKoulutusTyyppi();

        lisaaKoulutusTyyppi.setTarjoaja(koulutusTyyppi.getTarjoaja());
        lisaaKoulutusTyyppi.setKoulutusohjelmaKoodi(koulutusTyyppi.getKoulutusohjelmaKoodi());
        lisaaKoulutusTyyppi.setKoulutusKoodi(koulutusTyyppi.getKoulutusKoodi());
        lisaaKoulutusTyyppi.setPohjakoulutusvaatimus(koulutusTyyppi.getPohjakoulutusvaatimus());
        lisaaKoulutusTyyppi.setKansainvalistyminen(koulutusTyyppi.getKansainvalistyminen());
        lisaaKoulutusTyyppi.setKesto(koulutusTyyppi.getKesto());
        lisaaKoulutusTyyppi.setKoulutuksenAlkamisPaiva(koulutusTyyppi.getKoulutuksenAlkamisPaiva());
        lisaaKoulutusTyyppi.setKuvailevatTiedot(koulutusTyyppi.getKuvailevatTiedot());
        lisaaKoulutusTyyppi.setKoulutusohjelmanValinta(koulutusTyyppi.getKoulutusohjelmanValinta());
        lisaaKoulutusTyyppi.setKoulutusohjelmaKoodi(koulutusTyyppi.getKoulutusohjelmaKoodi());
        lisaaKoulutusTyyppi.setKoulutusaste(koulutusTyyppi.getKoulutusaste());
        lisaaKoulutusTyyppi.setNimi(koulutusTyyppi.getNimi());
        lisaaKoulutusTyyppi.setOid(koulutusTyyppi.getOid());
        lisaaKoulutusTyyppi.setPainotus(koulutusTyyppi.getPainotus());
        lisaaKoulutusTyyppi.setSijoittuminenTyoelamaan(koulutusTyyppi.getSijoittuminenTyoelamaan());
        lisaaKoulutusTyyppi.setTila(koulutusTyyppi.getTila());
        lisaaKoulutusTyyppi.setYhteistyoMuidenToimijoidenKanssa(koulutusTyyppi.getYhteistyoMuidenToimijoidenKanssa());

        return lisaaKoulutusTyyppi;
    }

}
