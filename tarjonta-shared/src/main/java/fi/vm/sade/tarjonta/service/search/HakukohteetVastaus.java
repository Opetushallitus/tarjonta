
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


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

    public List<HakukohteetVastaus.HakukohdeTulos> getHakukohdeTulos() {
        if (hakukohdeTulos == null) {
            hakukohdeTulos = new ArrayList<HakukohteetVastaus.HakukohdeTulos>();
        }
        return this.hakukohdeTulos;
    }


    public static class HakukohdeTulos implements Serializable
    {

        private final static long serialVersionUID = 100L;
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
