package com.identifix.contentlabelingservice.model

class BaseRule {
    private BaseRuleType type
    private String regex
    private String rule

    String getRegex() {
        return regex
    }

    void setRegex(String regex) {
        this.regex = regex
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
