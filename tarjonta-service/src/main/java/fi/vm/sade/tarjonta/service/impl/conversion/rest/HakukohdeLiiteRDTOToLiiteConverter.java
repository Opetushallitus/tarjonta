package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteDTO;
import org.springframework.core.convert.converter.Converter;

/*
* @author: Tuomas Katva 10/3/13
*/
public class HakukohdeLiiteRDTOToLiiteConverter implements Converter<HakukohdeLiiteDTO,HakukohdeLiite> {

    @Override
    public HakukohdeLiite convert(HakukohdeLiiteDTO hakukohdeLiiteDTO) {

        HakukohdeLiite hakukohdeLiite = new HakukohdeLiite();

        hakukohdeLiite.setErapaiva(hakukohdeLiiteDTO.getErapaiva());
        hakukohdeLiite.setKuvaus(CommonRestConverters.toMonikielinenTeksti(hakukohdeLiiteDTO.getKuvaus()));
        hakukohdeLiite.setLiitetyyppi(hakukohdeLiiteDTO.getLiitteenTyyppiUri());
        hakukohdeLiite.setToimitusosoite(CommonRestConverters.toOsoite(hakukohdeLiiteDTO.getToimitusosoite()));
        hakukohdeLiite.setSahkoinenToimitusosoite(hakukohdeLiiteDTO.getSahkoinenToimitusosoite());
        hakukohdeLiite.setLastUpdateDate(hakukohdeLiiteDTO.getModified());
        hakukohdeLiite.setLastUpdatedByOid(hakukohdeLiiteDTO.getCreatedBy());
        hakukohdeLiite.setLiitteenTyyppiKoodistoNimi(hakukohdeLiiteDTO.getLiitteenTyyppiKoodistonNimi());

        return hakukohdeLiite;

    }
}
