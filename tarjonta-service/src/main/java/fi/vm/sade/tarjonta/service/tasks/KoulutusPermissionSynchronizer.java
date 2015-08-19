package fi.vm.sade.tarjonta.service.tasks;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.dao.KoulutusPermissionDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionException;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionService;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteJarjestamiskuntaDTO;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteKoulutusDTO;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteOpetuskieliDTO;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteOrgDTO;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
public class KoulutusPermissionSynchronizer {

    @Autowired
    KoulutusPermissionDAO koulutusPermissionDAO;

    @Autowired
    KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    KoulutusPermissionService koulutusPermissionService;

    @Value("${host.virkailija}")
    private String HOST_VIRKAILIJA;

    private static final Map<String, String> opetuskieliKoodiMap;
    private static final int KOMOTO_BATCH_SIZE = 500;
    private static final int VUOROKAUSI_MILLISECONDS = 1000 * 60 * 60 * 24;
    static {
        opetuskieliKoodiMap = new HashMap<String, String>();
        opetuskieliKoodiMap.put("1", "kieli_fi");
        opetuskieliKoodiMap.put("2", "kieli_sv");
        opetuskieliKoodiMap.put("4", "kieli_en");
        opetuskieliKoodiMap.put("5", "kieli_se");
    }

    final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(KoulutusPermissionSynchronizer.class);

    @Scheduled(fixedDelay = VUOROKAUSI_MILLISECONDS)
    @Transactional
    public void runUpdate() throws MalformedURLException {
        LOG.info("KoulutusPermissions start update");

        ObjectMapper objectMapper = new ObjectMapper();
        List<AmkouteOrgDTO> orgs = new ArrayList<AmkouteOrgDTO>();

        try {
            orgs = objectMapper.readValue(
                    new URL("https://oosp.csc.fi/api/public/koulutustarjonta"),
                    new TypeReference<List<AmkouteOrgDTO>>() {}
            );
        }
        catch (JsonParseException e) {
            LOG.error("KoulutusPermission update failed, JSON parse error", e);
        }
        catch (JsonMappingException e) {
            LOG.error("KoulutusPermission update failed, JSON mapping error", e);
        }
        catch (IOException e) {
            LOG.error("KoulutusPermission update failed, IOException", e);
        }

        if (orgs.size() == 0) {
            LOG.error("KoulutusPermission update failed: no permissions returned in JSON");
        }
        else {
            updatePermissionsToDb(orgs);
            LOG.info("KoulutusPermissions updated");
        }
    }

