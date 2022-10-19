package sie.generator.in;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import xmlight.DocumentToXmlNodeParser;
import xmlight.XmlNode;

/**
 *
 * @author Håkan Lidén
 */
public class SruCodeSource {

    private static final XmlNode rootNode = new DocumentToXmlNodeParser(SruCodeSource.class.getResourceAsStream("/source/tax-return-forms-2015.xml")).parse();
    private final List<Field> fields;

    public SruCodeSource(String form) {
        if (form == null || form.isBlank()) {
            fields = List.of();
        } else {
            XmlNode formNode = rootNode.getChildren("form").stream().filter(n -> n.getAttribute("name").equals(form)).findFirst().orElse(null);
            fields = formNode.getChildren("field").stream().map(Field::new).collect(Collectors.toList());
        }
    }

    public List<String> findCodes(String number) {
        return fields.stream().map(f -> f.getMatched(number).orElse(null)).filter(s -> s != null).collect(Collectors.toList());
    }

    private static class Field {

        private final String code;
        private final List<String> includedAccounts;
        private final List<String> excludedAccounts;

        public Field(XmlNode fieldNode) {
            this.code = fieldNode.getAttribute("code");
            this.includedAccounts = fieldNode.getChildren("accounts").stream().map(n -> n.getAttribute("number")).collect(Collectors.toList());
            this.excludedAccounts = fieldNode.getChildren("excluded-accounts").stream().map(n -> n.getAttribute("number")).collect(Collectors.toList());
        }

        public Optional<String> getMatched(String number) {
            List<String> excludeList = excludedAccounts.stream().filter(field -> number.startsWith(field)).collect(Collectors.toList());
            return includedAccounts.stream().filter(field -> number.startsWith(field) && excludeList.isEmpty()).findAny().map(s -> code);
        }
    }
}
