package com.me.ktl

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "test MainActivity"

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            lifecycleScope.launch {
                test()
            }
        }
    }

    fun test() = runBlocking<Unit> {
        launch { // 默认继承 parent coroutine 的 CoroutineDispatcher，指定运行在 main 线程
                 Log.d(TAG, "main Thread.currentThread().name: " + Thread.currentThread().name)
                 delay(100)
                 Log.d(TAG, "main Thread.currentThread().name: " + Thread.currentThread().name)
        }
        launch(Dispatchers.Unconfined) {
            Log.d(TAG, "Unconfined bef delay Thread.currentThread().name: " + Thread.currentThread().name)
            delay(100)
            Log.d(TAG, "Unconfined aft delay Thread.currentThread().name: " + Thread.currentThread().name)
        }
    }

    // fun test() {
    //     // 创建一个单线程的协程调度器，下面两个协程都运行在这同一线程上
    //     val coroutineDispatcher = newSingleThreadContext("ctx")
    //     // 启动协程 1
    //     GlobalScope.launch(coroutineDispatcher) {
    //         Log.d(TAG, "1st coroutine")
    //         delay(200)
    //         Log.d(TAG, "1st coroutine")
    //     }
    //     // 启动协程 2
    //     GlobalScope.launch(coroutineDispatcher) {
    //         Log.d(TAG, "2nd coroutine")
    //         delay(100)
    //         Log.d(TAG, "2nd coroutine")
    //     }
    //     // 保证 main 线程存活，确保上面两个协程运行完成
    //     Thread.sleep(500)
    // }
    
    // fun test() {
    //     lifecycleScope.launch {
    //         // 任务1会立即启动, 并且会在别的线程上并行执行
    //         val deferred1 = async { requestDataAsync1() }
    //         // 上一个步骤只是启动了任务1, 并不会挂起当前协程
    //         // 所以任务2也会立即启动, 也会在别的线程上并行执行
    //         val deferred2 = async { requestDataAsync2() }
    //         // 先等待任务1结束(等了约1000ms), 
    //         // 然后等待任务2, 由于它和任务1几乎同时启动的, 所以也很快完成了
    //         Log.d(TAG, "data1=$deferred1.await(), data2=$deferred2.await()")
    //         // Log.d("data1=$deferred2.await(), data2=$deferred2.await()")
    //     }
    //     Thread.sleep(10000L) // 继续无视这个sleep
    // }
    // suspend fun requestDataAsync1(): String {
    //     delay(1000L)
    //     return "data1"    
    // }
    // suspend fun requestDataAsync2(): String {
    //     delay(1000L)
    //     return "data2"    
    // }
    
    // fun test() {
    //     // 1. 程序开始
    //     Log.d(TAG, "1 Thread.currentThread().name: " + Thread.currentThread().name)
    //     // 2. 启动一个协程, 并立即启动
    //     lifecycleScope.launch(Unconfined) { // Unconfined意思是在当前线程(主线程)运行协程
    //                          // 3. 本协程在主线程上直接开始执行了第一步
    //                          Log.d(TAG, "2 Thread.currentThread().name: " + Thread.currentThread().name)
    //                          /* 4. 本协程的第二步调用了一个suspend方法, 调用之后, 
    //                           * 本协程就放弃执行权, 遣散运行我的线程(主线程)请干别的去.
    //                           * 
    //                           * delay被调用的时候, 在内部创建了一个计时器, 并设了个callback.
    //                           * 1秒后计时器到期, 就会调用刚设置的callback.
    //                           * 在callback里面, 会调用系统的接口来恢复协程. 
    //                           * 协程在计时器线程上恢复执行了. (不是主线程, 跟Unconfined有关)
    //                           */
    //                          delay(1000L)  // 过1秒后, 计时器线程会resume协程
    //                          // 7. 计时器线程恢复了协程, 
    //                          Log.d(TAG, "4 Thread.currentThread().name: " + Thread.currentThread().name)
    //                          // Log.d("${Thread.currentThread().name}: 4")
    //     }
    //     // 5. 刚那个的协程不要我(主线程)干活了, 所以我继续之前的执行
    //     Log.d(TAG, "3 Thread.currentThread().name: " + Thread.currentThread().name)
    //     // 6. 我(主线程)睡2秒钟
    //     Thread.sleep(2000L)
    //     // 8. 我(主线程)睡完后继续执行
    //     Log.d(TAG, "5 Thread.currentThread().name: " + Thread.currentThread().name)
    // }

    // suspend fun test() {
    //     Log.d(TAG, "bef")
    //     suspendCoroutine<Unit> {
    //         continuation ->
    //         continuation.resume(Unit)
    //         Log.d(TAG, "bef two")
    //     }
    //     Log.d(TAG, "aft")
    // }

    // // lifecycleScope.launch { + suspend fun test() { 这样可以运行， .zip()
    // fun test() = runBlocking<Unit> {
    //     // 在内部使用launch也可以的
    //     launch {
    //         val nums = (1..5).asFlow().onEach {
    //             delay(300)
    //         } // 发射数字 1.;3，间隔 300 毫秒
    //         val strs = flowOf("one", "two", "three").onEach {
    //             delay(400)
    //         } // 每 400 毫秒发射一次字符串
    //         val startTime = System.currentTimeMillis() // 记录开始的时间
    //         nums.zip(strs) { a, b -> "$a ！！ $b" } // 使用“zip”组合单个字符串:
    //             .collect { value -> // 收集并打印
    //                        Log.d(TAG, "$value at ${System.currentTimeMillis() - startTime} ms from start")
    //             } 
    //         // }
    //     }
    // }
}
//     // 这里还没有想透
//     @ObsoleteCoroutinesApi
//     @UseExperimental(InternalCoroutinesApi::class)
//     suspend fun selectMulti(a: Channel<Int>, b: Channel<Int>): String = select<String> {
//         b.onReceive {
//             "b $it" // 优先执行第一个, 不是函数原因, 而是顺序
//         }
//         a.onReceive {
//             "a $it" // 优先执行第一个, 不是函数原因, 而是顺序
//         }
// //        b.onReceiveOrClosed {
// //            "b $it"
// //        }
//         a.onSend(23) {
//             Log.d(TAG, "onSend() a")
//             "发送 23"
//         }
//         b.onSend(27) {
//             Log.d(TAG, "onSend() b")
//             "发送 27"
//         }
//     }
//     fun test() = runBlocking<Unit> {
//         val a = Channel<Int>(1) // 缓冲数量, 避免发送数据时阻塞
//         val b = Channel<Int>(1)
//         launch {
//             a.send(23)
//             b.send(27)
//             val s = selectMulti(a, b)
//             Log.d(TAG, "结果 = $s")
//         }
//     }

    // fun test() = runBlocking<Unit> {
    //     CoroutineScope(coroutineContext).launch {
    //         launch(SupervisorJob(coroutineContext[Job]) + CoroutineExceptionHandler { _, _ ->  }) {
    //             throw NullPointerException()
    //         }
    //         delay(500)
    //         Log.d(TAG, "( Process.kt:13 )    ")
    //     }
    //     Log.d(TAG, "( Process.kt:16 )    finish")
    // }

    // // 全局协程作用域也存在嵌套子父级关系, 故异常可能也会依次抛出多个异常
    // fun test() = runBlocking {
    //     val handler = CoroutineExceptionHandler { _, exception ->
    //         // 第三, 这里的异常是第一个被抛出的异常对象
    //         Log.d(TAG, "捕捉的异常: $exception 和被嵌套的异常: ${exception.suppressed.contentToString()}")
    //     }
    //     val job = GlobalScope.launch(handler) {
    //         launch {
    //             try {
    //                 delay(Long.MAX_VALUE)
    //             } finally { // 当父协程被取消时其所有子协程都被取消, finally被取消之前或者完成任务之后一定会执行
    //                 throw ArithmeticException() // 第二, 再次抛出异常, 异常被聚合
    //             }
    //         }
    //         launch {
    //             delay(100)
    //             throw IOException() // 第一, 这里抛出异常将导致父协程被取消
    //         }
    //         delay(Long.MAX_VALUE)
    //     }
    //     job.join() // 避免GlobalScope作用域没有执行完毕JVM虚拟机就退出
    // }
