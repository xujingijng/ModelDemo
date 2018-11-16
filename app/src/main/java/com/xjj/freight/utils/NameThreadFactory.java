package com.xjj.freight.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Describe: 支持阿里代码规范要求(线程池代替线程)
 *
 * @author xujingjing
 * @date 2018/6/6 0006
 */
public final class NameThreadFactory implements ThreadFactory {

    private final String name;
    private AtomicInteger count = new AtomicInteger(0);

    public NameThreadFactory(String name){
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable var1) {
        Thread t = new Thread(var1);
        String threadName = getName() + count.addAndGet(1);
        t.setName(threadName);
        return t;
    }

    public String getName() {
        return name;
    }
}
