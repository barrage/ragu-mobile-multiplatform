package net.barrage.chatwhitelabel.ui.screens.chat

interface ReceiveMessageCallback {
    fun receiveMessage(message: String)

    fun enableSending()

    fun disableSending()

    fun stopReceivingMessage()

    fun setTtsLanguage(language: String)

    fun setChatId(chatId: String)
}
