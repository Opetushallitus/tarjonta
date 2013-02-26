/*
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
package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import java.util.List;

/**
 */
public interface HakukohdeDAO extends JpaDAO<Hakukohde, Long> {

    public List<Hakukohde> findByKoulutusOid(String koulutusmoduuliToteutusOid);

    public List<Hakukohde> haeHakukohteetJaKoulutukset(HaeHakukohteetKyselyTyyppi kysely);
    
    public List<Hakukohde> findOrphanHakukohteet();
    
    List<Hakukohde> findHakukohdeWithDepenciesByOid(String oid);

    HakukohdeLiite findHakuKohdeLiiteById(String id);

    Valintakoe findValintaKoeById(String id);

    List<Valintakoe> findValintakoeByHakukohdeOid(String oid);

    Hakukohde findHakukohdeWithKomotosByOid(String oid);

    void removeValintakoe(Valintakoe valintakoe);

    void updateValintakoe(List<Valintakoe> valintakoes, String hakukohdeOid);


}

