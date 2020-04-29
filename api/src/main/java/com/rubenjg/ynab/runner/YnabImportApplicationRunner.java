package com.rubenjg.ynab.runner;

import com.rubenjg.ynab.client.YnabClient;
import com.rubenjg.ynab.dto.BudgetDto;
import com.rubenjg.ynab.dto.BudgetsDto;
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

    private YnabClient ynabClient;

    @Override
    public void run(ApplicationArguments args) {
        BudgetDto budget = getCurrentBudget();
        if (null == budget) {
            log.error("Could not find a budget");
            return;
        }

        String budgetId = budget.getId();

        log.info("Budget id: '{}'", budgetId);
        log.info("Budget name: '{}'", budget.getName());

    }

    private BudgetDto getCurrentBudget() {
        BudgetsDto budgetsDto = ynabClient.getBudgets().getData();
        if (null != budgetsDto) {
            List<BudgetDto> budgets = budgetsDto.getBudgets();
            if (null != budgets && budgets.size() > 0) {
                return budgets.get(0);
            }
        }
        return null;
    }
}
