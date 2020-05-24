package com.rubenjg.ynab.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ynab")
@Getter
@Setter
public class YnabPrivateProperties {

    private String ynabPersonalAccessToken;
    private String accountId;
    private String transactionsSince;
}
