package sie.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;
import java.util.stream.*;
import sie.domain.*;
import sie.generator.in.*;

/**
 *
 * @author Håkan Lidén
 */
class CompanyGenerator implements Generator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public CompanyGenerator() {
    }

    public static Company run(PersonSource person) {
        CompanyGenerator generator = new CompanyGenerator();
        Company.Type type = generator.getType();
        String name;
        Address address;
        String corporateId;
        if (type.equals(Company.Type.E)) {
            if (person == null) {
                person = PersonGenerator.run();
            }
            name = person.getName();
            corporateId = person.getFormatedPin();
            address = Address.builder()
                    .contact(person.getName())
                    .phone(person.getPhone())
                    .postalAddress(person.getFormatedPostalAddress())
                    .streetAddress(person.getFormatedStreetAddress())
                    .apply();
        } else {
            CompanySource company = generator.getCompany();
            name = company.getCompanyName();
            switch (company.getType()) {
                case "AB" ->
                    type = Company.Type.AB;
                case "HB" ->
                    type = Company.Type.HB;
                case "EK" ->
                    type = Company.Type.EK;
            }
            corporateId = company.getOrgNum();
            address = Address.builder()
                    .contact(person.getName())
                    .phone(company.getContactPhone())
                    .postalAddress(company.getFormatedPostalAddress())
                    .streetAddress(company.getFormatedStreetAddress())
                    .apply();
        }
        Company.Builder builder = Company.builder(name)
                .address(address)
                .corporateId(corporateId)
                .id(generator.getId())
                .type(type);
        generator.getAquisitionNumber().ifPresent(builder::aquisitionNumber);
        return builder.apply();
    }

    private Company.Type getType() {
        int procent = rand().nextInt(100);
        if (procent < 40) {
            return Company.Type.AB;
        }
        if (procent < 50) {
            return Company.Type.HB;
        }
        if (procent < 60) {
            return Company.Type.E;
        }
        if (procent < 70) {
            return Company.Type.EK;
        }

        Company.Type type = randomType();
        if (type == Company.Type.X) {
            return Company.Type.AB;
        }
        return type;
    }

    private String getId() {
        Integer id = rand().nextInt(10000);
        return id.toString();
    }

    private Optional<Integer> getAquisitionNumber() {
        Integer id = rand().nextInt(20) + 1;
        return Optional.ofNullable(id <= 5 ? id : null);
    }

    private Company.Type randomType() {
        List<Company.Type> types = List.of(Company.Type.values());
        return types.get(RANDOM.nextInt(types.size()));
    }

//    private Optional<String> getSniCode() {
//        Integer sni = rand().nextInt(50000);
//        if (sni > 9900 || sni < 111) {
//            return Optional.empty();
//        }
//        String sniCode = String.format("%04d", sni);
//        return Optional.of(sniCode.substring(0, 2) + "." + sniCode.substring(2));
//    }
//
    private CompanySource getCompany() {
        try {
            InputStream stream = CompanyGenerator.class.getResourceAsStream("/source/companies.json");
            List<CompanySource> companies = Stream.of(MAPPER.readValue(stream, CompanySource[].class))
                    //                    .filter(co -> co.getType().equals(companyType) || co.getType().equals("any"))
                    .collect(Collectors.toList());
            return companies.get(RANDOM.nextInt(companies.size()));
        } catch (IOException ex) {
            throw new SieGeneratorException(ex);
        }
    }
}
