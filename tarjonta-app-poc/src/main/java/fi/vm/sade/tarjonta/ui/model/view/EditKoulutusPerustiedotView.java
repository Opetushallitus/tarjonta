/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.poc.helper.MapItem;
import fi.vm.sade.vaadin.Oph;
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
        setSizeUndefined();
        addComponent(new Label("EI ALUSTETTU"));

        initialize();

//        MapItem bi = new MapItem(_data);
//        addTextField(bi, "koulutusohjelma", "koulutusohjelma.prompt", "100%", this);
    }
    //
    // Define data fields
    //
    private TextField _koulutusohjelma;
    private NativeSelect _koulutuksenTyyppi;
    private Label _koulutusala;
    private Label _tutkinto;
    private Label _tutkintonimike;
    private Label _opintojenLaajuusYksikko;
    private Label _opintojenLaajuus;
    // TODO ei näytetä yliopistoille
    private Label _opintoala;
    // TODO voi olla monta opetuskieltä, vaikuttaa muihin kenttiin
    private NativeSelect _opetuskieli;
    private DateField _koulutuksenAlkamisPvm;
    private TextField _suunniteltuKesto;
    private NativeSelect _suunniteltuKestoYksikko;
    // TODO TEEMAT xKpl, checkbox? mistä tulevat?
    // TODO Voi olla monta!
    private TextField _suuntautumisvaihtoehto;
    // TODO mistä tulevat vaihtoehdot?
    // TODO ei näytetä yliopistoille
    private NativeSelect _opetusmuoto;
    private NativeSelect _koulutuslaji;
    // TODO yhteyshenkilöt
    // TODO naitetaan opetuskieliin joissa on yhteyshenkilönä
    // TODO kieliversiot linkeille
    private TextField _linkkiOpetussuunnitelmaan;
    private TextField _linkkiOppilaitos;
    private TextField _linkkiSosiaalinenMedia;
    private TextField _linkkiMultimedia;
    private TextField _linkkiMaksullisuus;
    private TextField _linkkiStipendimahdollisuus;
    private CheckBox _koulutusOnMaksullista;
    private CheckBox _koulutusStipendimahdollisuus;

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
        addButton("Peruuta", this, "doCancel", hlButtonsTop);
        addButton("TallennaLuonnoksena", this, "doSaveIncomplete", hlButtonsTop);
        addButton("TallennaValmiina", this, "doSaveComplete", hlButtonsTop);
        addButton("Jatka", this, "doContinue", hlButtonsTop);

        Panel p = new Panel(i18n.getMessage("KoulutuksenPerustiedot"));
        p.setSizeFull();
        p.setScrollable(true);
        p.addStyleName(Oph.PANEL_LIGHT);
        addComponent(p);

        GridLayout grid = new GridLayout(3, 1);
        p.setContent(grid);

        grid.addComponent(addLabel("KoulutusTaiKoulutusOhjelma", null));
        grid.addComponent(addTextField(mi, "koulutus", "KoulutusTaiKoulutusOhjelma.prompt", null, null));
        // TODO add help icon
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
        grid.addComponent(addLabel("Opetuskieli", null));
        // TODO opetuskieli
        grid.newLine();
        grid.addComponent(addLabel("KoulutuksenAlkamisPvm", null));
        grid.addComponent(addDate(mi, "koulutuksenAlkamisPvm", null));
        grid.newLine();
        grid.addComponent(addLabel("SuuniteltuKesto", null));
        grid.newLine();
        grid.addComponent(addLabel("Teema", null));
        grid.newLine();
        grid.addComponent(addLabel("SuuntautumisvaihtoehtoTaiPainotus", null));
        grid.newLine();
        grid.addComponent(addLabel("Opetusmuoto", null));
        grid.newLine();
        grid.addComponent(addLabel("Koulutuslaji", null));
        grid.newLine();
        grid.addComponent(addLabel("Yhteyshenkilo", null));
        grid.newLine();
        grid.addComponent(addLabel("LinkkiOpetussunnitelmaan", null));
        grid.newLine();
        grid.addComponent(addLabel("LinkkiOppilaitokseen", null));
        grid.newLine();
        grid.addComponent(addLabel("LinkkiSOME", null));
        grid.newLine();
        grid.addComponent(addLabel("LinkkiMultimedia", null));
        grid.newLine();
        grid.addComponent(addLabel("KoulutusOnMaksullista", null));
        grid.newLine();
        grid.addComponent(addLabel("StipendiMahdollisuus", null));
        grid.newLine();
        
        HorizontalLayout hlButtonsBottom = new HorizontalLayout();
        addComponent(hlButtonsBottom);
        addButton("Peruuta", this, "doCancel", hlButtonsBottom);
        addButton("TallennaLuonnoksena", this, "doSaveIncomplete", hlButtonsBottom);
        addButton("TallennaValmiina", this, "doSaveComplete", hlButtonsBottom);
        addButton("Jatka", this, "doContinue", hlButtonsBottom);
    }

    private TextField addTextField(PropertysetItem psi, String expression, String promptKey, String width, AbstractOrderedLayout layout) {
        TextField tf = new TextField();
        
        if (psi != null && expression != null) {
            tf.setPropertyDataSource(psi.getItemProperty(expression));
        }
        
        // tf.setImmediate(true);
        tf.setNullRepresentation("");
        tf.addStyleName(Oph.TEXTFIELD_SMALL);
        
        if (width != null) {
            tf.setWidth(width);
        }

        
        if (promptKey != null) {
            tf.setInputPrompt(i18n.getMessage(promptKey));
        }

        if (layout != null) {
            layout.addComponent(tf);
        }
        
        
        return tf;
    }

    private Button addButton(String captionKey, EditKoulutusPerustiedotView target, String methodname, AbstractOrderedLayout layout) {
        Button b = new Button(i18n.getMessage(captionKey), target, methodname);
        b.addStyleName(Oph.BUTTON_SMALL);
        
        if (layout != null) {
            layout.addComponent(b);
        }

        return b;
    }

    private Label addLabel(String captionKey, AbstractOrderedLayout layout) {
        Label l = new Label(i18n.getMessage(captionKey));
        l.addStyleName(Oph.LABEL_SMALL);

        if (layout != null) {
            layout.addComponent(l);
        }

        return l;
    }

    private Label addLabel(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {
        Label l = new Label();
        if (psi != null && expression != null) {
            l.setPropertyDataSource(psi.getItemProperty(expression));
        }

        l.addStyleName(Oph.LABEL_SMALL);

        if (layout != null) {
            layout.addComponent(l);
        }

        return l;
    }

    
    private DateField addDate(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {
        DateField df = new DateField();
        df.setDateFormat("dd.MM.yyyy");
        
        if (psi != null && expression != null) {
            df.setPropertyDataSource(psi.getItemProperty(expression));
        }
        
        // TODO what is the style?
        // df.addStyleName(Oph.DATE);
        
        if (layout != null) {
            layout.addComponent(df);
        }
        
        return df;
    }
    
    
    

    public void doCancel(ClickEvent event) {
        LOG.info("doCancel()");
    }
    
    public void doSaveIncomplete(ClickEvent event) {
        LOG.info("doSaveIncomplete(): data={}", _data);
    }
    
    public void doSaveComplete(ClickEvent event) {
        LOG.info("doSaveComplete(): data={}", _data);
    }

    public void doContinue(ClickEvent event) {
        LOG.info("doCantinue()");
    }
}
