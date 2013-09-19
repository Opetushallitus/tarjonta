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
package fi.vm.sade.tarjonta.service.impl.resources;

import com.google.common.base.Preconditions;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import static fi.vm.sade.tarjonta.service.business.impl.EntityUtils.copyFields;
import fi.vm.sade.tarjonta.service.resources.KoulutusResource;
import fi.vm.sade.tarjonta.service.resources.dto.KoodiUriDTO;
import fi.vm.sade.tarjonta.service.resources.dto.UiDTO;
import fi.vm.sade.tarjonta.service.resources.dto.UiListDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KorkeakouluDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ToteutusDTO;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jani Wilén
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KoulutusResourceImpl implements KoulutusResource {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusResourceImpl.class);
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    private static final String DEMO_LOCALE = "fi";

    @Override
    public String help() {
        return "/help (this REST resource help text)\n"
                + "/toteutus/<KOMO-OID>/\n"
                + "/tekstis?lang=<koodi-kieli-URI>\n"
                + "/tekstis/<KOMO-OID>\n";
    }

    @Override
    public ToteutusDTO getToteutus(final String komotoOid) {
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");
        LOG.info("OID : {}", komotoOid);
        KorkeakouluDTO t = new KorkeakouluDTO();
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(komotoOid);


        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        LOG.info("KOMO : {}", komo);

        t.setOid(komotoOid);
        t.setKomoOid(komo.getOid());
        t.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(komo.getModuuliTyyppi().name()));

        switch (komo.getModuuliTyyppi()) {
            case TUTKINTO:
                t.setKoulutuskoodi(convertKoodiUriToKoodiUriListDTO(komo.getKoulutusKoodi(), DEMO_LOCALE, "tutkinto->koulutuskoodi"));
                break;
            case TUTKINTO_OHJELMA:
                final KoulutusasteTyyppi koulutusaste = EntityUtils.KoulutusTyyppiStrToKoulutusAsteTyyppi(komo.getKoulutustyyppi());
                switch (koulutusaste) {
                    case AMMATILLINEN_PERUSKOULUTUS:
                        t.setKoulutusohjelma(convertKoodiUriToKoodiUriListDTO(komo.getKoulutusohjelmaKoodi(), DEMO_LOCALE, "tutkinto-ohjelma->koulutusohjelma"));
                        break;
                    case LUKIOKOULUTUS:
                        t.setKoulutusohjelma(convertKoodiUriToKoodiUriListDTO(komo.getLukiolinja(), DEMO_LOCALE, "tutkinto-ohjelma->lukiolinja"));
                        break;
                }

                t.setKoulutuskoodi(convertKoodiUriToKoodiUriListDTO(komo.getKoulutusKoodi(), DEMO_LOCALE, "tutkinto-ohjelma->koulutuskoodi"));

                t.setKoulutusohjelma(convertKoodiUriToKoodiUriListDTO(komo.getKoulutusohjelmaKoodi(), DEMO_LOCALE, "tutkinto-ohjelma->koulutusohjelma"));
                t.setTeemas(convertKoodiUriToKoodiUriListDTO(komoto.getTeemas(), DEMO_LOCALE, "tutkinto-ohjelma->teemas"));
                t.setOpetuskielis(convertKoodiUriToKoodiUriListDTO(komoto.getOpetuskielis(), DEMO_LOCALE, "tutkinto-ohjelma->teemas"));
                final String maksullisuus = komoto.getMaksullisuus();
                t.setOpintojenMaksullisuus(maksullisuus != null && maksullisuus.equals("true") ? true : false);
                t.setOpetusmuodos(convertKoodiUriToKoodiUriListDTO(komoto.getOpetusmuotos(), DEMO_LOCALE, "tutkinto-ohjelma->opetusmuodos"));
                t.setPohjakoulutusvaatimukset(convertKoodiUriToKoodiUriDTO(komoto.getPohjakoulutusvaatimus(), DEMO_LOCALE, "tutkinto-ohjelma->pohjakoulutusvaatimukset", true));
                t.setSuunniteltuKesto(komoto.getSuunniteltuKestoArvo());
                t.setSuunniteltuKestoTyyppi(convertKoodiUriToKoodiUriDTO(komoto.getSuunniteltuKestoYksikko(), DEMO_LOCALE, "tutkinto-ohjelma->SuunniteltuKestoTyyppi", true));
                t.setTunniste(komo.getUlkoinenTunniste());
                break;

        }

        Map<KomoTeksti, UiListDTO> tekstis = t.getTekstis();
        for (Entry<KomoTeksti, MonikielinenTeksti> e : komo.getTekstit().entrySet()) {
            UiListDTO dto = new UiListDTO();

            Collection<TekstiKaannos> tekstis1 = e.getValue().getTekstis();
            for (TekstiKaannos kaannos : tekstis1) {
                UiDTO uri = new UiDTO();
                uri.setKoodi(convertKoodiUri(kaannos.getKieliKoodi(), kaannos.getArvo()));
                uri.setTeksti(kaannos.getArvo());
                dto.getTekstis().add(uri);
            }
            tekstis.put(KomoTeksti.PATEVYYS, dto);
        }
        return t;
    }

    private UiListDTO convertKoodiUriToKoodiUriListDTO(final String fromKoodiUri, final String langCode, final String fieldName) {
        UiListDTO koodiUriDto = new UiListDTO();
        convertKoodiUriToKoodiUriListDTO(fromKoodiUri, koodiUriDto, new Locale(langCode.toLowerCase()), fieldName);
        return koodiUriDto;
    }

    private UiListDTO convertKoodiUriToKoodiUriListDTO(final Set<KoodistoUri> fromKoodiUris, final String langCode, final String fieldName) {
        UiListDTO koodiUriListDTO = new UiListDTO();
        for (KoodistoUri s : fromKoodiUris) {
            koodiUriListDTO.getTekstis().add(convertKoodiUriToKoodiUriDTO(s.getKoodiUri(), langCode, fieldName, false));
        }

        return koodiUriListDTO;
    }

    private UiDTO convertKoodiUriToKoodiUriDTO(final String fromKoodiUri, final String langCode, final String fieldName, boolean convertKoodiLang) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(langCode, "Locale object cannot be null. field in " + fieldName);

        UiDTO uiDto = new UiDTO();
        final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);

        KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodiByUri, new Locale(langCode.toLowerCase()));
        if (convertKoodiLang) {
            convertKoodiUri(fromKoodiUri, koodiByUri.getKoodiArvo());
        } else {
            uiDto.setKoodi(convertKoodiUri(fromKoodiUri, koodiByUri.getKoodiArvo()));
        }

        uiDto.setTeksti(metadata.getNimi());

        return uiDto;
    }

    private void convertKoodiUriToKoodiUriListDTO(final String fromKoodiUri, final UiListDTO koodiUriListDto, final Locale locale, final String fieldName) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(locale, "Locale object cannot be null. field in " + fieldName);
        Preconditions.checkNotNull(koodiUriListDto, "UiListDTO object cannot be null in field " + fieldName);

        final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);
        toKoodiUriDTO(koodiUriListDto, fromKoodiUri, koodiByUri, locale);
        addOtherLanguages(koodiUriListDto, koodiByUri.getMetadata(), locale);
    }

    private UiDTO toKoodiUriDTO(UiDTO uiDto, final String fromKoodiUri, final KoodiType koodiByUri, final Locale locale) {
        if (uiDto == null) {
            uiDto = new UiDTO();
        }

        uiDto.setKoodi(convertKoodiUri(fromKoodiUri, koodiByUri.getKoodiArvo()));
        KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodiByUri, locale);
        uiDto.setTeksti(metadata.getNimi());
        return uiDto;
    }

    private void addOtherLanguages(final UiListDTO koodiUriDto, List<KoodiMetadataType> metadata, final Locale locale) {
        Preconditions.checkNotNull(koodiUriDto, "KoodiUriDTO object cannot be null.");
        for (KoodiMetadataType meta : metadata) {
            final String kieliUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(meta.getKieli().value());

            UiDTO dto = new UiDTO();
            dto.setKoodi(convertKoodiUri(kieliUri, meta.getKieli().value()));
            dto.setTeksti(meta.getNimi());
            koodiUriDto.getTekstis().add(dto);
        }
    }
