package com.yandex.navigationdemo.ui.guidance.views.maneuver.lanes

class LaneItem private constructor(
    val secondaryLanesImages: List<Int>,
    val highlightedLaneImage: Int?,
    val laneKindImage: Int?,
    val laneKindCropImage: Int?,
    val hasLeftOffset: Boolean,
    val hasLargeOverlap: Boolean,
    val hasRightOffset: Boolean,
) {

    class Builder {
        val secondaryLanesImages = mutableListOf<Int>()
        var highlightedLaneImage: Int? = null
        var laneKindImage: Int? = null
        var laneKindCropImage: Int? = null
        var hasLeftOffset = false
        var hasLargeOverlap = false
        var hasRightOffset = false

        fun setHighlightedLaneImage(highlightedLaneImage: Int) {
            this.highlightedLaneImage = highlightedLaneImage
        }

        fun setLaneKindImage(laneKindImage: Int) {
            this.laneKindImage = laneKindImage
        }

        fun setLaneKindCropImage(laneKindCropImage: Int) {
            this.laneKindCropImage = laneKindCropImage
        }

        fun build() = LaneItem(
            secondaryLanesImages,
            highlightedLaneImage,
            laneKindImage,
            laneKindCropImage,
            hasLeftOffset,
            hasLargeOverlap,
            hasRightOffset
        )
    }
}
