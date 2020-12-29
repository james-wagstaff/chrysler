package com.identifix.contentlabelingservice.label.chrysler

import com.identifix.contentlabelingservice.label.AbstractLabelMaker
import com.identifix.contentlabelingservice.label.AbstractLabelMakerMessage
import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.model.Document
import com.identifix.contentlabelingservice.utils.CommonConstants
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.kraken.client.bean.Manual
import groovy.util.logging.Slf4j
import org.json.JSONArray
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.springframework.stereotype.Component

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import java.util.regex.Matcher

@Component
@Slf4j
class ServiceInfoLabelMaker extends AbstractLabelMaker {

    Class messageClass = ChryslerServiceInfoMessage

    @Override
    MessageHandlerResponse labelContent(LabelMakerMessage message) {
        Manual manual = krakenClient.getManuals(message.year, message.model, null).findAll { it.publisherManualCategory.toLowerCase() == CommonConstants.MANUAL_TYPE_SERVICE.toLowerCase() } [0]

        byte[] content = krakenClient.getManualBytes(manual)
        String csvFile = krakenClient.buildChryslerLabelingCsv(message.year, message.model, toJSONArrayByte(content), CommonConstants.MANUAL_TYPE_SERVICE)
        labelCsv(csvFile, CommonConstants.PUBLISHER_CHRYSLER, CommonConstants.MANUAL_TYPE_SERVICE, message.manualId, removeDuplicateWords(manual.title))
        SUCCESS
    }

    @Override
    Class getMessageClass() {
        return messageClass
    }

    @Override
    String titleValue(Document document) {
        return document.title
    }

    @Override
    String headerValue(Document document) {
        return cleanToc(document.tocpath).split(TOC_SPLIT)[1]
    }

    static byte[] toJSONArrayByte(byte[] content) {
        org.jsoup.nodes.Document document = Jsoup.parse(new String(content), "", Parser.xmlParser())
        JSONArray array = new JSONArray()
        document.select(CommonConstants.ITEM).each { item ->
            if (item.hasAttr(CommonConstants.PAGECODE)) {
                JSONArray object = new JSONArray()
                object.put(item.attr(CommonConstants.PAGECODE))
                String tocPath = extractToc(item.attr(CommonConstants.TITLE))
                object.put(tocPath)
                object.put(item.attr(CommonConstants.TITLE).replace(tocPath, "").trim())
                array.put(object)
            }
        }
        array.toString().bytes
    }

    static String extractToc(String title) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"\\\\[(.*?)\\\\]\"", java.util.regex.Pattern.CASE_INSENSITIVE)
        Matcher matcher = pattern.matcher(title)
        matcher.find() ? matcher.group() : title
    }

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

class ChryslerServiceInfoMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/CHRYSLER_SERVICE_LABEL/)
    String crawlerTypeKey
    @NotNull
    String manualType
}
