package com.nullpointer.blogcompose.domain.toke

import com.nullpointer.blogcompose.data.remote.TokenDataSource

class TokenRepoImpl(
    private val tokenDataSource: TokenDataSource) : TokenRepository {
    override suspend fun updateCurrentToken(token: String) =
        tokenDataSource.updateTokenUser(token)

}