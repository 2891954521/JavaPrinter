package com.print.handler.annotation;

import java.lang.annotation.*;

/**
 * 使用这个功能需要有一个打印任务
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequirePrintTask{

}