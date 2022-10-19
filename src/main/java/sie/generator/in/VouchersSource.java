package sie.generator.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.*;
import java.math.*;
import java.util.*;
import java.util.stream.Stream;
import sie.generator.*;

/**
 *
 * @author hakan
 */
public class VouchersSource {

    public static List<VoucherTemplate> LIST = parse();

    public static List<VoucherTemplate> list() {
        return LIST;
    }

    public static List<VoucherTemplate> byType(Type type) {
        return list().stream().filter(vt -> vt.type() == type).toList();
    }

    private static List<VoucherTemplate> parse() {
        try {
            InputStream source = VouchersSource.class.getResourceAsStream("/source/vouchers.json");

            return new ObjectMapper().readValue(source, new TypeReference<List<VoucherTemplate>>() {
            });
        } catch (IOException ex) {
            throw new SieGeneratorException(ex);
        }
    }

    public record VoucherTemplate(@JsonDeserialize(using = TypeDeserializer.class)
            @JsonProperty("type") Type type,
            @JsonProperty("accounts") List<AccountTemplate> accounts,
            @JsonProperty("text") String text,
            @JsonProperty("companyType") String companyType) {

        public BigDecimal total() {
            return new BigDecimal(accounts.stream().mapToDouble(at -> at.amount().doubleValue()).filter(d -> d > 0).sum()).setScale(Generator.ROUNDING_SCALE, Generator.ROUNDING_MODE);
        }
    }

    public record AccountTemplate(@JsonProperty("number") String number,
            @JsonProperty("amount") BigDecimal amount) {

    }

    public enum Type {
        EMPLOYEE,
        PURCHASE,
        RENT,
        REPRESENTATION,
        SALES,
        WITHDRAWAL,
        DEPOSIT;

        @Override
        public String toString() {
            return name().toLowerCase().replaceAll("_", "-");
        }

        public static Type of(String text) {
            return find(text).orElseThrow();
        }

        public static Optional<Type> find(String text) {
            return Stream.of(values()).filter(t -> t.name().equalsIgnoreCase(text) || t.toString().equalsIgnoreCase(text)).findFirst();
        }
    }

    public static class TypeDeserializer extends JsonDeserializer<Type> {

        @Override
        public Type deserialize(JsonParser parser, DeserializationContext dc) throws IOException, JacksonException {
            return Type.of(parser.getText());
        }
    }

    public static void main(String[] args) {
        System.out.println(VouchersSource.list().size());
    }
}
