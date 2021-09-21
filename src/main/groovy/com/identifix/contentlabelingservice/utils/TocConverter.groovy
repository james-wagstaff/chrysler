package com.identifix.contentlabelingservice.utils

import org.json.JSONArray
import org.jsoup.Jsoup
import org.jsoup.parser.Parser

class TocConverter {

    static byte[] tocXmlToJson(byte[] content) {
        org.jsoup.nodes.Document document = Jsoup.parse(new String(content), CommonConstants.UTF_8, Parser.xmlParser())
        JSONArray array = new JSONArray()
        document.select(CommonConstants.ITEM).each { item ->
            if (item.hasAttr(CommonConstants.PAGECODE)) {
                JSONArray object = new JSONArray()
                object.put(item.attr(CommonConstants.PAGECODE))
                object.put(item.attr(CommonConstants.TITLE).trim())
                String tocPath = item.parent().attr(CommonConstants.TITLE)
                object.put(tocPath)
                array.put(object)
            }
        }
        array.toString().bytes
    }

}
