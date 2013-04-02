package fi.vm.sade.tarjonta.data;

import fi.vm.sade.tarjonta.data.util.DataUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

        final int fileCount = koodistoFiles.size() + relaatioFiles.size();
        int fileCounter = 1;

        final Map<File, Exception> koodistoErrors = new HashMap<File, Exception>();
        // insert koodistos
        for (final File koodisto : koodistoFiles) {
            try {
                uploadKoodistoData.loadKoodistoFromExcel(koodisto.getAbsolutePath(), getKoodistoRyhmaUri(koodisto.toURI().toString()),
                        StringUtils.substringBefore(koodisto.getName().toLowerCase(), "."), organisaatioOid);
            } catch (final Exception e) {
                log.error(e.getMessage());
                koodistoErrors.put(koodisto, e);
            }

            log.info(getProgress(fileCounter++, fileCount));
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

            log.info(getProgress(fileCounter++, fileCount));
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

    private String getKoodistoRyhmaUri(final String filePath) {
        if (StringUtils.containsIgnoreCase(filePath, koodistoDirectory)) {
            final String koodistoRyhmaNimi = StringUtils.substringBetween(filePath, koodistoDirectory + "/", "/");
            if (StringUtils.isNotBlank(koodistoRyhmaNimi)) {
                final String uri = String.format("http://%s", DataUtils.createKoodiUriFromName(koodistoRyhmaNimi));
                log.info("Found koodistoRyhmaUri [{}]", uri);
                return uri;
            }
        }
        log.warn("koodistoRyhmaUri not found");
        return null;
    }

    protected String getProgress(final int currentCount, final int totalCount) {
        if (totalCount == 0) {
            return "[invalid]";
        }
        final int done = new BigDecimal(currentCount).divide(new BigDecimal(totalCount), new MathContext(2, RoundingMode.HALF_UP))
                .multiply(new BigDecimal(100)).intValue(); // 0 ... 100
        final StringBuilder progress = new StringBuilder("Progress: [");
        for (int i = 0; i < 20; i++) {
            if (done > (i * 5)) {
                progress.append("#");
            } else {
                progress.append(" ");
            }
        }
        progress.append("] ").append(done).append(" % done");

        return progress.toString();
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
