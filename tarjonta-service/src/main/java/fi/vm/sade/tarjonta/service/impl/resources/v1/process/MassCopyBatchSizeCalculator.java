package fi.vm.sade.tarjonta.service.impl.resources.v1.process;

public class MassCopyBatchSizeCalculator {

    private static final int BATCH_KOMOTO_SIZE = 50;
    private static final int BATCH_HAKUKOHDE_SIZE = 20;

    public static boolean shouldStartNewHakuKohdeBatch(long currentCount) {
        return currentCount > 0 && currentCount % BATCH_HAKUKOHDE_SIZE == 0;
    }

    public static boolean shouldStartNewKomotoBatch(long currentCount) {
        return currentCount > 0 && currentCount % BATCH_KOMOTO_SIZE == 0;
    }

    public static double calcPercentage(long countKomoto, long countTotalKomoto, long countHakukohde, long countTotalHakukohde) {
        long total = countTotalKomoto + countTotalHakukohde;
        if (total == 0) {
            return 0;
        }
        if (countKomoto < BATCH_KOMOTO_SIZE) {
            return 0;
        }
        if (countHakukohde == 0) {
            return (countKomoto - BATCH_KOMOTO_SIZE) * 100 / total;
        }
        return (countKomoto + (countHakukohde - BATCH_HAKUKOHDE_SIZE )) * 100 / total;
    }

}
