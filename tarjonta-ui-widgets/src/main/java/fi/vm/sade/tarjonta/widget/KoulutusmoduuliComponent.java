package fi.vm.sade.tarjonta.widget;

import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.generic.ui.component.WrapperComponent;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSearchDTO;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * @author Antti Salonen
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class KoulutusmoduuliComponent extends WrapperComponent<KoulutusmoduuliDTO> {

    @Autowired
    protected KoulutusmoduuliAdminService koulutusmoduuliService;

    protected KoulutusmoduuliSearchDTO searchSpecification;

    public KoulutusmoduuliComponent(KoulutusmoduuliSearchDTO searchSpecification) {
        super(new KoulutusmoduuliCaptionFormatter(), new KoulutusmoduuliFieldValueFormatter());
        this.searchSpecification = searchSpecification;
        root = new HorizontalLayout();
        setCompositionRoot(root);
    }

    @Override
    protected List<KoulutusmoduuliDTO> loadOptions() {
        return koulutusmoduuliService.find(searchSpecification);
    }

    public void setKoulutusmoduuliService(KoulutusmoduuliAdminService koulutusmoduuliService) {
        this.koulutusmoduuliService = koulutusmoduuliService;
    }

    public static class KoulutusmoduuliCaptionFormatter implements CaptionFormatter<KoulutusmoduuliDTO> {
        @Override
        public String formatCaption(KoulutusmoduuliDTO dto) {
            return dto.getNimi();
        }
    }

    public static class KoulutusmoduuliFieldValueFormatter implements FieldValueFormatter<KoulutusmoduuliDTO> {
        @Override
        public Object formatFieldValue(KoulutusmoduuliDTO dto) {
            return dto.getOid();
        }
    }

}
