/**
 * Central domain abstractions especially to be used in combination with the
 * {@link com.ziyao.springframework.data.repository.Repository} abstraction.
 *
 * @see com.ziyao.springframework.data.repository.Repository
 */
@XmlSchema(
        xmlns = {@XmlNs(prefix = "sd", namespaceURI = com.ziyao.springframework.data.domain.jaxb.SpringDataJaxb.NAMESPACE)})
@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(value = com.ziyao.springframework.data.domain.jaxb.PageableAdapter.class, type = Pageable.class),
        @XmlJavaTypeAdapter(value = com.ziyao.springframework.data.domain.jaxb.SortAdapter.class, type = Sort.class),
        @XmlJavaTypeAdapter(value = com.ziyao.springframework.data.domain.jaxb.PageAdapter.class, type = Page.class)})
@org.springframework.lang.NonNullApi
package com.ziyao.springframework.data.domain.jaxb;

import com.ziyao.springframework.data.domain.Page;
import com.ziyao.springframework.data.domain.Pageable;
import com.ziyao.springframework.data.domain.Sort;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
