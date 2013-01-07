/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesscomputing.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * Provides support for binding Function instances that use Jackson to transform data to or from serialized form
 */
public class JacksonSerializerBinder {
    private JacksonSerializerBinder() { }

    /**
     * Begin creating a new serialization binding
     * @param binder the Guice binder to attach to
     * @param type the type we will transform
     * @return a builder
     */
    public static <T> SerializerBinderBuilder<T> builderOf(Binder binder, final Class<T> type) {
        return new SerializerBinderBuilderImpl<T> (binder, new TypeReference<T>() {
            @Override
            public Type getType() {
                return type;
            }
        });
    }

    /**
     * Begin creating a new serialization binding
     * @param binder the Guice binder to attach to
     * @param type the type we will transform
     * @return a builder
     */
    public static <T> SerializerBinderBuilder<T> builderOf(Binder binder, TypeReference<T> type) {
        return new SerializerBinderBuilderImpl<T> (binder, type);
    }

    public static <T> SerializerBinderBuilder<T> builderOf(Binder binder, final TypeLiteral<T> literal) {
        final TypeReference<T> type = new TypeReference<T>() {
            @Override
            public Type getType() {
                return literal.getType();
            }
        };

        return new SerializerBinderBuilderImpl<T> (binder, type);
    }


    @SuppressWarnings("unchecked")
    public static <F, T> Key<Function<F, T>> keyFor(TypeReference<F> from, TypeReference<T> to, Class<? extends Annotation> annotation)
    {
        return (Key<Function<F, T>>)
                Key.get(Types.newParameterizedType(
                        Function.class,
                        from.getType(),
                        to.getType()), annotation);
    }
}
