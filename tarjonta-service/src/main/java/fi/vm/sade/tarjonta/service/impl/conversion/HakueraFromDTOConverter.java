package fi.vm.sade.tarjonta.service.impl.conversion;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;

public class HakueraFromDTOConverter extends
        AbstractToDomainConverter<HakueraDTO, Hakuera> {

    @Override
    public Hakuera convert(HakueraDTO hakueraDTO) {
        // TODO Auto-generated method stub
        //return null;
        Hakuera hakuera = new Hakuera();
        hakuera.setNimiFi(hakueraDTO.getNimiFi());
        hakuera.setNimiSv(hakueraDTO.getNimiSv());
        hakuera.setNimiEn(hakueraDTO.getNimiEn());
        hakuera.setHakutyyppi(hakueraDTO.getHakutyyppi());
        hakuera.setHakukausi(hakueraDTO.getHakukausi());
        hakuera.setHakutapa(hakueraDTO.getHakutapa());
        hakuera.setHaunAlkamisPvm(convertDate(hakueraDTO.getHaunAlkamisPvm()));
        hakuera.setHaunLoppumisPvm(convertDate(hakueraDTO.getHaunLoppumisPvm()));
        hakuera.setOid(hakueraDTO.getOid());
        hakuera.setKohdejoukko(hakueraDTO.getKohdejoukko());
        hakuera.setKoulutuksenAlkaminen(hakueraDTO.getKoulutuksenAlkaminen());
        hakuera.setLomake(hakueraDTO.getLomake());
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
