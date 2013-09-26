package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import org.springframework.stereotype.Component;

import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;

/**
*
* @author Markus
*/
@Component
public class KoulutusSearchSpecificationViewModelToDTOConverter {

	public KoulutuksetKysely convertViewModelToKoulutusDTO(KoulutusSearchSpesificationViewModel viewModel) {
		KoulutuksetKysely kysely = new KoulutuksetKysely();
		kysely.setNimi(viewModel.getSearchStr());
		kysely.getTarjoajaOids().addAll(viewModel.getOrganisaatioOids());
		kysely.setKoulutuksenAlkamiskausi(viewModel.getKoulutuksenAlkamiskausi());
        if (viewModel.getKoulutuksenTila() != null) {
            kysely.setKoulutuksenTila(TarjontaTila.fromValue(viewModel.getKoulutuksenTila()));
        }
		kysely.setKoulutuksenAlkamisvuosi(viewModel.getKoulutuksenAlkamisvuosi());
		return kysely;
	}
	
	public HakukohteetKysely convertViewModelToHakukohdeDTO(KoulutusSearchSpesificationViewModel viewModel) {
		HakukohteetKysely kysely = new HakukohteetKysely();
		kysely.setNimi(viewModel.getSearchStr());
		kysely.getTarjoajaOids().addAll(viewModel.getOrganisaatioOids());
		kysely.setKoulutuksenAlkamiskausi(viewModel.getKoulutuksenAlkamiskausi());
        if (viewModel.getKoulutuksenTila() != null) {
            kysely.addTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(viewModel.getKoulutuksenTila()));
        }
		kysely.setKoulutuksenAlkamisvuosi(viewModel.getKoulutuksenAlkamisvuosi());
		return kysely;
	}
	
}
