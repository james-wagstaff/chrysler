package com.identifix.contentlabelingservice.web

import com.identifix.contentlabelingservice.model.LabelRequest
import com.identifix.contentlabelingservice.service.GitService
import com.identifix.contentlabelingservice.service.LabelService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid

@RestController()
@Api(value="Labeling Services")
class LabelController {
    @Autowired
    LabelService labelService
    @Autowired
    GitService gitService

    @ApiOperation(value = "Creates a label using the base rules.")
    @ApiResponses(value = [
            @ApiResponse(code = 200, message = "Successfully found a label"),
            @ApiResponse(code = 204, message = "Response was Successful, but no label found"),
            @ApiResponse(code = 400, message = "Invalid RequestBody"),
            @ApiResponse(code = 503, message = "BitBucket network issue")
    ])
    @PostMapping('/label')
    ResponseEntity createLabel(@ApiParam(value = "All values needed to create a label using base rules.", required = true)
                               @Valid @RequestBody LabelRequest labelRequest) {
        String label  = labelService.createLabel(labelRequest)
        new ResponseEntity(label, label ? HttpStatus.OK : HttpStatus.NO_CONTENT)
    }

    @GetMapping('/labels/{oem}/{manualType}/{id}/bytes')
    ResponseEntity findLabelsForManual(@ApiParam(defaultValue ='Toyota') @PathVariable String oem,
                                       @ApiParam(defaultValue ='Repair Manual') @PathVariable  String manualType,
                                       @ApiParam(defaultValue ='314a4a99-6731-43e0-8920-79d29553e855')  @PathVariable String id) {
        byte[] csv = gitService.findCsv(oem, manualType, id)
        return new ResponseEntity(csv, csv == null ? HttpStatus.NOT_FOUND : HttpStatus.OK)
    }
}
