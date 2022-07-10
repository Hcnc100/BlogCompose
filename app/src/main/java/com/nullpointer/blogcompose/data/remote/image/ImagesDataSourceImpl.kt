package com.nullpointer.blogcompose.data.remote.image

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import kotlin.math.absoluteValue

class ImagesDataSourceImpl:ImageDataSource {
    private val refStorage = Firebase.storage.getReference("imgPost")
    private val refImgUser = Firebase.storage.getReference("imgUsers")
    private val idUser get() = Firebase.auth.currentUser?.uid ?: "NoAuth"

    override suspend fun uploadImageUserWithOutState(uriImg: Uri): Uri {
        val result=refImgUser.child(idUser).putFile(uriImg).await()
        return result.storage.downloadUrl.await()
    }


    override fun uploadImageUserWithState(uriImg: Uri): Flow<StorageUploadTaskResult> =
        callbackFlow {
            refImgUser.child(idUser).putFile(uriImg).addOnSuccessListener { task ->
                task.storage.downloadUrl.addOnSuccessListener {
                    trySend(StorageUploadTaskResult.Complete.Success(it.toString()))
                    close()
                }.addOnFailureListener {
                    trySend(StorageUploadTaskResult.Complete.Failed(it))
                    close()
                }
            }
                .addOnFailureListener {
                    trySend(StorageUploadTaskResult.Complete.Failed(it))
                    close(it)
                }
                .addOnPausedListener {
                    trySend(StorageUploadTaskResult.Paused(it))
                }
                .addOnProgressListener { task ->
                    val progress =
                        ((task.bytesTransferred.toFloat() / task.totalByteCount.absoluteValue.toFloat()) * 100).toInt()
                    val progressResult = StorageUploadTaskResult.InProgress(progress)
                    if (trySend(progressResult).isFailure) Timber.d("callback is busy")
                }
                .addOnCanceledListener {
                    trySend(StorageUploadTaskResult.Complete.Cancelled)
                    close(CancellationException("Download was cancelled"))
                }
            awaitClose()
        }



    override fun uploadImagePostWithState(uriImg: Uri, name: String): Flow<StorageUploadTaskResult> =
        callbackFlow {
            refStorage.child(idUser).child(name).putFile(uriImg).addOnSuccessListener { task ->
                task.storage.downloadUrl.addOnSuccessListener {
                    trySend(StorageUploadTaskResult.Complete.Success(it.toString()))
                    close()
                }.addOnFailureListener {
                    trySend(StorageUploadTaskResult.Complete.Failed(it))
                    close()
                }
            }
                .addOnFailureListener {
                    trySend(StorageUploadTaskResult.Complete.Failed(it))
                    close(it)
                }
                .addOnPausedListener {
                    trySend(StorageUploadTaskResult.Paused(it))
                }
                .addOnProgressListener { task ->
                    val progress =
                        ((task.bytesTransferred.toFloat() / task.totalByteCount.absoluteValue.toFloat()) * 100).toInt()
                    val progressResult = StorageUploadTaskResult.InProgress(progress)
                    if (trySend(progressResult).isFailure) Timber.d("callback is busy")
                }
                .addOnCanceledListener {
                    trySend(StorageUploadTaskResult.Complete.Cancelled)
                    close(CancellationException("Download was cancelled"))
                }
            awaitClose()
        }


}
