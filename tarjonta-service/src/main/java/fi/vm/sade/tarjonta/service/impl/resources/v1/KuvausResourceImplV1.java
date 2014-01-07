package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Preconditions;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;
import fi.vm.sade.tarjonta.service.resources.v1.KuvausV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausSearchV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import  fi.vm.sade.tarjonta.dao.KuvausDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/*
* @author: Tuomas Katva 16/12/13
*/
public class KuvausResourceImplV1 implements KuvausV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(KuvausResourceImplV1.class);

    @Autowired
    private KuvausDAO kuvausDAO;

    @Autowired
    private ConverterV1 converter;

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<List<String>> findAllKuvauksesByTyyppi() {
        ResultV1RDTO<List<String>> resultV1RDTO = new ResultV1RDTO<List<String>>();
        try {

        List<ValintaperusteSoraKuvaus> valintaperusteSoraKuvauses = kuvausDAO.findAll();
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
    public ResultV1RDTO<List<KuvausV1RDTO>> getKuvaustenTiedot(String tyyppi, String orgType) {
        ResultV1RDTO<List<KuvausV1RDTO>> kuvaukset = new ResultV1RDTO<List<KuvausV1RDTO>>();
        try {

            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi  = ConverterV1.getTyyppiFromString(tyyppi);
            List<ValintaperusteSoraKuvaus> kuvaukses = kuvausDAO.findByTyyppiAndOrganizationType(vpsTyyppi,orgType);
            if (kuvaukses != null && kuvaukses.size() > 0) {

                List<KuvausV1RDTO> foundKuvaukses = new ArrayList<KuvausV1RDTO>();
                for (ValintaperusteSoraKuvaus vpkSora : kuvaukses) {
                    foundKuvaukses.add(converter.toKuvausRDTO(vpkSora,false));
                }

                kuvaukset.setResult(foundKuvaukses);
                kuvaukset.setStatus(ResultV1RDTO.ResultStatus.OK);

            } else {
                kuvaukset.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            }


        } catch (Exception exp) {

            kuvaukset.addError(ErrorV1RDTO.createSystemError(exp, null, null));
            kuvaukset.setStatus(ResultV1RDTO.ResultStatus.ERROR);

        }

        return kuvaukset;
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    public ResultV1RDTO<List<KuvausV1RDTO>> getKuvauksesWithOrganizationType(String tyyppi, String orgType) {
        ResultV1RDTO<List<KuvausV1RDTO>> kuvaukset = new ResultV1RDTO<List<KuvausV1RDTO>>();
        try {

            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi  = ConverterV1.getTyyppiFromString(tyyppi);
            List<ValintaperusteSoraKuvaus> kuvaukses = kuvausDAO.findByTyyppiAndOrganizationType(vpsTyyppi,orgType);
            if (kuvaukses != null && kuvaukses.size() > 0) {

                List<KuvausV1RDTO> foundKuvaukses = new ArrayList<KuvausV1RDTO>();
                for (ValintaperusteSoraKuvaus vpkSora : kuvaukses) {
                    foundKuvaukses.add(converter.toKuvausRDTO(vpkSora,true));
                }

                kuvaukset.setResult(foundKuvaukses);
                kuvaukset.setStatus(ResultV1RDTO.ResultStatus.OK);

            } else {
                kuvaukset.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            }


        } catch (Exception exp) {

            kuvaukset.addError(ErrorV1RDTO.createSystemError(exp, null, null));
            kuvaukset.setStatus(ResultV1RDTO.ResultStatus.ERROR);

        }

        return kuvaukset;
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> findByNimiAndOppilaitosTyyppi(String tyyppi,
                                                                    String oppilaitosTyyppi,
                                                                    String nimi) {
        ResultV1RDTO<KuvausV1RDTO> result = new ResultV1RDTO<KuvausV1RDTO>();
        try {
        //TODO: query from monikielinen teksti nimi (WHERE NIMI IN MONIKIELINEN TEKSTI)
        List<ValintaperusteSoraKuvaus> valintaperusteSoraKuvauses = kuvausDAO.findByOppilaitosTyyppiTyyppiAndNimi(ConverterV1.getTyyppiFromString(tyyppi),oppilaitosTyyppi,nimi);
        if (valintaperusteSoraKuvauses != null && valintaperusteSoraKuvauses.size() > 0) {
           //TODO: move this logic in to the query
           ValintaperusteSoraKuvaus foundKuvaus = null;

            for (ValintaperusteSoraKuvaus loopValintaSora:valintaperusteSoraKuvauses)   {

                for (TekstiKaannos kuvausNimi : loopValintaSora.getMonikielinenNimi().getKaannoksetAsList()) {

                    if (kuvausNimi.getArvo().trim().equalsIgnoreCase(nimi.trim())) {
                        foundKuvaus = loopValintaSora;
                    }

                }

            }

            if (foundKuvaus != null) {
                result.setStatus(ResultV1RDTO.ResultStatus.OK);
                result.setResult(converter.toKuvausRDTO(foundKuvaus,true));
            } else {
                result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            }


        } else {
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        }

        } catch (Exception exp ){

            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            result.addError(ErrorV1RDTO.createSystemError(exp,null,null));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<KuvausV1RDTO> findById(String tunniste) {
      ResultV1RDTO<KuvausV1RDTO> resultV1RDTO = new ResultV1RDTO<KuvausV1RDTO>();
        try {
            Long id = new Long(tunniste);
            ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = kuvausDAO.read(id);
            if (valintaperusteSoraKuvaus != null) {

                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                resultV1RDTO.setResult(converter.toKuvausRDTO(valintaperusteSoraKuvaus,true));

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
    @Transactional (readOnly = false)
    public ResultV1RDTO<KuvausV1RDTO> createNewKuvaus(String tyyppi, KuvausV1RDTO kuvausRDTO) {
        ResultV1RDTO<KuvausV1RDTO> resultV1RDTO = new ResultV1RDTO<KuvausV1RDTO>();
        try {
            LOG.debug("CREATING NEW KUVAUS ");
            ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = converter.toValintaperusteSoraKuvaus(kuvausRDTO);
            valintaperusteSoraKuvaus = kuvausDAO.insert(valintaperusteSoraKuvaus);
            KuvausV1RDTO kuvaus = converter.toKuvausRDTO(valintaperusteSoraKuvaus,true);

            resultV1RDTO.setResult(kuvaus);
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

        } catch (Exception exp) {
           resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
           resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));
        }
        return  resultV1RDTO;
    }

    @Override
    @Transactional (readOnly = false)
    public ResultV1RDTO<KuvausV1RDTO> updateKuvaus(String tyyppi, KuvausV1RDTO kuvausRDTO) {
        ResultV1RDTO<KuvausV1RDTO> resultV1RDTO = new ResultV1RDTO<KuvausV1RDTO>();
        try {
            ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = converter.toValintaperusteSoraKuvaus(kuvausRDTO);

            ValintaperusteSoraKuvaus oldVps = kuvausDAO.read(valintaperusteSoraKuvaus.getId());

            oldVps.setKausi(valintaperusteSoraKuvaus.getKausi());
            oldVps.setMonikielinenNimi(valintaperusteSoraKuvaus.getMonikielinenNimi());
            oldVps.setOrganisaatioTyyppi(valintaperusteSoraKuvaus.getOrganisaatioTyyppi());
            oldVps.setTyyppi(valintaperusteSoraKuvaus.getTyyppi());
            oldVps.setVuosi(valintaperusteSoraKuvaus.getVuosi());
            oldVps.setTekstis(valintaperusteSoraKuvaus.getTekstis());

            kuvausDAO.update(oldVps);


            resultV1RDTO.setResult(kuvausRDTO);
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

        } catch (Exception exp) {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));
        }
        return  resultV1RDTO;
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> removeById(String tunniste) {
        ResultV1RDTO<KuvausV1RDTO> resultV1RDTO = new ResultV1RDTO<KuvausV1RDTO>();

        try {

            ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = kuvausDAO.read(Long.getLong(tunniste));

            kuvausDAO.remove(valintaperusteSoraKuvaus);

            resultV1RDTO.setResult(converter.toKuvausRDTO(valintaperusteSoraKuvaus,true));
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

        } catch (Exception exp ){
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));
        }


        return  resultV1RDTO;
    }

    @Override
    public ResultV1RDTO<List<KuvausV1RDTO>> searchKuvaukses(KuvausSearchV1RDTO searchParam) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
