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
package ness.jackson;

import static ness.jackson.JacksonSerializerBinder.keyFor;

import java.lang.annotation.Annotation;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.util.Types;
import com.nesscomputing.callback.Callback;

class SerializerBinderBuilderImpl<T> implements SerializerBinderBuilder<T> {
    private static final TypeReference<byte[]> BYTEA_TYPE = new TypeReference<byte[]>() {};
    private static final TypeReference<String> STRING_TYPE = new TypeReference<String>() {};

    private final Binder binder;
    private final TypeReference<T> type;
    private Callback<Throwable> action = new Callback<Throwable>() {
        @Override
        public void call(Throwable item) throws Exception {
            throw Throwables.propagate(item);
        }
    };


    SerializerBinderBuilderImpl(Binder binder, TypeReference<T> type) {
        this.binder = binder;
        this.type = type;
    }

    // CONFIGURATION

    @Override
    public SerializerBinderBuilder<T> onError(Callback<Throwable> action) {
        this.action = action;
        return this;
    }

    // END CONFIGURATION.  BEGIN BINDING

    private void buildSerializer() {
        binder.install(new AbstractModule() {
            @Override
            protected void configure() {
                if (String.class != type.getType())
                {
                    bindSerializers(Json.class, Smile.class);
                }
                bindSerializers(JsonSerializer.class, SmileSerializer.class);
            }

            @SuppressWarnings("unchecked")
            private void bindSerializers(Class<? extends Annotation> jsonSerializerAnnotation, Class<? extends Annotation> smileSerializerAnnotation)
            {
                // T -> String
                SerializerProvider<T, String> stringProvider =
                        new StringSerializerProvider<T>(action);

                // T -> byte[]
                SerializerProvider<T, byte[]> bytesProviderSmile = new SmileSerializerProvider<T>(action);
                SerializerProvider<T, byte[]> bytesProviderJson = new JsonBytesSerializerProvider<T>(action);

                // @Json T -> String
                bind (keyFor(type, STRING_TYPE, jsonSerializerAnnotation))
                    .toProvider(stringProvider).in(Scopes.SINGLETON);

                // @Json ? super T -> String
                bind ((Key<Function<? super T, String>>)
                        Key.get(Types.newParameterizedType(
                                Function.class,
                                Types.supertypeOf(type.getType()),
                                String.class), jsonSerializerAnnotation))
                    .toProvider(stringProvider).in(Scopes.SINGLETON);

                // @Json T -> byte[]
                bind (keyFor(type, BYTEA_TYPE, jsonSerializerAnnotation))
                    .toProvider(bytesProviderJson).in(Scopes.SINGLETON);

                // @Json ? super T -> byte[]
                bind ((Key<Function<? super T, byte[]>>)
                        Key.get(Types.newParameterizedType(
                                Function.class,
                                Types.supertypeOf(type.getType()),
                                byte[].class), jsonSerializerAnnotation))
                    .toProvider(bytesProviderJson).in(Scopes.SINGLETON);

                // @Smile T -> byte[]
                bind (keyFor(type, BYTEA_TYPE, smileSerializerAnnotation))
                    .toProvider(bytesProviderSmile).in(Scopes.SINGLETON);

                // @Smile ? super T -> byte[]
                bind ((Key<Function<? super T, byte[]>>)
                        Key.get(Types.newParameterizedType(
                                Function.class,
                                Types.supertypeOf(type.getType()),
                                byte[].class), smileSerializerAnnotation))
                    .toProvider(bytesProviderSmile).in(Scopes.SINGLETON);
            }
        });
    }

