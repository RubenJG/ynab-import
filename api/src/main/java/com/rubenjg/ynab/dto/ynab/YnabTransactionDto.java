package com.rubenjg.ynab.dto.ynab;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String cleared;
    @JsonProperty("payee_name")
    private String payeeName;
}
