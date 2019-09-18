package com.identifix.contentlabelingservice.model

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "All details needed to find a label through base rules.")
class LabelRequest {
    @ApiModelProperty(example = "Ford")
    String publisher
    @ApiModelProperty(example = "Workshop")
    String manualType
    @ApiModelProperty(example = "Removal and Installation: Axle Assembly - (Front Drive Axle/Differential)")
    String title
    @ApiModelProperty(example = "\"[113 > 2: Chassis > 05: Driveline > 205-03 Front Drive Axle/Differential > Removal and Installation > Axle Assembly]\"")
    String tocPath
}
