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
package fi.vm.sade.tarjonta.ui.view.haku;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Form;

import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.view.HakuPresenter;
import fi.vm.sade.tarjonta.ui.helper.I18NHelper;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;

/**
 * And editor for "Haku" object.
 *
 * @author mlyly
 * @see HakuViewModel the model that is bound this edit form, see the PropertyId annotations.
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class EditHakuViewImpl extends CustomComponent implements EditHakuView {

    private static final Logger LOG = LoggerFactory.getLogger(EditHakuViewImpl.class);

    public static final Object[] HAKUAJAT_COLUMNS = new Object[]{"kuvaus", "alkuPvm", "loppuPvm", "poistaB"};
    
    @Autowired(required = true)
    private HakuPresenter _presenter;

    private VerticalLayout _layout;
    @NotNull(message="{validation.Haku.hakutyyppiNull}")
    @PropertyId("hakutyyppi")
    private KoodistoComponent _hakutyyppi;
    
    @NotNull(message="{validation.Haku.hakukausiNull}")
    @PropertyId("hakukausi")
    private KoodistoComponent _hakukausi;
    
    @NotNull(message="{validation.Haku.hakuvuosiNull}")
    @PropertyId("hakuvuosi")
    private TextField _hakuvuosi;
    @NotNull(message="{validation.Haku.koulutuksenAlkamisKausiNull}")
    @PropertyId("koulutuksenAlkamisKausi")
    private KoodistoComponent _koulutusAlkamiskausi;
    
    @NotNull(message="{validation.Haku.koulutuksenAlkamisVuosiNull}")
    @PropertyId("koulutuksenAlkamisvuosi")
    private TextField koulutuksenAlkamisvuosi;
    
    @NotNull(message="{validation.Haku.kohdejoukkoNull}")
    @PropertyId("haunKohdejoukko")
    private KoodistoComponent _hakuKohdejoukko;
    
    @NotNull(message="{validation.Haku.hakutapaNull}")
    @PropertyId("hakutapa")
    private KoodistoComponent _hakutapa;
    
    @PropertyId("nimiFi")
    private TextField _haunNimiFI;
    @PropertyId("nimiSe")
    private TextField _haunNimiSE;
    @PropertyId("nimiEn")
    private TextField _haunNimiEN;
    @PropertyId("haunTunniste")
    private Label _haunTunniste;
    
    // TODO hakuaika
    @PropertyId("alkamisPvm")
    private DateField hakuAlkaa;
    @PropertyId("paattymisPvm")
    private DateField hakuLoppuu;
    
    private OptionGroup sisHakuajat;
    
    private Table sisaisetHakuajatTable;
    private HakuajatContainer sisaisetHakuajatContainer;
    
    private Button lisaaHakuaika;
    
    
    @PropertyId("haussaKaytetaanSijoittelua")
    private CheckBox _kaytetaanSijoittelua;
    @PropertyId("kaytetaanJarjestelmanHakulomaketta")
    private CheckBox _kayteaanJarjestelmanHakulomaketta;
    @PropertyId("hakuLomakeUrl")
    private TextField _muuHakulomakeUrl;
    @Value("${koodisto-uris.hakutyyppi:HAKUTYYPPI}")
    private String _koodistoUriHakutyyppi;
    @Value("${koodisto-uris.hakukausi:KAUSI}")
    private String _koodistoUriHakukausi;
    @Value("${koodisto-uris.koulutuksenAlkamiskausi:KAUSI}")
    private String _koodistoUriAlkamiskausi;
    @Value("${koodisto-uris.haunKohdejoukko:KOULUTUSRYHMÄ}")
    private String _koodistoUriKohdejoukko;
    @Value("${koodisto-uris.hakutapa:HAKUTAPA}")
    private String _koodistoUriHakutapa;
    private I18NHelper _i18n = new I18NHelper(this);
    private Form form;
    
    private ErrorMessage errorView;
    
    private boolean attached = false;

    public EditHakuViewImpl() {
        super();
        _presenter.setEditHaku(this);
        HakuViewModel haku = new HakuViewModel();
        initialize(haku);
    }

    public EditHakuViewImpl(HakuViewModel model) {
        _presenter.setEditHaku(this);

        initialize(model);
    }

    @Override
    public void attach() {
        LOG.debug("attach()");
        super.attach();
        if (attached) {
        	return;
        }
        attached = true;
        this.sisaisetHakuajatContainer = new HakuajatContainer(_presenter.getHakuModel().getSisaisetHakuajat());
        this.sisaisetHakuajatTable.setContainerDataSource(this.sisaisetHakuajatContainer);
        this.sisaisetHakuajatTable.setVisibleColumns(HAKUAJAT_COLUMNS);
        this.sisaisetHakuajatTable.setPageLength((this.sisaisetHakuajatContainer.size() > 5) ? this.sisaisetHakuajatContainer.size() : 5);
        this.sisaisetHakuajatTable.setColumnHeaders(new String[]{_i18n.getMessage("Kuvaus"), 
        														_i18n.getMessage("Alkupvm"), 
        														_i18n.getMessage("Loppupvm"),
        														_i18n.getMessage("Poista")});
        
        if (_presenter.getHakuModel().getSisaisetHakuajat().size() > 0) {
        	this.sisHakuajat.setValue(_i18n.getMessage("sisHakuajat"));
        } else {
        	this.sisHakuajat.setValue(_i18n.getMessage("yksiHakuaika"));
        }
    }

    @Override
    public void initialize(HakuViewModel hakuViewModel) {
        LOG.info("inititialize()");

       

        // Clean up old components if any
        if (_layout != null) {
            _layout.removeAllComponents();
        }

        // Create root layout for this component
        _layout = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        setCompositionRoot(_layout);

        //
        // Init fields
        //

        _hakutyyppi = UiBuilder.koodistoComboBox(null,_koodistoUriHakutyyppi, null, null, T("Hakutyyppi.prompt"));
        _hakutyyppi.setSizeUndefined();
        _hakukausi = UiBuilder.koodistoComboBox(null,_koodistoUriHakukausi, null, null, T("Hakukausi.prompt"));
        _hakukausi.setSizeUndefined();
        _hakuvuosi = UiUtil.textField(null, "", T("Hakuvuosi.prompt"), false);
        _hakuvuosi.setSizeUndefined();
        _koulutusAlkamiskausi =UiBuilder.koodistoComboBox(null,_koodistoUriAlkamiskausi, null, null, T("KoulutuksenAlkamiskausi.prompt"));
        _koulutusAlkamiskausi.setSizeUndefined();
         koulutuksenAlkamisvuosi = UiUtil.textField(null, "", T("KoulutuksenAlkamisvuosi.prompt"), false);
         koulutuksenAlkamisvuosi.setSizeUndefined();
        _hakuKohdejoukko = UiBuilder.koodistoComboBox(null,_koodistoUriKohdejoukko, null, null, T("HakuKohdejoukko.prompt"));
        _hakuKohdejoukko.setSizeUndefined();
        _hakutapa = UiBuilder.koodistoComboBox(null,_koodistoUriHakutapa, null, null, T("Hakutapa.prompt"));
        _hakutapa.setSizeUndefined();
        _haunNimiFI = UiUtil.textField(null, "", T("HaunNimiFI.prompt"), false);
        _haunNimiFI.setSizeUndefined();
        _haunNimiSE = UiUtil.textField(null, "", T("HaunNimiSE.prompt"), false);
        _haunNimiSE.setSizeUndefined();
        _haunNimiEN = UiUtil.textField(null, "", T("HaunNimiEN.prompt"), false);
        _haunNimiEN.setSizeUndefined();
        _haunTunniste = UiUtil.label((AbstractLayout) null, hakuViewModel.getHaunTunniste());
        _haunTunniste.setSizeUndefined();
        // TODO hakuaika
        _kaytetaanSijoittelua = UiUtil.checkbox(null, T("KaytetaanSijoittelua"));
        _kaytetaanSijoittelua.setSizeUndefined();
        _kayteaanJarjestelmanHakulomaketta = UiUtil.checkbox(null, T("KaytetaanJarjestemanHakulomaketta"));
        _kayteaanJarjestelmanHakulomaketta.setSizeUndefined();
        _kayteaanJarjestelmanHakulomaketta.setImmediate(true);
        _kayteaanJarjestelmanHakulomaketta.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    LOG.debug("Value change for kaytetaan jarjestelman hakulomaketta.");
                    _muuHakulomakeUrl.setEnabled(!_kayteaanJarjestelmanHakulomaketta.booleanValue());
                }
            });
        _muuHakulomakeUrl = UiUtil.textField(null, "", T("MuuHakulomake.prompt"), false);
        _muuHakulomakeUrl.setSizeUndefined();

        createButtonBar(_layout);

        UiUtil.label(_layout, T("HaunTiedot"), LabelStyleEnum.H2);
        UiUtil.hr(_layout);

        GridLayout grid = new GridLayout(3, 1);
        grid.setSpacing(true);
        grid.setSizeUndefined();
        _layout.addComponent(grid);

        grid.addComponent(UiUtil.label(null, T("Hakutyyppi")));
        grid.addComponent(_hakutyyppi);
        grid.newLine();

        {
            grid.addComponent(UiUtil.label(null, T("HakukausiJaVuosi")));
            HorizontalLayout hl = UiUtil.horizontalLayout();
            hl.setSizeUndefined();
            hl.setSpacing(true);
            hl.addComponent(_hakukausi);
            hl.addComponent(_hakuvuosi);
            grid.addComponent(hl);
            grid.newLine();
        }

        {
            grid.addComponent(UiUtil.label(null, T("KoulutuksenAlkamiskausi")));
            HorizontalLayout hl = UiUtil.horizontalLayout();
            hl.setSizeUndefined();
            hl.setSpacing(true);
            hl.addComponent(_koulutusAlkamiskausi);
            hl.addComponent(koulutuksenAlkamisvuosi);
            grid.addComponent(hl);
            grid.newLine();
        }

        grid.addComponent(UiUtil.label(null, T("HakuKohdejoukko")));
        grid.addComponent(_hakuKohdejoukko);
        grid.newLine();

        grid.addComponent(UiUtil.label(null, T("Hakutapa")));
        grid.addComponent(_hakutapa);
        grid.newLine();

        {
            grid.addComponent(UiUtil.label(null, T("HaunNimi")));
            VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.NONE);
            vl.setSizeUndefined();

            vl.addComponent(_haunNimiFI);
            vl.addComponent(_haunNimiSE);
            vl.addComponent(_haunNimiEN);
            grid.addComponent(vl);
            grid.newLine();
        }

        grid.addComponent(UiUtil.label(null, T("HaunTunniste")));
        grid.addComponent(_haunTunniste);
        grid.newLine();

        {
            grid.addComponent(UiUtil.label(null, T("Hakuaika")));

            VerticalLayout vl = UiUtil.verticalLayout();
            vl.setSizeUndefined();

            
            
            //vl.addComponent(UiUtil.checkbox(null, "Yksi hakuaika"));
            sisHakuajat = new OptionGroup();
            sisHakuajat.addItem(_i18n.getMessage("yksiHakuaika"));
            sisHakuajat.addItem(_i18n.getMessage("sisHakuajat"));
            sisHakuajat.setMultiSelect(false);
            //sisHakuajat = UiUtil.//checkbox(null, _i18n.getMessage("sisHakuajat"));
            sisHakuajat.setImmediate(true);
            sisHakuajat.addListener(new Property.ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					sisaisetHakuajatTable.setEnabled(sisHakuajat.getValue().equals(_i18n.getMessage("sisHakuajat")));
					lisaaHakuaika.setEnabled(sisHakuajat.getValue().equals(_i18n.getMessage("sisHakuajat")));
					hakuAlkaa.setEnabled(sisHakuajat.getValue().equals(_i18n.getMessage("yksiHakuaika")));
					hakuLoppuu.setEnabled(sisHakuajat.getValue().equals(_i18n.getMessage("yksiHakuaika")));
				}
            });
            
            vl.addComponent(sisHakuajat);

            HorizontalLayout hl = UiUtil.horizontalLayout();
            hl.setSizeUndefined();
            vl.addComponent(hl);
            
            hakuAlkaa = UiUtil.dateField();

            hl.addComponent(hakuAlkaa);
            hl.addComponent(UiUtil.label(null, "-"));
            
            this.hakuLoppuu = UiUtil.dateField();
            hl.addComponent(hakuLoppuu);
            
            this.sisaisetHakuajatTable = new Table();
            this.sisaisetHakuajatTable.setEditable(true);
            
            lisaaHakuaika = UiUtil.buttonSmallPlus(vl, _i18n.getMessage("LisaaHakuaika"), new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					sisaisetHakuajatContainer.addRowToHakuajat();
				}
			});
            vl.addComponent(sisaisetHakuajatTable);

            grid.addComponent(vl);
            grid.newLine();
        }

        grid.space();
        grid.addComponent(_kaytetaanSijoittelua);
        grid.newLine();

        {
            grid.addComponent(UiUtil.label(null, T("Hakulomake")));
            VerticalLayout vl = UiUtil.verticalLayout();
            vl.setSizeUndefined();
            vl.setSpacing(true);
            vl.addComponent(_kayteaanJarjestelmanHakulomaketta);
            vl.addComponent(_muuHakulomakeUrl);
            grid.addComponent(vl);
            grid.newLine();
        }

        createButtonBar(_layout);
        grid.setColumnExpandRatio(1, 1);
        grid.setColumnExpandRatio(2, 5);
        
        BeanItem<HakuViewModel> hakuBean = new BeanItem<HakuViewModel>(hakuViewModel);
        form = new ValidatingViewBoundForm(this);
        form.setItemDataSource(hakuBean);
        _presenter.setHakuViewModel(hakuViewModel);
        
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        this.form.setValidationVisible(false);
        this.form.setValidationVisibleOnCommit(false);
    }

     /**
     * Top and botton button bars.
     * Buttons are bound to send events defined in this class (SaveEvent, DeleteEvent etc.)
     *
     * @param layout
     * @return
     */
    private VerticalLayout createButtonBar(VerticalLayout layout) {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.NONE);
        vl.setSizeUndefined();
        if (layout != null) {
            layout.addComponent(vl);
        }
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);

        Button btnCancel = UiUtil.buttonSmallSecodary(hl, T("Peruuta"));
        btnCancel.addStyleName(Oph.CONTAINER_SECONDARY);
        btnCancel.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                fireEvent(new CancelEvent(EditHakuViewImpl.this));
            }
        });

        Button btnSaveUncomplete = UiUtil.buttonSmallPrimary(hl, T("TallennaLuonnoksena"));
        btnSaveUncomplete.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                fireEvent(new SaveEvent(EditHakuViewImpl.this, false));

            }
        });

        Button btnSaveComplete = UiUtil.buttonSmallPrimary(hl, T("TallennaValmiina"));
        btnSaveComplete.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                fireEvent(new SaveEvent(EditHakuViewImpl.this, true));

            }
        });

        Button btnContinue = UiUtil.buttonSmallPrimary(hl, T("Jatka"));
        btnContinue.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (_presenter.getHakuModel().getHakuOid() != null) {
                    fireEvent(new ContinueEvent(EditHakuViewImpl.this));
                } else {
                    getWindow().showNotification(_i18n.getMessage("TallennaEnsin"));
                }
            }
        });

        hl.setSizeUndefined();
        hl.setComponentAlignment(btnCancel, Alignment.TOP_LEFT);
        hl.setComponentAlignment(btnSaveUncomplete, Alignment.TOP_LEFT);
        hl.setComponentAlignment(btnSaveComplete, Alignment.TOP_LEFT);
        hl.setComponentAlignment(btnContinue, Alignment.TOP_LEFT);
        vl.addComponent(hl);
        if (errorView == null) {
            errorView = new ErrorMessage();
        }
        vl.addComponent(errorView);
        return vl;
    }

    /**
     * Translator helper. Makes code so much more hip... and shorter.
     *
     * Its using I18NHelper so the actual translation key will be deducted like this:
     *
     * T("Jatka") becomes translation value for "EditHakuViewImpl.Jatka".
     *
     * @param key
     * @return
     */
    private String T(String key) {
        return _i18n.getMessage(key);
    }

    /*
     * Component events emitted when buttons are pressed.
     */

    /**
     * Fired when save is pressed.
     */
    public class CancelEvent extends Component.Event {

        public CancelEvent(Component source) {
            super(source);

        }
    }

    /**
     * Fired when save is pressed.
     */
    public class SaveEvent extends Component.Event {

        boolean _complete = false;

        public SaveEvent(Component source, boolean complete) {
            super(source);
            sisaisetHakuajatContainer.bindHakuajat();
            if (sisHakuajat.getValue().equals(_i18n.getMessage("yksiHakuaika"))) {
            	_presenter.getHakuModel().setSisaisetHakuajat(new ArrayList<HakuaikaViewModel>());
            } else {
            	_presenter.getHakuModel().setAlkamisPvm(null);
            	_presenter.getHakuModel().setPaattymisPvm(null);
            }
            _complete = complete;
            if (_presenter.getHakuModel().isKaytetaanJarjestelmanHakulomaketta()) {
                _presenter.getHakuModel().setHakuLomakeUrl(null);
            }
            errorView.resetErrors();
            try {
                form.commit();
            
                if (complete) {
                    _presenter.saveHakuValmiina();
                    getWindow().showNotification(_i18n.getMessage("HakuTallennettuValmiina"));
                } else {
                    _presenter.saveHakuLuonnoksenaModel();
                 getWindow().showNotification(_i18n.getMessage("HakuTallennettuLuonnoksena"));
                }
            } catch (Validator.InvalidValueException e) {
                errorView.addError(e);
            }

        }

        public boolean isComplete() {
            return _complete;
        }
    }

    /**
     * Fired when delete is pressed.
     */
    public class DeleteEvent extends Component.Event {

        public DeleteEvent(Component source) {
            super(source);

        }
    }

    /**
     * Fired when Continue is pressed.
     */
    public class ContinueEvent extends Component.Event {

        public ContinueEvent(Component source) {
            super(source);
        }
    }
    
    public class HakuajatContainer extends BeanItemContainer<HakuajatView> implements Serializable {
    	
    	public HakuajatContainer(List<HakuaikaViewModel> hakuajat) {
    		super(HakuajatView.class);
    		
    		initHakuaikaContainer(hakuajat);
    	}
    	
    	public void addRowToHakuajat() {
    		final HakuajatView hakuaikaRow = new HakuajatView(new HakuaikaViewModel());
			hakuaikaRow.getPoistaB().addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					removeItem(hakuaikaRow);
				}
			});
			addItem(hakuaikaRow);
    	}
    	
    	public void bindHakuajat() {
    		List<HakuaikaViewModel> hakuajat = new ArrayList<HakuaikaViewModel>();
    		for (HakuajatView curRow : this.getItemIds()) {
    			hakuajat.add(curRow.getModel());
    		}
    		_presenter.getHakuModel().setSisaisetHakuajat(hakuajat);
    	}
    	
    	private void initHakuaikaContainer(List<HakuaikaViewModel> hakuajat) {
    		if (hakuajat == null || hakuajat.size() == 0) {
    			addRowToHakuajat();
    		}
    		
    		for (HakuaikaViewModel curHakuaika : hakuajat) {
    			final HakuajatView hakuaikaRow = new HakuajatView(curHakuaika);
    			hakuaikaRow.getPoistaB().addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						removeItem(hakuaikaRow);
					}
				});
    			addItem(hakuaikaRow);
    		}
    	}
    	
    }

}
