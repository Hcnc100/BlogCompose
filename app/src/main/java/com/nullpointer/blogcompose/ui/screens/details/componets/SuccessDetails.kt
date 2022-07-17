package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.ActionDetails
import com.nullpointer.blogcompose.models.posts.Post


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuccessFullDetails(
    listComment: List<Comment>,
    post: Post,
    modifier: Modifier = Modifier,
    hasNewComments: Boolean,
    listState: LazyListState,
    isLoading: Boolean,
    actionPost: (ActionDetails) -> Unit,
) {
    Box(modifier = modifier) {
        LazyColumn(state = listState) {
            item {
                HeaderBlogDetails(blog = post) {
                    actionPost(ActionDetails.LIKE_THIS_POST)
                }
            }
            item {
                if (post.numberComments != listState.layoutInfo.totalItemsCount - 2) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(30.dp)
                        )
                    } else {
                        Text(
                            stringResource(id = R.string.text_load_more_comments),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { actionPost(ActionDetails.GET_MORE_COMMENTS) }
                                .padding(10.dp)
                        )
                    }

                }
            }
            items(listComment.size, key = { index -> listComment[index].id }) { index ->
                ItemComment(
                    comment = listComment[index],
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
        if (hasNewComments)
            Text(
                stringResource(id = R.string.message_has_new_comments),
                modifier = Modifier
                    .padding(10.dp)
                    .background(MaterialTheme.colors.primary)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { actionPost(ActionDetails.HAS_NEW_COMMENTS) }
                    .align(Alignment.BottomCenter)
            )
    }

}

@Composable
private fun ItemComment(
    comment: Comment,
    modifier: Modifier
) {
    Row(modifier = modifier.padding(10.dp)) {
        ImageProfileUser(
            urlImg = comment.userComment?.urlImg.toString(),
            contentDescription = stringResource(id = R.string.description_img_owner_post),
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Card(shape = RoundedCornerShape(10.dp)) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(comment.userComment?.name.toString())
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = comment.comment)
            }
        }
    }
}

@Composable
fun ImageProfileUser(
    urlImg: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .crossfade(true).transformations(CircleCropTransformation())
            .data(urlImg).build(),
        contentDescription = contentDescription,
        placeholder = painterResource(id = R.drawable.ic_person),
        error = painterResource(id = R.drawable.ic_person),
        modifier = modifier
    )
}

@Composable
fun HeaderBlogDetails(
    blog: Post,
    modifier: Modifier = Modifier,
    actionLike: () -> Unit
) {

    val iconLike = derivedStateOf {
        if (blog.ownerLike) R.drawable.ic_fav else R.drawable.ic_unfav
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            ImageProfileUser(
                urlImg = blog.userPoster?.urlImg ?: "",
                contentDescription = "",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = blog.userPoster?.name ?: "")
        }
        Text(text = blog.description, modifier = Modifier.padding(10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        AsyncImage(
            model = blog.urlImage,
            contentDescription = stringResource(id = R.string.description_img_post),
            modifier = Modifier
                .fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(modifier = Modifier
                .padding(10.dp)
                .clickable {
                    actionLike()
                }, verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconLike.value), contentDescription = stringResource(
                        id = R.string.description_like_button
                    )
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.text_count_likes, blog.numberLikes)
                )
            }
            Text(
                text = stringResource(id = R.string.text_count_comments, blog.numberComments),
                modifier = Modifier.padding(10.dp)
            )
        }

    }
}
