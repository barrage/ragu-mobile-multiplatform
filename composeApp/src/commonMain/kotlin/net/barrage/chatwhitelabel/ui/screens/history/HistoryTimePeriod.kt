package net.barrage.chatwhitelabel.ui.screens.history

import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.last_30_days
import chatwhitelabel.composeapp.generated.resources.last_7_days
import chatwhitelabel.composeapp.generated.resources.last_year
import chatwhitelabel.composeapp.generated.resources.today
import chatwhitelabel.composeapp.generated.resources.yesterday
import org.jetbrains.compose.resources.StringResource

enum class HistoryTimePeriod(val label: StringResource) {
    TODAY(Res.string.today),
    YESTERDAY(Res.string.yesterday),
    LAST_7_DAYS(Res.string.last_7_days),
    LAST_30_DAYS(Res.string.last_30_days),
    LAST_YEAR(Res.string.last_year),
}
