package com.rubenjg.ynab.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("app")
@Getter
@Setter
public class AppProperties {

    private List<String> ignoreList;
}
