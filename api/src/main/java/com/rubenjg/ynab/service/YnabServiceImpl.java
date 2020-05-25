package com.rubenjg.ynab.service;

import com.rubenjg.ynab.client.YnabClient;
import com.rubenjg.ynab.dto.ynab.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class YnabServiceImpl implements YnabService {

    private final YnabClient ynabClient;

    @Override
    public Stream<YnabBudgetDto> getBudgets() {
        YnabResponse<YnabBudgetsDto> response = ynabClient.getBudgets();
        if (null == response) return Stream.empty();

        YnabBudgetsDto ynabBudgetsDto = response.getData();
        if (null == ynabBudgetsDto) return Stream.empty();

        return ynabBudgetsDto.getBudgets().stream();
    }

    @Override
    public Stream<YnabAccountDto> getAccounts(String budgetId) {
        YnabResponse<YnabAccountsDto> response = ynabClient.getAccounts(budgetId);
        if (null == response) return Stream.empty();

        YnabAccountsDto ynabAccountsDto = response.getData();
        if (null == ynabAccountsDto) return Stream.empty();

        return ynabAccountsDto.getAccounts().stream();
    }

    @Override
    public Stream<YnabTransactionDto> getTransactions(String budgetId, String accountId, String since) {
        YnabResponse<YnabTransactionsDto> response = ynabClient.getTransactions(budgetId, accountId, since);
        if (null == response) return Stream.empty();

        YnabTransactionsDto ynabTransactionsDto = response.getData();
        if (null == ynabTransactionsDto) return Stream.empty();

        return ynabTransactionsDto.getTransactions().stream();
    }
}
