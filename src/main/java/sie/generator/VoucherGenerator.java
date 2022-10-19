package sie.generator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import sie.domain.*;
import sie.generator.in.VouchersSource;

/**
 *
 * @author Håkan Lidén
 */
class VoucherGenerator implements Generator {

    public static Voucher run(LocalDate date, String signature, Integer number, String series) {
        VouchersSource.VoucherTemplate template = template();
        Voucher.Builder builder = Voucher.builder()
                .date(date)
                .registrationDate(date.plusDays(RANDOM.nextInt(5)))
                .number(number)
                .signature(signature)
                .text(template.text())
                .series(series(template.type(), series));
        transactions(template).forEach(builder::addTransaction);
        return builder.apply();
    }

    private static String series(VouchersSource.Type type, String series) {
        if (type == null) {
            return series == null ? "A" : series;
        }
        return "A";
    }

    private static List<Transaction> transactions(VouchersSource.VoucherTemplate template) {
        BigDecimal templateTotal = template.total();
        BigDecimal baseAmount;
        if (templateTotal.equals(new BigDecimal("1000.00"))) {
            baseAmount = AmountGenerator.randomAmount().setScale(ROUNDING_SCALE, ROUNDING_MODE);
        } else {
            baseAmount = template.total();
        }

        List<Transaction> result = new ArrayList<>(template.accounts().stream().map(at -> {
            Transaction.Builder builder = Transaction.builder();
            String number = at.number();
            if (number.contains("*")) {
                int replacement = RANDOM.nextInt(3) + 1;
                number = number.replace("*", replacement + "");
            }
            builder.accountNumber(number)
                    .amount(at.amount().multiply(baseAmount).divide(templateTotal));
            return builder.apply();
        }).toList());
        BigDecimal sum = new BigDecimal(result.stream().mapToDouble(t -> t.getAmount().doubleValue()).sum()).setScale(ROUNDING_SCALE, ROUNDING_MODE);
        if (!sum.equals(BigDecimal.ZERO.setScale(ROUNDING_SCALE, ROUNDING_MODE))) {
            result.add(Transaction.builder().accountNumber("3740").amount(sum.negate()).apply());
        }
        return result;
    }

    private static VouchersSource.VoucherTemplate template() {
        int rand = RANDOM.nextInt(100);
        List<VouchersSource.VoucherTemplate> templates;
        if (rand < 30) {
            templates = VouchersSource.byType(VouchersSource.Type.SALES);
        } else if (rand < 60) {
            templates = VouchersSource.byType(VouchersSource.Type.PURCHASE);
        } else if (rand < 70) {
            templates = VouchersSource.byType(VouchersSource.Type.DEPOSIT);
        } else if (rand < 80) {
            templates = VouchersSource.byType(VouchersSource.Type.WITHDRAWAL);
        } else if (rand < 90) {
            templates = VouchersSource.byType(VouchersSource.Type.REPRESENTATION);
        } else if (rand < 95) {
            templates = VouchersSource.byType(VouchersSource.Type.RENT);
        } else {
            templates = VouchersSource.byType(VouchersSource.Type.EMPLOYEE);
        }
        return templates.get(RANDOM.nextInt(templates.size()));
    }
}
