package fi.vm.sade.tarjonta.model.index;

import java.util.Date;

public class HakuAikaIndexEntity {

    public HakuAikaIndexEntity(Date alkamisPvm, Date paattymisPvm) {
        this.alkamisPvm = alkamisPvm;
        this.paattymisPvm = paattymisPvm;
    }

    public Date getAlkamisPvm() {
        return alkamisPvm;
    }

    public Date getPaattymisPvm() {
        return paattymisPvm;
    }

    private final Date alkamisPvm;
    private final Date paattymisPvm;

}
