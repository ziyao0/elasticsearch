package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.util.Currency;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
class StringToCurrencyConverter implements Converter<String, Currency> {

    @Override
    public Currency convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return Currency.getInstance(source);
    }

}
