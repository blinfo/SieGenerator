package sie.generator;

import java.time.LocalDate;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.IntStream;
import sie.domain.*;
import sie.generator.in.VoucherSeriesSource;
import sie.generator.in.VoucherSeriesSource.VoucherNumberSeriesTemplate;

/**
 *
 * @author Håkan Lidén
 */
class VouchersGenerator implements Generator {

    private static final int MAX_NUMBER_OF_VOUCHERS = 1000;
    private final List<VoucherNumberSeriesTemplate> seriesList;
    private final List<FinancialYear> years;
    private final List<String> signatures;

    public VouchersGenerator(List<FinancialYear> years, List<String> signatures) {
        this.years = years;
        this.seriesList = VoucherSeriesSource.list();
        this.signatures = signatures;
    }

    public static List<Voucher> run(List<FinancialYear> years, List<String> signatures) {
        VouchersGenerator generator = new VouchersGenerator(years, signatures);
        return generator.parse();
    }

    private List<Voucher> parse() {
        return parse("A");
    }

    private List<Voucher> parse(String series) {
        LocalDate startDate = years.get(0).getStartDate();
        long duration = startDate.until(years.get(0).getEndDate(), ChronoUnit.DAYS) + 1;
        AtomicLong days = new AtomicLong(RANDOM.nextLong(3) < 1 ? 0 : 1);
        return IntStream.range(1, MAX_NUMBER_OF_VOUCHERS)
                .filter(i -> days.get() < duration)
                .mapToObj(num -> {
                    Voucher voucher = VoucherGenerator.run(startDate.plusDays(days.get()), randomSignature(), num, series);
                    long addDays = RANDOM.nextLong(10) + 1;
                    if (addDays < 7) {
                        days.addAndGet(addDays);
                    }
                    return voucher;
                })
                .toList();
    }

    private String randomSignature() {
        return signatures.get(RANDOM.nextInt(signatures.size()));
    }

    private String series() {
        if (RANDOM.nextInt(3) < 2) {
            return "A";
        }
        return seriesList.get(RANDOM.nextInt(seriesList.size())).getNumber();
    }
}
