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
package fi.vm.sade.tarjonta.ui;

import com.github.wolfie.blackboard.Blackboard;
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * This could be moved as common base class.
 * 
 * @author Jukka Raanamo
 */
public class SadeApplication extends Application implements TransactionListener {

    private static ThreadLocal<Blackboard> blackboard = new ThreadLocal<Blackboard>();

    private final Blackboard blackboardInstance = new Blackboard();

    /**
     * When overriding this method, remember to call super as the first thing.
     */
    @Override
    public synchronized void init() {

        blackboard.set(blackboardInstance);
        registerListeners(blackboardInstance);

    }

    /**
     * Invoked at init to register event listeners and events with given event bus.
     *
     * @param blackboard
     */
    protected void registerListeners(Blackboard blackboard) {
        // no-op
    }

    @Override
    public void transactionStart(Application application, Object transactionData) {
        if (application == this) {
            blackboard.set(blackboardInstance);
        }
    }

    @Override
    public void transactionEnd(Application application, Object transactionData) {
        if (application == this) {
            blackboard.set(null);
        }
    }

    public static Blackboard getBlackboard() {
        return blackboard.get();
    }

    public static void setBlackBoard(Blackboard blackBoard) {
        blackboard.set(blackBoard);
    }

}

