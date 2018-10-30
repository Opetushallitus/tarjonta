package fi.vm.sade.tarjonta.service.business;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class IndexServiceMock implements IndexService {
    @Override
    public int indexHakukohdeBatch(List<Long> hakukohdeIdt, int batch_size, int index) {
        return index + 100;
    }

    @Override
    public void indexKoulutukset(List<Long> ids) {

    }

    @Override
    public void indexHakukohteet(List<Long> ids) {

    }

    @Override
    public int indexKoulutusBatch(List<Long> koulutukset, int batch_size, int index) {
        return index + 100;
    }
}
