package com.rubenjg.ynab.dto.ynab;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class YnabTransactionDto {

    private String id;
    private String date;
    private BigDecimal amount;
    private String cleared;
    @JsonProperty("payee_name")
    private String payeeName;
}
