package fi.vm.sade.tarjonta.service.tasks;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
@Profile("default")
public class IndexSyncronizer {

    final Logger logger = LoggerFactory.getLogger(IndexSyncronizer.class);

    @Autowired
    private IndexerDAO indexerDao;

    @Autowired
    private IndexerResource indexerResource;

    @Transactional
    @Scheduled(cron = "*/10 * * * * ?")
    public void updateHakukohteet() {
        logger.debug("Searching for unindexed hakukohdes...");
        List<Long> ids = indexerDao.findUnindexedHakukohdeIds();
        for (Long id : ids) {
            Date now = new Date();
            logger.debug("  index now: id = {}", id);
            indexerResource.indexHakukohteet(Lists.newArrayList(id));
            indexerDao.updateHakukohdeIndexed(id, now);
        }
    }

    @Transactional
    @Scheduled(cron = "*/10 * * * * ?")
    public void updateKoulutukset() {
        logger.debug("Searching for unindexed koulutukses...");
        List<Long> ids = indexerDao.findUnindexedKoulutusIds();
        for (Long id : ids) {
            logger.debug("  index now: id = {}", id);
            Date now = new Date();
            indexerResource.indexKoulutukset(Lists.newArrayList(id));
            indexerDao.updateKoulutusIndexed(id, now);
        }
    }

}
