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

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.YhteyshenkiloDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaVirheKoodi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;

/**
 *
 */
@Service
@Transactional
public class KoulutusBusinessServiceImpl implements KoulutusBusinessService {

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
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
            case LUKIOKOULUTUS:
                moduuli = handleLukiomoduuli(koulutus);
                break;
            default:
                throw new RuntimeException("Unsupported koulutustyyppi.");
        }

        if (moduuli == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value());
        }

        KoulutusmoduuliToteutus komotoModel = new KoulutusmoduuliToteutus();
        EntityUtils.copyFields(koulutus, komotoModel);
        komotoModel.setKoulutusmoduuli(moduuli);
        moduuli.addKoulutusmoduuliToteutus(komotoModel);

        KoulutusmoduuliToteutus response =  koulutusmoduuliToteutusDAO.insert(komotoModel);
        //TODO index
        return response;
    }

    private Koulutusmoduuli handleToisenAsteenModuuli(LisaaKoulutusTyyppi koulutus) {

//        if (koulutus.getKoulutusohjelmaKoodi() == null) {
//            moduuli = koulutusmoduuliDAO.findTutkintoOhjelma(koulutus.getKoulutusKoodi().getUri(), null);
//        } else {
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
        //}
        return moduuli;

    }

    private Koulutusmoduuli handleLukiomoduuli(LisaaKoulutusTyyppi koulutus) {

//        if (koulutus.getLukiolinjaKoodi() == null) {
//            moduuli = koulutusmoduuliDAO.findLukiolinja(koulutus.getKoulutusKoodi().getUri(), null);
//       } else {

        Koulutusmoduuli moduuli = koulutusmoduuliDAO.findLukiolinja(koulutus.getKoulutusKoodi().getUri(), koulutus.getLukiolinjaKoodi().getUri());

        if (moduuli == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value()
                    + " - koulutus koodi : " + koulutus.getKoulutusKoodi().getUri()
                    + ", lukiolinja koodi : " + koulutus.getLukiolinjaKoodi().getUri());
        }

        
        //Handling the creation of the parent komoto
        handleParentKomoto(koulutus, moduuli);
        //}

        return moduuli;
    }

    @Override
    public KoulutusmoduuliToteutus updateKoulutus(PaivitaKoulutusTyyppi koulutus) {

        final String oid = koulutus.getOid();
        KoulutusmoduuliToteutus model = koulutusmoduuliToteutusDAO.findByOid(oid);

        Koulutusmoduuli moduuli = model.getKoulutusmoduuli();
        //Handling the creation or update of the parent (tutkinto) komoto
        handleParentKomoto(koulutus, moduuli);

        if (model == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.OID_EI_OLEMASSA.value(), oid);
        }

        EntityUtils.copyFields(koulutus, model);

        koulutusmoduuliToteutusDAO.update(model);

        return model;

    }

    /*
     * Handling the creation or update of the parent komoto
     */
    private void handleParentKomoto(KoulutusTyyppi koulutus, Koulutusmoduuli moduuli) {
        Koulutusmoduuli parentKomo = this.koulutusmoduuliDAO.findParentKomo(moduuli);
        List<KoulutusmoduuliToteutus> parentKomotos = this.koulutusmoduuliToteutusDAO.findKomotosByKomoAndtarjoaja(parentKomo, koulutus.getTarjoaja());
        KoulutusmoduuliToteutus parentKomoto = (parentKomotos != null && !parentKomotos.isEmpty()) ? parentKomotos.get(0) : null;
        //If the komoto for the parentKomo already exists it is updated according to the values given in koulutus
        if (parentKomoto != null && parentKomo != null) {
            parentKomoto.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva());
            parentKomoto.setKoulutusohjelmanValinta(EntityUtils.copyFields(koulutus.getKoulutusohjelmanValinta()));
            this.koulutusmoduuliToteutusDAO.update(parentKomoto);

            //Start date is updated to siblings of the komoto given in koulutus. The start date is 
            //replicated to the children of the parent komoto to enable more efficient search based 
            //on start year and semester of komotos. 
            handleChildKomos(parentKomo, moduuli, koulutus);
            //If there is not a komoto for the parentKomo, it is created here.    
        } else if (parentKomo != null) {
            parentKomoto = new KoulutusmoduuliToteutus();
            generateOidForKomoto(parentKomoto);
            parentKomoto.setTarjoaja(koulutus.getTarjoaja());
            parentKomoto.setTila(EntityUtils.convertTila(koulutus.getTila()));
            parentKomoto.setKoulutusmoduuli(parentKomo);
            parentKomoto.setKoulutusohjelmanValinta(EntityUtils.copyFields(koulutus.getKoulutusohjelmanValinta()));
            parentKomoto.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva());
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
        Random r = new Random();
        long number = oidMin + ((long) (r.nextDouble() * (oidMax - oidMin)));
        int checkDigit = ibmCheck(number);
        return OID_PRE + number + checkDigit;
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
    private void handleChildKomos(Koulutusmoduuli parentKomo, Koulutusmoduuli moduuli, KoulutusTyyppi koulutus) {
        for (Koulutusmoduuli curChildKomo : parentKomo.getAlamoduuliList()) {

            List<KoulutusmoduuliToteutus> curKomotos = this.koulutusmoduuliToteutusDAO.findKomotosByKomoAndtarjoaja(curChildKomo, koulutus.getTarjoaja());

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
    }

    private boolean isNew(BaseEntity e) {
        // no good
        return (e.getId() == null);
    }
}