    @Transactional
    @Scheduled(fixedDelay = VUOROKAUSI_MILLISECONDS)
    public void checkExistingKoulutus() {
        LOG.info("Amkoute: check existing koulutus start");

        List<ToteutustyyppiEnum> tyyppis = Lists.newArrayList(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        List<KoulutusmoduuliToteutus> komotos;
        Map<String, List<KoulutusPermissionException>> orgsWithInvalidKomotos = new HashMap<String, List<KoulutusPermissionException>>();
        int offset = 0;

        do {
            komotos = koulutusmoduuliToteutusDAO.findFutureKoulutukset(tyyppis, offset, KOMOTO_BATCH_SIZE);
            offset += KOMOTO_BATCH_SIZE + 100000;

            for (KoulutusmoduuliToteutus komoto : komotos) {
                try {
                    koulutusPermissionService.checkThatOrganizationIsAllowedToOrganizeEducation(komoto);
                }
                catch (KoulutusPermissionException e) {
                    e.setKomoto(komoto);
                    List<KoulutusPermissionException> invalidKomotos = orgsWithInvalidKomotos.get(e.getOrganisaationOid());
                    if (invalidKomotos == null) {
                        invalidKomotos = new ArrayList<KoulutusPermissionException>();
                    }
                    invalidKomotos.add(e);
                    orgsWithInvalidKomotos.put(e.getOrganisaationOid(), invalidKomotos);
                }
            }
        } while (!komotos.isEmpty());

        if (!orgsWithInvalidKomotos.isEmpty()) {
            sendMail(orgsWithInvalidKomotos);
        }
    }

    private void sendMail(Map<String, List<KoulutusPermissionException>> orgsWithInvalidKomotos) {
        String subject = "Tarjonnasta löydetty koulutuksia ilman järjestämisoikeutta";
        String body = "Tarjonnasta löytyi seuraavat koulutukset, joilta puuttuu järjestämisoikeus:\n\n";

        for (Map.Entry<String, List<KoulutusPermissionException>> entry : orgsWithInvalidKomotos.entrySet()) {
            KoulutusPermissionException e = entry.getValue().iterator().next();

            body += e.getOrganisaationNimi() + " (" + e.getOrganisaationOid() + ")\n";

            for (KoulutusPermissionException exception : entry.getValue()) {
                KoulutusmoduuliToteutus komoto = exception.getKomoto();
                body += "\t- https://" + HOST_VIRKAILIJA + "/tarjonta-app/#/koulutus/"
                        + komoto.getOid() + " (" + komoto.getTila().toString()
                        + ") (ei oikeutta koodiin \"" + e.getPuuttuvaKoodi() + "\")\n";
            }
        }

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("admin@oph.fi", "admin@oph.fi"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("alexistroberg@gmail.com", "Alexis Troberg"));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            LOG.info("AmkouteMail successfully sent");
        } catch (AddressException e) {
            LOG.error("AmkouteMail: Invalid recipient address", e);
        } catch (MessagingException e) {
            LOG.error("AmkouteMail: MessagingException", e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("AmkouteMail: UnsupportedEncodingException", e);
        }
    }

    public void updatePermissionsToDb(List<AmkouteOrgDTO> orgs) {
        koulutusPermissionDAO.removeAll();

        for (AmkouteOrgDTO org : orgs) {
            for (KoulutusPermission permission : convertFromDto(org)) {
                koulutusPermissionDAO.insert(permission);
            }
        }
    }

    public static List<KoulutusPermission> convertFromDto(AmkouteOrgDTO org) {
        List<KoulutusPermission> permissions = new ArrayList<KoulutusPermission>();

        // Tarkista, että organisaatiolla on kaikki tarvittavat tiedot
        if (org.getKoulutukset() == null || org.getJarjestamiskunnat() == null || org.getOpetuskielet() == null) {
            return permissions;
        }

        Map<String, KoulutusPermission> koulutusKoodit = new HashMap<String, KoulutusPermission>();

        for (AmkouteKoulutusDTO permissionDto : org.getKoulutukset()) {
            if (permissionDto.getOsaamisala() != null) {
                permissions.add(new KoulutusPermission(
                        org.getOid(),
                        "osaamisala",
                        "osaamisala_" + permissionDto.getOsaamisala(),
                        permissionDto.getAlkupvm(),
                        permissionDto.getLoppupvm()
                ));
            }

            if (permissionDto.getTutkinto() != null) {
                koulutusKoodit.put(
                        permissionDto.getTutkinto(),
                        new KoulutusPermission(
                                org.getOid(),
                                "koulutus",
                                "koulutus_" + permissionDto.getTutkinto(),
                                permissionDto.getAlkupvm(),
                                permissionDto.getLoppupvm()
                        )
                );
            }
        }

        for (AmkouteOpetuskieliDTO permissionDto : org.getOpetuskielet()) {
            String kielikoodi = opetuskieliKoodiMap.get(permissionDto.getOppilaitoksenopetuskieli());
            if (kielikoodi != null) {
                permissions.add(new KoulutusPermission(
                        org.getOid(),
                        "kieli",
                        kielikoodi,
                        permissionDto.getAlkupvm(),
                        permissionDto.getLoppupvm()
                ));
            }
        }

        for (AmkouteJarjestamiskuntaDTO permissionDto : org.getJarjestamiskunnat()) {
            permissions.add(new KoulutusPermission(
                    org.getOid(),
                    "kunta",
                    "kunta_" + permissionDto.getKunta(),
                    permissionDto.getAlkupvm(),
                    permissionDto.getLoppupvm()
            ));
        }

        permissions.addAll(koulutusKoodit.values());

        return permissions;
    }

}