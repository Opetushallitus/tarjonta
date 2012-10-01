package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.service.HakuService;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HaunNimi;
import fi.vm.sade.tarjonta.service.types.tarjonta.TarjontaTyyppi;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

 
public class TarjontaServiceMock implements HakuService {

    private HashMap<String,HakuTyyppi> haut = new HashMap<String, HakuTyyppi>();
    
    public TarjontaServiceMock() {
        initDefValues();
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

}
