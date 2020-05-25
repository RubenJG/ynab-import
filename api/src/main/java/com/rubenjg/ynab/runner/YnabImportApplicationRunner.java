package com.rubenjg.ynab.runner;

import com.rubenjg.ynab.helper.TransactionHelper;
import com.rubenjg.ynab.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class YnabImportApplicationRunner implements ApplicationRunner {

    private final TransactionHelper transactionHelper;

    @Override
    public void run(ApplicationArguments args) {
        List<Transaction> scotiabankTransactions = transactionHelper.getScotiabankTransactions();
        scotiabankTransactions.forEach(t -> log.info("T: {}", t));
        log.info("{} transactions were found", scotiabankTransactions.size());

        List<Transaction> ynabTransactions = transactionHelper.getYnabTransactions();
        ynabTransactions.forEach(t -> log.info("T: {}", t));
        log.info("{} YNAB transactions were found", ynabTransactions.size());


        try (BufferedWriter writer = new BufferedWriter(new FileWriter("test.txt", Charsets.UTF_8));) {

            List<Transaction> transactions = new ArrayList<>();
            transactions.addAll(scotiabankTransactions);
            transactions.addAll(ynabTransactions);
            transactions.sort(Comparator
                    .comparing(Transaction::getAmount)
                    .thenComparing(Transaction::getDate));

            for (Transaction t : transactions) {
                writer.write(t.toString());
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
