package fi.vm.sade.tarjonta.ui.service;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;

public class UserContextTest {
    
    private String userOid="USER";
    private String subOrgOid = "1.2.2004.2";
    private String rootOrgOid = "1.0";


    @Test
    public void test() throws Exception {
      setCurrentUser(userOid, Lists.newArrayList(getAuthority("ROLE_TARJONTA_APP", subOrgOid)));
      UserContext context = new UserContext(rootOrgOid);
      context.afterPropertiesSet();
      Assert.assertEquals(1, context.getUserOrganisations().size());
      Assert.assertEquals(false, context.isOphUser());
      Assert.assertEquals(true, context.isDoAutoSearch());
      Assert.assertEquals(true, context.isUseRestriction());
      context.setUseRestriction(false);
      Assert.assertEquals(false, context.isUseRestriction());
    }

    
    List<GrantedAuthority> getAuthority(String appPermission, String oid) {
        GrantedAuthority orgAuthority = new SimpleGrantedAuthority(String.format("%s", appPermission));
        GrantedAuthority roleAuthority = new SimpleGrantedAuthority(String.format("%s_%s", appPermission, oid));
        return Lists.newArrayList(orgAuthority, roleAuthority);
    }
    
    static void setCurrentUser(final String oid, final List<GrantedAuthority> grantedAuthorities) {
        
        Authentication auth = new TestingAuthenticationToken(oid, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
