package fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.*;
import fi.vm.sade.generic.ui.component.GenericForm;
import fi.vm.sade.generic.ui.component.MultipleSelectToTableWrapper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliPerustiedotDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSearchDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;
import fi.vm.sade.tarjonta.widget.KoulutusmoduuliComponent;
import fi.vm.sade.tarjonta.widget.factory.TarjontaWidgetFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import java.util.Arrays;
import java.util.List;

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

/**
 *
 * @author Tuomas Katva
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = false)
public class KoulutusmoduuliToteutusEditView extends GenericForm<KoulutusmoduuliToteutusDTO> {

    public static final String KOODISTO_KIELI_URI = "http://kieli"; // TODO: tuleeko opetuskieli kieli-koodistosta, vai onko erikseen joku opetuskieli-koodisto?
    public static final String KOODISTO_OPETUSMUOTO_URI = "http://opetusmuoto"; // TODO: mistä koodistosta oikeasti tulee opetusmuoto?
    public static final String SUUNNITELTU_KESTO_URI = "http://suunniteltuKesto";
    public static final String KOULUTUSLAJI_URI = "http://koulutuslaji";

//    @Autowired
//    private KoulutusmoduuliAdminService koulutusmoduuliAdminService;
    private TarjontaWidgetFactory tarjontaWidgetFactory = TarjontaWidgetFactory.getInstance();

    private int komotoEditViewWidth =  760;
   
   private int komotoEditViewHeight = 560;
   
   private GridLayout rootLayout;
    
   private Button tallennaValmiinaBtn;
	
   private Button tallennaLuonnoksenaBtn;
	
   private Button peruutaBtn;
	
   @PropertyId("maksullisuus")
   private TextField maksullinenKoulutusTextfield;
	
   private CheckBox maksullinenKoulutusCheckbox;

    //@PropertyId("perustiedot.opetusmuotos") - nested properties cannot be bound with @PropertyId? doing bind manually
    private MultipleSelectToTableWrapper opetusmuotos;

   private Label opetusmuotoLabel;

    //@PropertyId("perustiedot.opetuskielis") - nested properties cannot be bound with @PropertyId? doing bind manually
    private MultipleSelectToTableWrapper opetuskielis;
	
   private Label opetuskieliLabel;
	
   private TextField organisaatioTextfield;
	
   private Label organisaatioLabel;
	
   @PropertyId("suunniteltuKestoUri")
   private KoodistoComponent suunniteltuKestoKoodisto;
	
   private Label suunniteltuKestoLabel;
	
   @PropertyId("koulutuslajiUri")
   private KoodistoComponent koulutusLajiKoodisto;
	
   private Label koulutusLajiLabel;
	
   private PopupDateField koulutuksenAlkamispvmDatefield;
	
   private Label koulutuksenAlkamisPvmLbl;

    @PropertyId("toteutettavaKoulutusmoduuliOID")
    private KoulutusmoduuliComponent koulutusmoduuliTextfield;
	
   private Label koulutusModuliLabel;
	
   private Label komotoEditTitle;
   
   private OptionGroup teematOptionGroup;
   
   private static final List<String> dummyTeemat = Arrays.asList(new String[] {
            "Sosiaaliala,yhteiskunta ja politiikka", "Kielet ja kulttuuri", "Talous, kauppa ja hallinta", "Terveys, hyvinvointi ja lääketiede", 
            "Kasvatus, opetus ja psykologia", "Biologia, kemia ja maantiede","Suojelu ja pelastus" });

    @Override
    protected void initFields() {
        buildRootLayout();
    }

    
   public KoulutusmoduuliToteutusEditView() {
       rootLayout = new GridLayout();
       model = new TutkintoOhjelmaToteutusDTO();
       // TODO: pitäiskö komotolla aina olla perustiedot, jolloin perustiedot voisi olla dto:ssa jo muuta kuin null?
       model.setPerustiedot(new KoulutusmoduuliPerustiedotDTO());
       initForm(model, rootLayout);
       form.getFooter().removeAllComponents();
       form.setImmediate(true);
       bind(model);
       setCompositionRoot(form);
   }
   
   private GridLayout buildRootLayout() {
		// common part: create layout
		
		rootLayout.setImmediate(false);
		rootLayout.setWidth(getKomotoEditViewWidth(), Sizeable.UNITS_PIXELS);
		rootLayout.setHeight(getKomotoEditViewHeight(), Sizeable.UNITS_PIXELS);
		rootLayout.setMargin(false);
		rootLayout.setColumns(4);
		rootLayout.setRows(8);
		
		// komotoEditTitle
		komotoEditTitle = new Label();
		komotoEditTitle.setImmediate(false);
		komotoEditTitle.setWidth("-1px");
		komotoEditTitle.setHeight("-1px");
		komotoEditTitle.setValue("Luo/muokkaa komoto");
		rootLayout.addComponent(komotoEditTitle, 0, 0);
		
		// koulutusModuliLabel
		koulutusModuliLabel = new Label();
		koulutusModuliLabel.setImmediate(false);
		koulutusModuliLabel.setWidth("-1px");
		koulutusModuliLabel.setHeight("-1px");
		koulutusModuliLabel.setValue("Koulutusmoduuli");
		rootLayout.addComponent(koulutusModuliLabel, 0, 1);
		
		// koulutusmoduuliTextfield
		//koulutusmoduuliTextfield = new TextField();
       KoulutusmoduuliSearchDTO searchSpecification = new KoulutusmoduuliSearchDTO();
       searchSpecification.setNimi(""); // with empty nimi we search all
       koulutusmoduuliTextfield = tarjontaWidgetFactory.createKoulutusmoduuliComponentWithCombobox(searchSpecification);
		koulutusmoduuliTextfield.setWidth("156px");
		koulutusmoduuliTextfield.setHeight("-1px");
		rootLayout.addComponent(koulutusmoduuliTextfield, 1, 1);
		
		// koulutuksenAlkamisPvmLbl
		koulutuksenAlkamisPvmLbl = new Label();
		koulutuksenAlkamisPvmLbl.setImmediate(false);
		koulutuksenAlkamisPvmLbl.setWidth("-1px");
		koulutuksenAlkamisPvmLbl.setHeight("-1px");
		koulutuksenAlkamisPvmLbl.setValue("Koulutuksen alkamispäivä");
		rootLayout.addComponent(koulutuksenAlkamisPvmLbl, 2, 1);
		
		// koulutuksenAlkamispvmDatefield
		koulutuksenAlkamispvmDatefield = new PopupDateField();
		koulutuksenAlkamispvmDatefield.setImmediate(false);
		koulutuksenAlkamispvmDatefield.setWidth("-1px");
		koulutuksenAlkamispvmDatefield.setHeight("-1px");
		koulutuksenAlkamispvmDatefield.setInvalidAllowed(false);
		rootLayout.addComponent(koulutuksenAlkamispvmDatefield, 3, 1);
		
		// koulutusLajiLabel
		koulutusLajiLabel = new Label();
		koulutusLajiLabel.setImmediate(false);
		koulutusLajiLabel.setWidth("-1px");
		koulutusLajiLabel.setHeight("-1px");
		koulutusLajiLabel.setValue("Koulutuslaji");
		rootLayout.addComponent(koulutusLajiLabel, 0, 2);
		
		// koulutusLajiCombobox
		koulutusLajiKoodisto = createKoodistoComponent(KOULUTUSLAJI_URI, "koulutlsajiId");
		rootLayout.addComponent(koulutusLajiKoodisto, 1, 2);
		
		// suunniteltuKestoLabel
		suunniteltuKestoLabel = new Label();
		suunniteltuKestoLabel.setImmediate(false);
		suunniteltuKestoLabel.setWidth("-1px");
		suunniteltuKestoLabel.setHeight("-1px");
		suunniteltuKestoLabel.setValue("Suunniteltu kesto");
		rootLayout.addComponent(suunniteltuKestoLabel, 2, 2);
		
		// suunniteltuKestoTextfield
		suunniteltuKestoKoodisto = createKoodistoComponent(SUUNNITELTU_KESTO_URI, "suunniteltuKestoId");
		rootLayout.addComponent(suunniteltuKestoKoodisto, 3, 2);
		
		// organisaatioLabel
		organisaatioLabel = new Label();
		organisaatioLabel.setImmediate(false);
		organisaatioLabel.setWidth("-1px");
		organisaatioLabel.setHeight("-1px");
		organisaatioLabel.setValue("Organisaatio");
		rootLayout.addComponent(organisaatioLabel, 0, 3);
		
		// organisaatioTextfield
		organisaatioTextfield = new TextField();
		organisaatioTextfield.setImmediate(false);
		organisaatioTextfield.setWidth("156px");
		organisaatioTextfield.setHeight("-1px");
		rootLayout.addComponent(organisaatioTextfield, 1, 3);
		
		// opetuskieliLabel
		opetuskieliLabel = new Label();
		opetuskieliLabel.setImmediate(false);
		opetuskieliLabel.setWidth("-1px");
		opetuskieliLabel.setHeight("-1px");
		opetuskieliLabel.setValue("Opetuskieli/-kielet");
		rootLayout.addComponent(opetuskieliLabel, 2, 3);

       // opetuskielis
       opetuskielis = createMultipleKoodiField(KOODISTO_KIELI_URI);
       rootLayout.addComponent(opetuskielis, 3, 3);

       // opetusmuotoLabel
		opetusmuotoLabel = new Label();
		opetusmuotoLabel.setImmediate(false);
		opetusmuotoLabel.setWidth("-1px");
		opetusmuotoLabel.setHeight("-1px");
		opetusmuotoLabel.setValue("Opetusmuoto");
		rootLayout.addComponent(opetusmuotoLabel, 0, 4);

       // opetusmuotos
       opetusmuotos = createMultipleKoodiField(KOODISTO_OPETUSMUOTO_URI);
       rootLayout.addComponent(opetusmuotos, 1, 4);

       // maksullinenKoulutusCheckbox
		maksullinenKoulutusCheckbox = new CheckBox();
		maksullinenKoulutusCheckbox.setCaption("Koulutus on maksullista");
		maksullinenKoulutusCheckbox.setImmediate(false);
		maksullinenKoulutusCheckbox.setWidth("-1px");
		maksullinenKoulutusCheckbox.setHeight("-1px");
		rootLayout.addComponent(maksullinenKoulutusCheckbox, 0, 5);
		
		// maksullinenKoulutusTextfield
		maksullinenKoulutusTextfield = new TextField();
		maksullinenKoulutusTextfield.setImmediate(false);
		maksullinenKoulutusTextfield.setWidth("-1px");
		maksullinenKoulutusTextfield.setHeight("-1px");
		rootLayout.addComponent(maksullinenKoulutusTextfield, 1, 5);
		
                
                teematOptionGroup = new OptionGroup("Teemat (1-3)",dummyTeemat);
                teematOptionGroup.setMultiSelect(true);
                rootLayout.addComponent(teematOptionGroup,0,6);        
                
                
		// peruutaBtn
		peruutaBtn = new Button();
		peruutaBtn.setCaption("Peruuta");
		peruutaBtn.setImmediate(false);
		peruutaBtn.setWidth("-1px");
		peruutaBtn.setHeight("-1px");
		rootLayout.addComponent(peruutaBtn, 1, 7);
		
		// tallennaLuonnoksenaBtn
		tallennaLuonnoksenaBtn = new Button();
		tallennaLuonnoksenaBtn.setCaption("Tallenna luonnoksena");
		tallennaLuonnoksenaBtn.setImmediate(false);
		tallennaLuonnoksenaBtn.setWidth("-1px");
		tallennaLuonnoksenaBtn.setHeight("-1px");
		rootLayout.addComponent(tallennaLuonnoksenaBtn, 2, 7);
		
		// tallennaValmiinaBtn
		tallennaValmiinaBtn = new Button();
		tallennaValmiinaBtn.setCaption("Tallenna valmiina");
		tallennaValmiinaBtn.setImmediate(false);
		tallennaValmiinaBtn.setWidth("-1px");
		tallennaValmiinaBtn.setHeight("-1px");
		rootLayout.addComponent(tallennaValmiinaBtn, 3, 7);
		
		return rootLayout;
	}

    private MultipleSelectToTableWrapper createMultipleKoodiField(String koodistoUri) {
        final MultipleSelectToTableWrapper field = WidgetFactory.createMultipleKoodiTableField(koodistoUri);
        final Table table = field.getTable();
        table.setVisibleColumns(new Object[]{"koodiArvo", MultipleSelectToTableWrapper.REMOVE_BUTTON});
        table.setPageLength(0); // this will achieve table size to shrink to content size
        table.setWidth("100%");
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN); // hide table header
        return field;
    }

    @Override
    public void bind(KoulutusmoduuliToteutusDTO model) {
        super.bind(model);

        // nested properties cannot be bound with @PropertyId? doing bind manually
        opetuskielis.setPropertyDataSource(new ObjectProperty(model.getPerustiedot().getOpetuskielis()));
        opetusmuotos.setPropertyDataSource(new ObjectProperty(model.getPerustiedot().getOpetusmuotos()));
        form.addField("perustiedot.opetuskielis", opetuskielis);
        form.addField("perustiedot.opetusmuotos", opetusmuotos);
    }

    @Override
    protected KoulutusmoduuliToteutusDTO save(KoulutusmoduuliToteutusDTO model) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the teematOptionGroup
     */
    public OptionGroup getTeematOptionGroup() {
        return teematOptionGroup;
    }

    /**
     * @param teematOptionGroup the teematOptionGroup to set
     */
    public void setTeematOptionGroup(OptionGroup teematOptionGroup) {
        this.teematOptionGroup = teematOptionGroup;
    }

    /**
     * @return the komoto
     */
    public KoulutusmoduuliToteutusDTO getKomoto() {
        return model;
    }

    /**
     * @return the komotoEditViewWidth
     */
    public int getKomotoEditViewWidth() {
        return komotoEditViewWidth;
    }

    /**
     * @param komotoEditViewWidth the komotoEditViewWidth to set
     */
    public void setKomotoEditViewWidth(int komotoEditViewWidth) {
        this.komotoEditViewWidth = komotoEditViewWidth;
    }

    /**
     * @return the komotoEditViewHeight
     */
    public int getKomotoEditViewHeight() {
        return komotoEditViewHeight;
    }

    /**
     * @param komotoEditViewHeight the komotoEditViewHeight to set
     */
    public void setKomotoEditViewHeight(int komotoEditViewHeight) {
        this.komotoEditViewHeight = komotoEditViewHeight;
    }
    
    public KoodistoComponent createKoodistoComponent(String koodistoUri, String debugId) {
        KoodistoComponent koodistoComponent = WidgetFactory.create(koodistoUri);
        //koodistoComponent.setCaption(I18N.getMessage(captionKey));
        ComboBox koodistoCombo = new ComboBox();
        koodistoComponent.setDebugId(debugId);
        koodistoCombo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        //koodistoCombo.setImmediate(true);
        koodistoComponent.setField(koodistoCombo);
        //layout.addComponent(koodistoComponent);
        return koodistoComponent;
    }

    public GridLayout getRootLayout() {
        return rootLayout;
    }

    public Button getTallennaValmiinaBtn() {
        return tallennaValmiinaBtn;
    }

    public Button getTallennaLuonnoksenaBtn() {
        return tallennaLuonnoksenaBtn;
    }

    public Button getPeruutaBtn() {
        return peruutaBtn;
    }

    public TextField getMaksullinenKoulutusTextfield() {
        return maksullinenKoulutusTextfield;
    }

    public CheckBox getMaksullinenKoulutusCheckbox() {
        return maksullinenKoulutusCheckbox;
    }

    public MultipleSelectToTableWrapper getOpetusmuotos() {
        return opetusmuotos;
    }

    public Label getOpetusmuotoLabel() {
        return opetusmuotoLabel;
    }

    public MultipleSelectToTableWrapper getOpetuskielis() {
        return opetuskielis;
    }

    public Label getOpetuskieliLabel() {
        return opetuskieliLabel;
    }

    public TextField getOrganisaatioTextfield() {
        return organisaatioTextfield;
    }

    public Label getOrganisaatioLabel() {
        return organisaatioLabel;
    }

    public KoodistoComponent getSuunniteltuKestoTextfield() {
        return suunniteltuKestoKoodisto;
    }

    public Label getSuunniteltuKestoLabel() {
        return suunniteltuKestoLabel;
    }

    public KoodistoComponent getKoulutusLajiKoodisto() {
        return koulutusLajiKoodisto;
    }

    public Label getKoulutusLajiLabel() {
        return koulutusLajiLabel;
    }

    public PopupDateField getKoulutuksenAlkamispvmDatefield() {
        return koulutuksenAlkamispvmDatefield;
    }

    public Label getKoulutuksenAlkamisPvmLbl() {
        return koulutuksenAlkamisPvmLbl;
    }

    public KoulutusmoduuliComponent getKoulutusmoduuliTextfield() {
        return koulutusmoduuliTextfield;
    }

    public Label getKoulutusModuliLabel() {
        return koulutusModuliLabel;
    }

    public Label getKomotoEditTitle() {
        return komotoEditTitle;
    }

    public static List<String> getDummyTeemat() {
        return dummyTeemat;
    }
}
