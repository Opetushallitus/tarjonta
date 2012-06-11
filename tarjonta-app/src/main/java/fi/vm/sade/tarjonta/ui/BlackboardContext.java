package fi.vm.sade.tarjonta.ui;

import com.github.wolfie.blackboard.Blackboard;

/**
 * Context for getting blackboard, Uses BlackboardProvider inside.
 * In Application we should use ThreadLocalBlackboardProvider (default).
 * When used together with AbstractSadeApplication,
 * this combination will init blackboard when application starts, and set/unset blackboard to threadlocal when request starts/ends.
 *
 * In component based selenium tests we should use SimpleBlackboardProvider, which gives same blackboard instance in every situation.
 *
 * @author Antti Salonen
 */
public class BlackboardContext {

    private static BlackboardProvider blackboardProvider = new ThreadLocalBlackboardProvider();

    public static BlackboardProvider getBlackboardProvider() {
        return blackboardProvider;
    }

    public static void setBlackboardProvider(BlackboardProvider blackboardProvider) {
        BlackboardContext.blackboardProvider = blackboardProvider;
    }

    public static Blackboard getBlackboard() {
        if (blackboardProvider == null) {
            throw new NullPointerException("BlackboardContext.blackboardProvider not initialized");
        }
        return blackboardProvider.getBlackboard();
    }

    public static void setBlackboard(Blackboard blackboard) {
        if (blackboardProvider == null) {
            throw new NullPointerException("BlackboardContext.blackboardProvider not initialized");
        }
        blackboardProvider.setBlackboard(blackboard);
    }

    // BlackboardProvider interface and two implementations

    public static interface BlackboardProvider {
        Blackboard getBlackboard();
        void setBlackboard(Blackboard blackboard);
    }

    public static class ThreadLocalBlackboardProvider implements BlackboardProvider {

        private ThreadLocal<Blackboard> blackboard = new ThreadLocal<Blackboard>();

        @Override
        public Blackboard getBlackboard() {
            return blackboard.get();
        }

        @Override
        public void setBlackboard(Blackboard blackboard) {
            this.blackboard.set(blackboard);
        }
    }

    public static class SimpleBlackboardProvider implements BlackboardProvider {
        private Blackboard blackboard = new Blackboard();

        @Override
        public Blackboard getBlackboard() {
            return blackboard;
        }

        @Override
        public void setBlackboard(Blackboard blackboard) {
            this.blackboard = blackboard;
        }
    }

}
