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

package org.ziyao.data.format.datetime.standard;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.EnumMap;
import java.util.Map;

import org.ziyao.data.format.FormatterRegistrar;
import org.ziyao.data.format.FormatterRegistry;
import org.ziyao.data.format.annotation.DateTimeFormat.ISO;
import org.ziyao.data.format.datetime.standard.*;
import org.ziyao.data.format.datetime.standard.DateTimeConverters;
import org.ziyao.data.format.datetime.standard.DateTimeFormatterFactory;
import org.ziyao.data.format.datetime.standard.DurationFormatter;
import org.ziyao.data.format.datetime.standard.InstantFormatter;
import org.ziyao.data.format.datetime.standard.MonthDayFormatter;
import org.ziyao.data.format.datetime.standard.MonthFormatter;
import org.ziyao.data.format.datetime.standard.PeriodFormatter;
import org.ziyao.data.format.datetime.standard.TemporalAccessorParser;
import org.ziyao.data.format.datetime.standard.TemporalAccessorPrinter;
import org.ziyao.data.format.datetime.standard.YearFormatter;
import org.ziyao.data.format.datetime.standard.YearMonthFormatter;

/**
 * Configures the JSR-310 <code>java.time</code> formatting system for use with Spring.
 *
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 4.0
 * @see #setDateStyle
 * @see #setTimeStyle
 * @see #setDateTimeStyle
 * @see #setUseIsoFormat
 * @see FormatterRegistrar#registerFormatters
 * @see org.ziyao.data.format.datetime.DateFormatterRegistrar
 * @see org.ziyao.data.format.datetime.joda.DateTimeFormatterFactoryBean
 */
public class DateTimeFormatterRegistrar implements FormatterRegistrar {

	private enum Type {DATE, TIME, DATE_TIME}


	/**
	 * User-defined formatters.
	 */
	private final Map<Type, DateTimeFormatter> formatters = new EnumMap<>(Type.class);

	/**
	 * Factories used when specific formatters have not been specified.
	 */
	private final Map<Type, org.ziyao.data.format.datetime.standard.DateTimeFormatterFactory> factories = new EnumMap<>(Type.class);


	public DateTimeFormatterRegistrar() {
		for (Type type : Type.values()) {
			this.factories.put(type, new DateTimeFormatterFactory());
		}
	}


	/**
	 * Set whether standard ISO formatting should be applied to all date/time types.
	 * Default is "false" (no).
	 * <p>If set to "true", the "dateStyle", "timeStyle" and "dateTimeStyle"
	 * properties are effectively ignored.
	 */
	public void setUseIsoFormat(boolean useIsoFormat) {
		this.factories.get(Type.DATE).setIso(useIsoFormat ? ISO.DATE : ISO.NONE);
		this.factories.get(Type.TIME).setIso(useIsoFormat ? ISO.TIME : ISO.NONE);
		this.factories.get(Type.DATE_TIME).setIso(useIsoFormat ? ISO.DATE_TIME : ISO.NONE);
	}

	/**
	 * Set the default format style of {@link LocalDate} objects.
	 * Default is {@link FormatStyle#SHORT}.
	 */
	public void setDateStyle(FormatStyle dateStyle) {
		this.factories.get(Type.DATE).setDateStyle(dateStyle);
	}

	/**
	 * Set the default format style of {@link LocalTime} objects.
	 * Default is {@link FormatStyle#SHORT}.
	 */
	public void setTimeStyle(FormatStyle timeStyle) {
		this.factories.get(Type.TIME).setTimeStyle(timeStyle);
	}

	/**
	 * Set the default format style of {@link LocalDateTime} objects.
	 * Default is {@link FormatStyle#SHORT}.
	 */
	public void setDateTimeStyle(FormatStyle dateTimeStyle) {
		this.factories.get(Type.DATE_TIME).setDateTimeStyle(dateTimeStyle);
	}

	/**
	 * Set the formatter that will be used for objects representing date values.
	 * <p>This formatter will be used for the {@link LocalDate} type.
	 * When specified, the {@link #setDateStyle dateStyle} and
	 * {@link #setUseIsoFormat useIsoFormat} properties will be ignored.
	 * @param formatter the formatter to use
	 * @see #setTimeFormatter
	 * @see #setDateTimeFormatter
	 */
	public void setDateFormatter(DateTimeFormatter formatter) {
		this.formatters.put(Type.DATE, formatter);
	}

	/**
	 * Set the formatter that will be used for objects representing time values.
	 * <p>This formatter will be used for the {@link LocalTime} and {@link OffsetTime}
	 * types. When specified, the {@link #setTimeStyle timeStyle} and
	 * {@link #setUseIsoFormat useIsoFormat} properties will be ignored.
	 * @param formatter the formatter to use
	 * @see #setDateFormatter
	 * @see #setDateTimeFormatter
	 */
	public void setTimeFormatter(DateTimeFormatter formatter) {
		this.formatters.put(Type.TIME, formatter);
	}

