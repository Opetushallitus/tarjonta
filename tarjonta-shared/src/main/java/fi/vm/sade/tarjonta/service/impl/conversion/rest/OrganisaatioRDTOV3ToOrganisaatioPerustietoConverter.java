package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.dto.v3.OrganisaatioRDTOV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;

//public class OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter extends AbstractToDomainConverter<OrganisaatioRDTOV3, OrganisaatioPerustieto> {
public class OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter implements Converter<OrganisaatioRDTOV3, OrganisaatioPerustieto> {
    private static final Logger LOG = LoggerFactory.getLogger(OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter.class);

    @Override
    public OrganisaatioPerustieto convert(OrganisaatioRDTOV3 t) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //List<Yhteystieto> yhteystietos = new ArrayList<Yhteystieto>();
        OrganisaatioPerustieto s = new OrganisaatioPerustieto();

        s.setOid(t.getOid());
        s.setAlkuPvm(t.getAlkuPvm());
        s.setLakkautusPvm(t.getLakkautusPvm());
        s.setParentOid(t.getParentOid());
        s.setParentOidPath(t.getParentOid());
        s.setYtunnus(t.getYTunnus());
        s.setVirastoTunnus(t.getVirastoTunnus());
        //s.setAliOrganisaatioMaara(t.getAliOrganisaatioMaara);
        s.setOppilaitosKoodi(t.getOppilaitosKoodi());
        s.setOppilaitostyyppi(t.getOppilaitosTyyppiUri());
        s.setToimipistekoodi(t.getToimipistekoodi());
        //s.setMatch(t.getMatch);
        s.setNimi(t.getNimi());
        //s.setorganisaatioTyypit(t.getTyypit());
        //s.setk(t.getKieletUris());
        s.setKotipaikkaUri(t.getKotipaikkaUri());
        //s.setChildren(t.ge);

