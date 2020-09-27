package com.rubenjg.ynab.dto.ynab;

import com.rubenjg.ynab.enums.Cleared;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class YnabPatchTransactionDto {

    // TODO This model may be unnecesary if there null properties are ignored in the TransactionDto
    private String id;
    private Cleared cleared;
}
