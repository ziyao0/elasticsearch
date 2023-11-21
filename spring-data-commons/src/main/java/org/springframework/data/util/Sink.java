package org.springframework.data.util;

import org.springframework.lang.Nullable;

import java.util.function.Consumer;

/**
 * A simple {@link Consumer} that captures the instance handed into it.
 *
 * @author Oliver Drotbohm
 * @since 2.4.12
 */
class Sink<T> implements Consumer<T> {

	private T value;

	/**
	 * Returns the value captured.
	 *
	 * @return
	 */
	public T getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	public void accept(@Nullable T t) {
		this.value = t;
	}
}
