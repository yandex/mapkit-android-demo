package com.yandex.mapkitdemo

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible

class ClusterView(context: Context) : LinearLayout(context) {

    private val greenText by lazy { findViewById<TextView>(R.id.text_green_pins) }
    private val yellowText by lazy { findViewById<TextView>(R.id.text_yello_pins) }
    private val redText by lazy { findViewById<TextView>(R.id.text_red_pins) }

    private val greenLayout by lazy { findViewById<View>(R.id.layout_green_group) }
    private val yellowLayout by lazy { findViewById<View>(R.id.layout_yellow_group) }
    private val redLayout by lazy { findViewById<View>(R.id.layout_red_group) }

    init {
        inflate(context, R.layout.cluster_view, this)
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        orientation = HORIZONTAL
        setBackgroundResource(R.drawable.cluster_view_background)
    }

    fun setData(placemarkTypes: List<PlacemarkType>) {
        PlacemarkType.values().forEach {
            updateViews(placemarkTypes, it)
        }
    }

    private fun updateViews(
        placemarkTypes: List<PlacemarkType>,
        type: PlacemarkType
    ) {
        val (textView, layoutView) = when (type) {
            PlacemarkType.GREEN -> greenText to greenLayout
            PlacemarkType.YELLOW -> yellowText to yellowLayout
            PlacemarkType.RED -> redText to redLayout
        }
        val value = placemarkTypes.countTypes(type)

        textView.text = value.toString()
        layoutView.isVisible = value != 0
    }

    private fun List<PlacemarkType>.countTypes(type: PlacemarkType) = count { it == type }
}
