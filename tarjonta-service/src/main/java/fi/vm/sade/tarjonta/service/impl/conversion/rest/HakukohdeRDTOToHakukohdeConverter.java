package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;

/*
* @author: Tuomas Katva 10/1/13
*/
public class HakukohdeRDTOToHakukohdeConverter extends AbstractToDomainConverter<HakukohdeDTO,Hakukohde> {



    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeRDTOToHakukohdeConverter.class);
    @Autowired
    private OIDService oidService;

    @Override
    public Hakukohde convert(HakukohdeDTO hakukohdeDTO) {
        Hakukohde hakukohde = new Hakukohde();
        String newHakukohdeOid = null;
        LOG.info("OIDSERVICE: {}", oidService);
        try {
            newHakukohdeOid = oidService.newOid(NodeClassCode.TEKN_5);
            LOG.info("OID SERVICE NEW OID : {}",newHakukohdeOid);
        }  catch (ExceptionMessage emm) {
            LOG.warn("UNABLE TO GET OID : {}", emm.toString() );
        }


        if (hakukohdeDTO.getOid() != null) {

            hakukohde.setOid(hakukohdeDTO.getOid());
        } else {
            hakukohde.setOid(newHakukohdeOid);

        }

        hakukohde.setAloituspaikatLkm(hakukohdeDTO.getAloituspaikatLkm());
        if (hakukohdeDTO.getHakukohteenNimi() != null) {
            hakukohde.setHakukohdeNimi(hakukohdeDTO.getHakukohteenNimi());
        }  else if (hakukohdeDTO.getHakukohdeNimiUri() != null) {
            hakukohde.setHakukohdeNimi(hakukohdeDTO.getHakukohdeNimiUri());
        }

        hakukohde.setHakukohdeKoodistoNimi(hakukohdeDTO.getHakukohdeKoodistoNimi());
        hakukohde.setHakuaikaAlkuPvm(hakukohdeDTO.getHakuaikaAlkuPvm());
        hakukohde.setHakuaikaLoppuPvm(hakukohdeDTO.getHakuaikaLoppuPvm());
        hakukohde.setTila(TarjontaTila.valueOf(hakukohdeDTO.getTila()));
        hakukohde.setLisatiedot(CommonRestConverters.convertMapToMonikielinenTeksti(hakukohdeDTO.getLisatiedot()));
        hakukohde.setValintojenAloituspaikatLkm(hakukohdeDTO.getValintojenAloituspaikatLkm());
        hakukohde.setLiitteidenToimitusPvm(hakukohdeDTO.getLiitteidenToimitusPvm());
        hakukohde.setSahkoinenToimitusOsoite(hakukohdeDTO.getSahkoinenToimitusOsoite());
        hakukohde.setKaytetaanHaunPaattymisenAikaa(hakukohdeDTO.isKaytetaanHaunPaattymisenAikaa());
        hakukohde.setKaytetaanJarjestelmanValintapalvelua(hakukohdeDTO.isKaytetaanJarjestelmanValintaPalvelua());
        hakukohde.setSoraKuvausKoodiUri(hakukohdeDTO.getSoraKuvausKoodiUri());
        hakukohde.setSoraKuvaus(hakukohdeDTO.getSoraKuvausKoodiUri()!=null ? null :
               CommonRestConverters.convertMapToMonikielinenTeksti(hakukohdeDTO.getSorakuvaus()));

        hakukohde.setValintaperustekuvausKoodiUri(hakukohdeDTO.getValintaperustekuvausKoodiUri());
        hakukohde.setValintaperusteKuvaus(hakukohdeDTO.getValintaperustekuvausKoodiUri() != null  ? null : CommonRestConverters.convertMapToMonikielinenTeksti(hakukohdeDTO.getValintaperustekuvaus()));
        hakukohde.setAlinHyvaksyttavaKeskiarvo(hakukohdeDTO.getAlinHyvaksyttavaKeskiarvo());
        hakukohde.setAlinValintaPistemaara(hakukohdeDTO.getAlinValintaPistemaara());
        hakukohde.setYlinValintaPistemaara(hakukohdeDTO.getYlinValintapistemaara());
        hakukohde.setAloituspaikatLkm(hakukohdeDTO.getAloituspaikatLkm());

        hakukohde.setLiitteidenToimitusOsoite(CommonRestConverters.convertOsoiteRDTOToOsoite(hakukohdeDTO.getLiitteidenToimitusosoite()));

        hakukohde.setHakukelpoisuusVaatimukset(new HashSet<String>(hakukohdeDTO.getHakukelpoisuusvaatimusUris()));

        hakukohde.setLastUpdateDate(new Date());



        return hakukohde;
    }






}
