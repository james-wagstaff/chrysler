package com.identifix.contentlabelingservice.model

class BaseRule {
    BaseRuleType type
    String regexWords
    String rule
}

enum BaseRuleType {
    PAGE,HEADER
}
