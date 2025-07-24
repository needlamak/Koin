package com.koin.data.user

import com.koin.domain.user.User

fun UserEntity.toDomain(): User = User(
    id = id,
    username = username,
    email = email,
    avatarUri = avatarUri
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    username = username,
    email = email,
    avatarUri = avatarUri
)
