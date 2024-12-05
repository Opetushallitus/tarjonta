package fi.vm.sade.tarjonta.service.impl.aspects; /*
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

import fi.vm.sade.log.client.LoggerHelper;
import fi.vm.sade.log.model.Tapahtuma;
import fi.vm.sade.tarjonta.service.types.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author: Tuomas Katva Date: 12.8.2013
 */
@Aspect
public class AuditLogAspect {

  private static final Logger LOG = LoggerFactory.getLogger(AuditLogAspect.class);

  @Autowired private fi.vm.sade.log.client.Logger auditLogger;

  public static final String INSERT_OPERATION = "Insert";
  public static final String UPDATE_OPERATION = "Update";
  public static final String DELETE_OPERATION = "Delete";

  public static final int OPERATION_INSERT = 1;
  public static final int OPERATION_UPDATE = 2;
  public static final int OPERATION_DELETE = 3;

  public static final String HAKU_TYPE = "Haku";
  public static final String HAKUKOHDE_TYPE = "Hakukohde";
  public static final String KOULUTUS_TYPE = "Koulutus";
  public static final String VALINTAPERUSTEKUVAUS_TYPE = "Valintaperustekuvaus/Sora-vaatimukset";
  public static final String SYSTEM = "tarjonta-service";

  private void init() {
    LoggerHelper.init(auditLogger);
  }