	/**
	 * Set the formatter that will be used for objects representing date and time values.
	 * <p>This formatter will be used for {@link LocalDateTime}, {@link ZonedDateTime}
	 * and {@link OffsetDateTime} types. When specified, the
	 * {@link #setDateTimeStyle dateTimeStyle} and
	 * {@link #setUseIsoFormat useIsoFormat} properties will be ignored.
	 * @param formatter the formatter to use
	 * @see #setDateFormatter
	 * @see #setTimeFormatter
	 */
	public void setDateTimeFormatter(DateTimeFormatter formatter) {
		this.formatters.put(Type.DATE_TIME, formatter);
	}


	@Override
	public void registerFormatters(FormatterRegistry registry) {
		org.ziyao.data.format.datetime.standard.DateTimeConverters.registerConverters(registry);

		DateTimeFormatter df = getFormatter(Type.DATE);
		DateTimeFormatter tf = getFormatter(Type.TIME);
		DateTimeFormatter dtf = getFormatter(Type.DATE_TIME);

		// Efficient ISO_LOCAL_* variants for printing since they are twice as fast...

		registry.addFormatterForFieldType(LocalDate.class,
				new org.ziyao.data.format.datetime.standard.TemporalAccessorPrinter(
						df == DateTimeFormatter.ISO_DATE ? DateTimeFormatter.ISO_LOCAL_DATE : df),
				new org.ziyao.data.format.datetime.standard.TemporalAccessorParser(LocalDate.class, df));

		registry.addFormatterForFieldType(LocalTime.class,
				new org.ziyao.data.format.datetime.standard.TemporalAccessorPrinter(
						tf == DateTimeFormatter.ISO_TIME ? DateTimeFormatter.ISO_LOCAL_TIME : tf),
				new org.ziyao.data.format.datetime.standard.TemporalAccessorParser(LocalTime.class, tf));

		registry.addFormatterForFieldType(LocalDateTime.class,
				new org.ziyao.data.format.datetime.standard.TemporalAccessorPrinter(
						dtf == DateTimeFormatter.ISO_DATE_TIME ? DateTimeFormatter.ISO_LOCAL_DATE_TIME : dtf),
				new org.ziyao.data.format.datetime.standard.TemporalAccessorParser(LocalDateTime.class, dtf));

		registry.addFormatterForFieldType(ZonedDateTime.class,
				new org.ziyao.data.format.datetime.standard.TemporalAccessorPrinter(dtf),
				new org.ziyao.data.format.datetime.standard.TemporalAccessorParser(ZonedDateTime.class, dtf));

		registry.addFormatterForFieldType(OffsetDateTime.class,
				new org.ziyao.data.format.datetime.standard.TemporalAccessorPrinter(dtf),
				new org.ziyao.data.format.datetime.standard.TemporalAccessorParser(OffsetDateTime.class, dtf));

		registry.addFormatterForFieldType(OffsetTime.class,
				new TemporalAccessorPrinter(tf),
				new TemporalAccessorParser(OffsetTime.class, tf));

		registry.addFormatterForFieldType(Instant.class, new InstantFormatter());
		registry.addFormatterForFieldType(Period.class, new org.ziyao.data.format.datetime.standard.PeriodFormatter());
		registry.addFormatterForFieldType(Duration.class, new org.ziyao.data.format.datetime.standard.DurationFormatter());
		registry.addFormatterForFieldType(Year.class, new org.ziyao.data.format.datetime.standard.YearFormatter());
		registry.addFormatterForFieldType(Month.class, new org.ziyao.data.format.datetime.standard.MonthFormatter());
		registry.addFormatterForFieldType(YearMonth.class, new org.ziyao.data.format.datetime.standard.YearMonthFormatter());
		registry.addFormatterForFieldType(MonthDay.class, new org.ziyao.data.format.datetime.standard.MonthDayFormatter());

		registry.addFormatterForFieldAnnotation(new Jsr310DateTimeFormatAnnotationFormatterFactory());
	}

	private DateTimeFormatter getFormatter(Type type) {
		DateTimeFormatter formatter = this.formatters.get(type);
		if (formatter != null) {
			return formatter;
		}
		DateTimeFormatter fallbackFormatter = getFallbackFormatter(type);
		return this.factories.get(type).createDateTimeFormatter(fallbackFormatter);
	}

	private DateTimeFormatter getFallbackFormatter(Type type) {
		switch (type) {
			case DATE: return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
			case TIME: return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
			default: return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
		}
	}

}