//    private void addOtherLanguages(final KoodiUriListDTO koodiUriDto, List<KoodiMetadataType> metadata, final Locale locale) {
//        Preconditions.checkNotNull(koodiUriDto, "KoodiUriDTO object cannot be null.");
//        for (KoodiMetadataType meta : metadata) {
//            final String kieliUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(meta.getKieli().value());
//            final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(kieliUri);
//            koodiUriDto.getTekstis().add(toKoodiUriDTO(null, meta.getNimi(), koodiByUri, locale));
//        }
//    }

    @Override
    public void updateToteutus(ToteutusDTO dto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createToteutus(ToteutusDTO dto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteToteutus(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String loadMonikielinenTekstis(String oid, String langUri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveMonikielinenTeksti(String oid, String langUri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteMonikielinenTeksti(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveKuva(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteKuva(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private KoodiUriDTO convertKoodiUri(final String koodistoKoodiUri, final String arvo) {
        KoodiUriDTO koodiUri = new KoodiUriDTO();
        final String[] koodiUriSplit = TarjontaKoodistoHelper.splitKoodiURIWithVersion(koodistoKoodiUri);
        koodiUri.setUri(koodiUriSplit[0]);
        koodiUri.setVersio(koodiUriSplit[1]);
        koodiUri.setArvo(arvo);

        return koodiUri;
    }
}
