/*
 *
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
package fi.vm.sade.tarjonta.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nullable;
import javax.jws.WebParam;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.MonikielinenMetadataDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys.ValintaTyyppi;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.business.exception.HakuUsedException;
import fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException;
import fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.search.IndexerResource;

/**
 * @author Tuomas Katva
 * @author Timo Santasalo / Teknokala Ky
 */
@Transactional(rollbackFor = Throwable.class, readOnly = false)
@Service("tarjontaAdminService")
public class TarjontaAdminServiceImpl implements TarjontaAdminService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(TarjontaAdminServiceImpl.class);
    @Autowired(required = true)
    private HakuBusinessService hakuBusinessService;
    @Autowired(required = true)
    private KoulutusBusinessService koulutusBusinessService;
    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired(required = true)
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired(required = true)
    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;
    @Autowired(required = true)
    private HakuDAO hakuDAO;
    @Autowired(required = true)
    private HakukohdeDAO hakukohdeDAO;
    @Autowired(required = true)
    private ConversionService conversionService;
    @Autowired(required = true)
    private PublicationDataService publication;
    @Autowired(required = true)
    private MonikielinenMetadataDAO metadataDAO;
    @Autowired
    private TarjontaPublicService publicService;
    @Autowired
    private IndexerResource solrIndexer;
    @Autowired
    private TarjontaSearchService searchService;
    @Autowired
    private fi.vm.sade.log.client.Logger auditLogger;
    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;

    @Override
    @Transactional(readOnly = false)
    public HakuTyyppi paivitaHaku(HakuTyyppi hakuDto) {
        permissionChecker.checkHakuUpdate();

//        System.out.println("dto version:" + hakuDto.getVersion());
        final Haku foundHaku = hakuBusinessService.findByOid(hakuDto.getOid());

        if (foundHaku != null) {
            mergeHaku(conversionService.convert(hakuDto, Haku.class), foundHaku);
            hakuBusinessService.update(foundHaku);

            publication.sendEvent(foundHaku.getTila(), foundHaku.getOid(), PublicationDataService.DATA_TYPE_HAKU, PublicationDataService.ACTION_UPDATE);
            ListaaHakuTyyppi listaaHakuTyyppi = new ListaaHakuTyyppi();
            listaaHakuTyyppi.setHakuOid(foundHaku.getOid());
            HakuTyyppi hakuTyyppi = publicService.listHaku(listaaHakuTyyppi).getResponse().get(0);

            return hakuTyyppi;

        } else {
            throw new BusinessException("tarjonta.haku.update.no.oid");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public List<ValintakoeTyyppi> paivitaValintakokeitaHakukohteelle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid, @WebParam(name = "hakukohteenValintakokeet", targetNamespace = "") List<ValintakoeTyyppi> hakukohteenValintakokeet) {
        permissionChecker.checkUpdateHakukohde(hakukohdeOid);
        List<Valintakoe> valintakoes = convertValintaKokees(hakukohteenValintakokeet);
        List<Valintakoe> updateValintakokees = new ArrayList<Valintakoe>();
        for (Valintakoe valintakoe : valintakoes) {
            if (valintakoe.getId() != null) {
                updateValintakokees.add(valintakoe);
            }
        }
        hakukohdeDAO.updateValintakoe(updateValintakokees, hakukohdeOid);
        return hakukohteenValintakokeet;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public List<ValintakoeTyyppi> tallennaValintakokeitaHakukohteelle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid, @WebParam(name = "hakukohteenValintakokeet", targetNamespace = "") List<ValintakoeTyyppi> hakukohteenValintakokeet) {
        permissionChecker.checkUpdateHakukohde(hakukohdeOid);

        List<Valintakoe> valintakoes = convertValintaKokees(hakukohteenValintakokeet);
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);

        if (hakukohde != null) {
            hakukohdeDAO.updateValintakoe(valintakoes, hakukohde.getOid());

            hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
            if (hakukohde != null && hakukohde.getValintakoes() != null) {
                return convertValintakoeTyyppis(hakukohde.getValintakoes());
            } else {
                return new ArrayList<ValintakoeTyyppi>();
            }
        } else {
            throw new BusinessException("tarjonta.haku.no.hakukohde.found");
        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public void poistaHakukohdeLiite(@WebParam(name = "hakukohdeLiiteTunniste", targetNamespace = "") String hakukohdeLiiteTunniste) {
        permissionChecker.checkUpdateHakukohdeByHakukohdeliiteTunniste(hakukohdeLiiteTunniste);
        HakukohdeLiite liite = hakukohdeDAO.findHakuKohdeLiiteById(hakukohdeLiiteTunniste);
        Hakukohde hakukohde = liite.getHakukohde();
        hakukohde.removeLiite(liite);
        hakukohdeDAO.insert(hakukohde);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public void poistaValintakoe(@WebParam(name = "ValintakoeTunniste", targetNamespace = "") String valintakoeTunniste) {
        permissionChecker.checkUpdateHakukohdeByValintakoeTunniste(valintakoeTunniste);

        Valintakoe valintakoe = new Valintakoe();
        valintakoe.setId(new Long(valintakoeTunniste));

        hakukohdeDAO.removeValintakoe(valintakoe);

    }

    /*
     * This method returns true if komoto copy is allowed.
     */
    @Override
    public boolean tarkistaKoulutuksenKopiointi(@WebParam(partName = "parameters", name = "tarkistaKoulutusKopiointi", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") TarkistaKoulutusKopiointiTyyppi parameters) {
        //TODO add permission check??
        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKoulutusModuuliWithPohjakoulutusAndTarjoaja(parameters.getTarjoajaOid(), parameters.getPohjakoulutus(), parameters.getKoulutusLuokitusKoodi(), parameters.getKoulutusohjelmaKoodi(),
                parameters.getOpetuskielis(), parameters.getKoulutuslajis());
        if (komotos == null || komotos.size() < 1) {
            return true;
        } else {
            boolean retVal = true;
            komotoLoop:
            for (KoulutusmoduuliToteutus komoto : komotos) {
                if (checkKausiAndVuosi(komoto.getKoulutuksenAlkamisPvm(), parameters.getKoulutusAlkamisPvm())) {
                    retVal = false;
                    break komotoLoop;
                }
            }
            return retVal;
        }
    }

    /*
     * Returns true if compared dates year and kausi matches
     */
    private boolean checkKausiAndVuosi(Date komotoDate, Date checkDate) {
        Calendar komotoCal = Calendar.getInstance();
        Calendar checkCal = Calendar.getInstance();
        komotoCal.setTime(komotoDate);
        checkCal.setTime(checkDate);

        if (komotoCal.get(Calendar.YEAR) == checkCal.get(Calendar.YEAR)) {
            int komotoMonthInt = komotoCal.get(Calendar.MONTH);
            int checkMonthInt = checkCal.get(Calendar.MONTH);
            komotoMonthInt++;
            checkMonthInt++;
            String komotoKausi = getKausiStringFromMonth(komotoMonthInt);
            String checkKausi = getKausiStringFromMonth(checkMonthInt);

            if (komotoKausi.trim().equalsIgnoreCase(checkKausi.trim())) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

    private String getKausiStringFromMonth(int month) {
        if (month > 6) {
            return "s";
        } else {
            return "k";
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public void tallennaLiitteitaHakukohteelle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid, @WebParam(name = "hakukohteenLiitteen", targetNamespace = "") List<HakukohdeLiiteTyyppi> hakukohteenLiitteen) {
        permissionChecker.checkUpdateHakukohde(hakukohdeOid);

        List<HakukohdeLiite> liites = new ArrayList<HakukohdeLiite>();
        for (HakukohdeLiite hakukohdeLiite : convertLiiteTyyppi(hakukohteenLiitteen)) {
            liites.add(hakukohdeLiite);
        }

        hakukohdeDAO.insertLiittees(liites, hakukohdeOid);
    }

    private List<HakukohdeLiite> convertLiiteTyyppi(List<HakukohdeLiiteTyyppi> tyyppis) {
        ArrayList<HakukohdeLiite> hakukohdeLiites = new ArrayList<HakukohdeLiite>();

        for (HakukohdeLiiteTyyppi hakukohdeLiiteTyyppi : tyyppis) {
            HakukohdeLiite liite = conversionService.convert(hakukohdeLiiteTyyppi, HakukohdeLiite.class);
            hakukohdeLiites.add(liite);
        }

        return hakukohdeLiites;
    }

    private List<ValintakoeTyyppi> convertValintakoeTyyppis(Set<Valintakoe> valintakoes) {
        ArrayList<ValintakoeTyyppi> valintakoeTyyppis = new ArrayList<ValintakoeTyyppi>();

        for (Valintakoe valintakoe : valintakoes) {
            valintakoeTyyppis.add(conversionService.convert(valintakoe, ValintakoeTyyppi.class));
        }

        return valintakoeTyyppis;
    }

    private List<Valintakoe> convertValintaKokees(List<ValintakoeTyyppi> valintakoeTyyppis) {
        ArrayList<Valintakoe> valintakoes = new ArrayList<Valintakoe>();

        for (ValintakoeTyyppi valintakoeTyyppi : valintakoeTyyppis) {
            valintakoes.add(conversionService.convert(valintakoeTyyppi, Valintakoe.class));
        }

        return valintakoes;
    }

    private Hakuaika findHakuaika(Haku hk, SisaisetHakuAjat ha) {
        if (hk.getHakuaikas().size() == 1) {
            return hk.getHakuaikas().iterator().next();
        }
        if (ha != null && ha.getOid() != null) {
            long id = Long.parseLong(ha.getOid());
            for (Hakuaika hka : hk.getHakuaikas()) {
                if (hka.getId() == id) {
                    return hka;
                }
            }
        }
        return null;
    }

    private boolean checkHakuAndHakukohdekoulutusKaudet(HakukohdeTyyppi hakukohde, Haku haku) {



        //Get hakukohde koulutukses
        if (hakukohde.getHakukohteenKoulutusOidit() != null) {

            KoulutuksetKysely koulutusKysely = new KoulutuksetKysely();
            koulutusKysely.getKoulutusOids().addAll(hakukohde.getHakukohteenKoulutusOidit());
            List<KoulutusPerustieto> koulutusTuloses = searchService.haeKoulutukset(koulutusKysely).getKoulutukset();
            //Loop through hakukohtee's koulutukses and check all koulutukses and check that all have the same alkamiskausi and vuosi as the haku
            for (KoulutusPerustieto koulutusTulos : koulutusTuloses) {
                if (!koulutusTulos.getKoulutuksenAlkamiskausi().getUri().equals(haku.getKoulutuksenAlkamiskausiUri().trim())
                        || !koulutusTulos.getKoulutuksenAlkamisVuosi().equals(haku.getKoulutuksenAlkamisVuosi())) {
                    return false;
                }
            }


        } else {
            //If hakukohde does not have koulutukses it is a fault -> return false
            return false;
        }


        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public HakukohdeTyyppi lisaaHakukohde(HakukohdeTyyppi hakukohde) {
        permissionChecker.checkCreateHakukohde(hakukohde);

        Preconditions.checkNotNull(hakukohde, "HakukohdeTyyppi cannot be null.");
        final String hakuOid = hakukohde.getHakukohteenHakuOid();

        Preconditions.checkNotNull(hakuOid, "Haku OID (HakukohteenHakuOid) cannot be null.");
        Hakukohde hakuk = conversionService.convert(hakukohde, Hakukohde.class);
        Haku haku = hakuDAO.findByOid(hakuOid);
        if (!checkHakuAndHakukohdekoulutusKaudet(hakukohde, haku)) {
            throw new RuntimeException("hakukohde.koulutukses.alkamisaika.do.not.match.haku");
        }
        Preconditions.checkNotNull(haku, "Insert failed - no haku entity found by haku OID", hakuOid);

        hakuk.setHaku(haku);
        hakuk.setHakuaika(findHakuaika(haku, hakukohde.getSisaisetHakuajat()));
        hakuk = hakukohdeDAO.insert(hakuk);
        hakuk.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohde.getHakukohteenKoulutusOidit(), hakuk));
        hakuk.setViimIndeksointiPvm(hakuk.getLastUpdateDate());
        hakukohdeDAO.update(hakuk);
        solrIndexer.indexHakukohteet(Lists.newArrayList(hakuk.getId()));
        solrIndexer.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakuk.getKoulutusmoduuliToteutuses().iterator(), new Function<KoulutusmoduuliToteutus, Long>() {
            public Long apply(@Nullable KoulutusmoduuliToteutus arg0) {
                return arg0.getId();
            }
        })));

        publication.sendEvent(hakuk.getTila(), hakuk.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);

        //return fresh copy (that has fresh versions so that optimistic locking works)
        LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
        kysely.setOid(hakuk.getOid());
        return publicService.lueHakukohde(kysely).getHakukohde();
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
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public void lisaaTaiPoistaKoulutuksiaHakukohteelle(@WebParam(partName = "parameters", name = "lisaaKoulutusHakukohteelle", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LisaaKoulutusHakukohteelleTyyppi parameters) {
        permissionChecker.checkUpdateHakukohde(parameters.getHakukohdeOid());

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithDepenciesByOid(parameters.getHakukohdeOid());
        int originalHakukohdeKoulutusCount = hakukohde.getKoulutusmoduuliToteutuses().size();
        int numberOfKoulutuksesToRemove = parameters.getKoulutusOids().size();
        log.info("Hakukohde koulutukses : {}", hakukohde.getKoulutusmoduuliToteutuses().size());
        log.info("Number koulutukses to remove from hakukohde : {}", parameters.getKoulutusOids().size());
        if (parameters.isLisaa()) {
            Set<KoulutusmoduuliToteutus> komotos = findKoulutusModuuliToteutus(parameters.getKoulutusOids(), hakukohde);
            hakukohde.setKoulutusmoduuliToteutuses(komotos);
            log.info("Adding {} koulutukses to hakukohde: {}", hakukohde.getKoulutusmoduuliToteutuses().size(), hakukohde.getOid());
            hakukohdeDAO.update(hakukohde);
            solrIndexer.indexKoulutukset(getKomotoIds(komotos));
            List<Long> hakukohdeIds = new ArrayList<Long>();
            hakukohdeIds.add(hakukohde.getId());
            solrIndexer.indexHakukohteet(hakukohdeIds);
        } else {
            List<KoulutusmoduuliToteutus> poistettavatModuuliLinkitLista = koulutusmoduuliToteutusDAO.findKoulutusModuulisWithHakukohdesByOids(parameters.getKoulutusOids());
            Set<KoulutusmoduuliToteutus> poistettavatModuuliLinkit = new HashSet<KoulutusmoduuliToteutus>(poistettavatModuuliLinkitLista);
            for (KoulutusmoduuliToteutus komoto : poistettavatModuuliLinkit) {
                log.info("REMOVING KOULUTUS : {} FROM HAKUKOHDE {}", komoto.getOid(), hakukohde.getOid());

                komoto.removeHakukohde(hakukohde);

                hakukohde.removeKoulutusmoduuliToteutus(komoto);
                koulutusmoduuliToteutusDAO.update(komoto);
            }

            //If hakukohde has other koulutukses then just update it, otherwise remove it.

            if (!parameters.isLisaa()) {
                if (originalHakukohdeKoulutusCount > numberOfKoulutuksesToRemove) {
                    log.info("Removing : {} koulutukses from : {} koulutukses", poistettavatModuuliLinkit.size(), hakukohde.getKoulutusmoduuliToteutuses().size());
                    hakukohdeDAO.update(hakukohde);
                    List<Long> komotoIds = new ArrayList<Long>();
                    List<Long> hakukohdeOis = new ArrayList<Long>();
                    hakukohdeOis.add(hakukohde.getId());
                    for (KoulutusmoduuliToteutus komoto : poistettavatModuuliLinkit) {
                        komotoIds.add(komoto.getId());
                    }
                    solrIndexer.indexKoulutukset(komotoIds);
                    solrIndexer.indexHakukohteet(hakukohdeOis);
                } else {
                    HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();
                    hakukohdeTyyppi.setOid(parameters.getHakukohdeOid());
                    poistaHakukohde(hakukohdeTyyppi);
                }
            }

        }

    }

    private List<Long> getKomotoIds(Set<KoulutusmoduuliToteutus> komotos) {
        List<Long> komotoIds = new ArrayList<Long>();
        for (KoulutusmoduuliToteutus curKomoto : komotos) {
            komotoIds.add(curKomoto.getId());
        }
        return komotoIds;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public HakukohdeTyyppi poistaHakukohde(HakukohdeTyyppi hakukohdePoisto) {
        permissionChecker.checkRemoveHakukohde(hakukohdePoisto.getOid());
        Hakukohde hakukohde = hakukohdeDAO.findBy("oid", hakukohdePoisto.getOid()).get(0);

        if (hakuAlkanut(hakukohde)) {
            throw new HakukohdeUsedException();
        } else {
            for (KoulutusmoduuliToteutus curKoul : hakukohde.getKoulutusmoduuliToteutuses()) {
                curKoul.removeHakukohde(hakukohde);
            }
            try {
                log.info("Removing hakukohde from index...");
                solrIndexer.deleteHakukohde(Lists.newArrayList(hakukohdePoisto.getOid()));
                log.info("Removed hakukohde from index : {0}", hakukohdePoisto.getOid());
            } catch (IOException e) {
                throw new TarjontaBusinessException("indexing.error", e);
            }

            hakukohdeDAO.remove(hakukohde);
        }
        return new HakukohdeTyyppi();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public HakukohdeTyyppi paivitaHakukohde(HakukohdeTyyppi hakukohdePaivitys) {
        permissionChecker.checkUpdateHakukohde(hakukohdePaivitys.getOid());

        Hakukohde hakukohde = conversionService.convert(hakukohdePaivitys, Hakukohde.class);

        Hakukohde hakukohdeTemp = hakukohdeDAO.findHakukohdeByOid(hakukohdePaivitys.getOid());
        hakukohde.setId(hakukohdeTemp.getId());

        Haku haku = hakuDAO.findByOid(hakukohdePaivitys.getHakukohteenHakuOid());

        hakukohde.setHaku(haku);
        hakukohde.setHakuaika(findHakuaika(haku, hakukohdePaivitys.getSisaisetHakuajat()));
        hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohdePaivitys.getHakukohteenKoulutusOidit(), hakukohde));
        hakukohde.getValintakoes().addAll(hakukohdeTemp.getValintakoes());
        hakukohde.getLiites().addAll(hakukohdeTemp.getLiites());
        hakukohde.setViimIndeksointiPvm(hakukohde.getLastUpdateDate());
        hakukohdeDAO.update(hakukohde);
        solrIndexer.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
        publication.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_UPDATE);


        //return fresh copy (that has fresh version so that optimistic locking works)
        LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi(hakukohdePaivitys.getOid());
        return publicService.lueHakukohde(kysely).getHakukohde();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public HakuTyyppi lisaaHaku(HakuTyyppi hakuDto) {
        permissionChecker.checkCreateHaku();

        Haku haku = conversionService.convert(hakuDto, Haku.class);
        haku = hakuBusinessService.save(haku);

        publication.sendEvent(haku.getTila(), haku.getOid(), PublicationDataService.DATA_TYPE_HAKU, PublicationDataService.ACTION_INSERT);

        ListaaHakuTyyppi listaaHakuTyyppi = new ListaaHakuTyyppi();
        listaaHakuTyyppi.setHakuOid(haku.getOid());

        HakuTyyppi hakuTyyppi = publicService.listHaku(listaaHakuTyyppi).getResponse().get(0);

        return hakuTyyppi;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public void poistaHaku(HakuTyyppi hakuDto) throws GenericFault {
        permissionChecker.checkRemoveHaku();

        Haku haku = hakuBusinessService.findByOid(hakuDto.getOid());
        if (checkHakuDepencies(haku)) {
            throw new HakuUsedException();
        } else {
            hakuDAO.remove(haku);
        }

    }

    private boolean checkHakuDepencies(Haku haku) {
        List<Haku> haut = hakuDAO.findHakukohdeHakus(haku);
        if (haut != null && haut.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkHakukohdeDepencies(Hakukohde hakukohde) {
        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliDAO.findKomotoByHakukohde(hakukohde);
        if (komotos != null && komotos.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public LisaaKoulutusVastausTyyppi lisaaKoulutus(LisaaKoulutusTyyppi koulutus) {
        permissionChecker.checkCreateKoulutus(koulutus.getTarjoaja());
        checkOrganisationExists(koulutus.getTarjoaja());
        KoulutusmoduuliToteutus toteutus = koulutusBusinessService.createKoulutus(koulutus);
        solrIndexer.indexKoulutukset(Lists.newArrayList(toteutus.getId()));
        

        publication.sendEvent(toteutus.getTila(), toteutus.getOid(), PublicationDataService.DATA_TYPE_KOMOTO, PublicationDataService.ACTION_INSERT);

        LisaaKoulutusVastausTyyppi vastaus = new LisaaKoulutusVastausTyyppi();
        vastaus.setVersion(toteutus.getVersion()); //optimistic locking
        vastaus.setKomoOid(toteutus.getKoulutusmoduuli().getOid());

        return vastaus;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public PaivitaKoulutusVastausTyyppi paivitaKoulutus(PaivitaKoulutusTyyppi koulutus) {
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(koulutus.getTarjoaja());
        checkOrganisationExists(koulutus.getTarjoaja());
        KoulutusmoduuliToteutus toteutus = koulutusBusinessService.updateKoulutus(koulutus);


        publication.sendEvent(toteutus.getTila(), toteutus.getOid(), PublicationDataService.DATA_TYPE_KOMOTO, PublicationDataService.ACTION_UPDATE);
        try {
            solrIndexer.indexKoulutukset(Lists.newArrayList(toteutus.getId()));
        } catch (Throwable t) {
        }
        Set<Hakukohde> hakukohteet = toteutus.getHakukohdes();
        Set<Long> hakukohteenidt = Sets.newHashSet();
        for (Hakukohde hk : hakukohteet) {
            hakukohteenidt.add(hk.getId());
        }

        solrIndexer.indexHakukohteet(Lists.newArrayList(hakukohteenidt));

        return new PaivitaKoulutusVastausTyyppi();
    }

    /**
     * Tarkista että organisaatio löytyy. Heittää RuntimeExceptionin jos ei
     * löydy.
     *
     * @param tarjoaja
     */
    private void checkOrganisationExists(String tarjoaja) {
        List<OrganisaatioPerustieto> orgs = organisaatioSearchService.findByOidSet(Sets.newHashSet(tarjoaja));
        if (orgs.size() != 1 || (orgs.get(0).getLakkautusPvm() != null && orgs.get(0).getLakkautusPvm().before(new Date()))) {
            throw new RuntimeException("nonexisting.organisation.error");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public void poistaKoulutus(String koulutusOid) {
        permissionChecker.checkRemoveKoulutus(koulutusOid);
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(koulutusOid);

        if (komoto.getHakukohdes().isEmpty()) {
            this.koulutusmoduuliToteutusDAO.remove(komoto);
            try {
                solrIndexer.deleteKoulutus(Lists.newArrayList(koulutusOid));

            } catch (IOException e) {
                throw new TarjontaBusinessException("indexing.error", e);
            }

        } else {
            throw new KoulutusUsedException();
        }

    }

    private boolean hakuAlkanut(Hakukohde hakukohde) {
        for (Hakuaika curHakuaika : hakukohde.getHaku().getHakuaikas()) {
            if (!curHakuaika.getAlkamisPvm().after(Calendar.getInstance().getTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove once koodisto has proper data.
     */
    @Override
    public void initSample(String parameters) {
        //remove the method
    }

    /**
     * Remove once koodisto has proper data.
     */
    @Override
    public void initKomo(String parameters) {
        //remove the method
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public KoulutusmoduuliKoosteTyyppi lisaaKoulutusmoduuli(KoulutusmoduuliKoosteTyyppi komoKoosteTyyppi) throws GenericFault {
        permissionChecker.checkCreateKoulutusmoduuli();
        Preconditions.checkNotNull(komoKoosteTyyppi, "KoulutusmoduuliKoosteTyyppi object cannot be null.");
        Preconditions.checkNotNull(komoKoosteTyyppi.getKoulutustyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        Preconditions.checkNotNull(komoKoosteTyyppi.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");

        final String koulutuskoodiUri = komoKoosteTyyppi.getKoulutuskoodiUri();
        Preconditions.checkNotNull(koulutuskoodiUri, "Koulutuskoodi URI cannot be null.");

        Koulutusmoduuli komo = null;

        /*
         * Check type and fetch an existing KOMO, if any.
         */
        switch (komoKoosteTyyppi.getKoulutustyyppi()) {
            case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS: //no break.
            case AMMATILLINEN_PERUSKOULUTUS:
                //fetch children or parent
                komo = koulutusmoduuliDAO.findTutkintoOhjelma(koulutuskoodiUri, komoKoosteTyyppi.getKoulutusohjelmakoodiUri());
                break;
            case LUKIOKOULUTUS:
                //fetch children or parent
                komo = koulutusmoduuliDAO.findLukiolinja(koulutuskoodiUri, komoKoosteTyyppi.getLukiolinjakoodiUri());
                break;
            default:
                throw new GenericFault("Not supported KoulutusasteTyyppi object. Type : " + komoKoosteTyyppi.getKoulutustyyppi());
        }

        Koulutusmoduuli komoParent = null;

        if (komo == null) {
            //persist new KOMO
            komo = koulutusmoduuliDAO.insert(EntityUtils.copyFieldsToKoulutusmoduuli(komoKoosteTyyppi));
        }


        if (komoKoosteTyyppi.getParentOid() != null) {
            //added KOMO was a child, not parent.
            komoParent = handleParentKomo(komo, komoKoosteTyyppi.getParentOid());
        }

        Preconditions.checkNotNull(komo.getKoulutusKoodi(), "Koulutuskoodi URI cannot be null.");
        return EntityUtils.convertToKoulutusmoduuliKoosteTyyppi(komo, komoParent);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public KoulutusmoduuliKoosteTyyppi paivitaKoulutusmoduuli(KoulutusmoduuliKoosteTyyppi komoKoosteTyyppi) throws GenericFault {
        permissionChecker.checkUpdateKoulutusmoduuli();
        Preconditions.checkNotNull(komoKoosteTyyppi, "KoulutusmoduuliKoosteTyyppi object cannot be null.");
        Preconditions.checkNotNull(komoKoosteTyyppi.getOid(), "OID object cannot be null.");
        Preconditions.checkNotNull(komoKoosteTyyppi.getKoulutustyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        Preconditions.checkNotNull(komoKoosteTyyppi.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        Preconditions.checkNotNull(komoKoosteTyyppi.getKoulutuskoodiUri(), "Koulutuskoodi URI cannot be null.");

        Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(komoKoosteTyyppi.getOid());
        if (komo == null) {
            throw new RuntimeException("No KOMO found by OID '" + komoKoosteTyyppi.getOid() + "'.");
        }

        /*
         * Pre-validate the input data.
         */
        if (komoKoosteTyyppi.getKoulutusmoduuliTyyppi().equals(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA)) {
            switch (komoKoosteTyyppi.getKoulutustyyppi()) {
                case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS: //no break.
                case AMMATILLINEN_PERUSKOULUTUS:
                    //fetch children or parent
                    Preconditions.checkNotNull(komoKoosteTyyppi.getKoulutusohjelmakoodiUri(), "Koulutusohjelma URI cannot be null.");
                    break;
                case LUKIOKOULUTUS:
                    Preconditions.checkNotNull(komoKoosteTyyppi.getLukiolinjakoodiUri(), "Lukiolinja URI cannot be null.");
                    break;

                default:
                    throw new GenericFault("Not supported KoulutusasteTyyppi object. Type : " + komoKoosteTyyppi.getKoulutustyyppi());
            }
        }

        final Koulutusmoduuli convertedKomo = EntityUtils.copyFieldsToKoulutusmoduuli(komoKoosteTyyppi, komo);
        convertedKomo.setNimi(null);

        koulutusmoduuliDAO.update(convertedKomo);




        return EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppi(convertedKomo);
    }

    /**
     * Return parent KOMO or null if child KOMO.
     *
     * @param komo
     * @param parentOid
     * @return
     */
    private Koulutusmoduuli handleParentKomo(Koulutusmoduuli komo, String parentOid) {
        Preconditions.checkNotNull(parentOid, "Parent KOMO OID cannot be null.");
        Koulutusmoduuli parent = koulutusmoduuliDAO.findByOid(parentOid);
        Preconditions.checkNotNull(parent, "No parent KOMO found by OID '%s'.", parentOid);

        if (parent.getSisaltyvyysList().isEmpty()) {
            KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys();
            sisaltyvyys.setYlamoduuli(parent);
            sisaltyvyys.addAlamoduuli(komo);
            sisaltyvyys.setValintaTyyppi(ValintaTyyppi.SOME_OFF);
            this.koulutusSisaltyvyysDAO.insert(sisaltyvyys);
            parent.addSisaltyvyys(sisaltyvyys);
        } else {
            KoulutusSisaltyvyys sisaltyvyys = parent.getSisaltyvyysList().iterator().next();
            sisaltyvyys.addAlamoduuli(komo);
            this.koulutusSisaltyvyysDAO.update(sisaltyvyys);
        }
        koulutusmoduuliDAO.update(parent);
        komo.setKoulutusKoodi(parent.getKoulutusKoodi());
        koulutusmoduuliDAO.update(komo);

        return parent;
    }

    /**
     * @return the businessService
     */
    public HakuBusinessService getBusinessService() {
        return hakuBusinessService;
    }

    /**
     * @param businessService the businessService to set
     */
    public void setBusinessService(HakuBusinessService businessService) {
        this.hakuBusinessService = businessService;
    }

    /**
     * @return the conversionService
     */
    public ConversionService getConversionService() {
        return conversionService;
    }

    /**
     * @param conversionService the conversionService to set
     */
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * @return the hakuDao
     */
    public HakuDAO getHakuDao() {
        return hakuDAO;
    }

    /**
     * @param hakuDao the hakuDao to set
     */
    public void setHakuDao(HakuDAO hakuDao) {
        this.hakuDAO = hakuDao;
    }

    private void mergeHaku(Haku source, Haku target) {
        target.setNimi(null);
        target.setNimi(source.getNimi());
        target.setOid(source.getOid());
        target.setHakukausiUri(source.getHakukausiUri());
        target.setHakukausiVuosi(source.getHakukausiVuosi());
        target.setHakulomakeUrl(source.getHakulomakeUrl());
        target.setHakutapaUri(source.getHakutapaUri());
        target.setHakutyyppiUri(source.getHakutyyppiUri());
        target.setKohdejoukkoUri(source.getKohdejoukkoUri());
        target.setKoulutuksenAlkamiskausiUri(source.getKoulutuksenAlkamiskausiUri());
        target.setKoulutuksenAlkamisVuosi(source.getKoulutuksenAlkamisVuosi());
        target.setSijoittelu(source.isSijoittelu());
        target.setTila(source.getTila());
        target.setVersion(source.getVersion());
        target.setHaunTunniste(source.getHaunTunniste());
        mergeSisaisetHaunAlkamisAjat(source, target);
    }

    private void mergeSisaisetHaunAlkamisAjat(Haku source, Haku target) {
        Map<Long, Hakuaika> ths = new TreeMap<Long, Hakuaika>();
        for (Hakuaika ca : target.getHakuaikas()) {
            ths.put(ca.getId(), ca);
        }

        for (Hakuaika ca : source.getHakuaikas()) {
            if (ca.getId() == null) {
                // uusi
                target.addHakuaika(ca);
            } else {
                // vanha
                Hakuaika na = ths.remove(ca.getId());
                na.setSisaisenHakuajanNimi(ca.getSisaisenHakuajanNimi());
                na.setAlkamisPvm(ca.getAlkamisPvm());
                na.setPaattymisPvm(ca.getPaattymisPvm());
            }
        }

        for (Hakuaika ca : ths.values()) {
            target.removeHakuaika(ca);
        }
    }

    /**
     * @return the hakukohdeDAO
     */
    public HakukohdeDAO getHakukohdeDAO() {
        return hakukohdeDAO;
    }

    /**
     * @param hakukohdeDAO the hakukohdeDAO to set
     */
    public void setHakukohdeDAO(HakukohdeDAO hakukohdeDAO) {
        this.hakukohdeDAO = hakukohdeDAO;
    }

    /**
     * @return the koulutusmoduuliToteutusDAO
     */
    public KoulutusmoduuliToteutusDAO getKoulutusmoduuliToteutusDAO() {
        return koulutusmoduuliToteutusDAO;
    }

    /**
     * @param koulutusmoduuliToteutusDAO the koulutusmoduuliToteutusDAO to set
     */
    public void setKoulutusmoduuliToteutusDAO(KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO) {
        this.koulutusmoduuliToteutusDAO = koulutusmoduuliToteutusDAO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public PaivitaTilaVastausTyyppi paivitaTilat(PaivitaTilaTyyppi tarjontatiedonTila) {
        permissionChecker.checkTilaUpdate(tarjontatiedonTila);
        publication.updatePublicationStatus(tarjontatiedonTila.getTilaOids());
        indexTilatToSolr(tarjontatiedonTila);
        return new PaivitaTilaVastausTyyppi();
    }

    private void indexTilatToSolr(PaivitaTilaTyyppi tarjontatiedonTila) {
        List<Long> komotot = Lists.newArrayList();
        List<Long> hakukohteet = Lists.newArrayList();
        for (GeneerinenTilaTyyppi curTilaT : tarjontatiedonTila.getTilaOids()) {
            if (SisaltoTyyppi.KOMOTO.equals(curTilaT.getSisalto())) {
                KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(curTilaT.getOid());
                if (komoto != null) {
                    komotot.add(komoto.getId());
                }
            } else if (SisaltoTyyppi.HAKUKOHDE.equals(curTilaT.getSisalto())) {
                Hakukohde hakukohde = this.hakukohdeDAO.findHakukohdeWithKomotosByOid(curTilaT.getOid());
                if (hakukohde != null) {
                    hakukohteet.add(hakukohde.getId());
                }
            } else if (SisaltoTyyppi.HAKU.equals(curTilaT.getSisalto()) && curTilaT.getTila().equals(TarjontaTila.JULKAISTU)) {
                addRelatedHakukohteetAndKoulutukset(curTilaT.getOid(), komotot, hakukohteet);
            }
        }
        solrIndexer.indexKoulutukset(komotot);
        solrIndexer.indexHakukohteet(hakukohteet);
    }

    private void addRelatedHakukohteetAndKoulutukset(String hakuOid, List<Long> komotoIds, List<Long> hakukohdeIds) {
        List<String> hakuOids = new ArrayList<String>();
        hakuOids.add(hakuOid);
        List<Hakukohde> hakukohteet = publication.searchHakukohteetByHakuOid(hakuOids, fi.vm.sade.tarjonta.shared.types.TarjontaTila.JULKAISTU);
        for (Hakukohde curhakukohde : hakukohteet) {
            hakukohdeIds.add(curhakukohde.getId());
            for (KoulutusmoduuliToteutus komoto : curhakukohde.getKoulutusmoduuliToteutuses()) {
                komotoIds.add(komoto.getId());
            }
        }
    }

    @Override
    public boolean testaaTilasiirtyma(GeneerinenTilaTyyppi parameters) {
        return publication.isValidStatusChange(parameters);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public MonikielinenMetadataTyyppi tallennaMetadata(@WebParam(name = "avain", targetNamespace = "") String avain, @WebParam(name = "kategoria", targetNamespace = "") String kategoria, @WebParam(name = "kieli", targetNamespace = "") String kieli, @WebParam(name = "arvo", targetNamespace = "") String arvo) {
        permissionChecker.checkUpdateValintaperustekuvaus();

        log.info("tallennaMetadata({}, {}, {}, ...)", new Object[]{avain, kategoria, kieli});

        MonikielinenMetadata md = metadataDAO.createOrUpdate(avain, kategoria, kieli, arvo);
        log.info("  entity = {}", md);

        if (md == null) {
            //Metadata was removed.
            return null;
        }

        MonikielinenMetadataTyyppi result = new MonikielinenMetadataTyyppi();
        result.setKieli(md.getKieli());
        result.setKategoria(md.getKategoria());
        result.setAvain(md.getAvain());
        result.setArvo(md.getArvo());


        return result;
    }

    @Override
    public List<MonikielinenMetadataTyyppi> haeMetadata(@WebParam(name = "avain", targetNamespace = "") String avain, @WebParam(name = "kategoria", targetNamespace = "") String kategoria) {
        log.info("haeMetadata({}, {}) ...", avain, kategoria);

        List<MonikielinenMetadataTyyppi> result = new ArrayList<MonikielinenMetadataTyyppi>();

        List<MonikielinenMetadata> metadataEntities = null;
        if (avain == null && kategoria == null) {
            // Null search, find all
            metadataEntities = metadataDAO.findAll();
        } else if (kategoria == null) {
            // Find by avain if kategoria does not matter
            metadataEntities = metadataDAO.findByAvain(avain);
        } else if (avain == null) {
            // Find by kategoria if avain does not matter
            metadataEntities = metadataDAO.findByKategoria(kategoria);
        } else {
            // Otherwise find by avain and kategoria
            metadataEntities = metadataDAO.findByAvainAndKategoria(avain, kategoria);
        }

        // Convert to API
        for (MonikielinenMetadata md : metadataEntities) {
            MonikielinenMetadataTyyppi mdt = new MonikielinenMetadataTyyppi();
            mdt.setArvo(md.getArvo());
            mdt.setAvain(md.getAvain());
            mdt.setKategoria(md.getKategoria());
            mdt.setKieli(md.getKieli());

            result.add(mdt);
        }

        log.info("  result = {}", result);

        return result;
    }
}
