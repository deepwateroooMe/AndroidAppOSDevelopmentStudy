package com.me.jv.ui.activities.tests;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

@AndroidEntryPoint
public class ObservableObserve extends AppCompatActivity {
    private final String TAG = "test ObservableObserve";

    @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1、创建被观察者Observable
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    e.onNext("RxJava：e.onNext== 第一次");
                    e.onNext("RxJava：e.onNext== 第二次");
                    e.onNext("RxJava：e.onNext== 第三次");
                    e.onComplete();
                }
            });
        Log.d(TAG, "主线程==" + Thread.currentThread().getId()); // 被观察者发送事件的线程
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    e.onNext("RxJava：e.onNext== 第一次");       
                    e.onComplete();
                    Log.d(TAG, "subscribe()线程==" + Thread.currentThread().getId()); // 被观察者发送事件的线程
                }
            }).subscribeOn(Schedulers.io())            // 指定被观察者subscribe()(发送事件的线程)在IO线程()
            .observeOn(AndroidSchedulers.mainThread());// 指定观察者接收响应事件的线程在主线程

        // 2、创建观察者Observer
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe == 订阅");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext == " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete == ");
            }
        };

        // 3、订阅(观察者观察被观察者)
        observable.subscribe(observer);

        
        // 快速创建被观察者对象，仅发送onComplete()事件，直接通知完成。
        Observable.empty()
            .subscribe(new Observer<Object>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "empty：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Object o) {
                        Log.e(TAG, "empty：onNext ==" + o.toString());
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "empty：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "empty：onComplete == ");
                    }
                });

        // 快速创建被观察者对象，仅发送onError()事件，直接通知异常。
        Observable.error(new Throwable("只回调error"))
            .subscribe(new Observer<Object>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.d(TAG, "error：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Object o) {
                        Log.d(TAG, "error：onNext ==" + o.toString());
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.d(TAG, "error：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.d(TAG, "error：onComplete == ");
                    }
                });

        // 快速创建被观察者对象，不发送任何事件。
        Observable.never()
            .subscribe(new Observer<Object>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "never：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Object o) {
                        Log.e(TAG, "never：onNext ==" + o.toString());
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "never：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "never：onComplete == ");
                    }
                });

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).subscribe(new Observer<Integer>() { // 最多只能发送10个事件
                @Override
                    public void onSubscribe(Disposable d) {
                    Log.d(TAG, "just：onSubscribe == 订阅");
                }
                @Override
                    public void onNext(Integer integer) {
                    Log.d(TAG, "just：onNext == " + integer);
                }
                @Override
                    public void onError(Throwable e) {
                    Log.d(TAG, "just：onError == " + e.getMessage());
                }
                @Override
                    public void onComplete() {
                    Log.d(TAG, "just：onComplete == ");
                }
            });

        // 设置需要传入的数组
        String[] strings = {"商品类","非商品类", "Mine", "is", "yours", "Yours", " are", "all", "mine", "too", "right?"};
        // 传入数组，被观察者创建后会将数组转换成Observable并且发送里面所以的数据
        Observable.fromArray(strings).subscribe(new Observer<String>() {
                @Override
                    public void onSubscribe(Disposable d) {
                    Log.e(TAG, "fromArray：onSubscribe == 订阅");
                }
                @Override
                    public void onNext(String s) {
                    Log.e(TAG, "fromArray：onNext == " + s);
                }
                @Override
                    public void onError(Throwable e) {
                    Log.e(TAG, "fromArray：onError == " + e.getMessage());
                }
                @Override
                    public void onComplete() {
                    Log.e(TAG, "fromArray：onComplete == ");
                }
            });

        // 创建集合
        List<Integer> list = new ArrayList();
        for (int i = 0; i < 12; i++) 
            list.add(i);
        // 传入集合，被观察者创建后会将数组转换成Observable并且发送里面所有的数据
        Observable.fromIterable(list).subscribe(new Observer<Integer>() { // 这些个就可以远远超过10个了。。。
                @Override
                    public void onSubscribe(Disposable d) {
                    Log.e(TAG, "fromArray：onSubscribe == 订阅");
                }
                @Override
                    public void onNext(Integer goods) {
                    Log.e(TAG, "fromArray：onNext == " + goods);
                }
                @Override
                    public void onError(Throwable e) {
                    Log.e(TAG, "fromArray：onError == " + e.getMessage());
                }
                @Override
                    public void onComplete() {
                    Log.e(TAG, "fromArray：onComplete == ");
                }
            });

        // 1.初始化i
        Integer i = 100;
        // 2.通过defer()定义被观察者（此时被观察者对象还没创建）
