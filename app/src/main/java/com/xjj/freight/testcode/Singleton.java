package com.xjj.freight.testcode;

/**
 * Describe: 线程安全的单例模式
 *
 * @author xujingjing
 * @date 2018/10/19 0019
 */
public class Singleton {

    /**
     * 这里又要提出一种新的模式——Initialization on Demand Holder.
     * 这种方法使用内部类来做到延迟加载对象，在初始化这个内部类的时候，
     * JLS(Java Language Sepcification)会保证这个类的线程安全。这种写法最大的美在于，
     * 完全使用了Java虚拟机的机制进行同步保证，没有一个同步的关键字。
     */
    private static class singletonHolder{
        private final static Singleton instance = new Singleton();
    }

    public static Singleton getInstance(){
        return singletonHolder.instance;
    }



    /**
     * 双重检查锁（Double-Checked Lock）
     */
    private static Singleton singleton = null;

    public static Singleton getSingleton(){
        if (singleton == null){
            synchronized (Singleton.class){
                if (singleton == null){
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
