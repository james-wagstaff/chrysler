package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.BaseRuleType
import spock.lang.Specification

class BaseRulesComponentSpec extends Specification {
    def 'Base Rules Component is called with no header in base rule'() {
        def baseRulesComponent = Spy(BaseRulesComponent) {
            getBaseRulesFromRepository("ford","workshop") >> "testRegex,test"
        }
        when: 'getting base rules'
            List<BaseRule> baseRules = baseRulesComponent.getBaseRules("ford", "workshop")
        then:
            baseRules.size() == 1
            baseRules.get(0).getType() == BaseRuleType.PAGE
            baseRules.get(0).getRegexWords().equalsIgnoreCase("testRegex")
            baseRules.get(0).getRule().equalsIgnoreCase("test")
    }

    def 'Base Rules Component is called with header in base rule'() {
        def baseRulesComponent = Spy(BaseRulesComponent) {
            getBaseRulesFromRepository("ford","workshop") >> "testRegex,test, HEADER"
        }
        when: 'getting base rules'
        List<BaseRule> baseRules = baseRulesComponent.getBaseRules("ford", "workshop")
        then:
        baseRules.size() == 1
        baseRules.get(0).getType() == BaseRuleType.HEADER
        baseRules.get(0).getRegexWords().equalsIgnoreCase("testRegex")
        baseRules.get(0).getRule().equalsIgnoreCase("test")
    }
}
