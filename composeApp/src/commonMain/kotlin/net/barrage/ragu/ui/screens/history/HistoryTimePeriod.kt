package net.barrage.ragu.ui.screens.history

import org.jetbrains.compose.resources.StringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.last_30_days
import ragumultiplatform.composeapp.generated.resources.last_7_days
import ragumultiplatform.composeapp.generated.resources.last_year
import ragumultiplatform.composeapp.generated.resources.today
import ragumultiplatform.composeapp.generated.resources.yesterday

enum class HistoryTimePeriod(val label: StringResource) {
    TODAY(Res.string.today),
    YESTERDAY(Res.string.yesterday),
    LAST_7_DAYS(Res.string.last_7_days),
    LAST_30_DAYS(Res.string.last_30_days),
    LAST_YEAR(Res.string.last_year),
}
