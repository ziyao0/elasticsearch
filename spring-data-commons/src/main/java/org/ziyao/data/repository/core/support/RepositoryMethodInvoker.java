/*
 * Copyright 2020-2023 the original author or authors.
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
package org.ziyao.data.repository.core.support;

import org.springframework.core.KotlinDetector;
import org.springframework.lang.Nullable;
import org.ziyao.data.repository.core.support.RepositoryMethodInvocationListener.RepositoryMethodInvocation;
import org.ziyao.data.repository.core.support.RepositoryMethodInvocationListener.RepositoryMethodInvocationResult;
import org.ziyao.data.repository.core.support.RepositoryMethodInvocationListener.RepositoryMethodInvocationResult.State;
import org.ziyao.data.repository.query.RepositoryQuery;
import org.ziyao.data.util.KotlinReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * Invoker for repository methods. Used to invoke query methods and fragment methods. This invoker considers Kotlin
 * coroutine method adaption by either forwarding the entire call or by bridging invocations over a reactive
 * implementation.
 *
 * @author Mark Paluch
 * @author Christoph Strobl
 * @see #forFragmentMethod(Method, Object, Method)
 * @see #forRepositoryQuery(Method, RepositoryQuery)
 * @see RepositoryQuery
 * @see RepositoryComposition
 * @since 2.4
 */
abstract class RepositoryMethodInvoker {

    private final Method method;
    private final Class<?> returnedType;
    private final Invokable invokable;
    private final boolean suspendedDeclaredMethod;

    protected RepositoryMethodInvoker(Method method, Invokable invokable) {

        this.method = method;
        this.invokable = invokable;

        if (KotlinDetector.isKotlinReflectPresent()) {

            this.suspendedDeclaredMethod = KotlinReflectionUtils.isSuspend(method);
            this.returnedType = this.suspendedDeclaredMethod ? KotlinReflectionUtils.getReturnType(method)
                    : method.getReturnType();
        } else {

            this.suspendedDeclaredMethod = false;
            this.returnedType = method.getReturnType();
        }

    }

    static RepositoryQueryMethodInvoker forRepositoryQuery(Method declaredMethod, RepositoryQuery query) {
        return new RepositoryQueryMethodInvoker(declaredMethod, query);
    }

    /**
     * Create a {@link RepositoryMethodInvoker} to call a fragment {@link Method}.
     *
     * @param declaredMethod the declared repository method from the repository interface.
     * @param instance       fragment instance.
     * @param baseMethod     the base method to call on fragment {@code instance}.
     * @return {@link RepositoryMethodInvoker} to call a fragment {@link Method}.
     */
    static RepositoryMethodInvoker forFragmentMethod(Method declaredMethod, Object instance, Method baseMethod) {
        return new RepositoryFragmentMethodInvoker(declaredMethod, instance, baseMethod);
    }

    /**
     * Return whether the {@link Method declared method} can be adapted by calling {@link Method baseClassMethod}.
     *
     * @param declaredMethod  the declared repository method from the repository interface.
     * @param baseClassMethod the base method to call on fragment {@code instance}.
     * @return
     */
    public static boolean canInvoke(Method declaredMethod, Method baseClassMethod) {
        return RepositoryFragmentMethodInvoker.CoroutineAdapterInformation.create(declaredMethod, baseClassMethod)
                .canInvoke();
    }

    /**
     * Invoke the repository method and return its value.
     *
     * @param multicaster listener to notify about the call outcome.
     * @param args        invocation arguments.
     * @return
     * @throws Exception
     */
    @Nullable
    public Object invoke(Class<?> repositoryInterface, RepositoryInvocationMulticaster multicaster, Object[] args)
            throws Exception {
        return doInvoke(repositoryInterface, multicaster, args);
    }

    protected boolean shouldAdaptReactiveToSuspended() {
        return suspendedDeclaredMethod;
    }

    @Nullable
    private Object doInvoke(Class<?> repositoryInterface, RepositoryInvocationMulticaster multicaster, Object[] args)
            throws Exception {

        RepositoryMethodInvocationCaptor invocationResultCaptor = RepositoryMethodInvocationCaptor
                .captureInvocationOn(repositoryInterface);

        try {

            Object result = invokable.invoke(args);


            if (result instanceof Stream) {
                return ((Stream<?>) result).onClose(
                        () -> multicaster.notifyListeners(method, args, computeInvocationResult(invocationResultCaptor.success())));
            }

            multicaster.notifyListeners(method, args, computeInvocationResult(invocationResultCaptor.success()));

            return result;
        } catch (Exception e) {
            multicaster.notifyListeners(method, args, computeInvocationResult(invocationResultCaptor.error(e)));
            throw e;
        }
    }


    private RepositoryMethodInvocation computeInvocationResult(RepositoryMethodInvocationCaptor captured) {
        return new RepositoryMethodInvocation(captured.getRepositoryInterface(), method, captured.getCapturedResult(),
                captured.getDuration());
    }

    interface Invokable {

        @Nullable
        Object invoke(Object[] args) throws ReflectiveOperationException;
    }

    /**
     * Implementation to invoke query methods.
     */
    private static class RepositoryQueryMethodInvoker extends RepositoryMethodInvoker {
        public RepositoryQueryMethodInvoker(Method method, RepositoryQuery repositoryQuery) {
            super(method, repositoryQuery::execute);
        }
    }


    /**
     * Implementation to invoke fragment methods.
     */
    private static class RepositoryFragmentMethodInvoker extends RepositoryMethodInvoker {

        private final CoroutineAdapterInformation adapterInformation;

