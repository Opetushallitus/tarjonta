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
package fi.vm.sade.tarjonta.service.business.impl;

import java.lang.reflect.Method;
import java.util.*;

import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.YhteyshenkiloDAO;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaVirheKoodi;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Service
@Transactional
public class KoulutusBusinessServiceImpl implements KoulutusBusinessService {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusBusinessServiceImpl.class);
    
    @Autowired
    private OidService oidService;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private KoulutusmoduuliToteutusDAOImpl koulutusmoduuliToteutusDAO;
    @Autowired
    private YhteyshenkiloDAO yhteyshenkiloDAO;

    @Override
    public Koulutusmoduuli create(Koulutusmoduuli moduuli) {

        return koulutusmoduuliDAO.insert(moduuli);

    }

    @Override
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, Koulutusmoduuli moduuli) {

        final Koulutusmoduuli m = isNew(moduuli) ? create(moduuli) : moduuli;
        toteutus.setKoulutusmoduuli(m);

        return (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.insert(toteutus);
    }

    @Override
    public Koulutusmoduuli findTutkintoOhjelma(String koulutusLuokitusUri, String koulutusOhjelmaUri) {
        // todo: dao kerroksen voisi poistaa, ainoastaan vaikeammat haut voisi sijoittaa helper:n taakse
        return koulutusmoduuliDAO.findTutkintoOhjelma(koulutusLuokitusUri, koulutusOhjelmaUri);
    }

    @Override
    public List<Koulutusmoduuli> findTutkintoOhjelmat() {
        // todo: dao kerroksen voisi poistaa, ainoastaan vaikeammat haut voisi sijoittaa helper:n taakse      
        return koulutusmoduuliDAO.findAll();
    }

    @Override
    public KoulutusmoduuliToteutus createKoulutus(LisaaKoulutusTyyppi koulutus) {
        Koulutusmoduuli moduuli = null;

        if (koulutus.getKoulutustyyppi() == null) {
            throw new TarjontaBusinessException("Undefined koulutustyyppi.");
        }

        switch (koulutus.getKoulutustyyppi()) {
            case AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS: //no break.
            case MAAHANM_AMM_VALMISTAVA_KOULUTUS: //no break.
            case MAAHANM_LUKIO_VALMISTAVA_KOULUTUS: //no break.
            case PERUSOPETUKSEN_LISAOPETUS: //no break.
            case VAPAAN_SIVISTYSTYON_KOULUTUS: //no break.
            case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS://no break.
            case AMMATILLINEN_PERUSKOULUTUS:
                moduuli = handleToisenAsteenModuuli(koulutus);
                break;
            case LUKIOKOULUTUS:
                moduuli = handleLukiomoduuli(koulutus);
                break;
            case AMMATTIKORKEAKOULUTUS:
            case YLIOPISTOKOULUTUS:
                moduuli = handleKorkeakoulumoduuli(koulutus);
                break;

            default:
                throw new RuntimeException("Unsupported koulutustyyppi.");
        }

        if (moduuli == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value());
        }

        KoulutusmoduuliToteutus komotoModel = new KoulutusmoduuliToteutus();
        EntityUtils.copyFields(koulutus, komotoModel);

        komotoModel.setViimIndeksointiPvm(komotoModel.getUpdated());
        komotoModel.setKoulutusmoduuli(moduuli);
        moduuli.addKoulutusmoduuliToteutus(komotoModel);
        komotoModel.setAlkamiskausiUri(getKausiFromDate(komotoModel.getKoulutuksenAlkamisPvm()));
        komotoModel.setAlkamisVuosi(getYearFromDate(komotoModel.getKoulutuksenAlkamisPvm()));
        KoulutusmoduuliToteutus response = koulutusmoduuliToteutusDAO.insert(komotoModel);
        return koulutusmoduuliToteutusDAO.findByOid(response.getOid());
    }

    private String getKausiFromDate(Date aloituspvm) {
        return IndexDataUtils.parseKausiKoodi(aloituspvm);
    }

    public static Integer getYearFromDate(Date aloitusPvm) {
        return new Integer(IndexDataUtils.parseYear(aloitusPvm));

    }

    private Koulutusmoduuli handleToisenAsteenModuuli(LisaaKoulutusTyyppi koulutus) {
        Koulutusmoduuli moduuli = koulutusmoduuliDAO.findTutkintoOhjelma(
                koulutus.getKoulutusKoodi().getUri(),
                koulutus.getKoulutusohjelmaKoodi().getUri());

        if (moduuli == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value()
                    + " - koulutus koodi : " + koulutus.getKoulutusKoodi().getUri()
                    + ", koulutusohjelma koodi : " + koulutus.getKoulutusohjelmaKoodi().getUri());
        }

        //Handling the creation of the parent komoto
        handleParentKomoto(koulutus, moduuli);

        return moduuli;

    }

    private Koulutusmoduuli handleLukiomoduuli(LisaaKoulutusTyyppi koulutus) {
        Koulutusmoduuli moduuli = koulutusmoduuliDAO.findLukiolinja(koulutus.getKoulutusKoodi().getUri(), koulutus.getLukiolinjaKoodi().getUri());

        if (moduuli == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value()
                    + " - koulutus koodi : " + koulutus.getKoulutusKoodi().getUri()
                    + ", lukiolinja koodi : " + koulutus.getLukiolinjaKoodi().getUri());
        }

        //Handling the creation of the parent komoto
        handleParentKomoto(koulutus, moduuli);

        return moduuli;
    }

    private Koulutusmoduuli handleKorkeakoulumoduuli(LisaaKoulutusTyyppi koulutus) {
        Preconditions.checkNotNull(koulutus, "LisaaKoulutusTyyppi cannot be null.");

        Koulutusmoduuli komo = null;
        if (koulutus.getKoulutusmoduuli() == null && koulutus.getKoulutusmoduuli().getOid() != null) {
            komo = koulutusmoduuliDAO.findByOid(koulutus.getKoulutusmoduuli().getOid());
        } else {
            komo = koulutusmoduuliDAO.createKomoKorkeakoulu(koulutus.getKoulutusmoduuli());
            this.koulutusmoduuliDAO.insert(komo);
        }

        return komo;
    }

    /**
     * Filtteröi monikieliset tekstit, poistaen kaikki jotka ovat tyhjiä ja eri
     * kuin opetuskieli.
     *
     * @param pkt
     */
    private void filterKieliKoodis(PaivitaKoulutusTyyppi pkt) {
        Set<String> ret = new HashSet<String>();
        for (KoodistoKoodiTyyppi kkt : pkt.getOpetuskieli()) {
            ret.add(kkt.getUri());
        }

        List<MonikielinenTekstiTyyppi> mkts = new ArrayList<MonikielinenTekstiTyyppi>();

        try {
            // haetaan mkt:t looppaamalla getterit läpi (parempi olisi pitää mkt:t enum->string mapissa)
            for (Method m : pkt.getClass().getMethods()) {
                if (m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && m.getReturnType().equals(MonikielinenTekstiTyyppi.class)) {
                    MonikielinenTekstiTyyppi mtt = (MonikielinenTekstiTyyppi) m.invoke(pkt);
                    if (mtt != null) {
                        mkts.add(mtt);
                        for (Teksti t : mtt.getTeksti()) {
                            if (t.getValue() != null && t.getValue().trim().length() > 0) {
                                ret.add(t.getKieliKoodi());
                            }
                        }
                    }

                }
            }
        } catch (Exception e) { // reflektiovirheitä varten, joita ei tietenkääns saisi tapahtua
            throw new RuntimeException(e);
        }

        for (MonikielinenTekstiTyyppi mtt : mkts) {
            for (Iterator<Teksti> i = mtt.getTeksti().iterator(); i.hasNext();) {
                Teksti t = i.next();
                if (!ret.contains(t.getKieliKoodi())) {
                    i.remove();
                }
            }
        }

    }

    @Override
    public KoulutusmoduuliToteutus updateKoulutus(PaivitaKoulutusTyyppi koulutus) {

        final String oid = koulutus.getOid();
        KoulutusmoduuliToteutus model = koulutusmoduuliToteutusDAO.findByOid(oid);

        filterKieliKoodis(koulutus);

        if (model == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.OID_EI_OLEMASSA.value(), oid);
        }

        Koulutusmoduuli moduuli = model.getKoulutusmoduuli();
        //Handling the creation or update of the parent (tutkinto) komoto
        handleParentKomoto(koulutus, moduuli);

        EntityUtils.copyFields(koulutus, model);
        model.setViimIndeksointiPvm(model.getUpdated());
        model.setAlkamisVuosi(getYearFromDate(model.getKoulutuksenAlkamisPvm()));
        model.setAlkamiskausiUri(getKausiFromDate(model.getKoulutuksenAlkamisPvm()));
        koulutusmoduuliToteutusDAO.update(model);
        model = koulutusmoduuliToteutusDAO.read(model.getId());

        return model;

    }

    /*
     * Handling the creation or update of the parent komoto
     */
    private void handleParentKomoto(KoulutusTyyppi koulutus, Koulutusmoduuli moduuli) {
        Koulutusmoduuli parentKomo = this.koulutusmoduuliDAO.findParentKomo(moduuli);
        String pohjakoulutusUri = koulutus.getPohjakoulutusvaatimus() != null ? koulutus.getPohjakoulutusvaatimus().getUri() : null;
        List<KoulutusmoduuliToteutus> parentKomotos = this.koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(parentKomo, koulutus.getTarjoaja(), pohjakoulutusUri);
        KoulutusmoduuliToteutus parentKomoto = (parentKomotos != null && !parentKomotos.isEmpty()) ? parentKomotos.get(0) : null;
        //If the komoto for the parentKomo already exists it is updated according to the values given in koulutus
        if (parentKomoto != null && parentKomo != null) {
            //parentKomoto.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva()); koulutuksen alkamispäivä is no longer saved in parent komoto
            EntityUtils.copyFields(parentKomoto.getTekstit(), koulutus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
            //parentKomoto.setKoulutusohjelmanValinta(EntityUtils.copyFields(koulutus.getKoulutusohjelmanValinta(), parentKomoto.getKoulutusohjelmanValinta()));
            //parentKomoto.setOpetuskieli(EntityUtils.toKoodistoUriSet(koulutus.getOpetuskieli()));
            this.koulutusmoduuliToteutusDAO.update(parentKomoto);

            //Start date is updated to siblings of the komoto given in koulutus. The start date is 
            //replicated to the children of the parent komoto to enable more efficient search based 
            //on start year and semester of komotos. 
            //handleChildKomos(parentKomo, moduuli, koulutus);
            //If there is not a komoto for the parentKomo, it is created here.    
        } else if (parentKomo != null) {
            parentKomoto = new KoulutusmoduuliToteutus();
            generateOidForKomoto(parentKomoto);
           // parentKomoto.setOpetuskieli(EntityUtils.toKoodistoUriSet(koulutus.getOpetuskieli()));
            parentKomoto.setTarjoaja(koulutus.getTarjoaja());
            parentKomoto.setTila(EntityUtils.convertTila(koulutus.getTila()));
            parentKomoto.setKoulutusmoduuli(parentKomo);
            try {
                parentKomoto.setOid(oidService.get(TarjontaOidType.KOMOTO));
            } catch (OIDCreationException e) {
                throw new RuntimeException(e);
            }
            EntityUtils.copyFields(parentKomoto.getTekstit(), koulutus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
            //parentKomoto.setKoulutusohjelmanValinta(EntityUtils.copyFields(koulutus.getKoulutusohjelmanValinta(), parentKomoto.getKoulutusohjelmanValinta()));
            //parentKomoto.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva());
            parentKomoto.setPohjakoulutusvaatimusUri(koulutus.getPohjakoulutusvaatimus() != null ? koulutus.getPohjakoulutusvaatimus().getUri() : null);
            parentKomo.addKoulutusmoduuliToteutus(parentKomoto);
            
            LOG.warn("**** handleParentKomoto - create new parent komoto: pkv = {}", parentKomoto.getPohjakoulutusvaatimusUri());

            if (parentKomoto.getPohjakoulutusvaatimusUri() != null && parentKomoto.getPohjakoulutusvaatimusUri().indexOf("#") < 0) {
                LOG.error("*** FAILING FAST *** to see where the problem lies...OVT-7849");
                throw new RuntimeException("parent komoto pohjakoulutusvaatimus = " + parentKomoto.getPohjakoulutusvaatimusUri());
            }
            
            this.koulutusmoduuliToteutusDAO.insert(parentKomoto);
        }
    }

    private void generateOidForKomoto(KoulutusmoduuliToteutus komoto) {
        String oidCandidate = null;
        boolean oidExists = true;
        while (oidExists) {
            oidCandidate = generateOid();
            oidExists = this.koulutusmoduuliToteutusDAO.findByOid(oidCandidate) != null ? true : false;
        }
        komoto.setOid(oidCandidate);
    }

    private String generateOid() {
        try {
            return oidService.get(TarjontaOidType.KOMOTO);
        } catch (OIDCreationException ex) {
            throw new TarjontaBusinessException("OID service unavailable.", ex);
        }
    }

    private boolean isNew(BaseEntity e) {
        // no good
        return (e.getId() == null);
    }
}
