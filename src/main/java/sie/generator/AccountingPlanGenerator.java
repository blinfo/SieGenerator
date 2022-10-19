package sie.generator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import sie.domain.Account;
import sie.domain.Company;
import sie.generator.in.AccountingPlanSource;
import sie.generator.in.SruCodeSource;

/**
 *
 * @author Håkan Lidén
 */
class AccountingPlanGenerator {

    static sie.domain.AccountingPlan run(Optional<Company.Type> companyType) {
        AccountingPlanSource source = new AccountingPlanSource();
        return sie.domain.AccountingPlan.builder()
                .type(source.getLabel())
                .accounts(getAccounts(source, companyType)).apply();
    }

    private static List<Account> getAccounts(final AccountingPlanSource source, Optional<Company.Type> companyType) {
        Optional<String> form = Optional.empty();
        if (companyType.isPresent()) {
            switch (companyType.get()) {
                case AB:
                case EK:
                case BRF:
                    form = Optional.of("INK2");
                    break;
                case I:
                case S:
                    form = Optional.of("INK3");
                    break;
                case HB:
                    form = Optional.of("INK4");
                    break;
                case E:
                    form = Optional.of("NEejK1");
                    break;
            }
        }
        Optional<SruCodeSource> optCodes = form.map(SruCodeSource::new);
        return source.getAccounts().stream().map(ac -> {
            Integer classNumber = Integer.valueOf(ac.getParent().getParent().getNumber());
            Account.Type type;
            switch (classNumber) {
                case 1:
                    type = Account.Type.T;
                    break;
                case 2:
                    type = Account.Type.S;
                    break;
                case 3:
                    type = Account.Type.I;
                    break;
                default:
                    type = Account.Type.K;
            }
            Account.Builder builder = Account.builder(ac.getNumber()).label(ac.getLabel()).type(type);
            optCodes.ifPresent(sru -> sru.findCodes(ac.getNumber()).forEach(builder::addSruCode));
            return builder.apply();
        }).collect(Collectors.toList());
    }

}
