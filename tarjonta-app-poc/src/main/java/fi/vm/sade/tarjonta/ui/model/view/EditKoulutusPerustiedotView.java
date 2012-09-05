/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;

/**
 *
 * @author mlyly
 */
public class EditKoulutusPerustiedotView extends VerticalLayout {
    
    private TarjontaPresenter _p;
    
    public EditKoulutusPerustiedotView(TarjontaPresenter presenter) {
        super();
        setSizeUndefined();
        _p = presenter;
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
        _koulutusohjelma = createTextField(_p, "koulutus.koulutusohjelma", "100%", "valitse.koulutusohjelma.prompt", this);
    }
    
    private TextField createTextField(TarjontaPresenter p, String modelExpression, String width, String prompt, AbstractOrderedLayout layout) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
