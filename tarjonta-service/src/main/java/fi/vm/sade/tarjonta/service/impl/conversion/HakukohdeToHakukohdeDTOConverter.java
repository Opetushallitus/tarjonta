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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.MonikielinenMetadataDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.PainotettavaOppiaine;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;

/**
 * Conversion for the REST services.
 * 
 * @author mlyly
 */
public class HakukohdeToHakukohdeDTOConverter extends BaseRDTOConverter<Hakukohde, HakukohdeDTO> {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeToHakukohdeDTOConverter.class);

    @Autowired
    private MonikielinenMetadataDAO monikielinenMetadataDAO;

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Override
    public HakukohdeDTO convert(Hakukohde s) {
        HakukohdeDTO t = new HakukohdeDTO();

        t.setOid(s.getOid());
        t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : -1);

        // tarjoajaOid, tarjoajaNimi
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : s.getKoulutusmoduuliToteutuses()) {
            if (koulutusmoduuliToteutus.getTarjoaja() != null) {
                // Assumes that only one provider for koulutus - is this true?
                String organisaatioOid = koulutusmoduuliToteutus.getTarjoaja();
                t.setTarjoajaOid(organisaatioOid);
                if (organisaatioOid != null) {
                    try {
                        OrganisaatioDTO organisaatio = organisaatioService.findByOid(organisaatioOid);
                        if (organisaatio != null) {
                            Map<String, String> map = new HashMap<String, String>();
                            for (MonikielinenTekstiTyyppi.Teksti teksti : organisaatio.getNimi().getTeksti()) {
                                map.put(tarjontaKoodistoHelper.convertKielikoodiToKieliUri(teksti.getKieliKoodi()),
                                        teksti.getValue());
                            }
                            t.setTarjoajaNimi(map);
                        }
                    } catch (Throwable th) {
                        //organisaation nimihaku epäonnistui!!!
                        Map<String, String> map = new HashMap<String, String>();
                        map.put(KoodistoURI.KOODI_LANG_FI_URI, "Organisaatiohaku epäonnistui (" + organisaatioOid + ")");
                        t.setTarjoajaNimi(map);
                    }
                }
                break;
            }
        }
        // hakukohdeNimi
        t.setHakukohdeNimi(tarjontaKoodistoHelper.getKoodiMetadataNimi(s.getHakukohdeNimi()));

        t.setAlinHyvaksyttavaKeskiarvo(s.getAlinHyvaksyttavaKeskiarvo() != null ? s.getAlinHyvaksyttavaKeskiarvo()
                .doubleValue() : 0.0d);
        t.setAlinValintaPistemaara(s.getAlinValintaPistemaara() != null ? s.getAlinValintaPistemaara().intValue() : 0);
        t.setAloituspaikatLkm(s.getAloituspaikatLkm());
        t.setEdellisenVuodenHakijatLkm(s.getEdellisenVuodenHakijat() != null ? s.getEdellisenVuodenHakijat().intValue()
                : 0);
        t.setHakuOid(s.getHaku() != null ? s.getHaku().getOid() : null);
        t.setHakukohdeKoodistoNimi(s.getHakukohdeKoodistoNimi());
        t.setHakukohdeNimiUri(s.getHakukohdeNimi());
        t.setKaksoisTutkinto(s.isKaksoisTutkinto());
        t.setModified(s.getLastUpdateDate());
        t.setModifiedBy(s.getLastUpdatedByOid());
        t.setLiitteidenToimitusosoite(getConversionService().convert(s.getLiitteidenToimitusOsoite(), OsoiteRDTO.class));
        t.setLiitteidenToimitusPvm(s.getLiitteidenToimitusPvm());
        t.setLisatiedot(convertMonikielinenTekstiToMap(s.getLisatiedot()));
        t.setPainotettavatOppiaineet(convertPainotettavatOppianeet(s.getPainotettavatOppiaineet()));
        t.setSahkoinenToimitusOsoite(s.getSahkoinenToimitusOsoite());
        t.setTila(s.getTila() != null ? s.getTila().name() : null);
        t.setHakukohdeKoulutusOids(convertKoulutusOids(s.getKoulutusmoduuliToteutuses()));
        t.setValintakoes(convertValintakokeet(s.getValintakoes()));

        t.setValintojenAloituspaikatLkm(s.getValintojenAloituspaikatLkm());
        t.setYlinValintapistemaara(s.getYlinValintaPistemaara() != null ? s.getYlinValintaPistemaara().intValue() : 0);

        t.setKaytetaanHaunPaattymisenAikaa(s.isKaytetaanHaunPaattymisenAikaa());

        t.setLiitteet(convertLiitteet(s.getLiites()));

        if (s.getHakuaikaAlkuPvm() != null && s.getHakuaikaLoppuPvm() != null) {
            t.setKaytetaanHakukohdekohtaistaHakuaikaa(true);
            t.setHakuaikaAlkuPvm(s.getHakuaikaAlkuPvm());
            t.setHakuaikaLoppuPvm(s.getHakuaikaLoppuPvm());
        } else {
            t.setKaytetaanHakukohdekohtaistaHakuaikaa(false);
            if (s.getHakuaika() != null) {
                t.setHakuaikaAlkuPvm(s.getHakuaika().getAlkamisPvm());
                t.setHakuaikaLoppuPvm(s.getHakuaika().getPaattymisPvm());
            }
        }

        if (s.getSoraKuvaus() != null && s.getSoraKuvaus().getTekstiKaannos() != null) {

            HashMap<String,String> soraKuvaukset = new HashMap<String,String>();
            for (TekstiKaannos tekstiKaannos:s.getSoraKuvaus().getTekstiKaannos()) {

               soraKuvaukset.put(tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());
            }
            t.setSorakuvaus(soraKuvaukset);
        } else {
            LOG.debug("Hakukohde sorakuvaus was null : {}",t.getOid()) ;
        }

        if (s.getValintaperusteKuvaus() != null && s.getValintaperusteKuvaus().getTekstiKaannos() != null ) {

            HashMap<String,String> valintaperusteKuvaukset = new HashMap<String,String>();
            for (TekstiKaannos tekstiKaannos : s.getValintaperusteKuvaus().getTekstiKaannos()) {

                valintaperusteKuvaukset.put(tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());
            }
            t.setValintaperustekuvaus(valintaperusteKuvaukset);
        } else {
            LOG.debug("HAKUKOHDE valintaperustekuvaus was null : {}", t.getOid());
        }

        // HAKUKELPOISUUSVAATIMUS DESCRIPTION (relation + description from
        // koodisto)
        {
            String uri = getTarjontaKoodistoHelper().getHakukelpoisuusvaatimusrymaUriForHakukohde(s.getHakukohdeNimi());
            t.getHakukelpoisuusvaatimusUris().add(uri);
            t.setHakukelpoisuusvaatimus(getTarjontaKoodistoHelper().getKoodiMetadataKuvaus(uri));
        }

        // VALINTAPERUSTEKUVAUS DESCRIPTION (relation from koodisto, data from
        // metadata)
        {
            String uri = getTarjontaKoodistoHelper().getValintaperustekuvausryhmaUriForHakukohde(s.getHakukohdeNimi());
            t.setValintaperustekuvausKoodiUri(uri);
            if (t.getValintaperustekuvausKoodiUri() != null) {
                t.setValintaperustekuvaus(getMetadata(monikielinenMetadataDAO.findByAvainAndKategoria(
                        t.getValintaperustekuvausKoodiUri(), MetaCategory.VALINTAPERUSTEKUVAUS.name())));
            }
        }
        


        // SORAKUVAUS DESCRIPTION, (relation from koodisto, description data
        // from metadata)
        {
            String uri = getTarjontaKoodistoHelper().getSORAKysymysryhmaUriForHakukohde(s.getHakukohdeNimi());
            t.setSoraKuvausKoodiUri(uri);
            if (t.getSoraKuvausKoodiUri() != null) {
                t.setSorakuvaus(getMetadata(monikielinenMetadataDAO.findByAvainAndKategoria(t.getSoraKuvausKoodiUri(),
                        MetaCategory.SORA_KUVAUS.name())));
            }
        }

        //
        // Get the opetuskieli information - makes life easier for Team1.
        //
        Set<String> opetuskielis = new HashSet<String>();
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : s.getKoulutusmoduuliToteutuses()) {
            for (KoodistoUri koodistoUri : koulutusmoduuliToteutus.getOpetuskielis()) {
                opetuskielis.add(koodistoUri.getKoodiUri());
            }
        }
        t.setOpetuskielet(new ArrayList<String>(opetuskielis));

        return t;
    }

    private List<ValintakoeRDTO> convertValintakokeet(Set<Valintakoe> valintakoes) {
        List<ValintakoeRDTO> result = new ArrayList<ValintakoeRDTO>();

        for (Valintakoe valintakoe : valintakoes) {
            result.add(getConversionService().convert(valintakoe, ValintakoeRDTO.class));
        }

        return result.isEmpty() ? null : result;
    }

    private List<String> convertKoulutusOids(Set<KoulutusmoduuliToteutus> komotos) {

        if (komotos != null)  {
             List<String> komotoOids = new ArrayList<String>();
            for (KoulutusmoduuliToteutus komoto:komotos) {
                komotoOids.add(komoto.getOid());
            }
            return  komotoOids;
        } else {

            return null;
        }



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

        return result.isEmpty() ? null : result;
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

        return result.isEmpty() ? null : result;
    }

    /**
     * Extract metadata - key + category ("uri: soste-alue", "SORA") from many
     * languages.
     * 
     * @param metas
     * @return map if language keyed translations
     */
    private Map<String, String> getMetadata(List<MonikielinenMetadata> metas) {
        Map<String, String> result = new HashMap<String, String>();

        for (MonikielinenMetadata monikielinenMetadata : metas) {
            result.put(getTarjontaKoodistoHelper().convertKielikoodiToKieliUri(monikielinenMetadata.getKieli()),
                    monikielinenMetadata.getArvo());
        }

        return result.isEmpty() ? null : result;
    }
    
    private Map<String, String> getMap(MonikielinenTeksti valintaperusteKuvaus) {
        // TODO Auto-generated method stub
        return null;
    }


}
