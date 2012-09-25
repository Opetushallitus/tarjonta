package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.service.TarjontaService;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTyyppi;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


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
    
    
  
}
