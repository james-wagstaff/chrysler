package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.BaseRuleType
import com.identifix.contentlabelingservice.model.LabelRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class LabelService {

    @Autowired
    BaseRulesComponent baseRulesComponent

    String createLabel(LabelRequest request) {
        if (request.refresh) {
            baseRulesComponent.evictBaseRuleFromCache(request.publisher, request.manualType)
        }

        List<BaseRule> baseRules = baseRulesComponent.getBaseRules(request.publisher, request.manualType)
        String label = baseRules.find {
            (it.type == BaseRuleType.PAGE ? request.title : request.tocPath) =~ buildRegex(it.regexWords)
        }?.rule ?: ""

        log.info("Label for ${request.toString()} is $label")
        label
    }

    private static String buildRegex(String regexWords) {
        "(?i).*\\b(${regexWords})\\b.*"
    }
}
