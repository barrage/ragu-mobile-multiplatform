package net.barrage.chatwhitelabel.ui.screens.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.usecase.chat.HistoryUseCase
import net.barrage.chatwhitelabel.utils.BluePrimary
import net.barrage.chatwhitelabel.utils.BrownPrimary
import net.barrage.chatwhitelabel.utils.GreenPrimary
import net.barrage.chatwhitelabel.utils.LimePrimary
import net.barrage.chatwhitelabel.utils.MagentaPrimary
import net.barrage.chatwhitelabel.utils.OrangePrimary
import net.barrage.chatwhitelabel.utils.RedPrimary
import net.barrage.chatwhitelabel.utils.SagePrimary
import net.barrage.chatwhitelabel.utils.TealPrimary
import net.barrage.chatwhitelabel.utils.VioletPrimary
import net.barrage.chatwhitelabel.utils.YellowPrimary

class HistoryViewModel(private val historyUseCase: HistoryUseCase) : ViewModel() {
    var historyViewState by
        mutableStateOf(
            HistoryModalDrawerContentViewState(
                persistentListOf(
                    White,
                    SagePrimary,
                    TealPrimary,
                    BluePrimary,
                    VioletPrimary,
                    LimePrimary,
                    GreenPrimary,
                    YellowPrimary,
                    OrangePrimary,
                    RedPrimary,
                    MagentaPrimary,
                    BrownPrimary,
                ),
                HistoryScreenState.Idle,
            )
        )
        private set

    init {
        viewModelScope.launch {
            historyViewState =
                when (val response = historyUseCase.invoke(1, 10)) {
                    is Response.Failure -> {
                        Napier.d("Error: ${response.e}")
                        historyViewState.copy(history = HistoryScreenState.Error)
                    }

                    Response.Loading -> {
                        Napier.d("Loading")
                        historyViewState.copy(history = HistoryScreenState.Loading)
                    }

                    is Response.Success -> {
                        Napier.d("Success: ${response.data}")
                        historyViewState.copy(history = HistoryScreenState.Success(response.data))
                    }
                }
        }
    }
}
