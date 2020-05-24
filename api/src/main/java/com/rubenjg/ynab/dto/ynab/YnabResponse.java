package com.rubenjg.ynab.dto.ynab;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class YnabResponse<T> {

    private T data;
}
