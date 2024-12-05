package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;

/*
 * Convert REST-DTO to WS-DTO
 * @author: Tuomas Katva 9/25/13
 */
public class HakukohdeDTOToHakukohdeTyyppiConverter
    implements Converter<HakukohdeDTO, HakukohdeTyyppi> {

  @Override
  public HakukohdeTyyppi convert(HakukohdeDTO hakukohdeDTO) {
    HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();
    hakukohdeTyyppi.setHakukohdeNimi(hakukohdeDTO.getHakukohdeNimiUri());
    hakukohdeTyyppi.setOid(hakukohdeDTO.getOid());
    hakukohdeTyyppi.setHakukohteenHakuOid(hakukohdeDTO.getHakuOid());
    hakukohdeTyyppi.setAloituspaikat(hakukohdeDTO.getAloituspaikatLkm());
    hakukohdeTyyppi.setValintaperustekuvausKoodiUri(hakukohdeDTO.getValintaperustekuvausKoodiUri());
    hakukohdeTyyppi.setValintaperustekuvausTeksti(
        convertRDtoArvos(hakukohdeDTO.getValintaperustekuvaus()));
    hakukohdeTyyppi.setLisatiedot(convertRDtoArvos(hakukohdeDTO.getLisatiedot()));
    hakukohdeTyyppi.setSahkoinenToimitusOsoite(hakukohdeDTO.getSahkoinenToimitusOsoite());
    hakukohdeTyyppi.setLiitteidenToimitusPvm(hakukohdeDTO.getLiitteidenToimitusPvm());
    hakukohdeTyyppi.setKaytetaanHaunPaattymisenAikaa(
        hakukohdeDTO.isKaytetaanHaunPaattymisenAikaa());
    hakukohdeTyyppi.setAlinHyvaksyttavaKeskiarvo(
        new BigDecimal(hakukohdeDTO.getAlinHyvaksyttavaKeskiarvo()));
    hakukohdeTyyppi.setHakuaikaAlkuPvm(hakukohdeDTO.getHakuaikaAlkuPvm());
    hakukohdeTyyppi.setHakuaikaLoppuPvm(hakukohdeDTO.getHakuaikaLoppuPvm());
    hakukohdeTyyppi.setValinnanAloituspaikat(hakukohdeDTO.getValintojenAloituspaikatLkm());
    hakukohdeTyyppi.setLiitteidenToimitusOsoite(
        convertToOsoiteTyyppi(hakukohdeDTO.getLiitteidenToimitusosoite()));
    hakukohdeTyyppi.getOpetuskieliUris().addAll(hakukohdeDTO.getOpetuskielet());
    hakukohdeTyyppi.setViimeisinPaivittajaOid(hakukohdeDTO.getModifiedBy());
    hakukohdeTyyppi.setViimeisinPaivitysPvm(hakukohdeDTO.getModified());
    hakukohdeTyyppi.setHakukohteenTila(TarjontaTila.fromValue(hakukohdeDTO.getTila()));
    if (hakukohdeDTO.getHakukohdeKoulutusOids() != null) {
      hakukohdeTyyppi.getHakukohteenKoulutusOidit().addAll(hakukohdeDTO.getHakukohdeKoulutusOids());
    }
    return hakukohdeTyyppi;
  }

  private OsoiteTyyppi convertToOsoiteTyyppi(OsoiteRDTO osoiteDto) {

    if (osoiteDto != null) {
      OsoiteTyyppi osoiteTyyppi = new OsoiteTyyppi();
      osoiteTyyppi.setOsoiteRivi(osoiteDto.getOsoiterivi1());
      osoiteTyyppi.setLisaOsoiteRivi(osoiteDto.getOsoiterivi2());
      osoiteTyyppi.setPostinumero(osoiteDto.getPostinumero());
      osoiteTyyppi.setPostitoimipaikka(osoiteDto.getPostitoimipaikka());
      return osoiteTyyppi;
    } else {
      return null;
    }
  }

  private MonikielinenTekstiTyyppi convertRDtoArvos(Map<String, String> valueMap) {

    if (valueMap != null) {
      MonikielinenTekstiTyyppi monikielinenTekstiTyyppi = new MonikielinenTekstiTyyppi();
      for (String key : valueMap.keySet()) {
        MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
        teksti.setKieliKoodi(key);
        teksti.setValue(valueMap.get(key));
      }
      return monikielinenTekstiTyyppi;

    } else {
      return null;
    }
  }
}
