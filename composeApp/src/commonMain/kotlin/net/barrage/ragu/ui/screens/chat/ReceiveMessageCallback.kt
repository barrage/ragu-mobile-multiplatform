package net.barrage.ragu.ui.screens.chat

interface ReceiveMessageCallback {
    fun receiveMessage(message: String)

    fun enableSending()

    fun disableSending()

    fun stopReceivingMessage()

    fun onError(error: String, retry: Boolean = false)

    fun setTtsLanguage(language: String)

    fun setChatTitle(title: String, chatId: String)

    fun closeChat()
}
