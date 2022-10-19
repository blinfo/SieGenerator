package sie.generator;

import java.math.*;
import java.util.Optional;
import java.util.stream.Stream;
import static sie.generator.AmountGenerator.Type.*;
import static sie.generator.Generator.RANDOM;

/**
 *
 * @author hakan
 */
class AmountGenerator implements Generator {

    private static Type randomType() {
        int type = RANDOM.nextInt(100);
        if (type < 5) {
            return TINY;
        }
        if (type < 15) {
            return SMALL;
        }
        if (type < 60) {
            return MEDIUM;
        }
        if (type < 95) {
            return LARGE;
        }
        return HUGE;
    }

    public static BigDecimal randomAmount() {
        Type mainType = AmountGenerator.randomType();
        BigDecimal mainAmount = mainType.random().setScale(ROUNDING_SCALE, ROUNDING_MODE);
        return mainAmount;
    }

    public static VoucherAmount createVoucherAmount() {
        Type mainType = AmountGenerator.randomType();
        Optional<Type> secondaryType = Optional.empty();
        if (mainType.index > 1 && RANDOM.nextInt(5) > 3) {
            secondaryType = Type.byIndex(mainType.index - RANDOM.nextInt(2));
        }
        BigDecimal mainAmount = mainType.random().setScale(ROUNDING_SCALE, ROUNDING_MODE);
        BigDecimal secondaryAmount = secondaryType.map(Type::random).orElse(BigDecimal.ZERO).setScale(ROUNDING_SCALE, ROUNDING_MODE);
        BigDecimal vatMultiplier = new BigDecimal("0.25");
        int vatRand = RANDOM.nextInt(100);
        if (vatRand < 3) {
            vatMultiplier = BigDecimal.ZERO;
        } else if (vatRand < 20) {
            vatMultiplier = new BigDecimal("0.06");
        } else if (vatRand < 40) {
            vatMultiplier = new BigDecimal("0.12");
        }

        return new VoucherAmount(mainAmount, secondaryAmount, vatMultiplier);
    }

    public record VoucherAmount(BigDecimal mainAmount, BigDecimal secondaryAmount, BigDecimal vatMultiplier) {

        public BigDecimal vatAmount() {
            return mainAmount.add(secondaryAmount).multiply(vatMultiplier()).negate().setScale(ROUNDING_SCALE, ROUNDING_MODE);
        }

        public BigDecimal restAmount() {
            return mainAmount.add(secondaryAmount).add(vatAmount()).negate();
        }

        @Override
        public String toString() {
            return "VoucherAmount{"
                    + "mainAmount=" + mainAmount + ", "
                    + "secondaryAmount=" + secondaryAmount + ", "
                    + "vatAmount=" + vatAmount() + ", "
                    + "restAmount=" + restAmount() + ", "
                    + "totalAmount=" + mainAmount.add(secondaryAmount) + ", "
                    + "vatMultiplier=" + vatMultiplier
                    + "}";
        }
    }

    public enum Type {
        TINY(0.5, 10, 0),
        SMALL(10, 200, 1),
        MEDIUM(200, 5000, 2),
        LARGE(5000, 90000, 3),
        HUGE(90000, 1000000, 4);

        private final Double minAmount;
        private final Double maxAmount;
        private final int index;

        private Type(double minAmount, double maxAmount, int index) {
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.index = index;
        }

        public Double min() {
            return minAmount;
        }

        public Double max() {
            return maxAmount;
        }

        public Integer index() {
            return index;
        }

        public static Optional<Type> byIndex(int index) {
            return Stream.of(values()).filter(t -> t.index == index).findFirst();
        }

        public BigDecimal random() {
            int cents = min() >= 5000 ? 0 : RANDOM.nextInt(99);
            if (this == MEDIUM) {
                cents = RANDOM.nextInt(3) == 1 ? cents : 0;
            }
            int value = RANDOM.nextInt(maxAmount.intValue() - minAmount.intValue()) + minAmount.intValue();
            return new BigDecimal(value + "." + cents).setScale(2, ROUNDING_MODE.HALF_UP);
        }

    }
}
