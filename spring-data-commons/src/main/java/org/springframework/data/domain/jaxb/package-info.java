/**
 * Central domain abstractions especially to be used in combination with the
 * {@link org.springframework.data.repository.Repository} abstraction.
 *
 * @see org.springframework.data.repository.Repository
 */
@XmlSchema(
        xmlns = {@XmlNs(prefix = "sd", namespaceURI = SpringDataJaxb.NAMESPACE)})
@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(value = PageableAdapter.class, type = Pageable.class),
        @XmlJavaTypeAdapter(value = SortAdapter.class, type = Sort.class),
        @XmlJavaTypeAdapter(value = PageAdapter.class, type = Page.class)})
@org.springframework.lang.NonNullApi
package org.springframework.data.domain.jaxb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
