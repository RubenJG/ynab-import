package com.rubenjg.ynab.dto.ynab;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class YnabAccountsDto {

    private List<YnabAccountDto> accounts;
}
