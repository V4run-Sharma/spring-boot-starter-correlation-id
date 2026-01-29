package io.github.v4runsharma.correlationid.autoconfigure;

import io.github.v4runsharma.correlationid.async.MdcTaskDecorator;
import io.github.v4runsharma.correlationid.filter.CorrelationIdFilter;
import io.github.v4runsharma.correlationid.interceptor.RestTemplateCorrelationIdInterceptor;
import io.github.v4runsharma.correlationid.interceptor.WebClientCorrelationIdFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(CorrelationIdProperties.class)
public class CorrelationIdAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnClass(Filter.class)
  public CorrelationIdFilter correlationIdFilter(CorrelationIdProperties properties) {
    return new CorrelationIdFilter(properties);
  }

  @Bean
  public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilterRegistration(
      CorrelationIdFilter filter) {

    FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registration;
  }

  @Bean
  @ConditionalOnClass(RestTemplate.class)
  public RestTemplateCustomizer correlationIdRestTemplateCustomizer(
      CorrelationIdProperties properties) {

    return restTemplate ->
        restTemplate.getInterceptors()
            .add(new RestTemplateCorrelationIdInterceptor(properties));
  }

  @Bean
  @ConditionalOnClass(WebClient.class)
  @ConditionalOnMissingBean(WebClient.Builder.class)
  public WebClient.Builder correlationIdWebClientBuilder(
      CorrelationIdProperties properties) {

    WebClientCorrelationIdFilter filter =
        new WebClientCorrelationIdFilter(properties);

    return WebClient.builder()
        .filter(filter.asExchangeFilterFunction());
  }

  @Bean
  @ConditionalOnMissingBean(TaskDecorator.class)
  public TaskDecorator mdcTaskDecorator() {
    return new MdcTaskDecorator();
  }

}
