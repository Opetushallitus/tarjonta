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

package fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.*;
import fi.vm.sade.generic.ui.component.GenericForm;
import fi.vm.sade.generic.ui.component.MultipleSelectToTableWrapper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.organisaatio.ui.widgets.OrganisaatioSearchType;
import fi.vm.sade.organisaatio.ui.widgets.OrganisaatioSearchWidget;
import fi.vm.sade.organisaatio.ui.widgets.factory.OrganisaatioWidgetFactory;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliPerustiedotDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSearchDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import fi.vm.sade.tarjonta.widget.KoulutusmoduuliComponent;
import fi.vm.sade.tarjonta.widget.factory.TarjontaWidgetFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Tuomas Katva
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = false)
public class KoulutusmoduuliToteutusEditView extends GenericForm<KoulutusmoduuliToteutusDTO> {

    public static final String KOODISTO_KIELI_URI = "http://kieli"; // TODO: tuleeko opetuskieli kieli-koodistosta, vai onko erikseen joku opetuskieli-koodisto?
    public static final String KOODISTO_OPETUSMUOTO_URI = "http://opetusmuoto"; // TODO: mistä koodistosta oikeasti tulee opetusmuoto?
    public static final String SUUNNITELTU_KESTO_URI = "http://suunniteltuKesto";
    public static final String KOULUTUSLAJI_URI = "http://koulutuslaji";
    public static final String TEEMA_URI = "http://teema";

    //    @Autowired
//    private KoulutusmoduuliAdminService koulutusmoduuliAdminService;
    private TarjontaWidgetFactory tarjontaWidgetFactory = TarjontaWidgetFactory.getInstance();

    private int komotoEditViewWidth = 860;

    private int komotoEditViewHeight = 660;

    private CustomLayout rootLayout;

    private Button tallennaValmiinaBtn;

    private Button tallennaLuonnoksenaBtn;

    private Button peruutaBtn;

    @PropertyId("maksullisuus")
    private TextField maksullinenKoulutusTextfield;

    private CheckBox maksullinenKoulutusCheckbox;

    //@PropertyId("perustiedot.opetusmuotos") - nested properties cannot be bound with @PropertyId? doing bind manually
    private MultipleSelectToTableWrapper opetusmuotos;

    //@PropertyId("perustiedot.opetuskielis") - nested properties cannot be bound with @PropertyId? doing bind manually
    private MultipleSelectToTableWrapper opetuskielis;

   private OrganisaatioSearchWidget organisaatioField;

    //@PropertyId("suunniteltuKestoUri")
    private KoodistoComponent suunniteltuKestoKoodisto;

    @PropertyId("koulutuslajiUri")
    private KoodistoComponent koulutusLajiKoodisto;

    private PopupDateField koulutuksenAlkamispvmDatefield;

    @PropertyId("toteutettavaKoulutusmoduuliOID")
    private KoulutusmoduuliComponent koulutusmoduuliTextfield;

    //@PropertyId("teemaUris")
    private KoodistoComponent teemat;
    private OptionGroup teematOptionGroup;

    private static final I18NHelper i18n = new I18NHelper("KoulutusmoduuliToteutusEditView.");

    /*
    private static final List<String> dummyTeemat = Arrays.asList(new String[] {
            "Sosiaaliala,yhteiskunta ja politiikka", "Kielet ja kulttuuri", "Talous, kauppa ja hallinta", "Terveys, hyvinvointi ja lääketiede",
            "Kasvatus, opetus ja psykologia", "Biologia, kemia ja maantiede","Suojelu ja pelastus" });
    */

    @Override
    protected void initFields() {
        buildRootLayout();
    }

