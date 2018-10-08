package com.wfz.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * Created with IDEA
 * author:weifuzhi
 * Date:2018/10/7
 * Time:16:36
 *
 * 地址映射处理 注解
 **/
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})//该注解应用于方法和类
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    public  String value();
}
