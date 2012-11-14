package fi.vm.sade.tarjonta.service.impl.conversion;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.HakueraTyyppi;

public class HakueraToDTOConverter extends AbstractFromDomainConverter<Haku, HakueraTyyppi> {

    @Override
    public HakueraTyyppi convert(
        Haku entity) {
        HakueraTyyppi hakueraDTO = new HakueraTyyppi();
        hakueraDTO.setNimiFi(entity.getNimiFi());
        hakueraDTO.setNimiSv(entity.getNimiSv());
        hakueraDTO.setNimiEn(entity.getNimiEn());
        hakueraDTO.setHakutyyppi(entity.getHakutyyppiUri());
        hakueraDTO.setHakukausi(entity.getHakukausiUri());
        hakueraDTO.setHakutapa(entity.getHakutapaUri());
        hakueraDTO.setOid(entity.getOid());
        hakueraDTO.setKohdejoukko(entity.getKohdejoukkoUri());
        hakueraDTO.setKoulutuksenAlkaminen(entity.getKoulutuksenAlkamiskausiUri());
        hakueraDTO.setHakulomakeUrl(entity.getHakulomakeUrl());
        hakueraDTO.setTila(EntityUtils.convertTila(entity.getTila()));
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

