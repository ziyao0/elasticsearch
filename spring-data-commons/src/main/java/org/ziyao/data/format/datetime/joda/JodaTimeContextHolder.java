/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ziyao.data.format.datetime.joda;

import java.util.Locale;

import org.joda.time.format.DateTimeFormatter;

import org.springframework.core.NamedThreadLocal;
import org.ziyao.data.format.datetime.joda.JodaTimeContext;
import org.springframework.lang.Nullable;

/**
 * A holder for a thread-local {@link org.ziyao.data.format.datetime.joda.JodaTimeContext}
 * with user-specific Joda-Time settings.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.context.i18n.LocaleContextHolder
 * @deprecated as of 5.3, in favor of standard JSR-310 support
 */
@Deprecated
public final class JodaTimeContextHolder {

	private static final ThreadLocal<org.ziyao.data.format.datetime.joda.JodaTimeContext> jodaTimeContextHolder =
			new NamedThreadLocal<>("JodaTimeContext");


	private JodaTimeContextHolder() {
	}


	/**
	 * Reset the JodaTimeContext for the current thread.
	 */
	public static void resetJodaTimeContext() {
		jodaTimeContextHolder.remove();
	}

	/**
	 * Associate the given JodaTimeContext with the current thread.
	 * @param jodaTimeContext the current JodaTimeContext,
	 * or {@code null} to reset the thread-bound context
	 */
	public static void setJodaTimeContext(@Nullable org.ziyao.data.format.datetime.joda.JodaTimeContext jodaTimeContext) {
		if (jodaTimeContext == null) {
			resetJodaTimeContext();
		}
		else {
			jodaTimeContextHolder.set(jodaTimeContext);
		}
	}

	/**
	 * Return the JodaTimeContext associated with the current thread, if any.
	 * @return the current JodaTimeContext, or {@code null} if none
	 */
	@Nullable
	public static org.ziyao.data.format.datetime.joda.JodaTimeContext getJodaTimeContext() {
		return jodaTimeContextHolder.get();
	}


	/**
	 * Obtain a DateTimeFormatter with user-specific settings applied to the given base Formatter.
	 * @param formatter the base formatter that establishes default formatting rules
	 * (generally user independent)
	 * @param locale the current user locale (may be {@code null} if not known)
	 * @return the user-specific DateTimeFormatter
	 */
	public static DateTimeFormatter getFormatter(DateTimeFormatter formatter, @Nullable Locale locale) {
		DateTimeFormatter formatterToUse = (locale != null ? formatter.withLocale(locale) : formatter);
		JodaTimeContext context = getJodaTimeContext();
		return (context != null ? context.getFormatter(formatterToUse) : formatterToUse);
	}

}
