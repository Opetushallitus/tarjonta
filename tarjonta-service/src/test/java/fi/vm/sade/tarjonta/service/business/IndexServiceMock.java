package fi.vm.sade.tarjonta.service.business;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class IndexServiceMock implements IndexService {
    @Override
    public int indexHakukohdeBatch(List<Long> hakukohdeIdt, List<SolrInputDocument> docs, int batch_size, int index) {
        return 100;
    }

    @Override
    public int indexKoulutusBatch(List<Long> koulutukset, int batch_size, int index) {
        return 100;
    }
}
