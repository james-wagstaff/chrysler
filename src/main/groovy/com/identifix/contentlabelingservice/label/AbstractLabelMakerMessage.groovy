package com.identifix.contentlabelingservice.label

import javax.validation.constraints.NotNull

class AbstractLabelMakerMessage implements LabelMakerMessage {
    @NotNull
    String year
    @NotNull
    String model
    @NotNull
    String manualId
}
