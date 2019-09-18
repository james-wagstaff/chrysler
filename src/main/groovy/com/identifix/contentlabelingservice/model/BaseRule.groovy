package com.identifix.contentlabelingservice.model

class BaseRule {
    private BaseRuleType type
    private String regexWords
    private String rule

    String getRegexWords() {
        return regexWords
    }

    void setRegexWords(String regexWords) {
        this.regexWords = regexWords
    }

    String getRule() {
        return rule
    }

    void setRule(String rule) {
        this.rule = rule
    }

    BaseRuleType getType() {
        return type
    }

    void setType(BaseRuleType type) {
        this.type = type
    }
}

enum BaseRuleType {
    PAGE,HEADER
}
