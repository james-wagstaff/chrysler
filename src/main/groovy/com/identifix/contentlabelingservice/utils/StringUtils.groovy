package com.identifix.contentlabelingservice.utils

import java.util.regex.Matcher

class StringUtils {

    static String removeDuplicateWords(String input) {
        String noRepeat = input
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(CommonConstants.REGEX_REPEATING_WORD, java.util.regex.Pattern.CASE_INSENSITIVE)
        Matcher m = p.matcher(input)
        while (m.find()) {
            noRepeat = input.replaceAll(m.group(), m.group(1))
        }
        return noRepeat
    }

}