    public static CustomLayout getCustomLayout(String layoutPath) {
        CustomLayout customLayout = null;
        try {
            customLayout = new CustomLayout(Thread.currentThread().getContextClassLoader().getResourceAsStream(layoutPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return customLayout;
    }

    protected final String getCustomLayoutPath() {
        return "layouts/KomotoEdit.html";
    }


    protected String getCaptionForString(String key) {
        String retval = i18n.getMessage(key);
        if (retval == null || retval.length() < 1) {
            return key;
        } else {
            return retval;
        }
    }

    public KoulutusmoduuliToteutusEditView() {
        rootLayout = getCustomLayout(getCustomLayoutPath());
        model = new TutkintoOhjelmaToteutusDTO();
        // TODO: pitäiskö komotolla aina olla perustiedot, jolloin perustiedot voisi olla dto:ssa jo muuta kuin null?
        model.setPerustiedot(new KoulutusmoduuliPerustiedotDTO());
        initForm(model, rootLayout);

        form.setImmediate(true);
        bind(model);
        setCompositionRoot(form);
        getButtonSave().setCaption(getCaptionForString("tallennaValmiina"));

    }

    private CustomLayout buildRootLayout() {
        // common part: create layout

        rootLayout.setImmediate(false);


        // koulutusmoduuliTextfield
        //koulutusmoduuliTextfield = new TextField();
        KoulutusmoduuliSearchDTO searchSpecification = new KoulutusmoduuliSearchDTO();
        searchSpecification.setNimi(""); // with empty nimi we search all
        koulutusmoduuliTextfield = tarjontaWidgetFactory.createKoulutusmoduuliComponentWithCombobox(searchSpecification);
        koulutusmoduuliTextfield.setCaption(SAVE_SUCCESSFUL);
        koulutusmoduuliTextfield.setCaption(getCaptionForString("koulutusmoduuli"));
        rootLayout.addComponent(koulutusmoduuliTextfield, "koulutusmoduuli");

        // koulutuksenAlkamispvmDatefield
        koulutuksenAlkamispvmDatefield = new PopupDateField();
        koulutuksenAlkamispvmDatefield.setImmediate(false);
        koulutuksenAlkamispvmDatefield.setCaption(getCaptionForString("koulutuksenAlkamispvm"));
        koulutuksenAlkamispvmDatefield.setInvalidAllowed(false);
        rootLayout.addComponent(koulutuksenAlkamispvmDatefield, "koulutuksenAlkamispvm");

        // koulutusLajiCombobox
        koulutusLajiKoodisto = createKoodistoComponent(KOULUTUSLAJI_URI, "koulutlsajiId");
        rootLayout.addComponent(koulutusLajiKoodisto, "koulutuslaji");


        // suunniteltuKestoTextfield
        suunniteltuKestoKoodisto = createKoodistoComponent(SUUNNITELTU_KESTO_URI, "suunniteltuKestoId");
        rootLayout.addComponent(suunniteltuKestoKoodisto, "suunniteltuKesto");

        // organisaatioTextfield
        organisaatioField = OrganisaatioWidgetFactory.createOrganisaatioSearchWidget(OrganisaatioSearchType.BASIC);
        organisaatioField.setPropertyDataSource(new NestedMethodProperty(model, "tarjoajat"));
        rootLayout.addComponent(organisaatioField, "organisaatio");

        // opetuskielis
        opetuskielis = createMultipleKoodiField(KOODISTO_KIELI_URI);
        rootLayout.addComponent(opetuskielis, "opetuskielet");

        // opetusmuotos
        opetusmuotos = createMultipleKoodiField(KOODISTO_OPETUSMUOTO_URI);
        rootLayout.addComponent(opetusmuotos, "opetusmuoto");

        // maksullinenKoulutusCheckbox
        maksullinenKoulutusCheckbox = new CheckBox();
        maksullinenKoulutusCheckbox.setCaption(getCaptionForString("koulutusMaksullista"));
        maksullinenKoulutusCheckbox.setImmediate(true);
        maksullinenKoulutusCheckbox.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                // TODO Auto-generated method stub
                maksullinenKoulutusTextfield.setEnabled(maksullinenKoulutusCheckbox.booleanValue());
            }
        });
        rootLayout.addComponent(maksullinenKoulutusCheckbox, "maksullinenKoulutus");

        // maksullinenKoulutusTextfield
        maksullinenKoulutusTextfield = new TextField();
        maksullinenKoulutusTextfield.setImmediate(false);
        rootLayout.addComponent(maksullinenKoulutusTextfield, "maksullinenKoulutusText");


        // teema

        teematOptionGroup = new OptionGroup("Teemat (1-3)");
        teematOptionGroup.setMultiSelect(true);
        teemat = WidgetFactory.create(TEEMA_URI);
        teemat.setField(teematOptionGroup);
        rootLayout.addComponent(teemat, "teemat");


        // peruutaBtn
        peruutaBtn = new Button();
        peruutaBtn.setCaption(getCaptionForString("Peruuta"));
        peruutaBtn.setImmediate(false);
        form.getFooter().addComponent(peruutaBtn);

        // tallennaLuonnoksenaBtn
        tallennaLuonnoksenaBtn = new Button();
        tallennaLuonnoksenaBtn.setCaption(getCaptionForString("tallennaLuonnoksena"));
        form.getFooter().addComponent(tallennaLuonnoksenaBtn);


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
        if (model.getMaksullisuus() == null) {
            this.maksullinenKoulutusCheckbox.setValue(Boolean.FALSE);
            this.maksullinenKoulutusTextfield.setEnabled(false);
        } else {
            this.maksullinenKoulutusCheckbox.setValue(Boolean.TRUE);
            this.maksullinenKoulutusTextfield.setEnabled(true);
        }

        // nested properties cannot be bound with @PropertyId? doing bind manually
        opetuskielis.setPropertyDataSource(new ObjectProperty(model.getPerustiedot().getOpetuskielis()));
        opetusmuotos.setPropertyDataSource(new ObjectProperty(model.getPerustiedot().getOpetusmuotos()));
        teemat.setPropertyDataSource(new ObjectProperty(model.getPerustiedot().getAsiasanoituses()));
        suunniteltuKestoKoodisto.setPropertyDataSource(new NestedMethodProperty(model.getPerustiedot(), "suunniteltuKestoUri"));
        form.addField("perustiedot.opetuskielis", opetuskielis);
        form.addField("perustiedot.opetusmuotos", opetusmuotos);
        form.addField("perustiedot.suunniteltuKestoUri", opetusmuotos);
        form.addField("teemaUris", opetusmuotos);
    }

