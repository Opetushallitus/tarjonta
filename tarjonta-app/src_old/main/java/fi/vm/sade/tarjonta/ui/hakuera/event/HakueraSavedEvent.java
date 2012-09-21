package fi.vm.sade.tarjonta.ui.hakuera.event;

import com.github.wolfie.blackboard.Event;
import com.github.wolfie.blackboard.Listener;
import com.github.wolfie.blackboard.annotation.ListenerMethod;

import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;

public class HakueraSavedEvent implements Event {

    private HakueraDTO hakueraDTO;
    
    public HakueraDTO getHakueraDTO() {
        return hakueraDTO;
    }

    public void setHakueraDTO(HakueraDTO hakueraDTO) {
        this.hakueraDTO = hakueraDTO;
    }

    public HakueraSavedEvent(HakueraDTO hakuera) {
        hakueraDTO = hakuera;
    }
    
    public interface HakueraSavedEventListener extends Listener {

        @ListenerMethod
        void onHakueraSaved(HakueraSavedEvent event);

    }

}
