package fi.vm.sade.tarjonta.data.tarjontauploader;

import fi.vm.sade.tarjonta.data.loader.xls.Column;
import fi.vm.sade.tarjonta.data.loader.xls.ExcelReader;
import fi.vm.sade.tarjonta.data.loader.xls.InputColumnType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class TarjontaFileReader {
    private final Logger logger = LoggerFactory.getLogger(TarjontaFileReader.class);
    private static final Column[] KOULUTUS_COLUMNS = {
            new Column("nimi", "NIMI", InputColumnType.STRING)
    };
    private static final Column[] HAKUKOHDE_COLUMNS = {
            new Column("alkamisvuosi", "ALKAMISVUOSI", InputColumnType.STRING),
            new Column("alkamiskausi", "ALKAMISKAUSI", InputColumnType.STRING),
            new Column("hakutyyppi", "HAKUTYYPPI", InputColumnType.STRING),
            new Column("yhkoulu", "YHKOULU", InputColumnType.STRING),
            new Column("oppilaitosnumero", "OPPILAITOSNUMERO", InputColumnType.STRING),
            new Column("toimipisteJno", "TOIMIPISTE", InputColumnType.STRING),
            new Column("hakukohdekoodi", "HAKUKOHDEKOODI", InputColumnType.STRING),
            new Column("valinnanAloituspaikka", "VALINNAN_ALOITUSPAIKKA", InputColumnType.INTEGER),
            new Column("aloituspaikka", "ALOITUSPAIKKA", InputColumnType.INTEGER),
            new Column("valintakoe", "VALINTAKOE", InputColumnType.STRING)
    };

    private TarjontaHandler tarjontaHandler;

    @Autowired
    public TarjontaFileReader(final TarjontaHandler tarjontaHandler) {
        this.tarjontaHandler = tarjontaHandler;
    }

    public void read(final TarjontaFileType type, final String filename, final String hakuOid) throws IOException {
        logger.info("Luetaan [{}] tiedostoa [{}]", type.name(), filename);

        final List<ErrorRow> errors = new ArrayList<ErrorRow>();

        if (type == TarjontaFileType.KOULUTUS) {
            final ExcelReader<Koulutus> koulutusReader = new ExcelReader<Koulutus>(Koulutus.class, KOULUTUS_COLUMNS, Integer.MAX_VALUE);
            final Set<Koulutus> koulutukset = koulutusReader.read(filename, true);

            if (CollectionUtils.isNotEmpty(koulutukset)) {
                logger.info("Luettiin [{}] riviä", koulutukset.size());
                int rivilaskuri = 0;
                for (final Koulutus koulutus : koulutukset) {
                    rivilaskuri++;
                    try {
                        tarjontaHandler.addKoulutus(koulutus);
                    } catch (final Exception e) {
                        errors.add(new ErrorRow(e, rivilaskuri, koulutus));
                    }
                }
            }
        } else if (type == TarjontaFileType.HAKUKOHDE) {
            final ExcelReader<Hakukohde> hakukohdeReader = new ExcelReader<Hakukohde>(Hakukohde.class, HAKUKOHDE_COLUMNS, Integer.MAX_VALUE);
            final Set<Hakukohde> hakukohteet = hakukohdeReader.read(filename, true);

            if (CollectionUtils.isNotEmpty(hakukohteet)) {
                logger.info("Luettiin [{}] riviä", hakukohteet.size());
                int rivilaskuri = 0;
                for (final Hakukohde hakukohde : hakukohteet) {
                    rivilaskuri++;
                    try {
                        tarjontaHandler.addHakukohde(hakukohde, hakuOid);
                    } catch (final Exception e) {
                        errors.add(new ErrorRow(e, rivilaskuri, hakukohde));
                    }
                }
            }
        }

        if (errors.size() > 0) {
            for (final ErrorRow error : errors) {
                logger.info(error.getErrorMessage());
            }
        }

        logger.info("Tiedoston käsittely tehty");
    }

    class ErrorRow {
        private Exception exception;
        private Integer rowNumber;
        private Object rowData;

        public ErrorRow(final Exception exception, final Integer rowNumber, final Object rowData) {
            this.exception = exception;
            this.rowNumber = rowNumber;
            this.rowData = rowData;
        }

        public String getErrorMessage() {
            return String.format("Row %d: %s\nData:\n%s", rowNumber, exception.toString(), ToStringBuilder.reflectionToString(rowData, ToStringStyle.SHORT_PREFIX_STYLE));
        }
    }
}
