package com.identifix.contentlabelingservice.web

import com.identifix.contentlabelingservice.model.LabelRequest
import com.identifix.contentlabelingservice.service.LabelService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid

@RestController()
@Api(value="Labeling Services", description="Operations to apply a label to a given document.")
class LabelController {
    @Autowired
    LabelService labelService

    @ApiOperation(value = "Creates a label using the base rules.")
    @PostMapping('/label')
    ResponseEntity createLabel(@ApiParam(value = "All values needed to create a label using base rules.", required = true)
                               @Valid @RequestBody LabelRequest labelRequest) {
        new ResponseEntity(labelService.createLabel(labelRequest), HttpStatus.OK)
    }
}
