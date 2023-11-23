package org.ziyao.data.convert.support;

import org.springframework.lang.Nullable;
import org.ziyao.data.convert.ConversionService;
import org.ziyao.data.convert.converter.ConverterRegistry;

import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
public class DefaultConversionService extends GenericConversionService {

    @Nullable
    private static volatile DefaultConversionService sharedInstance;


    /**
     * Create a new {@code DefaultConversionService} with the set of
     * {@linkplain DefaultConversionService#addDefaultConverters(ConverterRegistry) default converters}.
     */
    public DefaultConversionService() {
        addDefaultConverters(this);
    }


    /**
     * Return a shared default {@code ConversionService} instance,
     * lazily building it once needed.
     * <p><b>NOTE:</b> We highly recommend constructing individual
     * {@code ConversionService} instances for customization purposes.
     * This accessor is only meant as a fallback for code paths which
     * need simple type coercion but cannot access a longer-lived
     * {@code ConversionService} instance any other way.
     *
     * @return the shared {@code ConversionService} instance (never {@code null})
     * @since 4.3.5
     */
    public static ConversionService getSharedInstance() {
        DefaultConversionService cs = sharedInstance;
        if (cs == null) {
            synchronized (DefaultConversionService.class) {
                cs = sharedInstance;
                if (cs == null) {
                    cs = new DefaultConversionService();
                    sharedInstance = cs;
                }
            }
        }
        return cs;
    }

    /**
     * Add converters appropriate for most environments.
     *
     * @param converterRegistry the registry of converters to add to
     *                          (must also be castable to ConversionService, e.g. being a {@link ConfigurableConversionService})
     * @throws ClassCastException if the given ConverterRegistry could not be cast to a ConversionService
     */
    public static void addDefaultConverters(ConverterRegistry converterRegistry) {
        addScalarConverters(converterRegistry);
        addCollectionConverters(converterRegistry);

        converterRegistry.addConverter(new ByteBufferConverter((ConversionService) converterRegistry));
        converterRegistry.addConverter(new StringToTimeZoneConverter());
        converterRegistry.addConverter(new ZoneIdToTimeZoneConverter());
        converterRegistry.addConverter(new ZonedDateTimeToCalendarConverter());

        converterRegistry.addConverter(new ObjectToObjectConverter());
        converterRegistry.addConverter(new IdToEntityConverter((ConversionService) converterRegistry));
        converterRegistry.addConverter(new FallbackObjectToStringConverter());
        converterRegistry.addConverter(new ObjectToOptionalConverter((ConversionService) converterRegistry));
    }

    /**
     * Add common collection converters.
     *
     * @param converterRegistry the registry of converters to add to
     *                          (must also be castable to ConversionService, e.g. being a {@link ConfigurableConversionService})
     * @throws ClassCastException if the given ConverterRegistry could not be cast to a ConversionService
     * @since 4.2.3
     */
    public static void addCollectionConverters(ConverterRegistry converterRegistry) {
        ConversionService conversionService = (ConversionService) converterRegistry;

        converterRegistry.addConverter(new ArrayToCollectionConverter(conversionService));
        converterRegistry.addConverter(new CollectionToArrayConverter(conversionService));

        converterRegistry.addConverter(new ArrayToArrayConverter(conversionService));
        converterRegistry.addConverter(new CollectionToCollectionConverter(conversionService));
        converterRegistry.addConverter(new MapToMapConverter(conversionService));

        converterRegistry.addConverter(new ArrayToStringConverter(conversionService));
        converterRegistry.addConverter(new StringToArrayConverter(conversionService));

        converterRegistry.addConverter(new ArrayToObjectConverter(conversionService));
        converterRegistry.addConverter(new ObjectToArrayConverter(conversionService));

        converterRegistry.addConverter(new CollectionToStringConverter(conversionService));
        converterRegistry.addConverter(new StringToCollectionConverter(conversionService));

        converterRegistry.addConverter(new CollectionToObjectConverter(conversionService));
        converterRegistry.addConverter(new ObjectToCollectionConverter(conversionService));

        converterRegistry.addConverter(new StreamConverter(conversionService));
    }

    private static void addScalarConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverterFactory(new NumberToNumberConverterFactory());

        converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
        converterRegistry.addConverter(Number.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverter(new StringToCharacterConverter());
        converterRegistry.addConverter(Character.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverter(new NumberToCharacterConverter());
        converterRegistry.addConverterFactory(new CharacterToNumberFactory());

        converterRegistry.addConverter(new StringToBooleanConverter());
        converterRegistry.addConverter(Boolean.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverterFactory(new StringToEnumConverterFactory());
        converterRegistry.addConverter(new EnumToStringConverter((ConversionService) converterRegistry));

        converterRegistry.addConverterFactory(new IntegerToEnumConverterFactory());
        converterRegistry.addConverter(new EnumToIntegerConverter((ConversionService) converterRegistry));

        converterRegistry.addConverter(new StringToLocaleConverter());
        converterRegistry.addConverter(Locale.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverter(new StringToCharsetConverter());
        converterRegistry.addConverter(Charset.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverter(new StringToCurrencyConverter());
        converterRegistry.addConverter(Currency.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverter(new StringToPropertiesConverter());
        converterRegistry.addConverter(new PropertiesToStringConverter());

        converterRegistry.addConverter(new StringToUUIDConverter());
        converterRegistry.addConverter(UUID.class, String.class, new ObjectToStringConverter());
    }

}
