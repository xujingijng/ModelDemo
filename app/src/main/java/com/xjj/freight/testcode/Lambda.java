package com.xjj.freight.testcode;

/**
 * Describe: lambda表达式
 *
 * @author xujingjing
 * @date 2018/10/19 0019
 */
public class Lambda {

    public void none(None n){
        n.none();
    }

    public void one(One o){
        o.one("一个参数");
    }

    public void two(Two t){
        t.two(1,2);
    }

   // (java 8 以后，不然会报错)
    public static void main(String[] args) {
        Lambda lambda = new Lambda();
        //Lambda表达式的代码块只有一句，可以省略花括号
        lambda.none(() -> System.out.println(""));
        //只有一个参数，可以省略圆括号
        lambda.one(one -> {
            System.out.println(one);
        });
        //只有一条语句，即使需要返回值，也可以省略return 关键字
        lambda.two((a,b) -> a+b);
    }


}

interface None {
    void none();
}

interface One {
    void one(String one);
}

interface Two {
    int two(int a, int b);
}