//}
            //     // main()
            //     // runBloTest()
            //     // asyncTest2()
            //     // dispatchersTest()
            //     // jobTest()
            // test()

            // lifecycleScope.launch {
            //     //1.创建一个Flow
            //     flow<Int> {
            //         for (i in 1..3) {
            //             delay(200)
            //             //2.发出数据
            //             emit(i)
            //         }
            //         //3.从流中收集值
            //     }.collect {
            //         Log.d(TAG, "收集: it: " + it)
            //     }
            // }

            // 冷数据流
            // val flow = flow {
            //     for (i in 1..3) {
            //         delay(200)
            //         emit(i)//从流中发出值
            //     }
            // }
            // lifecycleScope.launch {
            //     flow.collect {
            //         Log.d(TAG, "it: " + it)
            //     }
            // }

            // // 线程的切换
            // lifecycleScope.launch {
            //     //创建一个Flow<T>
            //     flow {
            //         for (i in 1..3) {
            //             delay(500)
            //             emit(i)//从流中发出值
            //         }
            //         // 通过flowOn()改变的是Flow函数内部发射数据时的线程，而在collect收集数据时会自动切回创建Flow时的线程
            //     }.flowOn(Dispatchers.IO)//将上面的数据发射操作放到 IO 线程中的协程
            //         .collect { value ->
            //                        // 具体数据的消费处理
            //                    Log.d(TAG, "value: " + value)
            //         }
            // }

            // lifecycleScope.launch {
            //     flow {
            //         // 做了2件事：发送一个数据，以及 抛出一个异常
            //         emit(10)//从流中发出值
            //         throw NullPointerException()//抛出空指针异常
            //     }.catch { e ->//捕获上游抛出的异常
            //               Log.d(TAG, "caught error: $e")
            //     }.collect {
            //         Log.d(TAG, "it: " + it)
            //     }
            // }

            // lifecycleScope.launch {
            //     flow {
            //         emit(10)
            //     }.onCompletion {//流操作完成回调
            //                     Log.d(TAG, "Flow 操作完成")
            //     }.collect {
            //         Log.d(TAG, "收集:$it")
            //     }
            // }

            // lifecycleScope.launch {
            //     //1.创建一个子协程
            //     val job = launch {
            //         //2.创建flow
            //         val intFlow = flow {
            //             (1..5).forEach {
            //                 delay(1000)
            //                 //3.发送数据
            //                 emit(it)
            //             }
            //         }
            //         //4.收集数据
            //         intFlow.collect {//收集
            //                          Log.d(TAG, "it: " + it)
            //         }
            //     }
            //     //5.在3.5秒后取消协程
            //      delay(3500) // 这里不能用wait(3500)
            //     Log.d(TAG, "cancelAndJoin() bef")
            //     job.cancelAndJoin()
            // }

            // lifecycleScope.launch {
            //     val time = measureTimeMillis {
            //         flow {
            //             for (i in 1..3) {
            //                 delay(300)//假设我们正在异步等待100毫秒
            //                 Log.d(TAG, "emit(i) bef: " + i)
            //                 emit(i)//发出下一个值
            //             }
            //         }.buffer()//缓存排放，不要等待, buffer()创建缓存，不要等待，运行更快
            //             .collect { value ->
            //                            delay(900)//假设我们处理了300毫秒
            //                        Log.d(TAG, "value: " + value)
            //             }
            //     }
            //     Log.d(TAG, "收集耗时：$time ms")
            // }

