package net.barrage.ragu.domain.usecase.user

import net.barrage.ragu.domain.repository.UserRepository

class UpdateProfileAvatarUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(avatar: ByteArray) = userRepository.updateAvatar(avatar)
}