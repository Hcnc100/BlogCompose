package com.nullpointer.blogcompose.data.remote

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import kotlin.math.absoluteValue

class ImagesDataSource {
    private val refStorage = Firebase.storage.getReference("imgPost")
    private val refImgUser = Firebase.storage.getReference("imgUsers")
    private val idUser = Firebase.auth.currentUser?.uid ?: "NoAuth"


    @OptIn(ExperimentalCoroutinesApi::class)
    fun uploadImageUser(uriImg: Uri, name: String): Flow<StorageUploadTaskResult> =
        callbackFlow {
            refImgUser.child(idUser).child(name).putFile(uriImg).addOnSuccessListener { task ->
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun uploadImagePost(uriImg: Uri, name: String): Flow<StorageUploadTaskResult> =
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
