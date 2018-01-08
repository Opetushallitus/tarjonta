package fi.vm.sade.tarjonta.service.tasks;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.tarjonta.dao.KoulutusPermissionDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionException;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionService;
import fi.vm.sade.tarjonta.shared.UrlConfiguration;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteOrgDTO;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;

@Service
public class KoulutusPermissionSynchronizer {

    private final KoulutusPermissionDAO koulutusPermissionDAO;
    private final KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    private final KoulutusPermissionService koulutusPermissionService;
    private final UrlConfiguration urlConfiguration;

    @Value("${invalid.koulutus.report.recipient}")
    private String RECIPIENT;

    @Value("${smtp.host}")
    private String SMTP_HOST;
    @Value("${smtp.port}")
    private String SMTP_PORT;
    @Value("${smtp.sender}")
    private String SMTP_SENDER;
    @Value("${smtp.use_tls:false}")
    private boolean SMTP_USE_TLS;
    @Value("${smtp.authenticate:false}")
    private boolean SMTP_AUTHENTICATE;
    @Value("${smtp.username}")
    private String SMTP_USERNAME;
    @Value("${smtp.password}")
    private String SMTP_PASSWORD;

    private static final int KOMOTO_BATCH_SIZE = 500;

    final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(KoulutusPermissionSynchronizer.class);

    @Autowired
    public KoulutusPermissionSynchronizer(KoulutusPermissionDAO koulutusPermissionDAO, KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO, KoulutusPermissionService koulutusPermissionService, UrlConfiguration urlConfiguration) {
        this.koulutusPermissionDAO = koulutusPermissionDAO;
        this.koulutusmoduuliToteutusDAO = koulutusmoduuliToteutusDAO;
        this.koulutusPermissionService = koulutusPermissionService;
        this.urlConfiguration = urlConfiguration;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void runUpdate() {
        LOG.info("KoulutusPermissions start update");

        ObjectMapper objectMapper = new ObjectMapper();
        List<AmkouteOrgDTO> orgs = new ArrayList<>();

        try {
            orgs = objectMapper.readValue(
                    new URL("https://oiva.minedu.fi/api/export/jarjestysluvat"),
                    new TypeReference<List<AmkouteOrgDTO>>() {}
            );
        } catch(JsonParseException e) {
            LOG.error("KoulutusPermission update failed, JSON parse error", e);
        } catch(JsonMappingException e) {
            LOG.error("KoulutusPermission update failed, JSON mapping error", e);
        } catch(IOException e) {
            LOG.error("KoulutusPermission update failed, IOException", e);
        }

        if (orgs.size() == 0) {
            LOG.error("KoulutusPermission update failed: no permissions returned in JSON");
        } else {
            updatePermissionsToDb(orgs);
            LOG.info("KoulutusPermissions updated");
        }
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkExistingKoulutus() {
        LOG.info("Amkoute: check existing koulutus start");

        Map<String, List<KoulutusPermissionException>> orgsWithInvalidKomotos = getOrgsWithInvalidKomotos();

        if (!orgsWithInvalidKomotos.isEmpty()) {
            sendMail(orgsWithInvalidKomotos);
        }
    }

    public Map<String, List<KoulutusPermissionException>> getOrgsWithInvalidKomotos() {
        List<ToteutustyyppiEnum> tyyppis = KoulutusPermissionService.toteustustyyppisToCheckPermissionFor();
        List<KoulutusmoduuliToteutus> komotos;
        Map<String, List<KoulutusPermissionException>> orgsWithInvalidKomotos = new HashMap<>();
        int offset = 0;

        do {
            komotos = koulutusmoduuliToteutusDAO.findFutureKoulutukset(tyyppis, offset, KOMOTO_BATCH_SIZE);
            offset += KOMOTO_BATCH_SIZE;

            for (KoulutusmoduuliToteutus komoto : komotos) {
                try {
                    koulutusPermissionService.checkThatOrganizationIsAllowedToOrganizeEducation(komoto);
                } catch(KoulutusPermissionException e) {
                    LOG.warn("Found koulutus without Oiva permission", e);
                    e.setKomoto(komoto);
                    List<KoulutusPermissionException> invalidKomotos = orgsWithInvalidKomotos.get(e.getOrganisaationOid());
                    if (invalidKomotos == null) {
                        invalidKomotos = new ArrayList<>();
                    }
                    invalidKomotos.add(e);
                    orgsWithInvalidKomotos.put(e.getOrganisaationOid(), invalidKomotos);
                }
            }
        } while(!komotos.isEmpty());

        return orgsWithInvalidKomotos;
    }

    private void sendMail(Map<String, List<KoulutusPermissionException>> orgsWithInvalidKomotos) {
        String subject = "Tarjonnasta löydetty koulutuksia ilman järjestämisoikeutta";
        StringBuilder body = new StringBuilder("Tarjonnasta löytyi seuraavat koulutukset, joilta puuttuu järjestämisoikeus:\n\n");

        for (Map.Entry<String, List<KoulutusPermissionException>> entry : orgsWithInvalidKomotos.entrySet()) {
            KoulutusPermissionException firstException = entry.getValue().iterator().next();

            body
                    .append("\n")
                    .append(firstException.getOrganisaationNimi())
                    .append(" (")
                    .append(firstException.getOrganisaationOid())
                    .append(")\n");

            for (KoulutusPermissionException exception : entry.getValue()) {
                KoulutusmoduuliToteutus komoto = exception.getKomoto();
                body
                        .append("\t")
                        .append(urlConfiguration.url("tarjonta-app.koulutus", komoto.getOid()))
                        .append(" (")
                        .append(komoto.getTila().toString())
                        .append(") (ei oikeutta koodiin \"")
                        .append(exception.getKohdeKoodi())
                        .append("\")\n");
            }
        }

        Session session = createMailSession();

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SMTP_SENDER, SMTP_SENDER));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(RECIPIENT, RECIPIENT));
            msg.setSubject(subject);
            msg.setText(body.toString());
            Transport.send(msg);
            LOG.info("AmkouteMail successfully sent");
        } catch(AddressException e) {
            LOG.error("AmkouteMail: Invalid recipient address", e);
        } catch(MessagingException e) {
            LOG.error("AmkouteMail: MessagingException", e);
        } catch(UnsupportedEncodingException e) {
            LOG.error("AmkouteMail: UnsupportedEncodingException", e);
        }
    }

    private Session createMailSession() {
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", SMTP_HOST);
        mailProps.put("mail.smtp.port", SMTP_PORT);
        mailProps.put("mail.smtp.auth", SMTP_AUTHENTICATE);
        mailProps.put("mail.starttls.enable", SMTP_USE_TLS);

        if (SMTP_AUTHENTICATE) {
            return Session.getInstance(mailProps, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });
        } else {
            return Session.getInstance(mailProps);
        }
    }

    public void updatePermissionsToDb(List<AmkouteOrgDTO> orgs) {
        koulutusPermissionDAO.removeAll();
        for (KoulutusPermission permission : KoulutusPermissionCreator.convertFromDto(orgs)) {
            koulutusPermissionDAO.insert(permission);
        }
    }
}