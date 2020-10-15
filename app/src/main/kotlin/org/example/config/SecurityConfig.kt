package org.example.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.ldap.DefaultSpringSecurityContextSource
import org.springframework.security.ldap.authentication.BindAuthenticator
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Security configuration for Spring Boot.
 *
 * [link](https://stackoverflow.com/questions/19500332/spring-security-and-json-authentication)
 * [link](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/core-web-filters.html)
 * [link](https://stackoverflow.com/questions/19500332/spring-security-and-json-authentication)
 * [link](https://stackoverflow.com/questions/45287003/compiler-warning-while-using-value-annotation-in-kotlin-project)
 * [link](https://medium.com/@dmarko484/spring-boot-active-directory-authentication-5ea04969f220)
 * [link](https://spring.io/guides/gs/authenticating-ldap/)
 */
@Configuration
@EnableWebSecurity
internal class SecurityConfig : WebSecurityConfigurerAdapter() {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Value("\${org.example.backend.ldap.domain}")
    private val ldapDomain: String? = null
    @Value("\${org.example.backend.ldap.url}")
    private val ldapUrl: String? = null
    @Value("\${org.example.backend.ldap.username}")
    private val ldapUsername: String? = null
    @Value("\${org.example.backend.ldap.password}")
    private val ldapPassword: String? = null
    @Value("\${org.example.backend.ldap.searchFilter}")
    private val ldapSearchFilter: String? = null
    @Value("\${org.example.backend.ldap.userSearchBase}")
    private val ldapUserSearchBase: String? = null
    @Value("\${org.example.backend.ldap.userDnPatterns}")
    private val ldapUserDnPatterns: String? = null
    @Value("\${org.example.backend.ldap.groupSearchBase}")
    private val ldapGroupSearchBase: String? = null

    /**
     * Global CORS configuration source.
     *
     * [SO link](https://stackoverflow.com/questions/18264334/cross-origin-resource-sharing-with-spring-security)
     *
     * @return CorsConfigurationSource
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH")
        // setAllowCredentials(true) is important, otherwise:
// The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.allowCredentials = true
        // setAllowedHeaders is important! Without it, OPTIONS pre-flight request
// will fail with 403 Invalid CORS request
        configuration.allowedHeaders = listOf("Origin", "Authorization", "Cache-Control", "Content-Type", "X-Forwarded-For")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Autowired
    @Throws(Exception::class)
    public override fun configure(authManagerBuilder: AuthenticationManagerBuilder) {
        authManagerBuilder.eraseCredentials(true)
        authManagerBuilder.authenticationProvider(activeDirectoryLdapAuthenticationProvider())
        authManagerBuilder.userDetailsService(userDetailsService())
        authManagerBuilder
            .ldapAuthentication()
            .userSearchFilter(ldapSearchFilter)
            .userSearchBase(ldapUserSearchBase)
            .groupSearchBase(ldapGroupSearchBase)
            .userDnPatterns(ldapUserDnPatterns)
            .contextSource()
            .url(ldapUrl)
            .managerDn(ldapUsername)
            .managerPassword(ldapPassword)
    }

    @Bean
    public override fun authenticationManager(): AuthenticationManager {
        return ProviderManager(listOf(activeDirectoryLdapAuthenticationProvider()))
    }

    @Bean
    fun activeDirectoryLdapAuthenticationProvider(): AuthenticationProvider {
        return if (ldapDomain == null || ldapDomain.isEmpty()) {
            logger.info("Configuration: Configuring LDAP AuthenticationProvider")
            val contextSource = DefaultSpringSecurityContextSource(ldapUrl)
            contextSource.userDn = ldapUsername
            contextSource.password = ldapPassword
            contextSource.afterPropertiesSet()
            val ldapUserSearch = FilterBasedLdapUserSearch(ldapUserSearchBase, ldapSearchFilter, contextSource)
            val bindAuthenticator = BindAuthenticator(contextSource)
            bindAuthenticator.setUserSearch(ldapUserSearch)
            LdapAuthenticationProvider(bindAuthenticator, DefaultLdapAuthoritiesPopulator(contextSource, ldapGroupSearchBase))
        } else {
            logger.info("Configuration: Configuring ActiveDirectory AuthenticationProvider")
            val provider = ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldapUrl)
            provider.setConvertSubErrorCodesToExceptions(true)
            provider.setUseAuthenticationRequestCredentials(true)
            provider
        }
    }

    override fun configure(web: WebSecurity) {
        // TODO: are these coming from swagger? or are they global?
        web.ignoring().antMatchers("/configuration/ui", "/configuration/**", "/webjars/**")
    }
}
