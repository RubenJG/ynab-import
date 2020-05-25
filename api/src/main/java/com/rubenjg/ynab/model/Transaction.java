package com.rubenjg.ynab.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Transaction {

    private LocalDate date;
    private String payee;
    private BigDecimal amount;
    private Source source;
}
