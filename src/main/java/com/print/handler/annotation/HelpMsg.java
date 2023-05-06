package com.print.handler.annotation;

import java.lang.annotation.*;

/**
 * 帮助信息
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HelpMsg{
	String value() default "";
}
