package fi.vm.sade.tarjonta.spring.security;

import fi.vm.sade.java_utils.security.OpintopolkuCasAuthenticationFilter;
import fi.vm.sade.javautils.kayttooikeusclient.OphUserDetailsServiceImpl;
import fi.vm.sade.properties.OphProperties;
import fi.vm.sade.tarjonta.spring.ConfigEnums;
import fi.vm.sade.tarjonta.spring.properties.CasProperties;
import org.apereo.cas.client.session.SessionMappingStorage;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Cas20ProxyTicketValidator;
import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Profile("!dev")
@Configuration
@Order(2)
@EnableGlobalMethodSecurity(jsr250Enabled = false, prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class SecurityConfigDefault {

  private final CasProperties casProperties;
  private final OphProperties ophProperties;
  private final Environment environment;
  private final SessionMappingStorage sessionMappingStorage;

  @Autowired
  public SecurityConfigDefault(
      CasProperties casProperties,
      OphProperties ophProperties,
      Environment environment,
      SessionMappingStorage sessionMappingStorage) {
    this.casProperties = casProperties;
    this.ophProperties = ophProperties;
    this.environment = environment;
    this.sessionMappingStorage = sessionMappingStorage;
  }

  @Bean
  public ServiceProperties serviceProperties() {
    ServiceProperties serviceProperties = new ServiceProperties();
    serviceProperties.setService(casProperties.getService() + "/j_spring_cas_security_check");
    serviceProperties.setSendRenew(casProperties.getSendRenew());
    serviceProperties.setAuthenticateAllArtifacts(true);
    return serviceProperties;
  }

  //
  // CAS authentication provider (authentication manager)
  //

  @Bean
  public CasAuthenticationProvider casAuthenticationProvider() {
    CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
    String host =
        environment.getProperty(
            "host.host-alb", "https://" + environment.getRequiredProperty("host.host-virkailija"));
    casAuthenticationProvider.setUserDetailsService(
        new OphUserDetailsServiceImpl(host, ConfigEnums.CALLER_ID.value()));
    casAuthenticationProvider.setServiceProperties(serviceProperties());
    casAuthenticationProvider.setTicketValidator(ticketValidator());
    casAuthenticationProvider.setKey(casProperties.getKey());
    return casAuthenticationProvider;
  }

  @Bean
  public TicketValidator ticketValidator() {
    Cas20ProxyTicketValidator ticketValidator =
        new Cas20ProxyTicketValidator(ophProperties.url("cas.url") + "/cas");
    ticketValidator.setAcceptAnyProxy(true);
    return ticketValidator;
  }

  //
  // CAS filter
  //

  @Bean
  public CasAuthenticationFilter casAuthenticationFilter(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    OpintopolkuCasAuthenticationFilter casAuthenticationFilter =
        new OpintopolkuCasAuthenticationFilter(serviceProperties());
    casAuthenticationFilter.setAuthenticationManager(
        authenticationConfiguration.getAuthenticationManager());
    casAuthenticationFilter.setFilterProcessesUrl("/j_spring_cas_security_check");
    return casAuthenticationFilter;
  }

  //
  // CAS single logout filter
  // requestSingleLogoutFilter is not configured because our users always sign out through CAS
  // logout (using virkailija-raamit
  // logout button) when CAS calls this filter if user has ticket to this service.
  //
  @Bean
  public SingleSignOutFilter singleSignOutFilter() {
    SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
    singleSignOutFilter.setIgnoreInitConfiguration(true);
    singleSignOutFilter.setSessionMappingStorage(sessionMappingStorage);
    return singleSignOutFilter;
  }

  //
  // CAS entry point
  //

  @Bean
  public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
    CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
    casAuthenticationEntryPoint.setLoginUrl(ophProperties.url("cas.login"));
    casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
    return casAuthenticationEntryPoint;
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, CasAuthenticationFilter casAuthenticationFilter) throws Exception {
    http.headers(headers -> headers.disable())
        .csrf(csrf -> csrf.disable())
        .authorizeRequests()
        .requestMatchers("/buildversion.txt")
        .permitAll()
        .requestMatchers("/actuator/health")
        .permitAll()
        .requestMatchers("/swagger-ui/**")
        .permitAll()
        .requestMatchers("/swagger-resources/**")
        .permitAll()
        .requestMatchers("/swagger**")
        .permitAll()
        .requestMatchers("/webjars/springfox-swagger-ui/**")
        .permitAll()
        .requestMatchers("/v2/api-docs")
        .permitAll()
        .requestMatchers(HttpMethod.GET, "/api/v1/rest/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilter(casAuthenticationFilter)
        .exceptionHandling(eh -> eh.authenticationEntryPoint(casAuthenticationEntryPoint()))
        .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);

    return http.build();
  }
}
