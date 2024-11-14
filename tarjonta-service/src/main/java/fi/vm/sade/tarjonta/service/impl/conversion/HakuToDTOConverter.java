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

package fi.vm.sade.tarjonta.service.impl.conversion;


import fi.vm.sade.tarjonta.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HaunNimi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;

/**
 *
 * @author Tuomas Katva
 */
public class HakuToDTOConverter extends AbstractFromDomainConverter<Haku, HakuTyyppi> {

    @Override
    public HakuTyyppi convert(Haku from) {
        HakuTyyppi h = new HakuTyyppi();
        h.setVersion(from.getVersion());
        h.setHakuVuosi(from.getHakukausiVuosi());
        h.setHakukausiUri(from.getHakukausiUri());
        h.setHakulomakeUrl(from.getHakulomakeUrl());
        h.setHakutapaUri(from.getHakutapaUri());
        h.setHakutyyppiUri(from.getHakutyyppiUri());
        h.setHaunTila(EntityUtils.convertTila(from.getTila()));
        h.setHaunTunniste(from.getHaunTunniste());
        h.setKohdejoukkoUri(from.getKohdejoukkoUri());
        h.setKoulutuksenAlkamisKausiUri(from.getKoulutuksenAlkamiskausiUri());
        if (from.getKoulutuksenAlkamisVuosi() != null) {
            h.setKoulutuksenAlkamisVuosi(from.getKoulutuksenAlkamisVuosi());
        }
        h.setOid(from.getOid());
        h.setSijoittelu(from.isSijoittelu());
        h.setHaunTunniste(from.getHaunTunniste());
        if (from.getLastUpdateDate() != null) {
            h.setViimeisinPaivitysPvm(from.getLastUpdateDate());
        }
        if (from.getLastUpdatedByOid() != null) {
            h.setViimeisinPaivittajaOid(from.getLastUpdatedByOid());
        }
        convertHaunNimet(h, from);
        convertHaunAjat(h, from);
        //ConvertHakukohdes(h,from);
        return h;
    }

    private void convertHaunAjat(HakuTyyppi h, Haku s) {
        if (s.getHakuaikas() != null) {
            for (Hakuaika ha: s.getHakuaikas()) {
                h.getSisaisetHakuajat().add(CommonToDTOConverter.convertHakuaikaToSisaisetHakuAjat(ha));
            }
        }
    }

    private void convertHaunNimet(HakuTyyppi h, Haku s) {
        if (s.getNimi() != null && s.getNimi().getTekstiKaannos() != null) {
        for (TekstiKaannos tk: s.getNimi().getTekstiKaannos()) {
            HaunNimi hn = new HaunNimi();
            hn.setKielikoodi(TarjontaKoodistoHelper.convertKieliUriToKielikoodi(tk.getKieliKoodi()));
            hn.setNimi(tk.getArvo());
            h.getHaunKielistetytNimet().add(hn);
        }
       }
    }
}
