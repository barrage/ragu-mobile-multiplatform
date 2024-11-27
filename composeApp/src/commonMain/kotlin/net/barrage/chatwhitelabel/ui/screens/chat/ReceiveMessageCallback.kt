package net.barrage.chatwhitelabel.ui.screens.chat

interface ReceiveMessageCallback {
    fun receiveMessage(message: String)

    fun enableSending()

    fun disableSending()

    fun stopReceivingMessage()

    fun onError(error: String)

    fun setTtsLanguage(language: String)

    fun setChatTitle(title: String, chatId: String)

    fun closeChat()
}
