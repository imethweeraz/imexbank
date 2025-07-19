package com.imeth.imexbank.security.annotations;

import com.imeth.imexbank.common.enums.UserRole;
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom security annotation to be used with an interceptor
 * for fine-grained method-level security.
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SecureMethod {
    UserRole[] value();
}