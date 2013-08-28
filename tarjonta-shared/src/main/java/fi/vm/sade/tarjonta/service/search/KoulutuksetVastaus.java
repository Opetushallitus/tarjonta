
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.tarjonta.service.types.KoulutusListausTyyppi;


public class KoulutuksetVastaus implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected List<KoulutuksetVastaus.KoulutusTulos> koulutusTulos;

    /**
     * Default no-arg constructor
     * 
     */
    public KoulutuksetVastaus() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public KoulutuksetVastaus(final List<KoulutuksetVastaus.KoulutusTulos> koulutusTulos) {
        this.koulutusTulos = koulutusTulos;
    }

    public List<KoulutuksetVastaus.KoulutusTulos> getKoulutusTulos() {
        if (koulutusTulos == null) {
            koulutusTulos = new ArrayList<KoulutuksetVastaus.KoulutusTulos>();
        }
        return this.koulutusTulos;
    }


    public static class KoulutusTulos implements Serializable
    {

        private final static long serialVersionUID = 100L;
        protected KoulutusListausTyyppi koulutus;

        /**
         * Default no-arg constructor
         * 
         */
        public KoulutusTulos() {
            super();
        }

        /**
         * Fully-initialising value constructor
         * 
         */
        public KoulutusTulos(final KoulutusListausTyyppi koulutus) {
            this.koulutus = koulutus;
        }

        /**
         * Gets the value of the koulutus property.
         * 
         * @return
         *     possible object is
         *     {@link KoulutusListausTyyppi }
         *     
         */
        public KoulutusListausTyyppi getKoulutus() {
            return koulutus;
        }

        /**
         * Sets the value of the koulutus property.
         * 
         * @param value
         *     allowed object is
         *     {@link KoulutusListausTyyppi }
         *     
         */
        public void setKoulutus(KoulutusListausTyyppi value) {
            this.koulutus = value;
        }

    }

}
