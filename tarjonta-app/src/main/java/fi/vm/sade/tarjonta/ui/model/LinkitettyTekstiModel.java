package fi.vm.sade.tarjonta.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * 
 * @author Timo Santasalo / Teknokala Ky
 */
public class LinkitettyTekstiModel {
	
	private final String uri;
	private final List<KielikaannosViewModel> kaannokset;
	
	public LinkitettyTekstiModel(String uri) {
		Preconditions.checkArgument(uri!=null);
		this.uri = uri;
		this.kaannokset = null;
	}

	public LinkitettyTekstiModel(List<KielikaannosViewModel> kaannokset) {
		Preconditions.checkArgument(kaannokset!=null);
		this.uri = null;
		this.kaannokset = kaannokset;
	}
	
	public LinkitettyTekstiModel(String url, List<KielikaannosViewModel> kaannokset) {
		super();
		this.uri = url;
		this.kaannokset = kaannokset;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(uri, kaannokset);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LinkitettyTekstiModel)) {
			return false;
		}
		LinkitettyTekstiModel o = (LinkitettyTekstiModel) obj;
		return Objects.equal(uri, o.uri) && Objects.equal(kaannokset, o.kaannokset);
	}

	public boolean isEmpty() {
		return uri==null && (kaannokset==null || kaannokset.isEmpty());
	}
	
	public LinkitettyTekstiModel() {
		this(new ArrayList<KielikaannosViewModel>());
	}
	
	public String getUri() {
		return uri;
	}
	
	public List<KielikaannosViewModel> getKaannokset() {
		return kaannokset;
	}
	
	public Set<String> getKielet() {
		if (kaannokset==null) {
			return Collections.emptySet();
		}
		Set<String> ret = new TreeSet<String>();
		for (KielikaannosViewModel kvm : kaannokset) {
			ret.add(kvm.getKielikoodi());
		}
		return ret;
	}

}
