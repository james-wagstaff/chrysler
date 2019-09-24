package com.identifix.contentlabelingservice.model

import groovy.transform.ToString
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@ApiModel(description = "All details needed to find a label through base rules.")
@ToString(includeNames = true, includePackage = false)
class LabelRequest {
    @ApiModelProperty(example = "Ford")
    @NotNull(message = "publisher must not be null")
    @NotEmpty(message = "publisher must not be empty")
    String publisher
    @ApiModelProperty(example = "Workshop")
    @NotNull(message = "manualType must not be null")
    @NotEmpty(message = "manualType must not be empty")
    String manualType
    @ApiModelProperty(example = "Axle Assembly")
    @NotNull(message = "title must not be null")
    @NotEmpty(message = "title must not be empty")
    String title
    @ApiModelProperty(example = "Removal and Installation")
    @NotNull(message = "header must not be null")
    @NotEmpty(message = "header must not be empty")
    String header
    @ApiModelProperty(example = "false")
    boolean refresh
}
