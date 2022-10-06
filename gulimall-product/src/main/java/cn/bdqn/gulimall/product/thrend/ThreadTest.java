package cn.bdqn.gulimall.product.thrend;

import java.util.concurrent.*;

public class ThreadTest {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("main --- start ---");
        // 创建异步任务
        // 1 无返回值  可以指定 线程池
        /*CompletableFuture<Void> runA = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("i 的值：" + i);
        }, executor);*/

        // 2 有返回值 可以指定 线程池
  /*      CompletableFuture<Integer> suppli = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("i 的值：" + i);
            return i;
        }, executor).whenComplete((res,exception) -> {
            // 当异步编排执行成功之后执行
            System.out.println("当异步编排执行成功.." + res + "，异常：" + exception);
        }).exceptionally(throwable -> {
            // 感知异常
            System.out.println("当异步编排执行失败了");
            return 10;
        });
*/
        // 不管成功异常都会执行
       /* CompletableFuture<Integer> suppli = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("i 的值：" + i);
            return i;
        }, executor).handle((res,thr) -> {
            if (res!=null){
                return res*5;
            }
            if (thr!=null){
                return 1;
            }
            return 0;
        });*/
        /**
         * 串行化
         * 1 thenRun 无参无返，
         *  .thenRunAsync(()->{
         *             System.out.println("任务二开启了。。。");
         *         },executor);
         *  2 thenAccept 有参无返
         *      .thenAcceptAsync(res->{
         *             // 返回上一次的返回结果
         *             System.out.println("任务二开启了。。。"+res);
         *         },executor);
         *  3
         */
       /* CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("i 的值：" + i);
            return i;
        }, executor).thenApplyAsync(res -> {
            System.out.println("任务二启动了。。。" + res);
            return "hello " + res;
        }, executor);*/
        /**
         * 多任务组合 2个完成
         *
         */
        /*CompletableFuture<Object> f1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1启动：" + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("任务1结束。。。");
            return i;
        }, executor);

        CompletableFuture<Object> f2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2启动：" + Thread.currentThread().getId());
            int i = 10 / 4;
            try {
                Thread.sleep(3000);
                System.out.println("任务2结束。。。");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "f2 hello...";
        }, executor);*/

        /**
         * void run();
         */
        /*f1.runAfterBothAsync(f2,()->{
            System.out.println("任务3启动：" + Thread.currentThread().getId());
        },executor);*/
        /**
         * void accept(T t, U u);
         */
        /*f1.thenAcceptBothAsync(f2,(f1p,f2p) -> {
            System.out.println("任务三启动。。" + f1p + "，f2 ->" + f2p);
        },executor);*/
        /**
         *  R apply(T t, U u);
         */
        /*CompletableFuture<String> f3 = f1.thenCombineAsync(f2, (f1p, f2p) -> {
            return f1p + "-=>" + f2p + "........................";
        }, executor);*/
        // 两个任务组合 一个完成即可
        // 不感知结果，
        /*f1.runAfterEitherAsync(f2,()->{
            System.out.println("任务3启动" );
        },executor);*/
        // 感知结果 void accept(T t);  两个任务的返回类型必须一样
        /*f1.acceptEitherAsync(f2, res -> {
            System.out.println("任务3启动 " + res);
        },executor);*/
        // 感知结果,有返回值
        /*CompletableFuture<String> f3 = f1.applyToEitherAsync(f2, res -> {
            System.out.println("任务3启动 " + res);
            return res.toString() + "任务3333";
        }, executor);*/

        // 多任务组合
        //   全部任务执行完毕
        CompletableFuture<String> productImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("商品图片");
            return "hello.jpeg";
        }, executor);

        CompletableFuture<String> productName = CompletableFuture.supplyAsync(() -> {
            System.out.println("商品名称");
            return "华为";
        }, executor);

        CompletableFuture<String> productAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("商品属性");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "属性";
        }, executor);

        /*CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(productImg, productName, productAttr);
        System.out.println("商品图片：" + productImg.get() + "，商品名称："+productName.get()+
                "，商品属性："+productAttr.get());*/
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(productName, productAttr,productImg);

        System.out.println(anyOf.get());


        System.out.println("main --- end ---");
    }


    public void threadTest(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main --- start ---");
        //new Thread01().start();
        //new Thread(new Runnable02()).start();
       /* // 阻塞时等待，要等返回值返回才进行结束
        FutureTask<Integer> futureTask = new FutureTask<>(new callabel03());
        futureTask.run();
        Integer i = futureTask.get();
        System.out.println("i " + i);*/
       // 线程池方式
       // service.execute(new Runnable02());

        System.out.println("main --- end ---");


        /**
         *  corePoolSize：核心线程数；一直存在，除非设置 allowCoreThreadTimeOut ，线程池，准备好就可以提交任务
         *  maximumPoolSize 最大线程数（200个）控制资源
         *  keepAliveTime 存活时间；当前线程大于core 数量，释放空闲线程，maximumPoolSize-corePoolSize，
         *      只要 空闲时间大于 keepAliveTime
         *  unit 时间单位
         *  BlockingQueue<Runnable> workQueue, 阻塞队列，如果任务过多，就会将目前多的任务放到队列，只要线程空闲，就会进入队列进入
         *  threadFactory 线程工厂
         *  handler 如果队列满了，就使用决绝策略
         *
         * 工作顺序：
         * 1 线程池创建，准备核心线程数量（core），准备接受任务
         * 1.1 核心线程数满了，将进来的任务放入阻塞队列中，空闲的core就会自己去阻塞队列获取任务执行
         * 1.2 阻塞队列满了，直接开新线程执行，最大只能考到max指定的数量
         * 1.3 max满了就用 RejectedExcutionHandler 拒绝任务
         * 1.4 max都执行完毕，很多空闲线程。在指定时间 keepAliveTime 以后，是否 max-core这些线程
         *  new LinkedBlockingDeque<>() 默认是Integer 最大值，内存不够
         *
         *  面试题：
         *  一个线程池，core 7  max 20 queue 50 100并发进来怎么分配的
         *  7 个线程立即执行，50个会进入队列，再开13个进行执行，剩下的30个就是要拒绝策略
         *      如果不想抛弃还有执行， CallerRunsPolicy
         * 第二个方式
         */

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        //new ThreadPoolExecutor.DiscardOldestPolicy()  队列满了，则把旧的任务丢了，执行新任务
        //new ThreadPoolExecutor.CallerRunsPolicy() 队列满了，则执行run方法，同步执行
        //new ThreadPoolExecutor.AbortPolicy() 队列满了，则把新任务扔掉，会抛异常
        //new ThreadPoolExecutor.DiscardPolicy() 队列满了，则把新任务扔掉，不会抛异常


        executor.execute(new Runnable02());







    }

    /**
     * 继承 Thread
     */
    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("i 的值：" + i);
        }
    }

    /**
     * 实现 Runnable 接口
     */
    public static class Runnable02 implements Runnable {

        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("i 的值：" + i);
        }
    }

    /**
     * 实现 Callable 接口，泛型是返回类型  + FutureTask
     */
    public static class callabel03 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("i 的值：" + i);
            return i;
        }
    }
    /**
     * 线程池
     *  给线程池提交任务
     *  将所有提交的任务异步提交给线程池
     *  1/2  不能得到返回值，3 可以得到返回值
     *  1/2/3 不能控制资源
     *  4 可以控制资源，性能稳定。（性能第一Z）
     */

}
