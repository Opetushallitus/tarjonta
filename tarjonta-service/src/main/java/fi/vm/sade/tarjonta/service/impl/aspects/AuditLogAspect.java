package fi.vm.sade.tarjonta.service.impl.aspects;/*
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

import fi.vm.sade.log.model.Tapahtuma;
import fi.vm.sade.tarjonta.service.types.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

/**
 * @author: Tuomas Katva
 * Date: 12.8.2013
 */
@Aspect
public class AuditLogAspect {

    @Autowired
    private fi.vm.sade.log.client.Logger auditLogger;

    protected static final Logger LOGGER = LoggerFactory.getLogger(AuditLogAspect.class);

    public static final String INSERT_OPERATION = "Insert";
    public static final String UPDATE_OPERATION = "Update";
    public static final String DELETE_OPERATION = "Delete";

    public static final int OPERATION_INSERT  = 1;
    public static final int OPERATION_UPDATE = 2;
    public static final int OPERATION_DELETE = 3;

    public static final String HAKU_TYPE = "Haku";
    public static final String HAKUKOHDE_TYPE = "Hakukohde";
    public static final String KOULUTUS_TYPE = "Koulutus";
    public static final String VALINTAPERUSTEKUVAUS_TYPE = "Valintaperustekuvaus/Sora-vaatimukset";

