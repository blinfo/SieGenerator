package sie.generator;

import java.time.*;
import java.util.List;
import java.util.stream.*;
import sie.domain.FinancialYear;

/**
 *
 * @author hakan
 */
class FinancialYearsGenerator implements Generator {

    public static List<FinancialYear> randomYears() {
        int result = RANDOM.nextInt(6) + 1;
        if (result > 4) {
            result = 2;
        }
        return randomYears(result);
    }

    public static List<FinancialYear> randomYears(int numberOfYears) {
        LocalDate startDate = createStartDate();
        return IntStream.range(0, numberOfYears)
                .mapToObj(index -> FinancialYear.of(0 - index, startDate.minusYears(index), startDate.plusYears(1).minusYears(index).minusDays(1)))
                .collect(Collectors.toList());
    }

    private static LocalDate createStartDate() {
        long year = RANDOM.nextLong(8);
        if (year > 4) {
            year = 0;
        }
        return LocalDate.of(Year.now().minusYears(year).getValue(), getStartMonth(), 1);
    }

    private static int getStartMonth() {
        int result = RANDOM.nextInt(18) + 1;
        if (result > 12) {
            return 1;
        }
        return result;
    }
}
