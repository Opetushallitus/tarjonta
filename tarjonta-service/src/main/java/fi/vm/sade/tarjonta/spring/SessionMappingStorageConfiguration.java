package fi.vm.sade.tarjonta.spring;

import org.apereo.cas.client.session.HashMapBackedSessionMappingStorage;
import org.apereo.cas.client.session.SessionMappingStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionMappingStorageConfiguration {

  @Bean
  public SessionMappingStorage sessionMappingStorage() {
    return new HashMapBackedSessionMappingStorage();
  }
}
