package com.identifix.contentlabelingservice.web

import com.identifix.contentlabelingservice.model.LabelRequest
import com.identifix.contentlabelingservice.service.GitService
import com.identifix.contentlabelingservice.service.LabelService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class LabelControllerSpec extends Specification {
    GitService mockGitService = Mock(GitService)
    LabelService mockLabelService = Mock(LabelService)

    LabelController systemUnderTest = new LabelController().with {
        gitService = mockGitService
        labelService = mockLabelService
        it
    }

    def 'Label found'() {
        when:
            ResponseEntity actual = systemUnderTest.createLabel(new LabelRequest().with {
                publisher = 'Ford'
                manualType = 'Workshop'
                title = 'Axle Assembly'
                header = 'Removal and Installation'
                it
            })
        then:
            1 * mockLabelService.createLabel(_ as LabelRequest) >> "test"
            actual.statusCode == HttpStatus.OK
            actual.body == 'test'
    }

    def 'Label not found'() {
        when:
            ResponseEntity actual = systemUnderTest.createLabel(new LabelRequest().with {
                publisher = 'Ford'
                manualType = 'Workshop'
                title = 'Axle Assembly'
                header = 'Removal and Installation'
                it
            })
        then:
            1 * mockLabelService.createLabel(_ as LabelRequest) >> ""
            actual.statusCode == HttpStatus.NO_CONTENT
            actual.body == ''
    }

    def 'findLabelsForManual found'() {
        when:
            ResponseEntity actual = systemUnderTest.findLabelsForManual('Toyota', 'Repair Manual', 'test')
        then:
            1 * mockGitService.findCsv('Toyota', 'Repair Manual', 'test') >> 'test'.bytes
            actual.statusCode == HttpStatus.OK
            actual.body == 'test'.bytes
    }

    def 'findLabelsForManual not found'() {
        when:
            ResponseEntity actual = systemUnderTest.findLabelsForManual('Toyota', 'Repair Manual', 'test')
        then:
            1 * mockGitService.findCsv('Toyota', 'Repair Manual', 'test') >> null
            actual.statusCode == HttpStatus.NOT_FOUND
            actual.body == null
    }
}
