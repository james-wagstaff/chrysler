package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.model.BaseRule
import com.identifix.contentlabelingservice.model.LabelRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.regex.Matcher
import java.util.regex.Pattern

@Service
class LabelService {

    @Autowired
    BaseRulesComponent baseRulesComponent

    String createLabel(LabelRequest request) {
        List<BaseRule> baseRules = baseRulesComponent.getBaseRules(request.getPublisher(), request.getManualType())
        String label = "Not Found"
        for(BaseRule baseRule : baseRules) {
            Pattern pattern = Pattern.compile(baseRule.regex)
            Matcher matcher = pattern.matcher(request.getTitle())
            if (matcher.matches()) {
                label = baseRule.getRule()
                break
            }
        }
        label
    }
}
