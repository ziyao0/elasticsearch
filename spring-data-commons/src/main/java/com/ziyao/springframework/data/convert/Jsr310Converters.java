/*
 * Copyright 2013-2023 the original author or authors.
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
package com.ziyao.springframework.data.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

/**
 * Helper class to register JSR-310 specific {@link Converter} implementations in case the we're running on Java 8.
 *
 * @author Oliver Gierke
 * @author Barak Schoster
 * @author Christoph Strobl
 * @author Jens Schauder
 * @author Mark Paluch
 */
public abstract class Jsr310Converters {

	private static final List<Class<?>> CLASSES = Arrays.asList(LocalDateTime.class, LocalDate.class, LocalTime.class,
			Instant.class, ZoneId.class, Duration.class, Period.class);

	/**
	 * Returns the converters to be registered. Will only return converters in case we're running on Java 8.
	 *
	 * @return
	 */
	public static Collection<Converter<?, ?>> getConvertersToRegister() {

		List<Converter<?, ?>> converters = new ArrayList<>();
		converters.add(DateToLocalDateTimeConverter.INSTANCE);
		converters.add(LocalDateTimeToDateConverter.INSTANCE);
		converters.add(DateToLocalDateConverter.INSTANCE);
		converters.add(LocalDateToDateConverter.INSTANCE);
		converters.add(DateToLocalTimeConverter.INSTANCE);
		converters.add(LocalTimeToDateConverter.INSTANCE);
		converters.add(DateToInstantConverter.INSTANCE);
		converters.add(InstantToDateConverter.INSTANCE);
		converters.add(LocalDateTimeToInstantConverter.INSTANCE);
		converters.add(InstantToLocalDateTimeConverter.INSTANCE);
		converters.add(ZoneIdToStringConverter.INSTANCE);
		converters.add(StringToZoneIdConverter.INSTANCE);
		converters.add(DurationToStringConverter.INSTANCE);
		converters.add(StringToDurationConverter.INSTANCE);
		converters.add(PeriodToStringConverter.INSTANCE);
		converters.add(StringToPeriodConverter.INSTANCE);
		converters.add(StringToLocalDateConverter.INSTANCE);
		converters.add(StringToLocalDateTimeConverter.INSTANCE);
		converters.add(StringToInstantConverter.INSTANCE);

		return converters;
	}

	public static boolean supports(Class<?> type) {

		return CLASSES.contains(type);
	}

	@ReadingConverter
	public static enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {

		INSTANCE;

		@NonNull
		@Override
		public LocalDateTime convert(Date source) {
			return ofInstant(source.toInstant(), systemDefault());
		}
	}

	@WritingConverter
	public static enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {

		INSTANCE;

		@NonNull
		@Override
		public Date convert(LocalDateTime source) {
			return Date.from(source.atZone(systemDefault()).toInstant());
		}
	}

	@ReadingConverter
	public static enum DateToLocalDateConverter implements Converter<Date, LocalDate> {

		INSTANCE;

		@NonNull
		@Override
		public LocalDate convert(Date source) {
			return ofInstant(ofEpochMilli(source.getTime()), systemDefault()).toLocalDate();
		}
	}

	@WritingConverter
	public static enum LocalDateToDateConverter implements Converter<LocalDate, Date> {

		INSTANCE;

		@NonNull
		@Override
		public Date convert(LocalDate source) {
			return Date.from(source.atStartOfDay(systemDefault()).toInstant());
		}
	}

	@ReadingConverter
	public static enum DateToLocalTimeConverter implements Converter<Date, LocalTime> {

		INSTANCE;

		@NonNull
		@Override
		public LocalTime convert(Date source) {
			return ofInstant(ofEpochMilli(source.getTime()), systemDefault()).toLocalTime();
		}
	}

	@WritingConverter
	public static enum LocalTimeToDateConverter implements Converter<LocalTime, Date> {

		INSTANCE;

		@NonNull
		@Override
		public Date convert(LocalTime source) {
			return Date.from(source.atDate(LocalDate.now()).atZone(systemDefault()).toInstant());
		}
	}

	@ReadingConverter
	public static enum DateToInstantConverter implements Converter<Date, Instant> {

		INSTANCE;

		@NonNull
		@Override
		public Instant convert(Date source) {
			return source.toInstant();
		}
	}

	@WritingConverter
	public static enum InstantToDateConverter implements Converter<Instant, Date> {

		INSTANCE;

		@NonNull
		@Override
		public Date convert(Instant source) {
			return Date.from(source);
		}
	}

	@ReadingConverter
	public static enum LocalDateTimeToInstantConverter implements Converter<LocalDateTime, Instant> {

		INSTANCE;

		@NonNull
		@Override
		public Instant convert(LocalDateTime source) {
			return source.atZone(systemDefault()).toInstant();
		}
	}

	@ReadingConverter
	public static enum InstantToLocalDateTimeConverter implements Converter<Instant, LocalDateTime> {

		INSTANCE;

		@NonNull
		@Override
		public LocalDateTime convert(Instant source) {
			return LocalDateTime.ofInstant(source, systemDefault());
		}
	}

	@WritingConverter
	public static enum ZoneIdToStringConverter implements Converter<ZoneId, String> {

		INSTANCE;

		@NonNull
		@Override
		public String convert(ZoneId source) {
			return source.toString();
		}
	}

	@ReadingConverter
	public static enum StringToZoneIdConverter implements Converter<String, ZoneId> {

		INSTANCE;

		@NonNull
		@Override
		public ZoneId convert(String source) {
			return ZoneId.of(source);
		}
	}

	@WritingConverter
	public static enum DurationToStringConverter implements Converter<Duration, String> {

		INSTANCE;

		@NonNull
		@Override
		public String convert(Duration duration) {
			return duration.toString();
		}
	}

	@ReadingConverter
	public static enum StringToDurationConverter implements Converter<String, Duration> {

		INSTANCE;

		@NonNull
		@Override
		public Duration convert(String s) {
			return Duration.parse(s);
		}
	}

	@WritingConverter
	public static enum PeriodToStringConverter implements Converter<Period, String> {

		INSTANCE;

		@NonNull
		@Override
		public String convert(Period period) {
			return period.toString();
		}
	}

	@ReadingConverter
	public static enum StringToPeriodConverter implements Converter<String, Period> {

		INSTANCE;

		@NonNull
		@Override
		public Period convert(String s) {
			return Period.parse(s);
		}
	}

	@ReadingConverter
	public static enum StringToLocalDateConverter implements Converter<String, LocalDate> {

		INSTANCE;

		/*
		 * (non-Javadoc)
		 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
		 */
		@NonNull
		@Override
		public LocalDate convert(String source) {
			return LocalDate.parse(source, DateTimeFormatter.ISO_DATE);
		}
	}

	@ReadingConverter
	public static enum StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

		INSTANCE;

		/*
		 * (non-Javadoc)
		 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
		 */
		@NonNull
		@Override
		public LocalDateTime convert(String source) {
			return LocalDateTime.parse(source, DateTimeFormatter.ISO_DATE_TIME);
		}
	}

	@ReadingConverter
	public static enum StringToInstantConverter implements Converter<String, Instant> {

		INSTANCE;

		/*
		 * (non-Javadoc)
		 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
		 */
		@NonNull
		@Override
		public Instant convert(String source) {
			return Instant.parse(source);
		}
	}
}