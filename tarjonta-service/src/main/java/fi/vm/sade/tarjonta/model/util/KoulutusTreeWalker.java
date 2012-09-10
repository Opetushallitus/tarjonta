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
package fi.vm.sade.tarjonta.model.util;

import fi.vm.sade.tarjonta.model.Koulutus;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import java.util.Set;

/**
 * Helper class that traverses the "tree" of Koulutus in breath first and performs a test on tree nodes using WalkTester. 
 * This can be used to e.g. find parents or children.
 *
 * @author Jukka Raanamo
 */
public class KoulutusTreeWalker {

    private int walkedDepth;

    private int maxDepth;

    private NodeHandler matcher;

    public KoulutusTreeWalker(int maxDepth, NodeHandler matcher) {

        this.maxDepth = maxDepth;
        this.matcher = matcher;

    }

    public void walk(Koulutus startNode) {
        walkInternal(startNode);
    }


    private boolean walkInternal(Koulutus node) {

        if (maxDepth != -1 && walkedDepth == maxDepth) {
            return false;
        }

        if (!matcher.match(node)) {
            return false;
        }

        final Set<KoulutusSisaltyvyys> nextSet = node.getChildren();
        for (KoulutusSisaltyvyys s : nextSet) {
            final Koulutus nextNode = s.getChild();
            if (!walkInternal(nextNode)) {
                return false;
            }
        }

        // keep walking
        return true;
    }

    /**
     *
     */
    public interface NodeHandler {

        /**
         * Tests if walking the tree should continue past given Koulutus.
         * 
         * @param moduuli
         * @return
         */
        public boolean match(Koulutus kolutus);

    }


    /**
     * WalkTester that stops when a node equal to match node is found.
     */
    public static class EqualsMatcher implements NodeHandler {

        private boolean found;

        private Koulutus koulutus;

        public EqualsMatcher(Koulutus koulutus) {
            this.koulutus = koulutus;
        }

        @Override
        public boolean match(Koulutus k) {
            if (koulutus.equals(k)) {
                found = true;
                return false;
            }
            return true;
        }

        public boolean isFound() {
            return found;
        }

    }


}

