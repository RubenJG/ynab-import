package com.rubenjg.ynab.runner;

import com.rubenjg.ynab.dto.ynab.YnabAccountDto;
import com.rubenjg.ynab.dto.ynab.YnabBudgetDto;
import com.rubenjg.ynab.dto.ynab.YnabTransactionDto;
import com.rubenjg.ynab.properties.YnabPrivateProperties;
import com.rubenjg.ynab.service.YnabService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class YnabImportApplicationRunner implements ApplicationRunner {

    private final YnabPrivateProperties ynabPrivateProperties;
    private final YnabService ynabService;

    @Override
    public void run(ApplicationArguments args) {
        List<YnabTransactionDto> transactions = getYnabTransactions();

        log.info("{} transactions were found", transactions.size());
    }

    public List<YnabTransactionDto> getYnabTransactions() {
        Optional<YnabBudgetDto> budget = ynabService.getBudgets().findFirst();
        if (budget.isEmpty()) {
            log.error("Could not find budget");
            return Collections.emptyList();
        }

        log.info("Budget id: '{}'", budget.get().getId());
        log.info("Budget name: '{}'", budget.get().getName());

        Optional<YnabAccountDto> account = ynabService.getAccounts(budget.get().getId())
                .filter(a -> a.getId().equals(ynabPrivateProperties.getAccountId()))
                .findFirst();
        if (account.isEmpty()) {
            log.error("Could not find account with id: '{}'", ynabPrivateProperties.getAccountId());
            return Collections.emptyList();
        }

        log.info("Found account with id: {} -> {}", ynabPrivateProperties.getAccountId(), account);

        return ynabService.getTransactions(
                budget.get().getId(),
                account.get().getId(),
                ynabPrivateProperties.getTransactionsSince())
                .collect(Collectors.toList());
    }
}
