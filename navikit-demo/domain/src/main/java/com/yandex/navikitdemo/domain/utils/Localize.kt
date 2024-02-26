package com.yandex.navikitdemo.domain.utils

import com.yandex.runtime.i18n.I18nManagerFactory

fun Double.localizeSpeed(): String {
    return I18nManagerFactory.getI18nManagerInstance().localizeSpeed(this)
}

fun Int.localizeDistance(): String {
    return I18nManagerFactory.getI18nManagerInstance().localizeDistance(this)
}
