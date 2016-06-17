package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * REST API impl.
 *
 * @author mlyly
 * @see HakukohdeResource
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class HakukohdeResourceImpl implements HakukohdeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImpl.class);

    @Autowired
    private HakukohdeDAO hakukohdeDAO;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired
    private OrganisaatioService organisaatioService;

    // /hakukohde?...
    @Override
    public List<OidRDTO> search(String searchTerms,
            int count,
            int startIndex,
            Date lastModifiedBefore,
            Date lastModifiedSince,
            List<String> organisationOids,
            List<String> hakukohdeTilas) {

        organisationOids = organisationOids != null ? organisationOids : new ArrayList<String>();
        hakukohdeTilas = hakukohdeTilas != null ? hakukohdeTilas : new ArrayList<String>();

        LOG.debug("/hakukohde -- search({}, {}, {}, {}, {}, {}, {})",
                new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince, organisationOids, hakukohdeTilas});

        TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

        if (count < 0) {
            LOG.debug("  negative parameter for count, using Integer.MAX_VALUE");
            count = Integer.MAX_VALUE;
        } else if (count == 0) {
            count = 100;
            LOG.debug("  count not specified, autolimit search to {} entries!", count);
        }

        List<OidRDTO> result = HakuResourceImpl.convertOidList(hakukohdeDAO.findOIDsBy(tarjontaTila != null ? tarjontaTila.asDto() : null, count, startIndex, lastModifiedBefore, lastModifiedSince,false));
        LOG.debug("  result count ={}", result.size());
        return result;
    }

    // /hakukohde/OID
    @Override
    public HakukohdeDTO getByOID(String oid) {
        LOG.debug("/hakukohde/{} -- getByOID()", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakukohdeDTO result = conversionService.convert(hakukohde, HakukohdeDTO.class);
        LOG.debug("  result={}", result);
        return result;
    }

    // /hakukohde/OID/haku
    @Override
    public HakuDTO getHakuByHakukohdeOID(String oid) {
        LOG.debug("/hakukohde/{}/haku -- getHakuByHakukohdeOID()", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakuDTO result = conversionService.convert(hakukohde.getHaku(), HakuDTO.class);
        LOG.debug("  result={}", result);
        return result;
    }

    // /hakukohde/OID/komoto
    @Override
    public List<OidRDTO> getKomotosByHakukohdeOID(String oid) {
        LOG.debug("/hakukohde/{}/komoto -- getKomotosByHakukohdeOID()", oid);

        List<OidRDTO> result = new ArrayList<OidRDTO>();

        // TODO fixme to be more efficient! Add a custom finder for this
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            result.add(new OidRDTO(koulutusmoduuliToteutus.getOid()));
        }
        LOG.debug("  result={}", result);
        return result;
    }

    // GET /hakukohde/{oid}/paasykoe
    @Override
    public List<String> getPaasykoesByHakukohdeOID(String oid) {
        LOG.debug("/hakukohde/{}/paasykoe -- getPaasykoesByHakukohdeOID()", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // GET /hakukohde/{oid}/valintakoe
    @Override
    public List<ValintakoeRDTO> getValintakoesByHakukohdeOID(String oid) {
        LOG.debug("/hakukohde/{}/valintakoe -- getValintakoesByHakukohdeOID()", oid);
        return getValintakoeFixedByHakukohdeOID(oid);
    }

    // /hakukohde/OID/valintaperusteet
    @Override
    public HakukohdeValintaperusteetDTO getHakukohdeValintaperusteet(String oid) {
        LOG.debug("getHakukohdeValintaperusteet({})", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakukohdeValintaperusteetDTO result = conversionService.convert(hakukohde, HakukohdeValintaperusteetDTO.class);
        LOG.debug("  result={}", result);
        return result;
    }

    // GET /hakukohde/{oid}/nimi
    @Override
    public HakukohdeNimiRDTO getHakukohdeNimi(String oid) {
        LOG.debug("getHakukohdeNimi({})", oid);

        HakukohdeNimiRDTO result = new HakukohdeNimiRDTO();

        // TODO fixme to be more efficient! Add a custom finder for this
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        if (hakukohde == null) {
            return result;
        }

        //
        // Get hakukohde name
        //
        // Get multilingual name for koodisto "hakukohde" (application option?)
        result.setHakukohdeNimi(tarjontaKoodistoHelper.getKoodiMetadataNimi(hakukohde.getHakukohdeNimi()));
        result.setHakukohdeOid(oid);
        result.setHakukohdeTila(hakukohde.getTila() != null ? hakukohde.getTila().name() : null);

        //
        // Haku/koulutus year and term
        //
        Haku haku = hakukohde.getHaku();
        if (haku != null) {
            result.setHakuVuosi(haku.getHakukausiVuosi());
            result.setHakuKausi(tarjontaKoodistoHelper.getKoodiMetadataNimi(haku.getHakukausiUri()));
        } else {
            result.setHakuVuosi(-1);
            result.setHakuKausi(null);
        }

        //
        // Get organisaatio name
        //
        String organisaatioOid = null;

        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            if (koulutusmoduuliToteutus.getTarjoaja() != null) {
                // Assumes that only one provider for koulutus - is this true?
                organisaatioOid = koulutusmoduuliToteutus.getTarjoaja();

                // Also assume that kausi and vuosi is the same also
                result.setKoulutusKausi(tarjontaKoodistoHelper.getKoodiMetadataNimi(resolveDateToKausiUri(koulutusmoduuliToteutus.getKoulutuksenAlkamisPvm())));
                result.setKoulutusVuosi(resolveDateToYear(koulutusmoduuliToteutus.getKoulutuksenAlkamisPvm()));

                // assume it is the same for all komotos
                break;
            }
        }

        if (organisaatioOid != null) {
            result.setTarjoajaOid(organisaatioOid);
            result.setTarjoajaNimi(organisaatioService.getTarjoajaNimiMap(organisaatioOid));
        }

        LOG.debug("  --> result = {}", result);

        return result;
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // KJOH-669
    private List<ValintakoeRDTO> getValintakoeFixedByHakukohdeOID(String oid) {
        LOG.debug("getValintakoeFixedByHakukohdeOID({})", oid);

        List<ValintakoeRDTO> result = new ArrayList<ValintakoeRDTO>();

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        if (hakukohde == null) {
            LOG.info("  Hakukohde with oid {} was not found.");
            return result;
        }

        Set<Valintakoe> valintakoes = hakukohde.getValintakoes();
        for (Valintakoe valintakoe : valintakoes) {
            LOG.debug("  process: vk.id = {}", valintakoe.getId());

            ValintakoeRDTO tmp = conversionService.convert(valintakoe, ValintakoeRDTO.class);

            // This is now applied to ALL valintakoes, not only lukios... lets see how this goes
            if (true || isEmpty(valintakoe.getTyyppiUri())) {
                LOG.debug("  EMPTY getTyyppiUri - ie. Lukio valintakoe, {}", tmp);

                //
                // TODO / NOTE / ALERT: in Lukio the valintakoes were mistakenly compressed to
                // single valintakoe, should have one to two instances...
                // Reeeally ugly, hardcoded fix here... sorry, sorry sorry.
                //
                // TODO Fix UI to create correct valintakoe models to backend/db and do some flyway magic later!
                //
                ValintakoeRDTO vk = null;
                ValintakoeRDTO lt = null;

                //
                // Points
                //
                List<ValintakoePisterajaRDTO> addToBothVKs = new ArrayList<ValintakoePisterajaRDTO>();

                for (ValintakoePisterajaRDTO pisteraja : tmp.getValintakoePisterajas()) {
                    LOG.debug("  vk - pisteraja: '{}'", pisteraja.getTyyppi());

                    // TODO hardocded... :(
                    if ("Paasykoe".equals(pisteraja.getTyyppi())) {
                        LOG.debug("    pisteraja: pääsykoe");
                        vk = (vk == null) ? new ValintakoeRDTO() : vk;
                        if (vk.getValintakoePisterajas() == null) {
                            vk.setValintakoePisterajas(new ArrayList<ValintakoePisterajaRDTO>());
                        }
                        vk.getValintakoePisterajas().add(pisteraja);
                    } else if ("Lisapisteet".equals(pisteraja.getTyyppi())) {
                        LOG.debug("    pisteraja: lisäpisteet");
                        lt = (lt == null) ? new ValintakoeRDTO() : lt;
                        if (lt.getValintakoePisterajas() == null) {
                            lt.setValintakoePisterajas(new ArrayList<ValintakoePisterajaRDTO>());
                        }
                        lt.getValintakoePisterajas().add(pisteraja);
                    } else {
                        // Anything else, add to both ("Kokonaispisteet")
                        LOG.debug("    pisteraja: default case, add pisteraja to both");
                        addToBothVKs.add(pisteraja);
                    }
                }

                //
                // Valintakoe / selection examination
                //
                if (vk != null) {
                    LOG.debug("  vk != null, update data");
                    vk.setCreated(tmp.getCreated());
                    vk.setCreatedBy(tmp.getCreatedBy());
                    vk.setKuvaus(tmp.getKuvaus());
                    vk.setModified(tmp.getModified());
                    vk.setModifiedBy(tmp.getModifiedBy());
                    vk.setOid(tmp.getOid() + "_1");
                    vk.setTyyppiUri("valintakokeentyyppi_1#1"); // TODO hardcoded
                    vk.setValintakoeAjankohtas(tmp.getValintakoeAjankohtas());
                    vk.setValintakoeId(tmp.getValintakoeId());
                    vk.setVersion(tmp.getVersion());

                    // Add "common" points if any
                    vk.getValintakoePisterajas().addAll(addToBothVKs);

                    result.add(vk);
                }

                //
                // Lisänäytöt / additional test?
                //
                if (lt != null) {
                    LOG.debug("  lt (lisänäytöt) != null, update data");
                    lt.setCreated(tmp.getCreated());
                    lt.setCreatedBy(tmp.getCreatedBy());
                    lt.setKuvaus(tmp.getLisanaytot());
                    lt.setModified(tmp.getModified());
                    lt.setModifiedBy(tmp.getModifiedBy());
                    lt.setOid(tmp.getOid() + "_2");
                    lt.setTyyppiUri("valintakokeentyyppi_2#1"); // TODO hardcoded
                    lt.setVersion(tmp.getVersion());

                    // Add "common" points if any
                    lt.getValintakoePisterajas().addAll(addToBothVKs);

                    result.add(lt);
                }

                // Jos valintakokeella ei ole pisterajoja
                if(lt == null && vk == null) {
                    result.add(tmp);
                }
            } else {
                // Normal and the default case. NOT REACHED at the moment
                LOG.debug("  Normal case, vk has a type {}", tmp.getTyyppiUri());
                result.add(tmp);
            }
        }

        return result;
    }

    private String resolveDateToKausiUri(final Date d) {
        if (d == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        // FIXME hardcoded kausi uris
        // TODO check logic for kausi uris, hardcoded!
        if (cal.get(Calendar.MONTH) < 6) {
            return "kausi_k";
        } else {
            return "kausi_s";
        }
    }

    private int resolveDateToYear(final Date d) {
        if (d == null) {
            return -1;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }

}
