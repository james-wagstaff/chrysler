package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.BaseRuleType
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


@Component
@Slf4j
class BaseRulesComponent {

    @Value('${baseRulesBaseUrl}')
    String baseRulesBaseUrl

    @Cacheable(cacheNames = "BaseRules")
    @SuppressWarnings("GrMethodMayBeStatic")
    List<BaseRule> getBaseRules(String publisher, String manualType) {
        String baseRulesWhole =  getBaseRulesFromRepository(publisher, manualType)
        Map<String, BaseRule> baseRulesMap = [:]
        baseRulesWhole.split("\\r?\\n").each {
            String[] baseRuleValues = it.split(",")
            if (baseRuleValues.size() >= 2 && !baseRulesMap.containsKey(baseRuleValues[0])) {
                BaseRule baseRule = createBaseRule(baseRuleValues)
                baseRulesMap.put(baseRuleValues[0], baseRule)
            }
        }
        baseRulesMap.values() as List<BaseRule>
    }

    @SuppressWarnings('DuplicateNumberLiteral')
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

    @SuppressWarnings("GrMethodMayBeStatic")
    String getBaseRulesFromRepository(String publisher, String manualType) {
        log.info("Calling Bitbucket for $publisher, $manualType")
        new RestTemplate().getForObject("$baseRulesBaseUrl/raw/${publisher.toLowerCase()}/${manualType.toLowerCase()}_base_rules.csv", String.class)
    }

    @CacheEvict(cacheNames = "BaseRules")
    @SuppressWarnings("GrMethodMayBeStatic")
    void evictBaseRuleFromCache(String publisher, String manualType) {
        log.info("Removing cache for $publisher, $manualType")
    }
}
