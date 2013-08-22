
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class HakukohteetVastaus implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected List<HakukohteetVastaus.HakukohdeTulos> hakukohdeTulos;

    /**
     * Default no-arg constructor
     * 
     */
    public HakukohteetVastaus() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public HakukohteetVastaus(final List<HakukohteetVastaus.HakukohdeTulos> hakukohdeTulos) {
        this.hakukohdeTulos = hakukohdeTulos;
    }

    /**
     * Gets the value of the hakukohdeTulos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hakukohdeTulos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHakukohdeTulos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HakukohteetVastaus.HakukohdeTulos }
     * 
     * 
     */
    public List<HakukohteetVastaus.HakukohdeTulos> getHakukohdeTulos() {
        if (hakukohdeTulos == null) {
            hakukohdeTulos = new ArrayList<HakukohteetVastaus.HakukohdeTulos>();
        }
        return this.hakukohdeTulos;
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
     *         &lt;element name="Hakukohde" type="{http://service.tarjonta.sade.vm.fi/types}HakukohdeListausTyyppi"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    public static class HakukohdeTulos implements Serializable
    {

        private final static long serialVersionUID = 100L;
        @XmlElement(name = "Hakukohde", required = true)
        protected HakukohdeListaus hakukohde;

        /**
         * Default no-arg constructor
         * 
         */
        public HakukohdeTulos() {
            super();
        }

        /**
         * Fully-initialising value constructor
         * 
         */
        public HakukohdeTulos(final HakukohdeListaus hakukohde) {
            this.hakukohde = hakukohde;
        }

        /**
         * Gets the value of the hakukohde property.
         * 
         * @return
         *     possible object is
         *     {@link HakukohdeListaus }
         *     
         */
        public HakukohdeListaus getHakukohde() {
            return hakukohde;
        }

        /**
         * Sets the value of the hakukohde property.
         * 
         * @param value
         *     allowed object is
         *     {@link HakukohdeListaus }
         *     
         */
        public void setHakukohde(HakukohdeListaus value) {
            this.hakukohde = value;
        }

    }

}
