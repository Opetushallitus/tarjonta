

package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusSearchDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliToteutusAdminService;
import fi.vm.sade.tarjonta.service.NoSuchOIDException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
      
/*
 *
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

/**
 *
 * @author Tuomas Katva
 */
public class KoulutusmoduuliToteutusAdminServiceMock implements KoulutusmoduuliToteutusAdminService {

    private HashMap<String,KoulutusmoduuliToteutusDTO> repo = new HashMap<String,KoulutusmoduuliToteutusDTO>();
    private Random deGenerator = new Random();
    
    
    public KoulutusmoduuliToteutusAdminServiceMock() {
        loadMockData();
    }
    
    @Override
    public List<KoulutusmoduuliToteutusDTO> findWithTila(KoulutusmoduuliToteutusSearchDTO criteria) {
        List<KoulutusmoduuliToteutusDTO> tilaKomotos = new ArrayList<KoulutusmoduuliToteutusDTO>();
        for (KoulutusmoduuliToteutusDTO komoto : repo.values()) {
            if (komoto.getTila() == criteria.getTila()) {
                tilaKomotos.add(komoto);
            }
        }
        return tilaKomotos;
    }

    @Override
    public KoulutusmoduuliToteutusDTO findByOID(String koulutusmoduuliToteutusOID) throws NoSuchOIDException {
        
        KoulutusmoduuliToteutusDTO dto = repo.get(koulutusmoduuliToteutusOID);
        if (dto == null) {
            throw new NoSuchOIDException(koulutusmoduuliToteutusOID);
        }
        return dto;
        
    }
    
    
    

    @Override
    public KoulutusmoduuliToteutusDTO save(KoulutusmoduuliToteutusDTO toteutus) {
        toteutus.setOid(new Float(deGenerator.nextFloat()).toString());
        repo.put(toteutus.getOid(), toteutus);
        return toteutus;
    }
    
    private void loadMockData() {
        for (int i = 0; i < 30;i++) {
            KoulutusmoduuliToteutusDTO dto = randomToteutus();
            repo.put(dto.getOid(), dto);
        }
    }
    
    
    private KoulutusmoduuliToteutusDTO randomToteutus() {
        TutkintoOhjelmaToteutusDTO dto = new TutkintoOhjelmaToteutusDTO();
        
        dto.setOid(new Float(deGenerator.nextFloat()).toString());
        dto.setToteutettavaKoulutusmoduuliOID(new Float(deGenerator.nextFloat()).toString());
        dto.setNimi("Komoto " + deGenerator.nextInt(100));
        int tila = deGenerator.nextInt(3) +1;
        switch (tila) {
            case 1 :
                dto.setTila(KoulutusmoduuliTila.SUUNNITTELUSSA);
                break;
            case 2 : 
                dto.setTila(KoulutusmoduuliTila.VALMIS);
                break;
            case 3 :
                dto.setTila(KoulutusmoduuliTila.JULKAISTU);
                break;
                
            default :
                dto.setTila(KoulutusmoduuliTila.VALMIS);
                break;
        }
        
        return dto;
    }
}
