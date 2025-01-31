package com.rubenjg.ynab.helper;

import com.rubenjg.ynab.dto.scotiabank.ScotiabankCellType;
import com.rubenjg.ynab.dto.scotiabank.ScotiabankCurrency;
import com.rubenjg.ynab.dto.scotiabank.ScotiabankTransaction;
import com.rubenjg.ynab.dto.ynab.YnabAccountDto;
import com.rubenjg.ynab.dto.ynab.YnabBudgetDto;
import com.rubenjg.ynab.dto.ynab.YnabTransactionDto;
import com.rubenjg.ynab.model.Source;
import com.rubenjg.ynab.model.Transaction;
import com.rubenjg.ynab.properties.ScotiabankProperties;
import com.rubenjg.ynab.properties.YnabPrivateProperties;
import com.rubenjg.ynab.service.YnabService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class TransactionHelper {

    private static final List<String> SCOTIABANK_IGNORED_ROWS = Arrays.asList(
            "Número de Referencia",
            "Tarjeta Número:",
            "Rango de fechas (día/mes/año)");
    private static final String YNAB_DATE_PATTERN = "yyyy-MM-dd";
    private static final String SCOTIABANK_DATE_PATTERN = "dd/MM/yyyy";
    private static int count = 0;

    private final YnabService ynabService;
    private final YnabPrivateProperties ynabPrivateProperties;
    private final ScotiabankProperties scotiabankProperties;

    public List<Transaction> getYnabTransactions() {
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
                .map(this::mapYnabTransaction)
                .collect(Collectors.toList());
    }

    private Transaction mapYnabTransaction(YnabTransactionDto ynabTransactionDto) {
        return Transaction.builder()
                .id(count++)
                .date(LocalDate.parse(ynabTransactionDto.getDate(), DateTimeFormatter.ofPattern(YNAB_DATE_PATTERN)))
                .payee(ynabTransactionDto.getPayeeName())
                .amount(new BigDecimal(ynabTransactionDto.getAmount())
                        .setScale(2, RoundingMode.HALF_UP)
                        .divide(new BigDecimal("1000.00"), RoundingMode.HALF_UP))
                .source(Source.YNAB)
                .original(ynabTransactionDto)
                .build();
    }

    public List<Transaction> getScotiabankTransactions() {
        List<ScotiabankTransaction> transactions = new ArrayList<>();
        try (InputStream inputStream = new FileInputStream(scotiabankProperties.getFile())) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Iterator<Row> it = sheet.rowIterator(); it.hasNext(); ) {
                Row row = it.next();
                String firstCell = asString(row, 0);
                if (null != firstCell && !firstCell.isEmpty() && !SCOTIABANK_IGNORED_ROWS.contains(firstCell)) {
                    transactions.add(ScotiabankTransaction.builder()
                            .date(asString(row, ScotiabankCellType.DATE.getIndex()))
                            .description(asString(row, ScotiabankCellType.DESCRIPTION.getIndex()))
                            .amount(asString(row, ScotiabankCellType.AMOUNT.getIndex()))
                            .currency(ScotiabankCurrency.valueOf(asString(row, ScotiabankCellType.CURRENCY.getIndex())))
                            .build());
                }
            }
        } catch (IOException e) {
            log.error("An exception occurred while reading the Excel file with Scotiabank transactions", e);
        }
        return transactions.stream()
                .map(this::mapScotibankTransaction)
                .collect(Collectors.toList());
    }

    private String asString(Row row, int index) {
        String result = "";
        if (null != row) {
            Cell cell = row.getCell(index);
            if (null != cell) {
                result = cell.getStringCellValue();
            }
        }
        return result;
    }

    private Transaction mapScotibankTransaction(ScotiabankTransaction scotiabankTransaction) {
        return Transaction.builder()
                .id(count++)
                .date(LocalDate.parse(scotiabankTransaction.getDate(), DateTimeFormatter.ofPattern(SCOTIABANK_DATE_PATTERN)))
                .payee(scotiabankTransaction.getDescription())
                .amount(getAmount(scotiabankTransaction))
                .source(Source.SCOTIABANK)
                .original(scotiabankTransaction)
                .build();
    }

    private BigDecimal getAmount(ScotiabankTransaction scotiabankTransaction) {
        BigDecimal amount = new BigDecimal(scotiabankTransaction.getAmount());
        if (scotiabankTransaction.getCurrency().equals(ScotiabankCurrency.USD)) {
            amount = amount.multiply(scotiabankProperties.getDollarExchange());
        }
        return amount.negate().setScale(2, RoundingMode.HALF_UP);
    }
}
