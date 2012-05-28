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
package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliDAOImpl;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliPerustiedot;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliSisaltyvyys;
import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jukka Raanamo
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusmoduuliDAOTest {

    @Autowired
    private KoulutusmoduuliDAO dao;

    @Test
    public void testSimpleSaveAndRead() {

        final String oid = "http://someurl";
        
        TutkintoOhjelma t1 = new TutkintoOhjelma();
        t1.setOid(oid);
        
        KoulutusmoduuliPerustiedot p = new KoulutusmoduuliPerustiedot();
        p.setOrganisaatioOid(oid);
        
        t1.setPerustiedot(p);
        t1 = (TutkintoOhjelma) dao.insert(t1);

        assertNotNull(t1.getId());

        TutkintoOhjelma t2 = read(t1.getId());
        assertNotNull(t2);
        assertEquals(oid, t2.getOid());
        assertEquals(oid, t2.getPerustiedot().getOrganisaatioOid());

    }
    
    @Test
    public void testMultipleParents() throws Exception {
        
        Koulutusmoduuli parent1 = new TutkintoOhjelma();
        Koulutusmoduuli parent2 = new TutkintoOhjelma();
        Koulutusmoduuli child = new TutkintoOhjelma();
        
        dao.insert(parent1);
        dao.insert(parent2);
        dao.insert(child);
        
        parent1.addChild(child, true);
        parent2.addChild(child, true);
        
        dao.update(parent1);
        dao.update(parent2);
        
        child = read(child.getId());
        
        assertEquals(2, child.getParents().size());
        
    }
    
    
    @Test
    public void testDelete() {
        
        TutkintoOhjelma t1 = new TutkintoOhjelma();
        dao.insert(t1);
        
        final Long id = t1.getId();
        assertNotNull(read(id));
        dao.remove(t1);
        
        assertNull(dao.read(id));
        
    }
    
    
    @Test
    public void testAddChildRelationshipToExisting() throws Exception {
        
        TutkintoOhjelma parent = new TutkintoOhjelma();
        TutkintoOhjelma child = new TutkintoOhjelma();
        
        dao.insert(parent);
        dao.insert(child);
        
        parent.addChild(child, true);
        
        dao.update(parent);
        
        //
        // check that parent has given child as only child
        //
        
        parent  = read(parent.getId());
        assertEquals(1, parent.getChildren().size());
        assertEquals(0, parent.getParents().size());
        
        KoulutusmoduuliSisaltyvyys parentToChild = parent.getChildren().iterator().next();
        assertEquals(child, parentToChild.getChild());
        assertTrue(parentToChild.isOptional());
        
        //
        // check that child has given parent as only parent
        //
        
        child = read(child.getId());
        assertEquals(1, child.getParents().size());
        assertEquals(0, child.getChildren().size());
        
        KoulutusmoduuliSisaltyvyys childToParent = child.getParents().iterator().next();
        assertEquals(child, childToParent.getChild());
        assertEquals(parent, childToParent.getParent());
        
        
    }
    
    @Test
    public void testRemovingRelationshipDoesNotRemoveNodes() throws Exception {
        
        TutkintoOhjelma parent = new TutkintoOhjelma();
        TutkintoOhjelma child = new TutkintoOhjelma();
        
        dao.insert(parent);
        dao.insert(child);
        
        parent.addChild(child, true);
        dao.update(parent);
                
        parent = read(parent.getId());
        assertTrue(parent.removeChild(child));
        
        assertEquals(0, parent.getChildren().size());
        assertEquals(0, child.getParents().size());
        
        // both entities have full cascading to relationships so removing from either end should work
        dao.update(parent);
        
        parent = read(parent.getId());
        child = read(child.getId());
        
        assertEquals(0, parent.getChildren().size());
        assertEquals(0, child.getParents().size());
        
        // check that none of the existing relationships are pointing to entities we just created,
        // unless we clean tables before this test, there may be some relations
        List<KoulutusmoduuliSisaltyvyys> rels = ((KoulutusmoduuliDAOImpl) dao).findAllSisaltyvyys();
        for (KoulutusmoduuliSisaltyvyys r : rels) {
            assertFalse("relationship was not removed", r.getParent().getId() == parent.getId());
            assertFalse("relationship was not remove", r.getChild().getId() == child.getId());
        }
        
    }
    
    
    @Test
    public void testCannotAddSameRelatioshipTwice() throws Exception {
        
        TutkintoOhjelma parent = new TutkintoOhjelma();
        TutkintoOhjelma child = new TutkintoOhjelma();
        
        dao.insert(parent);
        dao.insert(child);
        
        parent.addChild(child, true);
        
        dao.update(parent);
        
        parent.addChild(child, true);
        dao.update(parent);
        
        // this actually works: what are the constraints
        
    }
    
    private TutkintoOhjelma read(Long id) {
        return (TutkintoOhjelma) dao.read(id);
    }
    

}

