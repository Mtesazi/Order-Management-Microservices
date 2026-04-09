package com.pollinate.conf;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
public class H2ConsoleConfig {

    @Value("${spring.h2.console.path:/h2-console}")
    private String h2ConsolePath;

    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServlet() {
        ServletRegistrationBean<JakartaWebServlet> registrationBean =
                new ServletRegistrationBean<>(new JakartaWebServlet(), h2ConsolePath, h2ConsolePath + "/*");
        registrationBean.addInitParameter("-trace", "false");
        registrationBean.addInitParameter("-webAllowOthers", "false");
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }
}

