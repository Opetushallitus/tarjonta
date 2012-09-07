/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.poc;

import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mlyly
 */
public class EditKoulutusPerustiedot extends VerticalLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedot.class);

    public EditKoulutusPerustiedot() {
        super();
        initialize();
    }
    
    private void initialize() {
        LOG.info("initialize()");

//         
//        Locale l = Locale.CANADA;
//        BeanItem<Locale> bi = new BeanItem<Locale>(l);
//        bi.addItemProperty("class.name", new NestedMethodProperty(l, "class.name"));
//        
//        TextField tf = new TextField("Country", bi.getItemProperty("displayCountry"));
//        addComponent(tf);
//        TextField tf2 = new TextField("Class name", bi.getItemProperty("class.name"));
//        addComponent(tf2);
        
        Embedded helpIcon1 = createIcon("http://png-3.findicons.com/files/icons/817/webgloss_3d/48/question.png", "16px", "16px");
        Embedded helpIcon2 = createIcon("http://png-3.findicons.com/files/icons/817/webgloss_3d/48/question.png", "16px", "16px");
        Embedded plusIcon1 = createIcon("http://cdn1.iconfinder.com/data/icons/softwaredemo/PNG/24x24/Plus__Orange.png", "16px", "16px");
        
        addControlButtons(this);
        
        //
        // Perustiedot
        //
        GridLayout perustiedotGrid = new GridLayout(3, 1);
        perustiedotGrid.setSpacing(true);
        
        Panel perustiedotPanel = new Panel("Koulutuksen perustiedot", perustiedotGrid);
        addComponent(perustiedotPanel);

        add(perustiedotGrid, new Label("Koulutus / Koulutusohjelma"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, UiBuilder.newTextField("", "Valitse Koulutus / Koulutusohjelma", true), Alignment.TOP_LEFT);
        add(perustiedotGrid, helpIcon1, Alignment.TOP_RIGHT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Koulutuksen tyyppi"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, UiBuilder.newComboBox(null, new String[] {"Tyyppi 1", "Tyyppi 2", "Tyyppi 3"}, null), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Koulutusala"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, new Label("[Luonnontieteellinen]"), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Tutkinto"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, new Label("[filosofian maisteri]"), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Tutkintonimike"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, new Label("[filosofian maisteri]"), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Opintojen laajuusyksikkö"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, new Label("[opintopisteet]"), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Opintojen laajuus"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, new Label("[300 op]"), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Opintoala"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, new Label("[-]"), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Opetuskieli"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, UiBuilder.newComboBox(null, new String[] {"Suomi", "Ruotsi", "Englanti", "Swahili"}, null), Alignment.TOP_LEFT);
        // TODO valitut listattuna
        // TODO lisää kaikki linkki
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Koulutuksen alkamispäivä"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, UiBuilder.newDate(), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Suunniteltu kesto"), Alignment.TOP_RIGHT);
        {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(UiBuilder.newTextField("", "Kesto, esim. 1-3", true));
            hl.addComponent(UiBuilder.newComboBox(null, new String[] {"Vuotta", "Kuukautta", "Päivää", "Tuntia"}, null));
            add(perustiedotGrid, hl, Alignment.TOP_LEFT);
        }
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Teema"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, new Label("[Valitse 1-3 teemaa]"), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Suuntautumisvaihtoehto / painotus"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, UiBuilder.newTextField("", "Valitse...", true), Alignment.TOP_LEFT);
        // TODO "+" button?
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Opetusmuoto"), Alignment.TOP_RIGHT);
        {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(UiBuilder.newComboBox(null, new String[] {"Valitse opetusmuoto", "Lähiopetus", "Aikuisopetus", "Etäopetus"}, null));
            hl.addComponent(UiBuilder.newComboBox(null, new String[] {"Valitse koulutuslaji", "Mikä", "Tämä", "On?"}, null));
            add(perustiedotGrid, hl, Alignment.TOP_LEFT);
        }
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Yhteyshenkilö"), Alignment.TOP_RIGHT);
        {
            HorizontalLayout hl = new HorizontalLayout();
            VerticalLayout vl = new VerticalLayout();
            hl.addComponent(vl);
            
            vl.addComponent(UiBuilder.newTextField("", "Nimi", true));
            vl.addComponent(UiBuilder.newTextField("", "Titteli", true));
            vl.addComponent(UiBuilder.newTextField("", "Sähköposti", true));
            vl.addComponent(UiBuilder.newTextField("", "Puhelinnumero", true));
            vl.addComponent(UiBuilder.newLabel("Yhteyshenkilö opetuskielissä:"));
            vl.addComponent(UiBuilder.newLabel("[checkboxit tähän]"));
            
            hl.addComponent(plusIcon1);
            
            add(perustiedotGrid, hl, Alignment.TOP_LEFT);
        }        
        add(perustiedotGrid, helpIcon2, Alignment.TOP_RIGHT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Linkki opetussuunnitelmaan"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, UiBuilder.newTextField("", "http://www.example.com/data?koodi=1234&kieli=fi_FI", true), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Linkki oppilaitokseen"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, UiBuilder.newTextField("", "http://www.example.com/data?koodi=1234&kieli=fi_FI", true), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Sosiaalisen median linkki"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, UiBuilder.newTextField("", "http://www.example.com/data?koodi=1234&kieli=fi_FI", true), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        add(perustiedotGrid, new Label("Multimedialinkki"), Alignment.TOP_RIGHT);
        add(perustiedotGrid, UiBuilder.newTextField("", "http://www.example.com/data?koodi=1234&kieli=fi_FI", true), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();

        {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(UiBuilder.newCheckbox(null, null));
            hl.addComponent(UiBuilder.newLabel("Koulutus on maksullista"));
            add(perustiedotGrid, hl, Alignment.TOP_RIGHT);
        }
        add(perustiedotGrid, UiBuilder.newTextField("", "http://www.example.com/data?koodi=1234&kieli=fi_FI", true), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();
        
        {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(UiBuilder.newCheckbox(null, null));
            hl.addComponent(UiBuilder.newLabel("Stipendimahdollisuus"));
            add(perustiedotGrid, hl, Alignment.TOP_RIGHT);
        }
        add(perustiedotGrid, UiBuilder.newTextField("", "http://www.example.com/data?koodi=1234&kieli=fi_FI", true), Alignment.TOP_LEFT);
        perustiedotGrid.newLine();

        addControlButtons(this);
    }

    private void add(GridLayout perustiedotGrid, Component comp, Alignment alignment) {
        perustiedotGrid.addComponent(comp);
        if (alignment != null) {
            perustiedotGrid.setComponentAlignment(comp, alignment);
        }
    }

    private void addControlButtons(AbstractOrderedLayout layout) {
        // Top/Bottom control buttons
        HorizontalLayout buttonsLayout = UiBuilder.newHorizontalLayout();
        UiBuilder.newButton("Peruuta", buttonsLayout);
        UiBuilder.newButton("Tallenna luonnoksena", buttonsLayout);
        UiBuilder.newButton("Tallenna valmiina", buttonsLayout);
        UiBuilder.newButton("Jatka", buttonsLayout);
        layout.addComponent(buttonsLayout);
    }
    
    private Embedded createIcon(String url, String width, String height) {
        Embedded helpIcon1 = new Embedded("", new ExternalResource(url));
        helpIcon1.setWidth(width);
        helpIcon1.setHeight(height);
        
        return helpIcon1;
    }
    
}
