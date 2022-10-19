package sie.generator;

import java.io.*;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.*;

/**
 *
 * @author hakan
 */
public interface Generator {

    static final String DEFAULT_CURRENCY = "SEK";
    static final String ALPHABET = "ABCDEFGHIJKLMNOPRSTUVWY";
    static final Random RANDOM = new Random();
    static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    static final Integer ROUNDING_SCALE = 2;

    static List<String> getSigns() {
        return getSigns(8);
    }

    static List<String> getSigns(int limit) {
        Random rand = new Random();
        List<String> letters = List.of(ALPHABET.split(""));
        return IntStream.range(0, limit).mapToObj(s
                -> IntStream.range(0, rand.nextInt(2) + 1)
                        .mapToObj(i -> letters.get(new Random().nextInt(letters.size())))
                        .collect(Collectors.joining()))
                .distinct()
                .sorted()
                .toList();
    }

    default Random rand() {
        return RANDOM;
    }

    default Optional<String> getCurrency() {
        InputStream stream = Generator.class.getResourceAsStream("/source/currencies.txt");
        List<String> currencies = new BufferedReader(new InputStreamReader(stream))
                .lines()
                .filter(str -> !str.startsWith("X")).toList();
        int index = rand().nextInt(currencies.size() + 500);
        if (index >= currencies.size()) {
            return Optional.ofNullable(index <= currencies.size() + 200 ? DEFAULT_CURRENCY : null);
        }
        return Optional.of(currencies.get(index));
    }

    static String capitalize(String string) {
        return Stream.of(string.split(" "))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
