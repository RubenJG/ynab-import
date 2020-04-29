package com.rubenjg.ynab.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class YnabResponse<T> {

    private T data;
}
