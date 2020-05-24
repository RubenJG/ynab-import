package com.rubenjg.ynab.configuration;

import com.rubenjg.ynab.properties.YnabPrivateProperties;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@AllArgsConstructor
@Slf4j
public class YnabClientConfiguration {

    private YnabPrivateProperties ynabPrivateProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header(
                "Authorization",
                String.format(
                        "Bearer %s",
                        ynabPrivateProperties.getPersonalAccessToken()));
    }
}