        /*

        // t.setChildCount(s.getChildCount());
        s.setDomainNimi(t.getDomainNimi());

        s.setKielet(convertListToList(t.getKieletUris()));
        s.setKotipaikka(t.getKotipaikkaUri());
        s.setKuvaus2(convertMapToMonikielinenTeksti(t.getKuvaus2()));

        s.setMaa(t.getMaaUri());
        s.setMetadata(convertMetadata(t.getMetadata()));
        s.setNimi(convertMapToMonikielinenTeksti(t.getNimi()));

        OrganisaatioNimiModelMapper organisaatioNimiModelMapper = new OrganisaatioNimiModelMapper();

        // Define the target list type for mapping
        Type organisaatioNimiListType = new TypeToken<List<OrganisaatioNimi>>() {}.getType();

        // Map DTO to domain type
        s.setNimet((List<OrganisaatioNimi>) organisaatioNimiModelMapper.map(t.getNimet(), organisaatioNimiListType));

        // Asetetaan nimihakuun nimeksi nimihistorian current nimi, tai uusin nimi
        MonikielinenTeksti nimi = OrganisaatioNimiUtil.getNimi(s.getNimet());
        if (nimi != null) {
            s.setNimihaku(convertNimiMapToNimihaku(nimi.getValues()));
        }

        // t.set(s.getNimiLyhenne());
        s.setOpetuspisteenJarjNro(t.getOpetuspisteenJarjNro());
        s.setOppilaitosKoodi(t.getOppilaitosKoodi());
        s.setOppilaitosTyyppi(t.getOppilaitosTyyppiUri());
        // t.set(s.getOrganisaatiotyypitStr());
        s.setParentOid(s.getParentO() != null ? s.getParent().getOid() : null);
        // t.set(s.getParentIdPath());
        // t.setParentMetadata(s.getParentMetadata());
        s.setParentOidPath(s.getParentOidPath());
        // t.set(s.getParentSuhteet());

        // t.set(s.getPuhelin());
        s.setToimipisteKoodi(t.getToimipistekoodi());
        s.setTyypit(convertListToList(t.getTyypit()));
        // t.set(s.getTyypitAsString());
        s.setVuosiluokat(convertListToList(t.getVuosiluokat()));
        s.setOrganisaatioLisatietotyypit(t.getLisatiedot().stream()
                .map(lisatietoNimi -> {
                    OrganisaatioLisatietotyyppi organisaatioLisatietotyyppi = new OrganisaatioLisatietotyyppi();
                    Lisatietotyyppi lisatietotyyppi = new Lisatietotyyppi();
                    lisatietotyyppi.setNimi(lisatietoNimi);
                    organisaatioLisatietotyyppi.setLisatietotyyppi(lisatietotyyppi);
                    organisaatioLisatietotyyppi.setOrganisaatio(s);
                    return organisaatioLisatietotyyppi;
                })
                .collect(Collectors.toSet()));
        s.setRyhmatyypit(convertSetToSet(t.getRyhmatyypit()));
        s.setKayttoryhmat(convertSetToSet(t.getKayttoryhmat()));
        s.setYhteishaunKoulukoodi(t.getYhteishaunKoulukoodi());
        // t.set(s.getYhteystiedot());
        // t.set(s.getYhteystietoArvos());
        s.setYritysmuoto(t.getYritysmuoto());
        s.setYtjKieli(t.getYTJKieli());
        s.setYtjPaivitysPvm(t.getYTJPaivitysPvm());
        s.setYtunnus(t.getYTunnus());
        s.setVirastoTunnus(t.getVirastoTunnus());

        if (t.getYhteystietoArvos()!=null) {
            s.setYhteystietoArvos(convertYhteystietoArvos(t.getYhteystietoArvos()));
        }

        for (Map<String, String> m : t.getYhteystiedot()) {
            Yhteystieto y = convertYhteystietoGeneric(m);
            if (y != null) {
                yhteystietos.add(y);
            }
        }
        s.setYhteystiedot(yhteystietos);

        return s;
    }

    private List<YhteystietoArvo> convertYhteystietoArvos(List<Map<String, String>> arvoMaps) {
        ArrayList<YhteystietoArvo> arvos = new ArrayList<>(arvoMaps.size());
        for (Map<String, String> arvoMap : arvoMaps) {
            YhteystietoArvo arvo = new YhteystietoArvo();
            arvo.setKentta(new YhteystietoElementti());
            arvo.setKieli(arvoMap.get("YhteystietoArvo.kieli"));
            arvo.setArvoText(arvoMap.get("YhteystietoArvo.arvoText"));
            YhteystietoElementti ye = arvo.getKentta();
            ye.setNimi(arvoMap.get("YhteystietoElementti.nimi"));
            ye.setNimiSv(arvoMap.get("YhteystietoElementti.nimisv"));
            ye.setOid(arvoMap.get("YhteystietoElementti.oid"));
            ye.setTyyppi(arvoMap.get("YhteystietoElementti.tyyppi"));
            ye.setKaytossa(Boolean.parseBoolean(arvoMap.get("YhteystietoElementti.kaytossa")));
            ye.setPakollinen(Boolean.parseBoolean(arvoMap.get("YhteystietoElementti.pakollinen")));
            if (arvoMap.get("YhteystietojenTyyppi.oid") != null) {
                YhteystietojenTyyppi yt = new YhteystietojenTyyppi();
                yt.setOid(arvoMap.get("YhteystietojenTyyppi.oid"));
                yt.setNimi(convertYATToMonikielinenTeksti(arvoMap));
                ye.setYhteystietojenTyyppi(yt);
            }
            arvos.add(arvo);
        }
        return arvos;
    }

    private Osoite convertMapToOsoite(Map<String, String> s, String tyyppi) {
        if (s == null) {
            return null;
        }

        Osoite t = new Osoite();
        t.setOsoiteTyyppi(tyyppi);
        if (s.containsKey("coordinateType")) {
            t.setCoordinateType(s.get("coordinateType"));
        }
        if (s.containsKey("extraRivi")) {
            t.setExtraRivi(s.get("extraRivi"));
        }
        if (s.containsKey("maaUri")) {
            t.setMaa(s.get("maaUri"));
        }
        if (s.containsKey("osavaltio")) {
            t.setOsavaltio(s.get("osavaltio"));
        }
        if (s.containsKey("osoite")) {
            t.setOsoite(s.get("osoite").replace("\n", "<br />"));
        }
        if (s.containsKey("osoiteTyyppi")) {
            t.setOsoiteTyyppi(s.get("osoiteTyyppi"));
        }
        if (s.containsKey("postinumeroUri")) {
            t.setPostinumero(s.get("postinumeroUri"));
        }
        if (s.containsKey("postitoimipaikka")) {
            t.setPostitoimipaikka(s.get("postitoimipaikka"));
        }
        if (s.containsKey("yhteystietoOid")) {
            t.setYhteystietoOid(s.get("yhteystietoOid"));
        }
        if (s.get("lap") != null) {
            try {
                t.setLat(Double.parseDouble(s.get("lap")));
            }
            catch (NumberFormatException nfe) {
                // just don't set it then
            }
        }
        if (s.get("lng") != null) {
            try {
                t.setLng(Double.parseDouble(s.get("lng")));
            }
            catch (NumberFormatException nfe) {
                // just don't set it then
            }
        }
        if (s.get("ytjPaivitysPvm") != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                t.setYtjPaivitysPvm(df.parse(s.get("ytjPaivitysPvm")));
            }
            catch (ParseException pe) {
                // just don't set it then
            }
        }

        return t;
    }

    private MonikielinenTeksti convertYATToMonikielinenTeksti(Map<String, String> m) {
        MonikielinenTeksti mt = null;
        if (m != null) {
            mt = new MonikielinenTeksti();
            for (Map.Entry<String, String> e : m.entrySet()) {
                if (e.getKey().startsWith("YhteystietojenTyyppi.nimi.")) {
                    mt.addString(e.getKey().substring("YhteystietojenTyyppi.nimi.".length()), e.getValue());
                }
            }
        }
        return mt;
    }

    private MonikielinenTeksti convertMapToMonikielinenTeksti(Map<String, String> m) {
        MonikielinenTeksti mt = null;
        if (m != null) {
            mt = new MonikielinenTeksti();
            for (Map.Entry<String, String> e : m.entrySet()) {
                mt.addString(e.getKey(), e.getValue());
            }
        }
        return mt;
    }

    private Puhelinnumero convertPuhelinnumero(String numero, String tyyppi) {
        Puhelinnumero p = new Puhelinnumero();
        p.setPuhelinnumero(numero);
        p.setTyyppi(tyyppi);
        return p;
    }

    private Www convertWww(String wwwOsoite) {
        Www www = new Www();
        www.setWwwOsoite(wwwOsoite);
        return www;
    }

    private Email convertEmail(String emailOsoite) {
        Email email = new Email();
        email.setEmail(emailOsoite);
        return email;
    }

    private List<String> convertListToList(List<String> s) {
        return new ArrayList<>(s);
    }

    private Set<String> convertSetToSet(Set<String> s) {
        return new HashSet<>(s);
    }

    private BinaryData decodeFromUUENCODED(String kuva) {
        if (kuva == null || kuva.isEmpty()) {
            return null;
        }
        BinaryData bd = new BinaryData();
        bd.setData(Base64.base64ToByteArray(kuva));
        return bd;
    }

    private OrganisaatioMetaData convertMetadata(OrganisaatioMetaDataRDTO t) {
        if (t == null) {
            return null;
        }

        OrganisaatioMetaData s = new OrganisaatioMetaData();

        s.setHakutoimistoEctsEmail(convertMapToMonikielinenTeksti(t.getHakutoimistoEctsEmail()));
        s.setHakutoimistoEctsNimi(convertMapToMonikielinenTeksti(t.getHakutoimistoEctsNimi()));
        s.setHakutoimistoEctsPuhelin(convertMapToMonikielinenTeksti(t.getHakutoimistoEctsPuhelin()));
        s.setHakutoimistoEctsTehtavanimike(convertMapToMonikielinenTeksti(t.getHakutoimistoEctsTehtavanimike()));
        s.setHakutoimistoNimi(convertMapToMonikielinenTeksti(t.getHakutoimistonNimi()));
        s.setKoodi(t.getKoodi());
        s.setKuva(decodeFromUUENCODED(t.getKuvaEncoded()));
        if (t.getLuontiPvm()!=null) {
            s.setLuontiPvm(t.getLuontiPvm());
        }
        if (t.getMuokkausPvm()!=null) {
            s.setMuokkausPvm(t.getMuokkausPvm());
        }
        s.setNimi(convertMapToMonikielinenTeksti(t.getNimi()));

        for (Map<String, String> yhteystieto : t.getYhteystiedot()) {
            Yhteystieto y = convertYhteystietoGeneric(yhteystieto);
            if (y != null) {
                s.getYhteystiedot().add(y);
            }
        }

        if (t.getData() != null) {
            Set<NamedMonikielinenTeksti> nmtSet = new HashSet<NamedMonikielinenTeksti>();
            for (Map.Entry<String, Map<String, String>> e : t.getData().entrySet()) {
                NamedMonikielinenTeksti nmt = new NamedMonikielinenTeksti();
                nmt.setKey(e.getKey());
                nmt.setValue(convertMapToMonikielinenTeksti(e.getValue()));
                nmtSet.add(nmt);
            }
            s.setValues(nmtSet);
        }

        return s;
    }

    private Yhteystieto convertYhteystietoGeneric(Map<String, String> s) {
        if (s != null) {
            try {
                if (s.containsKey("kieli") == false) {
                    // TODO: Kieli missing, what to do
                    // 1. raise exception, 2. only log, 3. use default 'kielivalikoima_fi'
                    LOG.warn("missing kieli from yhteystieto");
                }
                if (s.get("email") != null) {
                    Email v = convertEmail(s.get("email"));
                    if (s.containsKey("yhteystietoOid")) {
                        v.setId(Long.parseLong(s.get("id")));
                        v.setYhteystietoOid(s.get("yhteystietoOid"));
                    }
                    if (s.containsKey("kieli")) {
                        v.setKieli(s.get("kieli"));
                    }
                    return v;
                } else if (s.get("www") != null) {
                    Www v = convertWww(s.get("www"));
                    if (s.containsKey("yhteystietoOid")) {
                        v.setId(Long.parseLong(s.get("id")));
                        v.setYhteystietoOid(s.get("yhteystietoOid"));
                    }
                    if (s.containsKey("kieli")) {
                        v.setKieli(s.get("kieli"));
                    }
                    return v;
                } else if (s.get("numero") != null) {
                    Puhelinnumero v = convertPuhelinnumero(s.get("numero"), s.get("tyyppi"));
                    if (s.containsKey("yhteystietoOid")) {
                        v.setId(Long.parseLong(s.get("id")));
                        v.setYhteystietoOid(s.get("yhteystietoOid"));
                    }
                    if (s.containsKey("kieli")) {
                        v.setKieli(s.get("kieli"));
                    }
                    return v;
                } else if (s.get("osoite") != null) {
                    Osoite v = convertMapToOsoite(s, null);
                    if (s.containsKey("yhteystietoOid")) {
                        Long id = Long.parseLong(s.remove("id"));
                        v.setId(id);
                    }
                    if (s.containsKey("kieli")) {
                        v.setKieli(s.get("kieli"));
                    }
                    return v;
                }
            }
            catch (NumberFormatException nfe) {
                LOG.error("failed parsing number", nfe);
            }
        }
        return null;
    }

    private String convertNimiMapToNimihaku(Map<String, String> nimiMap) {
        StringBuilder sb = new StringBuilder();
        for (String nimi : nimiMap.values()) {
            sb.append(",");
            sb.append(nimi);
        }
        return sb.toString();
    }
}
{*/
        return s;
    }
}