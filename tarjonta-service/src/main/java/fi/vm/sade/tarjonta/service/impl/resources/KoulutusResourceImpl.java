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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.resources.KoulutusResource;
import fi.vm.sade.tarjonta.service.resources.dto.KoodiUriDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KoodiUriListDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KorkeakouluDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ToteutusDTO;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
                t.setKoulutuskoodi(convertKoodiUriToKoodiUriDTO(komo.getKoulutusKoodi(), DEMO_LOCALE, "tutkinto->koulutuskoodi"));
                break;
            case TUTKINTO_OHJELMA:
                final KoulutusasteTyyppi koulutusaste = EntityUtils.KoulutusTyyppiStrToKoulutusAsteTyyppi(komo.getKoulutustyyppi());
                switch (koulutusaste) {
                    case AMMATILLINEN_PERUSKOULUTUS:
                    case AMMATTIKORKEAKOULUTUS:
                        t.setKoulutusohjelma(convertKoodiUriToKoodiUriListDTO(komo.getKoulutusohjelmaKoodi(), DEMO_LOCALE, "tutkinto-ohjelma->koulutusohjelma"));
                        break;
                    case LUKIOKOULUTUS:
                        t.setKoulutusohjelma(convertKoodiUriToKoodiUriListDTO(komo.getLukiolinja(), DEMO_LOCALE, "tutkinto-ohjelma->lukiolinja"));
                        break;
                }

                t.setKoulutuskoodi(convertKoodiUriToKoodiUriDTO(komo.getKoulutusKoodi(), DEMO_LOCALE, "tutkinto-ohjelma->koulutuskoodi"));
                break;
        }
        return t;
    }

    private KoodiUriListDTO convertKoodiUriToKoodiUriListDTO(final String fromKoodiUri, final String langCode, final String fieldName) {
        KoodiUriListDTO koodiUriDto = new KoodiUriListDTO();
        convertKoodiUriToKoodiUriListDTO(fromKoodiUri, koodiUriDto, new Locale(langCode.toLowerCase()), fieldName);
        return koodiUriDto;
    }

    private KoodiUriDTO convertKoodiUriToKoodiUriDTO(final String fromKoodiUri, final String langCode, final String fieldName) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(langCode, "Locale object cannot be null. field in " + fieldName);

        KoodiUriDTO koodiUriDto = new KoodiUriDTO();
        final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);
        KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodiByUri, new Locale(langCode.toLowerCase()));

        final String[] koodiUriSplit = TarjontaKoodistoHelper.splitKoodiURIWithVersion(fromKoodiUri);
        koodiUriDto.setUri(koodiUriSplit[0]);
        koodiUriDto.setVersion(koodiUriSplit[1]);
        koodiUriDto.setTeksti(metadata.getNimi());
        koodiUriDto.setArvo(koodiByUri.getKoodiArvo());
        return koodiUriDto;
    }

    private void convertKoodiUriToKoodiUriListDTO(final String fromKoodiUri, final KoodiUriListDTO koodiUriListDto, final Locale locale, final String fieldName) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(locale, "Locale object cannot be null. field in " + fieldName);
        Preconditions.checkNotNull(koodiUriListDto, "KoodiUriListDTO object cannot be null in field " + fieldName);

        final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);

        KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodiByUri, locale);
        convertNimiToKoodiUriListDTO(metadata, koodiUriListDto);
        final String[] koodiUriSplit = TarjontaKoodistoHelper.splitKoodiURIWithVersion(fromKoodiUri);
        koodiUriListDto.setUri(koodiUriSplit[0]);
        koodiUriListDto.setVersion(koodiUriSplit[1]);
        koodiUriListDto.setArvo(koodiByUri.getKoodiArvo());
        convertAvailableLangs(koodiUriListDto, koodiByUri.getMetadata(), locale);
    }

    private void convertNimiToKoodiUriListDTO(final KoodiMetadataType koodi, final KoodiUriListDTO koodiUriListDto) {
        Preconditions.checkNotNull(koodi, "KoodiMetadataType object cannot be null.");
        Preconditions.checkNotNull(koodiUriListDto, "KoodiUriListDTO object cannot be null.");

        final String kieliUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(koodi.getKieli().value());
        koodiUriListDto.getTeksti().add(new MonikielinenTekstiTyyppi.Teksti(koodi.getNimi(), kieliUri));
    }

    private void convertAvailableLangs(final KoodiUriListDTO koodiUriDto, List<KoodiMetadataType> metadata, final Locale locale) {
        Preconditions.checkNotNull(koodiUriDto, "KoodiUriDTO object cannot be null.");
        for (KoodiMetadataType meta : metadata) {
            final String kieliUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(meta.getKieli().value());
            final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(kieliUri);
            final KoodiMetadataType koodiMetadataForLanguage = IndexDataUtils.getKoodiMetadataForLanguage(koodiByUri, locale);

            final MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti(koodiMetadataForLanguage.getNimi(), koodiByUri.getKoodiUri());
            koodiUriDto.getAvailableLanguages().add(teksti);
        }
    }

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
}
