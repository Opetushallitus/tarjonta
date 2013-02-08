package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.*;

import javax.jws.WebParam;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;


public class TarjontaPublicServiceMock implements TarjontaPublicService {

    private static final Logger LOG = Logger.getAnonymousLogger();

    private HashMap<String, HakuTyyppi> haut = new HashMap<String, HakuTyyppi>();

    public TarjontaPublicServiceMock() {
        initDefValues();
    }

    @Override
    public HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi haeHakukohteenValintakokeetHakukohteenTunnisteella(@WebParam(partName = "parameters", name = "haeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi parameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HaeTarjoajanKoulutustenPohjakoulutuksetVastaus haeTarjoajanKoulutustenPohjakoulutukset(@WebParam(partName = "parameters", name = "haeTarjoajanKoulutustenPohjakoulutuksetKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeTarjoajanKoulutustenPohjakoulutuksetKysely parameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LueHakukohteenValintakoeTunnisteellaVastausTyyppi lueHakukohteenValintakoeTunnisteella(@WebParam(partName = "parameters", name = "lueHakukohteenValintakoeTunnisteellaKyselyTyyppi", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueHakukohteenValintakoeTunnisteellaKyselyTyyppi parameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LueHakukohteenLiiteTunnisteellaVastausTyyppi lueHakukohteenLiiteTunnisteella(@WebParam(partName = "parameters", name = "lueHakukohteenLiiteTunnisteellaKyselyTyyppi", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueHakukohteenLiiteTunnisteellaKyselyTyyppi parameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HaeHakukohteenLiitteetVastausTyyppi lueHakukohteenLiitteet(@WebParam(partName = "parameters", name = "haeHakukohteenLiitteetKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeHakukohteenLiitteetKyselyTyyppi parameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ListHakuVastausTyyppi listHaku(ListaaHakuTyyppi parameters) {
        ListHakuVastausTyyppi lvt = new ListHakuVastausTyyppi();
        lvt.getResponse().addAll(haut.values());
        return lvt;
    }

    @Override
    public TarjontaTyyppi haeTarjonta(String oid) {
        TarjontaTyyppi tt = new TarjontaTyyppi();
        tt.setHaku(haut.get(oid));
        return tt;
    }

    @Override
    public LueHakukohdeKoulutuksineenVastausTyyppi lueHakukohdeKoulutuksineen(@WebParam(partName = "hakukohdeKysely", name = "LueHakukohdeKoulutuksineenKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueHakukohdeKoulutuksineenKyselyTyyppi hakukohdeKysely) {
        return new LueHakukohdeKoulutuksineenVastausTyyppi();
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
    public HaeHakukohteetVastausTyyppi haeHakukohteet(HaeHakukohteetKyselyTyyppi kysely) {
        LOG.info("haeHakukohteet(): kysely=" + kysely);

        HaeHakukohteetVastausTyyppi result = new HaeHakukohteetVastausTyyppi();

        // TODO add something

        return result;
    }

    @Override
    public HaeKoulutuksetVastausTyyppi haeKoulutukset(HaeKoulutuksetKyselyTyyppi kysely) {
        LOG.info("haeKoulutukset(): kysely=" + kysely);

        //TODO implement if necessary
        return null;
    }

	@Override
	public LueKoulutusVastausTyyppi lueKoulutus(LueKoulutusKyselyTyyppi kysely) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LueHakukohdeVastausTyyppi lueHakukohde(LueHakukohdeKyselyTyyppi kysely) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HaeKoulutusmoduulitVastausTyyppi haeKoulutusmoduulit(HaeKoulutusmoduulitKyselyTyyppi kysely) {
		// TODO Auto-generated method stub
		return null;
	}
        
        @Override
	public HaeKoulutusmoduulitVastausTyyppi haeKaikkiKoulutusmoduulit(HaeKoulutusmoduulitKyselyTyyppi kysely) {
		// TODO Auto-generated method stub
		return null;
	}

}

