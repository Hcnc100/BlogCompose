package com.nullpointer.blogcompose.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.services.UploadPostServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepo: PostRepoImpl,
) : ViewModel() {

    companion object {
        const val INIT_POST_LOAD = 10
    }

    private val _listPost = MutableStateFlow<Resource<List<Post>>>(Resource.Loading())
    val listPost = _listPost.asStateFlow()

    private val _listMyPost = MutableStateFlow<Resource<List<Post>>>(Resource.Loading())
    val listMyPost = _listMyPost.asStateFlow()

    private val enableGetLastPost = mutableStateOf(true)
    private val currentSizePost = mutableStateOf(INIT_POST_LOAD)

    init {
        fetchLastPost()
        fetchMyLastPost()
    }

    fun fetchLastPost() = viewModelScope.launch(Dispatchers.IO) {
        _listPost.value = Resource.Loading()
        _listPost.value = try {
            Resource.Success(postRepo.getLastPost(INIT_POST_LOAD).also {
                withContext(Dispatchers.Main) {
                    currentSizePost.value = it.size
                }
            })
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    fun fetchMyLastPost() = viewModelScope.launch(Dispatchers.IO) {
        _listMyPost.value = Resource.Loading()
        _listMyPost.value = try {
            Resource.Success(postRepo.getMyLastPost(INIT_POST_LOAD))
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    fun concatenatePost() = viewModelScope.launch(Dispatchers.IO) {
        try{
            if (enableGetLastPost.value) {
                val sizeEstimated = currentSizePost.value + 10
                Timber.d("Se solicitaron $sizeEstimated")
                val listNewPost = postRepo.getLastPost(sizeEstimated)
                Timber.d("Se obtuvieron ${listNewPost.size}")
                withContext(Dispatchers.Main) {
                    currentSizePost.value=listNewPost.size
                    _listPost.value = Resource.Success(listNewPost)
                    if (listNewPost.size < sizeEstimated){
                        enableGetLastPost.value = false
                        Timber.d("Limite alcalzado se desactivo la carga infinita")
                    }
                }
            }
        }catch (e:Exception){
            Timber.d("Excepcion con la carga concatedana ${e.message}")
            if(e is CancellationException) throw e
        }
    }

    fun uploadNewPost(post: Post) = viewModelScope.launch(Dispatchers.IO) {
        postRepo.updatePost(post)
    }
}