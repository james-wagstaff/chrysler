package com.identifix.contentlabelingservice.web

import groovy.transform.Canonical

@Canonical
class Document {
    private String label
    private String title
    private String tocpath
    private String category
    private String linkToPage
    private String nuxeoId

    Document(String label, String title, String tocpath, String category, String linkToPage, String nuxeoId) {
        this.label = label
        this.title = title
        this.tocpath = tocpath
        this.category = category
        this.linkToPage = linkToPage
        this.nuxeoId = nuxeoId
    }

    String getLabel() {
        return label
    }

    void setLabel(String label) {
        this.label = label
    }

    String getTitle() {
        return title
    }

    void setTitle(String title) {
        this.title = title
    }

    String getTocpath() {
        return tocpath
    }

    void setTocpath(String tocpath) {
        this.tocpath = tocpath
    }

    String getCategory() {
        return category
    }

    void setCategory(String category) {
        this.category = category
    }

    String getLinkToPage() {
        return linkToPage
    }

    void setLinkToPage(String linkToPage) {
        this.linkToPage = linkToPage
    }

    String getNuxeoId() {
        return nuxeoId
    }

    void setNuxeoId(String nuxeoId) {
        this.nuxeoId = nuxeoId
    }
}
