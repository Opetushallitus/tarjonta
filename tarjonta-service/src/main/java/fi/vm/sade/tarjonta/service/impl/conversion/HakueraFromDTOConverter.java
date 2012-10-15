package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

public class HakueraFromDTOConverter extends
        AbstractToDomainConverter<HakueraDTO, Haku> {

    @Autowired
    private HakuDAO dao;

    @Override
    public Haku convert(HakueraDTO hakueraDTO) {
        Haku haku = null;
        if (hakueraDTO.getOid() != null) {
            haku = dao.findByOid(hakueraDTO.getOid());
        }
        if (haku == null) {
            haku = new Haku();
        }
        haku.setNimiFi(hakueraDTO.getNimiFi());
        haku.setNimiSv(hakueraDTO.getNimiSv());
        haku.setNimiEn(hakueraDTO.getNimiEn());
        haku.setHakutyyppiUri(hakueraDTO.getHakutyyppi());
        haku.setHakukausiUri(hakueraDTO.getHakukausi());
        haku.setHakutapaUri(hakueraDTO.getHakutapa());
        haku.setOid(hakueraDTO.getOid());
        haku.setKohdejoukkoUri(hakueraDTO.getKohdejoukko());
        haku.setKoulutuksenAlkamiskausiUri(hakueraDTO.getKoulutuksenAlkaminen());
        haku.setHakulomakeUrl(hakueraDTO.getHakulomakeUrl());
        haku.setTila(hakueraDTO.getTila());
        return haku;
    }
    
    private Date convertDate(XMLGregorianCalendar cal) {
        Date convertedDate = null;
        if (cal != null) {
            convertedDate = cal.toGregorianCalendar().getTime();
        }
        return convertedDate;
    }

}
