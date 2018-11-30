package fi.vm.sade.tarjonta.service.tasks;

import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Update koulutus/hakukohde indexes
 */
@Service
@EnableScheduling
@Profile("default")
public class IndexSyncronizer {

    private final Logger logger = LoggerFactory.getLogger(IndexSyncronizer.class);

    @Autowired
    private IndexSyncronizerUtils indexSyncronizerUtils;

    @Autowired
    private IndexerDAO indexerDao;

    @Autowired
    private IndexerResource indexerResource;

    @Scheduled(cron = "*/10 * * * * ?")
    public void updateHakukohteet() {
        logger.debug("Searching for unindexed hakukohdes...");
        List<Long> ids = indexerDao.findUnindexedHakukohdeIds();
        indexerResource.indexHakukohteet(ids);
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void updateKoulutukset() {
        logger.debug("Searching for unindexed koulutukses...");
        List<Long> ids = indexerDao.findUnindexedKoulutusIds();
        indexerResource.indexKoulutukset(ids);
    }

    @Transactional
    @Scheduled(cron = "0 4 * * * ?")
    public void updateOrganizationChanges() {
        logger.debug("Starting organization change reindex");
        List<String> organizationOids = indexSyncronizerUtils.getChangedOrganizationOids();
        if (!organizationOids.isEmpty()) {
            indexerDao.reindexOrganizationChanges(organizationOids);
        }
    }

}
