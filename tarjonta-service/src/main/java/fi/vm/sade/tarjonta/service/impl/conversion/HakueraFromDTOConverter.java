package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.HakueraTyyppi;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

public class HakueraFromDTOConverter extends
        AbstractToDomainConverter<HakueraTyyppi, Haku> {

    @Autowired
    private HakuDAO dao;

    @Override
    public Haku convert(HakueraTyyppi source) {
        Haku target = null;
        if (source.getOid() != null) {
            target = dao.findByOid(source.getOid());
        }
        if (target == null) {
            target = new Haku();
        }
        target.setNimiFi(source.getNimiFi());
        target.setNimiSv(source.getNimiSv());
        target.setNimiEn(source.getNimiEn());
        target.setHakutyyppiUri(source.getHakutyyppi());
        target.setHakukausiUri(source.getHakukausi());
        target.setHakutapaUri(source.getHakutapa());
        target.setOid(source.getOid());
        target.setKohdejoukkoUri(source.getKohdejoukko());
        target.setKoulutuksenAlkamiskausiUri(source.getKoulutuksenAlkaminen());
        target.setHakulomakeUrl(source.getHakulomakeUrl());
        target.setTila(EntityUtils.convertTila(source.getTila()));
        return target;
    }

    private Date convertDate(XMLGregorianCalendar cal) {
        Date convertedDate = null;
        if (cal != null) {
            convertedDate = cal.toGregorianCalendar().getTime();
        }
        return convertedDate;
    }

}
