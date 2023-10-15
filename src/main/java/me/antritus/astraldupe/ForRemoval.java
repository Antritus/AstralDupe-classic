package me.antritus.astraldupe;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a feature going to be removed.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface ForRemoval {

	String reason();
}
