package fi.vm.sade.tarjonta.shared.amkouteDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmkouteOrgDTO {

    private String jarjestajaOid;
    private Date alkupvm;
    private Date loppupvm;
    private List<AmkouteMaarays> maaraykset = Lists.newArrayList();

    public String getJarjestajaOid() {
        return jarjestajaOid;
    }

    public void setJarjestajaOid(String oid) {
        this.jarjestajaOid = oid;
    }

    public Date getAlkupvm() {
        return alkupvm;
    }

    public void setAlkupvm(Date alkupvm) {
        this.alkupvm = alkupvm;
    }

    public Date getLoppupvm() {
        return loppupvm;
    }

    public void setLoppupvm(Date loppupvm) {
        this.loppupvm = loppupvm;
    }

    public List<AmkouteMaarays> getMaaraykset() {
        return maaraykset;
    }

    public void setMaaraykset(List<AmkouteMaarays> maaraykset) {
        this.maaraykset = maaraykset;
    }
}
