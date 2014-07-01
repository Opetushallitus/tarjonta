package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.model.ValintakoeAjankohta;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.impl.conversion.BaseRDTOConverter;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/*
* @author: Tuomas Katva 10/14/13
*/
public class RDTOTOHakukohdeConverter extends BaseRDTOConverter<HakukohdeV1RDTO,Hakukohde> {


    private static final Logger LOG = LoggerFactory.getLogger(RDTOTOHakukohdeConverter.class);
    @Autowired
    private OidService oidService;
    @Override
    public Hakukohde convert(HakukohdeV1RDTO hakukohdeRDTO) {

        Hakukohde hakukohde = new Hakukohde();
        String newHakukohdeOid = null;
        LOG.debug("OIDSERVICE: {}", oidService);
        try {
            newHakukohdeOid = oidService.get(TarjontaOidType.HAKUKOHDE);
            LOG.debug("OID SERVICE NEW OID : {}", newHakukohdeOid);
        }  catch (OIDCreationException emm) {
            LOG.warn("UNABLE TO GET OID : {}", emm.toString() );
        }

        if (hakukohdeRDTO.getOid() != null && hakukohdeRDTO.getOid().trim().length() > 0) {
            hakukohde.setOid(hakukohdeRDTO.getOid());
        } else {
            LOG.debug("NO OID FOUND ADDING NEW ONE : {}", newHakukohdeOid);
            hakukohde.setOid(newHakukohdeOid);
        }
        hakukohde.setAloituspaikatLkm(hakukohdeRDTO.getAloituspaikatLkm());
        hakukohde.setHakuaikaAlkuPvm(hakukohdeRDTO.getHakuaikaLoppuPvm());
        if (hakukohdeRDTO.getHakukohteenNimet() != null && hakukohdeRDTO.getHakukohteenNimet().size() > 0) {
           //hakukohde.setHakukohdeMonikielinenNimi(convertTekstiRDTOToMonikielinenTeksti(hakukohdeRDTO.getHakukohteenNimet()));
            hakukohde.setHakukohdeMonikielinenNimi(convertMapToMonikielinenTeksti(hakukohdeRDTO.getHakukohteenNimet()));
        }
        if (hakukohdeRDTO.getHakukohteenNimiUri() != null) {
            hakukohde.setHakukohdeNimi(hakukohdeRDTO.getHakukohteenNimiUri());
        }
        hakukohde.setTila(TarjontaTila.valueOf(hakukohdeRDTO.getTila()));
        hakukohde.setLiitteidenToimitusPvm(hakukohdeRDTO.getLiitteidenToimitusPvm());
        hakukohde.setValintojenAloituspaikatLkm(hakukohdeRDTO.getValintojenAloituspaikatLkm());
        hakukohde.setSahkoinenToimitusOsoite(hakukohdeRDTO.getSahkoinenToimitusOsoite());
        hakukohde.setKaytetaanJarjestelmanValintapalvelua(hakukohdeRDTO.isKaytetaanJarjestelmanValintaPalvelua());
        hakukohde.setKaytetaanHaunPaattymisenAikaa(hakukohdeRDTO.isKaytetaanHaunPaattymisenAikaa());
        hakukohde.setSoraKuvausKoodiUri(hakukohdeRDTO.getSoraKuvausKoodiUri());
        hakukohde.setValintaperustekuvausKoodiUri(hakukohdeRDTO.getValintaperustekuvausKoodiUri());
        if (hakukohdeRDTO.getValintaperusteKuvaukset() != null) {
        hakukohde.setValintaperusteKuvaus(convertMapToMonikielinenTeksti(hakukohdeRDTO.getValintaperusteKuvaukset()));
        }
        if (hakukohdeRDTO.getSoraKuvaukset() != null) {
        hakukohde.setSoraKuvaus(convertMapToMonikielinenTeksti(hakukohdeRDTO.getSoraKuvaukset()));
        }
        hakukohde.setAlinHyvaksyttavaKeskiarvo(hakukohdeRDTO.getAlinHyvaksyttavaKeskiarvo());
        hakukohde.setAlinValintaPistemaara(hakukohdeRDTO.getAlinValintaPistemaara());
        hakukohde.setYlinValintaPistemaara(hakukohdeRDTO.getYlinValintapistemaara());

        if (hakukohdeRDTO.getLisatiedot() != null){
            hakukohde.setLisatiedot(convertMapToMonikielinenTeksti(hakukohdeRDTO.getLisatiedot()));
        }

        if (hakukohdeRDTO.getHakukelpoisuusvaatimusUris() != null) {
            for (String hakukelpoisuusVaatimus : hakukohdeRDTO.getHakukelpoisuusvaatimusUris()) {
                hakukohde.getHakukelpoisuusVaatimukset().add(hakukelpoisuusVaatimus);
            }
        }

        hakukohde.setLiitteidenToimitusOsoite(CommonRestConverters.convertOsoiteRDTOToOsoite(hakukohdeRDTO.getLiitteidenToimitusOsoite()));


        //TODO: add liite converter

        hakukohde.getValintakoes().addAll(convertValintakoeRDTOToValintakoe(hakukohdeRDTO.getValintakokeet()));

        // KJOH-810
        hakukohde.setOrganisaatioRyhmaOids(hakukohdeRDTO.getOrganisaatioRyhmaOids());
        
        return hakukohde;
    }

