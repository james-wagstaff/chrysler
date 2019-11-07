package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.error.BitBucketNetworkException
import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.BaseRuleType
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate

@Component
@Slf4j
class BaseRulesComponent {
    private static final String CSV_LINE_BREAK = '\\r?\\n'
    private static final String CSV_COMMA_BREAK = ','
    private static final String CSV_HEADER = 'HEADER'
    private static final int CSV_INDEX_REGEX_WORDS = 0
    private static final int CSV_INDEX_RULE = 1
    private static final int CSV_INDEX_HEADER = 2
    private static final int CSV_BASE_COLUMN_SIZE = 2
    private static final int CSV_EXTRA_COLUMN_SIZE = 3

    @Value('${baseRulesBaseUrl}')
    String baseRulesBaseUrl

    @Cacheable(cacheNames = "BaseRules")
    @SuppressWarnings("GrMethodMayBeStatic")
    List<BaseRule> getBaseRules(String publisher, String manualType) {
        String baseRulesWhole =  getBaseRulesFromRepository(publisher, manualType)
        Map<String, BaseRule> baseRulesMap = [:]
        baseRulesWhole?.split(CSV_LINE_BREAK).each {
            String[] baseRuleValues = it.split(CSV_COMMA_BREAK)
            if (baseRuleValues.size() >= CSV_BASE_COLUMN_SIZE &&
                    !baseRulesMap.containsKey(baseRuleValues[CSV_INDEX_REGEX_WORDS])) {
                BaseRule baseRule = createBaseRule(baseRuleValues)
                baseRulesMap.put(baseRuleValues[CSV_INDEX_REGEX_WORDS], baseRule)
            }
        }
        baseRulesMap.values() as List<BaseRule>
    }

    private static BaseRule createBaseRule(String[] baseRuleValues) {
        BaseRule baseRule = new BaseRule()
        if (baseRuleValues.size() == CSV_EXTRA_COLUMN_SIZE && baseRuleValues[CSV_INDEX_HEADER].contains(CSV_HEADER)) {
            baseRule.setType(BaseRuleType.HEADER)
        } else {
            baseRule.setType(BaseRuleType.PAGE)
        }

        baseRule.setRegexWords(baseRuleValues[CSV_INDEX_REGEX_WORDS])
        baseRule.setRule(baseRuleValues[CSV_INDEX_RULE])

        baseRule
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    String getBaseRulesFromRepository(String publisher, String manualType) {
        log.info("Calling Bitbucket for $publisher, $manualType")
        String baseRules
        try {
            baseRules = new RestTemplate().getForObject("$baseRulesBaseUrl/raw/${publisher.toLowerCase()}/${manualType.toLowerCase()}_base_rules.csv", String)

            if (baseRules?.contains("<title>Sign in to your account</title>")) {
                log.error("Error accessing Bitbucket for $publisher, $manualType. The serivce is on the wrong network! Please select a new network and restart the service!")
                throw new BitBucketNetworkException("Network issues with BitBucket")
            }
        } catch (HttpClientErrorException | ResourceAccessException e) {
            log.error("Error accessing Bitbucket for $publisher, $manualType. ${e.message}")
            throw new BitBucketNetworkException("Error occurred while trying to access Bitbucket for $publisher, $manualType.")
        }

        baseRules
    }

    @CacheEvict(cacheNames = "BaseRules")
    @SuppressWarnings("GrMethodMayBeStatic")
    void evictBaseRuleFromCache(String publisher, String manualType) {
        log.info("Removing cache for $publisher, $manualType")
    }
}
