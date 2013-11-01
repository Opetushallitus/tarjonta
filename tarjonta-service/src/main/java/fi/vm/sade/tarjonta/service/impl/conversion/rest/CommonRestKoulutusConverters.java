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
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUriV1DTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.TekstiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.UiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.UiMetaV1RDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public TekstiV1RDTO convertMonikielinenTekstiToTekstiDTO(Map<TYPE, MonikielinenTeksti> tekstit) {
        TekstiV1RDTO tekstis = new TekstiV1RDTO();
        for (Map.Entry<TYPE, MonikielinenTeksti> e : tekstit.entrySet()) {
            UiMetaV1RDTO dto = new UiMetaV1RDTO();

            Collection<TekstiKaannos> tekstis1 = e.getValue().getTekstis();
            for (TekstiKaannos kaannos : tekstis1) {
                UiV1RDTO uri = new UiV1RDTO();
                uri.setKoodi(convertKoodiUri(kaannos.getKieliKoodi(), kaannos.getArvo()));
                uri.setArvo(kaannos.getArvo());
                dto.getMeta().put(uri.getKoodi().getUri(), uri);
            }
            tekstis.getTekstis().put(e.getKey(), dto);
        }

        return tekstis;
    }

    private KoodiUriV1DTO convertKoodiUri(final String koodistoKoodiUri, final String arvo) {
        KoodiUriV1DTO koodiUri = new KoodiUriV1DTO();
        if (koodistoKoodiUri != null && !koodistoKoodiUri.isEmpty()) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodistoKoodiUri);
            koodiUri.setUri(type.getKoodiUri());
            koodiUri.setVersio(type.getVersio() + "");
        }

        koodiUri.setArvo(arvo);
        koodiUri.setKaannos(tarjontaKoodistoHelper.getKoodiNimi(koodiUri.getUri(), new Locale(DEMO_LOCALE)));

        return koodiUri;
    }

    public void convertTekstiDTOToMonikielinenTeksti(TekstiV1RDTO tekstiDto, Map<TYPE, MonikielinenTeksti> tekstit) {
        Map<TYPE, UiMetaV1RDTO> tekstis = tekstiDto.getTekstis();
        for (Map.Entry<TYPE, UiMetaV1RDTO> e : tekstis.entrySet()) {
            Map<String, UiV1RDTO> restMeta = e.getValue().getMeta();

            MonikielinenTeksti merge = tekstit.get(e.getKey());

            if (merge == null) {
                merge = new MonikielinenTeksti();
                tekstit.put(e.getKey(), merge);
            }
            //  MonikielinenTeksti merged = MonikielinenTeksti.merge(oldMt, newMt);
            for (Map.Entry<String, UiV1RDTO> restKaannos : restMeta.entrySet()) {
                String newArvo = restKaannos.getValue().getKoodi().getArvo();
                TekstiKaannos searchByKielikoodi = searchByKielikoodi(merge, restKaannos.getKey());
                searchByKielikoodi.setArvo(newArvo);
                if (newArvo != null && newArvo.length() > 0) {
                    System.err.println("new arvo : " + newArvo);
                }
                merge.addTekstiKaannos(searchByKielikoodi);
            }
            tekstit.put(e.getKey(), merge);
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
