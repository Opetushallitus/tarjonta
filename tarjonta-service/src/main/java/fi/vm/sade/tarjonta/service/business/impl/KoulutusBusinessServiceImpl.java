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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
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

/**
 *
 */
@Service
@Transactional
public class KoulutusBusinessServiceImpl implements KoulutusBusinessService {

    @Autowired
    private OIDService oidService;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private KoulutusmoduuliToteutusDAOImpl koulutusmoduuliToteutusDAO;
    @Autowired
    private KoulutusSisaltyvyysDAO sisaltyvyysDAO;
    @Autowired
    private YhteyshenkiloDAO yhteyshenkiloDAO;
    private static final String OID_PRE = "1.2.246.562.5.";
    private static long oidMin = 1000000000L;
    private static long oidMax = 10000000000L;

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
            case AMMATILLINEN_PERUSKOULUTUS:
                moduuli = handleToisenAsteenModuuli(koulutus);
                break;
            case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS:
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

        KoulutusmoduuliToteutus response = koulutusmoduuliToteutusDAO.insert(komotoModel);
        return koulutusmoduuliToteutusDAO.findByOid(response.getOid());
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
            this.koulutusmoduuliToteutusDAO.update(parentKomoto);

            //Start date is updated to siblings of the komoto given in koulutus. The start date is 
            //replicated to the children of the parent komoto to enable more efficient search based 
            //on start year and semester of komotos. 
            //handleChildKomos(parentKomo, moduuli, koulutus);
            //If there is not a komoto for the parentKomo, it is created here.    
        } else if (parentKomo != null) {
            parentKomoto = new KoulutusmoduuliToteutus();
            generateOidForKomoto(parentKomoto);
            parentKomoto.setTarjoaja(koulutus.getTarjoaja());
            parentKomoto.setTila(EntityUtils.convertTila(koulutus.getTila()));
            parentKomoto.setKoulutusmoduuli(parentKomo);
        	EntityUtils.copyFields(parentKomoto.getTekstit(), koulutus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
        	//parentKomoto.setKoulutusohjelmanValinta(EntityUtils.copyFields(koulutus.getKoulutusohjelmanValinta(), parentKomoto.getKoulutusohjelmanValinta()));
            //parentKomoto.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva());
            parentKomoto.setPohjakoulutusvaatimus(koulutus.getPohjakoulutusvaatimus() != null ? koulutus.getPohjakoulutusvaatimus().getUri() : null);
            parentKomo.addKoulutusmoduuliToteutus(parentKomoto);
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
            return oidService.newOid(NodeClassCode.TEKN_5);
        } catch (ExceptionMessage ex) {
            throw new TarjontaBusinessException("OID service unavailable.", ex);
        }
    }

    private int ibmCheck(Long oid) {
        String oidStr = oid.toString();

        int sum = 0;
        int[] alternate = {7, 3, 1};

        for (int i = oidStr.length() - 1, j = 0; i >= 0; i--, j++) {
            int n = Integer.parseInt(oidStr.substring(i, i + 1));

            sum += n * alternate[j % 3];
        }

        return 10 - sum % 10;
    }

    /*
     * updating the startDate to siblings of the komoto given in koulutus. 
     */
    /*private void handleChildKomos(Koulutusmoduuli parentKomo, Koulutusmoduuli moduuli, KoulutusTyyppi koulutus) {
     for (Koulutusmoduuli curChildKomo : parentKomo.getAlamoduuliList()) {

     String pohjakoulutusUri = koulutus.getPohjakoulutusvaatimus() != null ? koulutus.getPohjakoulutusvaatimus().getUri() : null;
     List<KoulutusmoduuliToteutus> curKomotos = this.koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(curChildKomo, koulutus.getTarjoaja(), pohjakoulutusUri);

     if (curKomotos == null) {
     continue;
     }
     for (KoulutusmoduuliToteutus curKomoto : curKomotos) {
     if (!curKomoto.getOid().equals(koulutus.getOid())) {
     curKomoto.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva());
     this.koulutusmoduuliToteutusDAO.update(curKomoto);
     }
     }
     }
     }*/
    private boolean isNew(BaseEntity e) {
        // no good
        return (e.getId() == null);
    }
}
