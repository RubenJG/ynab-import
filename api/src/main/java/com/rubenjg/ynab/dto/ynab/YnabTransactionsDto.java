package com.rubenjg.ynab.dto.ynab;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class YnabTransactionsDto {

    private List<YnabTransactionDto> transactions;
}
