package com.nullpointer.blogcompose.core.states

import com.google.firebase.storage.UploadTask

sealed class StorageUploadTaskResult {

    data class InProgress(val percent: Int) : StorageUploadTaskResult()
    data class Paused(val task: UploadTask.TaskSnapshot) : StorageUploadTaskResult()
    object Init : StorageUploadTaskResult()

    sealed class Complete : StorageUploadTaskResult() {
        data class Success(val urlFile: String) : Complete()
        data class Failed(val error: Throwable) : Complete()
        object Cancelled : Complete()
    }
}