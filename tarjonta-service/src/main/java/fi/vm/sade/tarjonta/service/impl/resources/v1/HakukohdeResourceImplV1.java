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
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.generic.service.exception.NotAuthorizedException;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.KuvausDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.publication.Tila.Tyyppi;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidationMessages;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidator;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.search.*;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.ParameterServices;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

/**
 * @author mlyly
 */
public class HakukohdeResourceImplV1 implements HakukohdeV1Resource {
    
    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImplV1.class);
    
    @Autowired(required = true)
    private PublicationDataService publication;
    
    @Autowired
    private HakuDAO hakuDAO;
    @Autowired
    private HakukohdeDAO hakukohdeDAO;
    
    @Autowired
    private KuvausDAO kuvausDAO;
    
    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    
    @Autowired(required = true)
    TarjontaSearchService tarjontaSearchService;
    
    @Autowired
    private IndexerResource indexerResource;
    
    @Autowired(required = true)
    private PublicationDataService publicationDataService;
    
    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    
    @Autowired
    private ConverterV1 converterV1;
    
    @Autowired
    private PermissionChecker permissionChecker;
    
    @Autowired
    private OidService oidService;
    
    @Autowired(required = true)
    private ContextDataService contextDataService;
    
    @Autowired(required = true)
    private ParameterServices parameterService;
    
    public final static String KOULUTUSASTE_KEY = "koulutusaste";
    
    public final static String KOULUTUSASTE_LUKIO = "LUKIOKOULUTUS";
    
    public final static String KOULUSTUSASTE_KK = "KORKEAKOULUTUS";
    
    public final static String KOULUTUSLAJI_KEY = "koulutuslaji";
    
    public final static String KOULUTUS_TOTEUTUS_TYYPPI = "toteutustyyppi";
    
    public final static String KOULUTUSLAJI_AIKUISET = "A";
    
    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> search(String searchTerms,
            List<String> organisationOids, List<String> hakukohdeTilas,
            String alkamisKausi, Integer alkamisVuosi, String hakukohdeOid, List<KoulutusasteTyyppi> koulutusastetyyppi, String hakuOid, String organisaatioRyhmaOid, List<ToteutustyyppiEnum> koulutustyypit) {
        
        organisationOids = organisationOids != null ? organisationOids
                : new ArrayList<String>();
        hakukohdeTilas = hakukohdeTilas != null ? hakukohdeTilas
                : new ArrayList<String>();
        
        HakukohteetKysely q = new HakukohteetKysely();
        q.setNimi(searchTerms);
        q.setKoulutuksenAlkamiskausi(alkamisKausi);
        q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
        q.getTarjoajaOids().addAll(organisationOids);
        if (hakukohdeOid != null) {
            q.setHakukohdeOid(hakukohdeOid);
        }
        
        if (hakuOid != null) {
            q.setHakuOid(hakuOid);
        }
        
        if (organisaatioRyhmaOid != null) {
            q.setOrganisaatioRyhmaOid(organisaatioRyhmaOid);
        }
        
        q.getKoulutusasteTyypit().addAll(koulutusastetyyppi);
        
        for (String s : hakukohdeTilas) {
            q.getTilat().add(
                    fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(s));
        }
        
        for (ToteutustyyppiEnum koulutustyyppi : koulutustyypit) {
            q.getKoulutustyyppi().add(koulutustyyppi.uri());
        }

        HakukohteetVastaus r = tarjontaSearchService.haeHakukohteet(q);
        
        r.setHakukohteet(filterRemovedHakukohteet(r.getHakukohteet()));
        
        return new ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>(converterV1.fromHakukohteetVastaus(r));
    }
    
    private List<HakukohdePerustieto> filterRemovedHakukohteet(List<HakukohdePerustieto> perustietosParam) {
        
        List<HakukohdePerustieto> perustiedot = new ArrayList<HakukohdePerustieto>();
        
        for (HakukohdePerustieto hkp : perustietosParam) {
            if (hkp.getTila() != fi.vm.sade.tarjonta.service.types.TarjontaTila.POISTETTU) {
                perustiedot.add(hkp);
            }
        }
        
        return perustiedot;
        
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HashMap<String, String>> findHakukohdeValintaperusteet(String hakukohdeOid) {
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
        
        if (hakukohde.getValintaperusteKuvaus() != null && hakukohde.getValintaperusteKuvaus().getTekstiKaannos() != null && hakukohde.getValintaperusteKuvaus().getTekstiKaannos().size() > 0) {
            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<HashMap<String, String>>();
            
            HashMap<String, String> tekstis = converterV1.convertMonikielinenTekstiToMap(hakukohde.getValintaperusteKuvaus(), false);
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(tekstis);
            
            return result;
            
        } else {
            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<HashMap<String, String>>();
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return result;
        }
        
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HakukohdeV1RDTO> findByUlkoinenTunniste(String tarjoajaOid, String ulkoinenTunniste) {
        
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByUlkoinenTunniste(ulkoinenTunniste, tarjoajaOid);
        
        ResultV1RDTO<HakukohdeV1RDTO> resultV1RDTO = new ResultV1RDTO<HakukohdeV1RDTO>();
        if (hakukohde != null) {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            resultV1RDTO.setResult(converterV1.toHakukohdeRDTO(hakukohde));
        } else {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        }
        return resultV1RDTO;
    }
    
    @Override
    @Transactional
    public ResultV1RDTO<HashMap<String, String>> insertHakukohdeValintaPerusteet(String hakukohdeOid, HashMap<String, String> valintaPerusteet) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
            MonikielinenTeksti valintaPerusteetMonikielinen = converterV1.convertMapToMonikielinenTeksti(valintaPerusteet);
            
            hakukohde.setValintaperusteKuvaus(valintaPerusteetMonikielinen);
            
            hakukohdeDAO.update(hakukohde);
            
            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<HashMap<String, String>>();
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(valintaPerusteet);
            return result;
        } catch (Exception exp) {
            ResultV1RDTO<HashMap<String, String>> errorResult = new ResultV1RDTO<HashMap<String, String>>();
            errorResult.addTechnicalError(exp);
            return errorResult;
        }
    }
    
    @Override
    @Transactional
    public ResultV1RDTO<HashMap<String, String>> insertHakukohdeSora(String hakukohdeOid, HashMap<String, String> sorat) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
            MonikielinenTeksti soraKuvaukset = converterV1.convertMapToMonikielinenTeksti(sorat);
            hakukohde.setSoraKuvaus(soraKuvaukset);
            
            hakukohdeDAO.update(hakukohde);
            
            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<HashMap<String, String>>();
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(sorat);
            return result;
            
        } catch (Exception exp) {
            ResultV1RDTO<HashMap<String, String>> errorResult = new ResultV1RDTO<HashMap<String, String>>();
            errorResult.addError(ErrorV1RDTO.createSystemError(exp, null, null));
            return errorResult;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HashMap<String, String>> findHakukohdeSoraKuvaukset(String hakukohdeOid) {
        try {
            
            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<HashMap<String, String>>();
            
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
            if (hakukohde.getSoraKuvaus() != null && hakukohde.getSoraKuvaus().getKaannoksetAsList() != null) {
                
                HashMap<String, String> resultHm = new HashMap<String, String>();
                for (TekstiKaannos tekstiKaannos : hakukohde.getSoraKuvaus().getKaannoksetAsList()) {
                    resultHm.put(tekstiKaannos.getKieliKoodi(), tekstiKaannos.getArvo());
                }
                result.setResult(resultHm);
            }
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            
            return result;
            
        } catch (Exception exp) {
            ResultV1RDTO<HashMap<String, String>> exceptionResult = new ResultV1RDTO<HashMap<String, String>>();
            exceptionResult.addTechnicalError(exp);
            return exceptionResult;
        }
    }
    
    @Override
    public ResultV1RDTO<List<OidV1RDTO>> search() {
        List<Hakukohde> hakukohdeList = hakukohdeDAO.findAll();
        
        List<OidV1RDTO> oidList = new ArrayList<OidV1RDTO>();
        if (hakukohdeList != null && hakukohdeList.size() > 0) {
            
            for (Hakukohde hakukohde : hakukohdeList) {
                
                OidV1RDTO oidi = new OidV1RDTO();
                oidi.setOid(hakukohde.getOid());
                oidList.add(oidi);
            }
            ResultV1RDTO<List<OidV1RDTO>> result = new ResultV1RDTO<List<OidV1RDTO>>();
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(oidList);
            return result;
        } else {
            ResultV1RDTO<List<OidV1RDTO>> result = new ResultV1RDTO<List<OidV1RDTO>>();
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return result;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HakukohdeV1RDTO> findByOid(String oid) {
        
        LOG.debug("HAKUKOHDE-REST V1 findByOid : ", oid);
        if (oid != null && oid.trim().length() > 0) {
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);
            if (hakukohde != null) {
                HakukohdeV1RDTO hakukohdeRDTO = converterV1.toHakukohdeRDTO(hakukohde);
                
                if (hakukohdeRDTO.getSoraKuvausTunniste() != null) {
                    hakukohdeRDTO.setSoraKuvaukset(getKuvauksetWithId(hakukohdeRDTO.getSoraKuvausTunniste(), hakukohdeRDTO.getSoraKuvausKielet()));
                }
                
                if (hakukohdeRDTO.getValintaPerusteKuvausTunniste() != null) {
                    hakukohdeRDTO.setValintaperusteKuvaukset(getKuvauksetWithId(hakukohdeRDTO.getValintaPerusteKuvausTunniste(), hakukohdeRDTO.getValintaPerusteKuvausKielet()));
                }
                updateKoulutusTypesToHakukohdeDto(hakukohdeRDTO);
                ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
                result.setResult(hakukohdeRDTO);
                result.setStatus(ResultV1RDTO.ResultStatus.OK);
                
                return result;
            } else {
                ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
                result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                return result;
                
            }
        } else {
            ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
            
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return result;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> findValintaperusteetByOid(@ApiParam(value = "Hakukohteen oid", required = true) String oid) {
        if (oid != null && oid.trim().length() > 0) {
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);
            if (hakukohde != null) {
                HakukohdeValintaperusteetV1RDTO dto = converterV1.valintaperusteetFromHakukohde(hakukohde);
                ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> result = new ResultV1RDTO<HakukohdeValintaperusteetV1RDTO>();
                result.setResult(dto);
                result.setStatus(ResultV1RDTO.ResultStatus.OK);
                return result;
            } else {
                return valintaperusteitaEiLoydy();
            }
        } else {
            return valintaperusteitaEiLoydy();
        }
    }
    
    private ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> valintaperusteitaEiLoydy() {
        ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> result = new ResultV1RDTO<HakukohdeValintaperusteetV1RDTO>();
        result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        return result;
    }
    
    private HashMap<String, String> getKoulutusKoulutusAstetyyppi(String komotoOid) {
        LOG.debug("TRYING TO GET KOULUTUSASTE AND LAJI WITH KOMOTO OID : {}", komotoOid);
        HashMap<String, String> koulutusAstetyyppi = new HashMap<String, String>();
        
        final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(komotoOid);
        
        koulutusAstetyyppi.put(KOULUTUSASTE_KEY, (komoto.getKoulutusmoduuli() != null && komoto.getKoulutusmoduuli().getKoulutustyyppiEnum() != null)
                ? komoto.getKoulutusmoduuli().getKoulutustyyppiEnum().getKoulutusasteTyyppi().name() : null);
        
        koulutusAstetyyppi.put(KOULUTUS_TOTEUTUS_TYYPPI, komoto.getToteutustyyppi().name());
        //TKatva, just get the first koulutuslaji because koulutus cannot have many koulutuslajis (aikuisten,nuorten)
        //or can it ?
        if (komoto.getKoulutuslajis() != null && komoto.getKoulutuslajis().size() > 0) {
            LOG.debug("KOULUTUSLAJI : {}", new ArrayList<KoodistoUri>(komoto.getKoulutuslajis()).get(0).getKoodiUri());
            KoodiType koulutuslajiKoodi = tarjontaKoodistoHelper.getKoodiByUri(new ArrayList<KoodistoUri>(komoto.getKoulutuslajis()).get(0).getKoodiUri());
            koulutusAstetyyppi.put(KOULUTUSLAJI_KEY, koulutuslajiKoodi.getKoodiArvo());
        } else {
            LOG.debug("KOULUTUSLAJI WAS NULL!!!");
            koulutusAstetyyppi.put(KOULUTUSLAJI_KEY, null);
        }
        
        return koulutusAstetyyppi;
    }
    
    private HashMap<String, String> getKuvauksetWithId(Long kuvausId, Set<String> kielet) {
        
        HashMap<String, String> kuvaukset = new HashMap<String, String>();
        LOG.debug("TRYING TO GET VALINTAPERUSTEKUVAUS WITH KIELET: " + kielet.size());
        ValintaperusteSoraKuvaus kuvaus = kuvausDAO.read(kuvausId);
        LOG.debug("FOUND " + kuvaus.getTekstis().size() + " VALINTAPERUSTEKUVAUS TEKSTIS");
        if (kielet != null) {
            for (MonikielinenMetadata meta : kuvaus.getTekstis()) {
                for (String kieli : kielet) {
                    if (kieli.trim().equals(meta.getKieli().trim())) {
                        kuvaukset.put(meta.getKieli(), meta.getArvo());
                    }
                }
            }
        }
        
        return kuvaukset;
    }
    
    private Hakuaika getHakuAikaForHakukohde(HakukohdeV1RDTO hakukohdeRDTO, Haku haku) {
        LOG.debug("HAKUAIKA ID : {}", hakukohdeRDTO.getHakuaikaId());
        if (haku.getHakuaikas() != null) {
            Hakuaika selectedHakuaika = null;
            Long hakuaikaId = Long.decode(hakukohdeRDTO.getHakuaikaId());
            LOG.debug("HAKUAIKAID LONG : {}", hakuaikaId);
            for (Hakuaika hakuaika : haku.getHakuaikas()) {
                
                if (hakuaika.getId().equals(hakuaikaId)) {
                    selectedHakuaika = hakuaika;
                }
            }
            LOG.debug("SELECTED HAKUAIKA : {}", selectedHakuaika);
            return selectedHakuaika;
            
        } else {
            LOG.warn("HAKU HAKUAIKAS IS NULL, IT SHOULD NOT BE!");
            return null;
        }
    }
    
    private String removeUriVersion(String uri) {
        
        if (uri.contains("#")) {
            
            StringTokenizer st = new StringTokenizer(uri, "#");
            
            return st.nextToken();
            
        } else {
            return uri;
        }
        
    }
    
    private boolean doesHakukohdeExist(HakukohdeV1RDTO hakukohdeV1RDTO, Haku haku) {
        
        List<Hakukohde> hakukohdes = hakukohdeDAO.findByTermYearAndProvider(haku.getHakukausiUri(), haku.getHakukausiVuosi(), hakukohdeV1RDTO.getTarjoajaOids().iterator().next());
        
        if (hakukohdes != null) {
            boolean wasFound = false;
            for (Hakukohde hakukohde : hakukohdes) {
                
                for (TekstiKaannos tekstiKaannos : hakukohde.getHakukohdeMonikielinenNimi().getKaannoksetAsList()) {
                    LOG.debug("TRYING TO GET HAKUKOHDE NIMI WITH : {} ", hakukohdeV1RDTO.getHakukohteenNimet().get(removeUriVersion(tekstiKaannos.getKieliKoodi())));
                    String hakukohdeNimi = hakukohdeV1RDTO.getHakukohteenNimet().get(removeUriVersion(tekstiKaannos.getKieliKoodi()));
                    LOG.debug("CHECKING HAKUKOHDE NIMI : {}", hakukohdeNimi);
                    if (hakukohdeNimi != null && hakukohdeNimi.trim().equals(tekstiKaannos.getArvo())) {
                        LOG.debug("HAKUKOHDE NAME MATCHES : {} TO {}", hakukohdeNimi, tekstiKaannos.getArvo());
                        wasFound = true;
                    }
                }
            }
            return wasFound;
        } else {
            return false;
        }
        
    }
    
    private List<HakukohdeValidationMessages> validateHakukohde(
            HakukohdeV1RDTO hakukohdeV1RDTO) {
        
        final List<HakukohdeValidationMessages> validationMessageses = new ArrayList<HakukohdeValidationMessages>();
        
        ToteutustyyppiEnum toteutustyyppi;
        
        Preconditions.checkNotNull(hakukohdeV1RDTO.getToteutusTyyppi(), "toteutustyyppi == null");
        try {
            toteutustyyppi = ToteutustyyppiEnum.valueOf(hakukohdeV1RDTO.getToteutusTyyppi());
        } catch (IllegalArgumentException iae) {
            LOG.error("Toteutustyyppi:" + hakukohdeV1RDTO.getToteutusTyyppi() + " is unknown?!");
            validationMessageses.add(HakukohdeValidationMessages.HAKUKOHDE_UNKNOWN_TOTEUTUSTYYPPI);
            return validationMessageses;
        }

        //validointi
        switch (toteutustyyppi) {
            
            case KORKEAKOULUTUS:
                validationMessageses.addAll(HakukohdeValidator
                        .validateHakukohde(hakukohdeV1RDTO));
                break;
            
            case AMMATILLINEN_PERUSTUTKINTO:
            case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
            case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
            case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA:
            case ERIKOISAMMATTITUTKINTO:
            case AMMATTITUTKINTO:
            case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
                validationMessageses.addAll(HakukohdeValidator
                        .validateAikuLukioHakukohde(hakukohdeV1RDTO));
                break;
            case LUKIOKOULUTUS:
                validationMessageses.addAll(HakukohdeValidator
                        .validateAikuLukioHakukohde(hakukohdeV1RDTO));
                break;
            case PERUSOPETUKSEN_LISAOPETUS:
                validationMessageses.addAll(HakukohdeValidator
                        .validateAikuLukioHakukohde(hakukohdeV1RDTO));
                break;
            case AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
                validationMessageses.addAll(HakukohdeValidator
                        .validateAikuLukioHakukohde(hakukohdeV1RDTO));
                break;
            case MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
                validationMessageses.addAll(HakukohdeValidator
                        .validateAikuLukioHakukohde(hakukohdeV1RDTO));
                break;
            case MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
                validationMessageses.addAll(HakukohdeValidator
                        .validateAikuLukioHakukohde(hakukohdeV1RDTO));
                break;
            case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
                validationMessageses.addAll(HakukohdeValidator
                        .validateAikuLukioHakukohde(hakukohdeV1RDTO));
                break;
            case VAPAAN_SIVISTYSTYON_KOULUTUS:
                validationMessageses.addAll(HakukohdeValidator
                        .validateAikuLukioHakukohde(hakukohdeV1RDTO));
                break;
            default:
                LOG.error("Toteutustyyppi:" + toteutustyyppi + " validation rules not implemented");
                validationMessageses
                        .add(HakukohdeValidationMessages.HAKUKOHDE_NOT_IMPLEMENTED);
                break;
            
        }
        
        return validationMessageses;
    }
    
    @Override
    @Transactional
    public ResultV1RDTO<HakukohdeV1RDTO> createHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {
        permissionChecker.checkCreateHakukohde(hakukohdeRDTO.getHakuOid(), hakukohdeRDTO.getHakukohdeKoulutusOids());
        String hakuOid = hakukohdeRDTO.getHakuOid();
        Date today = new Date();
        List<HakukohdeValidationMessages> validationMessageses = validateHakukohde(hakukohdeRDTO);
        
        if (hakukohdeRDTO.getHakukohdeKoulutusOids() == null || hakukohdeRDTO.getHakukohdeKoulutusOids().size() < 1) {
            LOG.warn("HAKUKOHDE KOULUTUS OIDS SHOULD NOT BE NULL!!!");
        }
        
        Set<KoulutusmoduuliToteutus> komotot = Sets.newHashSet(koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(hakukohdeRDTO.getHakukohdeKoulutusOids()));
        
        validationMessageses.addAll(HakukohdeValidator.checkKoulutukset(komotot));
        
        if (hakukohdeRDTO.getOid() != null) { //uudessa hakukohteessa ei saa olla oidia
            validationMessageses.add(HakukohdeValidationMessages.HAKUKOHDE_OID_SPECIFIED);
        }
        
        if (validationMessageses.size() > 0) {
            return populateValidationErrors(hakukohdeRDTO, validationMessageses);
        }
        
        Hakukohde hakukohde = converterV1.toHakukohde(hakukohdeRDTO);

        //oidi
        String newHakukohdeOid = null;
        LOG.debug("OIDSERVICE: {}", oidService);
        try {
            newHakukohdeOid = oidService.get(TarjontaOidType.HAKUKOHDE);
            LOG.debug("OID SERVICE NEW OID : {}", newHakukohdeOid);
        } catch (OIDCreationException emm) {
            LOG.warn("UNABLE TO GET OID : {}", emm.toString());
        }
        
        hakukohde.setOid(newHakukohdeOid);
        hakukohde.setLastUpdateDate(today);
        hakukohde.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
        
        Haku haku = hakuDAO.findByOid(hakuOid);
        hakukohde.setHaku(haku);
        if (hakukohdeRDTO.getHakuaikaId() != null) {
            hakukohde.setHakuaika(getHakuAikaForHakukohde(hakukohdeRDTO, haku));
        }
        
        hakukohde = hakukohdeDAO.insert(hakukohde);
        setHakukohde(komotot, hakukohde);
        
        hakukohdeDAO.update(hakukohde);
        
        indexerResource.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
        indexerResource.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), new Function<KoulutusmoduuliToteutus, Long>() {
            public Long apply(@Nullable KoulutusmoduuliToteutus arg0) {
                return arg0.getId();
            }
        })));
        publicationDataService.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);

        //hakukohdeRDTO.setOid(hakukohde.getOid());
        ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
        result.setStatus(ResultV1RDTO.ResultStatus.OK);
        // hakukohdeRDTO.setModified(today);
        HakukohdeV1RDTO toHakukohdeRDTO = converterV1.toHakukohdeRDTO(hakukohde);
        updateKoulutusTypesToHakukohdeDto(toHakukohdeRDTO);
        result.setResult(toHakukohdeRDTO);
        return result;
    }
    
    @Override
    @Transactional
    public ResultV1RDTO<HakukohdeV1RDTO> updateHakukohde(String hakukohdeOid, HakukohdeV1RDTO hakukohdeRDTO) {
        permissionChecker.checkUpdateHakukohde(hakukohdeOid, hakukohdeRDTO.getHakuOid(), hakukohdeRDTO.getHakukohdeKoulutusOids());
        try {
            
            Date today = new Date();
            //LOG.info("TRY UPDATE HAKUKOHDE {}", hakukohdeOid);
            String hakuOid = hakukohdeRDTO.getHakuOid();
            
            List<HakukohdeValidationMessages> validationMessagesList = validateHakukohde(hakukohdeRDTO);
            if (hakukohdeRDTO.getValintakokeet() != null && !hakukohdeRDTO.getValintakokeet().isEmpty()) {
                HakukohdeValidator.validateValintakokees(hakukohdeRDTO.getValintakokeet());
            }
            
            Set<KoulutusmoduuliToteutus> komotot = Sets.newHashSet(koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(hakukohdeRDTO.getHakukohdeKoulutusOids()));
            
            validationMessagesList.addAll(HakukohdeValidator.checkKoulutukset(komotot));
            
            if (validationMessagesList.size() > 0) {
                return populateValidationErrors(hakukohdeRDTO, validationMessagesList);
            }
            
            Hakukohde hakukohde = converterV1.toHakukohde(hakukohdeRDTO);
            hakukohde.setLastUpdateDate(today);
            hakukohde.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
            Hakukohde hakukohdeTemp = hakukohdeDAO
                    .findHakukohdeByOid(hakukohdeRDTO.getOid());

            hakukohde.setId(hakukohdeTemp.getId());
            hakukohde.setVersion(hakukohdeTemp.getVersion());

            if (hakukohde.getValintaPerusteKuvausTunniste() != null) {
                hakukohde.setValintaperusteKuvaus(null);
            }
            
            if (hakukohde.getSoraKuvausTunniste() != null) {
                hakukohde.setSoraKuvaus(null);
            }
            
            Haku haku = hakuDAO.findByOid(hakuOid);
            
            hakukohde.setHaku(haku);
            
            if (hakukohdeRDTO.getHakuaikaId() != null) {
                hakukohde.setHakuaika(getHakuAikaForHakukohde(hakukohdeRDTO,
                        haku));
            }
            
            LOG.info("Hakukohde.liitteet = {}", hakukohde.getLiites());
            LOG.info("Hakukohde.kokeet = {}", hakukohde.getValintakoes());
            
            setHakukohde(komotot, hakukohde);
            
            Tila tilamuutos = new Tila(Tyyppi.HAKUKOHDE, TarjontaTila.valueOf(hakukohdeRDTO.getTila()), hakukohde.getOid());
            
            if (publicationDataService.isValidStatusChange(tilamuutos)) {
                
                hakukohdeDAO.update(hakukohde);
                LOG.info("Hakukohde.liitteet -> {}", hakukohde.getLiites());
                LOG.info("Hakukohde.kokeet -> {}", hakukohde.getValintakoes());
                LOG.info("Hakukohde", hakukohde);
                indexerResource.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
                indexerResource.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), new Function<KoulutusmoduuliToteutus, Long>() {
                    public Long apply(@Nullable KoulutusmoduuliToteutus arg0) {
                        return arg0.getId();
                    }
                })));
                publicationDataService.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);
                
                ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
                result.setStatus(ResultV1RDTO.ResultStatus.OK);
                
                HakukohdeV1RDTO toHakukohdeRDTO = converterV1.toHakukohdeRDTO(hakukohdeDAO.findHakukohdeByOid(hakukohde.getOid()));
                updateKoulutusTypesToHakukohdeDto(toHakukohdeRDTO);
                result.setResult(toHakukohdeRDTO);
                return result;
            } else {
                return populateValidationErrors(hakukohdeRDTO, Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_TILA_WRONG));
            }
        } catch (Exception e) {
            LOG.warn("Exception occured while updating hakukohde " + hakukohdeOid, e);
            ResultV1RDTO<HakukohdeV1RDTO> errorResult = new ResultV1RDTO<HakukohdeV1RDTO>();
            errorResult.addTechnicalError(e);
            return errorResult;
        }
    }
    
    @Override
    @Transactional
    public ResultV1RDTO<Boolean> deleteHakukohde(String oid) {
        permissionChecker.checkRemoveHakukohde(oid);
        try {
            LOG.debug("REMOVING HAKUKOHDE : " + oid);
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);
            
            if (hakukohde != null && !hakukohde.getTila().isRemovable()) {
                ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<Boolean>();
                errorResult.addError(ErrorV1RDTO.createValidationError("hakukohde.invalid.transition", HakukohdeValidationMessages.HAKUKOHDE_INVALID_TRANSITION.toString().toLowerCase()));
                return errorResult;
            }
            hakukohdeDAO.safeDelete(hakukohde.getOid(), contextDataService.getCurrentUserOid());
            List<Long> hakukohdeIds = new ArrayList<Long>();
            hakukohdeIds.add(hakukohde.getId());
            indexerResource.indexHakukohteet(hakukohdeIds);
            ResultV1RDTO<Boolean> result = new ResultV1RDTO<Boolean>();
            result.setResult(true);
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            return result;
        } catch (Exception exp) {
            LOG.warn("Exception occured when removing hakukohde {}, exception : {}", oid, exp.toString());
            ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<Boolean>();
            errorResult.addTechnicalError(exp);
            return errorResult;
        }
    }
    
    @Override
    @Transactional
    public ResultV1RDTO<List<ValintakoeV1RDTO>> findHakukohdeValintakoes(String hakukohdeOid) {
        
        if (hakukohdeOid != null) {
            
            ResultV1RDTO<List<ValintakoeV1RDTO>> resultRDTO = new ResultV1RDTO<List<ValintakoeV1RDTO>>();
            
            if (hakukohdeOid == null) {
                resultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
                errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
                errorRDTO.setErrorField("hakukohdeOid");
                errorRDTO.setErrorTechnicalInformation("Hakukohde oid cannot be null");
                resultRDTO.addError(errorRDTO);
                return resultRDTO;
            } else {
                try {
                    
                    List<ValintakoeV1RDTO> valintakoeV1RDTOs = new ArrayList<ValintakoeV1RDTO>();
                    List<Valintakoe> valintakokees = hakukohdeDAO.findValintakoeByHakukohdeOid(hakukohdeOid);
                    for (Valintakoe valintakoe : valintakokees) {
                        ValintakoeV1RDTO valintakoeV1RDTO = converterV1.fromValintakoe(valintakoe);
                        
                        valintakoeV1RDTOs.add(valintakoeV1RDTO);
                    }
                    resultRDTO.setResult(valintakoeV1RDTOs);
                    resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                    
                } catch (Exception exp) {
                    resultRDTO.addTechnicalError(exp);
                }
                return resultRDTO;
                
            }
        } else {
            ResultV1RDTO<List<ValintakoeV1RDTO>> resultRDTO = new ResultV1RDTO<List<ValintakoeV1RDTO>>();
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return resultRDTO;
        }
        
    }
    
    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<ValintakoeV1RDTO> insertValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            Valintakoe valintakoe = converterV1.toValintakoe(valintakoeV1RDTO);
            if (hakukohdeOid != null && valintakoe != null) {
                LOG.debug("INSERTING VALINTAKOE : {} with kieli : {}", valintakoe.getValintakoeNimi(), valintakoe.getKieli());
                List<Valintakoe> valintakoes = hakukohdeDAO.findValintakoeByHakukohdeOid(hakukohdeOid);
                valintakoes.add(valintakoe);
                hakukohdeDAO.updateValintakoe(valintakoes, hakukohdeOid);
                ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<ValintakoeV1RDTO>();
                ValintakoeV1RDTO result = converterV1.fromValintakoe(valintakoe);
                rdtoResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                rdtoResultRDTO.setResult(result);
                return rdtoResultRDTO;
            } else {
                ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<ValintakoeV1RDTO>();
                rdtoResultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
                errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
                errorRDTO.setErrorTechnicalInformation("Hakukohde cannot be null when inserting valintakoe");
                rdtoResultRDTO.addError(errorRDTO);
                return rdtoResultRDTO;
                
            }
            
        } catch (Exception exp) {
            ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<ValintakoeV1RDTO>();
            rdtoResultRDTO.addTechnicalError(exp);
            return rdtoResultRDTO;
        }
        
    }
    
    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<ValintakoeV1RDTO> updateValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            Valintakoe valintakoe = converterV1.toValintakoe(valintakoeV1RDTO);
            
            List<ValintakoeV1RDTO> valintakokees = new ArrayList<ValintakoeV1RDTO>();
            valintakokees.add(valintakoeV1RDTO);
            
            List<HakukohdeValidationMessages> validationMessageses = HakukohdeValidator.validateValintakokees(valintakokees);
            if (validationMessageses.size() > 0) {
                ResultV1RDTO<ValintakoeV1RDTO> errorResult = new ResultV1RDTO<ValintakoeV1RDTO>();
                errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
                for (HakukohdeValidationMessages message : validationMessageses) {
                    errorResult.addError(ErrorV1RDTO.createValidationError(null, message.name(), null));
                }
                errorResult.setResult(valintakoeV1RDTO);
                return errorResult;
            }
            
            LOG.debug("UPDATEVALINTAKOE SIZE: {} ", valintakoe.getAjankohtas().size());
            
            hakukohdeDAO.updateSingleValintakoe(valintakoe, hakukohdeOid);
            LOG.debug("UPDATED VALINTAKOE");
            ResultV1RDTO<ValintakoeV1RDTO> valintakoeResult = new ResultV1RDTO<ValintakoeV1RDTO>();
            valintakoeResult.setStatus(ResultV1RDTO.ResultStatus.OK);
            valintakoeResult.setResult(valintakoeV1RDTO);
            return valintakoeResult;
            
        } catch (Exception exp) {
            ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<ValintakoeV1RDTO>();
            rdtoResultRDTO.addTechnicalError(exp);
            return rdtoResultRDTO;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<Boolean> removeValintakoe(String hakukohdeOid, String valintakoeOid) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            LOG.debug("REMOVEVALINTAKOE: {}", valintakoeOid);
            Valintakoe valintakoe = hakukohdeDAO.findValintaKoeById(valintakoeOid);
            hakukohdeDAO.removeValintakoe(valintakoe);
            
            ResultV1RDTO<Boolean> resultRDTO = new ResultV1RDTO<Boolean>();
            resultRDTO.setResult(true);
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            return resultRDTO;
            
        } catch (Exception exp) {
            ResultV1RDTO<Boolean> resultRDTO = new ResultV1RDTO<Boolean>();
            resultRDTO.setResult(false);
            resultRDTO.addTechnicalError(exp);
            return resultRDTO;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> findHakukohdeLiites(String hakukohdeOid) {
        
        if (hakukohdeOid != null) {
            try {
                
                ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> listResultRDTO = new ResultV1RDTO<List<HakukohdeLiiteV1RDTO>>();
                
                List<HakukohdeLiite> liites = hakukohdeDAO.findHakukohdeLiitesByHakukohdeOid(hakukohdeOid);
                List<HakukohdeLiiteV1RDTO> liiteV1RDTOs = new ArrayList<HakukohdeLiiteV1RDTO>();
                if (liites != null) {
                    LOG.debug("LIITES SIZE : {} ", liites.size());
                    for (HakukohdeLiite liite : liites) {
                        liiteV1RDTOs.add(converterV1.fromHakukohdeLiite(liite));
                    }
                }
                
                listResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                listResultRDTO.setResult(liiteV1RDTOs);
                return listResultRDTO;
                
            } catch (Exception exp) {
                ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> errorResult = new ResultV1RDTO<List<HakukohdeLiiteV1RDTO>>();
                errorResult.addTechnicalError(exp);
                return errorResult;
            }
        } else {
            ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> errorResult = new ResultV1RDTO<List<HakukohdeLiiteV1RDTO>>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return errorResult;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> insertHakukohdeLiite(String hakukohdeOid, HakukohdeLiiteV1RDTO liiteV1RDTO) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
            
            List<HakukohdeValidationMessages> validationMessageses = HakukohdeValidator.validateLiite(liiteV1RDTO);
            if (validationMessageses.size() > 0) {
                return populateValidationErrors(liiteV1RDTO, validationMessageses);
            }
            
            ResultV1RDTO<HakukohdeLiiteV1RDTO> resultRDTO = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
            HakukohdeLiite hakukohdeLiite = converterV1.toHakukohdeLiite(liiteV1RDTO);
            List<HakukohdeLiite> liites = hakukohdeDAO.findHakukohdeLiitesByHakukohdeOid(hakukohdeOid);
            liites.add(hakukohdeLiite);
            hakukohdeDAO.insertLiittees(liites, hakukohdeOid);
            
            resultRDTO.setResult(converterV1.fromHakukohdeLiite(hakukohdeLiite));
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            return resultRDTO;
            
        } catch (Exception exp) {
            ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResult = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
            errorResult.addTechnicalError(exp);
            return errorResult;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> updateHakukohdeLiite(String hakukohdeOid, HakukohdeLiiteV1RDTO liiteV1RDTO) {
        
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
            
            List<HakukohdeValidationMessages> validationMessageses = HakukohdeValidator.validateLiite(liiteV1RDTO);
            if (validationMessageses.size() > 0) {
                return populateValidationErrors(liiteV1RDTO, validationMessageses);
            }
            
            ResultV1RDTO<HakukohdeLiiteV1RDTO> resultRDTO = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
            
            HakukohdeLiite hakukohdeLiite = converterV1.toHakukohdeLiite(liiteV1RDTO);
            
            hakukohdeDAO.updateLiite(hakukohdeLiite, hakukohdeOid);
            
            resultRDTO.setResult(converterV1.fromHakukohdeLiite(hakukohdeLiite));
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            
            return resultRDTO;
            
        } catch (Exception exp) {
            ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResultDto = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
            errorResultDto.addTechnicalError(exp);
            errorResultDto.setStatus(ResultV1RDTO.ResultStatus.OK); //why oh why?
            return errorResultDto;
        }
        
    }

    /**
     * Populate response with result and validation error messages.
     *
     * @param result
     * @param validationMessageses
     * @return
     */
    private <T> ResultV1RDTO<T> populateValidationErrors(
            T result,
            List<HakukohdeValidationMessages> validationMessageses) {
        ResultV1RDTO<T> errorResult = new ResultV1RDTO<T>();
        errorResult.setResult(result);
        errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
        for (HakukohdeValidationMessages msg : validationMessageses) {
            errorResult.addError(ErrorV1RDTO.createValidationError(null, msg.name(), null));
        }
        return errorResult;
    }
    
    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<Boolean> deleteHakukohdeLiite(String hakukohdeOid, String liiteId) {
        
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            
            HakukohdeLiite hakukohdeLiite = hakukohdeDAO.findHakuKohdeLiiteById(liiteId);
            
            if (hakukohdeLiite != null && hakukohdeLiite.getId() != null) {
                
                hakukohdeDAO.removeHakukohdeLiite(hakukohdeLiite);
                
                ResultV1RDTO<Boolean> booleanResultRDTO = new ResultV1RDTO<Boolean>();
                booleanResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                booleanResultRDTO.setResult(true);
                return booleanResultRDTO;
            } else {
                ResultV1RDTO<Boolean> booleanResultRDTO = new ResultV1RDTO<Boolean>();
                booleanResultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                booleanResultRDTO.setResult(false);
                return booleanResultRDTO;
            }
            
        } catch (Exception exp) {
            ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<Boolean>();
            errorResult.setResult(false);
            errorResult.addTechnicalError(exp);
            return errorResult;
        }
        
    }
    
    private void setHakukohde(Set<KoulutusmoduuliToteutus> komotot, Hakukohde hk) {
        hk.setKoulutusmoduuliToteutuses(komotot);
        for (KoulutusmoduuliToteutus komoto : komotot) {
            komoto.addHakukohde(hk);
        }
    }
    
    @Override
    @Transactional(readOnly = false)
    public ResultV1RDTO<Tilamuutokset> updateTila(String oid, TarjontaTila tila) {
        
        Tila tilamuutos = new Tila(Tyyppi.HAKUKOHDE, tila, oid);
        Tilamuutokset tm = null;
        try {
            tm = publicationDataService.updatePublicationStatus(Lists.newArrayList(tilamuutos));
        } catch (IllegalArgumentException iae) {
            ResultV1RDTO<Tilamuutokset> r = new ResultV1RDTO<Tilamuutokset>();
            r.addError(ErrorV1RDTO.createValidationError(null, iae.getMessage()));
            return r;
        }

        //indeksoi uudelleen muuttunut data
        indexerResource.indexMuutokset(tm);
        
        return new ResultV1RDTO<Tilamuutokset>(tm);
    }
    
    @Override
    public ResultV1RDTO<List<NimiJaOidRDTO>> getKoulutukset(String oid) {
        KoulutuksetKysely ks = new KoulutuksetKysely();
        ks.getHakukohdeOids().add(oid);
        
        KoulutuksetVastaus kv = tarjontaSearchService.haeKoulutukset(ks);
        List<NimiJaOidRDTO> ret = new ArrayList<NimiJaOidRDTO>();
        for (KoulutusPerustieto kp : kv.getKoulutukset()) {
            ret.add(new NimiJaOidRDTO(kp.getNimi(), kp.getKomotoOid()));
        }
        return new ResultV1RDTO<List<NimiJaOidRDTO>>(ret);
    }
    
    @Override
    @Transactional(readOnly = false)
    public ResultV1RDTO<List<String>> lisaaKoulutuksesToHakukohde(String hakukohdeOid, List<String> koulutukses) {
        
        LOG.info("lisätään koulutuksia ghakukohteelle" + koulutukses);
        
        ResultV1RDTO<List<String>> resultV1RDTO = new ResultV1RDTO<List<String>>();
        
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
        if (!parameterService.parameterCanAddHakukohdeToHaku(hakukohde.getHaku().getOid())) {
            throw new NotAuthorizedException("no.permission");
        }
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohde.getOid());
        
        Set<KoulutusmoduuliToteutus> liitettavatKomotot = Sets.newHashSet(koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(koulutukses));
        Set<KoulutusmoduuliToteutus> kaikkiKoulutukset = Sets.newHashSet(liitettavatKomotot);
        kaikkiKoulutukset.addAll(hakukohde.getKoulutusmoduuliToteutuses());
        
        List<HakukohdeValidationMessages> validationMessages = HakukohdeValidator.checkKoulutukset(kaikkiKoulutukset);
        if (validationMessages.size() > 0) {
            return populateValidationErrors(null, validationMessages);
        }
        
        if (liitettavatKomotot != null && liitettavatKomotot.size() > 0) {
            
            for (KoulutusmoduuliToteutus komoto : liitettavatKomotot) {
                
                hakukohde.addKoulutusmoduuliToteutus(komoto);
                
                komoto.addHakukohde(hakukohde);
                
                koulutusmoduuliToteutusDAO.update(komoto);
                
                LOG.info("indexing koulutukset");
                indexerResource.indexKoulutukset(Lists.newArrayList(komoto.getId()));
            }
            
            hakukohdeDAO.update(hakukohde);
            LOG.info("indexing hakukohde");
            indexerResource.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
            
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            
        } else {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            
        }
        
        return resultV1RDTO;
    }
    
    @Override
    @Transactional(readOnly = false)
    public ResultV1RDTO<List<String>> removeKoulutuksesFromHakukohde(String hakukohdeOid, List<String> koulutukses) {
        
        ResultV1RDTO<List<String>> resultV1RDTO = new ResultV1RDTO<List<String>>();
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
        
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohde.getOid());
        if (hakukohde != null) {
            
            List<KoulutusmoduuliToteutus> komotoToRemove = new ArrayList<KoulutusmoduuliToteutus>();
            
            for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
                LOG.debug("Looping hakukohde komoto : {}", komoto.getOid());
                for (String komotoOid : koulutukses) {
                    if (komoto.getOid().trim().equals(komotoOid)) {
                        komotoToRemove.add(komoto);
                    }
                }
            }
            
            if (komotoToRemove.size() > 0) {
                Collection<KoulutusmoduuliToteutus> remainingKomotos = CollectionUtils.subtract(hakukohde.getKoulutusmoduuliToteutuses(), komotoToRemove);
                
                List<String> remainingOids = Lists.newArrayList(Iterables.transform(remainingKomotos, new Function<KoulutusmoduuliToteutus, String>() {
                    public String apply(@Nullable KoulutusmoduuliToteutus input) {
                        return input.getOid();
                    }
                }));
                
                permissionChecker.checkUpdateHakukohde(hakukohdeOid, hakukohde.getHaku().getOid(), remainingOids);
                
                LOG.debug("Removed {} koulutukses from hakukohde : {}", komotoToRemove.size(), hakukohde.getOid());
                if (remainingKomotos.size() > 0) {
                    
                    for (KoulutusmoduuliToteutus komoto : komotoToRemove) {
                        
                        komoto.removeHakukohde(hakukohde);
                        
                        hakukohde.removeKoulutusmoduuliToteutus(komoto);
                        
                        koulutusmoduuliToteutusDAO.update(komoto);
                        LOG.info("indexing koulutus");
                        indexerResource.indexKoulutukset(Lists.newArrayList(komoto.getId()));
                    }
                    
                    LOG.debug("Hakukohde has more koulutukses, updating it");
                    hakukohdeDAO.update(hakukohde);
                    LOG.info("indexing hk");
                    indexerResource.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
                } else {
                    
                    List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKoulutusModuulisWithHakukohdesByOids(koulutukses);
                    
                    for (KoulutusmoduuliToteutus komoto : komotos) {
                        komoto.removeHakukohde(hakukohde);
                        hakukohde.removeKoulutusmoduuliToteutus(komoto);
                        indexerResource.indexKoulutukset(Lists.newArrayList(komoto.getId()));
                    }
                    
                    LOG.debug("Hakukohde does not have anymore koulutukses, removing it");
                    hakukohdeDAO.remove(hakukohde);
                    try {
                        indexerResource.deleteHakukohde(Lists.newArrayList(hakukohdeOid));
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                }
                
            }
            
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            resultV1RDTO.setResult(koulutukses);
            
        } else {
            
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            resultV1RDTO.setResult(koulutukses);
            
        }
        
        return resultV1RDTO;
        
    }
    
    @Override
    public ResultV1RDTO<Boolean> isStateChangePossible(String oid,
                                                       TarjontaTila tila) {
        Tila tilamuutos = new Tila(Tyyppi.HAKUKOHDE, tila, oid);
        return new ResultV1RDTO<Boolean>(publication.isValidStatusChange(tilamuutos));
    }

    // POST /hakukohde/ryhmat/lisaa
    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<Boolean> lisaaRyhmatHakukohteille(List<HakukohdeRyhmaV1RDTO> data) {
        LOG.info("lisaaRyhmatHakukohteille()");
        
        ResultV1RDTO<Boolean> result = new ResultV1RDTO<Boolean>();

        // By default we fail :)
        result.setResult(Boolean.FALSE);
        
        for (HakukohdeRyhmaV1RDTO operation : data) {
            
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(operation.getHakukohdeOid());
            
            if (hakukohde == null) {
                result.addError(ErrorV1RDTO.createValidationError(
                        "none",
                        "hakukohde.notFound",
                        operation.getHakukohdeOid()
                ));

                // Hmm...
                continue;
            }

            // Security!
            try {
                permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(operation.getHakukohdeOid());
            } catch (Throwable ex) {
                result.addError(ErrorV1RDTO.createValidationError(
                        "none",
                        "hakukohde.permissionDenied",
                        operation.getHakukohdeOid(),
                        operation.getRyhmaOid()));
                // Skip this hakukohde
                continue;
            }
            
            switch (operation.getToiminto()) {
                case LISAA:
                    hakukohde.setOrganisaatioRyhmaOids(addToArray(hakukohde.getOrganisaatioRyhmaOids(), operation.getRyhmaOid()));
                    break;
                case POISTA:
                    hakukohde.setOrganisaatioRyhmaOids(removeFromArray(hakukohde.getOrganisaatioRyhmaOids(), operation.getRyhmaOid()));
                    break;
                default:
                    LOG.warn("UNKNOWN OPERATION: {}", operation.getToiminto());
                    result.addError(ErrorV1RDTO.createValidationError(
                            "none",
                            "hakukohde.permissionDenied",
                            operation.getHakukohdeOid(),
                            operation.getRyhmaOid()));
                    // Skip this hakukohde
                    continue;
            }
            
            hakukohde.setLastUpdateDate(new Date());
            hakukohdeDAO.update(hakukohde);
        }
        
        return result;
    }
    
    private String[] addToArray(String source[], String newValue) {
        List<String> tmp = new ArrayList<String>(Arrays.asList(source));
        if (!tmp.contains(newValue)) {
            tmp.add(newValue);
        }
        return tmp.toArray(new String[tmp.size()]);
    }
    
    private String[] removeFromArray(String source[], String newValue) {
        List<String> tmp = new ArrayList<String>(Arrays.asList(source));
        tmp.remove(newValue);
        return tmp.toArray(new String[tmp.size()]);
    }
    
    private void updateKoulutusTypesToHakukohdeDto(HakukohdeV1RDTO hakukohdeRDTO) {
        HashMap<String, String> koulutusAstetyyppi = new HashMap<String, String>();

        //TODO: What if koulutusastetyyppi is different ? It should not be
        if (hakukohdeRDTO.getHakukohdeKoulutusOids() != null && !hakukohdeRDTO.getHakukohdeKoulutusOids().isEmpty()) {
            koulutusAstetyyppi = getKoulutusKoulutusAstetyyppi(hakukohdeRDTO.getHakukohdeKoulutusOids().get(0));
        } else {
            throw new RuntimeException("No komos for hakukohde!");
        }
        
        hakukohdeRDTO.setKoulutusAsteTyyppi(koulutusAstetyyppi.get(KOULUTUSASTE_KEY));
        hakukohdeRDTO.setKoulutuslaji(koulutusAstetyyppi.get(KOULUTUSLAJI_KEY));
        hakukohdeRDTO.setToteutusTyyppi(koulutusAstetyyppi.get(KOULUTUS_TOTEUTUS_TYYPPI));
    }
    
    @Override
    public ResultV1RDTO<ValitutKoulutuksetV1RDTO> isValidKoulutusSelection(List<String> oids) {
        if (oids == null || oids.isEmpty()) {
            ResultV1RDTO<ValitutKoulutuksetV1RDTO> resultV1RDTO = new ResultV1RDTO<ValitutKoulutuksetV1RDTO>();
            resultV1RDTO.addError(ErrorV1RDTO.createValidationError("INVALID_PARAM_NO_KOMOTO_OIDS", ""));
            return resultV1RDTO;
        }
        
        Set<String> toteutustyyppis = Sets.<String>newHashSet();
        KoulutuksetKysely ks = new KoulutuksetKysely();
        ks.getKoulutusOids().addAll(oids);
        KoulutuksetVastaus kv = tarjontaSearchService.haeKoulutukset(ks);
        
        List<NimiJaOidRDTO> names = new ArrayList<NimiJaOidRDTO>();
        for (KoulutusPerustieto kp : kv.getKoulutukset()) {
            toteutustyyppis.add(kp.getToteutustyyppi().name());
            names.add(new NimiJaOidRDTO(kp.getNimi(), kp.getKomotoOid()));
        }
        
        ResultV1RDTO<ValitutKoulutuksetV1RDTO> validKomotoSelection = HakukohdeValidator.getValidKomotoSelection(kv);
        validKomotoSelection.getResult().setNames(names);
        validKomotoSelection.getResult().setToteutustyyppis(toteutustyyppis);
        
        return validKomotoSelection;
    }
    
}
