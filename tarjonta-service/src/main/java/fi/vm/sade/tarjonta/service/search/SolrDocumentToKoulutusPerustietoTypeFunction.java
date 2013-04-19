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
package fi.vm.sade.tarjonta.service.search;

import java.util.Date;

import org.apache.solr.common.SolrDocument;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusKoosteTyyppi;

/**
 * Convert {@link SolrDocument} from solr to {@link HaeKoulutuksetVastausTyyppi.KoulutusTulos}
 */
public class SolrDocumentToKoulutusPerustietoTypeFunction implements
        Function<SolrDocument, HaeKoulutuksetVastausTyyppi.KoulutusTulos> {

    @Override
    public HaeKoulutuksetVastausTyyppi.KoulutusTulos apply(SolrDocument doc) {
        Preconditions.checkNotNull(doc);
        final HaeKoulutuksetVastausTyyppi.KoulutusTulos result = new HaeKoulutuksetVastausTyyppi.KoulutusTulos();
        
        final KoulutusKoosteTyyppi kooste = new KoulutusKoosteTyyppi();
        result.setKoulutus(kooste);
        
        //TODO populate the values...
        
        return result;
    }

    /**
     * Get date value
     * 
     * @param doc
     * @param field
     * @return
     */
    private Date dGet(final SolrDocument doc, final String field) {
        return (Date) doc.getFieldValue(field);
    }

    /**
     * Get String value
     * 
     * @param doc
     * @param field
     * @return
     */
    private String sGet(final SolrDocument doc, final String field) {
        return (String) doc.getFieldValue(field);
    }

}
