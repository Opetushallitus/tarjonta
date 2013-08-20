/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mlyly
 */
public class KoulutusSearchSpesificationViewModel extends BaseUIViewModel {

    private String searchStr;
    private List<String> organisaatioOids = new ArrayList<String>();
    private String koulutuksenAlkamiskausi;
    private int koulutuksenAlkamisvuosi;
    private String koulutuksenTila;
    private String hakukausi;
    
    
	public String getSearchStr() {
		return searchStr;
	}
	public void setSearchStr(String searchStr) {
		this.searchStr = searchStr;
	}
	
	public List<String> getOrganisaatioOids() {
		return organisaatioOids;
	}
	public void setOrganisaatioOids(List<String> organisaatioOids) {
		this.organisaatioOids = organisaatioOids;
	}
	public String getKoulutuksenAlkamiskausi() {
		return koulutuksenAlkamiskausi;
	}
	public void setKoulutuksenAlkamiskausi(String koulutuksenAlkamiskausi) {
		this.koulutuksenAlkamiskausi = koulutuksenAlkamiskausi;
	}
	public String getHakukausi() {
		return hakukausi;
	}
	public void setHakukausi(String hakukausi) {
		this.hakukausi = hakukausi;
	}
    public int getKoulutuksenAlkamisvuosi() {
        return koulutuksenAlkamisvuosi;
    }
    public void setKoulutuksenAlkamisvuosi(int koulutuksenAlkamisvuosi) {
        this.koulutuksenAlkamisvuosi = koulutuksenAlkamisvuosi;
    }


    public String getKoulutuksenTila() {
        return koulutuksenTila;
    }

    public void setKoulutuksenTila(String koulutuksenTila) {
        this.koulutuksenTila = koulutuksenTila;
    }
}
