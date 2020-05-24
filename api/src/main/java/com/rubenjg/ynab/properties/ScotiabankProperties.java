package com.rubenjg.ynab.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("xls")
@Getter
@Setter
public class ScotiabankProperties {

    private String file;
}
