package org.ionproject.integration.ui.config

import org.ionproject.integration.application.config.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor

@EnableWebSecurity
@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var appProperties: AppProperties

    @Autowired
    private lateinit var filterChainExceptionHandler: FilterChainExceptionHandler

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .addFilterAfter(AuthFilter(appProperties.token), FilterSecurityInterceptor::class.java)
            .addFilterBefore(filterChainExceptionHandler, AuthFilter::class.java)
            .authorizeRequests()
            .anyRequest().permitAll()
    }
}
