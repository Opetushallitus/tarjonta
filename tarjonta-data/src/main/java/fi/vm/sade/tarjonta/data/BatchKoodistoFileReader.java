package fi.vm.sade.tarjonta.data;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BatchKoodistoFileReader {
    private final Logger log = LoggerFactory.getLogger(BatchKoodistoFileReader.class);

    @Autowired
    private UploadKoodistoData uploadKoodistoData;

    @Value("${tarjonta-data.koodisto.directory:'src/main/resources/20130320_KOODISTOJA'}")
    private String koodistoDirectory;

    @Value("${organisaatio.oid:NO_OID}")
    private String organisaatioOid;

    public void read() {
        log.info("Starting koodisto upload");
        // gather all koodisto files
        final List<File> koodistoFiles = new ArrayList<File>();
        final List<File> relaatioFiles = new ArrayList<File>();
        try {
            log.info("Filepath [{}]", koodistoDirectory);
            final File rootDir = new File(koodistoDirectory);
            if (rootDir == null || !rootDir.isDirectory()) {
                log.error("Koodisto directory is null or not a directory");
                System.exit(1);
            }
            iterateDirectories(koodistoFiles, relaatioFiles, rootDir);
        } catch (final Exception e) {
            log.error("Error reading files", e);
            System.exit(1);
        }
        log.info("Found [{}] koodisto files and [{}] relaatio files", koodistoFiles.size(), relaatioFiles.size());

        final Map<File, Exception> koodistoErrors = new HashMap<File, Exception>();
        // insert koodistos
        for (final File koodisto : koodistoFiles) {
            try {
                uploadKoodistoData.loadKoodistoFromExcel(koodisto.getAbsolutePath(), StringUtils.substringBefore(koodisto.getName().toLowerCase(), "."), organisaatioOid);
            } catch (final Exception e) {
                log.error(e.getMessage());
                koodistoErrors.put(koodisto, e);
            }
        }

        final Map<File, Exception> relaatioErrors = new HashMap<File, Exception>();
        // insert relations
        for (final File relaatio : relaatioFiles) {
            try {
                uploadKoodistoData.createKoodistoRelations(relaatio.getAbsolutePath());
            } catch (final Exception e) {
                log.error(e.getMessage());
                relaatioErrors.put(relaatio, e);
            }
        }

        if (koodistoErrors.size() > 0 || relaatioErrors.size() > 0) {
            final StringBuilder message = new StringBuilder();
            message.append("There were following errors while uploading:");
            for (final Map.Entry<File, Exception> koodistoError : koodistoErrors.entrySet()) {
                message.append("\n\nkoodisto: ").append(koodistoError.getKey().getAbsolutePath()).append(": ").append(koodistoError.getValue().getMessage());
            }
            for (final Map.Entry<File, Exception> relaatioError : relaatioErrors.entrySet()) {
                message.append("\n\nrelaatio: ").append(relaatioError.getKey().getAbsolutePath()).append(": ").append(relaatioError.getValue().getMessage());
            }
            log.error(message.toString());
        }
    }

    private void iterateDirectories(final List<File> koodistoFiles, final List<File> relaatioFiles, final File file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            for (final File childFile : file.listFiles()) {
                iterateDirectories(koodistoFiles, relaatioFiles, childFile);
            }
        } else if (StringUtils.containsIgnoreCase(file.getName(), "xls")) {
            if (StringUtils.containsIgnoreCase(file.getName(), "relaatio")) {
                relaatioFiles.add(file);
            } else if (!StringUtils.containsIgnoreCase(file.getName(), "koodisto")) {
                koodistoFiles.add(file);
            }
        }
    }
}
