package com.nullpointer.blogcompose.domain.toke

interface TokenRepository {
    suspend fun updateCurrentToken(token: String)
}