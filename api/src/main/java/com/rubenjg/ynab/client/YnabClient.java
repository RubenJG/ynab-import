package com.rubenjg.ynab.client;

import com.rubenjg.ynab.dto.AccountsDto;
import com.rubenjg.ynab.dto.BudgetsDto;
import com.rubenjg.ynab.dto.YnabResponse;
import com.rubenjg.ynab.dto.TransactionsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ynabClient", url = "${ynab.url}")
public interface YnabClient {

    @GetMapping(
            value = "/budgets",
            headers = "Authorization=Bearer ${ynab.personal-access-token}")
    YnabResponse<BudgetsDto> getBudgets();

    @GetMapping(
            value = "/budgets/{budgetId}/accounts",
            headers = "Authorization=Bearer ${ynab.personal-access-token}")
    YnabResponse<AccountsDto> getAccounts(
            @PathVariable("budgetId") String budgetId);

    @GetMapping(
            value = "/budgets/{budgetId}/accounts/{accountId}/transactions",
            headers = "Authorization=Bearer ${ynab.personal-access-token}")
    YnabResponse<TransactionsDto> getTransactions(
            @PathVariable("budgetId") String budgetId,
            @PathVariable("accountId") String accountId);
}
