package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.search.*;
import fi.vm.sade.tarjonta.service.types.*;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Ordering;

import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakuaikaDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.resources.HakuResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeNimiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeTulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

@Transactional(readOnly = true, rollbackFor = Throwable.class)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class HakuResourceImpl implements HakuResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakuResourceImpl.class);
    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private TarjontaAdminService tarjontaAdminService;

    @Autowired
    private KoulutusSearchService koulutusSearchService;

    @Autowired
    private HakukohdeSearchService hakukohdeSearchService;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired(required = true)
    private ConversionService conversionService;

    public static List<OidRDTO> convertOidList(List<String> oids) {
        List<OidRDTO> result = new ArrayList<OidRDTO>();
        for (String oid : oids) {
            result.add(new OidRDTO(oid));
        }
        return result;
    }

    // /haku/hello
    @Override
    public String hello() {
        LOG.debug("/haku/hello -- hello()");
        return "Well Hello! " + new Date();
    }

    // /haku?...
    @Override
    public List<OidRDTO> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore,
            Date lastModifiedSince) {
        LOG.debug("/haku -- search({}, {}, {}, {}, {})", new Object[] { searchTerms, count, startIndex,
                lastModifiedBefore, lastModifiedSince });

        TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

        count = manageCountDefaultValue(count);

        List<OidRDTO> result = convertOidList(hakuDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore,
                lastModifiedSince));
        LOG.debug("  result={}", result);
        return result;
    }

    // /haku/OID
    @Override
    public HakuDTO getByOID(String oid) {
        LOG.debug("/haku/{} -- getByOID()", oid);

        HakuDTO result = conversionService.convert(hakuDAO.findByOid(oid), HakuDTO.class);
        LOG.debug("  result={}", result);
        return result;
    }


    @Override
    public List<HakuDTO> findAllHakus() {
        List<HakuDTO> hakuDTOs = new ArrayList<HakuDTO>();

        List<Haku> hakus = hakuDAO.findAll();
        LOG.info("Found : {} hakus",hakus.size());

        if (hakus != null){
            for (Haku haku:hakus) {
                HakuDTO hakuDTO = conversionService.convert(haku,HakuDTO.class);
                hakuDTOs.add(hakuDTO);
            }
        }

        return hakuDTOs;
    }

    // /haku/OID/hakukohde
    @Override
    public List<OidRDTO> getByOIDHakukohde(String oid, String searchTerms, int count, int startIndex,
            Date lastModifiedBefore, Date lastModifiedSince, String organisationOidsStr, String hakukohdeTilasStr) {
        LOG.debug("/haku/{}/hakukohde -- getByOIDHakukohde()", oid);

        List<String> organisationOids = splitToList(organisationOidsStr, ",");
        List<String> hakukohdeTilas = splitToList(hakukohdeTilasStr, ",");

        LOG.debug("  oids = {}", organisationOids);
        LOG.debug("  tilas = {}", hakukohdeTilas);

        if (!organisationOids.isEmpty()) {
            throw new IllegalArgumentException("organisationOids - parameter not supported yet");
        }
        if (!hakukohdeTilas.isEmpty()) {
            throw new IllegalArgumentException("hakukohdeTilas - parameter not supported yet");
        }

        count = manageCountDefaultValue(count);

        List<OidRDTO> result = convertOidList(hakukohdeDAO.findByHakuOid(oid, searchTerms, count, startIndex,
                lastModifiedBefore, lastModifiedSince));
        LOG.debug("  result={}", result);
        return result;
    }

    private List<String> splitToList(String input, String separator) {
        LOG.debug("splitToList({}, {})", input, separator);

        if (input == null || input.trim().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        String[] params = input.split(separator);
        return Arrays.asList(params);
    }

    // /haku/OID/hakukohdeTulos
    @Override
    public HakukohdeTulosRDTO getByOIDHakukohdeTulos(String oid, String searchTerms, int count, int startIndex,
            Date lastModifiedBefore, Date lastModifiedSince, String organisationOidsStr, String hakukohdeTilasStr, Integer alkamisVuosi, String alkamisKausi) {

        LOG.debug("/haku/{}/hakukohdeTulos -- getByOIDHakukohdeTulos()", oid);

        final String kieliAvain = "fi"; // TODO: lisää
                                        // rajapintaan

        final String kieliAvain_fi = "fi";
        final String kieliAvain_sv = "sv";
        final String kieliAvain_en = "en";

        final String filtterointiTeksti = StringUtils.upperCase(StringUtils.trimToEmpty(searchTerms));

        List<String> organisationOids = splitToList(organisationOidsStr, ",");
        List<String> hakukohdeTilas = splitToList(hakukohdeTilasStr, ",");

        LOG.debug("  oids = {}", organisationOids);
        LOG.debug("  tilas = {}", hakukohdeTilas);

        count = manageCountDefaultValue(count);

        HakukohteetKysely hakukohteetKysely = new HakukohteetKysely();
        hakukohteetKysely.setHakuOid(oid);
        hakukohteetKysely.getTarjoajaOids().addAll(organisationOids);
        hakukohteetKysely.setKoulutuksenAlkamiskausi(alkamisKausi);
        hakukohteetKysely.setKoulutuksenAlkamisvuosi(alkamisVuosi);

        if (hakukohdeTilas.size() >0 ) {
            for(String tilaString: hakukohdeTilas) {
                TarjontaTila tila = TarjontaTila.valueOf(tilaString);
                if (tila != null) {
                    hakukohteetKysely.addTila(tila);
                } else {
                    LOG.error("  INVALID TarjontaTila in 'hakukohdeTila' : {}", hakukohdeTilas);
                }
            }
        }

        HakukohteetVastaus v = hakukohdeSearchService.haeHakukohteet(hakukohteetKysely);
        LOG.debug("  kysely for haku '{}' found '{}'", new Object[] { oid, v.getHakukohteet().size() });
        Collection<HakukohdePerustieto> tulokset = v.getHakukohteet();
        // filtteroi tarvittaessa tulokset joko tarjoaja- tai hakukohdenimen
        // mukaan!
        if (!filtterointiTeksti.isEmpty()) {
            tulokset = Collections2.filter(tulokset, new Predicate<HakukohdePerustieto>() {

                private String haeTekstiAvaimella(Map<String, String> tekstit) {

                    if(tekstit.containsKey(kieliAvain)) {
                            return StringUtils.upperCase(tekstit.get(kieliAvain));
                    }

                    // Koska kieliavain on vielä kovakoodattu ja tarjoajanimiä ei ole
                    // kuin yhdellä kielellä niin testataan muutkin kielet
                    if(tekstit.containsKey(kieliAvain_fi)) {
                        return StringUtils.upperCase(tekstit.get(kieliAvain_fi));
                    }

                    if(tekstit.containsKey(kieliAvain_sv)) {
                        return StringUtils.upperCase(tekstit.get(kieliAvain_sv));
                    }

                    if(tekstit.containsKey(kieliAvain_en)) {
                        return StringUtils.upperCase(tekstit.get(kieliAvain_en));
                    }

                    return StringUtils.EMPTY;
                }

                public boolean apply(@Nullable HakukohdePerustieto hakukohde) {
                    return haeTekstiAvaimella(hakukohde.getTarjoajaNimi())
                            .contains(filtterointiTeksti)
                            || haeTekstiAvaimella(hakukohde.getNimi()).contains(filtterointiTeksti);
                }
            });
        }
        // sortataan tarjoajanimen mukaan!, testi olis kiva (tm)

        Ordering<HakukohdePerustieto> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<HakukohdePerustieto, Comparable>() {
            public Comparable apply(HakukohdePerustieto input) {
                String tarjoajaNimi = input.getTarjoajaNimi().get(kieliAvain);
                // Varajärjestys, jos valitulla kieliavaimella ei löydy tarjoajanimeä
                if(tarjoajaNimi == null) {
                    tarjoajaNimi = input.getTarjoajaNimi().get(kieliAvain_fi);
                    if(tarjoajaNimi == null) {
                        tarjoajaNimi = input.getTarjoajaNimi().get(kieliAvain_sv);
                        if(tarjoajaNimi == null) {
                            tarjoajaNimi = input.getTarjoajaNimi().get(kieliAvain_en);
                        }
                    }
                }
                return tarjoajaNimi;
            }
        });

        List<HakukohdePerustieto> sortattuLista = ordering.immutableSortedCopy(tulokset);

        int size = sortattuLista.size();
        LOG.debug("  after filtering results found '{}'", size);
        List<HakukohdeNimiRDTO> results = new ArrayList<HakukohdeNimiRDTO>();
        int index = 0;
        for (HakukohdePerustieto tulos : sortattuLista) {
            if (index >= startIndex + count) {
                break; // done!
            }
            if (index >= startIndex) {
                HakukohdePerustieto hakukohde = tulos;
                HakukohdeNimiRDTO rdto = new HakukohdeNimiRDTO();
                // tarvitaanko?
                // result.setHakuVuosi(haku.getHakukausiVuosi());
                // result.setHakuKausi(tarjontaKoodistoHelper.getKoodiMetadataNimi(haku.getHakukausiUri()));
                rdto.setTarjoajaOid(hakukohde.getTarjoajaOid());
                rdto.setHakukohdeNimi(hakukohde.getNimi());
                rdto.setTarjoajaNimi(hakukohde.getTarjoajaNimi());
                rdto.setHakukohdeOid(hakukohde.getOid());
                rdto.setHakukohdeTila(hakukohde.getTila() != null ? hakukohde.getTila().name() : null);
                results.add(rdto);
            }
            ++index;
        }
        return new HakukohdeTulosRDTO(size, results);
    }

    // /haku/OID/hakukohdeWithName
    @Override
    public List<Map<String, String>> getByOIDHakukohdeExtra(String oid, String searchTerms, int count, int startIndex,
            Date lastModifiedBefore, Date lastModifiedSince, String organisationOidsStr, String hakukohdeTilasStr) {
        LOG.debug("/haku/{}/hakukohdeWithName -- getByOIDHakukohdeExtra()", oid);

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        count = manageCountDefaultValue(count);

        // Get list of oids
        List<OidRDTO> hakukohdeOids = getByOIDHakukohde(oid, searchTerms, count, startIndex, lastModifiedBefore,
                lastModifiedSince, organisationOidsStr, hakukohdeTilasStr);

        // Loop the result
        for (OidRDTO oidRDTO : hakukohdeOids) {
            String hakukohdeOid = oidRDTO.getOid();

            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(hakukohdeOid);

            Map<String, String> row = new HashMap<String, String>();

            row.put("oid", hakukohde.getOid());
            row.put("nimiUri", hakukohde.getHakukohdeNimi());

            // TODO resolve nimi uri :)

            result.add(row);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = false)
    @Secured("ROLE_APP_TARJONTA_READ_UPDATE")
    public String createHaku(HakuDTO dto) {
        LOG.error("operation not supported!");
        return "not supported";
    }

    @Override
    @Secured("ROLE_APP_TARJONTA_READ_UPDATE")
    @Transactional(readOnly = false)
    public void replaceHaku(HakuDTO dto) {
        LOG.error("operation not supported!");
    }

    @Override
    @Secured("ROLE_APP_TARJONTA_READ_UPDATE")
    @Transactional(readOnly = false)
    public void deleteHaku(String hakuOid) {
        LOG.error("operation not supported!");
    }

    @Override
    @Transactional(readOnly = false)
    @Secured("ROLE_APP_TARJONTA_READ_UPDATE")
    public void updateHakuState(String hakuOid, String state) {
        TarjontaTila tt = TarjontaTila.valueOf(state);
        PaivitaTilaTyyppi ptt = new PaivitaTilaTyyppi(Collections.singletonList(new GeneerinenTilaTyyppi(hakuOid,
                SisaltoTyyppi.HAKU, tt.asDto())));
        tarjontaAdminService.paivitaTilat(ptt);
    }


    /**
     * Adjust default value.
     *
     * @param count
     * @return if count < 0, set count = Integer.MAX_VALUE), if count == 0, set count to 100, else not modified
     */
    private int manageCountDefaultValue(int count) {

        if (count < 0) {
            count = Integer.MAX_VALUE;
            LOG.info("  count < 0, adjusting to Integer.MAX_VALUE");
        }

        if (count == 0) {
            count = 100;
            LOG.info("  count == 0, adjusting to default value of {}", count);
        }

        return count;
    }
}
