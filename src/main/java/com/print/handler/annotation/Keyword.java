package com.print.handler.annotation;

import java.lang.annotation.*;

/**
 * 用户发送消息的关键词
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Keyword{
	String value();
}
