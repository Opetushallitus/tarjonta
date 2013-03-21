package fi.vm.sade.tarjonta.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fi.vm.sade.generic.model.BaseEntity;

@Entity
@Table(name = Kielivalikoima.TABLE_NAME)
public class Kielivalikoima extends BaseEntity {

    private static final long serialVersionUID = 3305481184717052756L;

    public static final String TABLE_NAME = "kielivalikoima";
    
    private String key;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_kieli", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> kielet = new HashSet<KoodistoUri>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    /**
     * Koodisto uri
     *
     * @return the kieliUri
     */
    public Set<KoodistoUri> getKielet() {
        return Collections.unmodifiableSet(kielet);
    }

    /**
     * @param kieliUri the kieliUri to set
     */
    public void addKieli(String kieliUri) {
        kielet.add(new KoodistoUri(kieliUri));
    }

    public void removeKieli(String kieliUri) {
        kielet.remove(new KoodistoUri(kieliUri));
    }

    public void setKielet(Collection<String> uris) {
        kielet.clear();
        for (String uri : uris) {
            kielet.add(new KoodistoUri(uri));
        }
    }
}
