package fi.vm.sade.tarjonta.model.index;

import java.util.Date;

import com.mysema.query.annotations.QueryProjection;


public class HakuAikaIndexEntity {

    @QueryProjection
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
