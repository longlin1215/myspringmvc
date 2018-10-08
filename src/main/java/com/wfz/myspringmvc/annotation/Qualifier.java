package com.wfz.myspringmvc.annotation;
import java.lang.annotation.*;

/**
 * Created with IDEA
 * author:weifuzhi
 * Date:2018/10/7
 * Time:16:36
 *
 * Qualifier 注解
 **/
@Documented//JavaDoc
@Target(ElementType.FIELD)//作用于类上
@Retention(RetentionPolicy.RUNTIME)//限制注解的生命周期，当前定义为运行时
public @interface Qualifier {

    /**
     * 作用于该类上的注解有一个value属性，Qualifier名称
     * @return
     */
    public  String value();
}
