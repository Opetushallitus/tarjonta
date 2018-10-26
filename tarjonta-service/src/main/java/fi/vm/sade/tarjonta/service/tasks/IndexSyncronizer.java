package fi.vm.sade.tarjonta.service.tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fi.vm.sade.tarjonta.shared.OrganisaatioService;
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
    private IndexSyncronizerUtils indexSyncronizerUtils;

    @Autowired
    private IndexerDAO indexerDao;

    @Autowired
    private IndexerResource indexerResource;

    @Transactional
    @Scheduled(cron = "*/10 * * * * ?")
    public void updateHakukohteet() {
        logger.debug("Searching for unindexed hakukohdes...");
        List<Long> ids = indexerDao.findUnindexedHakukohdeIds();
        indexerResource.indexHakukohteet(ids)
                .forEach((id, date) -> {
                    logger.debug("  index now: id = {}", id);
                    indexerDao.updateHakukohdeIndexed(id, date);
        });
    }

    @Transactional
    @Scheduled(cron = "*/10 * * * * ?")
    public void updateKoulutukset() {
        logger.debug("Searching for unindexed koulutukses...");
        List<Long> ids = indexerDao.findUnindexedKoulutusIds();
        indexerResource.indexKoulutukset(ids)
                .forEach((id, date) -> {
                    logger.debug("  index now: id = {}", id);
                    indexerDao.updateKoulutusIndexed(id, date);
                });
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
