package com.print.convert.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SupportSuffixes{
	SupportSuffix[] value();
}