  // Haku pointcuts -->
  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.paivitaHaku(..))")
  private Object updateHakuAudit(ProceedingJoinPoint pjp) throws Throwable {
    init();
    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_UPDATE);
    return result;
  }

  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.lisaaHaku(..))")
  private Object insertHakuAudit(ProceedingJoinPoint pjp) throws Throwable {
    init();
    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_INSERT);
    return result;
  }

  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.poistaHaku(..))")
  private Object deleteHakuAudit(ProceedingJoinPoint pjp) throws Throwable {
    init();
    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_DELETE);
    return result;
  }

  // <--
  // Hakukohde pointcuts
  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.lisaaHakukohde(..))")
  private Object insertHakukohdeAudit(ProceedingJoinPoint pjp) throws Throwable {
    LoggerHelper.init(auditLogger);

    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_INSERT);

    LoggerHelper.log();
    return result;
  }

  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.paivitaHakukohde(..))")
  private Object updateHakukohdeAudit(ProceedingJoinPoint pjp) throws Throwable {
    init();
    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_UPDATE);
    return result;
  }

  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.poistaHakukohde(..))")
  private Object deleteHakukohdeAudit(ProceedingJoinPoint pjp) throws Throwable {
    init();
    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_DELETE);
    return result;
  }

  // <--
  // Koulutus pointcuts
  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.lisaaKoulutus(..))")
  private Object insertKoulutusAudit(ProceedingJoinPoint pjp) throws Throwable {
    init();
    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_INSERT);
    return result;
  }

  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.paivitaKoulutus(..))")
  private Object updateKoulutusAudit(ProceedingJoinPoint pjp) throws Throwable {
    init();
    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_UPDATE);
    return result;
  }

  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.poistaKoulutus(..))")
  private Object deleteKoulutusAudit(ProceedingJoinPoint pjp) throws Throwable {
    init();
    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_DELETE);
    return result;
  }

  // <--
  // Valintaperustekuvaus pointcut
  @Around(
      "execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.tallennaMetadata(..))")
  private Object updateValintaPerusteKuvausAudit(ProceedingJoinPoint pjp) throws Throwable {
    init();
    Object result = pjp.proceed();
    logHakuAuditAdvice(pjp, result, OPERATION_UPDATE);
    return result;
  }

  private void logHakuAuditAdvice(JoinPoint pjp, Object result, int operationType)
      throws Throwable {

    switch (operationType) {
      case OPERATION_INSERT:
        if (result instanceof HakuTyyppi) {
          HakuTyyppi hakuTyyppi = (HakuTyyppi) result;
          logAuditTapahtuma(constructHakuTapahtuma(hakuTyyppi, null, OPERATION_INSERT));
        }

        if (result instanceof HakukohdeTyyppi) {
          HakukohdeTyyppi hakukohdeTyyppi = (HakukohdeTyyppi) result;
          logAuditTapahtuma(constructHakukohdeTapahtuma(hakukohdeTyyppi, null, OPERATION_INSERT));
        }

        if (pjp.getArgs()[0] instanceof LisaaKoulutusTyyppi) {

          LisaaKoulutusTyyppi koulutusTyyppi = (LisaaKoulutusTyyppi) pjp.getArgs()[0];
          logAuditTapahtuma(constructAddKoulutusTapahtuma(koulutusTyyppi, OPERATION_INSERT));
        }

        break;

      case OPERATION_UPDATE:
        if (result instanceof HakuTyyppi) {

          HakuTyyppi hakuTyyppi = (HakuTyyppi) result;
          logAuditTapahtuma(
              constructHakuTapahtuma(
                  hakuTyyppi,
                  pjp.getArgs()[0] instanceof HakuTyyppi ? (HakuTyyppi) pjp.getArgs()[0] : null,
                  OPERATION_UPDATE));
        }

        if (result instanceof HakukohdeTyyppi) {
          HakukohdeTyyppi hakukohdeTyyppi = (HakukohdeTyyppi) result;
          logAuditTapahtuma(
              constructHakukohdeTapahtuma(
                  hakukohdeTyyppi,
                  pjp.getArgs()[0] instanceof HakukohdeTyyppi
                      ? (HakukohdeTyyppi) pjp.getArgs()[0]
                      : null,
                  OPERATION_UPDATE));
        }

        if (pjp.getArgs()[0] instanceof PaivitaKoulutusTyyppi) {
          PaivitaKoulutusTyyppi paivitaKoulutusTyyppi = (PaivitaKoulutusTyyppi) pjp.getArgs()[0];
          logAuditTapahtuma(
              constructUpdateKoulutusTapahtuma(paivitaKoulutusTyyppi, OPERATION_UPDATE));
        }

        if (result instanceof MonikielinenMetadataTyyppi) {
          MonikielinenMetadataTyyppi monikielinenMetadataTyyppi =
              (MonikielinenMetadataTyyppi) result;
          logAuditTapahtuma(constructMetadataTapahtuma(monikielinenMetadataTyyppi));
        }
        break;

      case OPERATION_DELETE:
        if (pjp.getArgs()[0] instanceof HakuTyyppi) {
          HakuTyyppi hakuTyyppi = (HakuTyyppi) pjp.getArgs()[0];

          logAuditTapahtuma(constructHakuTapahtuma(hakuTyyppi, null, OPERATION_DELETE));
        }

        if (pjp.getArgs()[0] instanceof HakukohdeTyyppi) {
          HakukohdeTyyppi hakukohdeTyyppi = (HakukohdeTyyppi) pjp.getArgs()[0];

          logAuditTapahtuma(constructHakukohdeTapahtuma(hakukohdeTyyppi, null, OPERATION_DELETE));
        }

        if (pjp.getArgs()[0] instanceof String) {
          String oid = (String) pjp.getArgs()[0];
          logAuditTapahtuma(constructRemoveKoulutusTapahtuma(oid, OPERATION_DELETE));
        }

        break;
    }
  }

  private void logAuditTapahtuma(Tapahtuma tapahtuma) {
    // Get the (possible) composite log event that can contain "sub" events and update it.
    Tapahtuma rootTapahtuma = LoggerHelper.getAuditRootTapahtuma();
    rootTapahtuma.setHost(tapahtuma.getHost());
    rootTapahtuma.setSystem(tapahtuma.getSystem());
    rootTapahtuma.setTarget(tapahtuma.getTarget());
    rootTapahtuma.setTargetType(tapahtuma.getTargetType());
    rootTapahtuma.setTimestamp(tapahtuma.getTimestamp());
    rootTapahtuma.setType(tapahtuma.getType());
    rootTapahtuma.setUser(tapahtuma.getUser());
    rootTapahtuma.setUserActsForUser(tapahtuma.getUserActsForUser());

    // Log the root event
    LoggerHelper.log();
  }

  private String getTekija() {
    if (SecurityContextHolder.getContext() != null
        && SecurityContextHolder.getContext().getAuthentication() != null) {
      return SecurityContextHolder.getContext().getAuthentication().getName();
    } else {
      return null;
    }
  }

  private Tapahtuma constructHakuTapahtuma(
      HakuTyyppi haku, HakuTyyppi vanhaHaku, int tapahtumaTyyppi) {
    Tapahtuma t = null;
    if (tapahtumaTyyppi == OPERATION_DELETE) {
      t = Tapahtuma.createDELETE(SYSTEM, getTekija(), HAKU_TYPE, haku.getOid());
    }
    if (tapahtumaTyyppi == OPERATION_INSERT) {
      t = Tapahtuma.createCREATE(SYSTEM, getTekija(), HAKU_TYPE, haku.getOid());
    }
    if (tapahtumaTyyppi == OPERATION_UPDATE) {
      t = Tapahtuma.createUPDATE(SYSTEM, getTekija(), HAKU_TYPE, haku.getOid());
    }

    return t;
  }

  private Tapahtuma constructHakukohdeTapahtuma(
      HakukohdeTyyppi hakukohde, HakukohdeTyyppi vanhaHakukohde, int tapahtumaTyyppi) {
    Tapahtuma t = null;
    if (tapahtumaTyyppi == OPERATION_DELETE) {
      t = Tapahtuma.createDELETE(SYSTEM, getTekija(), HAKUKOHDE_TYPE, hakukohde.getOid());
    }
    if (tapahtumaTyyppi == OPERATION_INSERT) {
      t = Tapahtuma.createCREATE(SYSTEM, getTekija(), HAKUKOHDE_TYPE, hakukohde.getOid());
    }
    if (tapahtumaTyyppi == OPERATION_UPDATE) {
      t = Tapahtuma.createUPDATE(SYSTEM, getTekija(), HAKUKOHDE_TYPE, hakukohde.getOid());
    }

    return t;
  }

  private Tapahtuma constructAddKoulutusTapahtuma(
      LisaaKoulutusTyyppi toteutus, int tapahtumaTyyppi) {
    Tapahtuma t = null;
    if (tapahtumaTyyppi == OPERATION_DELETE) {
      t = Tapahtuma.createDELETE(SYSTEM, getTekija(), KOULUTUS_TYPE, toteutus.getOid());
    }
    if (tapahtumaTyyppi == OPERATION_INSERT) {
      t = Tapahtuma.createCREATE(SYSTEM, getTekija(), KOULUTUS_TYPE, toteutus.getOid());
    }
    if (tapahtumaTyyppi == OPERATION_UPDATE) {
      t = Tapahtuma.createUPDATE(SYSTEM, getTekija(), KOULUTUS_TYPE, toteutus.getOid());
    }

    return t;
  }

  private Tapahtuma constructRemoveKoulutusTapahtuma(String oid, int tapahtumaTyyppi) {
    Tapahtuma t = Tapahtuma.createDELETE(SYSTEM, getTekija(), KOULUTUS_TYPE, oid);
    return t;
  }

  private Tapahtuma constructUpdateKoulutusTapahtuma(
      PaivitaKoulutusTyyppi toteutus, int tapahtumaTyyppi) {
    Tapahtuma t = null;
    if (tapahtumaTyyppi == OPERATION_DELETE) {
      t = Tapahtuma.createDELETE(SYSTEM, getTekija(), KOULUTUS_TYPE, toteutus.getOid());
    }
    if (tapahtumaTyyppi == OPERATION_INSERT) {
      t = Tapahtuma.createCREATE(SYSTEM, getTekija(), KOULUTUS_TYPE, toteutus.getOid());
    }
    if (tapahtumaTyyppi == OPERATION_UPDATE) {
      t = Tapahtuma.createUPDATE(SYSTEM, getTekija(), KOULUTUS_TYPE, toteutus.getOid());
    }

    return t;
  }

  private Tapahtuma constructMetadataTapahtuma(MonikielinenMetadataTyyppi meta) {
    String target = meta.getKategoria() + ":" + meta.getAvain() + ":" + meta.getKieli();
    Tapahtuma t = Tapahtuma.createUPDATE(SYSTEM, getTekija(), VALINTAPERUSTEKUVAUS_TYPE, target);
    return t;
  }
}
