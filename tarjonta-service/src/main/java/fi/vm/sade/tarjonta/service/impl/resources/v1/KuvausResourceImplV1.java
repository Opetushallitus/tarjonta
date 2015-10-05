package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Preconditions;
import fi.vm.sade.auditlog.tarjonta.TarjontaOperation;
import fi.vm.sade.auditlog.tarjonta.TarjontaResource;
import fi.vm.sade.tarjonta.dao.KuvausDAO;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.KuvausV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausSearchV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static fi.vm.sade.tarjonta.service.AuditHelper.*;

/*
* @author: Tuomas Katva 16/12/13
*/
public class KuvausResourceImplV1 implements KuvausV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(KuvausResourceImplV1.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private KuvausDAO kuvausDAO;

    @Autowired
    private ConverterV1 converter;

    @Autowired
    private PermissionChecker permissionChecker;

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
            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));

        }
        return resultV1RDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<List<KuvausV1RDTO>> getKuvaustenTiedot(String tyyppi, String orgType) {
        ResultV1RDTO<List<KuvausV1RDTO>> kuvaukset = new ResultV1RDTO<List<KuvausV1RDTO>>();
        try {

            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi = ConverterV1.getTyyppiFromString(tyyppi);
            List<ValintaperusteSoraKuvaus> kuvaukses = kuvausDAO.findByTyyppiAndOrganizationType(vpsTyyppi, orgType);
            if (kuvaukses != null && kuvaukses.size() > 0) {

                List<KuvausV1RDTO> foundKuvaukses = new ArrayList<KuvausV1RDTO>();
                for (ValintaperusteSoraKuvaus vpkSora : kuvaukses) {
                    foundKuvaukses.add(converter.toKuvausRDTO(vpkSora, false));
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
    public ResultV1RDTO<List<KuvausV1RDTO>> getKuvaustenTiedotVuodella(String tyyppi, int vuosi, String orgType) {

        ResultV1RDTO<List<KuvausV1RDTO>> kuvaukset = new ResultV1RDTO<List<KuvausV1RDTO>>();
        try {

            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi = ConverterV1.getTyyppiFromString(tyyppi);
            List<ValintaperusteSoraKuvaus> kuvaukses = kuvausDAO.findByTyyppiOrgTypeAndYear(vpsTyyppi, orgType, vuosi);
            if (kuvaukses != null && kuvaukses.size() > 0) {

                List<KuvausV1RDTO> foundKuvaukses = new ArrayList<KuvausV1RDTO>();
                for (ValintaperusteSoraKuvaus vpkSora : kuvaukses) {
                    foundKuvaukses.add(converter.toKuvausRDTO(vpkSora, true));
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
        ResultV1RDTO<List<HashMap<String, String>>> resultV1RDTO = new ResultV1RDTO<List<HashMap<String, String>>>();

        try {
            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi = ConverterV1.getTyyppiFromString(tyyppi);
            Preconditions.checkNotNull(vpsTyyppi);

            List<ValintaperusteSoraKuvaus> valintaperusteSoraKuvauses = kuvausDAO.findByTyyppi(vpsTyyppi);
            if (valintaperusteSoraKuvauses != null) {

                List<HashMap<String, String>> nimetList = new ArrayList<HashMap<String, String>>();

                for (ValintaperusteSoraKuvaus valintaperusteSoraKuvaus : valintaperusteSoraKuvauses) {
                    HashMap<String, String> nimet = new HashMap<String, String>();
                    for (TekstiKaannos nimi : valintaperusteSoraKuvaus.getMonikielinenNimi().getTekstiKaannos()) {
                        nimet.put(nimi.getKieliKoodi(), nimi.getArvo());
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
        ResultV1RDTO<List<HashMap<String, String>>> resultV1RDTO = new ResultV1RDTO<List<HashMap<String, String>>>();

        try {
            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi = ConverterV1.getTyyppiFromString(tyyppi);
            Preconditions.checkNotNull(vpsTyyppi);

            List<ValintaperusteSoraKuvaus> valintaperusteSoraKuvauses = kuvausDAO.findByTyyppiAndOrganizationType(vpsTyyppi, orgType);
            if (valintaperusteSoraKuvauses != null) {

                List<HashMap<String, String>> nimetList = new ArrayList<HashMap<String, String>>();

                for (ValintaperusteSoraKuvaus valintaperusteSoraKuvaus : valintaperusteSoraKuvauses) {
                    HashMap<String, String> nimet = new HashMap<String, String>();
                    for (TekstiKaannos nimi : valintaperusteSoraKuvaus.getMonikielinenNimi().getTekstiKaannos()) {
                        nimet.put(nimi.getKieliKoodi(), nimi.getArvo());
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
    public ResultV1RDTO<List<KuvausV1RDTO>> getKuvauksesWithOrganizationType(String tyyppi, String orgType) {
        ResultV1RDTO<List<KuvausV1RDTO>> kuvaukset = new ResultV1RDTO<List<KuvausV1RDTO>>();
        try {

            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi = ConverterV1.getTyyppiFromString(tyyppi);
            List<ValintaperusteSoraKuvaus> kuvaukses = kuvausDAO.findByTyyppiAndOrganizationType(vpsTyyppi, orgType);
            if (kuvaukses != null && kuvaukses.size() > 0) {

                List<KuvausV1RDTO> foundKuvaukses = new ArrayList<KuvausV1RDTO>();
                for (ValintaperusteSoraKuvaus vpkSora : kuvaukses) {
                    foundKuvaukses.add(converter.toKuvausRDTO(vpkSora, true));
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
    public ResultV1RDTO<KuvausV1RDTO> findByNimiAndOppilaitosTyyppi(String tyyppi,
                                                                    String oppilaitosTyyppi,
                                                                    String nimi) {
        ResultV1RDTO<KuvausV1RDTO> result = new ResultV1RDTO<KuvausV1RDTO>();
        try {
            //TODO: query from monikielinen teksti nimi (WHERE NIMI IN MONIKIELINEN TEKSTI)
            List<ValintaperusteSoraKuvaus> valintaperusteSoraKuvauses = kuvausDAO.findByOppilaitosTyyppiTyyppiAndNimi(ConverterV1.getTyyppiFromString(tyyppi), oppilaitosTyyppi, nimi);
            if (valintaperusteSoraKuvauses != null && valintaperusteSoraKuvauses.size() > 0) {
                //TODO: move this logic in to the query
                ValintaperusteSoraKuvaus foundKuvaus = null;

                for (ValintaperusteSoraKuvaus loopValintaSora : valintaperusteSoraKuvauses) {

                    for (TekstiKaannos kuvausNimi : loopValintaSora.getMonikielinenNimi().getKaannoksetAsList()) {

                        if (kuvausNimi.getArvo().trim().equalsIgnoreCase(nimi.trim())) {
                            foundKuvaus = loopValintaSora;
                        }

                    }

                }

                if (foundKuvaus != null) {
                    result.setStatus(ResultV1RDTO.ResultStatus.OK);
                    result.setResult(converter.toKuvausRDTO(foundKuvaus, true));
                } else {
                    result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                }


            } else {
                result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            }

        } catch (Exception exp) {

            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            result.addError(ErrorV1RDTO.createSystemError(exp, null, null));
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
            if (valintaperusteSoraKuvaus != null && !valintaperusteSoraKuvaus.getTila().equals(ValintaperusteSoraKuvaus.Tila.POISTETTU)) {

                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                resultV1RDTO.setResult(converter.toKuvausRDTO(valintaperusteSoraKuvaus, true));

            } else {
                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            }
        } catch (Exception exp) {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));
        }
        return resultV1RDTO;
    }

    @Override
    @Transactional(readOnly = false)
    public ResultV1RDTO<KuvausV1RDTO> createNewKuvaus(String tyyppi, KuvausV1RDTO kuvausRDTO) {
        ResultV1RDTO<KuvausV1RDTO> resultV1RDTO = new ResultV1RDTO<KuvausV1RDTO>();
        try {
            hasPermissionToCreate(kuvausRDTO);

            LOG.debug("USER CAN CREATE KUVAUS.... CREATING");
            ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = converter.toValintaperusteSoraKuvaus(kuvausRDTO);

            validateKuvaus(valintaperusteSoraKuvaus);

            if (!checkForExistingKuvaus(valintaperusteSoraKuvaus)) {

                LOG.debug("NO EXISTING KUVAUS FOUND, CREATING NEW");
                valintaperusteSoraKuvaus.setViimPaivitysPvm(new Date());
                valintaperusteSoraKuvaus.setTila(ValintaperusteSoraKuvaus.Tila.VALMIS);
                valintaperusteSoraKuvaus = kuvausDAO.insert(valintaperusteSoraKuvaus);
                KuvausV1RDTO kuvaus = converter.toKuvausRDTO(valintaperusteSoraKuvaus, true);

                resultV1RDTO.setResult(kuvaus);
                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.CREATE)
                        .setResource(TarjontaResource.VALINTAPERUSTE_SORA_KUVAUS)
                        .setDelta(getKuvausDelta(kuvaus, null))
                        .setResourceOid(kuvaus.getKuvauksenTunniste()).build());

            } else {
                LOG.debug("EXISTING KUVAUS FOUND, REPLYING WITH EXCEPTION");
                resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
                ErrorV1RDTO errorMsg = ErrorV1RDTO.createInfo("valintaperustekuvaus.validation.name.existing.exception");
                resultV1RDTO.addError(errorMsg);

            }


        } catch (Exception exp) {

            LOG.debug("EXCEPTION OCCURRED CREATING NEW KUVAUS : ", exp.toString());

            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));
        }
        return resultV1RDTO;
    }

    private void hasPermissionToCreate(KuvausV1RDTO kuvausRDTO) {
        if (isForKorkeakoulu(kuvausRDTO.getOrganisaatioTyyppi())) {
            permissionChecker.checkCreateValintaPerusteKK();
        } else {
            permissionChecker.checkCreateValintaPeruste();
        }
    }

    private boolean isForKorkeakoulu(String organisaatioTyyppi) {
        return StringUtils.contains(organisaatioTyyppi, "oppilaitostyyppi_41") ||
                StringUtils.contains(organisaatioTyyppi, "oppilaitostyyppi_42") ||
                StringUtils.contains(organisaatioTyyppi, "oppilaitostyyppi_43");
    }

    @Transactional
    private boolean checkForExistingKuvaus(ValintaperusteSoraKuvaus kuvaus) {

        List<ValintaperusteSoraKuvaus> kuvaukset = null;

        boolean retVal = false;

        try {

            kuvaukset = kuvausDAO.findByTyyppiOrgTypeYearKausi(kuvaus.getTyyppi(),
                    kuvaus.getOrganisaatioTyyppi(), kuvaus.getKausi(), kuvaus.getVuosi());

        } catch (Exception exp) {
            retVal = false;
        }


        if (kuvaukset == null || kuvaukset.size() < 1) {
            retVal = false;
        } else {

            for (ValintaperusteSoraKuvaus loopKuvaus : kuvaukset) {

                for (TekstiKaannos tekstiKaannos : loopKuvaus.getMonikielinenNimi().getKaannoksetAsList()) {

                    for (TekstiKaannos toinenTeksti : kuvaus.getMonikielinenNimi().getKaannoksetAsList()) {

                        if (toinenTeksti.getKieliKoodi().trim().equals(tekstiKaannos.getKieliKoodi().trim())
                                && toinenTeksti.getArvo().trim().equals(tekstiKaannos.getArvo().trim())) {
                            retVal = true;
                        }

                    }

                }

            }


        }

        return retVal;

    }

    @Override
    @Transactional(readOnly = false)
    public ResultV1RDTO<KuvausV1RDTO> updateKuvaus(String tyyppi, KuvausV1RDTO kuvausRDTO) {
        ResultV1RDTO<KuvausV1RDTO> resultV1RDTO = new ResultV1RDTO<KuvausV1RDTO>();
        try {
            hasPermissionToUpdate(kuvausRDTO);

            ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = converter.toValintaperusteSoraKuvaus(kuvausRDTO);

            validateKuvaus(valintaperusteSoraKuvaus);

            ValintaperusteSoraKuvaus oldVps = kuvausDAO.read(valintaperusteSoraKuvaus.getId());
            KuvausV1RDTO originalKuvaus = converter.toKuvausRDTO(oldVps, true);

            oldVps.setKausi(valintaperusteSoraKuvaus.getKausi());
            oldVps.setMonikielinenNimi(valintaperusteSoraKuvaus.getMonikielinenNimi());
            oldVps.setOrganisaatioTyyppi(valintaperusteSoraKuvaus.getOrganisaatioTyyppi());
            oldVps.setTyyppi(valintaperusteSoraKuvaus.getTyyppi());
            oldVps.setVuosi(valintaperusteSoraKuvaus.getVuosi());
            oldVps.setTekstis(valintaperusteSoraKuvaus.getTekstis());
            oldVps.setViimPaivittajaOid(valintaperusteSoraKuvaus.getViimPaivittajaOid());
            oldVps.setViimPaivitysPvm(new Date());
            oldVps.setAvain(valintaperusteSoraKuvaus.getAvain());


            kuvausDAO.update(oldVps);

            LOG.debug("UPDATED KUVAUS : ", kuvausRDTO.getOid());

            KuvausV1RDTO modifiedKuvaus = converter.toKuvausRDTO(oldVps, true);

            resultV1RDTO.setResult(modifiedKuvaus);
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

            AUDIT.log(builder()
                    .setOperation(TarjontaOperation.UPDATE)
                    .setResource(TarjontaResource.VALINTAPERUSTE_SORA_KUVAUS)
                    .setDelta(getKuvausDelta(modifiedKuvaus, originalKuvaus))
                    .setResourceOid(Long.toString(oldVps.getId())).build());

        } catch (Exception exp) {

            LOG.debug("EXCEPTION UPDATING KUVAUS: " + kuvausRDTO.getOid() + " : ", exp.toString());

            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));
        }
        return resultV1RDTO;
    }

    private void hasPermissionToUpdate(KuvausV1RDTO kuvausRDTO) {
        if(isForKorkeakoulu(kuvausRDTO.getOrganisaatioTyyppi())) {
            permissionChecker.checkUpdateValintaperustekuvausKK();
        } else {
            permissionChecker.checkUpdateValintaperustekuvaus();
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<KuvausV1RDTO> removeById(String tunniste) {
        ResultV1RDTO<KuvausV1RDTO> resultV1RDTO = new ResultV1RDTO<KuvausV1RDTO>();

        try {
            ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = kuvausDAO.read(Long.parseLong(tunniste));

            hasPermissionToRemove(valintaperusteSoraKuvaus);

            valintaperusteSoraKuvaus.setTila(ValintaperusteSoraKuvaus.Tila.POISTETTU);

            resultV1RDTO.setResult(converter.toKuvausRDTO(valintaperusteSoraKuvaus, true));
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

            AUDIT.log(builder()
                    .setResource(TarjontaResource.VALINTAPERUSTE_SORA_KUVAUS)
                    .setResourceOid(tunniste)
                    .setOperation(TarjontaOperation.DELETE).build());

        } catch (Exception exp) {
            LOG.warn("EXCEPTION REMOVING KUVAUS : " + tunniste + " " + exp.toString());
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            resultV1RDTO.addError(ErrorV1RDTO.createSystemError(exp, null, null));
        }


        return resultV1RDTO;
    }

    private void hasPermissionToRemove(ValintaperusteSoraKuvaus valintaperusteSoraKuvaus) {
        if(isForKorkeakoulu(valintaperusteSoraKuvaus.getOrganisaatioTyyppi())) {
            permissionChecker.checkRemoveValintaPerusteKK();
        } else {
            permissionChecker.checkRemoveValintaPeruste();
        }
    }


    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = true)
    public ResultV1RDTO<List<KuvausV1RDTO>> searchKuvaukses(String tyyppi, KuvausSearchV1RDTO searchParam) {
        ResultV1RDTO<List<KuvausV1RDTO>> result = new ResultV1RDTO<List<KuvausV1RDTO>>();

        try {

            ValintaperusteSoraKuvaus.Tyyppi vpsTyyppi = ConverterV1.getTyyppiFromString(tyyppi);
            List<ValintaperusteSoraKuvaus> resultList = kuvausDAO.findBySearchSpec(searchParam, vpsTyyppi);

            if (resultList != null && resultList.size() > 0) {
                List<KuvausV1RDTO> kuvauksesList = new ArrayList<KuvausV1RDTO>();
                for (ValintaperusteSoraKuvaus vpk : resultList) {
                    kuvauksesList.add(converter.toKuvausRDTO(vpk, false));
                }
                result.setResult(kuvauksesList);
                result.setStatus(ResultV1RDTO.ResultStatus.OK);
            } else {
                result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            }

        } catch (Exception exp) {
            LOG.warn("EXCEPTION RETRIEVING KUVAUKSES : {}", exp.toString());
            result.addError(ErrorV1RDTO.createSystemError(exp, null, null));
            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);

        }

        return result;
    }

    public void validateKuvaus(ValintaperusteSoraKuvaus kuvaus) {
        if ( kuvaus.getMonikielinenNimi() == null && kuvaus.getAvain() == null ) {
            throw new IllegalArgumentException("monikielinenNimi tai avain on pakollinen");
        }

        if (kuvaus.getAvain() != null) {
            // varmista, että virkailija ei tallenna kahteen kertaan samaa kausi/vuosi/2.astekuvaus comboa
            Query q = em.createNativeQuery("select id from valintaperuste_sora_kuvaus where " +
                    "avain = :avain AND vuosi = :vuosi AND tyyppi = :tyyppi AND kausi = :kausi AND id != :id");
            q.setParameter("avain", kuvaus.getAvain());
            q.setParameter("vuosi", kuvaus.getVuosi());
            q.setParameter("tyyppi", kuvaus.getTyyppi().ordinal());
            q.setParameter("kausi", kuvaus.getKausi());
            Long id = kuvaus.getId();
            if (id == null) {
                id = new Long(0);
            }
            q.setParameter("id", id);

            List result = q.getResultList();
            if (result.size() > 0) {
                throw new IllegalArgumentException("KUVAUS_ON_OLEMASSA_JO");
            }
        }
    }

}
