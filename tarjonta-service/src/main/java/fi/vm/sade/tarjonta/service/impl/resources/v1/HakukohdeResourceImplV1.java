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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.publication.Tila.Tyyppi;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auditlog.AuditLog;
import fi.vm.sade.tarjonta.service.auth.NotAuthorizedException;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.business.IndexService;
import fi.vm.sade.tarjonta.service.business.exception.DataErrorException;
import fi.vm.sade.tarjonta.service.copy.NullAwareBeanUtilsBean;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidationMessages;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidator;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusIdentification;
import fi.vm.sade.tarjonta.service.search.*;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.ParameterServices;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static fi.vm.sade.tarjonta.service.auditlog.AuditLog.*;
import static org.apache.commons.lang.StringUtils.*;

public class HakukohdeResourceImplV1 implements HakukohdeV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImplV1.class);

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Autowired
    private KoulutusSearchService koulutusSearchService;

    @Autowired
    private HakukohdeSearchService hakukohdeSearchService;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private IndexerResource indexerResource;

    @Autowired
    private IndexService indexService;

    @Autowired
    private PublicationDataService publicationDataService;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private ConverterV1 converterV1;

    @Autowired
    private PermissionChecker permissionChecker;

    @Autowired
    private OidService oidService;

    @Autowired
    private ContextDataService contextDataService;

    @Autowired
    private ParameterServices parameterService;

    @Autowired
    private PlatformTransactionManager tm;

    @Autowired
    private HakukohdeValidator hakukohdeValidator;

    private BeanUtilsBean beanUtils = new NullAwareBeanUtilsBean();

    public final static String KOULUTUSASTE_KEY = "koulutusaste";

    public final static String KOULUTUSLAJI_KEY = "koulutuslaji";

    public final static String KOULUTUS_TOTEUTUS_TYYPPI = "toteutustyyppi";

    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> search(String searchTerms,
                                                                             String hakukohteenNimiUri,
                                                                             List<String> organisationOids,
                                                                             List<String> hakukohdeTilas,
                                                                             String alkamisKausi,
                                                                             Integer alkamisVuosi,
                                                                             String hakukohdeOid,
                                                                             List<KoulutusasteTyyppi> koulutusastetyyppi,
                                                                             String hakuOid,
                                                                             List<String> organisaatioRyhmaOid,
                                                                             List<ToteutustyyppiEnum> koulutustyypit,
                                                                             List<KoulutusmoduuliTyyppi> koulutusmoduulityypit,
                                                                             String defaultTarjoaja,
                                                                             String hakutapa,
                                                                             String hakutyyppi,
                                                                             String koulutuslaji,
                                                                             String kohdejoukko,
                                                                             String oppilaitostyyppi,
                                                                             String kunta,
                                                                             List<String> opetuskielet,
                                                                             List<String> koulutusOids,
                                                                             Integer offset,
                                                                             Integer limit,
                                                                             HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters.isEmpty()) {
            LOG.error("HakukohteetKysely query is empty");
            ResultV1RDTO result = new ResultV1RDTO();
            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            result.addError(ErrorV1RDTO.createValidationError("all", "Need at least one parameter"));
            return result;
        }

        organisationOids = removeBlankStrings(organisationOids);
        hakukohdeTilas = hakukohdeTilas != null ? hakukohdeTilas : new ArrayList<>();

        HakukohteetKysely q = new HakukohteetKysely();
        q.setNimi(searchTerms);
        q.setNimiKoodiUri(hakukohteenNimiUri);
        q.setKoulutuksenAlkamiskausi(alkamisKausi);
        q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
        q.getTarjoajaOids().addAll(organisationOids);
        q.setHakutapa(hakutapa);
        q.setHakutyyppi(hakutyyppi);
        q.setKoulutuslaji(koulutuslaji);
        q.setKohdejoukko(kohdejoukko);
        q.setOppilaitostyyppi(oppilaitostyyppi);
        q.setKunta(kunta);
        q.setOpetuskielet(opetuskielet);
        q.setKoulutusmoduuliTyyppi(koulutusmoduulityypit);
        q.getKoulutusOids().addAll(koulutusOids);
        q.setOffset(offset);
        q.setLimit(limit);

        if (hakukohdeOid != null) {
            q.setHakukohdeOid(hakukohdeOid);
        }

        if (hakuOid != null) {
            q.setHakuOid(hakuOid);
        }

        if (organisaatioRyhmaOid != null) {
            q.setOrganisaatioRyhmaOid(removeBlankStrings(organisaatioRyhmaOid));
        }

        q.getKoulutusasteTyypit().addAll(koulutusastetyyppi);

        for (String s : hakukohdeTilas) {
            q.getTilat().add(
                    TarjontaTila.valueOf(s));
        }

        for (ToteutustyyppiEnum koulutustyyppi : koulutustyypit) {
            q.getKoulutustyyppi().add(koulutustyyppi.uri());
        }

        HakukohteetVastaus r = hakukohdeSearchService.haeHakukohteet(q, defaultTarjoaja);

        r.setHakukohteet(filterRemovedHakukohteet(r.getHakukohteet()));

        return new ResultV1RDTO<>(converterV1.fromHakukohteetVastaus(r));
    }

    private static List<String> removeBlankStrings(List<String> list) {
        return list.stream().filter(input -> !isBlank(input)).collect(Collectors.toList());
    }

    private List<HakukohdePerustieto> filterRemovedHakukohteet(List<HakukohdePerustieto> perustietosParam) {

        List<HakukohdePerustieto> perustiedot = new ArrayList<>();

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
            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<>();

            HashMap<String, String> tekstis = converterV1.convertMonikielinenTekstiToMap(hakukohde.getValintaperusteKuvaus(), false);
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(tekstis);

            return result;

        } else {
            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<>();
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return result;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HakukohdeV1RDTO> findByUlkoinenTunniste(String tarjoajaOid, String ulkoinenTunniste) {
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByUlkoinenTunniste(ulkoinenTunniste, tarjoajaOid);
        return findByOid(hakukohde == null ? null : hakukohde.getOid(), false);
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

            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<>();
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(valintaPerusteet);
            return result;
        } catch (Exception exp) {
            ResultV1RDTO<HashMap<String, String>> errorResult = new ResultV1RDTO<>();
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

            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<>();
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(sorat);
            return result;

        } catch (Exception exp) {
            ResultV1RDTO<HashMap<String, String>> errorResult = new ResultV1RDTO<>();
            errorResult.addError(ErrorV1RDTO.createSystemError(exp, null));
            return errorResult;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HashMap<String, String>> findHakukohdeSoraKuvaukset(String hakukohdeOid) {
        try {

            ResultV1RDTO<HashMap<String, String>> result = new ResultV1RDTO<>();

            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
            if (hakukohde.getSoraKuvaus() != null && hakukohde.getSoraKuvaus().getKaannoksetAsList() != null) {

                HashMap<String, String> resultHm = new HashMap<>();
                for (TekstiKaannos tekstiKaannos : hakukohde.getSoraKuvaus().getKaannoksetAsList()) {
                    resultHm.put(tekstiKaannos.getKieliKoodi(), tekstiKaannos.getArvo());
                }
                result.setResult(resultHm);
            }
            result.setStatus(ResultV1RDTO.ResultStatus.OK);

            return result;

        } catch (Exception exp) {
            ResultV1RDTO<HashMap<String, String>> exceptionResult = new ResultV1RDTO<>();
            exceptionResult.addTechnicalError(exp);
            return exceptionResult;
        }
    }

    @Override
    public ResultV1RDTO<List<OidV1RDTO>> search() {
        List<String> hakukohdeOids = hakukohdeDAO.findAllOids();

        List<OidV1RDTO> oidList = new ArrayList<>();
        if (hakukohdeOids != null && hakukohdeOids.size() > 0) {

            for (String hakukohdeOid : hakukohdeOids) {
                OidV1RDTO oidi = new OidV1RDTO();
                oidi.setOid(hakukohdeOid);
                oidList.add(oidi);
            }

            ResultV1RDTO<List<OidV1RDTO>> result = new ResultV1RDTO<>();
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(oidList);

            return result;
        } else {
            ResultV1RDTO<List<OidV1RDTO>> result = new ResultV1RDTO<>();
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return result;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HakukohdeV1RDTO> findByOid(String oid, boolean populate) {

        LOG.debug("HAKUKOHDE-REST V1 findByOid : ", oid);
        try {
            if (oid != null && oid.trim().length() > 0) {
                Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);
                if (hakukohde != null) {
                    HakukohdeV1RDTO hakukohdeRDTO = converterV1.toHakukohdeRDTO(hakukohde, populate);

                    updateKoulutusTypesToHakukohdeDto(hakukohdeRDTO);
                    ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<>();
                    result.setResult(hakukohdeRDTO);
                    result.setStatus(ResultV1RDTO.ResultStatus.OK);

                    return result;
                } else {
                    return ResultV1RDTO.notFound();
                }
            } else {
                return ResultV1RDTO.notFound();
            }
        } catch(Exception e) {
            if (e instanceof DataErrorException) {
                LOG.warn("Fetching hakukohde failed", e.getMessage());
            } else {
                LOG.error("Fetching hakukohde failed", e);
            }
            return ResultV1RDTO.notFound();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> findValintaperusteetByOid(@ApiParam(value = "Hakukohteen oid", required = true) String oid) {
        if (oid != null && oid.trim().length() > 0) {
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);
            if (hakukohde != null) {
                HakukohdeValintaperusteetV1RDTO dto = converterV1.valintaperusteetFromHakukohde(hakukohde);
                ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> result = new ResultV1RDTO<>();
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
        ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> result = new ResultV1RDTO<>();
        result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        return result;
    }

    private HashMap<String, String> getKoulutusKoulutusAstetyyppi(String komotoOid) {
        LOG.debug("TRYING TO GET KOULUTUSASTE AND LAJI WITH KOMOTO OID : {}", komotoOid);
        HashMap<String, String> koulutusAstetyyppi = new HashMap<>();

        final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(komotoOid);

        koulutusAstetyyppi.put(KOULUTUSASTE_KEY, (komoto.getKoulutusmoduuli() != null && komoto.getKoulutusmoduuli().getKoulutustyyppiEnum() != null)
                ? komoto.getKoulutusmoduuli().getKoulutustyyppiEnum().getKoulutusasteTyyppi().name() : null);

        koulutusAstetyyppi.put(KOULUTUS_TOTEUTUS_TYYPPI, komoto.getToteutustyyppi().name());
        //TKatva, just get the first koulutuslaji because koulutus cannot have many koulutuslajis (aikuisten,nuorten)
        //or can it ?
        if (komoto.getKoulutuslajis() != null && komoto.getKoulutuslajis().size() > 0) {
            LOG.debug("KOULUTUSLAJI : {}", new ArrayList<>(komoto.getKoulutuslajis()).get(0).getKoodiUri());
            KoodiType koulutuslajiKoodi = tarjontaKoodistoHelper.getKoodiByUri(new ArrayList<>(komoto.getKoulutuslajis()).get(0).getKoodiUri());
            koulutusAstetyyppi.put(KOULUTUSLAJI_KEY, koulutuslajiKoodi.getKoodiArvo());
        } else {
            LOG.debug("KOULUTUSLAJI WAS NULL!!!");
            koulutusAstetyyppi.put(KOULUTUSLAJI_KEY, null);
        }

        return koulutusAstetyyppi;
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

    private ToteutustyyppiEnum getToteutustyyppi(HakukohdeV1RDTO dto) {
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(dto.getHakukohdeKoulutusOids().iterator().next());
        return komoto.getToteutustyyppi();
    }

    private List<HakukohdeValidationMessages> validateHakukohdeAndPopulateImplicitFields(HakukohdeV1RDTO hakukohdeV1RDTO) {

        final List<HakukohdeValidationMessages> validationMessageses = new ArrayList<>(hakukohdeValidator.validateCommonProperties(hakukohdeV1RDTO));

        try {
            hakukohdeV1RDTO = populateImplicitFields(hakukohdeV1RDTO);
        } catch (Exception e) {
            // Hakukohde was missing data required in population -> return validation messages
            return validationMessageses;
        }

        switch (hakukohdeV1RDTO.getToteutusTyyppi()) {

            case KORKEAKOULUTUS:
            case KORKEAKOULUOPINTO:
                validationMessageses.addAll(hakukohdeValidator
                        .validateHakukohde(hakukohdeV1RDTO));
                break;
            case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
            case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
            case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA:
            case ERIKOISAMMATTITUTKINTO:
            case AMMATTITUTKINTO:
            case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
            case EB_RP_ISH:
            case AIKUISTEN_PERUSOPETUS:
                validationMessageses.addAll(hakukohdeValidator
                        .validateAikuLukioHakukohde(hakukohdeV1RDTO));
                break;
            case AMMATILLINEN_PERUSTUTKINTO:
            case AMMATILLINEN_PERUSTUTKINTO_ALK_2018:
            case LUKIOKOULUTUS:
            case PERUSOPETUKSEN_LISAOPETUS:
            case AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
            case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA:
            case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER:
            case MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
            case MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
            case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
            case VAPAAN_SIVISTYSTYON_KOULUTUS:
            case PELASTUSALAN_KOULUTUS:
                validationMessageses.addAll(hakukohdeValidator
                        .validateToisenAsteenHakukohde(hakukohdeV1RDTO));
                break;
            default:
                LOG.error("Toteutustyyppi:" + hakukohdeV1RDTO.getToteutusTyyppi() + " validation rules not implemented");
                validationMessageses
                        .add(HakukohdeValidationMessages.HAKUKOHDE_NOT_IMPLEMENTED);
                break;
        }

        return validationMessageses;
    }

    @Override
    @Transactional
    public Response postHakukohde(HakukohdeV1RDTO hakukohdeRDTO, HttpServletRequest request) {
        if (hakukohdeRDTO == null) {
            hakukohdeRDTO = new HakukohdeV1RDTO();
        }
        Hakukohde existingHakukohde = hakukohdeDAO.findExistingHakukohde(hakukohdeRDTO);
        if (existingHakukohde != null) {
            hakukohdeRDTO.setOid(existingHakukohde.getOid());
            return updateHakukohde(existingHakukohde.getOid(), hakukohdeRDTO, request);
        }

        hakukohdeRDTO = converterV1.setDefaultValues(hakukohdeRDTO);

        List<HakukohdeValidationMessages> validationMessageses = validateHakukohdeAndPopulateImplicitFields(hakukohdeRDTO);

        if (validationMessageses.size() > 0) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(populateValidationErrors(hakukohdeRDTO, validationMessageses))
                    .build();
        }

        Set<KoulutusmoduuliToteutus> komotot = Sets.newHashSet(koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(hakukohdeRDTO.getHakukohdeKoulutusOids()));
        Map<String, KoulutusmoduuliToteutusTarjoajatiedot> tarjoajatiedot = getTarjoajatiedot(hakukohdeRDTO, komotot);

        Hakukohde hakukohde = converterV1.toHakukohde(hakukohdeRDTO);

        if (!tarjoajatiedot.isEmpty()) {
            hakukohde.setKoulutusmoduuliToteutusTarjoajatiedot(tarjoajatiedot);
        }

        String newHakukohdeOid = null;
        try {
            newHakukohdeOid = oidService.get(TarjontaOidType.HAKUKOHDE);
        } catch (OIDCreationException ex) {
            LOG.warn("UNABLE TO GET OID : {}", ex.toString());
        }

        hakukohde.setOid(newHakukohdeOid);
        hakukohde.setLastUpdateDate(new Date());
        hakukohde.setLastUpdatedByOid(contextDataService.getCurrentUserOid());

        Haku haku = hakuDAO.findByOid(hakukohdeRDTO.getHakuOid());
        hakukohde.setHaku(haku);
        setYlioppilastutkintoAntaaHakukelpoisuuden(hakukohde, hakukohdeRDTO, haku);
        if (hakukohdeRDTO.getHakuaikaId() != null) {
            hakukohde.setHakuaika(getHakuAikaForHakukohde(hakukohdeRDTO, haku));
        }
        addRyhmaliitoksetForNewHakukohde(hakukohde, hakukohdeRDTO);

        hakukohde = hakukohdeDAO.insert(hakukohde);
        setHakukohde(komotot, hakukohde);
        hakukohdeDAO.update(hakukohde);

        indexService.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
        indexService.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), arg0 -> arg0 != null ? arg0.getId() : null)));

        ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<>();
        result.setStatus(ResultV1RDTO.ResultStatus.OK);
        HakukohdeV1RDTO toHakukohdeRDTO = converterV1.toHakukohdeRDTO(hakukohde);
        updateKoulutusTypesToHakukohdeDto(toHakukohdeRDTO);
        result.setResult(toHakukohdeRDTO);

        AuditLog.create(HAKUKOHDE, toHakukohdeRDTO.getOid(), toHakukohdeRDTO, request);

        return Response.ok(result).build();
    }

    private void addRyhmaliitoksetForNewHakukohde(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        if (hakukohdeRDTO.getOrganisaatioRyhmaOids() != null) {
            for (String ryhmaOid : hakukohdeRDTO.getOrganisaatioRyhmaOids()) {
                Ryhmaliitos ryhmaliitos = new Ryhmaliitos();
                ryhmaliitos.setRyhmaOid(ryhmaOid);
                ryhmaliitos.setHakukohde(hakukohde);
                hakukohde.addRyhmaliitos(ryhmaliitos);
            }
        }
    }

    private Map<String, KoulutusmoduuliToteutusTarjoajatiedot> getTarjoajatiedot(HakukohdeV1RDTO hakukohdeRDTO,
                                                                                 Set<KoulutusmoduuliToteutus> komotot) {
        Map<String, KoulutusmoduuliToteutusTarjoajatiedot> tarjoajatiedot = new HashMap<>();
        String hakukohdeOwner = getHakukohdeOwner(hakukohdeRDTO.getKoulutusmoduuliToteutusTarjoajatiedot());

        for (KoulutusmoduuliToteutus komoto : komotot) {
            for (KoulutusOwner owner : komoto.getOwners()) {
                if (!owner.getOwnerType().equals(KoulutusOwner.TARJOAJA)) {
                    continue;
                }

                OrganisaatioRDTO tmpOrg = organisaatioService.findByOid(owner.getOwnerOid());

                if (tmpOrg == null) {
                    continue;
                }

                ArrayList<String> orgPath = new ArrayList<>();
                orgPath.add(owner.getOwnerOid());
                if (tmpOrg.getParentOidPath() != null) {
                    orgPath.addAll(Arrays.asList(tmpOrg.getParentOidPath().split("\\|")));
                }

                for (String orgOidCandidate : orgPath) {
                    if (orgOidCandidate.equals(hakukohdeOwner)) {
                        KoulutusmoduuliToteutusTarjoajatiedot tarjojaTiedotKoulutukselle = new KoulutusmoduuliToteutusTarjoajatiedot();
                        HashSet<String> tarjoajatHashSet = new HashSet<>();
                        tarjoajatHashSet.add(owner.getOwnerOid());
                        tarjojaTiedotKoulutukselle.setTarjoajaOids(tarjoajatHashSet);
                        tarjoajatiedot.put(komoto.getOid(), tarjojaTiedotKoulutukselle);
                        break;
                    }
                }
            }

            // Check permission to komoto
            if (tarjoajatiedot.get(komoto.getOid()) != null) {
                String tmpTarjoaja = tarjoajatiedot.get(komoto.getOid()).getTarjoajaOids().iterator().next();
                permissionChecker.checkCreateHakukohde(hakukohdeRDTO.getHakuOid(), tmpTarjoaja);
            } else {
                permissionChecker.checkCreateHakukohde(hakukohdeRDTO.getHakuOid(), komoto.getTarjoaja());
            }
        }
        return tarjoajatiedot;
    }

    private String getHakukohdeOwner(Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> tarjoajatiedot) {
        String hakukohdeOwner = null;
        if (!tarjoajatiedot.isEmpty()) {
            hakukohdeOwner = tarjoajatiedot.entrySet().iterator().next().getValue().getTarjoajaOids().iterator().next();
        }
        return hakukohdeOwner;
    }

    private void setYlioppilastutkintoAntaaHakukelpoisuuden(Hakukohde hakukohde, HakukohdeV1RDTO dto, Haku haku) {
        if (dto.getYlioppilastutkintoAntaaHakukelpoisuuden() == null) {
            return;
        }

        // Tallenna valinta hakukohteelle AINOASTAAN jos valinta on eri kuin haun valinta
        // tai jos hakukohteelle on aiemmin tallennettu ylikirjoitettu arvo
        if (dto.getYlioppilastutkintoAntaaHakukelpoisuuden() != haku.getYlioppilastutkintoAntaaHakukelpoisuuden()
                || hakukohde.getYlioppilastutkintoAntaaHakukelpoisuuden() != null) {
            hakukohde.setYlioppilastutkintoAntaaHakukelpoisuuden(dto.getYlioppilastutkintoAntaaHakukelpoisuuden());
        }
    }

    private HakukohdeV1RDTO populateImplicitFields(HakukohdeV1RDTO dto) {
        dto.setToteutusTyyppi(getToteutustyyppi(dto));
        return dto;
    }

    private HakukohdeV1RDTO mergeExistingHakukohdeData(String oid, HakukohdeV1RDTO dto)
            throws InvocationTargetException, IllegalAccessException {
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);
        HakukohdeV1RDTO originalDto = converterV1.toHakukohdeRDTO(hakukohde);
        final String originalHakuOid = trimToEmpty(originalDto.getHakuOid());
        final boolean isChangingExistingHakuOid = isNotEmpty(originalHakuOid) && isNotEmpty(dto.getHakuOid()) && !originalHakuOid.equals(dto.getHakuOid());
        if(isChangingExistingHakuOid && !permissionChecker.isOphCrud()) {
            throw new RuntimeException("Hakukohteen (OID = "+oid+") haku OID:n muuttaminen on kiellettyä. Alkuperäinen haku OID on " + originalHakuOid + " -> " + dto.getHakuOid());
        }
        beanUtils.copyProperties(originalDto, dto);

        return originalDto;
    }

    @Override
    @Transactional
    public Response updateHakukohde(String hakukohdeOid, HakukohdeV1RDTO hakukohdeRDTO, HttpServletRequest request) {
        if (hakukohdeRDTO == null) {
            hakukohdeRDTO = new HakukohdeV1RDTO();
        }
        permissionChecker.checkUpdateHakukohde(hakukohdeOid, hakukohdeRDTO.getHakuOid(), getKomotoOidsForHakukohde(hakukohdeRDTO));
        try {

            Date today = new Date();

            hakukohdeRDTO = mergeExistingHakukohdeData(hakukohdeOid, hakukohdeRDTO);

            List<HakukohdeValidationMessages> validationMessagesList = validateHakukohdeAndPopulateImplicitFields(hakukohdeRDTO);

            if (validationMessagesList.size() > 0) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(populateValidationErrors(hakukohdeRDTO, validationMessagesList))
                        .build();
            }

            Hakukohde hakukohde = converterV1.toHakukohde(hakukohdeRDTO);
            hakukohde.setLastUpdateDate(today);
            hakukohde.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
            Hakukohde hakukohdeTemp = hakukohdeDAO.findHakukohdeByOid(hakukohdeRDTO.getOid());

            HakukohdeV1RDTO originalHakukohde = converterV1.toHakukohdeRDTO(hakukohdeTemp);
            updateKoulutusTypesToHakukohdeDto(originalHakukohde);
            hakukohdeDAO.detach(hakukohdeTemp);

            hakukohde.setId(hakukohdeTemp.getId());
            hakukohde.setYlioppilastutkintoAntaaHakukelpoisuuden(hakukohdeTemp.getYlioppilastutkintoAntaaHakukelpoisuuden());
            hakukohde.setVersion(hakukohdeTemp.getVersion());
            handleRyhmaliitokset(hakukohde, hakukohdeTemp, hakukohdeRDTO);

            hakukohde.setKoulutusmoduuliToteutusTarjoajatiedot(hakukohdeTemp.getKoulutusmoduuliToteutusTarjoajatiedot());

            // If hakukohde uses a common description -> don't save an individual description for this hakukohde
            if (hakukohde.getValintaPerusteKuvausTunniste() != null || hakukohde.getValintaperustekuvausKoodiUri() != null) {
                hakukohde.setValintaperusteKuvaus(null);
            }
            if (hakukohde.getSoraKuvausTunniste() != null || hakukohde.getSoraKuvausKoodiUri() != null) {
                hakukohde.setSoraKuvaus(null);
            }

            Haku haku = hakuDAO.findByOid(hakukohdeRDTO.getHakuOid());

            hakukohde.setHaku(haku);

            setYlioppilastutkintoAntaaHakukelpoisuuden(hakukohde, hakukohdeRDTO, haku);

            if (hakukohdeRDTO.getHakuaikaId() != null) {
                hakukohde.setHakuaika(getHakuAikaForHakukohde(hakukohdeRDTO,
                        haku));
            }

            LOG.debug("Hakukohde.liitteet = {}", hakukohde.getLiites());
            LOG.debug("Hakukohde.kokeet = {}", hakukohde.getValintakoes());

            Set<KoulutusmoduuliToteutus> komotot = Sets.newHashSet(koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(hakukohdeRDTO.getHakukohdeKoulutusOids()));
            setHakukohde(komotot, hakukohde);

            Tila tilamuutos = new Tila(Tyyppi.HAKUKOHDE, hakukohdeRDTO.getTila(), hakukohde.getOid());

            if (publicationDataService.isValidStatusChange(tilamuutos)) {

                hakukohdeDAO.update(hakukohde);
                LOG.debug("Hakukohde.liitteet -> {}", hakukohde.getLiites());
                LOG.debug("Hakukohde.kokeet -> {}", hakukohde.getValintakoes());
                LOG.debug("Hakukohde", hakukohde);
                indexService.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
                indexService.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), arg0 -> arg0 != null ? arg0.getId() : null)));

                ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<>();
                result.setStatus(ResultV1RDTO.ResultStatus.OK);

                HakukohdeV1RDTO toHakukohdeRDTO = converterV1.toHakukohdeRDTO(hakukohdeDAO.findHakukohdeByOid(hakukohde.getOid()));
                updateKoulutusTypesToHakukohdeDto(toHakukohdeRDTO);
                result.setResult(toHakukohdeRDTO);

                AuditLog.update(HAKUKOHDE, originalHakukohde.getOid(), toHakukohdeRDTO, originalHakukohde, request);

                return Response.ok(result).build();
            } else {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(populateValidationErrors(hakukohdeRDTO, Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_TILA_WRONG)))
                        .build();
            }
        } catch (Exception e) {
            LOG.warn("Exception occured while updating hakukohde " + hakukohdeOid, e);
            ResultV1RDTO<HakukohdeV1RDTO> errorResult = new ResultV1RDTO<>();
            errorResult.addTechnicalError(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResult).build();
        }
    }

    private void handleRyhmaliitokset(Hakukohde toBeUpdatedHakukohde, Hakukohde hakukohdeFromDB, HakukohdeV1RDTO hakukohdeRDTO) {
        if (hakukohdeRDTO.getOrganisaatioRyhmaOids() != null) {
            for (String ryhmaOid : hakukohdeRDTO.getOrganisaatioRyhmaOids()) {
                Ryhmaliitos ryhmaliitos = hakukohdeFromDB.getRyhmaliitosByRyhmaOid(ryhmaOid);
                if (ryhmaliitos == null) {
                    ryhmaliitos = new Ryhmaliitos();
                    ryhmaliitos.setRyhmaOid(ryhmaOid);
                    ryhmaliitos.setHakukohde(toBeUpdatedHakukohde);
                }
                toBeUpdatedHakukohde.addRyhmaliitos(ryhmaliitos);
            }
        }
    }

    @Override
    @Transactional
    public ResultV1RDTO<Boolean> deleteHakukohde(String oid, HttpServletRequest request) {
        permissionChecker.checkRemoveHakukohde(oid);
        try {
            LOG.debug("REMOVING HAKUKOHDE : " + oid);
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);

            if (hakukohde == null || !hakukohde.getTila().isRemovable()) {
                ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<>();
                errorResult.addError(ErrorV1RDTO.createValidationError("hakukohde.invalid.transition", HakukohdeValidationMessages.HAKUKOHDE_INVALID_TRANSITION.toString().toLowerCase()));
                return errorResult;
            }
            hakukohdeDAO.safeDelete(hakukohde.getOid(), contextDataService.getCurrentUserOid());
            List<Long> hakukohdeIds = new ArrayList<>();
            hakukohdeIds.add(hakukohde.getId());
            indexService.indexHakukohteet(hakukohdeIds);

            HakukohdeV1RDTO originalHakukohdeDTO = converterV1.toHakukohdeRDTO(hakukohde);
            AuditLog.delete(HAKUKOHDE, oid, originalHakukohdeDTO, request);

            ResultV1RDTO<Boolean> result = new ResultV1RDTO<>();
            result.setResult(true);
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            return result;
        } catch (Exception exp) {
            LOG.warn("Exception occured when removing hakukohde {}, exception : {}", oid, exp.toString());
            ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<>();
            errorResult.addTechnicalError(exp);
            return errorResult;
        }
    }

    private Set<String> getKomotoOidsForHakukohde(HakukohdeV1RDTO hakukohde) {
        Set<String> oids = new HashSet<>();

        if (hakukohde.getHakukohdeKoulutusOids() != null) {
            oids.addAll(hakukohde.getHakukohdeKoulutusOids());
        }

        if (hakukohde.getKoulutukset() != null) {
            for (KoulutusIdentification id : hakukohde.getKoulutukset()) {
                KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findKomotoByKoulutusId(id);
                if (komoto != null) {
                    oids.add(komoto.getOid());
                }
            }
        }

        return oids;
    }

    @Override
    @Transactional
    public ResultV1RDTO<List<ValintakoeV1RDTO>> findHakukohdeValintakoes(String hakukohdeOid) {

        if (hakukohdeOid != null) {

            ResultV1RDTO<List<ValintakoeV1RDTO>> resultRDTO = new ResultV1RDTO<>();

            try {

                List<ValintakoeV1RDTO> valintakoeV1RDTOs = new ArrayList<>();
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

        } else {
            ResultV1RDTO<List<ValintakoeV1RDTO>> resultRDTO = new ResultV1RDTO<>();
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return resultRDTO;
        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultV1RDTO<ValintakoeV1RDTO> insertValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            Valintakoe valintakoe = converterV1.toValintakoe(valintakoeV1RDTO);
            if (hakukohdeOid != null && valintakoe != null) {
                LOG.debug("INSERTING VALINTAKOE : {} with kieli : {}", valintakoe.getValintakoeNimi(), valintakoe.getKieli());
                List<Valintakoe> valintakoes = hakukohdeDAO.findValintakoeByHakukohdeOid(hakukohdeOid);
                valintakoes.add(valintakoe);
                hakukohdeDAO.updateValintakoe(valintakoes, hakukohdeOid);
                ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<>();
                ValintakoeV1RDTO result = converterV1.fromValintakoe(valintakoe);
                rdtoResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                rdtoResultRDTO.setResult(result);
                return rdtoResultRDTO;
            } else {
                ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<>();
                rdtoResultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
                errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
                errorRDTO.setErrorTechnicalInformation("Hakukohde cannot be null when inserting valintakoe");
                rdtoResultRDTO.addError(errorRDTO);
                return rdtoResultRDTO;

            }

        } catch (Exception exp) {
            ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<>();
            rdtoResultRDTO.addTechnicalError(exp);
            return rdtoResultRDTO;
        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultV1RDTO<ValintakoeV1RDTO> updateValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            Valintakoe valintakoe = converterV1.toValintakoe(valintakoeV1RDTO);

            List<ValintakoeV1RDTO> valintakokees = new ArrayList<>();
            valintakokees.add(valintakoeV1RDTO);

            List<HakukohdeValidationMessages> validationMessageses = hakukohdeValidator.validateValintakokees(valintakokees);
            if (validationMessageses.size() > 0) {
                ResultV1RDTO<ValintakoeV1RDTO> errorResult = new ResultV1RDTO<>();
                errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
                for (HakukohdeValidationMessages message : validationMessageses) {
                    errorResult.addError(ErrorV1RDTO.createValidationError(null, message.name()));
                }
                errorResult.setResult(valintakoeV1RDTO);
                return errorResult;
            }

            LOG.debug("UPDATEVALINTAKOE SIZE: {} ", valintakoe.getAjankohtas().size());

            hakukohdeDAO.updateSingleValintakoe(valintakoe, hakukohdeOid);
            LOG.debug("UPDATED VALINTAKOE");
            ResultV1RDTO<ValintakoeV1RDTO> valintakoeResult = new ResultV1RDTO<>();
            valintakoeResult.setStatus(ResultV1RDTO.ResultStatus.OK);
            valintakoeResult.setResult(valintakoeV1RDTO);
            return valintakoeResult;

        } catch (Exception exp) {
            ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<>();
            rdtoResultRDTO.addTechnicalError(exp);
            return rdtoResultRDTO;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultV1RDTO<Boolean> removeValintakoe(String hakukohdeOid, String valintakoeOid) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            LOG.debug("REMOVEVALINTAKOE: {}", valintakoeOid);
            Valintakoe valintakoe = hakukohdeDAO.findValintaKoeById(valintakoeOid);
            hakukohdeDAO.removeValintakoe(valintakoe);

            ResultV1RDTO<Boolean> resultRDTO = new ResultV1RDTO<>();
            resultRDTO.setResult(true);
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            return resultRDTO;

        } catch (Exception exp) {
            ResultV1RDTO<Boolean> resultRDTO = new ResultV1RDTO<>();
            resultRDTO.setResult(false);
            resultRDTO.addTechnicalError(exp);
            return resultRDTO;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> findHakukohdeLiites(String hakukohdeOid) {

        if (hakukohdeOid != null) {
            try {

                ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> listResultRDTO = new ResultV1RDTO<>();

                List<HakukohdeLiite> liites = hakukohdeDAO.findHakukohdeLiitesByHakukohdeOid(hakukohdeOid);
                List<HakukohdeLiiteV1RDTO> liiteV1RDTOs = new ArrayList<>();
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
                ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> errorResult = new ResultV1RDTO<>();
                errorResult.addTechnicalError(exp);
                return errorResult;
            }
        } else {
            ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> errorResult = new ResultV1RDTO<>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return errorResult;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> insertHakukohdeLiite(String hakukohdeOid, HakukohdeLiiteV1RDTO liiteV1RDTO) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);

            List<HakukohdeValidationMessages> validationMessageses = hakukohdeValidator.validateLiite(liiteV1RDTO, !hakukohde.getHaku().isJatkuva());
            if (validationMessageses.size() > 0) {
                return populateValidationErrors(liiteV1RDTO, validationMessageses);
            }

            ResultV1RDTO<HakukohdeLiiteV1RDTO> resultRDTO = new ResultV1RDTO<>();
            HakukohdeLiite hakukohdeLiite = converterV1.toHakukohdeLiite(liiteV1RDTO);
            List<HakukohdeLiite> liites = hakukohdeDAO.findHakukohdeLiitesByHakukohdeOid(hakukohdeOid);
            liites.add(hakukohdeLiite);
            hakukohdeDAO.insertLiittees(liites, hakukohdeOid);

            resultRDTO.setResult(converterV1.fromHakukohdeLiite(hakukohdeLiite));
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            return resultRDTO;

        } catch (Exception exp) {
            ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResult = new ResultV1RDTO<>();
            errorResult.addTechnicalError(exp);
            return errorResult;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> updateHakukohdeLiite(String hakukohdeOid, HakukohdeLiiteV1RDTO liiteV1RDTO) {

        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);

            List<HakukohdeValidationMessages> validationMessageses = hakukohdeValidator.validateLiite(liiteV1RDTO, !hakukohde.getHaku().isJatkuva());
            if (validationMessageses.size() > 0) {
                return populateValidationErrors(liiteV1RDTO, validationMessageses);
            }

            ResultV1RDTO<HakukohdeLiiteV1RDTO> resultRDTO = new ResultV1RDTO<>();

            HakukohdeLiite hakukohdeLiite = converterV1.toHakukohdeLiite(liiteV1RDTO);

            hakukohdeDAO.updateLiite(hakukohdeLiite, hakukohdeOid);

            resultRDTO.setResult(converterV1.fromHakukohdeLiite(hakukohdeLiite));
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

            return resultRDTO;

        } catch (Exception exp) {
            ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResultDto = new ResultV1RDTO<>();
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
        ResultV1RDTO<T> errorResult = new ResultV1RDTO<>();
        errorResult.setResult(result);
        for (HakukohdeValidationMessages msg : validationMessageses) {
            errorResult.addError(ErrorV1RDTO.createValidationError(msg.name(), msg.name()));
        }
        errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
        return errorResult;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultV1RDTO<Boolean> deleteHakukohdeLiite(String hakukohdeOid, String liiteId) {

        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
        try {

            HakukohdeLiite hakukohdeLiite = hakukohdeDAO.findHakuKohdeLiiteById(liiteId);

            if (hakukohdeLiite != null && hakukohdeLiite.getId() != null) {

                hakukohdeDAO.removeHakukohdeLiite(hakukohdeLiite);

                ResultV1RDTO<Boolean> booleanResultRDTO = new ResultV1RDTO<>();
                booleanResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                booleanResultRDTO.setResult(true);
                return booleanResultRDTO;
            } else {
                ResultV1RDTO<Boolean> booleanResultRDTO = new ResultV1RDTO<>();
                booleanResultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                booleanResultRDTO.setResult(false);
                return booleanResultRDTO;
            }

        } catch (Exception exp) {
            ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<>();
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
    @Transactional()
    public ResultV1RDTO<Tilamuutokset> updateTila(String oid, TarjontaTila tila, HttpServletRequest request) {
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(oid);

        if (Sets.newHashSet(TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU).contains(tila)) {
            try {
                permissionChecker.checkPublishOrUnpublishHakukohde(oid);
            }
            catch (NotAuthorizedException e) {
                ResultV1RDTO<Tilamuutokset> r = new ResultV1RDTO<>();
                r.addError(ErrorV1RDTO.createValidationError(null, e.getMessage()));
                return r;
            }
        }


        Tila tilamuutos = new Tila(Tyyppi.HAKUKOHDE, tila, oid);
        Tilamuutokset tm;
        try {
            HakukohdeV1RDTO originalHakukohdeRDTO = converterV1.toHakukohdeRDTO(hakukohdeDAO.findHakukohdeByOid(oid));
            tm = publicationDataService.updatePublicationStatus(Lists.newArrayList(tilamuutos));
            HakukohdeV1RDTO hakukohdeRDTO = converterV1.toHakukohdeRDTO(hakukohdeDAO.findHakukohdeByOid(oid));
            AuditLog.stateChange(HAKUKOHDE, oid, tila, hakukohdeRDTO, originalHakukohdeRDTO, request, null);
        } catch (IllegalArgumentException iae) {
            ResultV1RDTO<Tilamuutokset> r = new ResultV1RDTO<>();
            r.addError(ErrorV1RDTO.createValidationError(null, iae.getMessage()));
            return r;
        }

        //indeksoi uudelleen muuttunut data
        if (tm.getMuutetutHakukohteet().size() + tm.getMuutetutKomotot().size() == 0) {
            LOG.warn("Jostain syystä indeksoitavaa ei ole vaikka tehtiin tilamuutos! " + tilamuutos.toString());
        }
        LOG.info("Indeksoidaan muuttuneet koulutukset: {} ja hakukohteet {}", tm.getMuutetutKomotot(), tm.getMuutetutHakukohteet());
        indexerResource.indexMuutokset(tm);

        return new ResultV1RDTO<>(tm);
    }

    @Override
    public ResultV1RDTO<List<NimiJaOidRDTO>> getKoulutukset(String oid) {
        KoulutuksetKysely ks = new KoulutuksetKysely();
        ks.getHakukohdeOids().add(oid);

        KoulutuksetVastaus kv = koulutusSearchService.haeKoulutukset(ks);
        List<NimiJaOidRDTO> ret = new ArrayList<>();
        for (KoulutusPerustieto kp : kv.getKoulutukset()) {
            ret.add(new NimiJaOidRDTO(kp.getNimi(), kp.getKomotoOid()));
        }
        return new ResultV1RDTO<>(ret);
    }

    @Override
    @Transactional()
    public ResultV1RDTO<List<String>> lisaaKoulutuksesToHakukohde(String hakukohdeOid, List<KoulutusTarjoajaV1RDTO> koulutukses, HttpServletRequest request) {

        LOG.info("lisätään koulutuksia hakukohteelle {}: {}", hakukohdeOid, koulutukses);

        ResultV1RDTO<List<String>> resultV1RDTO = new ResultV1RDTO<>();

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
        if (!parameterService.parameterCanAddHakukohdeToHaku(hakukohde.getHaku().getOid())
                && !permissionChecker.isOphCrud()) {
            throw new NotAuthorizedException("no.permission");
        }
        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohde.getOid());

        List<HakukohdeValidationMessages> validationMessages = hakukohdeValidator.checkKoulutukset(getKomotoOids(koulutukses));

        boolean isToinenAste = hakukohde.getKoulutusmoduuliToteutuses().stream().anyMatch(komoto -> komoto.getToteutustyyppi().isToisenAsteenKoulutus());
        if (isToinenAste) {
            validationMessages.addAll(hakukohdeValidator.checkTarjoajat(hakukohde, koulutukses));
        }

        if (validationMessages.size() > 0) {
            return populateValidationErrors(null, validationMessages);
        }

        List<String> liitettavatKomotoOids = getKomotoOids(koulutukses);
        Set<KoulutusmoduuliToteutus> liitettavatKomotot = Sets.newHashSet(koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(liitettavatKomotoOids));
        if (liitettavatKomotot.size() > 0) {

            for (KoulutusmoduuliToteutus komoto : liitettavatKomotot) {

                hakukohde.addKoulutusmoduuliToteutus(komoto);

                komoto.addHakukohde(hakukohde);

                koulutusmoduuliToteutusDAO.update(komoto);

                LOG.info("indexing koulutukset for hakukohde {}: {}", hakukohdeOid, liitettavatKomotoOids);
                indexService.indexKoulutukset(Lists.newArrayList(komoto.getId()));
            }

            updateTarjoajatiedot(hakukohde, koulutukses);

            hakukohdeDAO.update(hakukohde);
            LOG.info("indexing hakukohde {}", hakukohdeOid);
            indexService.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));

            AuditLog.log(ADD_KOULUTUS_TO_HAKUKOHDE, HAKUKOHDE, hakukohdeOid, null, null, request,
                    ImmutableMap.of("addedKomotos", liitettavatKomotot.stream().map(k -> k.getOid()).collect(Collectors.joining(", "))));

            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

        } else {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);

        }

        return resultV1RDTO;
    }

    private void updateTarjoajatiedot(Hakukohde hakukohde, List<KoulutusTarjoajaV1RDTO> koulutukses) {
        handleOldTarjontatiedot(hakukohde);

        for (KoulutusTarjoajaV1RDTO koulutusTarjoajaV1RDTO : koulutukses) {
            String oid = koulutusTarjoajaV1RDTO.getOid();
            String tarjoajaOid = koulutusTarjoajaV1RDTO.getTarjoajaOid();

            KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedot = hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().get(oid);

            if (tarjoajatiedot == null) {
                tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
            }

            tarjoajatiedot.getTarjoajaOids().add(tarjoajaOid);
            hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put(oid, tarjoajatiedot);
        }
    }

    private void handleOldTarjontatiedot(Hakukohde hakukohde) {
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            String oid = koulutusmoduuliToteutus.getOid();
            KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedot = hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().get(oid);
            if (tarjoajatiedot == null) {
                tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
                tarjoajatiedot.getTarjoajaOids().add(koulutusmoduuliToteutus.getTarjoaja());
                hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put(oid, tarjoajatiedot);
            }
        }
    }

    private List<String> getKomotoOids(List<KoulutusTarjoajaV1RDTO> koulutukses) {
        List<String> oids = new ArrayList<>();
        for (KoulutusTarjoajaV1RDTO koulutusTarjoajaV1RDTO : koulutukses) {
            String komotoOid = koulutusTarjoajaV1RDTO.getOid();
            oids.add(komotoOid);
        }
        return oids;
    }

    @Override
    @Transactional()
    public ResultV1RDTO<List<String>> removeKoulutuksesFromHakukohde(String hakukohdeOid, List<KoulutusTarjoajaV1RDTO> koulutukses, HttpServletRequest request) {

        ResultV1RDTO<List<String>> resultV1RDTO = new ResultV1RDTO<>();
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);

        permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohde.getOid());

        Set<KoulutusmoduuliToteutus> komotoToRemove = Sets.newHashSet();

        for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
            LOG.debug("Looping hakukohde komoto : {}", komoto.getOid());

            for (KoulutusTarjoajaV1RDTO koulutusTarjoajaV1RDTO : koulutukses) {
                String komotoOid = koulutusTarjoajaV1RDTO.getOid();
                String tarjoajaOid = koulutusTarjoajaV1RDTO.getTarjoajaOid();

                if (komoto.getOid().trim().equals(komotoOid)) {

                    if (!hakukohde.hasTarjoajatiedotForKoulutus(komotoOid)) {
                        komotoToRemove.add(komoto);
                    } else {
                        KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedotForKoulutus = hakukohde.getTarjoajatiedotForKoulutus(komotoOid);
                        if (tarjoajatiedotForKoulutus.containsOnlyTarjoaja(tarjoajaOid)) {
                            komotoToRemove.add(komoto);
                            hakukohde.removeTarjoajatiedotForKoulutus(komoto.getOid());
                        } else {
                            tarjoajatiedotForKoulutus.removeTarjoaja(tarjoajaOid);
                            hakukohdeDAO.update(hakukohde);
                            indexService.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
                        }
                    }
                }
            }
        }

        // Tätä ei suoriteta, jos saman koulutuksen yksittäinen tarjoaja poistetaan
        if (komotoToRemove.size() > 0) {
            Set<KoulutusmoduuliToteutus> remainingKomotos = Sets.difference(hakukohde.getKoulutusmoduuliToteutuses(), komotoToRemove);

            List<String> remainingOids = remainingKomotos.stream().map(input -> input.getOid()).collect(Collectors.toList());

            permissionChecker.checkUpdateHakukohde(hakukohdeOid, hakukohde.getHaku().getOid(), remainingOids);

            LOG.info("Removing {} koulutukses from hakukohde : {}", komotoToRemove.size(), hakukohde.getOid());
            if (remainingKomotos.size() > 0) {

                for (KoulutusmoduuliToteutus komoto : komotoToRemove) {

                    komoto.removeHakukohde(hakukohde);

                    hakukohde.removeKoulutusmoduuliToteutus(komoto);

                    koulutusmoduuliToteutusDAO.update(komoto);
                    LOG.info("indexing koulutus removal {} for {}", komoto.getId(), hakukohdeOid);
                    indexService.indexKoulutukset(Lists.newArrayList(komoto.getId()));
                }

                LOG.debug("Hakukohde has more koulutukses, updating it");
                hakukohdeDAO.update(hakukohde);
                LOG.info("indexing hk {}", hakukohdeOid);
                indexService.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
            } else {

                List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKoulutusModuulisWithHakukohdesByOids(getKomotoOids(koulutukses));

                for (KoulutusmoduuliToteutus komoto : komotos) {
                    komoto.removeHakukohde(hakukohde);
                    hakukohde.removeKoulutusmoduuliToteutus(komoto);
                    indexService.indexKoulutukset(Lists.newArrayList(komoto.getId()));
                }

                LOG.info("Hakukohde {} does not have anymore koulutukses, removing it", hakukohdeOid);
                hakukohdeDAO.remove(hakukohde);
                try {
                    indexerResource.deleteHakukohde(Lists.newArrayList(hakukohdeOid));
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        }

        AuditLog.log(REMOVE_KOULUTUS_FROM_HAKUKOHDE, HAKUKOHDE, hakukohde.getOid(), null, null,
                request, ImmutableMap.of("addedKomotos", komotoToRemove.stream().map(k -> k.getOid()).collect(Collectors.joining(", "))));

        resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
        resultV1RDTO.setResult(getKomotoOids(koulutukses));

        return resultV1RDTO;

    }

    @Override
    public ResultV1RDTO<Boolean> isStateChangePossible(String oid,
                                                       TarjontaTila tila) {
        Tila tilamuutos = new Tila(Tyyppi.HAKUKOHDE, tila, oid);
        return new ResultV1RDTO<>(publicationDataService.isValidStatusChange(tilamuutos));
    }

    // POST /hakukohde/ryhmat/lisaa
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultV1RDTO<Boolean> lisaaRyhmatHakukohteille(List<HakukohdeRyhmaV1RDTO> data, HttpServletRequest request) {
        LOG.debug("lisaaRyhmatHakukohteille()");

        ResultV1RDTO<Boolean> result = new ResultV1RDTO<>();

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
                    addRyhmaliitos(operation, hakukohde);
                    break;
                case POISTA:
                    removeRyhmaliitos(operation, hakukohde);
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

            indexService.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
            for (KoulutusmoduuliToteutus komoto: hakukohde.getKoulutusmoduuliToteutuses()) {
                indexService.indexKoulutukset(Lists.newArrayList(komoto.getId()));
            }

            AuditLog.log(MODIFY_RYHMAT, HAKUKOHDE, hakukohde.getOid(), null, null,
                    request, ImmutableMap.of("ryhmaOperation", operation.toString()));
        }

        return result;
    }

    private void removeRyhmaliitos(HakukohdeRyhmaV1RDTO operation, Hakukohde hakukohde) {
        Ryhmaliitos ryhmaliitos = hakukohde.getRyhmaliitosByRyhmaOid(operation.getRyhmaOid());
        if (ryhmaliitos != null) {
            hakukohde.removeRyhmaliitos(ryhmaliitos);
        }
    }

    private void addRyhmaliitos(HakukohdeRyhmaV1RDTO operation, Hakukohde hakukohde) {
        Ryhmaliitos newRyhmaliitos = new Ryhmaliitos();
        newRyhmaliitos.setRyhmaOid(operation.getRyhmaOid());
        newRyhmaliitos.setHakukohde(hakukohde);
        hakukohde.addRyhmaliitos(newRyhmaliitos);
    }

    private void updateKoulutusTypesToHakukohdeDto(HakukohdeV1RDTO hakukohdeRDTO) {
        HashMap<String, String> koulutusAstetyyppi;

        if (hakukohdeRDTO.getHakukohdeKoulutusOids() != null && !hakukohdeRDTO.getHakukohdeKoulutusOids().isEmpty()) {
            koulutusAstetyyppi = getKoulutusKoulutusAstetyyppi(hakukohdeRDTO.getHakukohdeKoulutusOids().get(0));
        } else {
            throw new RuntimeException("No komotos for hakukohde!");
        }

        hakukohdeRDTO.setKoulutusAsteTyyppi(koulutusAstetyyppi.get(KOULUTUSASTE_KEY));
        hakukohdeRDTO.setKoulutuslaji(koulutusAstetyyppi.get(KOULUTUSLAJI_KEY));
    }

    @Override
    public ResultV1RDTO<ValitutKoulutuksetV1RDTO> isValidKoulutusSelection(List<String> oids) {
        if (oids == null || oids.isEmpty()) {
            ResultV1RDTO<ValitutKoulutuksetV1RDTO> resultV1RDTO = new ResultV1RDTO<>();
            resultV1RDTO.addError(ErrorV1RDTO.createValidationError("INVALID_PARAM_NO_KOMOTO_OIDS", ""));
            return resultV1RDTO;
        }

        Set<String> toteutustyyppis = Sets.newHashSet();
        KoulutuksetKysely ks = new KoulutuksetKysely();
        ks.getKoulutusOids().addAll(oids);
        KoulutuksetVastaus kv = koulutusSearchService.haeKoulutukset(ks);

        List<NimiJaOidRDTO> names = new ArrayList<>();
        for (KoulutusPerustieto kp : kv.getKoulutukset()) {
            toteutustyyppis.add(kp.getToteutustyyppi().name());
            names.add(new NimiJaOidRDTO(kp.getNimi(), kp.getKomotoOid()));
        }

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> validKomotoSelection = hakukohdeValidator.getValidKomotoSelection(kv);
        validKomotoSelection.getResult().setNames(names);
        validKomotoSelection.getResult().setToteutustyyppis(toteutustyyppis);

        return validKomotoSelection;
    }

    public void updateValintakokeetToNewStructure() {
        List<String> oids = hakukohdeDAO.findAllOids();

        for (final String oid : oids) {

            executeInTransaction(() -> {
                permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(oid);
                Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);

                if (!hakukohde.getKoulutusmoduuliToteutuses().isEmpty()) {

                    final KoulutusmoduuliToteutus komoto = hakukohde.getKoulutusmoduuliToteutuses().iterator().next();

                    if (shouldUpdateValintakoeStructure(komoto)) {
                        updateValintakokeet(hakukohde);
                    }
                }
            });
        }
    }

    public void updateLiitteetToNewStructure() {

        List<String> oids = hakukohdeDAO.findAllOids();

        for (final String oid : oids) {

            executeInTransaction(() -> {
                permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(oid);
                Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);

                if (!hakukohde.getKoulutusmoduuliToteutuses().isEmpty()) {

                    final KoulutusmoduuliToteutus komoto = hakukohde.getKoulutusmoduuliToteutuses().iterator().next();

                    if (shouldUpdateLiiteStructure(komoto)) {
                        updateLiitteet(hakukohde);
                    }
                }

            });
        }
    }

    private void executeInTransaction(final Runnable runnable) {
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(status -> {
            runnable.run();
            return null;
        });
    }

    private boolean shouldUpdateValintakoeStructure(KoulutusmoduuliToteutus komoto) {
        return !komoto.getToteutustyyppi().equals(ToteutustyyppiEnum.KORKEAKOULUTUS) &&
                !komoto.getToteutustyyppi().equals(ToteutustyyppiEnum.LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA) &&
                !komoto.getToteutustyyppi().equals(ToteutustyyppiEnum.LUKIOKOULUTUS) &&
                !komoto.getToteutustyyppi().equals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA) &&
                !komoto.getToteutustyyppi().equals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA);
    }

    private boolean shouldUpdateLiiteStructure(KoulutusmoduuliToteutus komoto) {
        return !komoto.getToteutustyyppi().equals(ToteutustyyppiEnum.KORKEAKOULUTUS) &&
                !komoto.getToteutustyyppi().equals(ToteutustyyppiEnum.LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA) &&
                !komoto.getToteutustyyppi().equals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA) &&
                !komoto.getToteutustyyppi().equals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA);
    }

    private void updateLiitteet(Hakukohde hakukohde) {
        List<HakukohdeLiite> liitteet = new ArrayList<>();

        for (HakukohdeLiite oldLiite : hakukohde.getLiites()) {
            if (oldLiite.getKieli() == null) {
                liitteet.addAll(createLiitteet(oldLiite));
            }
        }

        if (liitteet.isEmpty()) {
            return;
        }

        hakukohde.getLiites().clear();

        for (HakukohdeLiite liite : liitteet) {
            hakukohde.addLiite(liite);
        }

        LOG.info("Updating hakukohde {} liitteet", hakukohde.getOid());
        hakukohdeDAO.update(hakukohde);
    }

    private List<HakukohdeLiite> createLiitteet(HakukohdeLiite oldLiite) {
        List<HakukohdeLiite> liitteet = new ArrayList<>();

        for (TekstiKaannos tekstiKaannos : oldLiite.getKuvaus().getKaannoksetAsList()) {
            HakukohdeLiite liite = new HakukohdeLiite();
            liite.setLiitetyyppi(oldLiite.getLiitetyyppi());
            liite.setKieli(substringBefore(tekstiKaannos.getKieliKoodi(), "#"));
            liite.setErapaiva(oldLiite.getErapaiva());
            liite.setHakukohdeLiiteNimi(oldLiite.getHakukohdeLiiteNimi());
            liite.setKuvaus(createKuvaus(tekstiKaannos));
            liite.setLastUpdateDate(new Date());
            liite.setLastUpdatedByOid(oldLiite.getLastUpdatedByOid());
            liite.setSahkoinenToimitusosoite(oldLiite.getSahkoinenToimitusosoite());
            liite.setToimitusosoite(oldLiite.getToimitusosoite());
            liite.setVersion(oldLiite.getVersion());
            liitteet.add(liite);
        }
        return liitteet;
    }

    private void updateValintakokeet(Hakukohde hakukohde) {
        List<Valintakoe> valintakokeet = new ArrayList<>();

        for (Valintakoe oldValintakoe : hakukohde.getValintakoes()) {
            if (oldValintakoe.getKieli() == null) {
                valintakokeet.addAll(createValintakokeet(oldValintakoe));
            }
        }

        if (valintakokeet.isEmpty()) {
            return;
        }

        hakukohde.getValintakoes().clear();

        for (Valintakoe valintakoe : valintakokeet) {
            hakukohde.addValintakoe(valintakoe);
        }

        LOG.info("Updating hakukohde {} valintakokeet", hakukohde.getOid());
        hakukohdeDAO.update(hakukohde);
    }

    private List<Valintakoe> createValintakokeet(Valintakoe oldValintakoe) {
        List<Valintakoe> valintakokeet = new ArrayList<>();

        for (TekstiKaannos tekstiKaannos : oldValintakoe.getKuvaus().getKaannoksetAsList()) {
            Valintakoe valintakoe = new Valintakoe();
            valintakoe.setKuvaus(createKuvaus(tekstiKaannos));
            valintakoe.setKieli(substringBefore(tekstiKaannos.getKieliKoodi(), "#"));
            valintakoe.setTyyppiUri(oldValintakoe.getTyyppiUri());
            valintakoe.setLastUpdateDate(new Date());
            valintakoe.setLastUpdatedByOid(oldValintakoe.getLastUpdatedByOid());
            valintakoe.setVersion(oldValintakoe.getVersion());

            Set<ValintakoeAjankohta> ajankohdat = createNewAjankohdat(oldValintakoe);
            for (ValintakoeAjankohta valintakoeAjankohta : ajankohdat) {
                valintakoeAjankohta.setValintakoe(valintakoe);
                valintakoe.addAjankohta(valintakoeAjankohta);
            }

            valintakokeet.add(valintakoe);
        }
        return valintakokeet;
    }

    private MonikielinenTeksti createKuvaus(TekstiKaannos tekstiKaannos) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();
        monikielinenTeksti.addTekstiKaannos(tekstiKaannos.getKieliKoodi(), tekstiKaannos.getArvo());
        return monikielinenTeksti;
    }

    private Set<ValintakoeAjankohta> createNewAjankohdat(Valintakoe oldValintakoe) {
        Set<ValintakoeAjankohta> ajankohdat = new HashSet<>();

        for (ValintakoeAjankohta oldAjankohta : oldValintakoe.getAjankohtas()) {
            ValintakoeAjankohta ajankohta = new ValintakoeAjankohta();
            ajankohta.setAjankohdanOsoite(oldAjankohta.getAjankohdanOsoite());
            ajankohta.setAlkamisaika(oldAjankohta.getAlkamisaika());
            ajankohta.setPaattymisaika(oldAjankohta.getPaattymisaika());
            ajankohta.setLisatietoja(oldAjankohta.getLisatietoja());
            ajankohdat.add(ajankohta);
        }
        return ajankohdat;
    }

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<List<HakukohdeV1RDTO>> findHakukohdesByKuvausId(Long id) {
        ResultV1RDTO<List<HakukohdeV1RDTO>> result = new ResultV1RDTO<>();

        ArrayList<Hakukohde> hakukohdes = new ArrayList<>();
        hakukohdes.addAll(hakukohdeDAO.findBy("valintaPerusteKuvausTunniste", id));
        hakukohdes.addAll(hakukohdeDAO.findBy("soraKuvausTunniste", id));

        ArrayList<HakukohdeV1RDTO> hakukohdeV1RDTOs = new ArrayList<>();
        for (Hakukohde hakukohde : hakukohdes) {
            if (hakukohde.getTila() != TarjontaTila.POISTETTU) {
                hakukohdeV1RDTOs.add(converterV1.toHakukohdeRDTO(hakukohde));
            }
        }

        result.setStatus(ResultV1RDTO.ResultStatus.OK);
        result.setResult(hakukohdeV1RDTOs);
        return result;
    }

}
