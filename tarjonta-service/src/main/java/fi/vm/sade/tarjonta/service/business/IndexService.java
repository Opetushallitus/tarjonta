package fi.vm.sade.tarjonta.service.business;

import org.apache.solr.common.SolrInputDocument;

import java.util.List;

public interface IndexService {
    int indexHakukohdeBatch(List<Long> hakukohdeIdt, List<SolrInputDocument> docs, int batch_size, int index);

    int indexKoulutusBatch(List<Long> koulutukset, int batch_size, int index);
}
