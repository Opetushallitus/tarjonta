package fi.vm.sade.tarjonta.service.impl.resources.v1.process;

public class MassCopyBatchSizeCalculator {

    private static final int BATCH_SIZE = 10;

    public static boolean shouldStartNewBatch(long currentCount) {
        return currentCount > 0 && currentCount % BATCH_SIZE == 0;
    }

    public static double calcPercentage(long countKomoto, long countTotalKomoto, long countHakukohde, long countTotalHakukohde) {
        long totalCount = countTotalKomoto + countTotalHakukohde;
        if (totalCount == 0) {
            return 100;
        }
        long currentCount = countHakukohde + countKomoto;
        return currentCount * 100 / totalCount;
    }

}
