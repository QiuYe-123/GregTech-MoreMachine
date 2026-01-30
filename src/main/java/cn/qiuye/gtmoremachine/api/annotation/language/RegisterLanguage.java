package cn.qiuye.gtmoremachine.api.annotation.language;

import kotlin.annotation.AnnotationTarget;
import kotlin.annotation.Target;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target(allowedTargets = AnnotationTarget.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterLanguage {

    String namePrefix() default "";

    String valuePrefix() default "";

    String key() default "";

    String en();

    String cn();
}
