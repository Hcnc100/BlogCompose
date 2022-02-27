package com.nullpointer.blogcompose.ui.customs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R

@Composable
fun FabProgress(
    modifier: Modifier=Modifier,
    cancelAction:(()->Unit)?=null,
    isLoading: Boolean,
    changeLoading:(Boolean)->Unit,
    colorNormal:Color,
    contentNormal: @Composable ()->Unit,
) {
    FloatingActionButton(
        onClick = { changeLoading(!isLoading) },
        modifier = Modifier.animateContentSize(),
        backgroundColor = if (!isLoading) MaterialTheme.colors.secondary else Color.LightGray,
    ) {
        if (isLoading) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(54.dp))
                if(cancelAction!=null){
                    Icon(painterResource(id = R.drawable.ic_clear),
                        contentDescription = "",
                        Modifier.padding(10.dp))
                }
            }
        } else {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(painterResource(id = R.drawable.ic_publish),
//                    contentDescription = "",
//                    Modifier.padding(10.dp))
//                Text("Publicar Post",
//                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 15.dp))
//            }
            contentNormal()
        }
    }
}