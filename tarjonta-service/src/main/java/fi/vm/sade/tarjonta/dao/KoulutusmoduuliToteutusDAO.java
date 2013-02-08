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
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import java.util.List;

/**
 *
 */
public interface KoulutusmoduuliToteutusDAO extends JpaDAO<KoulutusmoduuliToteutus, Long> {

    public KoulutusmoduuliToteutus findByOid(String oid);

    public KoulutusmoduuliToteutus findKomotoByOid(String oid);

    public List<KoulutusmoduuliToteutus> findByCriteria(
        List<String> tarjoajaOids, String nimi, int koulutusAlkuVuosi, List<Integer> koulutusAlkuKuukaudet);

    public KoulutusmoduuliToteutus findKomotoWithYhteyshenkilosByOid(String oid);

    public List<KoulutusmoduuliToteutus> findKoulutusModuuliToteutusesByOids(List<String> oids);

    /**
     * Return all koulutumoduulitoteutuses in oid list with Hakukohde depencies
     *
     * @param komotoOids
     * @return List<KoulutusmoduuliToteutus>
     */
    public List<KoulutusmoduuliToteutus> findKoulutusModuulisWithHakukohdesByOids(List<String> komotoOids);

    public List<KoulutusmoduuliToteutus> findKoulutusModuuliWithPohjakoulutusAndTarjoaja(String tarjoaja,String pohjakoulutus,
                                                                                         String koulutusluokitus,String koulutusohjelma);

}

