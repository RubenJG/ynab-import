package com.rubenjg.ynab.dto.ynab;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class YnabPatchTransactionsDto {

    private List<YnabPatchTransactionDto> transactions;
}
