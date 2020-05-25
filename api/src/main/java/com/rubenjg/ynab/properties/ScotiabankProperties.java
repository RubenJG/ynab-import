package com.rubenjg.ynab.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties("scotiabank")
@Getter
@Setter
public class ScotiabankProperties {

    private String file;
    private BigDecimal dollarExchange;
}