    @Override
    protected KoulutusmoduuliToteutusDTO save(KoulutusmoduuliToteutusDTO model) throws Exception {
        
        if (organisaatioField.getValue() != null) {
            List<String> organisaatiot = new ArrayList<String>();
            organisaatiot.add((String)organisaatioField.getValue());
            model.setTarjoajat(organisaatiot);
        }
        
        
        if (!this.maksullinenKoulutusCheckbox.isEnabled()) {
            model.setMaksullisuus(null);
        }
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

    public CustomLayout getRootLayout() {
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

    public MultipleSelectToTableWrapper getOpetuskielis() {
        return opetuskielis;
    }

    public KoodistoComponent getSuunniteltuKestoTextfield() {
        return suunniteltuKestoKoodisto;
    }

    public KoodistoComponent getKoulutusLajiKoodisto() {
        return koulutusLajiKoodisto;
    }

    public PopupDateField getKoulutuksenAlkamispvmDatefield() {
        return koulutuksenAlkamispvmDatefield;
    }

    public KoulutusmoduuliComponent getKoulutusmoduuliTextfield() {
        return koulutusmoduuliTextfield;
    }

    public KoodistoComponent getTeemat() {
        return teemat;
    }

    /**
     * @return the organisaatioField
     */
    public OrganisaatioSearchWidget getOrganisaatioField() {
        return organisaatioField;
    }

    /**
     * @param organisaatioField the organisaatioField to set
     */
    public void setOrganisaatioField(OrganisaatioSearchWidget organisaatioField) {
        this.organisaatioField = organisaatioField;
    }

}
