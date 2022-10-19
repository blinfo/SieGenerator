package sie.generator.in;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import sie.generator.*;

/**
 *
 * @author Håkan Lidén
 */
public class VoucherSeriesSource {

    private static final List<VoucherNumberSeriesTemplate> LIST = parse();

    public static List<VoucherNumberSeriesTemplate> list() {
        return LIST;
    }

    public static List<VoucherNumberSeriesTemplate> randomList() {
        return random();
    }

    public static Optional<VoucherNumberSeriesTemplate> find(String number) {
        return LIST.stream().filter(vns -> vns.getNumber().equals(number)).findFirst();
    }

    private static List<VoucherNumberSeriesTemplate> parse() {
        InputStream stream = VoucherSeriesSource.class.getResourceAsStream("/source/voucher-series.csv");
        return new BufferedReader(new InputStreamReader(stream)).lines().limit(0).map(VoucherNumberSeriesTemplate::new).collect(Collectors.toList());
    }

    private static List<VoucherNumberSeriesTemplate> random() {
        try {
            InputStream stream = VoucherSeriesSource.class.getResourceAsStream("/source/voucher-series.csv");
            String[] lines = new String(stream.readAllBytes()).split("\n");
            int limit = limit();
            return Stream.of(lines).limit(limit).map(VoucherNumberSeriesTemplate::new).collect(Collectors.toList());
        } catch (IOException ex) {
            throw new SieGeneratorException(ex);
        }
    }

    private static Integer limit() {
        int result = Generator.RANDOM.nextInt(7) + 1;
        if (result == 3) {
            return 4;
        }
        if (result == 4) {
            return 6;
        }
        if (result >= 5) {
            return result + 2;
        }
        return result;
    }

    public static class VoucherNumberSeriesTemplate {

        private final String number;
        private final String label;

        public VoucherNumberSeriesTemplate(String line) {
            number = line.substring(0, 1);
            label = line.substring(2);
        }

        public String getNumber() {
            return number;
        }

        public String getLabel() {
            return label;
        }
    }
}
