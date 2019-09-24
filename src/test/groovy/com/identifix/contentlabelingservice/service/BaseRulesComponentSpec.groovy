package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.BaseRuleType
import spock.lang.Specification

class BaseRulesComponentSpec extends Specification {
    def 'Base Rules Component is called with no header in base rule'() {
        def baseRulesComponent = Spy(BaseRulesComponent) {
            getBaseRulesFromRepository("ford", "workshop") >> "testRegex,test"
        }
        when: 'getting base rules'
            List<BaseRule> baseRules = baseRulesComponent.getBaseRules("ford", "workshop")
        then:
            baseRules.size() == 1
            baseRules.get(0).type == BaseRuleType.PAGE
            baseRules.get(0).regexWords.equalsIgnoreCase("testRegex")
            baseRules.get(0).rule.equalsIgnoreCase("test")
    }

    def 'Base Rules Component is called with header in base rule'() {
        def baseRulesComponent = Spy(BaseRulesComponent) {
            getBaseRulesFromRepository("ford", "workshop") >> "testRegex,test, HEADER"
        }
        when: 'getting base rules'
        List<BaseRule> baseRules = baseRulesComponent.getBaseRules("ford", "workshop")
        then:
        baseRules.size() == 1
        baseRules.get(0).type == BaseRuleType.HEADER
        baseRules.get(0).regexWords.equalsIgnoreCase("testRegex")
        baseRules.get(0).rule.equalsIgnoreCase("test")
    }
    def "Base Rules Component evict cache is called"() {
        given:
            def buffer = new ByteArrayOutputStream()
            System.out = new PrintStream(buffer)
            def baseRulesComponent = new BaseRulesComponent()
        when:
            baseRulesComponent.evictBaseRuleFromCache("test", "test")
        then:
            buffer.toString().contains('Removing cache for test, test')
    }
}
