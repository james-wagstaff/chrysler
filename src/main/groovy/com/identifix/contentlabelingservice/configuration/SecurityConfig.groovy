package com.identifix.contentlabelingservice.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers('/**')
    }
}
