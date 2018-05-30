package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTO;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusResourceImplV1;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.OrganisaatioUtil;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.shared.organisaatio.OrganisaatioKelaDTO;
import fi.vm.sade.tarjonta.shared.organisaatio.OrganisaatioResultDTO;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

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
    @Autowired
    private EntityConverterToRDTO converterToRDTO;
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

    @Override
    public HakukohdeKelaDTO getHakukohdeKelaByOID(String oid) {
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(oid);
        return hakukohde == null ? null : hakukohdeToHakukohdeKelaDTO(hakukohde);
    }

    @Override
    public List<HakukohdeKelaDTO> gatHakukohdeKelaDTOs(List<String> hakukohdeOids) {
        if (hakukohdeOids.isEmpty()) {
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("hakukohdeOid on tyhjä");
            throw new BadRequestException(responseBuilder.build());
        }
        List<Hakukohde> hakukohteet = hakukohdeDAO.findHakukohteetByOids(new HashSet(hakukohdeOids));
        List<HakukohdeKelaDTO> hakukohdeKelaDTOs = new ArrayList<>();
        for (Hakukohde hakukohde : hakukohteet) {
            hakukohdeKelaDTOs.add(hakukohdeToHakukohdeKelaDTO(hakukohde));
        }
        return hakukohdeKelaDTOs;
    }

    private HakukohdeKelaDTO hakukohdeToHakukohdeKelaDTO(Hakukohde hakukohde) {
        HakukohdeKelaDTO kelaDTO = new HakukohdeKelaDTO();
        kelaDTO.setHakukohdeOid(hakukohde.getOid());
        Set<String> tarjoajaOids = findTarjoajaOids(hakukohde);
        ImmutablePair<String, Integer> alkamiskausi = findAlkamiskausi(hakukohde);
        kelaDTO.setKoulutuksenAlkamiskausiUri(alkamiskausi.getLeft());
        kelaDTO.setKoulutuksenAlkamisVuosi(alkamiskausi.getRight());
        kelaDTO.setKoulutusLaajuusarvos(findKomotosAndKomos(hakukohde));
        try {
            ImmutablePair<String, OrganisaatioKelaDTO> oppilaitosKoodi = findOppilaitosKoodi(tarjoajaOids);
            kelaDTO.setTarjoajaOid(oppilaitosKoodi.getLeft());
            kelaDTO.setOppilaitosKoodi(oppilaitosKoodi.getRight().getOppilaitosKoodi());
        } catch (Exception e) {
            throw new RuntimeException("Tarjoajille " + tarjoajaOids + " ei löytynyt oppilaitoskoodia!", e);
        }
        return kelaDTO;
    }

    private ImmutablePair<String, OrganisaatioKelaDTO> findOppilaitosKoodi(Set<String> tarjoajaOids) {
        for(String tarjoajaOid: tarjoajaOids) {
            OrganisaatioResultDTO organisaatiot = organisaatioService.findByOidWithHaeAPI(tarjoajaOid);
            List<OrganisaatioKelaDTO> os = organisaatiot != null ? organisaatiot.getOrganisaatiot() : Collections.<OrganisaatioKelaDTO>emptyList();
            OrganisaatioKelaDTO closestParentOrChildOrganizationWithOppilaitoskoodi = OrganisaatioUtil.findOrganisaatioWithOppilaitosStartingFrom(os, tarjoajaOid);
            if(closestParentOrChildOrganizationWithOppilaitoskoodi != null) {
                return ImmutablePair.of(tarjoajaOid, closestParentOrChildOrganizationWithOppilaitoskoodi);
            }
        }
        throw new RuntimeException("Oppilaitosta ei löytynyt tarjoajaOideille " + tarjoajaOids);
    }

    private ImmutablePair<String,Integer> findAlkamiskausi(Hakukohde hakukohde) {
        ImmutablePair<String, Integer> hkAlkamis = ImmutablePair.of(hakukohde.getUniqueAlkamiskausiUri(), hakukohde.getUniqueAlkamisVuosi());
        if(hkAlkamis.getLeft() == null || hkAlkamis.getRight() == null) {
            Haku haku = hakukohde.getHaku();
            return ImmutablePair.of(haku.getKoulutuksenAlkamiskausiUri(), haku.getKoulutuksenAlkamisVuosi());
        } else {
            return hkAlkamis;
        }
    }
    private KoulutusLaajuusarvoDTO convert(KoulutusV1RDTO m) {
        KoulutusLaajuusarvoDTO k = new KoulutusLaajuusarvoDTO();
        k.setOid(m.getOid());
        k.setOpintojenLaajuusarvo(uriToArvo(m.getOpintojenLaajuusarvo().getUri()));
        k.setKoulutuskoodi(uriToArvo(m.getKoulutuskoodi().getUri()));
        return k;
    }
    private String uriToArvo(String uri) {
        if(uri == null) {
            return null;
        } else {
            final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(uri);
            return koodiByUri != null ? koodiByUri.getKoodiArvo() : uri;
        }
    }
    private KoulutusLaajuusarvoDTO convert(Koulutusmoduuli koulutusmoduuli) {
        KoulutusLaajuusarvoDTO k = new KoulutusLaajuusarvoDTO();
        k.setOid(koulutusmoduuli.getOid());
        k.setKoulutuskoodi(uriToArvo(koulutusmoduuli.getKoulutusUri()));
        k.setOpintojenLaajuusarvo(uriToArvo(koulutusmoduuli.getOpintojenLaajuusarvoUri()));
        return k;
    }
    private List<KoulutusLaajuusarvoDTO> flatMapAndConvert(Koulutusmoduuli koulutusmoduuli) {
        if(koulutusmoduuli == null) {
            return Collections.emptyList();
        } else {
            List<KoulutusLaajuusarvoDTO> komotos = Lists.newArrayList();
            komotos.add(convert(koulutusmoduuli));
            for(Koulutusmoduuli alamoduuli : koulutusmoduuli.getAlamoduuliList()) {
                komotos.add(convert(alamoduuli));
            }
            return komotos;
        }
    }
    private KoulutusLaajuusarvoDTO convert(KoodistoUri uri) {
        KoulutusLaajuusarvoDTO k = new KoulutusLaajuusarvoDTO();
        k.setOpintojenLaajuusarvo(null);
        k.setKoulutuskoodi(uriToArvo(uri.getKoodiUri()));
        return k;
    }
    private List<KoulutusLaajuusarvoDTO> findKomotosAndKomos(Hakukohde hakukohde) {
        List<KoulutusLaajuusarvoDTO> kl = Lists.newArrayList();
        for(KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
            KoulutusV1RDTO koulutus = KoulutusResourceImplV1.convert(converterToRDTO, komoto, RestParam.noImage(false, null));

            kl.addAll(flatMapAndConvert(komoto.getKoulutusmoduuli()));
            Set<KoodistoUri> sisaltyvatKoulutuskoodit = komoto.getSisaltyvatKoulutuskoodit();
            for(KoodistoUri uri : sisaltyvatKoulutuskoodit) {
                kl.add(convert(uri));
            }
            kl.add(convert(koulutus));

        }
        return kl;
    }

    private String findOppilaitosKoodi(List<OrganisaatioKelaDTO> organisaatiot) {
        for(OrganisaatioKelaDTO o: organisaatiot) {
            String oppilaitoskoodi = findOppilaitosKoodi(o);
            if(oppilaitoskoodi != null) {
                return oppilaitoskoodi;
            }
        }
        return null;
    }

    private String findOppilaitosKoodi(OrganisaatioKelaDTO organisaatio) {
        if(organisaatio.getOppilaitosKoodi() != null) {
            return organisaatio.getOppilaitosKoodi();
        } else {
            return findOppilaitosKoodi(organisaatio.getChildren());
        }
    }
    private Set<String> findTarjoajaOids(Hakukohde hakukohde) {
        Set<String> s = Sets.newHashSet();
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            if (koulutusmoduuliToteutus.getTarjoaja() != null) {
                s.add(koulutusmoduuliToteutus.getTarjoaja());
            }
        }
        return s;
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

        SortedSet<Valintakoe> valintakoes = hakukohde.getValintakoes();
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
                    switch (pisteraja.getTyyppi()) {
                        case "Paasykoe":
                            LOG.debug("    pisteraja: pääsykoe");
                            vk = (vk == null) ? new ValintakoeRDTO() : vk;
                            if (vk.getValintakoePisterajas() == null) {
                                vk.setValintakoePisterajas(new ArrayList<ValintakoePisterajaRDTO>());
                            }
                            vk.getValintakoePisterajas().add(pisteraja);
                            break;
                        case "Lisapisteet":
                            LOG.debug("    pisteraja: lisäpisteet");
                            lt = (lt == null) ? new ValintakoeRDTO() : lt;
                            if (lt.getValintakoePisterajas() == null) {
                                lt.setValintakoePisterajas(new ArrayList<ValintakoePisterajaRDTO>());
                            }
                            lt.getValintakoePisterajas().add(pisteraja);
                            break;
                        default:
                            // Anything else, add to both ("Kokonaispisteet")
                            LOG.debug("    pisteraja: default case, add pisteraja to both");
                            addToBothVKs.add(pisteraja);
                            break;
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
