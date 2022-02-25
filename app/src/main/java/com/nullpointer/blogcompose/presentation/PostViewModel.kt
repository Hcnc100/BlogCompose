package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepo: PostRepoImpl,
) : ViewModel() {
    private val _listPost = MutableStateFlow<Resource<List<Post>>>(Resource.Loading())
    val listPost = _listPost.asStateFlow()

    init {
        initFetchPost()
    }

    fun initFetchPost() = viewModelScope.launch(Dispatchers.IO) {
        _listPost.value = Resource.Loading()
        _listPost.value = try {
            Resource.Success(postRepo.getLastPost(10))
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    fun concateFetchPost() = viewModelScope.launch(Dispatchers.IO) {
        _listPost.value = Resource.Loading()
        _listPost.value = try {
            Resource.Success(postRepo.getLastPost(10))
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    fun uploadNewPost(post:Post)= viewModelScope.launch (Dispatchers.IO){
        postRepo.updatePost(post)
    }
}