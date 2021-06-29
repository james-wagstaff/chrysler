package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.BaseRuleType
import spock.lang.Specification

class BaseRulesComponentSpec extends Specification {
    BaseRulesComponent systemUnderTest = new BaseRulesComponent()
    GitService gitService = Mock()

    void setup() {
        systemUnderTest.gitService = gitService
    }
    def 'Base Rules Component is called with no header in base rule'() {
        when: 'getting base rules'
            List<BaseRule> baseRules = systemUnderTest.getBaseRules("ford", "workshop")
        then:
            1 * gitService.getBaseRules("ford", "workshop") >> "testRegex,test"
            baseRules.size() == 1
            baseRules.get(0).type == BaseRuleType.PAGE
            baseRules.get(0).regexWords.equalsIgnoreCase("testRegex")
            baseRules.get(0).rule.equalsIgnoreCase("test")
    }

    def 'Base Rules Component is called with header in base rule'() {
        when: 'getting base rules'
            List<BaseRule> baseRules = systemUnderTest.getBaseRules("ford", "workshop")
        then:
            1 * gitService.getBaseRules("ford", "workshop") >> "testRegex,test, HEADER"
            baseRules.size() == 1
            baseRules.get(0).type == BaseRuleType.HEADER
            baseRules.get(0).regexWords.equalsIgnoreCase("testRegex")
            baseRules.get(0).rule.equalsIgnoreCase("test")
    }
    def "Base Rules Component evict cache is called"() {
        when:
            systemUnderTest.evictBaseRuleFromCache("test", "test")
        then:
            'Removing cache for test, test'
    }
}
