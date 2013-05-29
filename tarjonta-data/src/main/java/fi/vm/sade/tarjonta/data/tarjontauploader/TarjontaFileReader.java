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
            new Column("oppilaitosnumero", "OPPILAITOSNUMERO", InputColumnType.STRING),
            new Column("toimipisteJno", "TOIMIPISTE", InputColumnType.STRING),
            new Column("yhkoodi", "YHKOODI", InputColumnType.STRING),
            new Column("koulutus", "KOULUTUS", InputColumnType.STRING),
            new Column("koulutusohjelma", "KOULUTUSOHJELMA", InputColumnType.STRING),
            new Column("painotus", "PAINOTUS", InputColumnType.STRING),
            new Column("koulutuslaji", "KOULUTUSLAJI", InputColumnType.STRING),
            new Column("pohjakoulutusvaatimus", "POHJAKOULUTUSVAATIMUS", InputColumnType.STRING),
            new Column("opetuskieli", "OPETUSKIELI", InputColumnType.STRING),
            new Column("opetusmuoto", "OPETUSMUOTO", InputColumnType.STRING),
            new Column("alkamisvuosi", "ALKAMISVUOSI", InputColumnType.STRING),
            new Column("alkamiskausi", "ALKAMISKAUSI", InputColumnType.STRING),
            new Column("suunniteltuKesto", "SUUNNITELTU_KESTO", InputColumnType.INTEGER),
            new Column("hakukohdekoodi", "HAKUKOHDEKOODI", InputColumnType.STRING)
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

    public void read(final TarjontaFileType type, final String filename, final String args2) throws IOException {
        logger.info("Luetaan [{}] tiedostoa [{}]", type.name(), filename);

        final List<ErrorRow> errors = new ArrayList<ErrorRow>();

        if (type == TarjontaFileType.KOULUTUS) {
            // luetaan koulutustiedosto
            final ExcelReader<Koulutus> koulutusReader = new ExcelReader<Koulutus>(Koulutus.class, KOULUTUS_COLUMNS, Integer.MAX_VALUE);
            final Set<Koulutus> koulutukset = koulutusReader.read(filename, true);

            if (CollectionUtils.isNotEmpty(koulutukset)) {
                logger.info("Luettiin [{}] riviä", koulutukset.size());
                int rivilaskuri = 0;
                for (final Koulutus koulutus : koulutukset) {
                    rivilaskuri++;
                    try {
                        tarjontaHandler.addKoulutus(koulutus, args2);
                    } catch (final Exception e) {
                        logger.error("virhe lisättäessä koulutusta", e);
                        errors.add(new ErrorRow(e, rivilaskuri, koulutus));
                    }
                }
            }
        } else if (type == TarjontaFileType.HAKUKOHDE) {
            // luetaan hakukohdetiedosto
            final ExcelReader<Hakukohde> hakukohdeReader = new ExcelReader<Hakukohde>(Hakukohde.class, HAKUKOHDE_COLUMNS, Integer.MAX_VALUE);
            final Set<Hakukohde> hakukohteet = hakukohdeReader.read(filename, true);

            if (CollectionUtils.isNotEmpty(hakukohteet)) {
                logger.info("Luettiin [{}] riviä", hakukohteet.size());
                int rivilaskuri = 0;
                for (final Hakukohde hakukohde : hakukohteet) {
                    rivilaskuri++;
                    try {
                        tarjontaHandler.addHakukohde(hakukohde, args2);
                    } catch (final Exception e) {
                        logger.error("virhe lisättäessä hakukohdetta", e);
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

    /**
     * Jos syöttämisessä tapahtui virhe, sen tiedot kootaan ErrorRow-olioon.
     */
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
