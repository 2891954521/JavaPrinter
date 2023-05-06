package com.print.handler.annotation;

import java.lang.annotation.*;

/**
 * 一个功能要生效需要的设置项
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequireSetting{
	String value();
}
