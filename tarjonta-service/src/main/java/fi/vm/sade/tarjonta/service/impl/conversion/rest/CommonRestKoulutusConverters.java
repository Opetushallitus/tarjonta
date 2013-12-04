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
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
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

    public KuvausV1RDTO convertMonikielinenTekstiToTekstiDTO(Map<TYPE, MonikielinenTeksti> tekstit, boolean showMeta) {
        KuvausV1RDTO tekstis = new KuvausV1RDTO();
        for (Map.Entry<TYPE, MonikielinenTeksti> e : tekstit.entrySet()) {
            NimiV1RDTO dto = new NimiV1RDTO();

            Collection<TekstiKaannos> tekstis1 = e.getValue().getTekstis();
            for (TekstiKaannos kaannos : tekstis1) {
                KoodiV1RDTO uri = new KoodiV1RDTO();
                if (kaannos.getKieliKoodi() != null && !kaannos.getKieliKoodi().isEmpty()) {
                    final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(kaannos.getKieliKoodi());
                    uri.setUri(type.getKoodiUri());
                    uri.setVersio(type.getVersio());
                    uri.setKaannos(tarjontaKoodistoHelper.getKoodiNimi(type.getKoodiUri(), new Locale(DEMO_LOCALE)));

                    if (showMeta) {
                        dto.getMeta().put(uri.getUri(), uri);
                    }
                    dto.getTekstis().put(uri.getUri(), kaannos.getArvo());
                }
                tekstis.put(e.getKey(), dto);
            }
        }

        return tekstis;
    }

    public void convertTekstiDTOToMonikielinenTeksti(KuvausV1RDTO tekstiDto, Map<TYPE, MonikielinenTeksti> tekstit) {
        Map<TYPE, NimiV1RDTO> tekstis = tekstiDto;
        for (Map.Entry<TYPE, NimiV1RDTO> e : tekstis.entrySet()) {
            Map<String, String> textMap = e.getValue().getTekstis();

            MonikielinenTeksti mkMerge = tekstit.get(e.getKey());

            if (mkMerge == null) {
                mkMerge = new MonikielinenTeksti();
                tekstit.put(e.getKey(), mkMerge);
            }

            for (Map.Entry<String, String> entry : textMap.entrySet()) {
                String text = entry.getValue();//text
                TekstiKaannos tekstiKaannos = searchByKielikoodi(mkMerge, entry.getKey());
                tekstiKaannos.setArvo(text);
                mkMerge.addTekstiKaannos(tekstiKaannos);
            }
            tekstit.put(e.getKey(), mkMerge);
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
