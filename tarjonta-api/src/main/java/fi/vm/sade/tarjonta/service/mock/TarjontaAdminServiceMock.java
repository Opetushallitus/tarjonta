package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.*;

import javax.jws.WebParam;
import java.util.*;

public class TarjontaAdminServiceMock implements TarjontaAdminService {

    private HashMap<String, HakuTyyppi> haut = new HashMap<String, HakuTyyppi>();

    public TarjontaAdminServiceMock() {
        initDefValues();
    }

    @Override
    public boolean tarkistaKoulutuksenKopiointi(@WebParam(partName = "parameters", name = "tarkistaKoulutusKopiointi", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") TarkistaKoulutusKopiointiTyyppi parameters) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void kopioiKoulutus(@WebParam(name = "kopioitavaKoulutus", targetNamespace = "") KoulutusTyyppi kopioitavaKoulutus, @WebParam(name = "organisaatioOids", targetNamespace = "") List<String> organisaatioOids) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ValintakoeTyyppi> paivitaValintakokeitaHakukohteelle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid, @WebParam(name = "hakukohteenValintakokeet", targetNamespace = "") List<ValintakoeTyyppi> hakukohteenValintakokeet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void poistaHakukohdeLiite(@WebParam(name = "hakukohdeLiiteTunniste", targetNamespace = "") String hakukohdeLiiteTunniste) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void poistaValintakoe(@WebParam(name = "ValintakoeTunniste", targetNamespace = "") String valintakoeTunniste) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ValintakoeTyyppi> tallennaValintakokeitaHakukohteelle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid, @WebParam(name = "hakukohteenValintakokeet", targetNamespace = "") List<ValintakoeTyyppi> hakukohteenValintakokeet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void tallennaLiitteitaHakukohteelle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid, @WebParam(name = "hakukohteenLiitteen", targetNamespace = "") List<HakukohdeLiiteTyyppi> hakukohteenLiitteen) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HakukohdeTyyppi lisaaHakukohde(HakukohdeTyyppi hakukohde) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HakukohdeTyyppi poistaHakukohde(HakukohdeTyyppi hakukohdePoisto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HakukohdeTyyppi paivitaHakukohde(HakukohdeTyyppi hakukohdePaivitys) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void lisaaTaiPoistaKoulutuksiaHakukohteelle(@WebParam(partName = "parameters", name = "lisaaKoulutusHakukohteelle", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LisaaKoulutusHakukohteelleTyyppi parameters) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HakuTyyppi paivitaHaku(HakuTyyppi hakuDto) {
        haut.remove(hakuDto.getOid());
        haut.put(hakuDto.getOid(), hakuDto);
        return hakuDto;
    }

    @Override
    public HakuTyyppi lisaaHaku(HakuTyyppi hakuDto) {
        haut.put(hakuDto.getOid(), hakuDto);
        return hakuDto;
    }

    @Override
    public void poistaHaku(HakuTyyppi hakuDto) {
        haut.remove(hakuDto.getOid());
    }

    @Override
    public PaivitaKoulutusVastausTyyppi paivitaKoulutus(PaivitaKoulutusTyyppi koulutus) throws GenericFault {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LisaaKoulutusVastausTyyppi lisaaKoulutus(LisaaKoulutusTyyppi koulutus) throws GenericFault {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initSample(String parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initKomo(String parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void initDefValues() {
        HakuTyyppi haku = new HakuTyyppi();
        HaunNimi nimi = new HaunNimi();
        nimi.setKielikoodi("fi");
        nimi.setNimi("Yhteishaku syksy 2013");
        haku.getHaunKielistetytNimet().add(nimi);
        HaunNimi nimiSe = new HaunNimi();
        nimiSe.setKielikoodi("sv");
        nimiSe.setNimi("gemensam sökning höst 2013");
        haku.getHaunKielistetytNimet().add(nimiSe);
        haku.setOid(UUID.randomUUID().toString());
        haku.setHakuVuosi(2013);
        haku.setHaunAlkamisPvm(new Date());
        haku.setHaunLoppumisPvm(new Date(System.currentTimeMillis() + 100000));
        haku.setSijoittelu(true);
        haku.setHaunTunniste("123456");
        haku.setKoulutuksenAlkamisVuosi(2014);

        haut.put(haku.getOid(), haku);

        HakuTyyppi haku2 = new HakuTyyppi();
        HaunNimi nimiFi = new HaunNimi();
        nimiFi.setKielikoodi("fi");
        nimiFi.setNimi("Yhteishaku syksy 2014");
        haku2.getHaunKielistetytNimet().add(nimiFi);
        HaunNimi nimiSee = new HaunNimi();
        nimiSee.setKielikoodi("sv");
        nimiSee.setNimi("gemensam sökning höst 2014");
        haku2.getHaunKielistetytNimet().add(nimiSee);
        haku2.setOid(UUID.randomUUID().toString());
        haku2.setHakuVuosi(2014);
        haku2.setHaunAlkamisPvm(new Date());
        haku2.setHaunLoppumisPvm(new Date(System.currentTimeMillis() + 100000));
        haku2.setSijoittelu(true);
        haku2.setHaunTunniste("7891011");
        haku2.setKoulutuksenAlkamisVuosi(2015);

        haut.put(haku2.getOid(), haku2);
    }

    @Override
    public void poistaKoulutus(String koulutusOid) {
        // TODO Auto-generated method stub
    }

    @Override
    public KoulutusmoduuliKoosteTyyppi lisaaKoulutusmoduuli(KoulutusmoduuliKoosteTyyppi koulutusmoduuli)
            throws GenericFault {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PaivitaTilaVastausTyyppi paivitaTilat(PaivitaTilaTyyppi parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean testaaTilasiirtyma(GeneerinenTilaTyyppi parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Mock storage
    private List<MonikielinenMetadataTyyppi> metadatas = new ArrayList<MonikielinenMetadataTyyppi>();

    @Override
    public List<MonikielinenMetadataTyyppi> haeMetadata(@WebParam(name = "avain", targetNamespace = "") String avain, @WebParam(name = "kategoria", targetNamespace = "") String kategoria) {
        List<MonikielinenMetadataTyyppi> result = new ArrayList<MonikielinenMetadataTyyppi>();

        for (MonikielinenMetadataTyyppi md : metadatas) {

            // Compare avain & kategoria
            if (avain != null && kategoria != null) {
                if (avain.equals(md.getAvain()) && kategoria.equals(md.getKategoria())) {
                    result.add(md);
                }
            }
            // Compare Avain
            else if (avain != null) {
                if (avain.equals(md.getAvain())) {
                    result.add(md);
                }
            }
            // Compare kategory only
            else if (kategoria != null) {
                if (kategoria.equals(md.getKategoria())) {
                    result.add(md);
                }
            }
            // "null" search - return all metadatas
            else {
                // "null" search - return all metadatas
                result.add(md);
            }
        }

        return result;
    }

    @Override
    public MonikielinenMetadataTyyppi tallennaMetadata(@WebParam(name = "avain", targetNamespace = "") String avain, @WebParam(name = "kategoria", targetNamespace = "") String kategoria, @WebParam(name = "kieli", targetNamespace = "") String kieli, @WebParam(name = "arvo", targetNamespace = "") String arvo) {
        MonikielinenMetadataTyyppi result = null;

        if (avain == null) {
            throw new IllegalArgumentException("Avain cannot be null.");
        }

        // Get matching metadatas
        List<MonikielinenMetadataTyyppi> mds = haeMetadata(avain, kategoria);

        for (MonikielinenMetadataTyyppi md : mds) {
            if (kieli != null && kieli.equals(md.getKieli())) {
                result = md;
                break;
            }
        }

        if (result == null) {
            result = new MonikielinenMetadataTyyppi();
            metadatas.add(result);
        }
        result.setArvo(arvo);
        result.setAvain(avain);
        result.setKategoria(kategoria);
        result.setKieli(kieli);

        return result;
    }
}
