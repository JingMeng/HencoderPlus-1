

TestRemember.kt


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

}

@Composable
fun TestWithKey(param: Int) {

}

/**
 * 这个地方那个怎么都执行了，有点夸张了
 */
@Composable
fun CommentBox(param: Int) {

}

@Composable
fun CommentBox2(param: Int) {

}

@Composable
fun RememberDemo() {

}


---------- 和下面的写法是存在区别的：

fun ComponentActivity.testRemember() {

    @Composable
    fun TestWithoutKey(param: Int) {

    }

    @Composable
    fun TestWithKey(param: Int) {

    }

    /**
     * 这个地方那个怎么都执行了，有点夸张了
     */
    @Composable
    fun CommentBox(param: Int) {

    }

    @Composable
    fun CommentBox2(param: Int) {

    }

    @Composable
    fun RememberDemo() {

    }

    /**
     * 在方法里面嵌套方法，需要把调用的方法写在前面
     */
    setContent {
        RememberDemo()
    }
}



----------------------------------------------------------


非常棒的观察 👀，你抓到的是一个 **Compose 作用域与重组边界（Composition boundary）** 的核心现象。
我们来一步步剖析你看到的“TextField 输入 → 所有组件都重组”这个现象为什么会发生，以及为什么“把 Composable 定义在外面”后就不会。

---

## 💡 现象描述

你写的这段代码：

```kotlin
fun ComponentActivity.testRemember() {
    setContent {
        RememberDemo()
    }
}
```

当 `RememberDemo()` 及所有 `@Composable` 都定义在外层（函数外部）时：

* 在 `TextField` 输入时，只有 `CommentBox` 这块区域重组 ✅
* `TestWithKey` / `TestWithoutKey` 等其他部分不会被波及。

但当你把所有 Composable（`RememberDemo`、`CommentBox` 等）都定义 **在 `fun testRemember()` 函数内部** 时：

* 每次输入 `TextField`，所有组件都会重新执行重组 🚨

---

## 🧩 关键原因：**Composable 函数定义的位置影响重组稳定性**

### ✅ 情况一：定义在函数外部（稳定引用）

```kotlin
@Composable
fun RememberDemo() { ... }
```

* 这类函数在编译期是一个**稳定的可组合函数引用**。
* Compose 框架知道这是一个**不会变的函数对象**，它的**函数引用地址是固定的**。
* 因此，只要它的参数（例如 `param`）没有变化，就不会重新执行整个树的重组。

简而言之：

> 函数定义在外层时，它对 Compose 来说是“静态存在的”，Compose 可以安全地跳过无关区域的重组。

---

### ⚠️ 情况二：定义在函数内部（动态声明）

```kotlin
fun ComponentActivity.testRemember() {
    @Composable
    fun RememberDemo() { ... }   // ⛔️ 定义在函数内部
    setContent { RememberDemo() }
}
```

这样一来，`RememberDemo` 不再是“静态函数引用”，而是：

* 每次调用 `testRemember()` 或触发 `setContent` 重组时，
  Compose 都会认为这是一个**新的函数声明**。
* 因为内部定义的 `@Composable` **在每次 composition 时都会重新创建**（它是闭包的一部分）。

结果就是：

> Compose 认为整个 Composition 树的结构可能变化了，因此会触发整棵 UI 树的重组（甚至 TextField 输入也触发所有 recomposition）。

---

## 🔍 为什么 TextField 也会触发全量重组？

`TextField` 的输入是一个非常敏感的可组合状态更新，它会触发：

```kotlin
input.value = it  // 触发状态变化 → 记忆点 (remember) 所在的作用域重组
```

但是当你的 Composables 都定义在 `testRemember()` 内时：

* 每个 Composable 都是一个 **新对象**（新函数引用）
* Compose 无法识别哪些函数是相同的，只能重新执行全部。

换句话说：

> Compose 认为“这是一棵新的树”，于是全量重组。

---

## 🧠 类比理解

| 定义位置 | Compose 看到的是什么 | 重组行为          |
| ---- | -------------- | ------------- |
| 函数外部 | 稳定的函数引用（静态函数）  | 局部重组（仅状态相关部分） |
| 函数内部 | 新闭包函数，每次可能不同   | 整棵树重组（性能灾难）   |

---

## ✅ 最佳实践建议

1. **永远不要在 `setContent` 的 lambda 或 `Activity` 的函数体内定义 Composable。**
   除非你真的需要一个动态生成的可组合 lambda。

   ❌ 错误示例：

   ```kotlin
   fun ComponentActivity.testRemember() {
       setContent {
           @Composable
           fun RememberDemo() { ... } // BAD
           RememberDemo()
       }
   }
   ```

   ✅ 正确示例：

   ```kotlin
   fun ComponentActivity.testRemember() {
       setContent {
           RememberDemo()
       }
   }

   @Composable
   fun RememberDemo() { ... }
   ```

2. **所有可复用的 Composable 应该定义为顶层函数**，这样 Compose 编译器可以为其生成稳定的重组代码。

3. 只有在确实需要“动态生成 Composable”时（如 Slot API 或自定义 Layout DSL），才在内部定义 Composable。

---

## 📋 你可以自己验证的对比测试

```kotlin
@Composable
fun TestOuter() {
    var text by remember { mutableStateOf("") }
    TextField(value = text, onValueChange = { text = it })
    Log.i("compose", "TestOuter recomposed")
}

fun ComponentActivity.testOuter() {
    setContent { TestOuter() }  // ✅ 正常，仅 TextField 自重组
}
```

VS

```kotlin
fun ComponentActivity.testInner() {
    setContent {
        @Composable
        fun TestInner() {
            var text by remember { mutableStateOf("") }
            TextField(value = text, onValueChange = { text = it })
            Log.i("compose", "TestInner recomposed")
        }
        TestInner()
    }
}
// ⚠️ TextField 输入会导致全树重组
```

---

## 🧩 一句话总结

> **Composable 函数定义在外层 = 稳定引用；定义在内部 = 动态闭包，每次新建，重组范围变大。**

---

要不要我帮你画一张图，展示「函数定义位置」如何影响 Composition 树稳定性？这张图会非常直观。