//             lifecycleScope.launch {
//                 val time = measureTimeMillis {
//                     flow {
//                         // 当数字1扔在处理时，数字2和数字3已经产生了，所以数字2被合并，只有最近的数字1(数字3)被交付给收集器
//                         for (i in 1..3) {
//                             delay(100)//假设我们正在异步等待100毫秒
//                             emit(i)//发出下一个值
//                         }
//                     }.conflate()//合并排放，而不是逐个处理
//                         .collect { value ->
//                                        delay(300)//假设我们处理了300毫秒
//                                    Log.d(TAG, "value: " + value)
//                         }
//                 }
//                 Log.d(TAG, "收集耗时：$time ms")
//             }
// // D/test MainActivity: value: 1
// // D/test MainActivity: value: 3
// // D/test MainActivity: 收集耗时：770 ms


            // lifecycleScope.launch {
            //     val time = measureTimeMillis {
            //         flow {
            //             for (i in 1..3) {
            //                 delay(100)//假设我们正在异步等待100毫秒
            //                 emit(i)//发出下一个值
            //             }
            //         }.collectLatest { value ->//取消并重新启动最新的值
            //                           Log.d(TAG, "收集的值：$value")
            //                           delay(300)//假设我们处理了300毫秒
            //                           Log.d(TAG, "完成：$value")
            //         }
            //     }
            //     Log.d(TAG, "收集耗时：$time ms")
            // }
            // // 由于collectLatest的代码需要300毫秒的时间，但是每100毫秒就会发出一个新值，所以我们看到代码块在每个值上运行，但只在最后一个值上完成。打印数据如下：
            // // [main] 收集的值：1
            // // [main] 收集的值：2
            // // [main] 收集的值：3
            // // [main] 完成：3
            // // [main] 收集耗时：679 ms

