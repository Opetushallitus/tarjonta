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
        Haku hakuera = null;
        if (hakueraDTO.getOid() != null) {
            hakuera = dao.findByOid(hakueraDTO.getOid());
        }
        if (hakuera == null) {
            hakuera = new Haku();
        }
        hakuera.setNimiFi(hakueraDTO.getNimiFi());
        hakuera.setNimiSv(hakueraDTO.getNimiSv());
        hakuera.setNimiEn(hakueraDTO.getNimiEn());
        hakuera.setHakutyyppiUri(hakueraDTO.getHakutyyppi());
        hakuera.setHakukausiUri(hakueraDTO.getHakukausi());
        hakuera.setHakutapaUri(hakueraDTO.getHakutapa());
        hakuera.setHaunAlkamisPvm(convertDate(hakueraDTO.getHaunAlkamisPvm()));
        hakuera.setHaunLoppumisPvm(convertDate(hakueraDTO.getHaunLoppumisPvm()));
        hakuera.setOid(hakueraDTO.getOid());
        hakuera.setKohdejoukkoUri(hakueraDTO.getKohdejoukko());
        hakuera.setKoulutuksenAlkamiskausiUri(hakueraDTO.getKoulutuksenAlkaminen());
        hakuera.setHakulomakeUrl(hakueraDTO.getHakulomakeUrl());
        return hakuera;
    }
    
    private Date convertDate(XMLGregorianCalendar cal) {
        Date convertedDate = null;
        if (cal != null) {
            convertedDate = cal.toGregorianCalendar().getTime();
        }
        return convertedDate;
    }

}
