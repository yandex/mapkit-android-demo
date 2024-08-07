package com.yandex.navikitdemo.domain.models

data class SectionRange(var from: Double = 0.0, var to: Double = 0.0) {

    fun appendRange(remainingDistance: Double, expectedRange: Double): SectionRange {
        from = to - remainingDistance
        to += expectedRange - remainingDistance
        return this
    }

}
