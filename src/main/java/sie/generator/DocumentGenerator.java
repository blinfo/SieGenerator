package sie.generator;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;
import sie.Sie4j;
import sie.domain.*;
import sie.dto.ValidationResultDTO;
import sie.generator.in.PersonSource;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentGenerator implements Generator {

    private final Document.Type documentType;
    private final LocalDate date;
    private final Integer noOfYears;
    private final Document.Builder builder;

    private DocumentGenerator(Document.Type documentType, LocalDate date, Integer noOfYears) {
        this.documentType = documentType;
        this.date = date;
        this.noOfYears = noOfYears;
        builder = Document.builder();
    }

    public static Builder builder() {
        return new Builder();
    }

    private Document run() {
        PersonSource person = PersonGenerator.run();
        List<String> signatures = PersonGenerator.createSignatures(person);
        MetaData meta = MetaDataGenerator.run(date, documentType, noOfYears, person);
        builder.metaData(meta);
        if (meta.getCompany().getType().orElse(null) == Company.Type.E) {
            signatures = List.of(person.getSignature());
        }
        AccountingPlan accountingPlan = AccountingPlanGenerator.run(meta.getCompany().getType());
        if (documentType.equals(Document.Type.E4) || documentType.equals(Document.Type.I4)) {
            final List<Voucher> vouchers = VouchersGenerator.run(meta.getFinancialYears(), signatures);
            builder.vouchers(vouchers);
            List<String> accountNumbers = vouchers.stream().flatMap(v -> v.getTransactions().stream()).map(t -> t.getAccountNumber()).distinct().sorted().collect(Collectors.toList());
            if (documentType.equals(Document.Type.E4)) {
                AccountingPlan.Builder apBuilder = AccountingPlan.builder();
                accountingPlan.getType().ifPresent(apBuilder::type);
                apBuilder.accounts(accountingPlan.getAccounts().stream().filter(acc -> accountNumbers.contains(acc.getNumber()))
                        .map(a -> {
                            Account.Builder accBuilder = Account.builder(a.getNumber()).label(a.getLabel().orElse(null));
                            if (a.getNumberAsInteger().get() < 3000) {
                                Balance cb = calculateBalance(a.getNumber(), vouchers);
                                accBuilder.addClosingBalance(cb);
                                int base = Math.abs(cb.getAmount().intValue()) + 100;
                                int ob = RANDOM.nextInt(base);
                                String negative = RANDOM.nextBoolean() ? "-" : "";
                                accBuilder.addOpeningBalance(Balance.of(new BigDecimal(negative + ob + ".00"), 0));
                                IntStream.range(1, noOfYears).forEachOrdered(y -> calculatePreviousBalance(accBuilder, a.getNumberAsInteger(), base, y));
                            } else {
                                Balance res = calculateBalance(a.getNumber(), vouchers);
                                int base = Math.abs(res.getAmount().intValue()) + 100;
                                accBuilder.addResult(res);
                                IntStream.range(1, noOfYears).forEachOrdered(y -> calculatePreviousBalance(accBuilder, a.getNumberAsInteger(), base, y));
                            }
                            return accBuilder.apply();
                        }).collect(Collectors.toList()));
                accountingPlan = apBuilder.apply();
            }
        }
        if (!documentType.equals(Document.Type.I4)) {
            builder.accountingPlan(accountingPlan);
        }
        return builder.apply();
    }

    private void calculatePreviousBalance(Account.Builder accBuilder, Optional<Integer> optNum, int base, int y) {
        optNum.ifPresent(num -> {
            String negative = RANDOM.nextBoolean() ? "-" : "";
            int balance = RANDOM.nextInt(base);
            int year = 0 - y;
            if (num < 3000) {
                accBuilder.addClosingBalance(Balance.of(new BigDecimal(negative + balance + ".00"), year));
                negative = RANDOM.nextBoolean() ? "-" : "";
                balance = RANDOM.nextInt(base);
                accBuilder.addOpeningBalance(Balance.of(new BigDecimal(negative + balance + ".00"), year));
            } else {
                accBuilder.addResult(Balance.of(new BigDecimal(negative + balance + ".00"), year));
            }
        });
    }

    private Balance calculateBalance(String accountNumber, List<Voucher> vouchers) {
        int firstYear = 0;
        return Balance.of(new BigDecimal(vouchers.stream()
                .flatMap(v -> v.getTransactions().stream())
                .filter(t -> t.getAccountNumber().equals(accountNumber))
                .mapToInt(t -> t.getAmount().intValue()).sum() + ".00"), firstYear);
    }

    public static class Builder {

        private Document.Type documentType;
        private LocalDate date;
        private Integer noOfYears;

        private Builder() {
        }

        public Builder setDocumentType(Document.Type documentType) {
            this.documentType = documentType;
            return this;
        }

        private Document.Type getDocumentType() {
            return Optional.ofNullable(documentType).orElse(Document.Type.E4);
        }

        public Builder setDate(LocalDate date) {
            this.date = date;
            return this;
        }

        private LocalDate getDate() {
            return Optional.ofNullable(date).orElse(LocalDate.now());
        }

        public Builder setNoOfYears(Integer noOfYears) {
            this.noOfYears = noOfYears;
            return this;
        }

        private Integer getNoOfYears() {
            return Optional.ofNullable(noOfYears).orElse(2);
        }

        public Document build() {
            return new DocumentGenerator(getDocumentType(), getDate(), getNoOfYears()).run();
        }

    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String companyType = RANDOM.nextInt(5) > 3 ? "E" : "AB";
        Document doc = DocumentGenerator.builder().setNoOfYears(2).build();
        String name = doc.getMetaData().getCompany().getName();
        String suffix = doc.getMetaData().getSieType().equals(Document.Type.I4) ? "SI" : "SE";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        File file = new File(System.getProperty("user.home") + "/SieGenerator/" + name + " - " + timestamp + "." + suffix);
        file.getParentFile().mkdirs();
        Sie4j.asSie(doc, file, Entity.CHARSET);
        ValidationResultDTO validate = Sie4j.validate(Files.readAllBytes(file.toPath()));
        if (validate.getLogs().isEmpty()) {
            System.out.println("Dokumentet \"" + file.getName() + "\" validerar");
        }
        validate.getLogs().forEach(System.out::println);
        System.out.println("File: " + file);
    }

}
