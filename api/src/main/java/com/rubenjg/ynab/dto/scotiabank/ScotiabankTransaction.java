package com.rubenjg.ynab.dto.scotiabank;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ScotiabankTransaction {

    private String date;
    private String description;
    private String amount;
    private ScotiabankCurrency currency;
}
