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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;
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
        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
                throws JsonMappingException
        {
            if (type.getRawClass().equals(Map.Entry.class)) {
                return new MapEntryDeserializer(type);
            }
            return null;
        }
    }

    private static class MapEntryDeserializer extends JsonDeserializer<Map.Entry<?, ?>>
    {
        private final JavaType type;

        MapEntryDeserializer(JavaType type)
        {
            this.type = type;
            Preconditions.checkArgument(type.containedTypeCount() == 2, "Map.Entry has exactly 2 child types");
        }

        @Override
        public Entry<?, ?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
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
                if ("key".equals(jp.getCurrentName())) {
                    k = deserK.deserialize(jp, ctxt);
                } else if ("value".equals(jp.getCurrentName())) {
                    v = deserV.deserialize(jp, ctxt);
                } else {
                    if (!ctxt.handleUnknownProperty(jp, this, Map.Entry.class, jp.getCurrentName())) {
                        throw new JsonMappingException("Unknown Map.Entry property " + jp.getCurrentName(), jp.getCurrentLocation());
                    }
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
