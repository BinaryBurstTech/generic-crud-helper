package cz.binaryburst.basetest.service.embeddable.dto.partial

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.binaryburst.generic.dto.BasePartialDtoInput
import jakarta.persistence.Embeddable

@Embeddable
data class EmbeddedChildTestInsertDto @JsonCreator constructor(
    @JsonProperty("name") var name: String = ""
): BasePartialDtoInput()