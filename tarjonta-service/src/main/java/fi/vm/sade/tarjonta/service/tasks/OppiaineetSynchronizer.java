package fi.vm.sade.tarjonta.service.tasks;

import fi.vm.sade.tarjonta.dao.OppiaineDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
@Profile("default")
public class OppiaineetSynchronizer {

    @Autowired
    private OppiaineDAO oppiaineDAO;

    final Logger logger = LoggerFactory.getLogger(OppiaineetSynchronizer.class);

    @Transactional
    @Scheduled(cron = "0 2 * * * ?")
    public void deleteUnusedOppiaineet() {
        logger.debug("Deleting unused oppiaineet");
        oppiaineDAO.deleteUnusedOppiaineet();
    }

}
