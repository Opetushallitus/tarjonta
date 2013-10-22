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
package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.resources.dto.kk.KoodiUriDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.TekstiDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.UiDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.UiMetaDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author jani
 * @param <TYPE>
 */
@Component
public class CommonRestKoulutusConverters<TYPE extends Enum> {

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    private static final String DEMO_LOCALE = "FI";

    public TekstiDTO convertMonikielinenTekstiToTekstiDTO(Map<TYPE, MonikielinenTeksti> tekstit) {
        TekstiDTO tekstis = new TekstiDTO();
        for (Map.Entry<TYPE, MonikielinenTeksti> e : tekstit.entrySet()) {
            UiMetaDTO dto = new UiMetaDTO();

            Collection<TekstiKaannos> tekstis1 = e.getValue().getTekstis();
            for (TekstiKaannos kaannos : tekstis1) {
                UiDTO uri = new UiDTO();
                uri.setKoodi(convertKoodiUri(kaannos.getKieliKoodi(), kaannos.getArvo()));
                uri.setArvo(kaannos.getArvo());
                dto.getMeta().put(uri.getKoodi().getUri(), uri);
            }
            tekstis.getTekstis().put(e.getKey(), dto);
        }

        return tekstis;
    }

    private KoodiUriDTO convertKoodiUri(final String koodistoKoodiUri, final String arvo) {
        KoodiUriDTO koodiUri = new KoodiUriDTO();
        if (koodistoKoodiUri != null && !koodistoKoodiUri.isEmpty()) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodistoKoodiUri);
            koodiUri.setUri(type.getKoodiUri());
            koodiUri.setVersio(type.getVersio() + "");
        }

        koodiUri.setArvo(arvo);
        koodiUri.setKaannos(tarjontaKoodistoHelper.getKoodiNimi(koodiUri.getUri(), new Locale(DEMO_LOCALE)));

        return koodiUri;
    }

    public void convertTekstiDTOToMonikielinenTeksti(TekstiDTO tekstiDto, Map<TYPE, MonikielinenTeksti> tekstit) {
        Map<TYPE, UiMetaDTO> tekstis = tekstiDto.getTekstis();
        for (Map.Entry<TYPE, UiMetaDTO> e : tekstis.entrySet()) {
            Map<String, UiDTO> restMeta = e.getValue().getMeta();

            MonikielinenTeksti merge = tekstit.get(e.getKey());

            if (merge == null) {
                merge = new MonikielinenTeksti();
                tekstit.put(e.getKey(), merge);
            }
            //  MonikielinenTeksti merged = MonikielinenTeksti.merge(oldMt, newMt);
            for (Map.Entry<String, UiDTO> restKaannos : restMeta.entrySet()) {
                if (restKaannos.getValue().getKoodi().getArvo() != null) {
                    TekstiKaannos searchByKielikoodi = searchByKielikoodi(merge, restKaannos.getKey());
                    searchByKielikoodi.setArvo(restKaannos.getValue().getArvo());
                    merge.addTekstiKaannos(searchByKielikoodi);
                }
            }
        }
    }

    private TekstiKaannos searchByKielikoodi(MonikielinenTeksti merge, final String kieliUri) {
        for (TekstiKaannos k : merge.getTekstis()) {
            if (k.getKieliKoodi().equals(kieliUri)) {
                return k;
            }
        }

        return new TekstiKaannos(merge, kieliUri, null);
    }

}