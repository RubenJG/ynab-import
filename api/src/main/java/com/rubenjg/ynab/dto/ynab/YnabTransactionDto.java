package com.rubenjg.ynab.dto.ynab;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rubenjg.ynab.enums.Cleared;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class YnabTransactionDto {

    private String id;
    private String date;
    private String amount;
    private Cleared cleared;
    @JsonProperty("payee_name")
    private String payeeName;
}
