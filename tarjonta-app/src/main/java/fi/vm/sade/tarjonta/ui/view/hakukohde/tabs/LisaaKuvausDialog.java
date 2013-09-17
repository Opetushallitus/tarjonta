package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CacheUpdateException;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.ui.component.OphTokenField;
import fi.vm.sade.generic.ui.component.OphTokenField.SelectedTokenToTextFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.service.types.MonikielinenMetadataTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaWindow;
import fi.vm.sade.vaadin.Oph;

public class LisaaKuvausDialog extends TarjontaWindow {
	
	public static enum Mode {
		SORA,
		VAPE;
				
		public MetaCategory category() {
			return this==SORA ? MetaCategory.SORA_KUVAUS : MetaCategory.VALINTAPERUSTEKUVAUS;
		}		
	};

	private static final long serialVersionUID = 1L;
	private static final String WINDOW_HEIGHT = "580px";
	private static final String WINDOW_WIDTH = "864px";

	private final TarjontaPresenter presenter;
	
	private final Mode mode;
	
	private Map<String, Set<String>> modelData;

	// ui-komponentit
	private final CheckBox hakuLinkitys = new CheckBox();
	private final CheckBox hakuPohjaksi = new CheckBox();
	private final Table table = new Table();
	private final CheckBox tuoMuutKielet = new CheckBox();
	
	private final KoodistoComponent langChooser = new KoodistoComponent(KoodistoURI.KOODISTO_KIELI_URI){
		private static final long serialVersionUID = 1L;
		@Override
		protected List<KoodiType> loadOptions() {
			// filtteröidään käyttämättömät kielet pois
			Set<String> langs = new TreeSet<String>();
			for (Set<String> ss : getKuvaukset().values()) {
				langs.addAll(ss);
			}
			
			List<KoodiType> ret = new ArrayList<KoodiType>();
			for (KoodiType kt : super.loadOptions()) {
				String cu = kt.getKoodiUri()+"#"+kt.getVersio();
				if (langs.contains(cu)) {
					ret.add(kt);
				}
			}
			return ret;
		};
	};
	private final OphTokenField langList = new OphTokenField(){
		private static final long serialVersionUID = 1L;
		@Override
		protected boolean onNewTokenSeleted(Object tokenSelected) {
			System.out.println("Selected "+tokenSelected);
			return tokenSelected!=null;
		};
		@Override
		protected boolean onTokenDelete(Object selectedToken) {
			System.out.println("Unselected "+selectedToken);
			return true;
		};
	};
	
	private final Button importBtn = new Button();

