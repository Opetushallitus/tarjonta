package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.service.TarjontaService;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.Haku;
import fi.vm.sade.tarjonta.service.types.tarjonta.TarjontaTyyppi;

 
public class TarjontaServiceMock implements TarjontaService {

    @Override
    public ListHakuVastausTyyppi listHaku(ListaaHakuTyyppi parameters) {
        // TODO Auto-generated method stub
        return null;
    } 

    @Override
    public TarjontaTyyppi haeTarjonta(String oid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Haku paivitaHaku(Haku hakuDto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Haku lisaaHaku(Haku hakuDto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void poistaHaku(Haku hakuDto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
