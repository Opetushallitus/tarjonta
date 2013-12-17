package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Preconditions;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;
import fi.vm.sade.tarjonta.service.resources.v1.KuvausV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import  fi.vm.sade.tarjonta.dao.KuvausDAO;
import org.springframework.beans.factory.annotation.Autowired;

/*
* @author: Tuomas Katva 16/12/13
*/
public class KuvausResourceImplV1 implements KuvausV1Resource {



    @Autowired
    private KuvausDAO kuvausDAO;

    @Autowired
    private ConverterV1 converter;

    @Override
    public ResultV1RDTO<List<String>> findAllKuvauksesByTyyppi(String tyyppi) {
        ResultV1RDTO<List<String>> resultV1RDTO = new ResultV1RDTO<List<String>>();
        try {
        ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi = ConverterV1.getTyyppiFromString(tyyppi);
        Preconditions.checkNotNull(vpsTyyppi);
        List<ValintaperusteSoraKuvaus> valintaperusteSoraKuvauses = kuvausDAO.findByTyyppi(vpsTyyppi);
        if (valintaperusteSoraKuvauses != null && valintaperusteSoraKuvauses.size() > 0) {

            List<String> tunnisteet = new ArrayList<String>();
            for (ValintaperusteSoraKuvaus soraKuvaus : valintaperusteSoraKuvauses) {
                tunnisteet.add(soraKuvaus.getId().toString());
            }

            resultV1RDTO.setResult(tunnisteet);
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

        } else {
           resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        }

        } catch (Exception exp) {

            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp,null,null));

        }
        return resultV1RDTO;
    }

    @Override
    public ResultV1RDTO<List<HashMap<String, String>>> getKuvausNimet(String tyyppi) {
       ResultV1RDTO<List<HashMap<String,String>>>  resultV1RDTO = new ResultV1RDTO<List<HashMap<String, String>>>();

        try {
            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi = ConverterV1.getTyyppiFromString(tyyppi);
            Preconditions.checkNotNull(vpsTyyppi);

            List<ValintaperusteSoraKuvaus> valintaperusteSoraKuvauses = kuvausDAO.findByTyyppi(vpsTyyppi);
            if (valintaperusteSoraKuvauses != null) {

                List<HashMap<String,String>> nimetList = new ArrayList<HashMap<String, String>>();

                    for (ValintaperusteSoraKuvaus valintaperusteSoraKuvaus : valintaperusteSoraKuvauses) {
                        HashMap<String,String> nimet = new HashMap<String, String>();
                        for (TekstiKaannos nimi: valintaperusteSoraKuvaus.getMonikielinenNimi().getTekstis()) {
                            nimet.put(nimi.getKieliKoodi(),nimi.getArvo());
                        }
                        nimetList.add(nimet);
                    }
                resultV1RDTO.setResult(nimetList);
                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            } else {
                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            }


        } catch (Exception exp) {

            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);

        }

        return resultV1RDTO;
    }

    @Override
    public ResultV1RDTO<List<HashMap<String, String>>> getKuvausNimetWithOrganizationType(String tyyppi, String orgType) {
        ResultV1RDTO<List<HashMap<String,String>>>  resultV1RDTO = new ResultV1RDTO<List<HashMap<String, String>>>();

        try {
            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi = ConverterV1.getTyyppiFromString(tyyppi);
            Preconditions.checkNotNull(vpsTyyppi);

            List<ValintaperusteSoraKuvaus> valintaperusteSoraKuvauses = kuvausDAO.findByTyyppiAndOrganizationType(vpsTyyppi,orgType);
            if (valintaperusteSoraKuvauses != null) {

                List<HashMap<String,String>> nimetList = new ArrayList<HashMap<String, String>>();

                for (ValintaperusteSoraKuvaus valintaperusteSoraKuvaus : valintaperusteSoraKuvauses) {
                    HashMap<String,String> nimet = new HashMap<String, String>();
                    for (TekstiKaannos nimi: valintaperusteSoraKuvaus.getMonikielinenNimi().getTekstis()) {
                        nimet.put(nimi.getKieliKoodi(),nimi.getArvo());
                    }
                    nimetList.add(nimet);
                }
                resultV1RDTO.setResult(nimetList);
                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            } else {
                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            }


        } catch (Exception exp) {

            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);

        }

        return resultV1RDTO;
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> findById(String tyyppi,String tunniste) {
      ResultV1RDTO<KuvausV1RDTO> resultV1RDTO = new ResultV1RDTO<KuvausV1RDTO>();
        try {
            Long id = new Long(tunniste);
            ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = kuvausDAO.read(id);
            if (valintaperusteSoraKuvaus != null) {

                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                resultV1RDTO.setResult(converter.toKuvausRDTO(valintaperusteSoraKuvaus));

            } else {
              resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            }
        } catch (Exception exp) {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp,null,null));
        }
       return resultV1RDTO;
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> createNewKuvaus(String tyyppi, KuvausV1RDTO kuvausRDTO) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> updateKuvaus(String tyyppi, KuvausV1RDTO kuvausRDTO) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
