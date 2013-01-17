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
package fi.vm.sade.tarjonta.publication;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import java.util.List;

/**
 * Service that provides ready-made methods that return tarjonta data which is
 * ready for publishing.
 *
 * @author Jukka Raanamo
 */
public interface PublicationDataService {

    /**
     * Returns a list of KoulutusmoduuliToteutus where: <ul>
     *  <li>{@link KoulutusmoduuliToteutus#getTila() } equal to
     * {@link TarjontaTila#VALMIS}</li> </ul>
     *
     * @return
     */
    public List<KoulutusmoduuliToteutus> listKoulutusmoduuliToteutus();

    /**
     * Returns a list of Hakukohde where: <ul> <li>{@link Hakukohde#getTila() }
     * equal to {@link TarjontaTila#VALMIS}</li> </ul>
     *
     * @return
     */
    public List<Hakukohde> listHakukohde();

    /**
     * Return a list of Haku objects ready to be published, i.e: <ul>
     *  <li>{@link Haku#getTila() } equal to {@link TarjontaTila#VALMIS}</li>
     * </ul>
     *
     *
     * @return
     */
    public List<Haku> listHaku();

    public void updatePublicationStatus(List<GeneerinenTilaTyyppi> tilaOids);
    
    public boolean isValidStateChange(GeneerinenTilaTyyppi tyyppi);
}
