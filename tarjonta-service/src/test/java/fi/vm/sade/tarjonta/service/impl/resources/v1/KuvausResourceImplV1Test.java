package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.TestMockBase;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;
import fi.vm.sade.tarjonta.service.auth.NotAuthorizedException;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class KuvausResourceImplV1Test extends TestMockBase {

    @InjectMocks
    private KuvausResourceImplV1 kuvausResource;
    private HttpTestHelper httpTestHelper = new HttpTestHelper();
    private HttpServletRequest request = httpTestHelper.request;


    private ValintaperusteSoraKuvaus getDummyValintaperustekuvaus() {
        ValintaperusteSoraKuvaus kuvaus = new ValintaperusteSoraKuvaus();
        MonikielinenTeksti nimi = new MonikielinenTeksti();
        nimi.addTekstiKaannos("kieli_fi", "Lorem");
        kuvaus.setMonikielinenNimi(nimi);
        kuvaus.setId(100L);
        return kuvaus;
    }

    private KuvausV1RDTO getDummyValintaperustekuvausDto() {
        KuvausV1RDTO kuvaus = new KuvausV1RDTO();
        return kuvaus;
    }

    @Test
    public void thatKKUserCanCreateKuvaus() {
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkCreateValintaPeruste();
        doNothing().when(permissionChecker).checkCreateValintaPerusteKK();
        when(converter.toValintaperusteSoraKuvaus(any(KuvausV1RDTO.class))).thenReturn(getDummyValintaperustekuvaus());
        when(converter.toKuvausRDTO(any(ValintaperusteSoraKuvaus.class), anyBoolean())).thenReturn(getDummyValintaperustekuvausDto());

        KuvausV1RDTO kuvausDTO = getAmmattikorkeakoulukuvausDTO();
        ResultV1RDTO<KuvausV1RDTO> resultDTO = kuvausResource.createNewKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());

        kuvausDTO = getYliopistokuvausDTO();
        resultDTO = kuvausResource.createNewKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());

        kuvausDTO = getSotilaskorkeakoulukuvausDTO();
        resultDTO = kuvausResource.createNewKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());
    }

    @Test
    public void thatKKUserCanUpdateKuvaus() {
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkUpdateValintaperustekuvaus();
        doNothing().when(permissionChecker).checkUpdateValintaperustekuvausKK();
        when(converter.toValintaperusteSoraKuvaus(any(KuvausV1RDTO.class))).thenReturn(getDummyValintaperustekuvaus());
        when(kuvausDAO.read(anyLong())).thenReturn(getDummyValintaperustekuvaus());

        KuvausV1RDTO kuvausDTO = getAmmattikorkeakoulukuvausDTO();
        ResultV1RDTO<KuvausV1RDTO> resultDTO = kuvausResource.updateKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());

        kuvausDTO = getYliopistokuvausDTO();
        resultDTO = kuvausResource.updateKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());

        kuvausDTO = getSotilaskorkeakoulukuvausDTO();
        resultDTO = kuvausResource.updateKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());
    }

    @Test
    public void thatUserWithoutPermissionCannotCreateKuvaus() {
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkCreateValintaPeruste();
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkCreateValintaPerusteKK();

        KuvausV1RDTO kuvausDTO = getAmmattikorkeakoulukuvausDTO();
        ResultV1RDTO<KuvausV1RDTO> resultDTO = kuvausResource.createNewKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());

        kuvausDTO = getYliopistokuvausDTO();
        resultDTO = kuvausResource.createNewKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());

        kuvausDTO = getSotilaskorkeakoulukuvausDTO();
        resultDTO = kuvausResource.createNewKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());

        kuvausDTO = getLukiokuvausDTO();
        resultDTO = kuvausResource.createNewKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());
    }

    @Test
    public void thatUserWithoutPermissionCannotUpdateKuvaus() {
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkUpdateValintaperustekuvaus();
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkUpdateValintaperustekuvausKK();

        KuvausV1RDTO kuvausDTO = getAmmattikorkeakoulukuvausDTO();
        ResultV1RDTO<KuvausV1RDTO> resultDTO = kuvausResource.updateKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());

        kuvausDTO = getYliopistokuvausDTO();
        resultDTO = kuvausResource.updateKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());

        kuvausDTO = getSotilaskorkeakoulukuvausDTO();
        resultDTO = kuvausResource.updateKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());

        kuvausDTO = getLukiokuvausDTO();
        resultDTO = kuvausResource.updateKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());
    }

    @Test
    public void thatUserWithoutPermissionCannotDeleteKuvaus() {
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkRemoveValintaPeruste();
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkRemoveValintaPerusteKK();

        when(kuvausDAO.read(1234L)).thenReturn(getAmmattikorkeakoulukuvaus());
        ResultV1RDTO<KuvausV1RDTO> resultDTO = kuvausResource.removeById("1234", request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());

        when(kuvausDAO.read(1234L)).thenReturn(getYliopistokuvaus());
        resultDTO = kuvausResource.removeById("1234", request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());

        when(kuvausDAO.read(1234L)).thenReturn(getSotilaskorkeakoulukuvaus());
        resultDTO = kuvausResource.removeById("1234", request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());

        when(kuvausDAO.read(1234L)).thenReturn(getLukiokuvaus());
        resultDTO = kuvausResource.removeById("1234", request);
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, resultDTO.getStatus());
    }

    @Test
    public void thatKKUserCanDeleteKuvaus() {
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkRemoveValintaPeruste();
        doNothing().when(permissionChecker).checkRemoveValintaPerusteKK();

        when(kuvausDAO.read(1234L)).thenReturn(getAmmattikorkeakoulukuvaus());
        ResultV1RDTO<KuvausV1RDTO> resultDTO = kuvausResource.removeById("1234", request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());

        when(kuvausDAO.read(1234L)).thenReturn(getYliopistokuvaus());
        resultDTO = kuvausResource.removeById("1234", request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());

        when(kuvausDAO.read(1234L)).thenReturn(getSotilaskorkeakoulukuvaus());
        resultDTO = kuvausResource.removeById("1234", request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());
    }

    @Test
    public void thatToisenAsteenUserCanDeleteKuvaus() {
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkRemoveValintaPerusteKK();
        doNothing().when(permissionChecker).checkRemoveValintaPeruste();

        when(kuvausDAO.read(1234L)).thenReturn(getLukiokuvaus());
        ResultV1RDTO<KuvausV1RDTO> resultDTO = kuvausResource.removeById("1234", request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());
    }

    @Test
    public void thatToisenAsteenUserCanCreateKuvaus() {
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkCreateValintaPerusteKK();
        doNothing().when(permissionChecker).checkCreateValintaPerusteKK();
        when(converter.toValintaperusteSoraKuvaus(any(KuvausV1RDTO.class))).thenReturn(getDummyValintaperustekuvaus());
        when(converter.toKuvausRDTO(any(ValintaperusteSoraKuvaus.class), anyBoolean())).thenReturn(getDummyValintaperustekuvausDto());

        KuvausV1RDTO kuvausDTO = getLukiokuvausDTO();
        ResultV1RDTO<KuvausV1RDTO> resultDTO = kuvausResource.createNewKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());
    }

    @Test
    public void thatToisenAsteenUserCanUpdateKuvaus() {
        doThrow(new NotAuthorizedException("")).when(permissionChecker).checkUpdateValintaperustekuvausKK();
        doNothing().when(permissionChecker).checkUpdateValintaperustekuvaus();
        when(converter.toValintaperusteSoraKuvaus(any(KuvausV1RDTO.class))).thenReturn(getDummyValintaperustekuvaus());
        when(converter.toKuvausRDTO(any(ValintaperusteSoraKuvaus.class), anyBoolean())).thenReturn(getDummyValintaperustekuvausDto());

        KuvausV1RDTO kuvausDTO = getLukiokuvausDTO();
        ResultV1RDTO<KuvausV1RDTO> resultDTO = kuvausResource.createNewKuvaus("valintaperustekuvaus", kuvausDTO, request);
        assertEquals(ResultV1RDTO.ResultStatus.OK, resultDTO.getStatus());
    }

    private KuvausV1RDTO getLukiokuvausDTO() {
        KuvausV1RDTO kuvausDTO = new KuvausV1RDTO();
        kuvausDTO.setOrganisaatioTyyppi("oppilaitostyyppi_15");
        return kuvausDTO;
    }

    private KuvausV1RDTO getAmmattikorkeakoulukuvausDTO() {
        KuvausV1RDTO kuvausDTO = new KuvausV1RDTO();
        kuvausDTO.setOrganisaatioTyyppi("oppilaitostyyppi_41");
        return kuvausDTO;
    }

    private KuvausV1RDTO getYliopistokuvausDTO() {
        KuvausV1RDTO kuvausDTO = new KuvausV1RDTO();
        kuvausDTO.setOrganisaatioTyyppi("oppilaitostyyppi_42");
        return kuvausDTO;
    }

    private KuvausV1RDTO getSotilaskorkeakoulukuvausDTO() {
        KuvausV1RDTO kuvausDTO = new KuvausV1RDTO();
        kuvausDTO.setOrganisaatioTyyppi("oppilaitostyyppi_43");
        return kuvausDTO;
    }

    private ValintaperusteSoraKuvaus getLukiokuvaus() {
        ValintaperusteSoraKuvaus kuvaus = getDummyValintaperustekuvaus();
        kuvaus.setOrganisaatioTyyppi("oppilaitostyyppi_15");
        return kuvaus;
    }

    private ValintaperusteSoraKuvaus getAmmattikorkeakoulukuvaus() {
        ValintaperusteSoraKuvaus kuvaus = getDummyValintaperustekuvaus();
        kuvaus.setOrganisaatioTyyppi("oppilaitostyyppi_41");
        return kuvaus;
    }

    private ValintaperusteSoraKuvaus getYliopistokuvaus() {
        ValintaperusteSoraKuvaus kuvaus = getDummyValintaperustekuvaus();
        kuvaus.setOrganisaatioTyyppi("oppilaitostyyppi_42");
        return kuvaus;
    }

    private ValintaperusteSoraKuvaus getSotilaskorkeakoulukuvaus() {
        ValintaperusteSoraKuvaus kuvaus = getDummyValintaperustekuvaus();
        kuvaus.setOrganisaatioTyyppi("oppilaitostyyppi_43");
        return kuvaus;
    }
}
