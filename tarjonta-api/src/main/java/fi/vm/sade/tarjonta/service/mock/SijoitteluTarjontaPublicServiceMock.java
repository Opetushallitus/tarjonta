package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.*;

import javax.jws.WebParam;

/**
 * @author Eetu Blomqvist
 */
public class SijoitteluTarjontaPublicServiceMock implements TarjontaPublicService {

    @Override
    public HaeHakukohteenLiitteetVastausTyyppi lueHakukohteenLiitteet(@WebParam(partName = "parameters", name = "haeHakukohteenLiitteetKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeHakukohteenLiitteetKyselyTyyppi parameters) {
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
    public HaeKoulutuksetVastausTyyppi haeKoulutukset(@WebParam(partName = "kysely", name = "haeKoulutuksetKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeKoulutuksetKyselyTyyppi kysely) {
        return null;
    }

    @Override
    public LueHakukohdeVastausTyyppi lueHakukohde(@WebParam(partName = "kysely", name = "lueHakukohdeKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueHakukohdeKyselyTyyppi kysely) {
        return null;
    }

    @Override
    public HaeHakukohteetVastausTyyppi haeHakukohteet(@WebParam(partName = "kysely", name = "haeHakukohteetKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeHakukohteetKyselyTyyppi kysely) {
        return null;
    }

    @Override
    public ListHakuVastausTyyppi listHaku(@WebParam(partName = "parameters", name = "listaaHaku", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") ListaaHakuTyyppi parameters) {
        return null;
    }

    @Override
    public TarjontaTyyppi haeTarjonta(@WebParam(name = "oid", targetNamespace = "") String oid) {

        System.out.println("Returning mocked tarjonta");
        TarjontaTyyppi tt = new TarjontaTyyppi();

        HakuTyyppi haku = new HakuTyyppi();
        haku.setOid(oid);
        haku.setSijoittelu(true);
        haku.setHaunTunniste("Tunniste " + oid);
        tt.setHaku(haku);
        addHakukohdeList(tt);
        return tt;
    }

    @Override
    public LueHakukohdeKoulutuksineenVastausTyyppi lueHakukohdeKoulutuksineen(@WebParam(partName = "hakukohdeKysely", name = "LueHakukohdeKoulutuksineenKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueHakukohdeKoulutuksineenKyselyTyyppi hakukohdeKysely) {
        return new LueHakukohdeKoulutuksineenVastausTyyppi();
    }

    private void addHakukohdeList(TarjontaTyyppi tt) {
        for (int i = 0; i < 10; i++) {
            tt.getHakukohde().add(createHakuKohde(i, 10));
        }
    }

    private HakukohdeTyyppi createHakuKohde(int seq, int aloituspaikat) {
        HakukohdeTyyppi hkt = new HakukohdeTyyppi();
        hkt.setAloituspaikat(aloituspaikat);
        hkt.setHakukelpoisuusVaatimukset("Hakukelpoisuusvaatimukset " + seq);
        hkt.setHakukohdeNimi("Hakukohde " + seq);
        hkt.setHakukohteenHakuOid("hakukohteenHakuOid" + seq);
        hkt.setHakukohteenTila(TarjontaTila.LUONNOS);
        hkt.setOid("Oid" + seq);

        return hkt;
    }

    @Override
    public LueKoulutusVastausTyyppi lueKoulutus(@WebParam(partName = "kysely", name = "lueKoulutusKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueKoulutusKyselyTyyppi kysely) {
        return null;
    }

    @Override
    public HaeKoulutusmoduulitVastausTyyppi haeKoulutusmoduulit(@WebParam(partName = "kysely", name = "haeKoulutusmoduulitKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeKoulutusmoduulitKyselyTyyppi kysely) {
        // TODO: implement this method.
        return null;
    }

    @Override
    public HaeKoulutusmoduulitVastausTyyppi haeKaikkiKoulutusmoduulit(@WebParam(partName = "kysely", name = "haeKoulutusmoduulitKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeKoulutusmoduulitKyselyTyyppi kysely) {
        // TODO: implement this method.
        return null;
    }
}
