package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.impl.conversion.BaseRDTOConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.CommonToDTOConverterHelper;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
* @author: Tuomas Katva 10/11/13
*/
public class HakukohdeToRDTOConverter extends BaseRDTOConverter<Hakukohde,HakukohdeRDTO> {




    @Override
    public HakukohdeRDTO convert(Hakukohde hakukohde) {
        HakukohdeRDTO hakukohdeRDTO = new HakukohdeRDTO();

        hakukohdeRDTO.setHakukohteenNimi(hakukohde.getHakukohdeNimi());
        hakukohdeRDTO.setVersion(hakukohde.getVersion().intValue());
        hakukohdeRDTO.setOid(hakukohde.getOid());
        hakukohdeRDTO.setAloituspaikatLkm(hakukohde.getAloituspaikatLkm());

        for (KoulutusmoduuliToteutus komoto:hakukohde.getKoulutusmoduuliToteutuses()) {
           hakukohdeRDTO.getHakukohdeKoulutusOids().add(komoto.getOid());

           hakukohdeRDTO.getTarjoajaOids().add(komoto.getTarjoaja());


        }

        for (String hakukelpoisuusVaatimus:hakukohde.getHakukelpoisuusVaatimukset()) {
            hakukohdeRDTO.getHakukelpoisuusvaatimusUris().add(hakukelpoisuusVaatimus);
        }

        hakukohdeRDTO.setHakuOid(hakukohde.getHaku().getOid());
        hakukohdeRDTO.setAlinHyvaksyttavaKeskiarvo(hakukohde.getAlinHyvaksyttavaKeskiarvo());
        hakukohdeRDTO.setAlinValintaPistemaara(hakukohde.getAlinValintaPistemaara());
        hakukohdeRDTO.setValintojenAloituspaikatLkm(hakukohde.getValintojenAloituspaikatLkm());
        hakukohdeRDTO.setYlinValintapistemaara(hakukohde.getYlinValintaPistemaara());

        hakukohdeRDTO.setSahkoinenToimitusOsoite(hakukohde.getSahkoinenToimitusOsoite());
        hakukohdeRDTO.setSoraKuvausKoodiUri(hakukohde.getSoraKuvausKoodiUri());
        hakukohdeRDTO.setTila(hakukohde.getTila().name());
        hakukohdeRDTO.setValintaperustekuvausKoodiUri(hakukohde.getValintaperustekuvausKoodiUri());
        hakukohdeRDTO.setLiitteidenToimitusPvm(hakukohde.getLiitteidenToimitusPvm());
        hakukohdeRDTO.setLisatiedot(CommonToDTOConverterHelper.convertMonikielinenTekstiToTekstiRDOT(hakukohde.getLisatiedot()));
        hakukohdeRDTO.setValintaperusteKuvaukset(CommonToDTOConverterHelper.convertMonikielinenTekstiToTekstiRDOT(hakukohde.getValintaperusteKuvaus()));
        hakukohdeRDTO.setKaytetaanJarjestelmanValintaPalvelua(hakukohde.isKaytetaanJarjestelmanValintapalvelua());
        hakukohdeRDTO.setKaytetaanHaunPaattymisenAikaa(hakukohde.isKaytetaanHaunPaattymisenAikaa());
        hakukohdeRDTO.setLiitteidenToimitusOsoite(CommonToDTOConverterHelper.convertOsoiteToOsoiteDTO(hakukohde.getLiitteidenToimitusOsoite()));

        if (hakukohde.getLiites() != null) {
            hakukohdeRDTO.setLiitteet(convertLiitteet(hakukohde.getLiites()));
        }

        if (hakukohde.getValintakoes() != null) {
            hakukohdeRDTO.setValintakokeet(convertValintakokeet(hakukohde.getValintakoes()));
        }


        return hakukohdeRDTO;
    }


    private List<ValintakoeRDTO> convertValintakokeet(Set<Valintakoe> valintakoes) {
        List<ValintakoeRDTO> result = new ArrayList<ValintakoeRDTO>();

        for (Valintakoe valintakoe : valintakoes) {
            result.add(getConversionService().convert(valintakoe, ValintakoeRDTO.class));
        }

        return result.isEmpty() ? null : result;
    }


    private List<HakukohdeLiiteDTO> convertLiitteet(Set<HakukohdeLiite> s) {
        List<HakukohdeLiiteDTO> result = new ArrayList<HakukohdeLiiteDTO>();

        for (HakukohdeLiite hakukohdeLiite : s) {
            result.add(getConversionService().convert(hakukohdeLiite, HakukohdeLiiteDTO.class));
        }

        return result.isEmpty() ? null : result;
    }
}
