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
package fi.vm.sade.tarjonta.service.copy;

import com.mysema.commons.lang.Pair;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.*;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.MassakopiointiDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copy to entity objects.
 *
 * @author Jani Wilén
 */
@Component
public class CopyConverter {

    private static final Logger LOG = LoggerFactory.getLogger(CopyConverter.class);
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConverters;
    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired(required = true)
    private OidService oidService;
    @Autowired(required = true)
    private KoulutusCommonConverter commonConverter;
    @Autowired(required = true)
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired(required = true)
    private IndexerResource indexerResource;
    @Autowired(required = true)
    private MassakopiointiDAO massakopiointi;

    public void convert(final String processId, final String toHakuOid) {
        List<String> oldOids = massakopiointi.searchOids(new MassakopiointiDAO.SearchCriteria(null, null, null, Massakopiointi.Tyyppi.KOMOTO_ENTITY, processId));

        Date processing = new Date();
        for (String oldKomoOid : oldOids) {

            Pair<Object, MetaObject> find = massakopiointi.find(processId, oldKomoOid, KoulutusmoduuliToteutus.class);
            KoulutusmoduuliToteutus komoto = (KoulutusmoduuliToteutus) find.getFirst();
            final MetaObject meta = find.getSecond();

            LOG.info("convert komoto by oid : {}, new oid : {}", meta.getNewKomotoOid());
            komoto.setOid(meta.getNewKomotoOid());
            komoto.setTila(TarjontaTila.KOPIOITU);
            komoto.setKoulutusmoduuli(koulutusmoduuliDAO.findByOid(meta.getOriginalKomoOid()));
            komoto.setUlkoinenTunniste(processId);
            koulutusmoduuliToteutusDAO.insert(komoto);
            massakopiointi.updateTila(meta.getOriginalHakuOid(), oldKomoOid, Massakopiointi.KopioinninTila.PROSESSING, processing);
        }
    }
}