    private void buildDeserializer() {
        binder.install(new AbstractModule() {
            @Override
            protected void configure() {
                if (String.class != type.getType())
                {
                    bindDeserializers(Json.class, Smile.class, false);
                }
                bindDeserializers(JsonDeserializer.class, SmileDeserializer.class, true);
            }

            @SuppressWarnings("unchecked")
            private void bindDeserializers(Class<? extends Annotation> jsonDeserializerAnnotation, Class<? extends Annotation> smileDeserializerAnnotation, boolean bindAuto)
            {
                // String -> T
                SerializerProvider<String, T> stringProvider = new StringDeserializerProvider<T>(type, action);

                // byte[] -> T
                SerializerProvider<byte[], T> bytesProviderSmile = new SmileDeserializerProvider<T>(type, action);
                SerializerProvider<byte[], T> bytesProviderJson = new JsonBytesDeserializerProvider<T>(type, action);
                Provider<Function<byte[], T>> bytesProviderAuto = new AutodetectDeserializerProvider<T>(bytesProviderJson, bytesProviderSmile);

                // @Json String -> T
                bind (keyFor(STRING_TYPE, type, jsonDeserializerAnnotation))
                    .toProvider(stringProvider).in(Scopes.SINGLETON);

                // @Json String -> ? extends T
                bind ((Key<Function<String, ? extends T>>)
                        Key.get(Types.newParameterizedType(
                                Function.class,
                                String.class,
                                Types.subtypeOf(type.getType()), jsonDeserializerAnnotation)))
                    .toProvider(stringProvider).in(Scopes.SINGLETON);

                // @Json byte[] -> T
                bind (keyFor(BYTEA_TYPE, type, jsonDeserializerAnnotation))
                    .toProvider(bytesProviderJson).in(Scopes.SINGLETON);

                // @Json byte[] -> ? extends T
                bind ((Key<Function<byte[], ? extends T>>)
                        Key.get(Types.newParameterizedType(
                                Function.class,
                                byte[].class,
                                Types.subtypeOf(type.getType())), jsonDeserializerAnnotation))
                    .toProvider(bytesProviderJson).in(Scopes.SINGLETON);

                // @Smile byte[] -> T
                bind (keyFor(BYTEA_TYPE, type, smileDeserializerAnnotation))
                    .toProvider(bytesProviderSmile).in(Scopes.SINGLETON);

                // @Smile byte[] -> ? extends T
                bind ((Key<Function<byte[], ? extends T>>)
                        Key.get(Types.newParameterizedType(
                                Function.class,
                                byte[].class,
                                Types.subtypeOf(type.getType())), smileDeserializerAnnotation))
                    .toProvider(bytesProviderSmile).in(Scopes.SINGLETON);

                if (!bindAuto)
                {
                    return;
                }

                // @Autodetect byte[] -> T
                bind (keyFor(BYTEA_TYPE, type, JsonAutodetectDeserializer.class))
                    .toProvider(bytesProviderAuto).in(Scopes.SINGLETON);

                // @Autodetect byte[] -> ? extends T
                bind ((Key<Function<byte[], ? extends T>>)
                        Key.get(Types.newParameterizedType(
                                Function.class,
                                byte[].class,
                                Types.subtypeOf(type.getType())), JsonAutodetectDeserializer.class))
                    .toProvider(bytesProviderAuto).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    public void build() {
        buildSerializer();
        buildDeserializer();
    }

    // END BINDING.

    /** Handles error handling logic */
    abstract static class SerializerProvider<In, Out> implements Provider<Function<In, Out>> {

        private final Callback<Throwable> action;

        SerializerProvider(Callback<Throwable> action) {
            this.action = action;
        }

        protected abstract Out serialize(In input) throws Exception;

        @Override
        public Function<In, Out> get() {
            return new Function<In, Out>() {
                @Override
                public Out apply(In input) {
                    try {
                        return serialize(input);
                    } catch (Exception e) {
                        try {
                            action.call(e);
                        } catch (Exception e1) {
                            throw Throwables.propagate(e1);
                        }
                        return null;
                    }
                }
            };
        }
    }

    static class StringSerializerProvider<T> extends SerializerProvider<T, String> {

        StringSerializerProvider(Callback<Throwable> action) {
            super(action);
        }

        private ObjectMapper mapper;

        @Inject
        void setObjectMapper(@Json ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        protected String serialize(T input) throws Exception {
            return mapper.writeValueAsString(input);
        }
    }

    static class SmileSerializerProvider<T> extends SerializerProvider<T, byte[]> {

        SmileSerializerProvider(Callback<Throwable> action) {
            super(action);
        }

        private ObjectMapper mapper;

        @Inject
        void setObjectMapper(@Smile ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        protected byte[] serialize(T input) throws Exception {
            return mapper.writeValueAsBytes(input);
        }
    }

    static class JsonBytesSerializerProvider<T> extends SerializerProvider<T, byte[]> {

        JsonBytesSerializerProvider(Callback<Throwable> action) {
            super(action);
        }

        private ObjectMapper mapper;

        @Inject
        void setObjectMapper(@Json ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        protected byte[] serialize(T input) throws Exception {
            return mapper.writeValueAsBytes(input);
        }
    }

    static class StringDeserializerProvider<T> extends SerializerProvider<String, T> {

        private final TypeReference<T> type;

        StringDeserializerProvider(TypeReference<T> type, Callback<Throwable> action) {
            super(action);
            this.type = type;
        }

        private ObjectMapper mapper;

        @Inject
        void setObjectMapper(@Json ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        protected T serialize(String input) throws Exception {
            return mapper.readValue(input, type);
        }
    }

    static class SmileDeserializerProvider<T> extends SerializerProvider<byte[], T> {

        private final TypeReference<T> type;

        SmileDeserializerProvider(TypeReference<T> type, Callback<Throwable> action) {
            super(action);
            this.type = type;
        }

        private ObjectMapper mapper;

        @Inject
        void setObjectMapper(@Smile ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        protected T serialize(byte[] input) throws Exception {
            return mapper.readValue(input, type);
        }
    }

    static class JsonBytesDeserializerProvider<T> extends SerializerProvider<byte[], T> {

        private final TypeReference<T> type;

        JsonBytesDeserializerProvider(TypeReference<T> type, Callback<Throwable> action) {
            super(action);
            this.type = type;
        }

        private ObjectMapper mapper;

        @Inject
        void setObjectMapper(@Json ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        protected T serialize(byte[] input) throws Exception {
            return mapper.readValue(input, type);
        }
    }

    static class AutodetectDeserializerProvider<T> implements Provider<Function<byte[], T>>, Function<byte[], T> {

        private final Function<byte[], T> bytesProviderJson;
        private final Function<byte[], T> bytesProviderSmile;

        AutodetectDeserializerProvider(
                SerializerProvider<byte[], T> bytesProviderJson,
                SerializerProvider<byte[], T> bytesProviderSmile)
        {
            this.bytesProviderJson = bytesProviderJson.get();
            this.bytesProviderSmile = bytesProviderSmile.get();
        }

        @Override
        public Function<byte[], T> get()
        {
            return this;
        }

        @Override
        public T apply(byte[] input)
        {
            if (input.length > 0 && input[0] == ':')
            {
                return bytesProviderSmile.apply(input);
            }
            return bytesProviderJson.apply(input);
        }
    }
}
