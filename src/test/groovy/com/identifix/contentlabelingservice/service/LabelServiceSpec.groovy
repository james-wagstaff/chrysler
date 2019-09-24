package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.BaseRuleType
import com.identifix.contentlabelingservice.model.LabelRequest
import org.spockframework.spring.SpringBean
import spock.lang.Specification

class LabelServiceSpec extends Specification {
    @SpringBean
    BaseRulesComponent baseRulesComponent = Mock()
    List<BaseRule> baseRules = []
    LabelService service = new LabelService()

    void setup() {
        BaseRule baseRule = new BaseRule()
        baseRule.setRegexWords("tester")
        baseRule.setRule("tester")
        baseRule.setType(BaseRuleType.HEADER)
        BaseRule baseRule1 = new BaseRule()
        baseRule1.setRegexWords("test")
        baseRule1.setRule("test")
        baseRule1.setType(BaseRuleType.PAGE)

        baseRules.add(baseRule)
        baseRules.add(baseRule1)

        service.baseRulesComponent = baseRulesComponent
    }

    def 'Call label service for found label for page'() {
        LabelRequest request = new LabelRequest()
            request.title = "test"
            request.header = "test"
            request.manualType = "workshop"
            request.publisher = "ford"

        when: 'getting label'
            String label = service.createLabel(request)
        then:
            label == "test"
            1 * baseRulesComponent.getBaseRules(request.publisher, request.manualType) >> baseRules
    }

    def 'Call label service for found label for header'() {
        LabelRequest request = new LabelRequest()
            request.title = "tester"
            request.header = "tester"
            request.manualType = "workshop"
            request.publisher = "ford"

        when: 'getting label'
            String label = service.createLabel(request)
        then:
            label == "tester"
            1 * baseRulesComponent.getBaseRules(request.publisher, request.manualType) >> baseRules
    }

    def 'Call label service for not found label'() {
        LabelRequest request = new LabelRequest()
            request.title = "fail"
            request.header = "fail"
            request.manualType = "workshop"
            request.publisher = "ford"

        when: 'getting label'
            String label = service.createLabel(request)
        then:
            label == ""
            1 * baseRulesComponent.getBaseRules(request.publisher, request.manualType) >> baseRules
    }

    def 'Call label service with refresh'() {
        LabelRequest request = new LabelRequest()
            request.title = "tester"
            request.header = "tester"
            request.manualType = "workshop"
            request.publisher = "ford"
            request.setRefresh(true)

        when: 'getting label'
            String label = service.createLabel(request)
        then:
            label == "tester"
            1 * baseRulesComponent.evictBaseRuleFromCache(request.publisher, request.manualType)
            1 * baseRulesComponent.getBaseRules(request.publisher, request.manualType) >> baseRules
    }
}
