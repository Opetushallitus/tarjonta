package fi.vm.sade.tarjonta.spring;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"fi.vm.sade.tarjonta.dao", "fi.vm.sade.tarjonta.dao.impl"})
@EntityScan({
  "fi.vm.sade.tarjonta.model",
  "fi.vm.sade.tarjonta.model.searchParams",
  "fi.vm.sade.tarjonta.model.util"
})
@EnableTransactionManagement
public class JpaConfiguration {}