//         }
//     }
// }
    // suspend fun main() {
        //     try {
            //         coroutineScope {
                //             print("1")
                //             val job1 = launch {//第一个子协程
                //                                print("2")
                //                                throw  NullPointerException()//抛出空指针异常
                //             }
                //             val job2 = launch {//第二个子协程
                //                                delay(1000)
                //                                print("3")
                //             }
                //             try {//这里try…catch捕获CancellationException
                //                  job2.join()
                //                  Log.d("4")//等待第二个子协程完成：
                //             } catch (e: Exception) {
                    //                 print("5. $e")//捕获第二个协程的取消异常
                    //             }
                    //         }
                    //     } catch (e: Exception) {//捕获父协程的取消异常
                    //                             print("6. $e")
                    //     }

                    //     Thread.sleep(3000)//阻塞主线程3秒，以保持JVM存活，等待上面执行完成
                    // }

                    // fun test() {
                        //     val scope = CoroutineScope(Dispatchers.Main)
                        //     scope.launch {
                            //         try {
                                //             print("模拟抛出一个数组越界异常")
                                //             throw IndexOutOfBoundsException() //launch 抛出异常
                                //         } catch (e: Exception) {
                                    //             //处理异常
                                    //             print("这里处理抛出的异常")
                                    //         }
                                    //     }
                                    
                                    //     // // 这么着是run不通的：GlobalScope.launch ，。。。
                                    //     // val job = CoroutineScope.launch {
                                        //     //     try {
                                            //     //         //TODO
                                            //     //     } catch (e: CancellationException) {
                                                //     //         print("协程取消抛出异常")
                                                //     //     } finally {
                                                    //     //         withContext(NonCancellable) {
                                                        //     //             delay(100)//或者其他挂起函数
                                                        //     //             print("协程清理工作")
                                                        //     //         }
                                                        //     //     }
                                                        //     // }
                                                        //     // delay(500L)
                                                        //     // job.cancel()//取消协程

                                                        //     // 这么着是run不通的：GlobalScope.launch ，。。。
                                                        //     // val job = GlobalScope.launch {
                                                            //     //     try {
                                                                //     //         delay(500L)
                                                                //     //     } catch (e: CancellationException) {
                                                                    //     //         print("协程取消抛出异常:$e")
                                                                    //     //     } finally {
                                                                        //     //         print("协程清理工作")
                                                                        //     //     }
                                                                        //     // }
                                                                        //     // job.cancel()//取消协程
                                                                        // }

                                                                        // fun jobTest() = runBlocking {
                                                                            //     val startTime = System.currentTimeMillis()
                                                                            //     val job = launch(Dispatchers.Default) {
                                                                                //         var nextPrintTime = startTime
                                                                                //         var i = 0
                                                                                //         //打印前五条消息
                                                                                //         while (i < 7) {
                                                                                    //             //每秒钟打印两次消息
                                                                                    //             if (System.currentTimeMillis() >= nextPrintTime) {
                                                                                        //                 Log.d(TAG, "job: I'm sleeping ${i++} ...")
                                                                                        //                 nextPrintTime += 300
                                                                                        //             }
                                                                                        //         }
                                                                                        //     }
                                                                                        //     delay(1200)//延迟1.2s
                                                                                        //     Log.d(TAG, "等待1.2秒后")
                                                                                        //     job.cancel()
                                                                                        //     Log.d(TAG, "协程被取消")
                                                                                        // }

                                                                                        // fun dispatchersTest() {
                                                                                            //     //创建一个在主线程执行的协程作用域
                                                                                            //     val mainScope = MainScope()
                                                                                            //     mainScope.launch {
                                                                                                //         //在协程上下参数中指定调度器
                                                                                                //         launch(Dispatchers.Main) {
                                                                                                    //             Log.d(TAG, "主线程调度器")
                                                                                                    //         }
                                                                                                    //         launch(Dispatchers.Default) {
                                                                                                        //             Log.d(TAG, "默认调度器")
                                                                                                        //         }
                                                                                                        //         launch(Dispatchers.Unconfined) {
                                                                                                            //             Log.d(TAG, "任意调度器")
                                                                                                            //         }
                                                                                                            //         launch(Dispatchers.IO) {
                                                                                                                //             Log.d(TAG, "IO调度器")
                                                                                                                //         }
                                                                                                                //     }
                                                                                                                // }

                                                                                                                // fun jobTest() = runBlocking {
                                                                                                                    //     val startTime = System.currentTimeMillis()
                                                                                                                    //     Log.d(TAG, "bgn")
                                                                                                                    //     val job = launch(Dispatchers.Default){
                                                                                                                        //         var nextPrintTime = startTime
                                                                                                                        //         var i = 0
                                                                                                                        //         //当job是活跃状态继续执行
                                                                                                                        //         while (isActive) {
                                                                                                                            //             //每秒钟打印两次消息
                                                                                                                            //             if (System.currentTimeMillis() >= nextPrintTime) {
                                                                                                                                //                 Log.d(TAG, "job: I'm sleeping ${i++} ...")
                                                                                                                                //                 nextPrintTime += 300
                                                                                                                                //             }
                                                                                                                                //         }
                                                                                                                                //     }
                                                                                                                                //     delay(2700)//延迟1.2s
                                                                                                                                //     Log.d(TAG, "等待2.7秒后")
                                                                                                                                //     //        job.join()
                                                                                                                                //     //        job.cancel()
                                                                                                                                //     job.cancelAndJoin()//取消任务并等待任务完成
                                                                                                                                //     Log.d(TAG, "协程被取消并等待完成")
                                                                                                                                // }

                                                                                                                                // fun asyncTest2() {
                                                                                                                                    //     Log.d(TAG, "start")
                                                                                                                                    //     GlobalScope.launch {
                                                                                                                                        //         //计算执行时间
                                                                                                                                        //         val time = measureTimeMillis {
                                                                                                                                            //             val deferredOne: Deferred<Int> = async {
                                                                                                                                                //                 delay(2000)
                                                                                                                                                //                 Log.d(TAG, "asyncOne")
                                                                                                                                                //                 100//这里返回值为100
                                                                                                                                                //             }
                                                                                                                                                //             val deferredTwo: Deferred<Int> = async {
                                                                                                                                                    //                 delay(3000)
                                                                                                                                                    //                 Log.d(TAG, "asyncTwo")
                                                                                                                                                    //                 200//这里返回值为200
                                                                                                                                                    //             }
                                                                                                                                                    //             val deferredThr: Deferred<Int> = async {
                                                                                                                                                        //                 delay(4000)
                                                                                                                                                        //                 Log.d(TAG, "asyncThr")
                                                                                                                                                        //                 300//这里返回值为300
                                                                                                                                                        //             }

                                                                                                                                                        //             //等待所有需要结果的协程完成获取执行结果
                                                                                                                                                        //             val result = deferredOne.await() + deferredTwo.await() + deferredThr.await()
                                                                                                                                                        //             Log.d(TAG, "result: " + result)
                                                                                                                                                        //         }
                                                                                                                                                        //         Log.d(TAG, "耗时 $time ms")
                                                                                                                                                        //     }
                                                                                                                                                        //     Log.d(TAG, "end")
                                                                                                                                                        // }
                                                                                                                                                        
                                                                                                                                                        // //获取返回值
                                                                                                                                                        // fun asyncTest1() {
                                                                                                                                                            //     Log.d(TAG, "start")
                                                                                                                                                            //     GlobalScope.launch {
                                                                                                                                                                //         val deferred: Deferred<String> = async {
                                                                                                                                                                    //             //协程将线程的执行权交出去，该线程继续干它要干的事情，到时间后会恢复至此继续向下执行
                                                                                                                                                                    //             delay(2000)//2秒无阻塞延迟（默认单位为毫秒）
                                                                                                                                                                    //             Log.d(TAG, "asyncOne()")
                                                                                                                                                                    //             "HelloWord"//这里返回值为HelloWord
                                                                                                                                                                    //         }

                                                                                                                                                                    //         //等待async执行完成获取返回值,此处并不会阻塞线程,而是挂起,将线程的执行权交出去
                                                                                                                                                                    //         //等到async的协程体执行完毕后,会恢复协程继续往下执行
                                                                                                                                                                    //         //await() 不能在协程之外调用，因为它需要挂起直到计算完成，而且只有协程可以以非阻塞的方式挂起。所以把它放到协程中
                                                                                                                                                                    //         val result = deferred.await()
                                                                                                                                                                    //         Log.d(TAG, "result: " + result)
                                                                                                                                                                    //         Log.d(TAG, "result == $result") 
                                                                                                                                                                    //     }
                                                                                                                                                                    //     Log.d(TAG, "end()")
                                                                                                                                                                    // }
                                                                                                                                                                    
                                                                                                                                                                    // // 常用在主程序？以及测试时用，会阻塞
                                                                                                                                                                    // fun runBloTest() {
                                                                                                                                                                        //     Log.d(TAG, "runBloTest()") 
                                                                                                                                                                        //     //context上下文使用默认值,阻塞当前线程，直到代码块中的逻辑完成
                                                                                                                                                                        //     runBlocking {
                                                                                                                                                                            //         //这里是协程体
                                                                                                                                                                            //         delay(1000)//挂起函数，延迟1000毫秒
                                                                                                                                                                            //         Log.d(TAG, "runBlocking()") 
                                                                                                                                                                            //     }
                                                                                                                                                                            //     Log.d(TAG, "end()")     
                                                                                                                                                                            // }

                                                                                                                                                                            // fun foo(): Flow<Int> = flow {
                                                                                                                                                                                //     // flow builder
                                                                                                                                                                                //     for (i in 1..3) {
                                                                                                                                                                                    //         delay(1000) // pretend we are doing something useful here
                                                                                                                                                                                    //         emit(i) // emit next value
                                                                                                                                                                                    //     }
                                                                                                                                                                                    // }
                                                                                                                                                                                    // @OptIn(InternalCoroutinesApi::class)
                                                                                                                                                                                    // fun main() = runBlocking<Unit> {
                                                                                                                                                                                        //     // Launch a concurrent coroutine to check if the main thread is blocked
                                                                                                                                                                                        //     launch {
                                                                                                                                                                                            //         for (k in 1..3) {
                                                                                                                                                                                                //             Log.d("I'm not blocked $k")
                                                                                                                                                                                                //             delay(1000)
                                                                                                                                                                                                //         }
                                                                                                                                                                                                //     }
                                                                                                                                                                                                //     // Collect the flow
                                                                                                                                                                                                //     foo().collect { value ->
                                                                                                                                                                                                    //                         Log.d(TAG, "value: " + value)
                                                                                                                                                                                                //                     // Log.d(value)
                                                                                                                                                                                                //     }
                                                                                                                                                                                                // }
                                                                                                                                                                                                //}

                                                                                                                                                                                                //     @Preview
                                                                                                                                                                                                //     @Composable
                                                                                                                                                                                                //     fun MyScreen() {
                                                                                                                                                                                                    //         Box {
                                                                                                                                                                                                        //             FloatingActionButton(
                                                                                                                                                                                                            //                 modifier = Modifier
                                                                                                                                                                                                            //                     .align(Alignment.BottomEnd)
                                                                                                                                                                                                            //                     .padding(16.dp) // normal 16dp of padding for FABs
                                                                                                                                                                                                            //                     .navigationBarsPadding(), // Move it out from under the nav bar
                                                                                                                                                                                                            //                 onClick = { }
                                                                                                                                                                                                            //             ) {
                                                                                                                                                                                                                // //                Icon( /* ... */)
                                                                                                                                                                                                                //             }
                                                                                                                                                                                                                //         }
                                                                                                                                                                                                                //     }
                                                                                                                                                                                                                //     @Preview
                                                                                                                                                                                                                //     @Composable
                                                                                                                                                                                                                //     fun recyclerview() {
                                                                                                                                                                                                                    //         val itemsList = arrayListOf<String>()
                                                                                                                                                                                                                    //         for (index in 1..100) {
                                                                                                                                                                                                                        //             itemsList.add("hello vivo-${index}")
                                                                                                                                                                                                                        //         }
                                                                                                                                                                                                                        //         // 横向你就lazyRow
                                                                                                                                                                                                                        //         LazyColumn {
                                                                                                                                                                                                                            //             // item {
                                                                                                                                                                                                                                //                 //     Image(painterResource(R.mipmap.tly2), contentDescription = "佟丽娅", Modifier.size(100.dp), contentScale = ContentScale.Crop)
                                                                                                                                                                                                                                //                 // }
                                                                                                                                                                                                                                //                items(itemsList) { 
                                                                                                                                                                                                                                //                                   Box(
                                                                                                                                                                                                                                    //                                        Modifier
                                                                                                                                                                                                                                    //                                            .fillMaxWidth()
                                                                                                                                                                                                                                    //                                            .height(30.dp)
                                                                                                                                                                                                                                    //                                    ) {
                                                                                                                                                                                                                                        //                                        Text(text = it, fontSize = 15.sp, color = Color.Red)
                                                                                                                                                                                                                                        //                                    }
                                                                                                                                                                                                                                        //                 }
                                                                                                                                                                                                                                        //         }
                                                                                                                                                                                                                                        //     }
                                                                                                                                                                                                                                        // }

                                                                                                                                                                                                                                        // @Preview
                                                                                                                                                                                                                                        // @Composable
                                                                                                                                                                                                                                        // fun CusContainer() {
                                                                                                                                                                                                                                            //     Container(modifier = Height(300.dp) wraps Expanded) {
                                                                                                                                                                                                                                                //         Clip(shape = androidx.ui.foundation.shape.corner.RoundedCornerShape(4.dp)) {
                                                                                                                                                                                                                                                    //             DrawImage(+imageResource(R.drawable.img_0))
                                                                                                                                                                                                                                                    //         }
                                                                                                                                                                                                                                                    //     }
                                                                                                                                                                                                                                                    // }
                                                                                                                                                                                                                                                    
                                                                                                                                                                                                                                                    //     @Composable // 不知道这种定义的函数该如何调用
                                                                                                                                                                                                                                                    //  fun CustomTheme(
                                                                                                                                                                                                                                                        //     darkTheme: Boolean = isSystemInDarkTheme(),  // 默认根据系统来设置是否为暗夜模式
                                                                                                                                                                                                                                                        //     content: @Composable () -> Unit // 被传入的 Composable 函数
                                                                                                                                                                                                                                                        // ){
                                                                                                                                                                                                                                                            //     val colors = if (darkTheme) {
                                                                                                                                                                                                                                                                //         DarkColors
                                                                                                                                                                                                                                                                //     } else {
                                                                                                                                                                                                                                                                    //         LightColors
                                                                                                                                                                                                                                                                    //     }

                                                                                                                                                                                                                                                                    //     MaterialTheme(colors = colors) { // 将设置好的色值传入
                                                                                                                                                                                                                                                                    //         content()
                                                                                                                                                                                                                                                                    //     }
                                                                                                                                                                                                                                                                    // }

                                                                                                                                                                                                                                                                    //  @SuppressLint("ConflictingOnColor")
                                                                                                                                                                                                                                                                    //  private val DarkColors = darkColors( // 暗夜模式下的色值
                                                                                                                                                                                                                                                                    //     primary = Color.Gray,
                                                                                                                                                                                                                                                                    //     primaryVariant = Color.Blue,
                                                                                                                                                                                                                                                                    //     onPrimary = Color.Black,
                                                                                                                                                                                                                                                                    //     secondary = Color.Gray,
                                                                                                                                                                                                                                                                    //     onSecondary = Color.Black,
                                                                                                                                                                                                                                                                    //     error = Color.Gray
                                                                                                                                                                                                                                                                    // )

                                                                                                                                                                                                                                                                    //  private val LightColors = lightColors( // 白天模式下的色值
                                                                                                                                                                                                                                                                    //     primary = Color.Blue,
                                                                                                                                                                                                                                                                    //     primaryVariant = Color.Blue,
                                                                                                                                                                                                                                                                    //     onPrimary = Color.White,
                                                                                                                                                                                                                                                                    //     secondary = Color.Blue,
                                                                                                                                                                                                                                                                    //     secondaryVariant = Color.Blue,
                                                                                                                                                                                                                                                                    //     onSecondary = Color.White,
                                                                                                                                                                                                                                                                    //     error = Color.DarkGray
                                                                                                                                                                                                                                                                    // )
                                                                                                                                                                                                                                                                    //     @Preview
                                                                                                                                                                                                                                                                    //     @Composable
                                                                                                                                                                                                                                                                    //     fun ClickedText() {
                                                                                                                                                                                                                                                                        //         val modifier = Modifier.clickable(
                                                                                                                                                                                                                                                                            //             onClick = {
                                                                                                                                                                                                                                                                                //                 Log.d("Andoter", this.javaClass.name)

                                                                                                                                                                                                                                                                                //                 Toast.makeText(this@MainActivity, "Button 点击", Toast.LENGTH_SHORT).show()
                                                                                                                                                                                                                                                                                //         })
                                                                                                                                                                                                                                                                                //         Text(text = "Hello Compose!", modifier = modifier.padding(10.dp))
                                                                                                                                                                                                                                                                                //     }    
                                                                                                                                                                                                                                                                                // @Preview
                                                                                                                                                                                                                                                                                // @Composable
                                                                                                                                                                                                                                                                                // fun ProductList() {
                                                                                                                                                                                                                                                                                    //     ScrollableColumn(Modifier.fillMaxSize()) {
                                                                                                                                                                                                                                                                                        //         listOf("Ant", "Andoter", "小伟").forEach { value ->
                                                                                                                                                                                                                                                                                            //                                                        ProductDetailView(value)
                                                                                                                                                                                                                                                                                        //         }
                                                                                                                                                                                                                                                                                        //     }
                                                                                                                                                                                                                                                                                        // }
                                                                                                                                                                                                                                                                                        // @Composable
                                                                                                                                                                                                                                                                                        // fun ProductDetailView(text: String) {
                                                                                                                                                                                                                                                                                            //     val image = imageResource(id = R.drawable.header)
                                                                                                                                                                                                                                                                                            //     Column(modifier = Modifier.padding(16.dp)) {
                                                                                                                                                                                                                                                                                                //         val imageModifier = Modifier
                                                                                                                                                                                                                                                                                                //             .preferredHeight(180.dp)
                                                                                                                                                                                                                                                                                                //             .clip(shape = RoundedCornerShape(5.dp))
                                                                                                                                                                                                                                                                                                //             .fillMaxWidth()
                                                                                                                                                                                                                                                                                                //             .clickable(onClick = {
                                                                                                                                                                                                                                                                                                    //                            Log.d("Ant", "click");
                                                                                                                                                                                                                                                                                                    //             })
                                                                                                                                                                                                                                                                                                    //         Image(image, modifier = imageModifier, contentScale = ContentScale.Crop)
                                                                                                                                                                                                                                                                                                    //         Spacer(modifier = Modifier.preferredHeight(16.dp))
                                                                                                                                                                                                                                                                                                    //         Text("Hello Compose!")
                                                                                                                                                                                                                                                                                                    //     }
                                                                                                                                                                                                                                                                                                    // }

                                                                                                                                                                                                                                                                                                    
                                                                                                                                                                                                                                                                                                    // // 应用栏高度
                                                                                                                                                                                                                                                                                                    // private val toolBarHeight = 56.dp
                                                                                                                                                                                                                                                                                                    // // 导航图标大小
                                                                                                                                                                                                                                                                                                    // private val navigationIconSize = 50.dp
                                                                                                                                                                                                                                                                                                    // @Preview
                                                                                                                                                                                                                                                                                                    // @Composable
                                                                                                                                                                                                                                                                                                    // fun CusButton () {
                                                                                                                                                                                                                                                                                                        //     Scaffold(
                                                                                                                                                                                                                                                                                                            //         floatingActionButton = {
                                                                                                                                                                                                                                                                                                                //             FloatingActionButton(onClick = { /* ... */ }) {
                                                                                                                                                                                                                                                                                                                    //                 /* FAB content */
                                                                                                                                                                                                                                                                                                                    //             }
                                                                                                                                                                                                                                                                                                                    //         },
                                                                                                                                                                                                                                                                                                                    //         isFloatingActionButtonDocked = true,
                                                                                                                                                                                                                                                                                                                    //         bottomBar = {
                                                                                                                                                                                                                                                                                                                        //             BottomAppBar(
                                                                                                                                                                                                                                                                                                                            //                 // Defaults to null, that is, No cutout
                                                                                                                                                                                                                                                                                                                            //                 cutoutShape = MaterialTheme.shapes.small.copy(
                                                                                                                                                                                                                                                                                                                                //                     CornerSize(percent = 50)
                                                                                                                                                                                                                                                                                                                                //                 )
                                                                                                                                                                                                                                                                                                                                //             ) {
                                                                                                                                                                                                                                                                                                                                    //                 /* Bottom app bar content */
                                                                                                                                                                                                                                                                                                                                    //             }
                                                                                                                                                                                                                                                                                                                                    //         }
                                                                                                                                                                                                                                                                                                                                    //     ) {
                                                                                                                                                                                                                                                                                                                                        //         // Screen content: 要设置把它们怎么对齐一下
                                                                                                                                                                                                                                                                                                                                        //         Column() {
                                                                                                                                                                                                                                                                                                                                            //             Button(
                                                                                                                                                                                                                                                                                                                                                //                 onClick = { /* ... */ },
                                                                                                                                                                                                                                                                                                                                                //                 // Uses ButtonDefaults.ContentPadding by default
                                                                                                                                                                                                                                                                                                                                                //                 contentPadding = PaddingValues(
                                                                                                                                                                                                                                                                                                                                                    //                     start = 20.dp,
                                                                                                                                                                                                                                                                                                                                                    //                     top = 12.dp,
                                                                                                                                                                                                                                                                                                                                                    //                     end = 20.dp,
                                                                                                                                                                                                                                                                                                                                                    //                     bottom = 12.dp
                                                                                                                                                                                                                                                                                                                                                    //                 )
                                                                                                                                                                                                                                                                                                                                                    //             ) {
                                                                                                                                                                                                                                                                                                                                                        //                 // Inner content including an icon and a text label
                                                                                                                                                                                                                                                                                                                                                        //                 Icon(
                                                                                                                                                                                                                                                                                                                                                            //                     Icons.Filled.Favorite,
                                                                                                                                                                                                                                                                                                                                                            //                     contentDescription = "Favorite",
                                                                                                                                                                                                                                                                                                                                                            //                     modifier = Modifier.size(ButtonDefaults.IconSize)
                                                                                                                                                                                                                                                                                                                                                            //                 )
                                                                                                                                                                                                                                                                                                                                                            //                 Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                                                                                                                                                                                                                                                                                                                                            //                 Text("Like")
                                                                                                                                                                                                                                                                                                                                                            //             }
                                                                                                                                                                                                                                                                                                                                                            //             Spacer(Modifier.size(15.dp))
                                                                                                                                                                                                                                                                                                                                                            //             ExtendedFloatingActionButton(
                                                                                                                                                                                                                                                                                                                                                                //                 onClick = { /* ... */ },
                                                                                                                                                                                                                                                                                                                                                                //                 icon = {
                                                                                                                                                                                                                                                                                                                                                                    //                     Icon(
                                                                                                                                                                                                                                                                                                                                                                        //                         Icons.Filled.Favorite,
                                                                                                                                                                                                                                                                                                                                                                        //                         contentDescription = "Favorite"
                                                                                                                                                                                                                                                                                                                                                                        //                     )
                                                                                                                                                                                                                                                                                                                                                                        //                 },
                                                                                                                                                                                                                                                                                                                                                                        //                 text = { Text("Like") }
                                                                                                                                                                                                                                                                                                                                                                        //             )
                                                                                                                                                                                                                                                                                                                                                                        //         }
                                                                                                                                                                                                                                                                                                                                                                        //     }
                                                                                                                                                                                                                                                                                                                                                                        // }
                                                                                                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                        // @Composable
                                                                                                                                                                                                                                                                                                                                                                        // fun ScrollableAppBar(
                                                                                                                                                                                                                                                                                                                                                                            //     modifier: Modifier = Modifier,
                                                                                                                                                                                                                                                                                                                                                                            //     title: String = stringResource(id = R.string.app_name), //默认为应用名
                                                                                                                                                                                                                                                                                                                                                                            //     navigationIcon: @Composable (() -> Unit) =
                                                                                                                                                                                                                                                                                                                                                                                //         { Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "ArrowBack", tint = Color.White) }, //默认为返回键
                                                                                                                                                                                                                                                                                                                                                                            //     @DrawableRes backgroundImageId: Int, // 背景图片
                                                                                                                                                                                                                                                                                                                                                                            //     background: Color = MaterialTheme.colors.primary,
                                                                                                                                                                                                                                                                                                                                                                            //     scrollableAppBarHeight: Dp, //ScrollableAppBar高度
                                                                                                                                                                                                                                                                                                                                                                            //     toolbarOffsetHeightPx: MutableState<Float> //向上偏移量
                                                                                                                                                                                                                                                                                                                                                                            // ) {
                                                                                                                                                                                                                                                                                                                                                                                //     // 应用栏最大向上偏移量
                                                                                                                                                                                                                                                                                                                                                                                //     val maxOffsetHeightPx = with(LocalDensity.current) { scrollableAppBarHeight.roundToPx().toFloat() - toolBarHeight.roundToPx().toFloat() }
                                                                                                                                                                                                                                                                                                                                                                                //     // Title 偏移量参考值
                                                                                                                                                                                                                                                                                                                                                                                //     val titleOffsetWidthReferenceValue = with(LocalDensity.current) { navigationIconSize.roundToPx().toFloat() }
                                                                                                                                                                                                                                                                                                                                                                                //     Box(modifier = Modifier
                                                                                                                                                                                                                                                                                                                                                                                //             .height(scrollableAppBarHeight)
                                                                                                                                                                                                                                                                                                                                                                                //             .offset {
                                                                                                                                                                                                                                                                                                                                                                                    //                 IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) //设置偏移量
                                                                                                                                                                                                                                                                                                                                                                                    //             }
                                                                                                                                                                                                                                                                                                                                                                                    //             .fillMaxWidth()
                                                                                                                                                                                                                                                                                                                                                                                    //     ) {
                                                                                                                                                                                                                                                                                                                                                                                        //         Image(painter = painterResource(id = backgroundImageId), contentDescription = "background", contentScale = ContentScale.FillBounds)
                                                                                                                                                                                                                                                                                                                                                                                        //         // 自定义应用栏
                                                                                                                                                                                                                                                                                                                                                                                        //         Row(
                                                                                                                                                                                                                                                                                                                                                                                            //             modifier = modifier
                                                                                                                                                                                                                                                                                                                                                                                            //                 .offset {
                                                                                                                                                                                                                                                                                                                                                                                                //                     IntOffset(
                                                                                                                                                                                                                                                                                                                                                                                                    //                         x = 0,
                                                                                                                                                                                                                                                                                                                                                                                                    //                         y = -toolbarOffsetHeightPx.value.roundToInt() //保证应用栏是始终不动的
                                                                                                                                                                                                                                                                                                                                                                                                    //                     )
                                                                                                                                                                                                                                                                                                                                                                                                    //                 }
                                                                                                                                                                                                                                                                                                                                                                                                    //                 .height(toolBarHeight)
                                                                                                                                                                                                                                                                                                                                                                                                    //                 .fillMaxWidth(),
                                                                                                                                                                                                                                                                                                                                                                                                    //             verticalAlignment = Alignment.CenterVertically
                                                                                                                                                                                                                                                                                                                                                                                                    //         ) {
                                                                                                                                                                                                                                                                                                                                                                                                        //             // 导航图标
                                                                                                                                                                                                                                                                                                                                                                                                        //             Box(modifier = Modifier.size(navigationIconSize),contentAlignment = Alignment.Center) {
                                                                                                                                                                                                                                                                                                                                                                                                            //                 navigationIcon()
                                                                                                                                                                                                                                                                                                                                                                                                            //             }
                                                                                                                                                                                                                                                                                                                                                                                                            //         }
                                                                                                                                                                                                                                                                                                                                                                                                            //         Box(
                                                                                                                                                                                                                                                                                                                                                                                                                //             modifier = Modifier
                                                                                                                                                                                                                                                                                                                                                                                                                //                 .height(toolBarHeight) //和ToolBar同高
                                                                                                                                                                                                                                                                                                                                                                                                                //                 .fillMaxWidth()
                                                                                                                                                                                                                                                                                                                                                                                                                //                 .align(Alignment.BottomStart)
                                                                                                                                                                                                                                                                                                                                                                                                                //                 .offset {
                                                                                                                                                                                                                                                                                                                                                                                                                    //                     IntOffset(
                                                                                                                                                                                                                                                                                                                                                                                                                        //                         x = -((toolbarOffsetHeightPx.value / maxOffsetHeightPx) * titleOffsetWidthReferenceValue).roundToInt(),
                                                                                                                                                                                                                                                                                                                                                                                                                        //                         y = 0
                                                                                                                                                                                                                                                                                                                                                                                                                        //                     )
                                                                                                                                                                                                                                                                                                                                                                                                                        //                 },
                                                                                                                                                                                                                                                                                                                                                                                                                        //             contentAlignment = Alignment.CenterStart
                                                                                                                                                                                                                                                                                                                                                                                                                        //         ) {
                                                                                                                                                                                                                                                                                                                                                                                                                            //             Text(text = title,color = Color.White,modifier = Modifier.padding(start = 20.dp),fontSize = 20.sp)
                                                                                                                                                                                                                                                                                                                                                                                                                            //         }
                                                                                                                                                                                                                                                                                                                                                                                                                            //     }
                                                                                                                                                                                                                                                                                                                                                                                                                            // }

                                                                                                                                                                                                                                                                                                                                                                                                                            // // @Preview
                                                                                                                                                                                                                                                                                                                                                                                                                            // @Composable
                                                                                                                                                                                                                                                                                                                                                                                                                            // fun One(modifier: Modifier) {
                                                                                                                                                                                                                                                                                                                                                                                                                                //     Text(modifier = modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                //              .fillMaxSize()
                                                                                                                                                                                                                                                                                                                                                                                                                                //              .padding(top = 100.dp), text = "One", color = Color.DarkGray)
                                                                                                                                                                                                                                                                                                                                                                                                                                // }
                                                                                                                                                                                                                                                                                                                                                                                                                                // @Composable
                                                                                                                                                                                                                                                                                                                                                                                                                                // fun Two(modifier: Modifier) {
                                                                                                                                                                                                                                                                                                                                                                                                                                    //     Text(modifier = modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                    //              .fillMaxSize()
                                                                                                                                                                                                                                                                                                                                                                                                                                    //              .padding(top = 100.dp), text = "Two", color = Color.DarkGray)
                                                                                                                                                                                                                                                                                                                                                                                                                                    // }
                                                                                                                                                                                                                                                                                                                                                                                                                                    // @Composable
                                                                                                                                                                                                                                                                                                                                                                                                                                    // fun Three(modifier: Modifier) {
                                                                                                                                                                                                                                                                                                                                                                                                                                        //     Text(modifier = modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                        //              .fillMaxSize()
                                                                                                                                                                                                                                                                                                                                                                                                                                        //              .padding(top = 100.dp), text = "Three", color = Color.DarkGray)
                                                                                                                                                                                                                                                                                                                                                                                                                                        // }
                                                                                                                                                                                                                                                                                                                                                                                                                                        // @Composable
                                                                                                                                                                                                                                                                                                                                                                                                                                        // fun Four(modifier: Modifier) {
                                                                                                                                                                                                                                                                                                                                                                                                                                            //     Text(modifier = modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                            //              .fillMaxSize()
                                                                                                                                                                                                                                                                                                                                                                                                                                            //              .padding(top = 100.dp), text = "Four", color = Color.DarkGray)
                                                                                                                                                                                                                                                                                                                                                                                                                                            // }

                                                                                                                                                                                                                                                                                                                                                                                                                                            // fun marginOrPadding() {
                                                                                                                                                                                                                                                                                                                                                                                                                                                //     Column {
                                                                                                                                                                                                                                                                                                                                                                                                                                                    //         Box(
                                                                                                                                                                                                                                                                                                                                                                                                                                                        //             Modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                                        //                 .background(Color.Black)
                                                                                                                                                                                                                                                                                                                                                                                                                                                        //                 .size(100.dp)
                                                                                                                                                                                                                                                                                                                                                                                                                                                        //         ) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                            //             Text(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                //                 text = "Margin", fontSize = 15.sp, color = Color.Red,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                //                 modifier = Modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                                                //                     .padding(10.dp)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                //                     .background(Color.White)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                //             )
                                                                                                                                                                                                                                                                                                                                                                                                                                                                //         }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                //         Box(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //             Modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //                 .background(Color.Blue)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //                 .size(100.dp)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //         ) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //             Text(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //                 text = "Padding", fontSize = 15.sp, color = Color.Red,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //                 modifier = Modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //                     .background(Color.White)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //                     .padding(20.dp)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //             )
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //         }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //     }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            // }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            // @OptIn(ExperimentalPagerApi::class)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            // fun viewPager() {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //     val pagerState = rememberPagerState()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //     HorizontalPager(state = pagerState, count = 10) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //         Text(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //             text = "Page: $it", modifier = Modifier.fillMaxWidth()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //         )
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //     }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        // }

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //     fun scrollView() {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //         Column(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //             Modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //                 .fillMaxWidth()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // //                .height(300.dp) // 这里的height不知道是什么原因，有问题
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //                 .verticalScroll(rememberScrollState())
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //         ) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //             Box(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //                 Modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //                     .fillMaxWidth()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //                     .background(Color.Black)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //                     // .height(150.dp)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //             ) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //                 Text(text = "1", fontSize = 15.sp, color = Color.Red, modifier = Modifier.align(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //                     Alignment.Center))
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //             }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //             Box(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //                 Modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //                     .fillMaxWidth()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //                     .background(Color.White)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //                     // .height(150.dp)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //             ) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //                 Text(text = "2", fontSize = 15.sp, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //             }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //             Box(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //                 Modifier
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //                     .fillMaxWidth()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //                     .background(Color.Blue)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //                     // .height(150.dp)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //             ) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //                 Text(text = "3", fontSize = 15.sp, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //             }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //         }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //     }

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // @Preview
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // @Composable
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // fun linearLayout() {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //     Column {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //         Column {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //             Text(text = "hello vivo ", fontSize = 15.sp, color = Color.Green)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //             Text(text = "hello apple", fontSize = 15.sp, color = Color.Green)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //         }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            //         Row() {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //             Text(text = "hello xiaomi ", fontSize = 15.sp, color = Color.Red)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //             Text(text = "hello 华为", fontSize = 15.sp, color = Color.Red)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //         }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //     }

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // }

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // @Preview
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // @Composable
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // fun DefaultPreview() {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    //         MaterialTheme {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //                 Greeting("NYC Jetpack Compose")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //             }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        // }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        //}
