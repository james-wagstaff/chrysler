package com.identifix.contentlabelingservice.web

import groovy.transform.Canonical

@Canonical
class Document {
    String label
    String title
    String tocpath
    String category
    String linkToPage
    String nuxeoId

    @SuppressWarnings('ParameterCount')
    Document(String label, String title, String tocpath, String category, String linkToPage, String nuxeoId) {
        this.label = label
        this.title = title
        this.tocpath = tocpath
        this.category = category
        this.linkToPage = linkToPage
        this.nuxeoId = nuxeoId
    }
}
