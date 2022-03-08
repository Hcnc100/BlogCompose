package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

class NotifyDataSource {
    private val auth = Firebase.auth
    private val database = Firebase.firestore.collection("notifications")

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getListNotify() = callbackFlow {
        var subscribe: ListenerRegistration? = null
        try {
            val refNotify = database.document(auth.currentUser?.uid!!).collection("listNotify").orderBy("timestamp")
            subscribe = refNotify.addSnapshotListener { value, error ->
                if (error != null) throw error
                val list=value?.documents?.mapNotNull {document->
                    document.toObject(Notify::class.java)?.apply {
                        id=document.id
                    }
                } ?: emptyList()
                trySend(list)
            }
        } catch (e: Exception) {
            Timber.e("Error en las notificaciones $e")
        }
        finally {
            awaitClose { subscribe?.remove() }
        }
    }

}