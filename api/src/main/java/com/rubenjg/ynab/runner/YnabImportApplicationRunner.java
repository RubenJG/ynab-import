package com.rubenjg.ynab.runner;

import com.rubenjg.ynab.client.YnabClient;
import com.rubenjg.ynab.dto.ynab.*;
import com.rubenjg.ynab.properties.YnabPrivateProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class YnabImportApplicationRunner implements ApplicationRunner {

    private YnabPrivateProperties ynabPrivateProperties;
    private YnabClient ynabClient;

    @Override
    public void run(ApplicationArguments args) {
        YnabBudgetDto budget = getCurrentBudget();
        if (null == budget) {
            log.error("Could not find a budget");
            return;
        }

        log.info("Budget id: '{}'", budget.getId());
        log.info("Budget name: '{}'", budget.getName());

        List<YnabAccountDto> accounts = getAccounts(budget.getId());
        if (null == accounts || accounts.size() == 0) {
            log.error("Could not find accounts for budget with id: '{}'", budget.getId());
            return;
        }

        YnabAccountDto account = accounts
                .stream()
                .filter(a -> a.getId().equals(ynabPrivateProperties.getAccountId()))
                .findFirst()
                .orElse(null);
        if (null == account) {
            log.error("Could not find account with id: '{}'", ynabPrivateProperties.getAccountId());
            return;
        }

        log.info("Found account with id: {} -> {}", ynabPrivateProperties.getAccountId(), account);

        List<YnabTransactionDto> transactions = getTransactions(budget.getId(), account.getId());

        log.info("Transactions:");
        transactions.forEach(t -> log.info("Transaction: {}", t));
    }

    private YnabBudgetDto getCurrentBudget() {
        YnabResponse<YnabBudgetsDto> response = ynabClient.getBudgets();
        if (null == response) return null;

        YnabBudgetsDto ynabBudgetsDto = response.getData();
        if (null == ynabBudgetsDto) return null;

        List<YnabBudgetDto> budgets = ynabBudgetsDto.getBudgets();
        if (null == budgets || budgets.size() == 0) return null;

        return budgets.get(0);
    }

    private List<YnabAccountDto> getAccounts(String budgetId) {
        YnabResponse<YnabAccountsDto> response = ynabClient.getAccounts(budgetId);
        if (null == response) return null;

        YnabAccountsDto ynabAccountsDto = response.getData();
        if (null == ynabAccountsDto) return null;

        return ynabAccountsDto.getAccounts();
    }

    private List<YnabTransactionDto> getTransactions(String budgetId, String accountId) {
        YnabResponse<YnabTransactionsDto> response = ynabClient.getTransactions(budgetId, accountId);
        if (null == response) return null;

        YnabTransactionsDto ynabTransactionsDto = response.getData();
        if (null == ynabTransactionsDto) return null;

        return ynabTransactionsDto.getTransactions();
    }
}
