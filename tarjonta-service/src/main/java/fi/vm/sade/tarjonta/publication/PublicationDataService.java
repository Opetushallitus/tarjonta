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
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;

import java.util.Collection;
import java.util.List;

/**
 * Service that provides ready-made methods that return tarjonta data which is
 * ready for publishing.
 *
 * @author Jukka Raanamo
 */
public interface PublicationDataService {

    String ACTION_INSERT = "INSERT";
    String ACTION_UPDATE = "UPDATE";
    String ACTION_CANCEL = "CANCEL";
    String DATA_TYPE_KOMOTO = "LOI";
    String DATA_TYPE_HAKU = "AS";
    String DATA_TYPE_HAKUKOHDE = "AO";

    /**
     * Returns a list of KoulutusmoduuliToteutus where: <ul>
     * <li>{@link KoulutusmoduuliToteutus#getTila() } equal to
     * {@link TarjontaTila#VALMIS}</li> </ul>
     *
     * @return
     */
    List<KoulutusmoduuliToteutus> listKoulutusmoduuliToteutus();

    /**
     * Returns a list of Hakukohde where: <ul> <li>{@link Hakukohde#getTila() }
     * equal to {@link TarjontaTila#VALMIS}</li> </ul>
     *
     * @return
     */
    List<Hakukohde> listHakukohde();

    /**
     * Return a list of Haku objects ready to be published, i.e: <ul>
     * <li>{@link Haku#getTila() } equal to {@link TarjontaTila#VALMIS}</li>
     * </ul>
     *
     *
     * @return
     */
    List<Haku> listHaku();

    /**
     * Throes IllegalArgumentException when an illegal state transfer is attempted.
     * @param tilaChanges
     * @return
     */
    Tilamuutokset updatePublicationStatus(List<Tila> tilaChanges);

    boolean isValidStatusChange(Tila tyyppi);

    List<Hakukohde> searchHakukohteetByKomotoOid(final Collection<String> komotoOids, final TarjontaTila hakuRequiredStatus, final TarjontaTila... hakukohdeRequiredStatus);

    List<MonikielinenMetadata> searchMetaData(final String key, final MetaCategory category);
}
