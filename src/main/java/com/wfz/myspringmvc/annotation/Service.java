package com.wfz.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * Created with IDEA
 * author:weifuzhi
 * Date:2018/10/7
 * Time:16:36
 *
 * Service 注解
 **/
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    public  String value();
}
