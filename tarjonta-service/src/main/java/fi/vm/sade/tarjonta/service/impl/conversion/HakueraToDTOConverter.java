package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HakueraToDTOConverter extends
        AbstractFromDomainConverter<Hakuera, HakueraDTO> {

    @Override
    public HakueraDTO convert(
            Hakuera entity) {
        HakueraDTO hakueraDTO = new HakueraDTO();
        hakueraDTO.setNimiFi(entity.getNimiFi());
        hakueraDTO.setNimiSv(entity.getNimiSv());
        hakueraDTO.setNimiEn(entity.getNimiEn());
        hakueraDTO.setHakutyyppi(entity.getHakutyyppi());
        hakueraDTO.setHakukausi(entity.getHakukausi());
        hakueraDTO.setHakutapa(entity.getHakutapa());
        hakueraDTO.setHaunAlkamisPvm(convertDate(entity.getHaunAlkamisPvm()));
        hakueraDTO.setHaunLoppumisPvm(convertDate(entity.getHaunLoppumisPvm()));
        hakueraDTO.setOid(entity.getOid());
        hakueraDTO.setKohdejoukko(entity.getKohdejoukko());
        hakueraDTO.setKoulutuksenAlkaminen(entity.getKoulutuksenAlkaminen());
        hakueraDTO.setHakulomakeUrl(entity.getHakulomakeUrl());
        return hakueraDTO;
    }
    
    private XMLGregorianCalendar convertDate(Date origDate) {
        XMLGregorianCalendar xmlDate = null;
        if (origDate != null) {
            try {
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(origDate);
                xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } catch (Exception ex) {
                
            }
        }
        return xmlDate;
    }

}
