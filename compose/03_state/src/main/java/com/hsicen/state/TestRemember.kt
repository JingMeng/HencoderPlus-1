package com.hsicen.state

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun ComponentActivity.testRemember() {
    /**
     * 在方法里面嵌套方法，需要把调用的方法写在前面
     */
    setContent {
        RememberDemo()
    }


}

@Composable
fun TestWithoutKey(param: Int) {
    val obj = remember { mutableIntStateOf(param) }
    Log.i("testRemember", "🔵 [WithoutKey] recomposed, param=$param, obj.value=${obj.intValue}")

    Text("Without key → obj.value = ${obj.intValue}")
}

@Composable
fun TestWithKey(param: Int) {
    val obj = remember(param) { mutableIntStateOf(param) }
    Log.i("testRemember", "🟢 [WithKey] recomposed, param=$param, obj.value=${obj.intValue}")

    Text("With key → obj.value = ${obj.intValue}")
}

/**
 * 这个地方那个怎么都执行了，有点夸张了
 */
@Composable
fun CommentBox(param: Int) {
    val input = remember { mutableStateOf("") }  // 保持用户输入
    TextField(value = input.value, onValueChange = { input.value = it })
    //不加这个就不会导致重新执行了
    Text("CommentBox param is $param")

    Log.i("testRemember", "🟢 CommentBox recomposed, param=$param, input.value=${input.value}")
}

@Composable
fun CommentBox2(param: Int) {
    val input = remember { mutableStateOf("") }  // 保持用户输入
    TextField(value = input.value, onValueChange = { input.value = it })
    //和 CommentBox 区分，没有使用参数 -日志里面也不要使用
    Log.i("testRemember", "🟢 CommentBox2 recomposed  input.value=${input.value}")
}

@Composable
fun RememberDemo() {
    var param by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("param = $param")
        Spacer(Modifier.height(8.dp))

        Button(onClick = { param++ }) {
            Text("Change param")
        }

        Spacer(Modifier.height(16.dp))
        CommentBox2(param)
        Spacer(Modifier.height(16.dp))
        CommentBox(param)
        Spacer(Modifier.height(16.dp))

        Divider()

        // 不带 key 的 remember
        TestWithoutKey(param)

        Divider()

        // 带 key 的 remember(param)
        TestWithKey(param)
    }
}
