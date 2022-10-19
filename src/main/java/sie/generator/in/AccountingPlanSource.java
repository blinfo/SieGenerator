package sie.generator.in;

import java.util.List;
import xmlight.DocumentToXmlNodeParser;
import xmlight.XmlNode;

/**
 *
 * @author Håkan Lidén
 */
public class AccountingPlanSource {

    private final List<AccountClass> accountClasses;
    private final XmlNode root;
    private final String label;

    public AccountingPlanSource() {
        this.root = new DocumentToXmlNodeParser(getClass().getResourceAsStream("/source/bas2016.xml")).parse();
        this.label = root.getAttribute("label").replaceAll("\\s", "");
        this.accountClasses = parse();
    }

    public String getLabel() {
        return label;
    }

    public List<AccountClass> getAccountClasses() {
        return accountClasses;
    }

    public List<AccountGroup> getAccountGroups() {
        return accountClasses.stream().flatMap(ac -> ac.getGroups().stream()).toList();
    }

    public List<AccountTemplate> getAccounts() {
        return accountClasses.stream().flatMap(ac -> ac.getGroups().stream()).flatMap(ac -> ac.getAccounts().stream()).toList();
    }

    private List<AccountClass> parse() {
        return root.getChildren("account-class").stream().map(acNode -> {
            return new AccountClass(acNode);
        }).toList();
    }

    public static class AccountClass {

        private final String number;
        private final String label;
        private final List<AccountGroup> groups;
        private final XmlNode node;

        public AccountClass(XmlNode node) {
            this.number = node.getAttribute("number");
            this.label = node.getAttribute("label");
            this.node = node;
            groups = parse();
        }

        private List<AccountGroup> parse() {
            return node.getChildren("account-group").stream().map(agNode -> {
                return new AccountGroup(this, agNode);
            }).toList();

        }

        public String getNumber() {
            return number;
        }

        public String getLabel() {
            return label;
        }

        public List<AccountGroup> getGroups() {
            return groups;
        }
    }

    public static class AccountGroup {

        private final AccountClass parent;
        private final String number;
        private final String label;
        private final List<AccountTemplate> accounts;
        private final XmlNode node;

        public AccountGroup(AccountClass parent, XmlNode node) {
            this.parent = parent;
            this.number = node.getAttribute("number");
            this.label = node.getAttribute("label");
            this.node = node;
            accounts = parse();
        }

        private List<AccountTemplate> parse() {
            return node.getChildren("account").stream().map(aNode -> {
                return new AccountTemplate(this, aNode);
            }).toList();

        }

        public AccountClass getParent() {
            return parent;
        }

        public String getNumber() {
            return parent.getNumber() + number;
        }

        public String getLabel() {
            return label;
        }

        public List<AccountTemplate> getAccounts() {
            return accounts;
        }
    }

    public static class AccountTemplate {

        private final AccountGroup parent;
        private final String number;
        private final String label;

        public AccountTemplate(AccountGroup parent, XmlNode node) {
            this.parent = parent;
            this.number = node.getAttribute("number");
            this.label = node.getAttribute("label");
        }

        public AccountGroup getParent() {
            return parent;
        }

        public String getNumber() {
            return parent.getNumber() + number;
        }

        public String getLabel() {
            return label;
        }
    }
}
