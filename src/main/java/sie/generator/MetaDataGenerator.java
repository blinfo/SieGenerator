package sie.generator;

import java.time.*;
import java.util.*;
import sie.domain.*;
import sie.generator.in.PersonSource;

/**
 *
 * @author Håkan Lidén
 */
class MetaDataGenerator implements Generator {

    private static final String PROGRAM_NAMN = "SieGenerator", PROGRAM_VERSION = "1.0";
    private static final Program PROGRAM = Program.of(PROGRAM_NAMN, PROGRAM_VERSION);

    public static MetaData run() {
        return run(LocalDate.now(), Document.Type.E4, 2, PersonGenerator.run());
    }

    public static MetaData run(LocalDate generatedDate, Document.Type type, int noOfYears, PersonSource person) {
        MetaDataGenerator generator = new MetaDataGenerator();
        Company company = CompanyGenerator.run(person);

        MetaData.Builder builder = MetaData.builder()
                .company(company)
                .comments(generator.getComments())
                .program(PROGRAM).generated(Generated.of(generatedDate, person.getSignature()))
                .sieType(type);
        if (!type.equals(Document.Type.I4)) {
            List<FinancialYear> years = FinancialYearsGenerator.randomYears(noOfYears);
            builder.taxationYear(Year.of(generatedDate.minusYears(1).getYear())).financialYears(years);
        }
        generator.getCurrency().ifPresent(builder::currency);
        return builder.apply();
    }

    private String getComments() {
        return "Skapad av " + PROGRAM_NAMN + " v " + PROGRAM_VERSION + " - " + LocalDateTime.now();
    }
}