    private List<Valintakoe> convertValintakoeRDTOToValintakoe(List<ValintakoeV1RDTO> valintakoeV1RDTOs) {
        List<Valintakoe> valintakoes = new ArrayList<Valintakoe>();

        for (ValintakoeV1RDTO valintakoeV1RDTO : valintakoeV1RDTOs) {
            valintakoes.add(convertValintakoeRDTOToValintakoe(valintakoeV1RDTO));
        }

        return valintakoes;
    }

    private Valintakoe convertValintakoeRDTOToValintakoe(ValintakoeV1RDTO valintakoeV1RDTO) {
        Valintakoe valintakoe = new Valintakoe();

        if (valintakoeV1RDTO.getOid() != null) {
            try {
              valintakoe.setId(new Long(valintakoeV1RDTO.getOid()));
            } catch (Exception exp) {

            }
            valintakoe.setValintakoeNimi(valintakoeV1RDTO.getValintakoeNimi());
            valintakoe.setKieli(valintakoeV1RDTO.getKieliUri());
            List<TekstiRDTO> tekstiRDTOs = new ArrayList<TekstiRDTO>();
            tekstiRDTOs.add(valintakoeV1RDTO.getValintakokeenKuvaus());
            valintakoe.setKuvaus(convertTekstiRDTOToMonikielinenTeksti(tekstiRDTOs));
            if (valintakoeV1RDTO.getValintakoeAjankohtas() != null) {
                valintakoe.getAjankohtas().addAll(convertAjankohtaRDTOToValintakoeAjankohta(valintakoeV1RDTO.getValintakoeAjankohtas()));
            }

        }

        return valintakoe;
    }


    private Set<ValintakoeAjankohta> convertAjankohtaRDTOToValintakoeAjankohta(List<ValintakoeAjankohtaRDTO> valintakoeAjankohtaRDTOs) {
        Set<ValintakoeAjankohta> valintakoeAjankohtas = new HashSet<ValintakoeAjankohta>();

        for (ValintakoeAjankohtaRDTO valintakoeAjankohtaRDTO:valintakoeAjankohtaRDTOs) {
            ValintakoeAjankohta valintakoeAjankohta = new ValintakoeAjankohta();

            valintakoeAjankohta.setLisatietoja(valintakoeAjankohtaRDTO.getLisatiedot());
            valintakoeAjankohta.setAjankohdanOsoite(CommonRestConverters.convertOsoiteRDTOToOsoite(valintakoeAjankohtaRDTO.getOsoite()));
            valintakoeAjankohta.setAlkamisaika(valintakoeAjankohtaRDTO.getAlkaa());
            valintakoeAjankohta.setPaattymisaika(valintakoeAjankohtaRDTO.getLoppuu());
            valintakoeAjankohtas.add(valintakoeAjankohta);

        }

        return valintakoeAjankohtas;
    }

    private MonikielinenTeksti convertMapToMonikielinenTeksti(Map<String,String> tekstis) {

        MonikielinenTeksti teksti = new MonikielinenTeksti();
        for(String key : tekstis.keySet()) {
            teksti.addTekstiKaannos(key,tekstis.get(key));
        }

        return teksti;
    }

    private MonikielinenTeksti convertTekstiRDTOToMonikielinenTeksti(List<TekstiRDTO> tekstis) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

        for (TekstiRDTO tekstiRDTO:tekstis){
            monikielinenTeksti.addTekstiKaannos(tekstiRDTO.getUri(),tekstiRDTO.getTeksti());
            LOG.debug("MONIKIELINEN TEKSTI : {}", tekstiRDTO.getTeksti());
        }

        return monikielinenTeksti;
    }
}
