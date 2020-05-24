package com.rubenjg.ynab.runner;

import com.rubenjg.ynab.client.YnabClient;
import com.rubenjg.ynab.dto.*;
import com.rubenjg.ynab.dto.TransactionDto;
import com.rubenjg.ynab.dto.TransactionsDto;
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
        BudgetDto budget = getCurrentBudget();
        if (null == budget) {
            log.error("Could not find a budget");
            return;
        }

        log.info("Budget id: '{}'", budget.getId());
        log.info("Budget name: '{}'", budget.getName());

        List<AccountDto> accounts = getAccounts(budget.getId());
        if (null == accounts || accounts.size() == 0) {
            log.error("Could not find accounts for budget with id: '{}'", budget.getId());
            return;
        }

        AccountDto account = accounts
                .stream()
                .filter(a -> a.getId().equals(ynabPrivateProperties.getAccountId()))
                .findFirst()
                .orElse(null);
        if (null == account) {
            log.error("Could not find account with id: '{}'", ynabPrivateProperties.getAccountId());
            return;
        }

        log.info("Found account with id: {} -> {}", ynabPrivateProperties.getAccountId(), account);

        List<TransactionDto> transactions = getTransactions(budget.getId(), account.getId());

        log.info("Transactions:");
        transactions.forEach(t -> log.info("Transaction: {}", t));
    }

    private BudgetDto getCurrentBudget() {
        YnabResponse<BudgetsDto> response = ynabClient.getBudgets();
        if (null == response) return null;

        BudgetsDto budgetsDto = response.getData();
        if (null == budgetsDto) return null;

        List<BudgetDto> budgets = budgetsDto.getBudgets();
        if (null == budgets || budgets.size() == 0) return null;

        return budgets.get(0);
    }

    private List<AccountDto> getAccounts(String budgetId) {
        YnabResponse<AccountsDto> response = ynabClient.getAccounts(budgetId);
        if (null == response) return null;

        AccountsDto accountsDto = response.getData();
        if (null == accountsDto) return null;

        return accountsDto.getAccounts();
    }

    private List<TransactionDto> getTransactions(String budgetId, String accountId) {
        YnabResponse<TransactionsDto> response = ynabClient.getTransactions(budgetId, accountId);
        if (null == response) return null;

        TransactionsDto transactionsDto = response.getData();
        if (null == transactionsDto) return null;

        return transactionsDto.getTransactions();
    }
}
