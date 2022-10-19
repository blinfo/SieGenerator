package sie.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;
import sie.generator.in.PersonSource;

/**
 *
 * @author hakan
 */
public class PersonGenerator implements Generator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static PersonSource run() {
        try {
            InputStream stream = CompanyGenerator.class.getResourceAsStream("/source/people.json");
            List<PersonSource> people = List.of(MAPPER.readValue(stream, PersonSource[].class));
            return people.get(RANDOM.nextInt(people.size()));
        } catch (IOException ex) {
            throw new SieGeneratorException(ex);
        }
    }

    static List<String> createSignatures(PersonSource person) {
        List<String> signatures = new ArrayList<>(Generator.getSigns(5));
        signatures.add(person.getSignature());
        String symbol = RANDOM.nextBoolean() ? "* " : "#";
        return signatures.stream().map(s -> s.length() == 1 ? symbol + s : s).toList();
    }
}
