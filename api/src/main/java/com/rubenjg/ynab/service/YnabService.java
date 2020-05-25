package com.rubenjg.ynab.service;

import com.rubenjg.ynab.dto.ynab.YnabAccountDto;
import com.rubenjg.ynab.dto.ynab.YnabBudgetDto;
import com.rubenjg.ynab.dto.ynab.YnabTransactionDto;

import java.util.stream.Stream;

public interface YnabService {

    Stream<YnabBudgetDto> getBudgets();

    Stream<YnabAccountDto> getAccounts(String budgetId);

    Stream<YnabTransactionDto> getTransactions(String budgetId, String accountId, String since);
}
