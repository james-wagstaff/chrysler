package com.identifix.contentlabelingservice.model

import io.swagger.annotations.ApiModelProperty

class LabelRequest {
    @ApiModelProperty(example = "Ford")
    String publisher
    @ApiModelProperty(example = "Workshop")
    String manualType
    @ApiModelProperty(example = "test")
    String title
    @ApiModelProperty(example = "test")
    String tocPath
}