        public RepositoryFragmentMethodInvoker(Method declaredMethod, Object instance, Method baseClassMethod) {
            this(CoroutineAdapterInformation.create(declaredMethod, baseClassMethod), declaredMethod, instance,
                    baseClassMethod);
        }

        public RepositoryFragmentMethodInvoker(CoroutineAdapterInformation adapterInformation, Method declaredMethod,
                                               Object instance, Method baseClassMethod) {
            super(declaredMethod, args -> {

                if (adapterInformation.isAdapterMethod()) {

                    /*
                     * Kotlin suspended functions are invoked with a synthetic Continuation parameter that keeps track of the Coroutine context.
                     * We're invoking a method without Continuation as we expect the method to return any sort of reactive type,
                     * therefore we need to strip the Continuation parameter.
                     */
                    Object[] invocationArguments = new Object[args.length - 1];
                    System.arraycopy(args, 0, invocationArguments, 0, invocationArguments.length);

                    return baseClassMethod.invoke(instance, invocationArguments);
                }
                try {
                    Object invoke = baseClassMethod.invoke(instance, args);
                    return invoke;
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    cause.printStackTrace();
                    throw e;
                }
            });
            this.adapterInformation = adapterInformation;
        }

        @Override
        protected boolean shouldAdaptReactiveToSuspended() {
            return adapterInformation.shouldAdaptReactiveToSuspended();
        }

        /**
         * Value object capturing whether a suspended Kotlin method (Coroutine method) can be bridged with a native or
         * reactive fragment method.
         */
        static class CoroutineAdapterInformation {

            private final boolean suspendedDeclaredMethod;
            private final boolean suspendedBaseClassMethod;
            private final boolean reactiveBaseClassMethod;
            private final int declaredMethodParameterCount;
            private final int baseClassMethodParameterCount;

            private CoroutineAdapterInformation(boolean suspendedDeclaredMethod, boolean suspendedBaseClassMethod,
                                                boolean reactiveBaseClassMethod, int declaredMethodParameterCount, int baseClassMethodParameterCount) {
                this.suspendedDeclaredMethod = suspendedDeclaredMethod;
                this.suspendedBaseClassMethod = suspendedBaseClassMethod;
                this.reactiveBaseClassMethod = reactiveBaseClassMethod;
                this.declaredMethodParameterCount = declaredMethodParameterCount;
                this.baseClassMethodParameterCount = baseClassMethodParameterCount;
            }

            /**
             * Create {@link CoroutineAdapterInformation}.
             *
             * @param declaredMethod
             * @param baseClassMethod
             * @return
             */
            public static CoroutineAdapterInformation create(Method declaredMethod, Method baseClassMethod) {

                return new CoroutineAdapterInformation(false, false, false, declaredMethod.getParameterCount(),
                        baseClassMethod.getParameterCount());
            }

            boolean canInvoke() {

                if (suspendedDeclaredMethod == suspendedBaseClassMethod) {
                    return declaredMethodParameterCount == baseClassMethodParameterCount;
                }

                if (isAdapterMethod()) {
                    return declaredMethodParameterCount - 1 == baseClassMethodParameterCount;
                }

                return false;
            }

            boolean isAdapterMethod() {
                return suspendedDeclaredMethod && reactiveBaseClassMethod;
            }

            public boolean shouldAdaptReactiveToSuspended() {
                return suspendedDeclaredMethod && !suspendedBaseClassMethod && reactiveBaseClassMethod;
            }
        }
    }

    private static class RepositoryMethodInvocationCaptor {

        private final Class<?> repositoryInterface;
        private long startTime;
        private @Nullable Long endTime;
        private final State state;
        private final @Nullable Throwable error;

        protected RepositoryMethodInvocationCaptor(Class<?> repositoryInterface, long startTime, Long endTime, State state,
                                                   @Nullable Throwable exception) {

            this.repositoryInterface = repositoryInterface;
            this.startTime = startTime;
            this.endTime = endTime;
            this.state = state;
            this.error = exception instanceof InvocationTargetException ? exception.getCause() : exception;
        }

        public static RepositoryMethodInvocationCaptor captureInvocationOn(Class<?> repositoryInterface) {
            return new RepositoryMethodInvocationCaptor(repositoryInterface, System.nanoTime(), null, State.RUNNING, null);
        }

        public RepositoryMethodInvocationCaptor error(Throwable exception) {
            return new RepositoryMethodInvocationCaptor(repositoryInterface, startTime, System.nanoTime(), State.ERROR,
                    exception);
        }

        public RepositoryMethodInvocationCaptor success() {
            return new RepositoryMethodInvocationCaptor(repositoryInterface, startTime, System.nanoTime(), State.SUCCESS,
                    null);
        }

        public RepositoryMethodInvocationCaptor canceled() {
            return new RepositoryMethodInvocationCaptor(repositoryInterface, startTime, System.nanoTime(), State.CANCELED,
                    null);
        }

        Class<?> getRepositoryInterface() {
            return repositoryInterface;
        }

        void trackStart() {
            startTime = System.nanoTime();
        }

        public State getState() {
            return state;
        }

        @Nullable
        public Throwable getError() {
            return error;
        }

        long getDuration() {
            return (endTime != null ? endTime : System.nanoTime()) - startTime;
        }

        RepositoryMethodInvocationResult getCapturedResult() {
            return new RepositoryMethodInvocationResult() {

                @Override
                public State getState() {
                    return RepositoryMethodInvocationCaptor.this.getState();
                }

                @Nullable
                @Override
                public Throwable getError() {
                    return RepositoryMethodInvocationCaptor.this.getError();
                }
            };
        }

    }
}
