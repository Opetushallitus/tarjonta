
/*
 *
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
package fi.vm.sade.tarjonta.ui.model;

import fi.vm.sade.tarjonta.service.types.KuvaTyyppi;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author Jani
 *
 */
public class TarjontaKuvaModel extends BaseUIViewModel {

    private static final long serialVersionUID = 4072449087761185821L;
    private String mimeType;
    private String fileName;
    private byte[] kuva;
    private KuvaTyyppi model;

    public TarjontaKuvaModel() {
    }

    public TarjontaKuvaModel(KuvaTyyppi model) {
        this.model = (model != null) ? model : new KuvaTyyppi();
        mimeType = this.model.getMimeType();
        fileName = this.model.getFileName();
        kuva = this.model.getKuva();
    }

    public KuvaTyyppi convertToDto() {
        model.setMimeType(mimeType);
        model.setFileName(fileName);
        model.setKuva(kuva);
        return model;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getKuva() {
        return kuva;
    }

    public void setKuva(byte[] kuva) {
        this.kuva = kuva;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        TarjontaKuvaModel other = (TarjontaKuvaModel) obj;

        EqualsBuilder builder = new EqualsBuilder();

        builder.append(mimeType, other.mimeType)
                .append(fileName, other.fileName)
                .append(kuva, other.kuva)
                .append(model, other.model);

        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this)
                .append(mimeType)
                .append(fileName)
                .append(kuva)
                .append(model)
                .toHashCode();
    }
}
