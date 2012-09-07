/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.MouseEvents;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.poc.helper.KeyValueBean;
import fi.vm.sade.tarjonta.ui.poc.helper.MapItem;
import fi.vm.sade.vaadin.Oph;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = false)
public class EditKoulutusPerustiedotView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotView.class);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    private Map _data = new HashMap();
    private I18NHelper i18n = new I18NHelper(this);

    public EditKoulutusPerustiedotView() {
        super();
        setSizeFull();
        initialize();
    }

    //
    // Define data fields
    //

    private void initialize() {
        LOG.info("initialize()");

        //
        // "Data model"
        //
        _data.put("koulutuksenAlkamisPvm", new Date());
        _data.put("koulutus", "Filosofian maisteri");
        _data.put("koulutusala", "Filosofia");
        _data.put("tutkinto", "Maisteri");
        _data.put("tutkintonimike", "Filosofian maisteri");
        _data.put("opintojenlaajuusyksikko", "Opintopisteet");
        _data.put("opintojenlaajuus", "300 op");
        _data.put("opintoala", "opintoala ei tiedossa");

        MapItem mi = new MapItem(_data);

        removeAllComponents();

        HorizontalLayout hlButtonsTop = new HorizontalLayout();
        addComponent(hlButtonsTop);
        addButton("Peruuta", "doCancel", hlButtonsTop);
        addButton("TallennaLuonnoksena", "doSaveIncomplete", hlButtonsTop);
        addButton("TallennaValmiina", "doSaveComplete", hlButtonsTop);
        addButton("Jatka", "doContinue", hlButtonsTop);

        Panel p = new Panel(i18n.getMessage("KoulutuksenPerustiedot"));
        p.setSizeUndefined();
        p.setScrollable(true);
        p.addStyleName(Oph.PANEL_LIGHT);
        addComponent(p);

        GridLayout grid = new GridLayout(3, 1);
        p.setContent(grid);

        grid.addComponent(addLabel("KoulutusTaiKoulutusOhjelma", null));
        grid.addComponent(addComboBox(mi, "koulutus", "KoulutusTaiKoulutusOhjelma.prompt", createKoulutusohjelmaContainer(), null, this));
        grid.addComponent(addHelpIcon("http://png.findicons.com/files/icons/2360/spirit20/20/system_question_alt_02.png", "onTopHelpClicked"));
        grid.newLine();
        grid.addComponent(addLabel("Koulutusala", null));
        grid.addComponent(addLabel(mi, "koulutusala", null));
        grid.newLine();
        grid.addComponent(addLabel("Tutkinto", null));
        grid.addComponent(addLabel(mi, "tutkinto", null));
        grid.newLine();
        grid.addComponent(addLabel("Tutkintonimike", null));
        grid.addComponent(addLabel(mi, "tutkintonimike", null));
        grid.newLine();
        grid.addComponent(addLabel("OpintojenLaajuusyksikko", null));
        grid.addComponent(addLabel(mi, "opintojenlaajuusyksikko", null));
        grid.newLine();
        grid.addComponent(addLabel("OpintojenLaajuus", null));
        grid.addComponent(addLabel(mi, "opintojenlaajuus", null));
        grid.newLine();
        grid.addComponent(addLabel("Opintoala", null));
        grid.addComponent(addLabel(mi, "opintoala", null));
        grid.newLine();
        
        {
            grid.addComponent(addLabel("Opetuskieli", null));
            
            VerticalLayout vl = new VerticalLayout();
            vl.addComponent(addTwinColSelect(mi, "opetuskielet", createOpetuskieliContainer(), "doOpetuskieletChanged", null));
            vl.addComponent(addCheckBox("Opetuskieli.ValitseKaikki", mi, "opetuskieliKaikki", "doOpetuskieletSelectAll", null));
            grid.addComponent(vl);
            
            grid.newLine();
        }
        
        grid.addComponent(addLabel("KoulutuksenAlkamisPvm", null));
        grid.addComponent(addDate(mi, "koulutuksenAlkamisPvm", null));
        grid.newLine();

        {        
            grid.addComponent(addLabel("SuunniteltuKesto", null));
            
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(addTextField(mi, "kesto", "SuunniteltuKesto.prompt", null, null));
            hl.addComponent(addComboBox(mi, "kestoTyyppi", "SuunniteltuKesto.tyyppi.prompt", createKoulutuslajiContainer(), null, null));
            grid.addComponent(hl);
            
            grid.newLine();
        }
        
        {
            Container teemaContaner = createTeemaContainer();
            grid.addComponent(addLabel("Teema", null));

            VerticalLayout vl = new VerticalLayout();
            vl.addComponent(addLabel("ValitseTeemat", null));
            vl.addComponent(addTwinColSelect(mi, "teemat", teemaContaner, null, null));
            grid.addComponent(vl);

            grid.newLine();
        }
        grid.addComponent(addLabel("SuuntautumisvaihtoehtoTaiPainotus", null));
        grid.newLine();
        {
            Container opetusmuotoContainer = createOpetusmuotoContainer();
            grid.addComponent(addLabel("Opetusmuoto", null));
            grid.addComponent(addComboBox(mi, "opetusmuoto", "Opetusmuoto.prompt", opetusmuotoContainer, null, null));
            grid.newLine();
        }
        {
            Container koulutuslajiContainer = createKoulutuslajiContainer();
            grid.addComponent(addLabel("Koulutuslaji", null));
            grid.addComponent(addComboBox(mi, "koulutuslaji", "Koulutuslaji.prompt", koulutuslajiContainer, null, null));
            grid.newLine();
        }

        {
            // TODO - dynamic addition with plus!
            grid.addComponent(addLabel("Yhteyshenkilo", null));

            HorizontalLayout hl = new HorizontalLayout();           
            VerticalLayout vl = new VerticalLayout();    
            
            vl.addComponent(addTextField(mi, "yhteyshenkiloNimi", "Yhteyshenkilo.Nimi.prompt", null, null));
            vl.addComponent(addTextField(mi, "yhteyshenkiloTitteli", "Yhteyshenkilo.Titteli.prompt", null, null));
            vl.addComponent(addTextField(mi, "yhteyshenkiloEmail", "Yhteyshenkilo.Email.prompt", null, null));
            vl.addComponent(addTextField(mi, "yhteyshenkiloPuhelin", "Yhteyshenkilo.Puhelin.prompt", null, null));
            
            hl.addComponent(vl);
            grid.addComponent(hl);
            
            hl.addComponent(addButton("Yhteyshenkilo.LisaaUusi", "onAddNewYhteyshenkilo", null));
            
            grid.newLine();
        }

        {
            grid.addComponent(addLabel("LinkkiOpetussunnitelmaan", null));
            VerticalLayout vl = new VerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiOpetussuunnitelma", "LinkkiOpetussunnitelmaan.prompt", null, null));
            vl.addComponent(addCheckBox("LinkkiOpetussunnitelmaan.eriOpetusKielet", mi, "multipleLinkkiOpetussunnitelmaan",
                    "doMultipleLinksForOpetussuunnitelma", null));
            grid.addComponent(vl);
            grid.newLine();
        }
        
        {
            grid.addComponent(addLabel("LinkkiOppilaitokseen", null));
            VerticalLayout vl = new VerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiOppilaitokseen", "LinkkiOppilaitokseen.prompt", null, null));
            vl.addComponent(addCheckBox("LinkkiOppilaitokseen.eriOpetusKielet", mi, "multipleLinkkiOppilaitos", "doMultipleLinksForOppilaitos", null));
            grid.addComponent(vl);
            grid.newLine();
        }
        
        {
            grid.addComponent(addLabel("LinkkiSOME", null));
            VerticalLayout vl = new VerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiSOME", "LinkkiSOME.prompt", null, null));
            vl.addComponent(addCheckBox("LinkkiSOME.eriOpetusKielet", mi, "multipleLinkkiSOME",
                    "doMultipleLinksForSOME", null));
            grid.addComponent(vl);
            grid.newLine();
        }
        
        {
            grid.addComponent(addLabel("LinkkiMultimedia", null));
            VerticalLayout vl = new VerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiMultimedia", "LinkkiMultimedia.prompt", null, null));
            vl.addComponent(addCheckBox("LinkkiMultimedia.eriOpetusKielet", mi, "multipleLinkkiMultimedia",
                    "doMultipleLinksForMultimedia", null));
            grid.addComponent(vl);
            grid.newLine();
        }

        {
            grid.addComponent(addCheckBox("KoulutusOnMaksullista", mi, "koulutusOnMaksullista", null, this));
            VerticalLayout vl = new VerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiKoulutusOnMaksullista", "KoulutusOnMaksullista.prompt", null, null));
            vl.addComponent(addCheckBox("KoulutusOnMaksullista.eriOpetusKielet", mi, "multipleLinkkiKoulutusOnMaksullista",
                    "doMultipleLinksForMaksullisuus", null));
            grid.addComponent(vl);
            grid.newLine();
        }

        {
            grid.addComponent(addCheckBox("StipendiMahdollisuus", mi, "stipendiMahdollisuus", null, this));
            VerticalLayout vl = new VerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiStipendiMahdollisuus", "StipendiMahdollisuus.prompt", null, null));
            vl.addComponent(addCheckBox("StipendiMahdollisuus.eriOpetusKielet", mi, "multipleLinkkiStipendiMahdollisuus",
                    "doMultipleLinksForStipendi", null));
            grid.addComponent(vl);
            grid.newLine();
        }
        
        
        HorizontalLayout hlButtonsBottom = new HorizontalLayout();
        addComponent(hlButtonsBottom);
        addButton("Peruuta", "doCancel", hlButtonsBottom);
        addButton("TallennaLuonnoksena", "doSaveIncomplete", hlButtonsBottom);
        addButton("TallennaValmiina", "doSaveComplete", hlButtonsBottom);
        addButton("Jatka", "doContinue", hlButtonsBottom);
    }

    /*
     * CREATE CONTAINERS
     * 
     * TODO DUMMY DATA
     */
    private Container createContainer(String[] values) {
        Container c = new BeanItemContainer(KeyValueBean.class);

        int i = 0;
        for (String value : values) {
            c.addItem(new KeyValueBean("" + (i++), value));
        }

        return c;
    }

    private Container createKoulutusohjelmaContainer() {
        String[] values = {
            "Filosofian maisteri",
            "Tekniikan tohtori",
            "Taivaanrannan maalari",
        };
        
        return createContainer(values);
    }
    
    private Container createOpetuskieliContainer() {
        String[] values = {
            "Suomi",
            "Ruotsi",
            "Englanti",
            "Saksa",
            "Venäjä"
        };
        
        return createContainer(values);
    }
    
    
    private Container createTeemaContainer() {
        String[] values = new String[]{
            "Filosofia ja teknologia",
            "Informaatio ja viestintä",
            "Kulttuuri ja yhteiskunta",
            "Liikenne ja kuljetus",
            "Oikeustiede",
            "Opetus",
            "Palvelut",
            "Sosiaaliuus",
            "Taide",
            "Talous ja hallinto",
            "Tekniikka",
            "Terveys ja hyvinvointi",
            "Turvallisuus",
            "Ympäristö, maa- ja metsätalous",};

        return createContainer(values);
    }

    private Container createOpetusmuotoContainer() {
        String[] values = new String[]{
            "Lähiopetus",
            "Etäopetus",
            "MISTÄ NÄMÄ TULEE"
        };

        return createContainer(values);
    }

    private Container createKoulutuslajiContainer() {
        String[] values = new String[]{
            "Koulutuslaji 1",
            "Koulutuslaji 2",
            "Koulutuslaji 3",
            "MISTÄ NÄMÄ TULEE"
        };

        return createContainer(values);
    }

    /*
     * UI HELPERS TO CREATE COMPONENTS
     */
    private TextField addTextField(PropertysetItem psi, String expression, String promptKey, String width, AbstractOrderedLayout layout) {
        TextField c = new TextField();

        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        // tf.setImmediate(true);
        c.setNullRepresentation("");
        c.addStyleName(Oph.TEXTFIELD_SMALL);

        if (width != null) {
            c.setWidth(width);
        }


        if (promptKey != null) {
            c.setInputPrompt(i18n.getMessage(promptKey));
        }

        if (layout != null) {
            layout.addComponent(c);
        }


        return c;
    }
    
    private Button addButton(String captionKey, String onClickMethodName, AbstractOrderedLayout layout) {
        Button c = new Button(i18n.getMessage(captionKey));
        c.setImmediate(true);
        
        if (onClickMethodName != null) {
            c.addListener(getClickListener(onClickMethodName));
        }
        
        c.addStyleName(Oph.BUTTON_SMALL);

        if (layout != null) {
            layout.addComponent(c);
        }
        
        return c;
    }

    private Label addLabel(String captionKey, AbstractOrderedLayout layout) {
        Label c = new Label(i18n.getMessage(captionKey));
        // c.addStyleName(Oph.LABEL_SMALL);

        if (layout != null) {
            layout.addComponent(c);
        }

        return c;
    }

    private Label addLabel(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {
        Label c = new Label();
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        c.addStyleName(Oph.LABEL_SMALL);

        if (layout != null) {
            layout.addComponent(c);
        }

        return c;
    }

    private DateField addDate(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {
        DateField c = new DateField();
        c.setDateFormat("dd.MM.yyyy");

        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        // TODO what is the style?
        // df.addStyleName(Oph.DATE);

        if (layout != null) {
            layout.addComponent(c);
        }

        return c;
    }

    private ComboBox addComboBox(PropertysetItem psi, String expression, String inputPromptKey, Container container, String valueChangeListenerMethod, AbstractOrderedLayout layout) {
        ComboBox c = new ComboBox();
        // TODO cb.addStyleName(Oph.COMBOBOX);
        c.setImmediate(true);
        
        if (valueChangeListenerMethod != null) {
            c.addListener(getValueChangeListener(valueChangeListenerMethod));
        }
        
        if (inputPromptKey != null) {
            c.setInputPrompt(i18n.getMessage(inputPromptKey));
        }

        // Data to be shown
        if (container != null) {
            c.setContainerDataSource(container);
        }

        // Selected data
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        if (layout != null) {
            layout.addComponent(c);
        }

        c.setItemCaptionPropertyId(KeyValueBean.VALUE);

        return c;
    }

    private TwinColSelect addTwinColSelect(PropertysetItem psi, String expression, Container container, String valueChangeListenerMethod, AbstractOrderedLayout layout) {
        TwinColSelect c = new TwinColSelect();
        // TODO s.addStyleName(Oph.TWINCOLSELECT);
        
        c.setImmediate(true);
        
        if (valueChangeListenerMethod != null) {
            c.addListener(getValueChangeListener(valueChangeListenerMethod));
        }
        
        // Data to be shown
        if (container != null) {
            c.setContainerDataSource(container);
        }

        // Selected data
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        if (layout != null) {
            layout.addComponent(c);
        }

        c.setItemCaptionPropertyId(KeyValueBean.VALUE);

        return c;
    }
    
    
    private CheckBox addCheckBox(String captionKey, PropertysetItem psi, String expression, String valueChangeListenerMethod, AbstractOrderedLayout layout) {
        CheckBox c = new CheckBox(i18n.getMessage(captionKey));
        
        // Selected data
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        // Routes "clicks" to methods
        if (valueChangeListenerMethod != null) {
            c.addListener(getValueChangeListener(valueChangeListenerMethod));
        }
                
        if (layout != null) {
            layout.addComponent(c);
        }
        
        c.setImmediate(true);
        
        return c;
    }
            
    private Embedded addHelpIcon(String iconUrl, String onClickListenerMethod) {
        Embedded helpIcon1 = new Embedded("", new ExternalResource(iconUrl));
        helpIcon1.setImmediate(true);

        if (onClickListenerMethod != null) {
            helpIcon1.addListener(getMouseClickListener(onClickListenerMethod));
        }
        
        return helpIcon1;
    }
    

    /*
     * VIEW ACTIONS
     */
    
    // Used to route clicks to methods by method name
    private Button.ClickListener getClickListener(final String methodName) {
        final EditKoulutusPerustiedotView target = this;
        final Method m = getMethod(methodName);
        return new Button.ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    m.invoke(target);
                } catch (Throwable ex) {
                    LOG.error("invoke of method {} failed, ex={}", methodName, ex);
                }
            }
        };
    }

    // Icons etc
    private MouseEvents.ClickListener getMouseClickListener(final String methodName) {
        final EditKoulutusPerustiedotView target = this;
        final Method m = getMethod(methodName);
        
        return new MouseEvents.ClickListener() {
            
            @Override
            public void click(MouseEvents.ClickEvent event) {
                try {
                    m.invoke(target);
                } catch (Throwable ex) {
                    LOG.error("invoke of method {} failed, ex={}", methodName, ex);
                    LOG.error("", ex);
                }
            }
        };
    }
    
    // Used to route value change information to methods by method name
    private Property.ValueChangeListener getValueChangeListener(final String methodName) {
        final EditKoulutusPerustiedotView target = this;
        final Method m = getMethod(methodName);
        return new Property.ValueChangeListener() {
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    m.invoke(target);
                } catch (Throwable ex) {
                    LOG.error("invoke of method {} failed, ex={}", methodName, ex);
                    LOG.error("", ex);
                }
            }
        };
    }
    
    private Method getMethod(String methodName) {
        try {
            return this.getClass().getMethod(methodName);
        } catch (Throwable ex) {
            LOG.error("Failed to get method: {}", methodName, ex);
            LOG.error("", ex);
            return null;
        }
    }
    
    
    
    public void doCancel() {
        LOG.info("doCancel()");
    }

    public void doSaveIncomplete() {
        LOG.info("doSaveIncomplete(): data={}", _data);
    }

    public void doSaveComplete() {
        LOG.info("doSaveComplete(): data={}", _data);
    }

    public void doContinue() {
        LOG.info("doCantinue()");
    }

    public void doMultipleLinksForOpetussuunnitelma() {
        LOG.info("doMultipleLinksForOpetussuunnitelma()");
    }

    public void doMultipleLinksForOppilaitos() {
        LOG.info("doMultipleLinksForOppilaitos()");
    }

    public void doMultipleLinksForSOME() {
        LOG.info("doMultipleLinksForSOME()");
    }

    public void doMultipleLinksForMultimedia() {
        LOG.info("doMultipleLinksForMultimedia()");
    }

    public void doMultipleLinksForMaksullisuus() {
        LOG.info("doMultipleLinksForMaksullisuus()");
    }

    public void doMultipleLinksForStipendi() {
        LOG.info("doMultipleLinksForStipendi()");
    }
    
    
    /*
     * Opetuskieli selection has been changed
     */
    public void doOpetuskieletChanged() {
        LOG.info("doOpetuskieletChanged()");
    }
    
    public void doOpetuskieletSelectAll() {
        LOG.info("doOpetuskieletSelectAll()");        
    }
    
    
    public void onTopHelpClicked() {
        LOG.info("onTopHelpClicked()");                
    }

    public void onBottomHelpClicked() {
        LOG.info("onBottomHelpClicked()");                
    }
    
    public void onAddNewYhteyshenkilo() {
        LOG.info("onAddNewYhteyshenkilo()");                
    }

}
