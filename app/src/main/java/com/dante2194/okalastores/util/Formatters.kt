package com.dante2194.okalastores.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private val FA_LOCALE = Locale("fa", "IR")

fun String.toPersianDigits(): String = buildString(length) {
    for (character in this@toPersianDigits) {
        append(if (character in '0'..'9') '۰' + (character - '0') else character)
    }
}

fun Int.toPersianDigits(): String = toString().toPersianDigits()

fun Long.toPersianDigits(): String = toString().toPersianDigits()

fun formatNumber(value: Long): String =
    runCatching { NumberFormat.getIntegerInstance(FA_LOCALE).format(value) }
        .getOrElse { value.toString().toPersianDigits() }

fun formatDistance(km: Double?): String {
    if (km == null || km.isNaN() || km < 0) return "—"
    return if (km < 1.0) {
        "${(km * 1000).roundToInt().toPersianDigits()} متر"
    } else {
        "${String.format(Locale.US, "%.1f", km).toPersianDigits()} کیلومتر"
    }
}

fun formatRating(rate: Double?): String {
    if (rate == null || rate <= 0.0) return "جدید"
    return String.format(Locale.US, "%.1f", rate).toPersianDigits()
}

fun formatRatingCount(count: Int): String =
    if (count <= 0) "بدون امتیاز" else "${formatNumber(count.toLong())} امتیاز"

fun formatToman(rial: Double?, freeLabel: Boolean = false): String {
    if (rial == null) return "—"
    if (rial <= 0.0) return if (freeLabel) "رایگان" else "۰ تومان"
    val toman = (rial / 10.0).roundToLong()
    return "${formatNumber(toman)} تومان"
}

fun formatMinimumOrder(rial: Double?): String {
    if (rial == null || rial <= 0.0) return "بدون حداقل"
    val toman = (rial / 10.0).roundToLong()
    return "${formatNumber(toman)} تومان"
}

fun formatEta(caption: String?): String =
    caption?.takeIf { it.isNotBlank() } ?: "—"

fun etaSortKey(caption: String?): Int {
    val value = caption ?: return Int.MAX_VALUE
    if (value.contains("بسته")) return Int.MAX_VALUE
    if (value.contains("فردا")) return 24 * 60 + 60
    val ascii = buildString {
        for (character in value) {
            append(
                when (character) {
                    in '۰'..'۹' -> '0' + (character - '۰')
                    else -> character
                },
            )
        }
    }
    val digits = ascii.filter { it in '0'..'9' }
    return digits.toIntOrNull() ?: 999
}

fun formatRadius(km: Double?): String {
    if (km == null || km <= 0) return ""
    val value = if (km % 1.0 == 0.0) km.toInt().toString() else String.format(Locale.US, "%.1f", km)
    return "تا ${value.toPersianDigits()} کیلومتر"
}
