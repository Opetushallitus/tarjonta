package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.*;

import javax.jws.WebParam;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class TarjontaAdminServiceMock implements TarjontaAdminService {

    private HashMap<String, HakuTyyppi> haut = new HashMap<String, HakuTyyppi>();

    public TarjontaAdminServiceMock() {
        initDefValues();
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
    public void lisaaKoulutuksiaHakukohteelle(@WebParam(partName = "parameters", name = "lisaaKoulutusHakukohteelle", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LisaaKoulutusHakukohteelleTyyppi parameters) {
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
}