	public LisaaKuvausDialog(TarjontaPresenter presenter, Mode mode) {
		super(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.presenter = presenter;
		this.mode = mode;
		
		langChooser.setField(new ComboBox());

		hakuLinkitys.setImmediate(true);
		hakuPohjaksi.setImmediate(true);
		tuoMuutKielet.setImmediate(true);
		langChooser.setImmediate(true);

		hakuLinkitys.setValue(true);
		langChooser.setEnabled(false);
		langList.setEnabled(false);

		hakuLinkitys.addListener(new CheckboxSwitcher(hakuLinkitys, hakuPohjaksi));
		hakuPohjaksi.addListener(new CheckboxSwitcher(hakuPohjaksi, hakuLinkitys));
		tuoMuutKielet.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				boolean enabled = (Boolean) tuoMuutKielet.getValue();
				langChooser.setEnabled(enabled);
				langList.setEnabled(enabled);
			}
		});
    }
	
	@Override
	protected String T(String key) {
		return super.T(mode+"."+key);
	}
	
	@Override
	protected String T(String key, Object... args) {
		return super.T(mode+"."+key, args);
	}

	@Override
	public void windowClose(CloseEvent e) {
		
	}
	
	private Map<String, Set<String>> getKuvaukset() {
		if (modelData==null) {
			modelData = new TreeMap<String, Set<String>>();
			
			for (MonikielinenMetadataTyyppi mt : presenter.haeMetadata(null, mode.category().toString())) {
				Set<String> s = modelData.get(mt.getAvain());
				if (s==null) {
					s = new TreeSet<String>();
					modelData.put(mt.getAvain(), s);
				}
				s.add(mt.getKieli());
			}
		}		
		return modelData;
	}

	@Override
	public void buildLayout(VerticalLayout layout) {
		setCaption(T(WINDOW_TITLE_PROPERTY));
		
		layout.setMargin(false,true,false,true);
		
		VerticalLayout valinta = new VerticalLayout();
		layout.addComponent(valinta);
		
		valinta.addComponent(new Label(super.T("valitseToiminto")));
		buildOption(valinta, T("haeSellaisenaan"), super.T("haeSellaisenaanOhje"), hakuLinkitys);
		buildOption(valinta, T("haePohjaksi"), super.T("haePohjaksiOhje"), hakuPohjaksi);
		
		table.setWidth("100%");	
		table.setHeight("200px");
				
		table.setContainerDataSource(new KuvausContainer(getKuvaukset()));
		table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
		table.setColumnHeader("title", T("kuvausRyhma")); // TODO i18n
		table.setColumnHeader("langs", super.T("kuvausKielet")); // TODO i18n
		table.setSortAscending(true);
		table.setVisibleColumns(new String[]{"title", "langs"});
		table.setSelectable(true);
		table.setImmediate(true);
		table.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				importBtn.setEnabled(table.getValue()!=null);
			}
		});

		try {
			layout.addComponent(table);
		} catch (CacheUpdateException e) {
			for (Throwable t : e.getCauses()) {
				t.printStackTrace(System.err);
			}
		}
		
		VerticalLayout muut = new VerticalLayout();
		muut.setWidth("100%");
		layout.addComponent(muut);
		
		tuoMuutKielet.setCaption(super.T("tuoMuutKuvaukset"));
		muut.addComponent(tuoMuutKielet);
		
		HorizontalLayout muut2 = new HorizontalLayout();
		muut.addComponent(muut2);
		muut2.setWidth("100%");
		muut2.setMargin(false, false, false, true);

		GridLayout langSels = new GridLayout(3,1);
		langList.setSelectionComponent(langChooser);
		langList.setSelectionLayout(langSels);
		langList.setFormatter(new SelectedTokenToTextFormatter() {
			@Override
			public String formatToken(Object selectedToken) {
				//System.out.println("Selected: "+selectedToken+" // "+(selectedToken==null ? null : selectedToken.getClass()));
				return presenter.getUiHelper().getKoodiNimi("kieli_"+String.valueOf(selectedToken).toLowerCase());
			}
		});

		Panel langPanel = new Panel();
		langPanel.addComponent(langSels);
		langPanel.setScrollable(true);
		langPanel.setHeight("100px");
		langPanel.setWidth("100%");
		
		muut2.addComponent(langChooser);
		muut2.setComponentAlignment(langChooser, Alignment.TOP_LEFT);
		muut2.addComponent(langPanel);
		muut2.setComponentAlignment(langPanel, Alignment.TOP_RIGHT);
		
		muut.addComponent(langList);

		((ComboBox)	langChooser.getField()).setInputPrompt(super.T("lisaaKieli"));
		
		Label ohje = new Label(T("ohje"));
		//ohje.addStyleName(Oph.LABEL_SMALL);
		layout.addComponent(ohje);
		
		HorizontalLayout btns = new HorizontalLayout();
		btns.setWidth("100%");
		layout.addComponent(btns);
		
		Button cancelBtn = new Button(super.T("peruuta"));
		btns.addComponent(cancelBtn);
		btns.setComponentAlignment(cancelBtn, Alignment.BOTTOM_LEFT);

		importBtn.setCaption(super.T("tuoTiedot"));
		importBtn.setEnabled(false);
		importBtn.addStyleName(Oph.BUTTON_DEFAULT);
		btns.addComponent(importBtn);
		btns.setComponentAlignment(importBtn, Alignment.BOTTOM_RIGHT);
		
		importBtn.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {

				System.out.println("LANGSELS = "+langList.getValue());
				
			}
		});
	}
	
	private CheckBox buildOption(VerticalLayout dst, String title, String descr, CheckBox cb) {
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setMargin(true, false, false, true);
		dst.addComponent(hl);

		hl.addComponent(cb);
		hl.setComponentAlignment(cb, Alignment.TOP_LEFT);
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(false, false, false, false);
		hl.addComponent(vl);
		
		vl.addComponent(new Label(title));
		
		Label dscr = new Label(descr);
		dscr.addStyleName(Oph.LABEL_SMALL);
		vl.addComponent(dscr);
		
		return cb;
	}
	
	private final class CheckboxSwitcher implements ClickListener {
		
		private static final long serialVersionUID = 1L;
		
		private final CheckBox checkbox;
		private final CheckBox other;
		
		public CheckboxSwitcher(CheckBox checkbox, CheckBox other) {
			super();
			this.checkbox = checkbox;
			this.other = other;
		}
		
		@Override
		public void buttonClick(ClickEvent event) {
			if ((Boolean) checkbox.getValue()) {
				other.setValue(false);
			}
		}

	}
	
	private final class KuvausContainer extends BeanContainer<String, KuvausRowModel> {

		private static final long serialVersionUID = 1L;

		public KuvausContainer(Map<String, Set<String>> data) throws IllegalArgumentException {
			super(KuvausRowModel.class);
			for (Map.Entry<String, Set<String>> e : data.entrySet()) {
				addItem(e.getKey(), new KuvausRowModel(e.getKey(), e.getValue()));
			}			
			addNestedContainerProperty("title");
			addNestedContainerProperty("langs");
		}
		
	}
	
	public final class KuvausRowModel {
		
		private final String title;
		private final String langs;
		
		public KuvausRowModel(String uri, Set<String> langs) {
			super();
			title = presenter.getUiHelper().getKoodiNimi(uri);
			
			StringBuilder s = new StringBuilder();
			for (String lc : langs) {
				if (s.length()>0) {
					s.append(", ");
				}
				s.append(presenter.getUiHelper().getKoodiNimi(lc));
			}
			this.langs = s.toString();
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getLangs() {
			return langs;
		}		
		
	}

}
