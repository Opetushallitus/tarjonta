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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public KoulutusmoduuliToteutus createKoulutus(LisaaKoulutusTyyppi koulutus) {

        Koulutusmoduuli moduuli = koulutusmoduuliDAO.findTutkintoOhjelma(
            koulutus.getKoulutusKoodi().getUri(),
            koulutus.getKoulutusohjelmaKoodi().getUri());

        if (moduuli == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value());
        }

        KoulutusmoduuliToteutus komotoModel = new KoulutusmoduuliToteutus();
        EntityUtils.copyFields(koulutus, komotoModel);
        komotoModel.setKoulutusmoduuli(moduuli);
        moduuli.addKoulutusmoduuliToteutus(komotoModel);

        //add all multilanguage strings to search keywords
        // TODO FIX
        //komotoModel.setNimi(SearchWordUtil.createSearchKeywords(koulutus));

        return koulutusmoduuliToteutusDAO.insert(komotoModel);
    }

    @Override
    public KoulutusmoduuliToteutus updateKoulutus(PaivitaKoulutusTyyppi koulutus) {

        final String oid = koulutus.getOid();
        KoulutusmoduuliToteutus model = koulutusmoduuliToteutusDAO.findByOid(oid);

        if (model == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.OID_EI_OLEMASSA.value(), oid);
        }

        EntityUtils.copyFields(koulutus, model);

        // add all multilanguage strings to search keywords
        // TODO FIX
        //model.setNimi(SearchWordUtil.createSearchKeywords(koulutus));

        koulutusmoduuliToteutusDAO.update(model);

        updateYhteyshenkilot(oid, koulutus.getYhteyshenkiloTyyppi());



        return model;

    }

    private boolean isNew(BaseEntity e) {
        // no good
        return (e.getId() == null);
    }

    private void updateYhteyshenkilot(final String oid, List<YhteyshenkiloTyyppi> yhteyshenkiloTyyppi) {
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findKomotoWithYhteyshenkilosByOid(oid);
        Set<Yhteyshenkilo> yhteyshenkilos = komoto.getYhteyshenkilos();
        Set<Yhteyshenkilo> removableObjects = new HashSet<Yhteyshenkilo>();

        if (yhteyshenkiloTyyppi != null && !yhteyshenkiloTyyppi.isEmpty()) {
            for (YhteyshenkiloTyyppi henkiloFrom : yhteyshenkiloTyyppi) {
                boolean updated = false;
                for (Yhteyshenkilo yhteyshenkilo : yhteyshenkilos) {
                    if (henkiloFrom.getHenkiloOid().equals(yhteyshenkilo.getHenkioOid())) {
                        //update existing object
                        EntityUtils.copyFields(henkiloFrom, yhteyshenkilo);
                        yhteyshenkiloDAO.update(yhteyshenkilo);
                        yhteyshenkilo.setPersisted(true);
                        updated = true;
                    }
                }

                if (!updated) {
                    //insert new object to database
                    Yhteyshenkilo to = new Yhteyshenkilo();
                    EntityUtils.copyFields(henkiloFrom, to);
                    to.setPersisted(true);
                    yhteyshenkiloDAO.insert(to);
                }
            }

        }

        for (Yhteyshenkilo y : yhteyshenkilos) {
            if (!y.isPersisted()) {
                removableObjects.add(y);
            }
        }

        for (Yhteyshenkilo y : removableObjects) {
            komoto.removeYhteyshenkilo(y);
            yhteyshenkiloDAO.remove(y);
        }
    }

}

