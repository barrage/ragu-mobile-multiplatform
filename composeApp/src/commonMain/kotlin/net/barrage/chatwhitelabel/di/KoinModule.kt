package net.barrage.chatwhitelabel.di

import net.barrage.chatwhitelabel.data.remote.ktor.ApiImpl
import net.barrage.chatwhitelabel.data.repository.AgentRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.AuthRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.ChatRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.HistoryRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.UserRepositoryImpl
import net.barrage.chatwhitelabel.data.repository.WebSocketRepositoryImpl
import net.barrage.chatwhitelabel.domain.remote.ktor.Api
import net.barrage.chatwhitelabel.domain.repository.AgentRepository
import net.barrage.chatwhitelabel.domain.repository.AuthRepository
import net.barrage.chatwhitelabel.domain.repository.ChatRepository
import net.barrage.chatwhitelabel.domain.repository.HistoryRepository
import net.barrage.chatwhitelabel.domain.repository.UserRepository
import net.barrage.chatwhitelabel.domain.repository.WebSocketRepository
import net.barrage.chatwhitelabel.domain.usecase.agents.GetAgentsUseCase
import net.barrage.chatwhitelabel.domain.usecase.auth.LoginUseCase
import net.barrage.chatwhitelabel.domain.usecase.auth.LogoutUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.ChatUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.DeleteChatUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.EvaluateMessageUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.GetChatByIdUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.GetChatHistoryUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.GetChatMessagesByIdUseCase
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

/**
 * Module for use cases
 * Defines single instances for various use cases in the application
 */
val useCaseModule = module {
    single<LoginUseCase> { LoginUseCase(get()) }
    single<LogoutUseCase> { LogoutUseCase(get()) }
    single<CurrentUserUseCase> { CurrentUserUseCase(get()) }
    single<GetChatHistoryUseCase> { GetChatHistoryUseCase(get()) }
    single<GetChatMessagesByIdUseCase> { GetChatMessagesByIdUseCase(get()) }
    single<WebSocketTokenUseCase> { WebSocketTokenUseCase(get()) }
    single<UpdateChatTitleUseCase> { UpdateChatTitleUseCase(get()) }
    single<DeleteChatUseCase> { DeleteChatUseCase(get()) }
    single<GetAgentsUseCase> { GetAgentsUseCase(get()) }
    single<EvaluateMessageUseCase> { EvaluateMessageUseCase(get()) }
    single<GetChatByIdUseCase> { GetChatByIdUseCase(get()) }
    single<ChatUseCase> { ChatUseCase(get(), get(), get(), get(), get(), get(), get()) }
}

/**
 * Module for API
 * Defines a single instance of the API implementation
 */
val apiModule = module {
    single<Api> { ApiImpl(restClient, get()) }
}

/**
 * Module for repositories
 * Defines single instances for various repositories
 */
val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    single<WebSocketRepository> { WebSocketRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    single<AgentRepository> { AgentRepositoryImpl(get()) }
}

/**
 * Module for app-wide dependencies
 * Defines a single instance for TokenStorage
 */
val appModule = module {
    single<TokenStorage> { DataStoreTokenStorage(coreComponent.appPreferences) }
}

/**
 * Module for view models
 * Defines view model factories for LoginViewModel and ChatViewModel
 */
val viewModelModule = module {
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { ChatViewModel(get(), get(), get(), get()) }
}

/**
 * Combines all modules into a single list for Koin initialization
 *
 * @return A list of all Koin modules used in the application
 */
fun allModules() =
    useCaseModule + apiModule + repositoryModule + appModule + viewModelModule