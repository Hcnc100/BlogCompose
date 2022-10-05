package com.nullpointer.blogcompose.domain.compress

import android.net.Uri

interface CompressRepository {
    suspend fun compressImage(uri: Uri): Uri
}