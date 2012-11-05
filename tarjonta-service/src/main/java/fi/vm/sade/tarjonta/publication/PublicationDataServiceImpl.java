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
package fi.vm.sade.tarjonta.publication;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.tarjonta.model.*;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jukka Raanamo
 */
@Service
@Transactional(readOnly = true)
public class PublicationDataServiceImpl implements PublicationDataService {

    @PersistenceContext
    public EntityManager em;

    @Override
    public List<KoulutusmoduuliToteutus> listKoulutusmoduuliToteutus() {

        QKoulutusmoduuliToteutus toteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        QKoulutusmoduuli m = QKoulutusmoduuli.koulutusmoduuli;

        // todo: filter only published

        return from(toteutus).
            leftJoin(toteutus.ammattinimikes).fetch().
            leftJoin(toteutus.avainsanas).fetch().
            leftJoin(toteutus.opetuskielis).fetch().
            leftJoin(toteutus.opetusmuotos).fetch().
            leftJoin(toteutus.koulutuslajis).fetch().
            leftJoin(toteutus.koulutusmoduuli, m).fetch().
            list(toteutus);
    }

    @Override
    public List<Hakukohde> listHakukohde() {

        QHakukohde hakukohde = QHakukohde.hakukohde;
        // selects all
        return from(hakukohde).list(hakukohde);

    }

    @Override
    public List<Haku> listHaku() {

        QHaku haku = QHaku.haku;
        return from(haku).list(haku);

    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(em).from(o);
    }

}

