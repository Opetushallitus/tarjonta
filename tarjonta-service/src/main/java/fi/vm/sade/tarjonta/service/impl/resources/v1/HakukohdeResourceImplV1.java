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
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.KuvausDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidationMessages;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidator;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.ws.rs.PathParam;

/**
 *
 * @author mlyly
 */
public class HakukohdeResourceImplV1 implements HakukohdeV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImplV1.class);

    @Autowired
    private HakuDAO hakuDao;
    @Autowired
    private HakukohdeDAO hakukohdeDao;

    @Autowired
    private KuvausDAO kuvausDAO;

    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    
    @Autowired(required=true)
    TarjontaSearchService tarjontaSearchService;

    @Autowired
    private IndexerResource solrIndexer;



    @Autowired(required = true)
    private PublicationDataService publication;


    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private ConverterV1 converter;

    @Autowired
    private PermissionChecker permissionChecker;

    
    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> search(String searchTerms,
            List<String> organisationOids, List<String> hakukohdeTilas,
            String alkamisKausi, Integer alkamisVuosi, String hakukohdeOid, List<KoulutusasteTyyppi> koulutusastetyyppi) {

        organisationOids = organisationOids != null ? organisationOids
                : new ArrayList<String>();
        hakukohdeTilas = hakukohdeTilas != null ? hakukohdeTilas
                : new ArrayList<String>();

        HakukohteetKysely q = new HakukohteetKysely();
        q.setNimi(searchTerms);
        q.setKoulutuksenAlkamiskausi(alkamisKausi);
        q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
        q.getTarjoajaOids().addAll(organisationOids);
        if(hakukohdeOid!=null) {
            q.setHakukohdeOid(hakukohdeOid);
        }
        
        q.getKoulutusasteTyypit().addAll(koulutusastetyyppi);

        for (String s : hakukohdeTilas) {
            q.getTilat().add(
                    fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(s));
        }

        HakukohteetVastaus r = tarjontaSearchService.haeHakukohteet(q);

        return new ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>(converter.fromHakukohteetVastaus(r));
    }

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HashMap<String,String>> findHakukohdeValintaperusteet(String hakukohdeOid){
       Hakukohde hakukohde =  hakukohdeDao.findHakukohdeByOid(hakukohdeOid);

       if (hakukohde.getValintaperusteKuvaus() != null && hakukohde.getValintaperusteKuvaus().getTekstis() != null && hakukohde.getValintaperusteKuvaus().getTekstis().size() > 0) {
           ResultV1RDTO<HashMap<String,String>> result = new ResultV1RDTO<HashMap<String,String>>();

           HashMap<String,String> tekstis = converter.convertMonikielinenTekstiToStringHashMap(hakukohde.getValintaperusteKuvaus());
           result.setStatus(ResultV1RDTO.ResultStatus.OK);
           result.setResult(tekstis);

           return result;

       }  else {
           ResultV1RDTO<HashMap<String,String>> result = new ResultV1RDTO<HashMap<String,String>>();
           result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
           return result;
       }


    }

    @Override
    @Transactional
    public ResultV1RDTO<HashMap<String,String>> insertHakukohdeValintaPerusteet(String hakukohdeOid, HashMap<String,String> valintaPerusteet) {
        try {

            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);
            MonikielinenTeksti valintaPerusteetMonikielinen = converter.convertStringHashMapToMonikielinenTeksti(valintaPerusteet);

            hakukohde.setValintaperusteKuvaus(valintaPerusteetMonikielinen);

            permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
            hakukohdeDao.update(hakukohde);

            ResultV1RDTO<HashMap<String,String>> result = new ResultV1RDTO<HashMap<String,String>>();
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(valintaPerusteet);
            return  result;
        } catch (Exception exp) {
            ResultV1RDTO<HashMap<String,String>> errorResult = new ResultV1RDTO<HashMap<String,String>>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            errorResult.addError(ErrorV1RDTO.createSystemError(exp, null, null));
            return errorResult;
        }

    }


    @Override
    @Transactional
    public ResultV1RDTO<HashMap<String,String>> insertHakukohdeSora(String hakukohdeOid, HashMap<String,String> sorat){
        try {

            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);
            MonikielinenTeksti soraKuvaukset = converter.convertStringHashMapToMonikielinenTeksti(sorat);
            hakukohde.setSoraKuvaus(soraKuvaukset);
            permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
            hakukohdeDao.update(hakukohde);

            ResultV1RDTO<HashMap<String,String>> result = new ResultV1RDTO<HashMap<String,String>>();
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(sorat);
            return  result;

        } catch (Exception exp) {
            ResultV1RDTO<HashMap<String,String>> errorResult = new ResultV1RDTO<HashMap<String,String>>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            errorResult.addError(ErrorV1RDTO.createSystemError(exp, null, null));
            return errorResult;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HashMap<String,String>> findHakukohdeSoraKuvaukset(String hakukohdeOid) {
        try {

            ResultV1RDTO<HashMap<String,String>> result = new ResultV1RDTO<HashMap<String, String>>();

            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);
            if (hakukohde.getSoraKuvaus() != null && hakukohde.getSoraKuvaus().getKaannoksetAsList() != null) {

                HashMap<String,String> resultHm = new HashMap<String, String>();
                for (TekstiKaannos tekstiKaannos: hakukohde.getSoraKuvaus().getKaannoksetAsList()) {
                    resultHm.put(tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());
                }
               result.setResult(resultHm);
            }
            result.setStatus(ResultV1RDTO.ResultStatus.OK);

            return result;

        } catch (Exception exp) {
            ResultV1RDTO<HashMap<String,String>> exceptionResult = new ResultV1RDTO<HashMap<String, String>>();
            exceptionResult.addError(ErrorV1RDTO.createSystemError(exp, null, null));
            exceptionResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            return exceptionResult;
        }
    }



    @Override
    public ResultV1RDTO<List<OidV1RDTO>> search() {
        List<Hakukohde> hakukohdeList =  hakukohdeDao.findAll();

        List<OidV1RDTO> oidList = new ArrayList<OidV1RDTO>();
        if (hakukohdeList != null && hakukohdeList.size() > 0) {

            for (Hakukohde hakukohde:hakukohdeList) {

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
        Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(oid);

        HakukohdeV1RDTO hakukohdeRDTO = converter.toHakukohdeRDTO(hakukohde);

        if (hakukohdeRDTO.getSoraKuvausTunniste() != null) {
              hakukohdeRDTO.setSoraKuvaukset(getKuvauksetWithId(hakukohdeRDTO.getSoraKuvausTunniste(),hakukohdeRDTO.getSoraKuvausKielet()));
        }

        if (hakukohdeRDTO.getValintaPerusteKuvausTunniste() != null) {
             hakukohdeRDTO.setValintaperusteKuvaukset(getKuvauksetWithId(hakukohdeRDTO.getValintaPerusteKuvausTunniste(),hakukohdeRDTO.getValintaPerusteKuvausKielet()));
        }

        ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
        result.setResult(hakukohdeRDTO);
        result.setStatus(ResultV1RDTO.ResultStatus.OK);

        return result;
        } else {
            ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();

            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return result;
        }
    }

    private HashMap<String,String> getKuvauksetWithId(Long kuvausId, Set<String> kielet) {

        HashMap<String,String> kuvaukset = new HashMap<String,String>();

        ValintaperusteSoraKuvaus kuvaus = kuvausDAO.read(kuvausId);

        if (kielet != null ) {
            for (MonikielinenMetadata meta: kuvaus.getTekstis()) {
                for (String kieli : kielet) {
                    if (kieli.trim().equals(meta.getKieli().trim())) {
                        kuvaukset.put(meta.getKieli(),meta.getArvo());
                    }

                }

            }
        }


        return kuvaukset;

    }


    private Hakuaika getHakuAikaForHakukohde(HakukohdeV1RDTO hakukohdeRDTO,Haku haku) {
        LOG.debug("HAKUAIKA ID : {}", hakukohdeRDTO.getHakuaikaId());
        if (haku.getHakuaikas() != null) {
            Hakuaika selectedHakuaika = null;
            Long hakuaikaId = Long.decode(hakukohdeRDTO.getHakuaikaId());
            LOG.debug("HAKUAIKAID LONG : {}" , hakuaikaId );
            for (Hakuaika hakuaika : haku.getHakuaikas()) {

                if (hakuaika.getId().equals(hakuaikaId)) {
                    selectedHakuaika = hakuaika;
                }
            }
           LOG.debug("SELECTED HAKUAIKA : {}",selectedHakuaika);
           return selectedHakuaika;

        } else {
            LOG.warn("HAKU HAKUAIKAS IS NULL, IT SHOULD NOT BE!");
            return null;
        }
    }

    @Override
    @Transactional
    public ResultV1RDTO<HakukohdeV1RDTO> createHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {
        String hakuOid = hakukohdeRDTO.getHakuOid();
        List<HakukohdeValidationMessages> validationMessageses = HakukohdeValidator.validateHakukohde(hakukohdeRDTO);
        if (validationMessageses.size() > 0) {
            ResultV1RDTO<HakukohdeV1RDTO> errorResult = new ResultV1RDTO<HakukohdeV1RDTO>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
            for (HakukohdeValidationMessages message: validationMessageses) {
                errorResult.addError(ErrorV1RDTO.createValidationError(null,message.name(),null));
            }
            errorResult.setResult(hakukohdeRDTO);
            return errorResult;
        }
        hakukohdeRDTO.setOid(null);
        Hakukohde hakukohde = converter.toHakukohde(hakukohdeRDTO);
        hakukohde.setLastUpdateDate(new Date());

        LOG.debug("INSERT HAKUKOHDE OID : ", hakukohde.getOid());

        Haku haku = hakuDao.findByOid(hakuOid);
        hakukohde.setHaku(haku);

        if (hakukohdeRDTO.getHakuaikaId() != null) {

          hakukohde.setHakuaika(getHakuAikaForHakukohde(hakukohdeRDTO,haku));

        }
        //TODO: Should check createHakukohde ? It uses hakukohdeTyyppi which is WS-specific, if user has right to create
        //koulutus for organization doesn't he have the right to create hakukohdes also ?
        permissionChecker.checkCreateKoulutus(hakukohdeRDTO.getTarjoajaOids().iterator().next());

        hakukohde = hakukohdeDao.insert(hakukohde);

        hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohdeRDTO.getHakukohdeKoulutusOids(),hakukohde));

        //TODO, add valintakokees and liittees etc.

        hakukohdeDao.update(hakukohde);

        solrIndexer.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
        solrIndexer.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), new Function<KoulutusmoduuliToteutus, Long>() {
            public Long apply(@Nullable KoulutusmoduuliToteutus arg0) {
                return arg0.getId();
            }
        })));
        publication.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);

        hakukohdeRDTO.setOid(hakukohde.getOid());

        ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
        result.setStatus(ResultV1RDTO.ResultStatus.OK);
        result.setResult(hakukohdeRDTO);
        return result;
    }

    @Override
    @Transactional
    public ResultV1RDTO<HakukohdeV1RDTO> updateHakukohde(String hakukohdeOid,HakukohdeV1RDTO hakukohdeRDTO) {
        String hakuOid = hakukohdeRDTO.getHakuOid();

        List<HakukohdeValidationMessages> validationMessagesList = HakukohdeValidator.validateHakukohde(hakukohdeRDTO);
        if (validationMessagesList.size() > 0 ) {
            ResultV1RDTO<HakukohdeV1RDTO> errorResult = new ResultV1RDTO<HakukohdeV1RDTO>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
            for (HakukohdeValidationMessages message: validationMessagesList) {
                errorResult.addError(ErrorV1RDTO.createValidationError(null,message.name(),null));
            }
            errorResult.setResult(hakukohdeRDTO);
            return errorResult;
        }

        Hakukohde hakukohde = converter.toHakukohde(hakukohdeRDTO);
        hakukohde.setLastUpdateDate(new Date());
        Hakukohde hakukohdeTemp = hakukohdeDao.findHakukohdeByOid(hakukohdeRDTO.getOid());

        permissionChecker.checkUpdateHakukohde(hakukohdeTemp.getOid());

        //These are updated in a separate resource
        hakukohde.getValintakoes().clear();
        hakukohde.getValintakoes().addAll(hakukohdeTemp.getValintakoes());

        hakukohde.getLiites().clear();
        hakukohde.getLiites().addAll(hakukohdeTemp.getLiites());

        hakukohde.setId(hakukohdeTemp.getId());
        hakukohde.setVersion(hakukohdeTemp.getVersion());

        //Just in case remove kuvaukses if tunniste is defined
        if (hakukohde.getValintaPerusteKuvausTunniste() != null) {
            hakukohde.setValintaperusteKuvaus(null);
        }

        if (hakukohde.getSoraKuvausTunniste() != null) {
            hakukohde.setSoraKuvaus(null);
        }

        Haku haku = hakuDao.findByOid(hakuOid);

        hakukohde.setHaku(haku);

        if (hakukohdeRDTO.getHakuaikaId() != null) {
            hakukohde.setHakuaika(getHakuAikaForHakukohde(hakukohdeRDTO,haku));
        }


        hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohdeRDTO.getHakukohdeKoulutusOids(),hakukohde));
        //TODO: valintakoes and liites

        hakukohdeDao.update(hakukohde);
        solrIndexer.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
        solrIndexer.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), new Function<KoulutusmoduuliToteutus, Long>() {
            public Long apply(@Nullable KoulutusmoduuliToteutus arg0) {
                return arg0.getId();
            }
        })));
        publication.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);

        ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
        result.setStatus(ResultV1RDTO.ResultStatus.OK);
        result.setResult(hakukohdeRDTO);

        return result;
    }

    @Override
    @Transactional
    public ResultV1RDTO<Boolean> deleteHakukohde(String oid) {
        try {
            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(oid);

            permissionChecker.checkRemoveHakukohde(hakukohde.getOid());

            if (hakukohde.getKoulutusmoduuliToteutuses() != null) {
                for (KoulutusmoduuliToteutus koulutus:hakukohde.getKoulutusmoduuliToteutuses()) {
                    koulutus.removeHakukohde(hakukohde);
                }
            }

            hakukohdeDao.remove(hakukohde);
            solrIndexer.deleteHakukohde(Lists.newArrayList(hakukohde.getOid()));
            ResultV1RDTO<Boolean> result = new ResultV1RDTO<Boolean>();
            result.setResult(true);
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            return  result;

        } catch (Exception exp) {
            LOG.warn("Exception occured when removing hakukohde {}, exception : {}" , oid,exp.toString());
            ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<Boolean>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            errorResult.addError(ErrorV1RDTO.createSystemError(exp,null));
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
        }  else {
        try {

        List<ValintakoeV1RDTO> valintakoeV1RDTOs = new ArrayList<ValintakoeV1RDTO>();
        List<Valintakoe> valintakokees = hakukohdeDao.findValintakoeByHakukohdeOid(hakukohdeOid);
        for (Valintakoe valintakoe:valintakokees) {
            ValintakoeV1RDTO valintakoeV1RDTO = converter.fromValintakoe(valintakoe);

            valintakoeV1RDTOs.add(valintakoeV1RDTO);
        }
        resultRDTO.setResult(valintakoeV1RDTOs);
        resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

        } catch (Exception exp) {
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
            exp.printStackTrace();
            errorRDTO.setErrorTechnicalInformation(exp.toString());
            errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
            resultRDTO.addError(errorRDTO);
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
        try {
            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);
            permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
            Valintakoe valintakoe = converter.toValintakoe(valintakoeV1RDTO);
            if (hakukohdeOid != null && valintakoe != null) {
                LOG.debug("INSERTING VALINTAKOE : {} with kieli : {}" , valintakoe.getValintakoeNimi(), valintakoe.getKieli() );
                List<Valintakoe> valintakoes = hakukohdeDao.findValintakoeByHakukohdeOid(hakukohdeOid);
                valintakoes.add(valintakoe);
                hakukohdeDao.updateValintakoe(valintakoes,hakukohdeOid);
                ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<ValintakoeV1RDTO>();
                ValintakoeV1RDTO result = converter.fromValintakoe(valintakoe);
                rdtoResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                rdtoResultRDTO.setResult(result);
                return rdtoResultRDTO;
            }else {
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
           rdtoResultRDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
           ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
           exp.printStackTrace();
           errorRDTO.setErrorTechnicalInformation(exp.toString());
           errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
           rdtoResultRDTO.addError(errorRDTO);

           return rdtoResultRDTO;
        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<ValintakoeV1RDTO> updateValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        try {
            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);
            permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
            Valintakoe valintakoe = converter.toValintakoe(valintakoeV1RDTO);

            List<ValintakoeV1RDTO> valintakokees = new ArrayList<ValintakoeV1RDTO>();
            valintakokees.add(valintakoeV1RDTO);

            List<HakukohdeValidationMessages> validationMessageses = HakukohdeValidator.validateValintakokees(valintakokees);
            if (validationMessageses.size() > 0) {
                ResultV1RDTO<ValintakoeV1RDTO> errorResult = new ResultV1RDTO<ValintakoeV1RDTO>();
                errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
                for (HakukohdeValidationMessages message: validationMessageses) {
                    errorResult.addError(ErrorV1RDTO.createValidationError(null,message.name(),null));
                }
                errorResult.setResult(valintakoeV1RDTO);
                return errorResult;
            }

            LOG.debug("UPDATEVALINTAKOE SIZE: {} ", valintakoe.getAjankohtas().size());

            hakukohdeDao.updateSingleValintakoe(valintakoe,hakukohdeOid);
            LOG.debug("UPDATED VALINTAKOE");
            ResultV1RDTO<ValintakoeV1RDTO> valintakoeResult = new ResultV1RDTO<ValintakoeV1RDTO>();
            valintakoeResult.setStatus(ResultV1RDTO.ResultStatus.OK);
            valintakoeResult.setResult(valintakoeV1RDTO);
            return valintakoeResult;

        } catch (Exception exp) {
           ResultV1RDTO<ValintakoeV1RDTO> errorResult = new ResultV1RDTO<ValintakoeV1RDTO>();

            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
            errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
            exp.printStackTrace();
            errorRDTO.setErrorTechnicalInformation(exp.toString());
            errorResult.addError(errorRDTO);

           return errorResult;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<Boolean> removeValintakoe(String hakukohdeOid, String valintakoeOid) {
        try {
            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);
            permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
            LOG.debug("REMOVEVALINTAKOE: {}", valintakoeOid);
            Valintakoe valintakoe =  hakukohdeDao.findValintaKoeById(valintakoeOid);
            hakukohdeDao.removeValintakoe(valintakoe);

            ResultV1RDTO<Boolean> resultRDTO = new ResultV1RDTO<Boolean>();
            resultRDTO.setResult(true);
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            return resultRDTO;


        } catch (Exception exp) {
            ResultV1RDTO<Boolean> resultRDTO = new ResultV1RDTO<Boolean>();
            resultRDTO.setResult(false);
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);

            ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
            errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
            exp.printStackTrace();
            errorRDTO.setErrorTechnicalInformation(exp.toString());

            resultRDTO.addError(errorRDTO);
            return resultRDTO;

        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> findHakukohdeLiites(String hakukohdeOid) {

        if (hakukohdeOid != null) {
        try {

            ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> listResultRDTO = new ResultV1RDTO<List<HakukohdeLiiteV1RDTO>>();

            List<HakukohdeLiite> liites = hakukohdeDao.findHakukohdeLiitesByHakukohdeOid(hakukohdeOid);
            List<HakukohdeLiiteV1RDTO> liiteV1RDTOs = new ArrayList<HakukohdeLiiteV1RDTO>();
            if (liites != null) {
             LOG.debug("LIITES SIZE : {} ",liites.size());
             for (HakukohdeLiite liite : liites) {
                 liiteV1RDTOs.add(converter.fromHakukohdeLiite(liite));
             }
            }

            listResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            listResultRDTO.setResult(liiteV1RDTOs);
            return listResultRDTO;

        } catch (Exception exp) {
            ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> errorResult = new ResultV1RDTO<List<HakukohdeLiiteV1RDTO>>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            exp.printStackTrace();
            ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
            errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
            errorRDTO.setErrorTechnicalInformation(exp.toString());
            errorResult.addError(errorRDTO);
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

         try {
             Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);
             permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
             List<HakukohdeValidationMessages> validationMessageses = HakukohdeValidator.validateLiite(liiteV1RDTO);
             if (validationMessageses.size() > 0) {
                 ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResult = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
                 errorResult.setResult(liiteV1RDTO);
                 errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
                 for (HakukohdeValidationMessages msg : validationMessageses) {
                     errorResult.addError(ErrorV1RDTO.createValidationError(null, msg.name(), null));
                 }
                return errorResult;
             }

             ResultV1RDTO<HakukohdeLiiteV1RDTO> resultRDTO = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
             HakukohdeLiite hakukohdeLiite = converter.toHakukohdeLiite(liiteV1RDTO);
             List<HakukohdeLiite> liites = hakukohdeDao.findHakukohdeLiitesByHakukohdeOid(hakukohdeOid);
             liites.add(hakukohdeLiite);
             hakukohdeDao.insertLiittees(liites, hakukohdeOid);

             resultRDTO.setResult(converter.fromHakukohdeLiite(hakukohdeLiite));
             resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
             return resultRDTO;

         } catch (Exception exp) {
             ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResult = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
             errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
             ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
             errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
             exp.printStackTrace();
             errorRDTO.setErrorTechnicalInformation(exp.toString());
             errorResult.addError(errorRDTO);
             return errorResult;

         }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> updateHakukohdeLiite(String hakukohdeOid, HakukohdeLiiteV1RDTO liiteV1RDTO) {

        try {
            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);
            permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
            List<HakukohdeValidationMessages> validationMessageses = HakukohdeValidator.validateLiite(liiteV1RDTO);
            if (validationMessageses.size() > 0) {
                ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResult = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
                errorResult.setResult(liiteV1RDTO);
                errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
                for (HakukohdeValidationMessages msg : validationMessageses) {
                    errorResult.addError(ErrorV1RDTO.createValidationError(null, msg.name(), null));
                }
                return errorResult;
            }

            ResultV1RDTO<HakukohdeLiiteV1RDTO> resultRDTO = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();

            HakukohdeLiite hakukohdeLiite = converter.toHakukohdeLiite(liiteV1RDTO);



            hakukohdeDao.updateLiite(hakukohdeLiite,hakukohdeOid);

            resultRDTO.setResult(converter.fromHakukohdeLiite(hakukohdeLiite));
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

            return resultRDTO;

        } catch (Exception exp) {


           ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResultDto = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
           errorResultDto.setStatus(ResultV1RDTO.ResultStatus.OK);
           errorResultDto.addError(ErrorV1RDTO.createSystemError(exp,"system.error",hakukohdeOid));
           return errorResultDto;

        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<Boolean> deleteHakukohdeLiite(String hakukohdeOid, String liiteId) {

        try {
            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);
            permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
            HakukohdeLiite hakukohdeLiite = hakukohdeDao.findHakuKohdeLiiteById(liiteId);



            if (hakukohdeLiite != null && hakukohdeLiite.getId() != null) {

               hakukohdeDao.removeHakukohdeLiite(hakukohdeLiite);

                ResultV1RDTO<Boolean> booleanResultRDTO = new ResultV1RDTO<Boolean>();
                booleanResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                booleanResultRDTO.setResult(true);
                return booleanResultRDTO;
            }  else {
                ResultV1RDTO<Boolean> booleanResultRDTO = new ResultV1RDTO<Boolean>();
                booleanResultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                booleanResultRDTO.setResult(false);
                return booleanResultRDTO;
            }



        } catch (Exception exp) {
            ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<Boolean>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            errorResult.setResult(false);
            errorResult.addError(ErrorV1RDTO.createSystemError(exp, "system.error", hakukohdeOid));
            return errorResult;

        }

    }

    private Set<KoulutusmoduuliToteutus> findKoulutusModuuliToteutus(List<String> komotoOids, Hakukohde hakukohde) {
        Set<KoulutusmoduuliToteutus> komotos = new HashSet<KoulutusmoduuliToteutus>();

        for (String komotoOid : komotoOids) {
            KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(komotoOid);
            komoto.addHakukohde(hakukohde);
            komotos.add(komoto);
        }

        return komotos;
    }
    
    @Override
    @Transactional(readOnly = false)
    public ResultV1RDTO<String> updateTila(String oid, TarjontaTila tila) {
        Hakukohde hk = hakukohdeDao.findHakukohdeByOid(oid);
        Preconditions.checkArgument(hk != null, "Hakukohdetta ei löytynyt: %s",
                oid);
        if (!hk.getTila().acceptsTransitionTo(tila)) {
            return new ResultV1RDTO<String>(hk.getTila().toString());
        }
        hk.setTila(tila);
        hakukohdeDao.update(hk);
        solrIndexer.indexHakukohteet(Collections.singletonList(hk.getId()));
        return new ResultV1RDTO<String>(tila.toString());
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
        ResultV1RDTO<List<String>> resultV1RDTO = new ResultV1RDTO<List<String>>();

        Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);

        permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
        List<KoulutusmoduuliToteutus> liitettavatKomotot = koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(koulutukses);

        if (liitettavatKomotot != null && liitettavatKomotot.size() > 0) {

            for (KoulutusmoduuliToteutus komoto : liitettavatKomotot) {

                hakukohde.addKoulutusmoduuliToteutus(komoto);

                komoto.addHakukohde(hakukohde);

                koulutusmoduuliToteutusDAO.update(komoto);

            }

            hakukohdeDao.update(hakukohde);

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
        Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(hakukohdeOid);

        permissionChecker.checkUpdateHakukohde(hakukohde.getOid());
        if (hakukohde != null) {

            List<KoulutusmoduuliToteutus> komotoToRemove = new ArrayList<KoulutusmoduuliToteutus>();


            for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
                LOG.debug("Looping hakukohde komoto : {}",komoto.getOid());
                for (String komotoOid : koulutukses) {
                    if (komoto.getOid().trim().equals(komotoOid)) {
                        komotoToRemove.add(komoto);
                    }
                }

            }

            if (komotoToRemove.size() > 0) {
                Collection<KoulutusmoduuliToteutus> remainingKomotos =  CollectionUtils.subtract(hakukohde.getKoulutusmoduuliToteutuses(),komotoToRemove);


                LOG.debug("Removed {} koulutukses from hakukohde : {}",komotoToRemove.size(),hakukohde.getOid());
                if (remainingKomotos.size() > 0)  {

                    for (KoulutusmoduuliToteutus komoto : komotoToRemove) {

                        komoto.removeHakukohde(hakukohde);

                        hakukohde.removeKoulutusmoduuliToteutus(komoto);

                        koulutusmoduuliToteutusDAO.update(komoto);

                    }

                    LOG.debug("Hakukohde has more koulutukses, updating it");
                    hakukohdeDao.update(hakukohde);
                } else {

                    List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKoulutusModuulisWithHakukohdesByOids(koulutukses);

                    for (KoulutusmoduuliToteutus komoto : komotos) {

                        komoto.removeHakukohde(hakukohde);

                        hakukohde.removeKoulutusmoduuliToteutus(komoto);

                    }

                    LOG.debug("Hakukohde does not have anymore koulutukses, removing it");
                    hakukohdeDao.remove(hakukohde);
                    try {
                    solrIndexer.deleteHakukohde(Lists.newArrayList(hakukohdeOid));
                    } catch (Exception exp) {
                        LOG.warn("EXCEPTION REMOVING HAKUKOHDE: {} FROM INDEX : {}",hakukohdeOid,exp.toString());
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
}
