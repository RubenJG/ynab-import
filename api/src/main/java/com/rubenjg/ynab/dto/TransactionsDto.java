package com.rubenjg.ynab.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TransactionsDto {

    private List<TransactionDto> transactions;
}
