package net.barrage.chatwhitelabel.di

import net.barrage.chatwhitelabel.data.remote.ktor.ApiImpl
import net.barrage.chatwhitelabel.data.repository.AgentRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.AuthRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.HistoryRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.ChatRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.UserRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.WebSocketRepositoryImpl
import net.barrage.chatwhitelabel.domain.remote.ktor.Api
import net.barrage.chatwhitelabel.domain.repository.AgentRepository
import net.barrage.chatwhitelabel.domain.repository.AuthRepository
import net.barrage.chatwhitelabel.domain.repository.HistoryRepository
import net.barrage.chatwhitelabel.domain.repository.ChatRepository
import net.barrage.chatwhitelabel.domain.repository.UserRepository
import net.barrage.chatwhitelabel.domain.repository.WebSocketRepository
import net.barrage.chatwhitelabel.domain.usecase.agents.GetAgentsUseCase
import net.barrage.chatwhitelabel.domain.usecase.auth.LoginUseCase
import net.barrage.chatwhitelabel.domain.usecase.auth.LogoutUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.HistoryUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.DeleteChatUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.UpdateChatTitleUseCase
import net.barrage.chatwhitelabel.domain.usecase.user.CurrentUserUseCase
import net.barrage.chatwhitelabel.domain.usecase.ws.WebSocketTokenUseCase
import net.barrage.chatwhitelabel.ui.screens.chat.ChatViewModel
import net.barrage.chatwhitelabel.ui.screens.login.LoginViewModel
import net.barrage.chatwhitelabel.utils.DataStoreTokenStorage
import net.barrage.chatwhitelabel.utils.TokenStorage
import net.barrage.chatwhitelabel.utils.coreComponent
import net.barrage.chatwhitelabel.utils.restClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// Module for use cases
val useCaseModule = module {
    single<LoginUseCase> { LoginUseCase(get()) }
    single<LogoutUseCase> { LogoutUseCase(get()) }
    single<CurrentUserUseCase> { CurrentUserUseCase(get()) }
    single<HistoryUseCase> { HistoryUseCase(get()) }
    single<WebSocketTokenUseCase> { WebSocketTokenUseCase(get()) }
    single<UpdateChatTitleUseCase> { UpdateChatTitleUseCase(get()) }
    single<DeleteChatUseCase> { DeleteChatUseCase(get()) }
    single<GetAgentsUseCase> { GetAgentsUseCase(get()) }
}

// Module for mappers
val mapperModule = module {}

val apiModule = module { single<Api> { ApiImpl(restClient, get()) } }

// Module for repositories
val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    single<WebSocketRepository> { WebSocketRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    single<AgentRepository> { AgentRepositoryImpl(get()) }
}

// Module for app
val appModule = module {
    single<TokenStorage> { DataStoreTokenStorage(coreComponent.appPreferences) }
}

// Module for view models
val viewModelModule = module {
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { ChatViewModel(get(), get(), get(), get()) }
}

// Combine all modules into a single module list for Koin initialization
fun allModules() =
    useCaseModule + apiModule + mapperModule + repositoryModule + appModule + viewModelModule
