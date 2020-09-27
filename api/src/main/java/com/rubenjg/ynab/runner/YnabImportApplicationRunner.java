package com.rubenjg.ynab.runner;

import com.rubenjg.ynab.client.YnabClient;
import com.rubenjg.ynab.dto.ynab.YnabBudgetDto;
import com.rubenjg.ynab.dto.ynab.YnabPatchTransactionDto;
import com.rubenjg.ynab.dto.ynab.YnabPatchTransactionsDto;
import com.rubenjg.ynab.dto.ynab.YnabTransactionDto;
import com.rubenjg.ynab.enums.Cleared;
import com.rubenjg.ynab.helper.TransactionHelper;
import com.rubenjg.ynab.model.Source;
import com.rubenjg.ynab.model.Transaction;
import com.rubenjg.ynab.properties.AppProperties;
import com.rubenjg.ynab.service.YnabService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class YnabImportApplicationRunner implements ApplicationRunner {

    private final TransactionHelper transactionHelper;
    private final AppProperties appProperties;
    private final YnabClient ynabClient;
    private final YnabService ynabService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Retrieving Scotiabank transactions");
        List<Transaction> scotiabankTransactions = getTransactions(transactionHelper.getScotiabankTransactions());

        log.info("Retrieving YNAB transactions");
        List<Transaction> ynabTransactions = getTransactions(transactionHelper.getYnabTransactions());

        List<Transaction> transactions = new ArrayList<>();
        transactions.addAll(scotiabankTransactions);
        transactions.addAll(ynabTransactions);
        transactions.sort(Comparator.comparing(Transaction::getDate));

        filterTransactions(
                transactions,
                this::fullMatch,
                this::onFullMatch);
        filterTransactions(
                transactions,
                this::partialMatch,
                this::onPartialMatch);

        log.info("Unmatched transactions:");
        transactions.stream()
                .sorted(Comparator.comparing(Transaction::getSource)
                        .thenComparing(Transaction::getDate))
                .forEach(t -> log.info("{} {} {} {}",
                        t.getDate(),
                        t.getAmount(),
                        t.getPayee(),
                        t.getSource()));
    }

    private List<Transaction> getTransactions(List<Transaction> transactions) {
        transactions.forEach(t -> log.info("T: {}", t));
        log.info("{} transactions were found", transactions.size());

        if (null != appProperties.getIgnoreList()) {
            transactions = transactions.stream()
                    .filter(this::nonIgnorableTransaction)
                    .collect(Collectors.toList());
        }
        return transactions;
    }

    private boolean nonIgnorableTransaction(Transaction transaction) {
        return this.appProperties
                .getIgnoreList()
                .stream()
                .noneMatch(ignore -> transaction
                        .getPayee()
                        .matches(ignore));
    }

    private void filterTransactions(
            List<Transaction> transactions,
            BiFunction<Transaction, Transaction, Boolean> filter,
            BiConsumer<Transaction, Transaction> consume) {
        boolean keepGoing = true;
        while (keepGoing) {
            keepGoing = false;
            List<Transaction> toRemove = new ArrayList<>();
            for (int i = 0; i < transactions.size() && !keepGoing; i++) {
                Transaction a = transactions.get(i);
                for (int j = 0; j < transactions.size() && !keepGoing; j++) {
                    Transaction b = transactions.get(j);
                    if (filter.apply(a, b)) {
                        keepGoing = true;
                        toRemove.add(a);
                        toRemove.add(b);
                        consume.accept(a, b);
                    }
                }
            }
            transactions.removeAll(toRemove);
        }
    }

    public boolean fullMatch(Transaction a, Transaction b) {
        return !a.equals(b) &&
                a.getAmount().subtract(b.getAmount()).compareTo(BigDecimal.ZERO) == 0 &&
                a.getDate().equals(b.getDate()) &&
                !a.getSource().equals(b.getSource());
    }

    public boolean partialMatch(Transaction a, Transaction b) {
        return !a.equals(b) &&
                a.getAmount().subtract(b.getAmount()).compareTo(BigDecimal.ZERO) == 0 &&
                !a.getSource().equals(b.getSource());
    }

    private void onFullMatch(Transaction a, Transaction b) {
        // TODO Refactor this to some kind of event listener
        Transaction transaction = a;
        if (b.getSource().equals(Source.YNAB)) {
            transaction = b;
        }
        YnabTransactionDto ynabTransactionDto = (YnabTransactionDto) transaction.getOriginal();
        if (null != ynabTransactionDto && ynabTransactionDto.getCleared().equals(Cleared.UNCLEARED)) {
            log.info("Cleared {}", ynabTransactionDto.getCleared());

            // TODO This code is duplicated. Create a method in YnabService with some sort of caching
            Optional<YnabBudgetDto> budget = ynabService.getBudgets().findFirst();
            if (budget.isPresent()) {
                String budgetId = budget.get().getId();

                YnabPatchTransactionsDto ynabPatchTransactionsDto = YnabPatchTransactionsDto.builder()
                        .transactions(List.of(YnabPatchTransactionDto.builder()
                                .id(ynabTransactionDto.getId())
                                .cleared(Cleared.CLEARED)
                                .build()))
                        .build();
                ynabClient.patchTransactions(
                        budgetId,
                        ynabPatchTransactionsDto
                );
            }
        }
    }

    private void onPartialMatch(Transaction a, Transaction b) {
        log.info("Partial match found\n{}\n{}", a, b);
    }
}
