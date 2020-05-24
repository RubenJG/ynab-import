package com.rubenjg.ynab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TransactionDto {

    private String id;
    private String date;
    private BigDecimal amount;
    private String cleared;
    @JsonProperty("payee_name")
    private String payeeName;
}
