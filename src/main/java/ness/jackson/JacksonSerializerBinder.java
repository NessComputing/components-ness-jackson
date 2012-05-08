package ness.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.codehaus.jackson.type.TypeReference;

import com.google.common.base.Function;
import com.google.inject.Binder;
import com.google.inject.Key;
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
    public static <T> SerializerBinderBuilder<T> bindSerializer(Binder binder, final Class<T> type) {
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
    public static <T> SerializerBinderBuilder<T> bindSerializer(Binder binder, TypeReference<T> type) {
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
