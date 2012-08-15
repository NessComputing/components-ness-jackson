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

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.type.JavaType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

class MapEntryModule extends Module
{
    @Override
    public String getModuleName()
    {
        return "Map.Entry module";
    }

    @Override
    public Version version()
    {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context)
    {
        context.addDeserializers(new MapEntryDeserializers());
    }

    private static class MapEntryDeserializers extends Deserializers.Base {
        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
                DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property)
                throws JsonMappingException
        {
            if (type.getRawClass().equals(Map.Entry.class)) {
                return new MapEntryDeserializer(type, property);
            }
            return null;
        }
    }

    private static class MapEntryDeserializer extends JsonDeserializer<Map.Entry<?, ?>>
    {
        private JavaType type;
        private BeanProperty property;

        MapEntryDeserializer(JavaType type, BeanProperty property)
        {
            this.property = property;
            this.type = type;
            Preconditions.checkArgument(type.containedTypeCount() == 2, "Map.Entry has exactly 2 child types");
        }

        @Override
        public Entry<?, ?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
        {
            DeserializerProvider provider = ctxt.getDeserializerProvider();
            JsonDeserializer<Object> deserK = provider.findValueDeserializer(ctxt.getConfig(), type.containedType(0), property);
            JsonDeserializer<Object> deserV = provider.findValueDeserializer(ctxt.getConfig(), type.containedType(1), property);

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
                if ("key".equals(jp.getCurrentName())) {
                    k = deserK.deserialize(jp, ctxt);
                } else if ("value".equals(jp.getCurrentName())) {
                    v = deserV.deserialize(jp, ctxt);
                } else {
                    throw ctxt.unknownFieldException(Map.Entry.class, jp.getCurrentName());
                }
            }


            return Maps.immutableEntry(k, v);
        }

        private void expect(JsonToken actual, JsonToken expected, JsonParser jp, DeserializationContext ctxt) throws JsonMappingException
        {
            if (actual != expected)
            {
                throw ctxt.wrongTokenException(jp, expected, "Wrong token");
            }
        }
    }
}
