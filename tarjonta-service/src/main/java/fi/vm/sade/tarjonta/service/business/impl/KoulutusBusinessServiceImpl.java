/*
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
package fi.vm.sade.tarjonta.service.business.impl;

import com.google.common.base.Preconditions;
import fi.vm.sade.oidgenerator.OIDGenerator;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.YhteyshenkiloDAO;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.BaseEntity;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaVirheKoodi;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.lang.reflect.Method;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** */
@Service
@Transactional
public class KoulutusBusinessServiceImpl implements KoulutusBusinessService {

  private static final Logger LOG = LoggerFactory.getLogger(KoulutusBusinessServiceImpl.class);

  @Autowired private KoulutusmoduuliDAO koulutusmoduuliDAO;
  @Autowired private KoulutusmoduuliToteutusDAOImpl koulutusmoduuliToteutusDAO;
  @Autowired private YhteyshenkiloDAO yhteyshenkiloDAO;

  @Override
  public Koulutusmoduuli create(Koulutusmoduuli moduuli) {

    return koulutusmoduuliDAO.insert(moduuli);
  }

  @Override
  public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, Koulutusmoduuli moduuli) {

    final Koulutusmoduuli m = isNew(moduuli) ? create(moduuli) : moduuli;
    toteutus.setKoulutusmoduuli(m);

    return (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.insert(toteutus);
  }

  @Override
  public Koulutusmoduuli findTutkintoOhjelma(
      String koulutusLuokitusUri, String koulutusOhjelmaUri) {
    // todo: dao kerroksen voisi poistaa, ainoastaan vaikeammat haut voisi sijoittaa helper:n taakse
    return koulutusmoduuliDAO.findTutkintoOhjelma(koulutusLuokitusUri, koulutusOhjelmaUri);
  }

  @Override
  public List<Koulutusmoduuli> findTutkintoOhjelmat() {
    // todo: dao kerroksen voisi poistaa, ainoastaan vaikeammat haut voisi sijoittaa helper:n taakse
    return koulutusmoduuliDAO.findAll();
  }

  @Override
  public KoulutusmoduuliToteutus createKoulutus(LisaaKoulutusTyyppi koulutus) {
    Koulutusmoduuli moduuli = null;

    if (koulutus.getKoulutustyyppi() == null) {
      throw new TarjontaBusinessException("Undefined koulutustyyppi.");
    }

    switch (ModuulityyppiEnum.fromEnum(koulutus.getKoulutustyyppi())) {
      case AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
      case MAAHANM_AMM_VALMISTAVA_KOULUTUS:
      case MAAHANM_LUKIO_VALMISTAVA_KOULUTUS:
      case PERUSOPETUKSEN_LISAOPETUS:
      case VAPAAN_SIVISTYSTYON_KOULUTUS:
      case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS:
      case AMMATILLINEN_PERUSKOULUTUS:
        moduuli = handleToisenAsteenModuuli(koulutus);
        break;
      case LUKIOKOULUTUS:
        moduuli = handleLukiomoduuli(koulutus);
        break;
      default:
        throw new RuntimeException("Unsupported koulutustyyppi.");
    }

    if (moduuli == null) {
      throw new TarjontaBusinessException(TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value());
    }

    // a quick conversion to ToteutustyyppiEnum, we need it as long we use SOAP API.
    ToteutustyyppiEnum tt = convertToTotetustyyppi(moduuli, koulutus);

    KoulutusmoduuliToteutus komotoModel = new KoulutusmoduuliToteutus();
    EntityUtils.copyFields(koulutus, komotoModel);

    komotoModel.setViimIndeksointiPvm(komotoModel.getUpdated());
    komotoModel.setKoulutusmoduuli(moduuli);
    moduuli.addKoulutusmoduuliToteutus(komotoModel);
    komotoModel.setToteutustyyppi(tt);
    komotoModel.setAlkamiskausiUri(getKausiFromDate(komotoModel.getKoulutuksenAlkamisPvm()));
    komotoModel.setAlkamisVuosi(getYearFromDate(komotoModel.getKoulutuksenAlkamisPvm()));
    komotoModel.setKoulutuksenLoppumisPvm(komotoModel.getKoulutuksenLoppumisPvm());
    KoulutusmoduuliToteutus response = koulutusmoduuliToteutusDAO.insert(komotoModel);
    return koulutusmoduuliToteutusDAO.findByOid(response.getOid());
  }

  private String getKausiFromDate(Date aloituspvm) {
    return IndexDataUtils.parseKausiKoodi(aloituspvm);
  }

  public static Integer getYearFromDate(Date aloitusPvm) {
    return new Integer(IndexDataUtils.parseYear(aloitusPvm));
  }

  private Koulutusmoduuli handleToisenAsteenModuuli(LisaaKoulutusTyyppi koulutus) {
    Koulutusmoduuli moduuli =
        koulutusmoduuliDAO.findTutkintoOhjelma(
            koulutus.getKoulutusKoodi().getUri(), koulutus.getKoulutusohjelmaKoodi().getUri());

    if (moduuli == null) {
      throw new TarjontaBusinessException(
          TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value()
              + " - koulutus koodi : "
              + koulutus.getKoulutusKoodi().getUri()
              + ", koulutusohjelma koodi : "
              + koulutus.getKoulutusohjelmaKoodi().getUri());
    }

    // Handling the creation of the parent komoto
    handleParentKomoto(koulutus, moduuli, convertToTotetustyyppi(moduuli, koulutus));

    return moduuli;
  }

  private Koulutusmoduuli handleLukiomoduuli(LisaaKoulutusTyyppi koulutus) {
    Koulutusmoduuli moduuli =
        koulutusmoduuliDAO.findLukiolinja(
            koulutus.getKoulutusKoodi().getUri(), koulutus.getLukiolinjaKoodi().getUri());

    if (moduuli == null) {
      throw new TarjontaBusinessException(
          TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value()
              + " - koulutus koodi : "
              + koulutus.getKoulutusKoodi().getUri()
              + ", lukiolinja koodi : "
              + koulutus.getLukiolinjaKoodi().getUri());
    }

    // Handling the creation of the parent komoto
    handleParentKomoto(koulutus, moduuli, convertToTotetustyyppi(moduuli, koulutus));

    return moduuli;
  }

  /**
   * Filtteröi monikieliset tekstit, poistaen kaikki jotka ovat tyhjiä ja eri kuin opetuskieli.
   *
   * @param pkt
   */
  private void filterKieliKoodis(PaivitaKoulutusTyyppi pkt) {
    Set<String> ret = new HashSet<String>();
    for (KoodistoKoodiTyyppi kkt : pkt.getOpetuskieli()) {
      ret.add(kkt.getUri());
    }

    List<MonikielinenTekstiTyyppi> mkts = new ArrayList<MonikielinenTekstiTyyppi>();

    try {
      // haetaan mkt:t looppaamalla getterit läpi (parempi olisi pitää mkt:t enum->string mapissa)
      for (Method m : pkt.getClass().getMethods()) {
        if (m.getName().startsWith("get")
            && m.getParameterTypes().length == 0
            && m.getReturnType().equals(MonikielinenTekstiTyyppi.class)) {
          MonikielinenTekstiTyyppi mtt = (MonikielinenTekstiTyyppi) m.invoke(pkt);
          if (mtt != null) {
            mkts.add(mtt);
            for (Teksti t : mtt.getTeksti()) {
              if (t.getValue() != null && t.getValue().trim().length() > 0) {
                ret.add(t.getKieliKoodi());
              }
            }
          }
        }
      }
    } catch (Exception e) { // reflektiovirheitä varten, joita ei tietenkääns saisi tapahtua
      throw new RuntimeException(e);
    }

    for (MonikielinenTekstiTyyppi mtt : mkts) {
      for (Iterator<Teksti> i = mtt.getTeksti().iterator(); i.hasNext(); ) {
        Teksti t = i.next();
        if (!ret.contains(t.getKieliKoodi())) {
          i.remove();
        }
      }
    }
  }

  @Override
  public KoulutusmoduuliToteutus updateKoulutus(PaivitaKoulutusTyyppi koulutus) {

    final String oid = koulutus.getOid();
    KoulutusmoduuliToteutus model = koulutusmoduuliToteutusDAO.findByOid(oid);

    filterKieliKoodis(koulutus);

    if (model == null) {
      throw new TarjontaBusinessException(TarjontaVirheKoodi.OID_EI_OLEMASSA.value(), oid);
    }

    Koulutusmoduuli moduuli = model.getKoulutusmoduuli();
    // Handling the creation or update of the parent (tutkinto) komoto
    handleParentKomoto(koulutus, moduuli, model.getToteutustyyppi());

    EntityUtils.copyFields(koulutus, model);
    model.setViimIndeksointiPvm(model.getUpdated());
    model.setAlkamisVuosi(getYearFromDate(model.getKoulutuksenAlkamisPvm()));
    model.setAlkamiskausiUri(getKausiFromDate(model.getKoulutuksenAlkamisPvm()));
    koulutusmoduuliToteutusDAO.update(model);
    model = koulutusmoduuliToteutusDAO.read(model.getId());

    return model;
  }

  /*
   * Handling the creation or update of the parent komoto
   */
  private void handleParentKomoto(
      KoulutusTyyppi koulutus, Koulutusmoduuli moduuli, ToteutustyyppiEnum toteutustyyppi) {
    Preconditions.checkNotNull(toteutustyyppi, "Toteutustyyppi enum cannot be null!");

    Koulutusmoduuli parentKomo = this.koulutusmoduuliDAO.findParentKomo(moduuli);
    String pohjakoulutusUri =
        koulutus.getPohjakoulutusvaatimus() != null
            ? koulutus.getPohjakoulutusvaatimus().getUri()
            : null;
    List<KoulutusmoduuliToteutus> parentKomotos =
        this.koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(
            parentKomo, koulutus.getTarjoaja(), pohjakoulutusUri);
    KoulutusmoduuliToteutus parentKomoto =
        (parentKomotos != null && !parentKomotos.isEmpty()) ? parentKomotos.get(0) : null;
    // If the komoto for the parentKomo already exists it is updated according to the values given
    // in koulutus

    if (parentKomoto != null && parentKomo != null) {
      // parentKomoto.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva()); koulutuksen
      // alkamispäivä is no longer saved in parent komoto
      EntityUtils.copyFields(
          parentKomoto.getTekstit(), koulutus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
      // parentKomoto.setKoulutusohjelmanValinta(EntityUtils.copyFields(koulutus.getKoulutusohjelmanValinta(), parentKomoto.getKoulutusohjelmanValinta()));
      // parentKomoto.setOpetuskieli(EntityUtils.toKoodistoUriSet(koulutus.getOpetuskieli()));
      if (parentKomoto.getToteutustyyppi() == null) {
        parentKomoto.setToteutustyyppi(toteutustyyppi); // only for future/angular use
      }
      this.koulutusmoduuliToteutusDAO.update(parentKomoto);

      // Start date is updated to siblings of the komoto given in koulutus. The start date is
      // replicated to the children of the parent komoto to enable more efficient search based
      // on start year and semester of komotos.
      // handleChildKomos(parentKomo, moduuli, koulutus);
      // If there is not a komoto for the parentKomo, it is created here.
    } else if (parentKomo != null) {
      parentKomoto = new KoulutusmoduuliToteutus();
      generateOidForKomoto(parentKomoto);
      // parentKomoto.setOpetuskieli(EntityUtils.toKoodistoUriSet(koulutus.getOpetuskieli()));
      parentKomoto.setTarjoaja(koulutus.getTarjoaja());
      parentKomoto.setTila(EntityUtils.convertTila(koulutus.getTila()));
      parentKomoto.setKoulutusmoduuli(parentKomo);
      parentKomoto.setToteutustyyppi(toteutustyyppi); // only for future/angular use
      parentKomoto.setOid(OIDGenerator.generateOID(TarjontaOidType.KOMOTO.getValue()));
      EntityUtils.copyFields(
          parentKomoto.getTekstit(), koulutus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
      // parentKomoto.setKoulutusohjelmanValinta(EntityUtils.copyFields(koulutus.getKoulutusohjelmanValinta(), parentKomoto.getKoulutusohjelmanValinta()));
      // parentKomoto.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva());
      parentKomoto.setPohjakoulutusvaatimusUri(
          koulutus.getPohjakoulutusvaatimus() != null
              ? koulutus.getPohjakoulutusvaatimus().getUri()
              : null);
      parentKomo.addKoulutusmoduuliToteutus(parentKomoto);

      LOG.warn(
          "**** handleParentKomoto - create new parent komoto: pkv = {}",
          parentKomoto.getPohjakoulutusvaatimusUri());

      if (parentKomoto.getPohjakoulutusvaatimusUri() != null
          && parentKomoto.getPohjakoulutusvaatimusUri().indexOf("#") < 0) {
        LOG.error("*** FAILING FAST *** to see where the problem lies...OVT-7849");
        throw new RuntimeException(
            "parent komoto pohjakoulutusvaatimus = " + parentKomoto.getPohjakoulutusvaatimusUri());
      }

      this.koulutusmoduuliToteutusDAO.insert(parentKomoto);
    }
  }

  private void generateOidForKomoto(KoulutusmoduuliToteutus komoto) {
    String oidCandidate = null;
    boolean oidExists = true;
    while (oidExists) {
      oidCandidate = generateOid();
      oidExists = this.koulutusmoduuliToteutusDAO.findByOid(oidCandidate) != null ? true : false;
    }
    komoto.setOid(oidCandidate);
  }

  private String generateOid() {
    return OIDGenerator.generateOID(TarjontaOidType.KOMOTO.getValue());
  }

  private boolean isNew(BaseEntity e) {
    // no good
    return (e.getId() == null);
  }

  private static ToteutustyyppiEnum convertToTotetustyyppi(
      final Koulutusmoduuli moduuli, KoulutusTyyppi koulutus) {
    ToteutustyyppiEnum tt = null;
    switch (moduuli.getKoulutustyyppiEnum()) {
      case AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
        tt = ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS;
        break;
      case MAAHANM_AMM_VALMISTAVA_KOULUTUS:
        tt =
            ToteutustyyppiEnum.MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS;
        break;
      case MAAHANM_LUKIO_VALMISTAVA_KOULUTUS:
        tt =
            ToteutustyyppiEnum
                .MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS;
        break;
      case PERUSOPETUKSEN_LISAOPETUS:
        tt = ToteutustyyppiEnum.PERUSOPETUKSEN_LISAOPETUS;
        break;
      case VAPAAN_SIVISTYSTYON_KOULUTUS:
        tt = ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS;
        break;
      case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS:
        tt = ToteutustyyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS;
        break;
      case AMMATILLINEN_PERUSKOULUTUS:
        Preconditions.checkNotNull(koulutus, "KoulutusTyyppi object cannot be null.");

        if (koulutus.getPohjakoulutusvaatimus() != null
            && koulutus
                .getPohjakoulutusvaatimus()
                .getUri()
                .contains("pohjakoulutusvaatimustoinenaste_er")) {
          // only for the 'er'
          tt = ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA;
        } else {
          // for all other code types
          tt = ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO;
        }
        break;
      case LUKIOKOULUTUS:
        tt = ToteutustyyppiEnum.LUKIOKOULUTUS;
        break;
      default:
        throw new RuntimeException("Unsupported koulutustyyppi.");
    }

    return tt;
  }
}
