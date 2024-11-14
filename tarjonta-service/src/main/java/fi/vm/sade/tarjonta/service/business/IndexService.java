package fi.vm.sade.tarjonta.service.business;

import java.util.List;

public interface IndexService {

  int indexHakukohdeBatch(List<Long> hakukohdeIdt, int batch_size, int index);

  void indexKoulutukset(List<Long> ids);

  void indexHakukohteet(List<Long> ids);

  int indexKoulutusBatch(List<Long> koulutukset, int batch_size, int index);
}
