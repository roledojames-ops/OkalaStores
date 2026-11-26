package com.dante2194.okalastores.util

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun friendlyError(throwable: Throwable): String = when (throwable) {
    is UnknownHostException -> "اتصال اینترنت برقرار نیست."
    is SocketTimeoutException -> "اتصال به سرور زمان‌بر شد. دوباره تلاش کنید."
    is HttpException -> when (throwable.code()) {
        401, 403 -> "دسترسی به سرویس فروشگاه‌ها ممکن نیست."
        429 -> "درخواست‌های بیش از حد؛ چند لحظه صبر کنید."
        in 500..599 -> "سرور اُکالا موقتاً در دسترس نیست."
        else -> "خطای سرور (${throwable.code()})"
    }
    is IOException -> "اتصال به اینترنت برقرار نیست."
    else -> throwable.message?.takeIf { it.isNotBlank() } ?: "خطای نامشخص"
}
