package com.print.convert.annotation;

import java.lang.annotation.*;

/**
 * 转换器支持的后缀名
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SupportSuffixes.class)
public @interface SupportSuffix{
	String value() default "";
}