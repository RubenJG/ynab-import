package com.rubenjg.ynab.client;

import com.rubenjg.ynab.configuration.YnabClientConfiguration;
import com.rubenjg.ynab.dto.ynab.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        name = "ynabClient",
        url = "${ynab.url}",
        configuration = YnabClientConfiguration.class)
public interface YnabClient {

    @GetMapping(
            value = "/budgets",
            produces = MediaType.APPLICATION_JSON_VALUE)
    YnabResponse<YnabBudgetsDto> getBudgets();

    @GetMapping(
            value = "/budgets/{budgetId}/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    YnabResponse<YnabAccountsDto> getAccounts(
            @PathVariable("budgetId") String budgetId);

    @GetMapping(
            value = "/budgets/{budgetId}/accounts/{accountId}/transactions",
            produces = MediaType.APPLICATION_JSON_VALUE)
    YnabResponse<YnabTransactionsDto> getTransactions(
            @PathVariable("budgetId") String budgetId,
            @PathVariable("accountId") String accountId,
            @RequestParam("since_date") String since);

    @PatchMapping(
            value = "/budgets/{budgetId}/transactions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    YnabResponse<Map<String, Object>> patchTransactions(
            @PathVariable("budgetId") String budgetId,
            @RequestBody YnabPatchTransactionsDto ynabPatchTransactionsDto
    );
}
