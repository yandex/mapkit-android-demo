package com.yandex.navikitdemo.ui.guidance.views.maneuver.lanes

import android.content.Context
import com.yandex.mapkit.directions.driving.Lane
import com.yandex.mapkit.directions.driving.LaneDirection
import com.yandex.mapkit.directions.driving.LaneSign
import com.yandex.navikitdemo.ui.guidance.views.maneuver.UpcomingManeuverResourcesProvider
import java.util.*

private enum class ResourceSize {
    BIG, SMALL
}

private enum class LaneImageTag {
    LEFT, RIGHT, BIG, SMALL, TURN, POCKET
}

class LaneItemsFactory(
    private val resourcesProvider: UpcomingManeuverResourcesProvider,
) {

    private fun getResourceForLaneDirection(direction: LaneDirection, size: ResourceSize): Int {
        return if (size === ResourceSize.BIG) {
            resourcesProvider.getLargeResourceForLaneDirection(direction)
        } else {
            resourcesProvider.getSmallResourceForLaneDirection(direction)
        }
    }

    private fun getLaneMetadata(direction: LaneDirection): EnumSet<LaneImageTag> {
        return when (direction) {
            LaneDirection.LEFT180 -> EnumSet.of(LaneImageTag.LEFT, LaneImageTag.SMALL)
            LaneDirection.LEFT135, LaneDirection.LEFT90 -> EnumSet.of(
                LaneImageTag.LEFT,
                LaneImageTag.TURN
            )
            LaneDirection.LEFT45, LaneDirection.LEFT_SHIFT -> EnumSet.of(
                LaneImageTag.LEFT,
                LaneImageTag.BIG
            )
            LaneDirection.STRAIGHT_AHEAD -> EnumSet.of(LaneImageTag.BIG)
            LaneDirection.RIGHT45, LaneDirection.RIGHT_SHIFT -> EnumSet.of(
                LaneImageTag.RIGHT,
                LaneImageTag.BIG
            )
            LaneDirection.RIGHT90, LaneDirection.RIGHT135 -> EnumSet.of(
                LaneImageTag.RIGHT,
                LaneImageTag.TURN
            )
            LaneDirection.RIGHT180 -> EnumSet.of(LaneImageTag.RIGHT, LaneImageTag.SMALL)
            LaneDirection.LEFT_FROM_RIGHT -> EnumSet.of(LaneImageTag.LEFT, LaneImageTag.POCKET)
            LaneDirection.RIGHT_FROM_LEFT -> EnumSet.of(LaneImageTag.RIGHT, LaneImageTag.POCKET)
            LaneDirection.UNKNOWN_DIRECTION -> throw IllegalStateException("Unexpected value: $direction")
        }
    }

    private fun hasProperty(direction: LaneDirection, property: LaneImageTag): Boolean {
        return getLaneMetadata(direction).contains(property)
    }

    private fun hasProperty(directions: List<LaneDirection>, property: LaneImageTag): Boolean {
        for (direction in directions) {
            if (hasProperty(direction, property)) {
                return true
            }
        }
        return false
    }

    private fun arrowsSizesInLane(lane: Lane): SortedMap<LaneDirection, ResourceSize?> {
        val directions = lane.directions
        val result: SortedMap<LaneDirection, ResourceSize?> = TreeMap()

        if (directions.size == 1) {
            result[directions[0]] = ResourceSize.BIG
            return result
        }

        val containsBig = hasProperty(directions, LaneImageTag.BIG)
        val containsTurn = hasProperty(directions, LaneImageTag.TURN)

        for (direction in directions) {
            var size: ResourceSize

            size = if (hasProperty(direction, LaneImageTag.BIG)) {
                ResourceSize.BIG
            } else if (hasProperty(direction, LaneImageTag.SMALL)) {
                ResourceSize.SMALL
            } else if (hasProperty(direction, LaneImageTag.TURN)) {
                if (containsBig) ResourceSize.SMALL else ResourceSize.BIG
            } else if (hasProperty(direction, LaneImageTag.POCKET)) {
                if (containsBig || containsTurn) ResourceSize.SMALL else ResourceSize.BIG
            } else {
                throw IllegalStateException("Wrong directions processing")
            }

            result[direction] = size
        }
        return result
    }

    private fun transformArrows(
        lanesArrowsSizes: List<SortedMap<LaneDirection, ResourceSize?>>,
        hasSmallTurn: Boolean,
        hasSmallTurnFromPocket: Boolean
    ): List<SortedMap<LaneDirection, ResourceSize?>> {
        for (sizes in lanesArrowsSizes) {
            if (sizes.size != 1) continue

            val direction = sizes.firstKey()
            var size = sizes[direction]

            if (hasSmallTurn && hasProperty(direction, LaneImageTag.TURN)) {
                size = ResourceSize.SMALL
            }

            if (hasSmallTurnFromPocket && hasProperty(direction, LaneImageTag.POCKET)) {
                size = ResourceSize.SMALL
            }

            sizes[direction] = size
        }
        return lanesArrowsSizes
    }

    private fun arrowsSizes(laneSign: LaneSign): List<SortedMap<LaneDirection, ResourceSize?>> {
        val result: MutableList<SortedMap<LaneDirection, ResourceSize?>> = ArrayList()

        var hasSmallTurn = false
        var hasSmallTurnFromPocket = false

        for (lane in laneSign.lanes) {
            val sizes = arrowsSizesInLane(lane)
            for ((direction, size) in sizes) {
                if (size !== ResourceSize.SMALL) {
                    continue
                }
                hasSmallTurn = hasSmallTurn or hasProperty(direction, LaneImageTag.TURN)
                hasSmallTurnFromPocket = hasSmallTurnFromPocket or hasProperty(
                    direction,
                    LaneImageTag.POCKET
                )
            }

            result.add(sizes)
        }

        return transformArrows(result, hasSmallTurn, hasSmallTurnFromPocket)
    }

    fun createLaneItems(laneSign: LaneSign, context: Context): List<LaneItem> {
        val allSizes = arrowsSizes(laneSign)
        val lanes = laneSign.lanes
        val lanesCount = lanes.size

        val result = ArrayList<LaneItem>(lanesCount)

        for (i in 0 until lanesCount) {
            val item = LaneItem.Builder()
            val lane = lanes[i]
            val sizes = allSizes[i]

            for (direction in lane.directions) {
                item.secondaryLanesImages.add(
                    getResourceForLaneDirection(
                        direction,
                        sizes[direction]!!
                    )
                )
            }

            val highlightedDirection = lane.highlightedDirection
            if (highlightedDirection != null) {
                item.setHighlightedLaneImage(
                    getResourceForLaneDirection(
                        highlightedDirection,
                        sizes[highlightedDirection]!!
                    )
                )
            }

            val isFirst = i == 0
            val isLast = i + 1 == lanesCount

            item.hasLeftOffset = isFirst && hasProperty(lane.directions, LaneImageTag.LEFT)
            item.hasRightOffset = isLast && hasProperty(lane.directions, LaneImageTag.RIGHT)

            if (isFirst) {
                item.hasLargeOverlap = false
            } else {
                val previousLane = lanes[i - 1]

                val bothHaveLeft90or180 = (previousLane.directions.contains(LaneDirection.LEFT90) ||
                    previousLane.directions.contains(LaneDirection.LEFT180)) &&
                    (lane.directions.contains(LaneDirection.LEFT90) ||
                        lane.directions.contains(LaneDirection.LEFT180))

                val bothHaveRight90or180 =
                    (previousLane.directions.contains(LaneDirection.RIGHT90) ||
                        previousLane.directions.contains(LaneDirection.RIGHT180)) &&
                        (lane.directions.contains(LaneDirection.RIGHT90) ||
                            lane.directions.contains(LaneDirection.RIGHT180))

                val right90or180toStraight =
                    (previousLane.directions.contains(LaneDirection.RIGHT90) ||
                        previousLane.directions.contains(LaneDirection.RIGHT180)) &&
                        lane.directions.contains(LaneDirection.STRAIGHT_AHEAD)

                val straightToLeft90or180 =
                    previousLane.directions.contains(LaneDirection.STRAIGHT_AHEAD) &&
                        (lane.directions.contains(LaneDirection.LEFT90) ||
                            lane.directions.contains(LaneDirection.LEFT180))

                item.hasLargeOverlap = !(bothHaveLeft90or180 || bothHaveRight90or180 ||
                    right90or180toStraight || straightToLeft90or180)
            }

            val laneKindResources = resourcesProvider.getLaneKindResources(lane.laneKind, context)
            if (laneKindResources != null) {
                item.setLaneKindImage(laneKindResources.first)
                item.setLaneKindCropImage(laneKindResources.second)
            }

            if (item.secondaryLanesImages.isNotEmpty() || item.highlightedLaneImage != null || item.laneKindImage != null) {
                result.add(item.build())
            }
        }

        return result
    }
}
