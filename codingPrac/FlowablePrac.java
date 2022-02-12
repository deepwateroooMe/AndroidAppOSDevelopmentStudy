package com.me.jv.ui.activities.tests;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.me.jv.viewmodels.LoginViewModel;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@AndroidEntryPoint
public class FlowablePrac extends AppCompatActivity {
    private final String TAG = "test FlowablePrac";
   
    @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //被观察者在主线程中，每1ms发送一个事件
        Observable.interval(1, TimeUnit.MILLISECONDS) // 这个 rxjava 1中的例子运行得不是很好， bug
                .observeOn(Schedulers.newThread()) // 指定发送事件的线程
            //观察者每1s才处理一个事件
            .subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "---->" + aLong);
                }
            });

        // 问题的产生：
        // Rxjava背压：被观察者发送事件的速度大于观察者接收事件的速度时，观察者内会创建一个无限制大少的缓冲池存储未接收的事件，因此当存储的事件越来越多时就会导致OOM的出现。
        Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                    int i = 0;
                    while(true){
                        Thread.sleep(500);
                        i++;
                        e.onNext(i);
                        Log.i(TAG,"每500ms发送一次数据："+i);
                    }
                }
            }).subscribeOn(AndroidSchedulers.mainThread()) // 指定观察者 运行在主线程执行
            .observeOn(Schedulers.newThread())    // 指定被观察者存在独立的线程执行
            .subscribe(new Consumer<Integer>() {
                    @Override
                        public void accept(Integer integer) throws Exception {
                        Thread.sleep(5000);
                        Log.e(TAG,"每5000m接收一次数据："+integer);
                    }
                });

        // 从Flowable源码查看，缓存池默认大少为：128
        // 当被观察者发送事件大于128时，观察者抛出异常并终止接收事件，但不会影响被观察者继续发送事件。
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    for(int j = 0; j <= 150;j++){
                        e.onNext(j);
                        Log.i(TAG," 发送数据："+j);
                        try{
                            Thread.sleep(50);
                        }catch (Exception ex){
                        }
                    }
                }
            }, BackpressureStrategy.ERROR)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new Subscriber<Integer>() {
                    @Override
                        public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE); // 观察者设置接收事件的数量,如果不设置接收不到事件
                    }
                    @Override
                        public void onNext(Integer integer) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext : "+(integer));
                    }
                    @Override
                        public void onError(Throwable t) {
                        Log.e(TAG,"onError : "+t.toString());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG,"onComplete");
                    }
                });

        // 与Observable一样存在背压问题，但是接收性能比Observable低，因为BUFFER类型通过BufferAsyncEmitter添加了额外的逻辑处理，再发送至观察者。
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    for(int j = 0; j <= 150;j++){
                        e.onNext(j);
                        Log.i(TAG," 发送数据："+j);
                        try{
                            Thread.sleep(50);
                        }catch (Exception ex){
                        }
                    }
                }
            }, BackpressureStrategy.BUFFER)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new Subscriber<Integer>() {
                    @Override
                        public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE); // 观察者设置接收事件的数量,如果不设置接收不到事件
                    }
                    @Override
                        public void onNext(Integer integer) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext : "+(integer));
                    }
                    @Override
                        public void onError(Throwable t) {
                        Log.e(TAG,"onError : "+t.toString());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG,"onComplete");
                    }
                });

        // DROP: 总结 ：每当观察者接收128事件之后，就会丢弃部分事件。
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    for(int j = 0; j <= 320;j++){
                        e.onNext(j);
                        Log.i(TAG," 发送数据："+j);
                        try{
                            Thread.sleep(50);
                        }catch (Exception ex){
                        }
                    }
                }
            }, BackpressureStrategy.DROP)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new Subscriber<Integer>() {
                    @Override
                        public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE); // 观察者设置接收事件的数量,如果不设置接收不到事件
                    }
                    @Override
                        public void onNext(Integer integer) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext : "+(integer));
                    }
                    @Override
                        public void onError(Throwable t) {
                        Log.e(TAG,"onError : "+t.toString());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG,"onComplete");
                    }
                });

        // LATEST与DROP使用效果一样，但LATEST会保证能接收最后一个事件，而DROP则不会保证。
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    for(int j = 0; j <= 150;j++){
                        e.onNext(j);
                        Log.i(TAG," 发送数据："+j);
                        try{
                            Thread.sleep(50);
                        }catch (Exception ex){
                        }
                    }
                }
            }, BackpressureStrategy.LATEST)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new Subscriber<Integer>() {
                    @Override
                        public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE); // 观察者设置接收事件的数量,如果不设置接收不到事件
                    }
                    @Override
                        public void onNext(Integer integer) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext : "+(integer));
                    }
                    @Override
                        public void onError(Throwable t) {
                        Log.e(TAG,"onError : "+t.toString());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG,"onComplete");
                    }
                });

        // MISSING就是没有采取背压策略的类型，效果跟Obserable一样。
        // 在设置MISSING类型时，可以配合onBackPressure相关操作符使用，也可以到达上述其他类型的处理效果。
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    for(int j = 0; j <= 150;j++){
                        e.onNext(j);
                        Log.i(TAG," 发送数据："+j);
                        try{
                            Thread.sleep(50);
                        }catch (Exception ex){
                        }
                    }
                }
            }, BackpressureStrategy.MISSING)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new Subscriber<Integer>() {
                    @Override
                        public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE); // 观察者设置接收事件的数量,如果不设置接收不到事件
                    }
                    @Override
                        public void onNext(Integer integer) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext : "+(integer));
                    }
                    @Override
                        public void onError(Throwable t) {
                        Log.e(TAG,"onError : "+t.toString());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG,"onComplete");
                    }
                });

        // onBackpressureDrop(),效果与Drop类型一样
        // onBackpressureBuffer ：与BUFFER类型一样效果。
        // onBackpressureDrop ：与DROP类型一样效果。
        // onBackpressureLaster ：与LASTER类型一样效果。
        Flowable.interval(50,TimeUnit.MILLISECONDS)
            .onBackpressureDrop()//效果与Drop类型一样
            .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
            .subscribe(new Consumer<Long>() {
                    @Override
                        public void accept(Long aLong) throws Exception {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext : "+(aLong));
                    }
                });

        // request(int count)：设置接收事件的数量.
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    for(int j = 0;j<30;j++){
                        e.onNext(j);
                        Log.i(TAG," 发送数据："+j);
                        try{
                            Thread.sleep(50);
                        }catch (Exception ex){
                        }
                    }
                }
            },BackpressureStrategy.BUFFER)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new Subscriber<Integer>() {
                    @Override
                        public void onSubscribe(Subscription s) {
                        s.request(10); // 观察者设置接收事件的数量,如果不设置接收不到事件
                    }
                    @Override
                        public void onNext(Integer integer) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext : "+(integer));
                    }
                    @Override
                        public void onError(Throwable t) {
                        Log.e(TAG,"onError : "+t.toString());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG,"onComplete");
                    }
                });

        // 当遇到在接收事件时想追加接收数量（如：通信数据通过几次接收，验证准确性的应用场景），可以通过以下方式进行扩展
        // 可以动态设置观察者接收事件的数量，但不影响被观察者继续发送事件。
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    for(int j = 0; j < 40;j++){
                        e.onNext(j);
                        Log.i(TAG," 发送数据："+j);
                        try{
                            Thread.sleep(50);
                        }catch (Exception ex){
                        }
                    }
                }
            },BackpressureStrategy.BUFFER)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    private Subscription subscription;
                    @Override
                        public void onSubscribe(Subscription s) {
                        subscription = s;
                        s.request(10); //观察者设置接收事件的数量,如果不设置接收不到事件
                    }
                    @Override
                        public void onNext(Integer integer) {
                        if (integer == 5) {
                            subscription.request(3);
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext : "+(integer));
                    }
                    @Override
                        public void onError(Throwable t) {
                        Log.e(TAG,"onError : "+t.toString());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG,"onComplete");
                    }
                });

        // requested打印的结果就是 剩余可接收的数量 ,它的作用就是可以检测剩余可接收的事件数量
        Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    for(int j = 0;j < 15;j++){
                        e.onNext(j);
                        Log.d(TAG, e.requested() + " 发送数据 j："+j);
                        try{
                            Thread.sleep(50);
                        }catch (Exception ex){
                        }
                    }
                }
            },BackpressureStrategy.BUFFER) //
            .subscribeOn(Schedulers.newThread()) //
            .observeOn(Schedulers.newThread())
            .subscribe(new Subscriber<Integer>() {
                    private Subscription subscription;
                    @Override
                        public void onSubscribe(Subscription s) {
                        subscription = s;
                        s.request(10); //观察者设置接收事件的数量,如果不设置接收不到事件
                    }
                    @Override
                        public void onNext(Integer integer) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext : "+(integer));
                    }
                    @Override
                        public void onError(Throwable t) {
                        Log.e(TAG,"onError : "+t.toString());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG,"onComplete");
                    }
                });
    }
}
