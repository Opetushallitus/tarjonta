/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.mock;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import fi.vm.sade.generic.ui.portlet.security.AccessRight;
import fi.vm.sade.generic.ui.portlet.security.User;
import fi.vm.sade.generic.ui.portlet.security.UserMock;
import fi.vm.sade.tarjonta.ui.service.UserProvider;

/**
 * This implementation is used when developing the app, specifically
 * permissions. Constructs user permissions & org membership from properties.
 */
@Component
@Profile("dev")
public class UserProviderMock extends UserProvider {

    private static final String ROLEPREFIX = "ROLE_APP_TARJONTA";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    public void setDebugCRUD(Boolean debugCRUD) {
        this.debugCRUD = debugCRUD;
    }

    public void setDebugRU(Boolean debugRU) {
        this.debugRU = debugRU;
    }

    public void setDebugR(Boolean debugR) {
        this.debugR = debugR;
    }

    public void setUserOrgSet(String userOrgSet) {
        this.userOrgSet = userOrgSet;
    }

    @Value("${debug.role.crud:false}")
    boolean debugCRUD;
    @Value("${debug.role.ru:false}")
    boolean debugRU;
    @Value("${debug.role.r:false}")
    boolean debugR;
    @Value("${debug.oidSet:}")
    String userOrgSet;

    @Override
    public User getUser() {

        final UserMock mockUser = new UserMock() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public Set<String> getOrganisations() {
                return ImmutableSet.copyOf(Iterables.transform(Splitter.on(" ").split(userOrgSet),
                        new Function<String, String>() {
                            @Override
                            public String apply(String input) {
                                return input.trim();
                            }
                        }));
            }

            @Override
            public String getOid() {
                return "mock-user-mock-oid";
            }

            @Override
            public Locale getLang() {
                return Locale.ENGLISH;
            }

            @Override
            public boolean isUserInRole(final String role) {
                Exception e = new Exception();
                logger.error("Nobody should call this method, caller from: {}", e.getStackTrace()[1]);
                return false;
            }

            @Override
            public List<AccessRight> getRawAccessRights() {
                throw new RuntimeException("not supported!");
            }

            @Override
            public Authentication getAuthentication() {
                List<GrantedAuthority> mockAuthorities = Lists.newArrayList();

                // add GrantedAuthorities to the mock user
                for (String ooid : getUser().getOrganisations()) {

                    if (debugCRUD) {
                        mockAuthorities.add(new SimpleGrantedAuthority(ROLEPREFIX + "_CRUD"));
                        mockAuthorities.add(new SimpleGrantedAuthority(ROLEPREFIX + "_CRUD_" + ooid));
                    }
                    if (debugRU) {
                        mockAuthorities.add(new SimpleGrantedAuthority(ROLEPREFIX + "_READ_UPDATE"));
                        mockAuthorities.add(new SimpleGrantedAuthority(ROLEPREFIX + "_READ_UPDATE_" + ooid));
                    }
                    if (debugR) {
                        mockAuthorities.add(new SimpleGrantedAuthority(ROLEPREFIX + "_READ"));
                        mockAuthorities.add(new SimpleGrantedAuthority(ROLEPREFIX + "_READ_" + ooid));
                    }
                }
                return new TestingAuthenticationToken("MOCK_TEST_USER", "ZZZ", mockAuthorities);
            }
        };

        return mockUser;
    }

}
