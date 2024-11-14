package fi.vm.sade.tarjonta.config;

import fi.vm.sade.oid.generator.OIDGenerator;
import fi.vm.sade.oid.generator.impl.RandomLuhnOIDGenerator;
import fi.vm.sade.tarjonta.service.OidService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"fi.vm.sade.oid.dao"})
@Configuration
public class OidConfig {

  public OidConfig() {
    System.out.println("OidConfig init()");
  }

  @Bean
  public OIDGenerator getGenerator() {
    return new RandomLuhnOIDGenerator();
  }

  @Bean
  public OidService getOidService() {
    return new OidService();
  }

  //    @Bean
  //    @Autowired
  //    public OIDGeneratorFactory getFactory(OIDGenerator defaultGenerator) {
  //        OIDGeneratorFactory factory = new OIDGeneratorFactory();
  //        factory.setDefaultGenerator(defaultGenerator);
  //        return factory;
  //    }
  //
  //    @Bean
  //    public OIDService getOidService() {
  //        OIDServiceImpl oid = new OIDServiceImpl();
  //        return oid;
  //    }

}
