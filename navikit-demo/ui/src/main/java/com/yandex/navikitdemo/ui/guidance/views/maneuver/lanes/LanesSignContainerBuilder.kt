package com.yandex.navikitdemo.ui.guidance.views.maneuver.lanes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.yandex.navikitdemo.ui.guidance.views.maneuver.UpcomingManeuverResourcesProvider
import com.yandex.navikitdemo.ui.guidance.views.maneuver.UpcomingManeuverResourcesProvider.DimensionType

internal class LaneSignContainerBuilder(
    private val context: Context,
    private val laneItems: List<LaneItem>,
    private val laneSignContainerLayout: LinearLayout,
    resourcesProvider: UpcomingManeuverResourcesProvider,
) {

    private val smallOverlap = resourcesProvider.getLaneItemDimension(DimensionType.SMALL_OVERLAP)
    private val largeOverlap = resourcesProvider.getLaneItemDimension(DimensionType.LARGE_OVERLAP)

    private val indent = resourcesProvider.getLaneItemDimension(DimensionType.INDENT)

    fun build() {
        laneSignContainerLayout.removeAllViews()
        if (laneItems.size == 1) {
            buildSingle()
            return
        }
        buildFirst()
        buildCentral()
        buildLast()
    }

    private val containerWidth = resourcesProvider.getLaneItemDimension(DimensionType.WIDTH)
    private val containerHeight = resourcesProvider.getLaneItemDimension(DimensionType.HEIGHT)

    private val alphaFactor = 0.4F
    private val blendColor = Color.argb((alphaFactor * 255).toInt(), 255, 255, 255)

    private fun renderLaneItem(laneItem: LaneItem): Bitmap {
        val bitmap = Bitmap.createBitmap(containerWidth.toInt(), containerHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        for (directionImage in laneItem.secondaryLanesImages) {
            drawInContainer(ContextCompat.getDrawable(context, directionImage)!!, canvas)
        }

        canvas.drawColor(blendColor, PorterDuff.Mode.MULTIPLY)

        laneItem.highlightedLaneImage?.let {
            drawInContainer(ContextCompat.getDrawable(context, it)!!, canvas)
        }

        laneItem.laneKindImage?.let { laneKindImage ->
            val laneKindDrawable = ContextCompat.getDrawable(context, laneKindImage)!!

            laneItem.laneKindCropImage?.let { laneKindCropImage ->
                val cropDrawable = ContextCompat.getDrawable(context, laneKindCropImage)!!
                val cropBitmap = Bitmap.createBitmap(
                    containerWidth.toInt(), containerHeight.toInt(), Bitmap.Config.ARGB_8888)

                val cropCanvas = Canvas(cropBitmap)
                drawInContainer(cropDrawable, cropCanvas)

                val paint = Paint()
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
                canvas.drawBitmap(cropBitmap, 0.0F, 0.0F, paint)
            }

            if (laneItem.highlightedLaneImage == null) {
                laneKindDrawable.colorFilter =
                    PorterDuffColorFilter(blendColor, PorterDuff.Mode.SRC_IN)
            }
            drawInContainer(laneKindDrawable, canvas)
        }

        return bitmap
    }

    private fun drawInContainer(drawable: Drawable, canvas: Canvas) {
        drawable.setBounds(0, 0, containerWidth.toInt(), containerHeight.toInt())
        drawable.draw(canvas)
    }

    private fun createItemContainer(laneItem: LaneItem): ImageView {
        val result = ImageView(context)

        val params = LinearLayout.LayoutParams(containerWidth.toInt(), containerHeight.toInt())
        result.layoutParams = params

        val bitmap = renderLaneItem(laneItem)
        result.setImageBitmap(bitmap)

        return result
    }

    private fun applyOverlap(lane: LaneItem, params: LinearLayout.LayoutParams) {
        params.leftMargin = if (lane.hasLargeOverlap) -largeOverlap else -smallOverlap
    }

    private fun buildSingle() {
        val laneItem = laneItems[0]
        val itemContainer = createItemContainer(laneItem)

        val params = itemContainer.layoutParams as LinearLayout.LayoutParams
        if (laneItem.hasLeftOffset) {
            params.leftMargin = indent
        }
        if (laneItem.hasRightOffset) {
            params.rightMargin = indent
        }

        laneSignContainerLayout.addView(itemContainer, 0)
    }

    private fun buildFirst() {
        val laneItem = laneItems[0]
        val itemContainer = createItemContainer(laneItem)

        if (laneItem.hasLeftOffset) {
            val params = itemContainer.layoutParams as LinearLayout.LayoutParams
            params.leftMargin = indent
        }

        laneSignContainerLayout.addView(itemContainer, 0)
    }

    private fun buildCentral() {
        for (i in 1 until (laneItems.size - 1)) {
            val laneItem = laneItems[i]

            val itemContainer = createItemContainer(laneItem)
            val params = itemContainer.layoutParams as LinearLayout.LayoutParams
            applyOverlap(laneItem, params)

            laneSignContainerLayout.addView(itemContainer, i)
        }
    }

    private fun buildLast() {
        val laneItem = laneItems.last()

        val itemContainer = createItemContainer(laneItem)
        val params = itemContainer.layoutParams as LinearLayout.LayoutParams

        if (laneItem.hasRightOffset) {
            params.rightMargin = indent
        }

        applyOverlap(laneItem, params)

        laneSignContainerLayout.addView(itemContainer, laneItems.size - 1)
    }
}
