package fi.vm.sade.tarjonta.ui.helper.conversion;

import org.springframework.stereotype.Component;

import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;

/**
*
* @author Markus
*/
@Component
public class KoulutusSearchSpecificationViewModelToDTOConverter {

	public HaeKoulutuksetKyselyTyyppi convertViewModelToDTO(KoulutusSearchSpesificationViewModel viewModel) {
		HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
		kysely.setNimi(viewModel.getSearchStr());
		kysely.getTarjoajaOids().addAll(viewModel.getOrganisaatioOids());
		return kysely;
	}
	
}
