package fi.vm.sade.tarjonta;

import com.google.common.collect.Lists;
import fi.vm.sade.security.OidProvider;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * By default executes tests as CRUD_USER, override before to customize
 */
abstract public class SecurityAwareTestBase extends TestUtilityBase {
    
    @Value("${root.organisaatio.oid}")
    protected String ophOid;

    private OidProvider oidProvider;
    /**
     * Set permissions for current user, setup Mock oid provider
     */
    @Before
    public void before() {
        setCurrentUser("ophadmin", getAuthority("APP_" + TarjontaPermissionServiceImpl.TARJONTA + "_CRUD", ophOid));
        OidProvider oidProvider = Mockito.mock(OidProvider.class);
        
        Mockito.stub(oidProvider.getSelfAndParentOids(ophOid)).toReturn(
                Lists.newArrayList(ophOid));

        Mockito.stub(oidProvider.getSelfAndParentOids("1.2.3.4.5")).toReturn(
                Lists.newArrayList(ophOid, "1.2.3.4.5"));

        Mockito.stub(oidProvider.getSelfAndParentOids("oid-1")).toReturn(
                Lists.newArrayList(ophOid, "oid-1"));

        Mockito.stub(oidProvider.getSelfAndParentOids("jokin.tarjoaja.oid.1")).toReturn(
                Lists.newArrayList(ophOid, "jokin.tarjoaja.oid.1"));

        Mockito.stub(oidProvider.getSelfAndParentOids("1.2.3.4.555")).toReturn(
                Lists.newArrayList(ophOid, "1.2.3.4.555"));

        Mockito.stub(oidProvider.getSelfAndParentOids("1.2.3.4.556")).toReturn(
                Lists.newArrayList(ophOid, "1.2.3.4.556"));

        
        Mockito.stub(oidProvider.getSelfAndParentOids("1.2.3.4.557")).toReturn(
                Lists.newArrayList(ophOid, "1.2.3.4.557"));

        Mockito.stub(oidProvider.getSelfAndParentOids("non.existing.tarjoaja.oid")).toReturn(
                Lists.newArrayList(ophOid, "non.existing.tarjoaja.oid"));

        

        //save original oidprovider
        this.oidProvider = Whitebox.getInternalState(authorizer, "oidProvider");
        //set mock oidprovider
        Whitebox.setInternalState(authorizer, "oidProvider", oidProvider);
    }
    
    @After
    public void after(){
        //restore original oidprovider
        Whitebox.setInternalState(authorizer, "oidProvider", this.oidProvider);
    }
    
    protected final List<GrantedAuthority> getAuthority(String appPermission, String oid) {
        GrantedAuthority orgAuthority = new SimpleGrantedAuthority(String.format("%s", appPermission));
        GrantedAuthority roleAuthority = new SimpleGrantedAuthority(String.format("%s_%s", appPermission, oid));
        //System.out.println("roles:" + orgAuthority + " " + roleAuthority);
        return Lists.newArrayList(orgAuthority, roleAuthority);
    }
    
    protected final void setCurrentUser(final String oid, final List<GrantedAuthority> grantedAuthorities) {
        Authentication auth = new TestingAuthenticationToken(oid, null, grantedAuthorities);
        setAuthentication(auth);
    }

    protected final void setAuthentication(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


}
