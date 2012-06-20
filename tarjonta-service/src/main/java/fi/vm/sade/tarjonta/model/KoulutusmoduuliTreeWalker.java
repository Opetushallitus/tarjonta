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
package fi.vm.sade.tarjonta.model;

import java.util.Set;

/**
 * Helper class that traverses the "tree" of Koulutusmoduuli's and performs a test on tree nodes using WalkTester. 
 * This can be used to e.g. find parents or children.
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliTreeWalker {

    private int walkedDepth;

    private int maxDepth;

    private boolean walkDown;

    private WalkTester tester;

    public static KoulutusmoduuliTreeWalker createWalker(int maxDepth, WalkTester tester) {

        KoulutusmoduuliTreeWalker walker = new KoulutusmoduuliTreeWalker();
        walker.maxDepth = maxDepth;
        walker.tester = tester;

        return walker;

    }

    public void walkDown(Koulutusmoduuli startNode) {
        walkDown = true;
        walkInternal(startNode);
    }

    public void walkUp(Koulutusmoduuli startNode) {
        walkDown = false;
        walkInternal(startNode);
    }

    private boolean walkInternal(Koulutusmoduuli node) {

        if (maxDepth != -1 && walkedDepth == maxDepth) {
            return false;
        }

        if (!tester.continueWalking(node)) {
            return false;
        }

        Set<KoulutusmoduuliSisaltyvyys> nextSet = (walkDown ? node.getChildren() : node.getParents());
        for (KoulutusmoduuliSisaltyvyys s : nextSet) {
            Koulutusmoduuli nextNode = (walkDown ? s.getChild() : s.getParent());
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
    public interface WalkTester {

        /**
         * Tests if walking the tree should continue past given Koulutusmoduuli.
         * 
         * @param moduuli
         * @return
         */
        public boolean continueWalking(Koulutusmoduuli moduuli);

    }


    /**
     * WalkTester that stops when a node equal to match node is found.
     */
    public static class NodeEqualsTester implements WalkTester {

        private boolean found;

        private Koulutusmoduuli match;

        public NodeEqualsTester(Koulutusmoduuli match) {
            this.match = match;
        }

        @Override
        public boolean continueWalking(Koulutusmoduuli moduuli) {
            if (match.equals(moduuli)) {
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

