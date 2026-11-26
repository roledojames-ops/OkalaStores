package com.dante2194.okalastores.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dante2194.okalastores.R

val Vazirmatn = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
)

private fun style(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal,
) = TextStyle(
    fontFamily = Vazirmatn,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
)

val OkalaTypography = Typography(
    displayLarge = style(42, 52, FontWeight.Bold),
    displayMedium = style(36, 46, FontWeight.Bold),
    displaySmall = style(30, 40, FontWeight.Bold),
    headlineLarge = style(28, 38, FontWeight.Bold),
    headlineMedium = style(24, 34, FontWeight.Bold),
    headlineSmall = style(21, 30, FontWeight.Bold),
    titleLarge = style(19, 28, FontWeight.Bold),
    titleMedium = style(16, 24, FontWeight.Medium),
    titleSmall = style(14, 21, FontWeight.Medium),
    bodyLarge = style(16, 26),
    bodyMedium = style(14, 23),
    bodySmall = style(12, 20),
    labelLarge = style(14, 20, FontWeight.Medium),
    labelMedium = style(12, 18, FontWeight.Medium),
    labelSmall = style(11, 16, FontWeight.Medium),
)
