
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

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

    /**
     * Gets the value of the koulutusTulos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the koulutusTulos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKoulutusTulos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KoulutuksetVastaus.KoulutusTulos }
     * 
     * 
     */
    public List<KoulutuksetVastaus.KoulutusTulos> getKoulutusTulos() {
        if (koulutusTulos == null) {
            koulutusTulos = new ArrayList<KoulutuksetVastaus.KoulutusTulos>();
        }
        return this.koulutusTulos;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Koulutus" type="{http://service.tarjonta.sade.vm.fi/types}KoulutusListausTyyppi"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    public static class KoulutusTulos implements Serializable
    {

        private final static long serialVersionUID = 100L;
        @XmlElement(name = "Koulutus", required = true)
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
