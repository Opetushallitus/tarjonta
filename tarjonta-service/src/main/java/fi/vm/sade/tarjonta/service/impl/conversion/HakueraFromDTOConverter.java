package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.dao.HakueraDAO;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

public class HakueraFromDTOConverter extends
        AbstractToDomainConverter<HakueraDTO, Hakuera> {

    @Autowired
    private HakueraDAO dao;

    @Override
    public Hakuera convert(HakueraDTO hakueraDTO) {
        Hakuera hakuera = null;
        if (hakueraDTO.getOid() != null) {
            hakuera = dao.findByOid(hakueraDTO.getOid());
        }
        if (hakuera == null) {
            hakuera = new Hakuera();
        }
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
