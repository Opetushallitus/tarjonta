package fi.vm.sade.tarjonta.service.tasks;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;

/**
 * Update koulutus/hakukohde indexes
 */
@Service
@EnableScheduling
public class IndexSyncronizer {
    
    @Autowired
    IndexerDAO indexerDao;

    @Autowired
    IndexerResource indexerResource;

    @Transactional
    @Scheduled(cron = "*/10 * * * * ?")
    public void updateKoulutukset() {
        List<Long> ids = indexerDao.findUnindexedHakukohdeIds();
        for (Long id : ids) {
            Date now = new Date();
            indexerResource.indexHakukohteet(Lists.newArrayList(id));
            indexerDao.updateHakukohdeIndexed(id, now);
        }            
    }

    @Transactional
    @Scheduled(cron = "*/10 * * * * ?")
    public void updateHakukohteet() {
        List<Long> ids = indexerDao.findUnindexedKoulutusIds();
        for (Long id : ids) {
            Date now = new Date();
            indexerResource.indexKoulutukset(Lists.newArrayList(id));
            indexerDao.updateKoulutusIndexed(id, now);
        }            
    }



}
