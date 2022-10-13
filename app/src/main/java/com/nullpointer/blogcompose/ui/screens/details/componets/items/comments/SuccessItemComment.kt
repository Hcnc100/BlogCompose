package com.nullpointer.blogcompose.ui.screens.details.componets.items.comments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.ui.share.SimpleImage

@Composable
fun ItemComment(
    comment: Comment,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ImageComment(imageUserComment = comment.userComment?.urlImg.toString())
        BodyComment(
            nameUserComment = comment.userComment?.name.toString(),
            comment = comment.comment
        )
    }
}

@Composable
private fun BodyComment(
    comment: String,
    nameUserComment: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(nameUserComment)
            Text(text = comment)
        }
    }
}

@Composable
private fun ImageComment(
    imageUserComment: String,
    modifier: Modifier = Modifier
) {
    SimpleImage(
        modifier = modifier,
        isCircular = true,
        image = imageUserComment,
        placeholder = R.drawable.ic_person,
        sizeImage = dimensionResource(id = R.dimen.size_photo_comment),
        contentDescription = stringResource(id = R.string.description_img_owner_post)
    )
}