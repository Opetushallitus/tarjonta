package fi.vm.sade.tarjonta.ui.view;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.view.haku.HakuResultRow;
import fi.vm.sade.tarjonta.ui.view.haku.ListHakuViewImpl;

public class HakuSearchResultView extends VerticalLayout {

    private static final long serialVersionUID = 3524699826568713447L;
    boolean attached = false;
    private I18NHelper _i18n = new I18NHelper(this);

    public HakuSearchResultView() {
        super();
    }

    @Override
    public void attach() {
        super.attach();
        if (attached) {
            return;
        }

        attached = true;


        TabSheet tabs = new TabSheet();
        addComponent(tabs);

        ListHakuViewImpl hakuList = new ListHakuViewImpl();
        hakuList.addListener(new Listener() {
            private static final long serialVersionUID = -8696709317724642137L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof HakuResultRow.HakuRowMenuEvent) {
                    fireEvent(event);
                } else if (event instanceof ListHakuViewImpl.NewHakuEvent) {
                    fireEvent(event);
                }

            }
        });
        tabs.addTab(hakuList, T("haut"));
    }

    private String T(String key) {
        return _i18n.getMessage(key);
    }
}
