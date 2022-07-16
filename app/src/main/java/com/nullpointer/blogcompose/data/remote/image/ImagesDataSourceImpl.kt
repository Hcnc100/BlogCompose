package com.nullpointer.blogcompose.data.remote.image

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.absoluteValue

class ImagesDataSourceImpl:ImagesDataSource {
    private val refStorage = Firebase.storage.getReference("imgPost")
    private val refImgUser = Firebase.storage.getReference("imgUsers")
    private val idUser get() = Firebase.auth.currentUser?.uid ?: "NoAuth"

    override suspend fun uploadImageUserWithOutState(uriImg: Uri): Uri {
        val result = refImgUser.child(idUser).putFile(uriImg).await()
        return result.storage.downloadUrl.await()
    }

    private fun uploadImageWithState(
        uploadTask: UploadTask
    ) = callbackFlow {
        uploadTask.addOnSuccessListener { task ->
            task.storage.downloadUrl.addOnSuccessListener {
                trySend(StorageUploadTaskResult.Complete.Success(it.toString()))
                close()
            }.addOnFailureListener {
                trySend(StorageUploadTaskResult.Complete.Failed(it))
                close()
            }
        }.addOnFailureListener {
            trySend(StorageUploadTaskResult.Complete.Failed(it))
            close(it)
        }.addOnPausedListener {
            trySend(StorageUploadTaskResult.Paused(it))
        }.addOnProgressListener { task ->
            val progressResult = StorageUploadTaskResult.InProgress(task.percent.toInt())
            trySend(progressResult)
        }.addOnCanceledListener {
            trySend(StorageUploadTaskResult.Complete.Cancelled)
            close(CancellationException("Upload was cancelled"))
        }
        awaitClose()
    }


    override fun uploadImageUserWithState(uriImg: Uri): Flow<StorageUploadTaskResult> =
        uploadImageWithState(refImgUser.child(idUser).putFile(uriImg))


    override fun uploadImagePostWithState(
        uriImg: Uri,
        name: String
    ): Flow<StorageUploadTaskResult> = uploadImageWithState(
        refStorage.child(idUser).child(name).putFile(uriImg)
    )

    private val UploadTask.TaskSnapshot.percent
        get() = (bytesTransferred.toFloat() / totalByteCount.absoluteValue.toFloat()) * 100

}
