package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.BaseRuleType
import com.identifix.contentlabelingservice.model.LabelRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LabelService {

    @Autowired
    BaseRulesComponent baseRulesComponent

    String createLabel(LabelRequest request) {
        List<BaseRule> baseRules = baseRulesComponent.getBaseRules(request.publisher, request.manualType)
        baseRules.find {
            (it.type == BaseRuleType.PAGE ? request.title : request.tocPath) =~ buildRegex(it.regexWords)
        }?.rule ?: "Not Found"
    }

    private static String buildRegex(String regexWords) {
        "(?i).*\\b(${regexWords})\\b.*"
    }
}
