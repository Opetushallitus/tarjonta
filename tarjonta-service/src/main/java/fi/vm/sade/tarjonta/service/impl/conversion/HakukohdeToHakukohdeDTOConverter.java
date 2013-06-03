/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.dao.MonikielinenMetadataDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import fi.vm.sade.tarjonta.model.PainotettavaOppiaine;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;

/**
 * Conversion for the REST services.
 *
 * @author mlyly
 */
public class HakukohdeToHakukohdeDTOConverter extends BaseRDTOConverter<Hakukohde, HakukohdeDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeToHakukohdeDTOConverter.class);

    @Autowired
    private MonikielinenMetadataDAO monikielinenMetadataDAO;

    @Autowired
    private ApplicationContext applicationContext;

    // @Autowired -- cannot do this, this bean is defined in "scope" of conversion beans creation...
    private ConversionService conversionService;

    @Override
    public HakukohdeDTO convert(Hakukohde s) {
        HakukohdeDTO t = new HakukohdeDTO();

        t.setOid(s.getOid());
        t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : -1);

        t.setAlinHyvaksyttavaKeskiarvo(s.getAlinHyvaksyttavaKeskiarvo() != null ? s.getAlinHyvaksyttavaKeskiarvo().doubleValue() : 0.0d);
        t.setAlinValintaPistemaara(s.getAlinValintaPistemaara() != null ? s.getAlinValintaPistemaara().intValue() : 0);
        t.setAloituspaikatLkm(s.getAloituspaikatLkm() != null ? s.getAloituspaikatLkm().intValue() : 0);
        t.setEdellisenVuodenHakijatLkm(s.getEdellisenVuodenHakijat() != null ? s.getEdellisenVuodenHakijat().intValue() : 0);
        // t.set(s.getHaku());
        t.setHakukelpoisuusvaatimusUri(s.getHakukelpoisuusvaatimus());
        t.setHakukohdeKoodistoNimi(s.getHakukohdeKoodistoNimi());
        t.setHakukohdeNimiUri(s.getHakukohdeNimi());
        // t.set(s.getKoulutusmoduuliToteutuses());
        t.setModified(s.getLastUpdateDate());
        t.setModifiedBy(s.getLastUpdatedByOid());
        // t.set(s.getLiites());
        // t.set(s.getLiitteidenToimitusOsoite());
        t.setLiitteidenToimitusPvm(s.getLiitteidenToimitusPvm());
        t.setLisatiedot(convertMonikielinenTekstiToMap(s.getLisatiedot()));
        t.setPainotettavatOppiaineet(convertPainotettavatOppianeet(s.getPainotettavatOppiaineet()));
        t.setSahkoinenToimitusOsoite(s.getSahkoinenToimitusOsoite());
        t.setSoraKuvausKoodiUri(s.getSoraKuvausKoodiUri());
        t.setTila(s.getTila() != null ? s.getTila().name() : null);
        // TODO t.set(s.getValintakoes());
        t.setValintaperustekuvausKoodiUri(s.getValintaperustekuvausKoodiUri());
        t.setValintojenAloituspaikatLkm(s.getValintojenAloituspaikatLkm() != null ? s.getValintojenAloituspaikatLkm().intValue() : 0);
        t.setYlinValintapistemaara(s.getYlinValintaPistemaara() != null ? s.getYlinValintaPistemaara().intValue() : 0);

        t.setKaytetaanHaunPaattymisenAikaa(s.isKaytetaanHaunPaattymisenAikaa());

        if (s.getSoraKuvausKoodiUri() != null) {
            t.setSorakuvaus(getMetadata(monikielinenMetadataDAO.findByAvainAndKategoria(s.getSoraKuvausKoodiUri(),
                    MetaCategory.SORA_KUVAUS.name())));
        }

        if (s.getValintaperustekuvausKoodiUri() != null) {
            t.setValintaperustekuvaus(getMetadata(monikielinenMetadataDAO.findByAvainAndKategoria(s.getSoraKuvausKoodiUri(),
                    MetaCategory.VALINTAPERUSTEKUVAUS.name())));
        }

        t.setHakukelpoisuusvaatimus(convertKoodistoToHakukelpoisuusVaatimukset(s.getHakukohdeNimi()));


        t.setLiitteet(convertLiitteet(s.getLiites()));


        return t;
    }

    private ConversionService getConversionService() {
        if (conversionService == null) {
            LOG.info("looking up ConversionService...");
            conversionService = applicationContext.getBean(ConversionService.class);
        }
        return conversionService;
    }


    /**
     * Convert PainotettavaOppiaine to list of [ [ "oppiaine", "9.7"], ... ]
     *
     * @param s
     * @return
     */
    private List<List<String>> convertPainotettavatOppianeet(Set<PainotettavaOppiaine> s) {
        List<List<String>> result = new ArrayList<List<String>>();

        for (PainotettavaOppiaine painotettavaOppiaine : s) {
            List<String> t = new ArrayList<String>();
            t.add(painotettavaOppiaine.getOppiaine());
            t.add("" + painotettavaOppiaine.getPainokerroin());

            result.add(t);
        }

        return result;
    }

    /**
     * Convert liite information.
     *
     * @param s
     * @return
     */
    private List<HakukohdeLiiteDTO> convertLiitteet(Set<HakukohdeLiite> s) {
        List<HakukohdeLiiteDTO> result = new ArrayList<HakukohdeLiiteDTO>();

        for (HakukohdeLiite hakukohdeLiite : s) {
            result.add(getConversionService().convert(hakukohdeLiite, HakukohdeLiiteDTO.class));
        }

        return result;
    }


    /**
     * Extract metadata - key + category ("uri: soste-alue", "SORA") from many languages.
     *
     * @param metas
     * @return map if language keyed translations
     */
    private Map<String, String> getMetadata(List<MonikielinenMetadata> metas) {
        Map<String, String> result = new HashMap<String, String>();

        for (MonikielinenMetadata monikielinenMetadata : metas) {
            result.put(getTarjontaKoodistoHelper().convertKielikoodiToKieliUri(monikielinenMetadata.getKieli()), monikielinenMetadata.getArvo());
        }

        return result;
    }

    /**
     * Conversion from koodisto:
     *
     * <pre>
     * "hakukohde" -> "hakukelpoisuusvaatimus ta" (description)
     * </pre>
     *
     * @param hakukohdenimiUri
     * @return
     */
    private Map<String, String> convertKoodistoToHakukelpoisuusVaatimukset(String hakukohdenimiUri) {
        LOG.info("convertKoodistoToHakukelpoisuusVaatimukset({})", hakukohdenimiUri);

        Map<String, String> result = new HashMap<String, String>();

        String HAKUKELPOISUUSVAATIMUS_TA = "hakukelpoisuusvaatimusta";

        if (hakukohdenimiUri != null) {
            // 1. get hakukohde name koodi
            KoodiType sourceKoodiType = getTarjontaKoodistoHelper().getKoodiByUri(hakukohdenimiUri);
            if (sourceKoodiType != null) {
                // 2. Get relation to "hakukelpoisuusvaatimus ta" koodisto
                Collection<KoodiType> relations =
                        getTarjontaKoodistoHelper().getKoodistoRelations(hakukohdenimiUri, HAKUKELPOISUUSVAATIMUS_TA, SuhteenTyyppiType.SISALTYY, false);

                if (relations != null && !relations.isEmpty()) {
                    if (relations.size() > 1) {
                        LOG.warn("  hmm.... too many relations for hakukohde '" + hakukohdenimiUri + "' to " + HAKUKELPOISUUSVAATIMUS_TA + " koodisto.");
                    }

                    // TODO JUST TAKE THE FIRST ONE...
                    KoodiType targetKoodiType = relations.iterator().next();

                    // 3. Get metadata (kieli, kuvaus)

                    for (KoodiMetadataType koodiMetadataType : targetKoodiType.getMetadata()) {
                        String kuvaus = koodiMetadataType.getKuvaus();
                        String kieli = koodiMetadataType.getKieli().name();

                        LOG.info("  {} {}", kieli, kuvaus);

                        result.put(getTarjontaKoodistoHelper().convertKielikoodiToKieliUri(kieli), kuvaus);
                    }
                }
            }
        }

        LOG.info("  --> result = {}", result);

        return result;
    }

}
