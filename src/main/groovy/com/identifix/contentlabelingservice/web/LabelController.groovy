package com.identifix.contentlabelingservice.web

import com.identifix.contentlabelingservice.model.LabelRequest
import com.identifix.contentlabelingservice.service.LabelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController()
class LabelController {
    @Autowired
    LabelService labelService

    @PostMapping('/label')
    ResponseEntity createLabel(@RequestBody LabelRequest labelRequest) {
        new ResponseEntity(labelService.createLabel(labelRequest), HttpStatus.OK)
    }
}
