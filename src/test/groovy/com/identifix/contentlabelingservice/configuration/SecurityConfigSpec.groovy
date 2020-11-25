package com.identifix.contentlabelingservice.configuration

import org.springframework.security.config.annotation.web.builders.WebSecurity
import spock.lang.Specification

class SecurityConfigSpec extends Specification {

    SecurityConfig systemUnderTest
    WebSecurity mockWebSecurity = GroovyMock(WebSecurity)
    WebSecurity.IgnoredRequestConfigurer mockIgnoredRequestConfigurer = Mock(WebSecurity.IgnoredRequestConfigurer)

    def setup() {
        mockWebSecurity.ignoring() >> mockIgnoredRequestConfigurer
        systemUnderTest = new SecurityConfig()
    }

    def 'can configure web security'() {
        expect: 'runs configure'
        systemUnderTest.configure(mockWebSecurity) == null
    }
}
