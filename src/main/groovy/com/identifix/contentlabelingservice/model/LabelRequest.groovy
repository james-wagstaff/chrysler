package com.identifix.contentlabelingservice.model

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "All details needed to find a label through base rules.")
class LabelRequest {
    @ApiModelProperty(example = "Ford")
    private String publisher
    @ApiModelProperty(example = "Workshop")
    private String manualType
    @ApiModelProperty(example = "Removal and Installation: Axle Assembly - (Front Drive Axle/Differential)")
    private String title
    @ApiModelProperty(example = "[113 > 2: Chassis > 05: Driveline > 205-03 Front Drive Axle/Differential > Removal and Installation > Axle Assembly]")
    private String tocPath

    String getPublisher() {
        return publisher
    }

    void setPublisher(String publisher) {
        this.publisher = publisher
    }

    String getManualType() {
        return manualType
    }

    void setManualType(String manualType) {
        this.manualType = manualType
    }

    String getTitle() {
        return title
    }

    void setTitle(String title) {
        this.title = title
    }

    String getTocPath() {
        return tocPath
    }

    void setTocPath(String tocPath) {
        this.tocPath = tocPath
    }
}