    //Haku pointcuts -->
    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.paivitaHaku(..))")
    private void updateHakuAudit(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object result = pjp.proceed();
            logHakuAuditAdvice(pjp,result,OPERATION_UPDATE);
        } catch (Exception exp) {
           LOGGER.warn("Unable to send audit log message: {}",exp.toString());
        }

    }
    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.lisaaHaku(..))")
    private void insertHakuAudit(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object result = pjp.proceed();
            logHakuAuditAdvice(pjp,result,OPERATION_INSERT);
        } catch (Exception exp) {
            LOGGER.warn("Unable to send audit log message: {}",exp.toString());
        }

    }

    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.poistaHaku(..))")
    private void deleteHakuAudit(ProceedingJoinPoint pjp) throws Throwable {
         try {
         Object result = pjp.proceed();
         logHakuAuditAdvice(pjp,result,OPERATION_DELETE);
         } catch (Exception exp) {
             LOGGER.warn("Unable to send audit log message: {}",exp.toString());
         }
    }

    //<--

    //Hakukohde pointcuts
    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.lisaaHakukohde(..))")
    private void insertHakukohdeAudit(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object result = pjp.proceed();
            logHakuAuditAdvice(pjp,result,OPERATION_INSERT);
        }  catch (Exception exp ) {
            LOGGER.warn("Unable to send audit log message: {}",exp.toString());
        }

    }

    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.paivitaHakukohde(..))")
    private void updateHakukohdeAudit(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object result = pjp.proceed();
            logHakuAuditAdvice(pjp,result,OPERATION_UPDATE);
        }  catch (Exception exp) {
            LOGGER.warn("Unable to send audit log message: {}",exp.toString());
        }

    }

    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.poistaHakukohde(..))")
    private void deleteHakukohdeAudit(ProceedingJoinPoint pjp) throws Throwable {
       try {
           Object result = pjp.proceed();
           logHakuAuditAdvice(pjp,result,OPERATION_DELETE);
       } catch (Exception exp ) {
           LOGGER.warn("Unable to send audit log message: {}",exp.toString());
       }

    }

    //<--

    //Koulutus pointcuts

    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.lisaaKoulutus(..))")
    private void insertKoulutusAudit(ProceedingJoinPoint pjp) throws Throwable{
        try {
            Object result = pjp.proceed();
            logHakuAuditAdvice(pjp,result,OPERATION_INSERT);
        } catch (Exception exp) {
            LOGGER.warn("Unable to send audit log message: {}",exp.toString());
        }

    }

    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.paivitaKoulutus(..))")
    private void updateKoulutusAudit(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object result = pjp.proceed();
            logHakuAuditAdvice(pjp,result,OPERATION_UPDATE);
        } catch (Exception exp) {
            LOGGER.warn("Unable to send audit log message: {}",exp.toString());
        }

    }

    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.poistaKoulutus(..))")
    private void deleteKoulutusAudit(ProceedingJoinPoint pjp) throws Throwable {
        try {

            Object result = pjp.proceed();
            logHakuAuditAdvice(pjp,result,OPERATION_DELETE);
        } catch (Exception exp ){
            LOGGER.warn("Unable to send audit log message: {}",exp.toString());
        }

    }

    //<--

    //Valintaperustekuvaus pointcut
    @Around("execution(public * fi.vm.sade.tarjonta.service.impl.TarjontaAdminServiceImpl.tallennaMetadata(..))")
    private void updateValintaPerusteKuvausAudit(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object result = pjp.proceed();
            logHakuAuditAdvice(pjp,result,OPERATION_UPDATE);
        } catch (Exception exp) {
            LOGGER.warn("Unable to send audit log message: {}",exp.toString());
        }

    }


    private void logHakuAuditAdvice(JoinPoint pjp, Object result,int  operationType) throws Throwable {

        switch (operationType) {

            case OPERATION_INSERT :

                if (result instanceof HakuTyyppi) {
                    HakuTyyppi hakuTyyppi = (HakuTyyppi)result;
                    logAuditTapahtuma(constructHakuTapahtuma(hakuTyyppi,null,INSERT_OPERATION));
                }

                if (result instanceof HakukohdeTyyppi) {
                    HakukohdeTyyppi hakukohdeTyyppi = (HakukohdeTyyppi)result;
                    logAuditTapahtuma(constructHakukohdeTapahtuma(hakukohdeTyyppi,null,INSERT_OPERATION));
                }

                if (pjp.getArgs()[0] instanceof LisaaKoulutusTyyppi) {

                    LisaaKoulutusTyyppi koulutusTyyppi = (LisaaKoulutusTyyppi)pjp.getArgs()[0];
                    logAuditTapahtuma(constructAddKoulutusTapahtuma(koulutusTyyppi,INSERT_OPERATION));
                }

                break;

            case OPERATION_UPDATE :
                if (result instanceof HakuTyyppi) {

                    HakuTyyppi hakuTyyppi = (HakuTyyppi)result;
                    logAuditTapahtuma(constructHakuTapahtuma(hakuTyyppi,
                            pjp.getArgs()[0] instanceof HakuTyyppi ? (HakuTyyppi)pjp.getArgs()[0] : null
                            ,UPDATE_OPERATION));

                }

                if (result instanceof HakukohdeTyyppi ) {
                    HakukohdeTyyppi hakukohdeTyyppi = (HakukohdeTyyppi)result;
                    logAuditTapahtuma(constructHakukohdeTapahtuma(hakukohdeTyyppi,
                            pjp.getArgs()[0] instanceof HakukohdeTyyppi ? (HakukohdeTyyppi)pjp.getArgs()[0] : null,
                            UPDATE_OPERATION));
                }

                if (pjp.getArgs()[0] instanceof  PaivitaKoulutusTyyppi) {
                    PaivitaKoulutusTyyppi paivitaKoulutusTyyppi = (PaivitaKoulutusTyyppi)pjp.getArgs()[0];
                    logAuditTapahtuma(constructUpdateKoulutusTapahtuma(paivitaKoulutusTyyppi,UPDATE_OPERATION));
                }

                if (result instanceof MonikielinenMetadataTyyppi) {
                    MonikielinenMetadataTyyppi monikielinenMetadataTyyppi = (MonikielinenMetadataTyyppi)result;
                    logAuditTapahtuma(constructMetadataTapahtuma(monikielinenMetadataTyyppi));
                }
                break;

            case OPERATION_DELETE :

                if (pjp.getArgs()[0] instanceof HakuTyyppi) {
                    HakuTyyppi hakuTyyppi = (HakuTyyppi)pjp.getArgs()[0];

                  logAuditTapahtuma(constructHakuTapahtuma(hakuTyyppi,null,DELETE_OPERATION));
                }

                if (pjp.getArgs()[0] instanceof HakukohdeTyyppi) {
                    HakukohdeTyyppi hakukohdeTyyppi = (HakukohdeTyyppi)pjp.getArgs()[0];

                    logAuditTapahtuma(constructHakukohdeTapahtuma(hakukohdeTyyppi,null,DELETE_OPERATION));
                }

                if (pjp.getArgs()[0] instanceof  String) {
                    String oid = (String)pjp.getArgs()[0];
                    logAuditTapahtuma(constructRemoveKoulutusTapahtuma(oid,DELETE_OPERATION));
                }

                break;


        }

    }

    private void logAuditTapahtuma(Tapahtuma tapahtuma) {
        try {

            if (tapahtuma.getUusiArvo() != null && tapahtuma.getAikaleima() != null) {

                auditLogger.log(tapahtuma);
            }

        } catch (Exception e) {
            e.printStackTrace();

            LOGGER.warn("Unable to send audit log message {}", e.toString());
        }
    }

    private String getTekija() {

        StringBuilder stb = new StringBuilder();
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
                stb.append(SecurityContextHolder.getContext().getAuthentication().getName() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : "");
                stb.append("/");
                    if (SecurityContextHolder.getContext().getAuthentication().getCredentials() != null)  {
                    stb.append(SecurityContextHolder.getContext().getAuthentication().getCredentials());
                    stb.append("/");
                    }
                    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
                        stb.append(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                    }
        }
        return stb.toString();
    }

    private Tapahtuma constructHakuTapahtuma(HakuTyyppi haku, HakuTyyppi vanhaHaku, String tapahtumaTyyppi) {
        Tapahtuma tapahtuma = new Tapahtuma();
        tapahtuma.setAikaleima(new Date());
        tapahtuma.setMuutoksenKohde(HAKU_TYPE);
        tapahtuma.setTapahtumatyyppi(tapahtumaTyyppi);
        try {
            tapahtuma.setTekija(getTekija());
        } catch (Exception exp) {
        }
        if (haku.getOid() != null) {
           


            tapahtuma.setUusiArvo(constructHakuArvo(haku));
        }

        if (vanhaHaku != null) {
           if (vanhaHaku.getOid() != null) {
              tapahtuma.setVanhaArvo(constructHakuArvo(vanhaHaku));
           }
        }

        return tapahtuma;
    }
    
    private String constructHakuArvo(HakuTyyppi hakuTyyppi) {
        StringBuilder arvo = new StringBuilder();
        arvo.append(hakuTyyppi.getHaunTunniste() != null ? hakuTyyppi.getHaunTunniste() : hakuTyyppi.getOid());
        arvo.append(", ");
        for (HaunNimi haunNimi:hakuTyyppi.getHaunKielistetytNimet()) {
            arvo.append(haunNimi.getKielikoodi() + " : " + haunNimi.getNimi() + " ");
        }
        
        return arvo.toString();
    }


    private Tapahtuma constructHakukohdeTapahtuma(HakukohdeTyyppi hakukohde, HakukohdeTyyppi vanhaHakukohde , String tapahtumaTyyppi) {
        Tapahtuma tapahtuma = new Tapahtuma();
        tapahtuma.setAikaleima(new Date());
        tapahtuma.setMuutoksenKohde(HAKUKOHDE_TYPE);
        try {
            tapahtuma.setTekija(getTekija());
        } catch (Exception exp) {
        }

        tapahtuma.setTapahtumatyyppi(tapahtumaTyyppi);
        tapahtuma.setUusiArvo(constructArvo(hakukohde));

        if (vanhaHakukohde != null )  {
            if (vanhaHakukohde.getOid() != null) {
                tapahtuma.setVanhaArvo(constructArvo(vanhaHakukohde));
            }
        }

        return tapahtuma;
    }

    private String constructArvo(HakukohdeTyyppi hakukohde) {
        String uusiArvo;
        if (hakukohde.getOid() != null && hakukohde.getHakukohdeKoodistoNimi() != null) {
            uusiArvo = "Hakukohde oid : " + hakukohde.getOid() + ", hakukohde nimi : " + hakukohde.getHakukohdeKoodistoNimi() != null ? hakukohde.getHakukohdeKoodistoNimi() : "";
        } else {
            uusiArvo = "Hakukohde oid:  " + hakukohde.getOid();
        }

        return uusiArvo;
    }

    private Tapahtuma constructAddKoulutusTapahtuma(LisaaKoulutusTyyppi toteutus, String tapahtumaTyyppi) {
        Tapahtuma tapahtuma = new Tapahtuma();


        tapahtuma.setAikaleima(new Date());
        tapahtuma.setMuutoksenKohde(KOULUTUS_TYPE);
        tapahtuma.setTapahtumatyyppi(tapahtumaTyyppi);
        try {
            tapahtuma.setTekija(getTekija());
        } catch (Exception exp) {
        }
        if (toteutus.getOid() != null) {
          StringBuilder stb = new StringBuilder();
          stb.append("Koulutus oid : " + toteutus.getOid());
          if (toteutus.getNimi() != null && toteutus.getNimi().getTeksti() != null) {
          for (MonikielinenTekstiTyyppi.Teksti teksti : toteutus.getNimi().getTeksti()) {
              stb.append(" " + teksti.getKieliKoodi() + " " + teksti.getValue());
          }
          }
         tapahtuma.setUusiArvo(stb.toString());
        }


        return tapahtuma;
    }

    private Tapahtuma constructRemoveKoulutusTapahtuma(String oid, String tapahtumaTyyppi) {
        Tapahtuma tapahtuma = new Tapahtuma();
        tapahtuma.setAikaleima(new Date());
        tapahtuma.setMuutoksenKohde(KOULUTUS_TYPE);
        tapahtuma.setTapahtumatyyppi(tapahtumaTyyppi);
        try {
            tapahtuma.setTekija(getTekija());
        } catch (Exception exp) {
        }
        tapahtuma.setUusiArvo("OID : " + oid);

        return tapahtuma;
    }

    private Tapahtuma constructUpdateKoulutusTapahtuma(PaivitaKoulutusTyyppi toteutus, String tapahtumaTyyppi) {
        Tapahtuma tapahtuma = new Tapahtuma();
        tapahtuma.setAikaleima(new Date());
        tapahtuma.setMuutoksenKohde(KOULUTUS_TYPE);
        tapahtuma.setTapahtumatyyppi(tapahtumaTyyppi);
        try {
            tapahtuma.setTekija(getTekija());
        } catch (Exception exp) {
        }
        if (toteutus.getOid() != null) {
            StringBuilder stb = new StringBuilder();
            stb.append("Koulutus oid : " + toteutus.getOid());
            if (toteutus.getNimi() != null && toteutus.getNimi().getTeksti() != null ) {
            for (MonikielinenTekstiTyyppi.Teksti teksti : toteutus.getNimi().getTeksti()) {
                stb.append(" " + teksti.getKieliKoodi() + " " + teksti.getValue());
            }
            }
            tapahtuma.setUusiArvo(stb.toString());
        }

        return tapahtuma;
    }

    private Tapahtuma constructMetadataTapahtuma(MonikielinenMetadataTyyppi meta) {
        Tapahtuma tapahtuma = new Tapahtuma();
        tapahtuma.setAikaleima(new Date());
        tapahtuma.setMuutoksenKohde(VALINTAPERUSTEKUVAUS_TYPE);

        try {
            tapahtuma.setTekija(getTekija());
        } catch (Exception exp) {
        }

        tapahtuma.setTapahtumatyyppi(UPDATE_OPERATION);

        tapahtuma.setUusiArvo(constructValintaPerusteKuvausArvo(meta));


        return tapahtuma;
    }

    private String constructValintaPerusteKuvausArvo(MonikielinenMetadataTyyppi meta) {
        StringBuilder sb = new StringBuilder();

        sb.append("Tyyppi : " + meta.getKategoria() + ", ");
        sb.append("Uri/avain : " + meta.getAvain() + ", ");
        sb.append("Kieli : " + meta.getKieli() +", ");
        sb.append("Arvo : "+ meta.getArvo());

        return sb.toString();
    }

}
