package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import com.google.common.base.Preconditions;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author: Tuomas Katva
 * Date: 4/4/13
 */
@Configurable(preConstruction = true)
public class ShowHakukohdeValintakoeRow {

    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;

    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;

    private ValintakoeAikaViewModel valintakoeAikaViewModel;

    private String language;


    private String valintakoeAika;
    private String valintakoeSijainti;
    private String valintakoeLisatiedot;

    private final String datePattern = "dd.MM.yyyy HH:mm";

    public ShowHakukohdeValintakoeRow(ValintakoeAikaViewModel viewModel, String language) {
        Preconditions.checkNotNull(language,"Language cannot be null");
        Preconditions.checkNotNull(viewModel,"Valintakoe cannot be null");
        this.valintakoeAikaViewModel = viewModel;
        this.language = language;
        resolveFields();

    }

    private void resolveFields() {

          setValintakoeAika(getValintakoeAikaText());
          setValintakoeSijainti(getValintaKoeAikaPaikka());
          setValintakoeLisatiedot(valintakoeAikaViewModel.getValintakoeAikaTiedot());
    }

    private String getValintakoeAikaText() {
        StringBuilder sb = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        sb.append(sdf.format(valintakoeAikaViewModel.getAlkamisAika()));
        sb.append(" - ");
        sb.append(sdf.format(valintakoeAikaViewModel.getPaattymisAika()));

        return sb.toString();
    }

    private String getValintaKoeAikaPaikka() {
        StringBuilder sb = new StringBuilder();

        sb.append(valintakoeAikaViewModel.getOsoiteRivi());
        sb.append(", ");
        List<KoodiType> koodis = tarjontaUIHelper.getKoodis(valintakoeAikaViewModel.getPostinumero());
        if (koodis != null) {
            sb.append(koodis.get(0).getKoodiArvo());
        }
        sb.append(", ");
        sb.append(tarjontaUIHelper.getKoodiNimi(valintakoeAikaViewModel.getPostinumero(),I18N.getLocale()));

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShowHakukohdeValintakoeRow that = (ShowHakukohdeValintakoeRow) o;

        if (getValintakoeAika() != null ? !getValintakoeAika().equals(that.getValintakoeAika()) : that.getValintakoeAika() != null)
            return false;
        if (getValintakoeLisatiedot() != null ? !getValintakoeLisatiedot().equals(that.getValintakoeLisatiedot()) : that.getValintakoeLisatiedot() != null)
            return false;
        if (getValintakoeSijainti() != null ? !getValintakoeSijainti().equals(that.getValintakoeSijainti()) : that.getValintakoeSijainti() != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getValintakoeAika() != null ? getValintakoeAika().hashCode() : 0;
        result = 31 * result + (getValintakoeSijainti() != null ? getValintakoeSijainti().hashCode() : 0);
        result = 31 * result + (getValintakoeLisatiedot() != null ? getValintakoeLisatiedot().hashCode() : 0);
        return result;
    }

    public void setValintakoeAika(String valintakoeAika) {
        this.valintakoeAika = valintakoeAika;
    }

    public String getValintakoeSijainti() {
        return valintakoeSijainti;
    }

    public void setValintakoeSijainti(String valintakoeSijainti) {
        this.valintakoeSijainti = valintakoeSijainti;
    }

    public String getValintakoeLisatiedot() {
        return valintakoeLisatiedot;
    }

    public void setValintakoeLisatiedot(String valintakoeLisatiedot) {
        this.valintakoeLisatiedot = valintakoeLisatiedot;
    }

    public String getValintakoeAika() {
        return valintakoeAika;
    }
}
