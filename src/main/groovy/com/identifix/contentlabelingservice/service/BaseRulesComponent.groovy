package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.BaseRuleType
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

import java.nio.file.Files

@Component
class BaseRulesComponent {

    @Value("classpath:ford/workshop_base_rules.csv")
    private Resource baseRules

    List<BaseRule> getBaseRules(String publisher, String manualType) {
        String baseRulesWhole = getBaseRulesFromRepository(publisher, manualType)
        createBaseRulesFromString(baseRulesWhole)
    }

    private static List<BaseRule> createBaseRulesFromString(String baseRulesWhole) {
        Map<String, BaseRule> baseRulesMap = new HashMap<>()
        Arrays.stream(baseRulesWhole.split("\\r?\\n")).forEach({baseRulesLine ->

            String[] baseRuleValues = baseRulesLine.split(",")
            if (baseRuleValues.size() >= 2 && !baseRulesMap.containsKey(baseRuleValues[0])) {
                BaseRule baseRule = createBaseRule(baseRuleValues)
                baseRulesMap.put(baseRuleValues[0],baseRule)
            }

        })
        baseRulesMap.values() as List<BaseRule>
    }

    private static BaseRule createBaseRule(String[] baseRuleValues) {
        BaseRule baseRule = new BaseRule()
        if (baseRuleValues.size() == 3 && baseRuleValues[2].contains("HEADER")) {
            baseRule.setType(BaseRuleType.HEADER)
        } else {
            baseRule.setType(BaseRuleType.PAGE)
        }

        baseRule.setRegexWords(baseRuleValues[0])
        baseRule.setRule(baseRuleValues[1])

        baseRule
    }

    String getBaseRulesFromRepository(String publisher, String manualType) {
        new String (Files.readAllBytes(baseRules.getFile().toPath()))
    }
}
