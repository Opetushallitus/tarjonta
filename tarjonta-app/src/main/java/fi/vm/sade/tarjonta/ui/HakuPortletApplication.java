package fi.vm.sade.tarjonta.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wolfie.blackboard.Blackboard;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.ui.app.AbstractSadePortletApplication;
import fi.vm.sade.tarjonta.ui.view.HakuRootView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;

public class HakuPortletApplication extends AbstractSadePortletApplication {
    
    private static final Logger LOG = LoggerFactory.getLogger(TarjontaPortletApplication.class);

    private Window window;

    @Override
    protected void registerListeners(Blackboard blackboard) {
        LOG.debug("registerListeners()");
    }

    @Override
    public synchronized void init() {
        LOG.debug("init()");

        super.init();

        window = new HakuRootView();
        setMainWindow(window);
    }
}
