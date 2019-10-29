package com.identifix.contentlabelingservice.web

import com.identifix.contentlabelingservice.model.LabelRequest
import com.identifix.contentlabelingservice.service.LabelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import spock.lang.Specification

@SpringBootTest
class LabelControllerSpec extends Specification {
    @Autowired
    LabelController controller

    def "Label comes back in response entity untouched" () {
        given:
            LabelRequest request = new LabelRequest(publisher:'ford', manualType:'test', title:'testing', header:'header')
            controller.labelService = Mock(LabelService)
            controller.labelService.createLabel(_) >> 'answer'
        when:
            ResponseEntity label = controller.createLabel(request)
        then:
            label != null
            label.body == 'answer'
    }
}
