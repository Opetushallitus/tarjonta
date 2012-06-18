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
package fi.vm.sade.tarjonta.model;

import fi.vm.sade.generic.model.BaseEntity;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author Jukka Raanamo
 */
@Entity
@Table(name = KoulutusmoduuliPerustiedot.TABLE_NAME)
public class KoulutusmoduuliPerustiedot extends BaseEntity {

    private static final long serialVersionUID = 9115186990353872981L;

    public static final String TABLE_NAME = "koulutusmoduuli_perustiedot";

    /**
     * TODO: this is just best guess, validate from model and Seppo what is the "name" of this field and if values are stored by value or by reference and
     * computed at runtime. Here runtime is guessed.
     */
    @Column(name = "koulutuskoodi_uri")
    private String koulutusKoodiUri;

    /**
     * Set of Koodisto uris, one for each "opetuskieli" (teaching language) provided.
     */
    @ElementCollection(fetch= FetchType.LAZY)
    @CollectionTable(name=TABLE_NAME + "_opetuskieli", joinColumns=@JoinColumn(name="perustiedot_id"))
    private Set<KoodistoKoodi> opetuskielis = new HashSet<KoodistoKoodi>();

    /**
     * Set of Koodisto uris, one for each "opetusmuoto" (form of teaching) provided.
     */
    @ElementCollection(fetch= FetchType.LAZY)
    @CollectionTable(name=TABLE_NAME + "_opetusmuoto", joinColumns=@JoinColumn(name="perustiedot_id"))
    private Set<KoodistoKoodi> opetusmuotos = new HashSet<KoodistoKoodi>();

    /**
     * Set of Koodisto uris, one for each "asiasanoitus" (a.k.a theme/teema) provided.
     */
    @ElementCollection(fetch= FetchType.LAZY)
    @CollectionTable(name=TABLE_NAME + "_asiasanoitus", joinColumns=@JoinColumn(name="perustiedot_id"))
    private Set<KoodistoKoodi> asiasanoituses = new HashSet<KoodistoKoodi>();

    /**
     *
     * @param koulutusKoodiUrl
     */
    public void setKoulutusKoodiUri(String koulutusKoodiUrl) {
        this.koulutusKoodiUri = koulutusKoodiUrl;
    }

    /**
     *
     * @return
     */
    public String getKoulutusKoodiUri() {
        return koulutusKoodiUri;
    }

    /**
     * Returns immutable, non null, set of "opetuskieli".
     *
     * @return
     */
    public Set<KoodistoKoodi> getOpetuskieletkielis() {
        return Collections.unmodifiableSet(opetuskielis);
    }

    /**
     * Adds new "opetuskieli"
     *
     * @param kieliUri non null koodisto uri to opetuskieli
     * @return true if item did not exist before
     */
    public boolean addOpetuskieli(String kieliUri) {
        return opetuskielis.add(new KoodistoKoodi(kieliUri));
    }

    /**
     *
     * @param kieliUri
     * @return true if given kieli existed and was removed
     */
    public boolean removeOpetuskieli(final String kieliUri) {
        return opetuskielis.remove(new KoodistoKoodi(kieliUri));
    }

    /**
     * Returns immutable, non null, set of "opetusmuoto".
     *
     * @return
     */
    public Set<KoodistoKoodi> getOpetusmuotos() {
        return Collections.unmodifiableSet(opetusmuotos);
    }

    /**
     *
     * @param opetusmuotoUri non-null uri to opetusmuoto
     * @return true if item did not exist before
     */
    public boolean addOpetusmuoto(final String opetusmuotoUri) {
        return opetusmuotos.add(new KoodistoKoodi(opetusmuotoUri));
    }

    /**
     *
     * @param opetusmuotoUri
     * @return true if given opetusmuoto existed and was removed
     */
    public boolean removeOpetusmuoto(final String opetusmuotoUri) {
        return opetusmuotos.remove(new KoodistoKoodi(opetusmuotoUri));
    }

    /**
     * Returns immutable set of "asiasanoitus", a.k.a "teema" in wire frame.
     *
     * @return
     */
    public Set<KoodistoKoodi> getAsiasanoituses() {
        return Collections.unmodifiableSet(asiasanoituses);
    }
    
    
    
    /**
     *
     * @param asiasanoitusUri
     * @return true if given item did not exist before
     */
    public boolean addAsiasanoitus(String asiasanoitusUri) {
        return asiasanoituses.add(new KoodistoKoodi(asiasanoitusUri));
    }

    /**
     *
     * @param asiasanoitusUri
     * @return true if given asiasanoitus existed and was removed
     */
    public boolean removeAsiasanoitus(String asiasanoitusUri) {
        return asiasanoituses.remove(new KoodistoKoodi(asiasanoitusUri));
    }
    
    
    public void setAsiasanoituses(Set<String> asiasanoituses) {
        
    }

}

