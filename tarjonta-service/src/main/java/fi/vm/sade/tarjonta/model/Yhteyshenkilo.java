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

import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Yhteyshenkilo's are always maintained in Henkilo service.
 */
@Entity
@Table(name = Yhteyshenkilo.TABLE_NAME)
public class Yhteyshenkilo extends TarjontaBaseEntity {

    public static final String TABLE_NAME = "yhteyshenkilo";
    private static final long serialVersionUID = -1434499440678133630L;
    private static final String KIELI_SEPARATOR = ",";
    @Column(name = "etunimis", nullable = false)
    private String etunimis;
    @Column(name = "sukunimi", nullable = false)
    private String sukunimi;
    @Column(name = "sahkoposti")
    private String sahkoposti;
    @Column(name = "puhelin")
    private String puhelin;
    @Column(name = "kielis")
    private String kielis;
    @Column(name = "henkilo_oid")
    private String henkioOid;
    @Column(name = "titteli")
    private String titteli;
    @Enumerated(EnumType.STRING)
    @Column(name = "tyyppi")
    private HenkiloTyyppi henkiloTyyppi;
    public Yhteyshenkilo() {
    }

    public Yhteyshenkilo(String henkioOid, String... kieli) {
        this.henkioOid = henkioOid;
        setKielis(kieli);
    }

    public final void setKielis(Collection<String> kielis) {

        if (kielis == null) {
            this.kielis = null;
            return;
        }

        String[] kieliArray = new String[kielis.size()];
        setKielis(kielis.toArray(kieliArray));

    }

    public final void setKielis(String... kieli) {

        if (kieli == null || kieli.length == 0) {
            kielis = null;
        }

        kielis = StringUtils.join(formatKielis(kieli), KIELI_SEPARATOR);

    }

    public String[] getKielis() {
        return StringUtils.split(kielis, KIELI_SEPARATOR);
    }

    public String getHenkioOid() {
        return henkioOid;
    }

    public void setHenkioOid(String henkioOid) {
        this.henkioOid = henkioOid;
    }

    public String getEtunimis() {
        return etunimis;
    }

    public void setEtunimis(String etunimis) {
        this.etunimis = etunimis;
    }

    public String getSahkoposti() {
        return sahkoposti;
    }

    public void setSahkoposti(String sahkoposti) {
        this.sahkoposti = sahkoposti;
    }

    public String getSukunimi() {
        return sukunimi;
    }

    public void setSukunimi(String sukunimi) {
        this.sukunimi = sukunimi;
    }

    public String getPuhelin() {
        return puhelin;
    }

    public void setPuhelin(String puhelin) {
        this.puhelin = puhelin;
    }

    public String getTitteli() {
        return titteli;
    }

    public void setTitteli(String titteli) {
        this.titteli = titteli;
    }

    private static String[] formatKielis(String[] kielis) {

        String[] formatted = new String[kielis.length];

        for (int i = 0; i < kielis.length; i++) {
            final String kieli = kielis[i].trim();
            formatted[i] = kieli;
        }

        return formatted;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Yhteyshenkilo other = (Yhteyshenkilo) obj;
        return new EqualsBuilder().append(henkioOid, other.henkioOid).append(henkiloTyyppi, other.henkiloTyyppi).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(henkioOid).append(henkiloTyyppi).toHashCode();
    }

    /**
     * @return the henkiloTyyppi
     */
    public HenkiloTyyppi getHenkiloTyyppi() {
        return henkiloTyyppi;
    }

    /**
     * @param henkiloTyyppi the henkiloTyyppi to set
     */
    public void setHenkiloTyyppi(HenkiloTyyppi henkiloTyyppi) {
        this.henkiloTyyppi = henkiloTyyppi;
    }
}
