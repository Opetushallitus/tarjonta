package fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.OptionGroup;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
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
public class KoulutusmoduuliToteutusEditView extends CustomComponent {
        
    private KoulutusmoduuliToteutusDTO boundDto;
    
   private GridLayout rootLayout;
    
   private Button tallennaValmiinaBtn;
	
   private Button tallennaLuonnoksenaBtn;
	
   private Button peruutaBtn;
	
   private TextField maksullinenKoulutusTextfield;
	
   private CheckBox maksullinenKoulutusCheckbox;
	
   private ComboBox opetusmuotoCombox;

   private Label opetusmuotoLabel;
	
   private TextField opetuskieliTextfield;
	
   private Label opetuskieliLabel;
	
   private TextField organisaatioTextfield;
	
   private Label organisaatioLabel;
	
   private TextField suunniteltuKestoTextfield;
	
   private Label suunniteltuKestoLabel;
	
   private ComboBox koulutusLajiCombobox;
	
   private Label koulutusLajiLabel;
	
   private PopupDateField koulutuksenAlkamispvmDatefield;
	
   private Label koulutuksenAlkamisPvmLbl;
	
   private TextField koulutusmoduuliTextfield;
	
   private Label koulutusModuliLabel;
	
   private Label komotoEditTitle;
   
   private OptionGroup teematOptionGroup;
   
   private static final List<String> dummyTeemat = Arrays.asList(new String[] {
            "Sosiaaliala,yhteiskunta ja politiikka", "Kielet ja kulttuuri", "Talous, kauppa ja hallinta", "Terveys, hyvinvointi ja lääketiede", 
            "Kasvatus, opetus ja psykologia", "Biologia, kemia ja maantiede","Suojelu ja pelastus" });

    
   public KoulutusmoduuliToteutusEditView() {
       buildRootLayout();
       setCompositionRoot(rootLayout);
   }
   
   private GridLayout buildRootLayout() {
		// common part: create layout
		rootLayout = new GridLayout();
		rootLayout.setImmediate(false);
		rootLayout.setWidth("760px");
		rootLayout.setHeight("560px");
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
		koulutusmoduuliTextfield = new TextField();
		koulutusmoduuliTextfield.setImmediate(false);
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
		koulutusLajiCombobox = new ComboBox();
		koulutusLajiCombobox.setImmediate(false);
		koulutusLajiCombobox.setWidth("-1px");
		koulutusLajiCombobox.setHeight("-1px");
		rootLayout.addComponent(koulutusLajiCombobox, 1, 2);
		
		// suunniteltuKestoLabel
		suunniteltuKestoLabel = new Label();
		suunniteltuKestoLabel.setImmediate(false);
		suunniteltuKestoLabel.setWidth("-1px");
		suunniteltuKestoLabel.setHeight("-1px");
		suunniteltuKestoLabel.setValue("Suunniteltu kesto");
		rootLayout.addComponent(suunniteltuKestoLabel, 2, 2);
		
		// suunniteltuKestoTextfield
		suunniteltuKestoTextfield = new TextField();
		suunniteltuKestoTextfield.setImmediate(false);
		suunniteltuKestoTextfield.setWidth("166px");
		suunniteltuKestoTextfield.setHeight("-1px");
		rootLayout.addComponent(suunniteltuKestoTextfield, 3, 2);
		
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
		
		// opetuskieliTextfield
		opetuskieliTextfield = new TextField();
		opetuskieliTextfield.setImmediate(false);
		opetuskieliTextfield.setWidth("166px");
		opetuskieliTextfield.setHeight("-1px");
		rootLayout.addComponent(opetuskieliTextfield, 3, 3);
		
		// opetusmuotoLabel
		opetusmuotoLabel = new Label();
		opetusmuotoLabel.setImmediate(false);
		opetusmuotoLabel.setWidth("-1px");
		opetusmuotoLabel.setHeight("-1px");
		opetusmuotoLabel.setValue("Opetusmuoto");
		rootLayout.addComponent(opetusmuotoLabel, 0, 4);
		
		// opetusmuotoCombox
		opetusmuotoCombox = new ComboBox();
		opetusmuotoCombox.setImmediate(false);
		opetusmuotoCombox.setWidth("-1px");
		opetusmuotoCombox.setHeight("-1px");
		rootLayout.addComponent(opetusmuotoCombox, 1, 4);
		
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
   
}
