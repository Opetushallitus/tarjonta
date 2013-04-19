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

import org.apache.solr.common.SolrInputDocument;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.*;

/**
 * Convert "Koulutus" to {@link SolrInputDocument} so that it can be
 * indexed.
 */
public class KoulutusmoduuliToSolrInputDocumentFunction implements
        Function<Koulutusmoduuli, SolrInputDocument> {

    @Override
    public SolrInputDocument apply(final Koulutusmoduuli komo) {
        Preconditions.checkNotNull(komo);
        final SolrInputDocument doc = new SolrInputDocument();
        add(doc, OID, komo.getOid());
        return doc;
    }

    /**
     * Add field if value is not null
     * 
     * @param doc
     * @param nimifi
     * @param string
     */
    private void add(final SolrInputDocument doc, final String fieldName, final Object value) {
        if (value != null) {
            doc.addField(fieldName, value);
        }
    }
}