//        Integer finalI = i;
        Observable<Integer> defer = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
                @Override
                public ObservableSource<? extends Integer> call() throws Exception {
                    return Observable.just(i); // 这里不知道该怎么测试
                }
            });
        // 3.重新设置i值
        i = 200;
        // 4.订阅观察者（此时才会调用defer,创建被观察者对象）
        defer.subscribe(new Observer<Integer>() {
                @Override
                    public void onSubscribe(Disposable d) {
                    Log.e(TAG, "defer：onSubscribe == 订阅");
                }
                @Override
                    public void onNext(Integer i) {
                    Log.e(TAG, "defer：onNext == " + i);
                }
                @Override
                    public void onError(Throwable e) {
                    Log.e(TAG, "defer：onError == " + e.getMessage());
                }
                @Override
                    public void onComplete() {
                    Log.e(TAG, "defer：onComplete == ");
                }
            });

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.e(TAG, "timer：当前时间 ==" + dateFormat.format(System.currentTimeMillis()));
        // 延时10秒后，发送一个long值为0的事件
        Observable.timer(3, TimeUnit.SECONDS)
            .subscribe(new Observer<Long>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "timer：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Long aLong) {
                        Log.e(TAG, "timer：onNext ==" + aLong + "   时间 ==" + dateFormat.format(System.currentTimeMillis()));
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "timer：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "timer：onComplete == ");
                    }
                });

        // initialDelay：表示延迟开始的时间, period：距离下一次发送事件的时间间隔, unit：时间单位    
        Observable.interval(3, 1, TimeUnit.SECONDS)
            .subscribe(new Observer<Long>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "interval：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Long aLong) {
                        Log.e(TAG, "interval：onNext ==" + aLong);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "interval：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "interval：onComplete == ");
                    }
                });

        // 事件开始大小为10，发送5次事件，延迟3秒后执行，每次执行的间隔为1，单位为秒
        Observable.intervalRange(10, 5, 3, 1, TimeUnit.SECONDS)
            .subscribe(new Observer<Long>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "intervalRange：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Long aLong) {
                        Log.e(TAG, "intervalRange：onNext ==" + aLong);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "intervalRange：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "intervalRange：onComplete == ");
                    }
                });

        // start：事件的开始值大小，count：发送的事件次数，一次整完，不能延迟
        Observable.range(5, 4).subscribe(new Observer<Integer>() {
                @Override
                    public void onSubscribe(Disposable d) {
                    Log.e(TAG, "range：onSubscribe == 订阅");
                }
                @Override
                    public void onNext(Integer integer) {
                    Log.e(TAG, "range：onNext ==" + integer);
                }
                @Override
                    public void onError(Throwable e) {
                    Log.e(TAG, "range：onError == " + e.getMessage());
                }
                @Override
                    public void onComplete() {
                    Log.e(TAG, "range：onComplete == ");
                }
            });

        // 链式编程
        Observable.just(1, 2, 3, 4, 5)
            // 使用Map操作符中的Function函数对被观察者发送的事件统一作出处理
            .map(new Function<Integer, Integer>() {
                    @Override
                        public Integer apply(Integer integer) throws Exception {
                        // 对被观察者just发送的结果，都全部乘以10处理
                        return integer * 10;
                    }
                }).subscribe(new Consumer<Integer>() {// 订阅
                        @Override
                            public void accept(Integer integer) throws Exception {// 接受事件结果，是处理后的结果
                            Log.e(TAG, "map：accept == " + integer);
                        }
                    });

        Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                    public void subscribe(ObservableEmitter<String> e) {
                    e.onNext("A");
                    e.onNext("B");
                    e.onComplete();
                }
            }).flatMap(new Function<String, ObservableSource<String>>() { // 可能会无序
                    @Override// 通过flatMap将被观察者生产的事件进行拆分，再将新的事件转换成一个新的Observable发送
                        public ObservableSource<String> apply(String s) {
                        List<String> list = new ArrayList<>();
                        Log.e(TAG, "flatMap：apply == 事件" + s);
                        // 将一个事件拆分成两个子事件，例如将A事件拆分成A0,A1两个事件，然后再整个合成一个Observable通过fromIterable发送给订阅者
                        for (int j = 0; j < 5; j++) {
                            list.add("拆分后的子事件" + s + j);
                        }
                        return Observable.fromIterable(list);
                    }
                }).subscribe(new Consumer<String>() {
                        @Override
                            public void accept(String s) {
                            Log.e(TAG, "flatMap：accept == " + s);
                        }
                    });
        
        Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                    public void subscribe(ObservableEmitter<String> e) {
                    e.onNext("A");
                    e.onNext("B");
                    e.onComplete();
                }
            }).concatMap(new Function<String, ObservableSource<String>>() { // 保证了顺序性
                    @Override// 通过concatMap（有序）将被观察者生产的事件进行拆分，再将新的事件转换成一个新的Observable发送
                        public ObservableSource<String> apply(String s) {
                        List<String> strings = new ArrayList<>();
                        Log.e(TAG, "concatMap：apply == 事件" + s);
                        for (int j = 0; j < 5; j++) {
                            strings.add("拆分后的子事件" + s + j);
                        }
                        return Observable.fromIterable(strings);
                    }
                }).subscribe(new Consumer<String>() {
                        @Override
                            public void accept(String s) {
                            Log.e(TAG, "concatMap：accept == " + s);
                        }
                    });

        Observable.just("A", "B", "C", "D", "E")// 这里演示发送5个事件
            .buffer(3, 2) // 缓存列表数量为3个，步长为2
            .subscribe(new Observer<List<String>>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "buffer：onSubscribe == 订阅");
                    }
                     @Override
                        public void onNext(List<String> strings) {
                        Log.e(TAG, "buffer：onNext == 缓存事件数：" + strings.size());
                        for (int j = 0; j < strings.size(); j++) {
                            Log.e(TAG, "buffer：子事件==" + strings.get(j));
                        }
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "buffer：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "buffer：onComplete == ");
                    }
                });

        Observable.concat(Observable.just(1, 2), Observable.just(3, 4), Observable.just(5, 6), Observable.just(7, 8))
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "concat：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "concat：onNext ==" + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "concat：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "concat：onComplete == ");
                    }
                });
        Observable.concatArray(Observable.just("一", "二"), Observable.just("三", "四"),
                               Observable.just("五", "六"), Observable.just("七", "八"), Observable.just("九", "十"))
            .subscribe(new Observer<String>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "concatArray：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(String s) {
                        Log.e(TAG, "concatArray：onNext ==" + s);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "concatArray：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "concatArray：onComplete == ");
                    }
                });

        // 起始值为1，发送3个事件，第一个事件延迟1秒发送，事件间隔为1秒
        Observable<Long> observable1 = Observable.intervalRange(1, 3, 1, 1, TimeUnit.SECONDS);
        // 起始值为10，发送3个事件，第一个事件延迟1秒发送，事件间隔为1秒
        Observable<Long> observable2 = Observable.intervalRange(10, 3, 1, 1, TimeUnit.SECONDS);
        Observable.merge(observable1, observable2)
            .subscribe(new Observer<Long>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "concatArray：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Long aLong) {
                        Log.e(TAG, "concatArray：onNext ==" + aLong);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "concatArray：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "concatArray：onComplete == ");
                    }
                });

        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    e.onNext("第一个事件");
                    e.onNext("第二个事件");
                    e.onError(new Exception("中途抛出异常"));
                    e.onComplete();
                }
            });
        Observable<String> observable2 = Observable.just("第三个事件");
        // 中途抛出异常列子：
        Observable.concat(observable1, observable2).subscribe(new Observer<String>() {
                @Override
                    public void onSubscribe(Disposable d) {
                    Log.e(TAG, "concatDelayError：onSubscribe == 订阅");
                }
                @Override
                    public void onNext(String s) {
                    Log.e(TAG, "concatDelayError：onNext ==" + s);
                }
                @Override
                    public void onError(Throwable e) {
                    Log.e(TAG, "concatDelayError：onError == " + e.getMessage());
                }
                @Override
                    public void onComplete() {
                    Log.e(TAG, "concatDelayError：onComplete == ");
                }
            });

        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    e.onNext("第一个事件");
                    e.onNext("第二个事件");
                    e.onError(new Exception("中途抛出异常"));
                    e.onComplete();
                }
            });
        Observable<String> observable2 = Observable.just("第三个事件");
        // 中途抛出异常列子：
        Observable.concatArrayDelayError(observable1, observable2).subscribe(new Observer<String>() {
                @Override
                    public void onSubscribe(Disposable d) {
                    Log.e(TAG, "concatDelayError：onSubscribe == 订阅");
                }
                @Override
                    public void onNext(String s) {
                    Log.e(TAG, "concatDelayError：onNext ==" + s);
                }
                @Override
                    public void onError(Throwable e) {
                    Log.e(TAG, "concatDelayError：onError == " + e.getMessage());
                }
                @Override
                    public void onComplete() {
                    Log.e(TAG, "concatDelayError：onComplete == ");
                }
            });

        // 创建被观察者-异常
        Observable<Object> errorObservable = Observable.error(new Exception("抛出异常"));
        // 产生0,2,4的事件序列，每隔1秒发送事件，一共发送3次
        Observable<Object> observable1 = Observable.interval(0, 1, TimeUnit.SECONDS)
            .map(new Function<Long, Object>() {
                    @Override
                    public Object apply(Long aLong) throws Exception {
                        return aLong * 2;
                    }
                }).take(3);/*.mergeWith(errorObservable.delay(4, TimeUnit.SECONDS))*/
        // 产生0,10,20的事件序列，每隔1秒发送事件，一共发送3次，
        Observable<Long> observable2 = Observable.interval(1, 1, TimeUnit.SECONDS)
            .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 10;
                    }
                }).take(3);
        Observable.mergeDelayError(observable1, errorObservable, observable2)
            .subscribe(new Observer<Object>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "mergeDelayError：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Object o) {
                        Log.e(TAG, "mergeDelayError：onNext ==" + o);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "mergeDelayError：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "mergeDelayError：onComplete == ");
                    }
                });

        ArrayList<String> strings = new ArrayList<>();
        strings.add("Array:1");
        Observable.just("一", "二", "三", "四")
            .startWith(strings)// 插入单个集合
            .startWith("startWith:2")// 插入单个数据
            .startWithArray("startWithArray:3", "startWithArray:4")// 插入多个数据
            .subscribe(new Observer<String>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "startWith：onSubscribe == 订阅");
                    }
                     @Override
                        public void onNext(String s) {
                        Log.e(TAG, "startWith：onNext 结果== " + s);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "startWith：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "startWith：onComplete == ");
                    }
                });

        // 设置需要传入的被观察者数据
        Observable<Integer> observable1 = Observable.just(1, 2, 3, 4);
        Observable<String> observable2 = Observable.just("A", "B", "C");
        // 回调apply()方法，并在里面自定义合并结果的逻辑
        // BiFunction<Integer, String, String>，第一个类型为observable1的参数类型，第二个类型为observable2的参数类型，第三个为合并后的参数类型
        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
                @Override
                    public String apply(Integer integer, String str) throws Exception {
                    return integer + str;
                }
            }).subscribe(new Observer<String>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "zip：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(String s) {
                        Log.e(TAG, "zip：onNext == " + s);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "zip：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "zip：onComplete == ");
                    }
                });

        // 产生0,10,20的事件序列，每隔1秒发送事件，一共发送3次
        Observable<Long> observable1 = Observable.interval(0, 1, TimeUnit.SECONDS)
            .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 10;
                    }
                }).take(3);
        // 产生0,1,2,3,4的事件序列，起始值为0，一共发送4次，延迟1秒后开始发送，每隔1秒发送事件
        Observable<Long> observable2 = Observable.intervalRange(0, 4, 1, 1, TimeUnit.SECONDS)
            .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 1;
                    }
                });
        Observable.combineLatest(observable1, observable2, new BiFunction<Long, Long, Long>() {
                @Override
                    public Long apply(Long o1, Long o2) throws Exception {
                    Log.e(TAG, "combineLatest：apply: o1+o2：" + o1 + "+" + o2);
                    // observable1的最后的一个数据都与observable2的每一个数据相加
                    return o1 + o2;
                }
            }).subscribe(new Observer<Long>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "combineLatest：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Long aLong) {
                        Log.e(TAG, "combineLatest：onNext 合并的结果== " + aLong);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "combineLatest：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "combineLatest：onComplete == ");
                    }
                });

        Observable.just(1, 2, 3, 4, 5).
            reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                        public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.e(TAG, "reduce：accept 计算结果== " + integer + "*" + integer2);
                        // 按先后顺序，两个事件聚合处理后 ，将结果再与下一事件聚合处理，依次类推
                        return integer * integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
                        @Override
                            public void accept(Integer integer) throws Exception {
                            Log.e(TAG, "reduce：accept 合并的结果== " + integer);
                        }
                    });

        // 第一个参数：声明容器的类型，第二个参数：处理数据的逻辑，加入容器中
        Observable.just(1, 2, 3, 4, 5).collect(new Callable<ArrayList<Integer>>() {
                @Override
                    public ArrayList<Integer> call() throws Exception {
                    return new ArrayList<>();
                }
            }, new BiConsumer<ArrayList<Integer>, Integer>() {
                @Override
                    public void accept(ArrayList<Integer> list, Integer integer) throws Exception {
                    Log.e(TAG, "reduce：collect 加入容器的数据== " + integer);
                    list.add(integer);
                }
            }).subscribe(new Consumer<ArrayList<Integer>>() {
                    @Override
                        public void accept(ArrayList<Integer> integers) throws Exception {
                        Log.e(TAG, "reduce：collect 最后结果== " + integers);
                    }
                });

        Observable.just(1, 2, 3, 4, 5)
            .count()
            .subscribe(new Consumer<Long>() {
                    @Override
                        public void accept(Long l) throws Exception {
                        Log.e(TAG, "reduce：accept 事件数== " + l);
                    }
                });
     
        Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(ObservableEmitter<Integer> e) {
                    e.onNext(1);
                    e.onNext(2);
                    e.onError(new Exception("抛出异常"));
                    e.onNext(3);
                    e.onComplete();
                }
            })
            // 延时2秒，时间单位为秒，开启错误回调延时
            .delay(2, TimeUnit.SECONDS, true)
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "delay：onSubscribe == 订阅");
                    }
                     @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "delay：onNext == " + integer);
                    }
                     @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "delay：onError == " + e.getMessage());
                    }
                     @Override
                        public void onComplete() {
                        Log.e(TAG, "delay：onComplete == ");
                    }
                });

        Observable.just(1, 2, 3)
            // 执行next()事件前执行
            .doOnNext(new Consumer<Integer>() {
                    @Override
                        public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "doOnNext 事件前执行== " + integer);
                    }
                })
            // 当Observable每次发送一个数据事件都会调用(onNext()前回调)
            .doOnEach(new Consumer<Notification<Integer>>() {
                    @Override
                        public void accept(Notification<Integer> integerNotification) {
                        Log.e(TAG, "doOnEach 每次都执行== " + integerNotification.getValue());
                    }
                })
            // 执行next()事件后执行
            .doAfterNext(new Consumer<Integer>() {
                    @Override
                        public void accept(Integer integer) {
                        Log.e(TAG, "doAfterNext 事件后执行== " + integer);
                    }
                })
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "onNext == " + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "onComplete == ");
                    }
                });

        Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(ObservableEmitter<Integer> e) {
                    e.onNext(1);
                    e.onError(new Exception("抛出异常"));
                    e.onNext(2);
                    e.onComplete();
                }
            })
            // Observable发送错误事件时调用
            .doOnError(new Consumer<Throwable>() {
                    @Override
                        public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "doOnError 发送错误事件== " + throwable.getMessage());
                    }
                })
            // Observable发送事件完毕后，无论是正常发送还是异常终止都会执行
            .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doAfterTerminate == 事件结束");
                    }
                })
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "onNext == " + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "onComplete == ");
                    }
                });

        Observable.just(1, 2, 3)
            // 观察者订阅时调用
            .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                        public void accept(Disposable disposable) {
                        Log.e(TAG, "doOnSubscribe == 订阅时执行");
                    }
                })
            // Observable正常发送事件完成后调用
            .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        Log.e(TAG, "doOnComplete == 事件完成执行");
                    }
                })
            // 最后执行
            .doFinally(new Action() {
                    @Override
                    public void run() {
                        Log.e(TAG, "doFinally == 最后执行");
                    }
                })
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "concat：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "concat：onNext ==" + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "concat：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "concat：onComplete == ");
                    }
                });

        Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                    // 发送1~10共10个事件
                    for (int i = 0; i < 10; i++) {
                        if (i == 5) {// i==5抛出异常
                            e.onError(new Exception("抛出异常"));
                        }
                        e.onNext(i);
                    }
                }
            })
            // 在Observable发生错误或者异常的时候，拦截错误并执行指定逻辑，
            // 返回一个与源Observable相同类型的结果（onNext()），最后回调onConplete()方法。
            .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                        public Integer apply(Throwable throwable) throws Exception {
                        return 617;
                    }
                })
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "concat：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "concat：onNext ==" + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "concat：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "concat：onComplete == ");
                    }
                });

        Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                    // 发送2个事件
                    e.onNext("一");
                    e.onNext("二");
                    // 抛出异常
                    e.onError(new Exception("抛出异常"));
                    e.onNext("三");
                }
            })
            // 拦截错误并执行指定逻辑，返回Observable
            .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends String>>() {
                    @Override
                        public ObservableSource<? extends String> apply(Throwable throwable) {
                        return Observable.just("607", "608", "609", "七");
                    }
                })
            .subscribe(new Observer<String>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onErrorResumeNext：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(String s) {
                        Log.e(TAG, "onErrorResumeNext：onNext ==" + s);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "onErrorResumeNext：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "onErrorResumeNext：onComplete == ");
                    }
                });

        Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                    // 发送2个事件
                    e.onNext("事件1");
                    e.onNext("事件2");
                    // 抛出异常
                    e.onError(new Exception("抛出异常"));
                }
            })
            // 拦截异常并执行指定逻辑,返回多个Observable执行的事件
            .onExceptionResumeNext(new ObservableSource<String>() {
                    @Override
                        public void subscribe(Observer<? super String> observer) {
                        observer.onNext("706");
                        observer.onNext("707");
                        observer.onNext("708");
                    }
                })
            .subscribe(new Observer<String>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onExceptionResumeNext：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(String s) {
                        Log.e(TAG, "onExceptionResumeNext：onNext ==" + s);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "onExceptionResumeNext：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "onExceptionResumeNext：onComplete == ");
                    }
                });

        Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(ObservableEmitter<Integer> e) {
                    e.onNext(1);
                    e.onNext(2);
                    e.onError(new Exception("抛出异常"));
                }
            })
            // 重新发送数据，这里重发2次，带有异常信息回调
            .retry(2, new Predicate<Throwable>() {
                    @Override
                        public boolean test(Throwable throwable) {
                        // false: 不重新发送数据，回调Observer的onError()方法结束
                        // true: 重新发送请求（最多发送2次）
                        return true;
                    }
                })
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "retry：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "retry：onNext ==" + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "retry：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "retry：onComplete == ");
                    }
                });

        Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(ObservableEmitter<Integer> e) {
                    e.onNext(66);
                    e.onNext(88);
                    e.onError(new Exception("抛出异常"));
                }
            })
            // 发生错误或者异常时，重试，将原来Observable的错误或者异常事件，转换成新的Observable
            // 如果新的Observable返回了onError()事件，则不再重新发送原来Observable的数据
            // 如果新的Observable返回onNext事件，则会重新发送原来Observable的数据
            .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                        public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                                @Override
                                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                    // 新观察者Observable发送onNext()事件，则源Observable重新发送数据。如果持续遇到错误则持续重试。
                                    // return Observable.just(1);
                                    // 回调onError()事件，并且接收传过去的错误信息
                                    return Observable.error(new Exception("抛出异常2"));
                                }
                            });
                    }
                })
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "retryWhen：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "retryWhen：onNext ==" + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "retryWhen：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "retryWhen：onComplete == ");
                    }
                });

        Observable.just(1,2)
            // 重复2次
            .repeat(2)
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "repeat：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "repeat：onNext ==" + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "repeat：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "repeat：onComplete == ");
                    }
                });

        Observable.just(1004,1005) // 
            .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
                    // 如果新的Observable被观察者返回onComplete()或onError()事件，则不重新订阅、发送源Observable的事件
                    // 如果新的Observable被观察者返回其他事件，则重新订阅、发送源Observable的事件
                    @Override
                        public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                        // flatMap用于接收上面的数据
                        return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                                @Override
                                    public ObservableSource<?> apply(Object o) throws Exception {
                                    // 通知原来的Observable，重新订阅和发送事件（发送什么数据不重要，这里仅做通知使用）
                                    // return Observable.just(1);
                                    // 等于发送onComplete()方法，但是不会回调Observer的onComplete()
                                    // return Observable.empty();
                                    // 回调onError()事件，并且接收传过去的错误信息
                                    return Observable.error(new Exception("抛出异常"));
                                }
                            });
                    }
                }).subscribe(new Observer<Integer>() {
                        @Override
                            public void onSubscribe(Disposable d) {
                            Log.e(TAG, "repeatWhen：onSubscribe == 订阅");
                        }
                        @Override
                            public void onNext(Integer integer) {
                            Log.e(TAG, "repeatWhen：onNext ==" + integer);
                        }
                        @Override
                            public void onError(Throwable e) {
                            Log.e(TAG, "repeatWhen：onError == " + e.getMessage());
                        }
                        @Override
                            public void onComplete() {
                            Log.e(TAG, "repeatWhen：onComplete == ");
                        }
                    });

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .filter(new Predicate<Integer>() {
                    @Override
                        public boolean test(Integer integer) throws Exception {
                        // 条件：能否被2整除
                        return integer % 2 == 0;
                    }
                }).subscribe(new Observer<Integer>() {
                        @Override
                            public void onSubscribe(Disposable d) {
                            Log.e(TAG, "filter：onSubscribe == 订阅");
                        }
                        @Override
                            public void onNext(Integer integer) {
                            Log.e(TAG, "filter：apply == 事件" + integer);
                        }
                        @Override
                            public void onError(Throwable e) {
                            Log.e(TAG, "filter：onError == " + e.getMessage());
                        }
                        @Override
                            public void onComplete() {
                            Log.e(TAG, "filter：onComplete == ");
                        }
                    });

        class Goods {
            String name;
            public Goods(String s) {
                name = s;
            }
            public String getName() {
                return name;
            }
        }
        Observable.just(1, "大闸蟹", true, 0.23f, 5L, new Goods("商品名"))
            .ofType(Goods.class).subscribe(new Observer<Goods>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "ofType：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Goods goods) {
                        Log.e(TAG, "ofType：onNext == " + goods.getName());
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "ofType：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "ofType：onComplete == ");
                    }
                });

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .elementAt(7)
            .subscribe(new Consumer<Integer>() {
                    @Override
                        public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "elementAt：accept == " + integer);
                    }
                });

        Observable.just(1, 2, 2, 3, 4, 4, 4)
            .distinct()
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "distinct：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "distinct：onNext == " + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "distinct：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "distinct：onComplete == ");
                    }
                });

        Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                    public void subscribe(ObservableEmitter<Integer> e) {
                    try {// 产生结果的时间间隔为0,100,200……900毫秒，每次发送的数据为0,1,2……9
                        for (int i = 0; i < 10; i++) {
                            e.onNext(i);
                            Thread.sleep(i * 100);
                        }
                    } catch (Exception exception) {
                        e.onError(exception);
                    }
                }
            }).debounce(300, TimeUnit.MILLISECONDS)
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "debounce：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "debounce：onNext ==" + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "debounce：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "debounce：onComplete == ");
                    }
                });

        Observable.just(1,2,3)
            .first(0)// defaultItem：默认值
            .subscribe(new Consumer<Integer>() {
                    @Override
                        public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "first：accept ==" + integer);
                    }
                });

        Observable.just(1, 2, 3, 4)
            .last(-1)
            .subscribe(new Consumer<Integer>() {
                    @Override
                        public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "last：accept ==" + integer);
                    }
                });
        
        Observable.just(1, 2, 3, 4, 5, 6, 7)
            .skip(3)
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "skip：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "skip：onNext ==" + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "skip：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "skip：onComplete == ");
                    }
                });

        Observable.just(1, 2, 3, 4, 5, 6, 7)
            .take(3)
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "take：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "take：onNext ==" + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "take：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "take：onComplete == ");
                    }
                });

        Observable.interval(1, TimeUnit.SECONDS)
            .take(5)
            .groupBy(new Function<Long, Object>() {
                    @Override
                        public Object apply(Long aLong) throws Exception {
                        return aLong * 10;
                    }
                }).subscribe(new Observer<GroupedObservable<Object, Long>>() {
                        @Override
                            public void onSubscribe(Disposable d) {
                            Log.e(TAG, "take：onSubscribe == 订阅");
                        }
                        @Override
                            public void onNext(GroupedObservable<Object, Long> objectLongGroupedObservable) {
                            Log.e(TAG, "take：onNext == key:" + objectLongGroupedObservable.getKey());
                        }
                        @Override
                            public void onError(Throwable e) {
                            Log.e(TAG, "take：onError == " + e.getMessage());
                        }
                        @Override
                            public void onComplete() {
                            Log.e(TAG, "take：onComplete == ");
                        }
                    });

        Observable.just(1, 2, 3, 4, 5, 6)
            .cast(Integer.class)
            .subscribe(new Observer<Integer>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "cast：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Integer integer) {
                        Log.e(TAG, "cast：onNext == " + integer);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "cast：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "cast：onComplete == ");
                    }
                });

        Observable.just(1, 2, 3)
            .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                        public Integer apply(Integer sum, Integer integer) throws Exception {
                        // sum是上次计算的记过，integer是本次计算的参数
                        Log.e(TAG, "scan：apply == sum + integer = " + sum + " + " + integer);
                        return sum + integer;
                    }
                }).subscribe(new Observer<Integer>() {
                        @Override
                            public void onSubscribe(Disposable d) {
                            Log.e(TAG, "scan：onSubscribe == 订阅");
                        }
                        @Override
                            public void onNext(Integer integer) {
                            Log.e(TAG, "scan：onNext == " + integer);
                        }
                        @Override
                            public void onError(Throwable e) {
                            Log.e(TAG, "scan：onError == " + e.getMessage());
                        }
                        @Override
                            public void onComplete() {
                            Log.e(TAG, "scan：onComplete == ");
                        }
                    });

        // 每隔一秒，产生0,5,10,15,20事件队列
        Observable<Long> observable1 = Observable.interval(0,1000, TimeUnit.MILLISECONDS)
            .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 5;
                    }
                }).take(5);
        // 延时500毫秒，每隔一秒，产生0,10,20,30,40 事件队列
        Observable<Long> observable2 = Observable.interval(500, 1000,TimeUnit.MILLISECONDS)
            .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return aLong * 10;
                    }
                }).take(5);
        observable1.join(observable2, new Function<Long, ObservableSource<Long>>() {
                @Override
                    public ObservableSource<Long> apply(Long aLong) {
                    // 使Observable延时600毫秒执行(控制observable1的生命周期)
                    return Observable.just(aLong).delay(600,TimeUnit.MILLISECONDS);
                }
            }, new Function<Long, ObservableSource<Long>>() {
                @Override
                    public ObservableSource<Long> apply(Long aLong) {
                    // 使Observable延时600毫秒执行(控制observable2的生命周期)
                    return Observable.just(aLong).delay(600,TimeUnit.MILLISECONDS);
                }
            }, new BiFunction<Long, Long, Long>() {
                @Override
                    public Long apply(Long aLong, Long aLong2) {
                    // 合并逻辑
                    Log.e(TAG, "BiFunction：apply == aLong1 + aLong2：" + aLong + " + " + aLong2);
                    return aLong + aLong2;
                }
            }).subscribe(new Observer<Long>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "join：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Long aLong) {
                        Log.e(TAG, "join：onNext == " + aLong);
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "join：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "join：onComplete == ");
                    }
                });

        // 每隔1秒，产生0,5,10,15,20事件队列
        Observable<Long> observable1 = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return aLong * 5;
                    }
                }).take(5);
        // 延迟0.5秒，每秒产生0,10,20,30,40事件队列
        Observable<Long> observable2 = Observable.interval(500, 1000, TimeUnit.MILLISECONDS)
            .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return aLong * 10;
                    }
                }).take(5);
        observable1.groupJoin(observable2, new Function<Long, ObservableSource<Long>>() {
                @Override
                    public ObservableSource<Long> apply(Long aLong) {
                    // 使Observable延时1600毫秒执行(控制observable1的生命周期)
                    return Observable.just(aLong).delay(1600, TimeUnit.MILLISECONDS);
                }
            }, new Function<Long, ObservableSource<Long>>() {
                @Override
                    public ObservableSource<Long> apply(Long aLong) {
                    // 使Observable延时600毫秒执行(控制observable2的生命周期)
                    return Observable.just(aLong).delay(600, TimeUnit.MILLISECONDS);
                }
            }, new BiFunction<Long, Observable<Long>, Observable<Long>>() {
                @Override
                    public Observable<Long> apply(final Long aLong, Observable<Long> longObservable) {
                    return longObservable.map(new Function<Long, Long>() {
                            @Override
                                public Long apply(Long aLong2) {
                                // 合并逻辑
                                Log.e(TAG, "BiFunction：apply == aLong1 + aLong2：" + aLong + " + " + aLong2);
                                return aLong + aLong2;
                            }
                        });
                }
            }).subscribe(new Observer<Observable<Long>>() {
                    @Override
                        public void onSubscribe(Disposable d) {
                        Log.e(TAG, "groupJoin：onSubscribe == 订阅");
                    }
                    @Override
                        public void onNext(Observable<Long> longObservable) {
                        longObservable.subscribe(new Consumer<Long>() {
                                @Override
                                    public void accept(Long aLong) {
                                    Log.e(TAG, "groupJoin：onNext == " + aLong);
                                }
                            });
                    }
                    @Override
                        public void onError(Throwable e) {
                        Log.e(TAG, "groupJoin：onError == " + e.getMessage());
                    }
                    @Override
                        public void onComplete() {
                        Log.e(TAG, "groupJoin：onComplete == ");
                    }
                });
    }
}



















