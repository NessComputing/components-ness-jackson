package com.nesscomputing.jackson.datatype;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.common.collect.ImmutableSet;

import org.apache.commons.lang3.tuple.Pair;

class CommonsLang3Module extends Module
{
    private static final Set<String> KEY_NAMES = ImmutableSet.of("key", "left", "car");
    private static final Set<String> VALUE_NAMES = ImmutableSet.of("value", "right", "cdr");

    @Override
    public String getModuleName()
    {
        return "Ness Commons-Lang3 Jackson support";
    }

    @Override
    public Version version()
    {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context)
    {
        context.addSerializers(new Ser());
        context.addDeserializers(new Deser());
    }

    static class Ser extends Serializers.Base
    {
        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
        {
            if (Pair.class.isAssignableFrom(type.getRawClass())) {
                return new PairSerializer(config);
            }
            return null;
        }
    }

    static class Deser extends Deserializers.Base
    {
        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException
        {
            if (type.hasRawClass(Pair.class)) {
                return new PairDeserializer(type);
            }
            return null;
        }
    }

    static class PairSerializer extends JsonSerializer<Pair<?, ?>>
    {
        PairSerializer(SerializationConfig config)
        {
        }

        @Override
        public void serialize(Pair<?, ?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException
        {
            jgen.writeStartObject();
            jgen.writeObjectField("key", value.getKey());
            jgen.writeObjectField("value", value.getValue());
            jgen.writeEndObject();
        }
    }

    static class PairDeserializer extends JsonDeserializer<Pair<?, ?>>
    {
        private final JavaType type;

        public PairDeserializer(JavaType type)
        {
            this.type = type;
        }

        @Override
        public Pair<?, ?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
        {
            JsonDeserializer<Object> deserK = ctxt.findContextualValueDeserializer(type.containedType(0), null);
            JsonDeserializer<Object> deserV = ctxt.findContextualValueDeserializer(type.containedType(1), null);

            Object k = null, v = null;

            if (! (jp.hasCurrentToken() && jp.getCurrentToken() == JsonToken.START_OBJECT)) {
                expect(jp.nextToken(), JsonToken.START_OBJECT, jp, ctxt);
            }

            while (true) {
                JsonToken nextToken = jp.nextToken();
                if (nextToken == JsonToken.END_OBJECT) {
                    break;
                }
                expect(nextToken, JsonToken.FIELD_NAME, jp, ctxt);
                jp.nextToken();
                if (KEY_NAMES.contains(jp.getCurrentName())) {
                    if (k != null) {
                        throw new JsonMappingException("Multiple key properties for Pair", jp.getCurrentLocation());
                    }
                    k = deserK.deserialize(jp, ctxt);
                } else if (VALUE_NAMES.contains(jp.getCurrentName())) {
                    if (v != null) {
                        throw new JsonMappingException("Multiple value properties for Pair", jp.getCurrentLocation());
                    }
                    v = deserV.deserialize(jp, ctxt);
                } else {
                    if (!ctxt.handleUnknownProperty(jp, this, Map.Entry.class, jp.getCurrentName())) {
                        throw new JsonMappingException("Unknown Pair property " + jp.getCurrentName(), jp.getCurrentLocation());
                    }
                }
            }

            return Pair.of(k, v);
        }
    }

    private static void expect(JsonToken actual, JsonToken expected, JsonParser jp, DeserializationContext ctxt) throws JsonMappingException
    {
        if (actual != expected)
        {
            throw ctxt.wrongTokenException(jp, expected, "Wrong token");
        }
    }
}
