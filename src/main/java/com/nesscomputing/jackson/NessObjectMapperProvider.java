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

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * Provider for Jackson {@link ObjectMapper}. This allows registration of additional serializers and deserializers from
 * other pieces of the platform using multibinders.  You may also register AbstractTypeResolvers via a simple binding.
 */
class NessObjectMapperProvider implements Provider<ObjectMapper>
{
    private final Map<Enum<?>, Boolean> featureMap = Maps.newHashMap();
    private final Set<Module> modules = Sets.newHashSet();

    private final JsonFactory jsonFactory;

    @Inject
    NessObjectMapperProvider()
    {
        this(null);
    }

    NessObjectMapperProvider(final JsonFactory jsonFactory)
    {
        this.jsonFactory = jsonFactory;

        // This needs to be set, otherwise the mapper will fail on every new property showing up.
        featureMap.put(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Don't write out nulls by default -- if you really want them, you can change it with setOptions later.
        featureMap.put(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

        // No need to flush after every value, which cuts throughput by ~30%
        featureMap.put(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false);

        // Awful JAXB shit
        featureMap.put(MapperFeature.USE_GETTERS_AS_SETTERS, false);
    }

    @Inject(optional=true)
    void injectConfig(final NessJacksonConfig config)
    {
        switch(config.getTimeFormat()) {
        case MILLIS:
            featureMap.put(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, Boolean.TRUE);
            break;
        case ISO8601:
            featureMap.put(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, Boolean.FALSE);
            break;
        default:
            throw new IllegalStateException("Unknown time format: " + config.getTimeFormat());
        }
    }

    @Inject(optional=true)
    void setSpecificSerializers(@Named(NessObjectMapperBinder.JACKSON_NAME) final Set<Module> modules)
    {
        this.modules.addAll(modules);
    }

    @Inject(optional=true)
    void setOptions(@Named(NessObjectMapperBinder.JACKSON_NAME) final Map<Enum<?>, Boolean> options)
    {
        this.featureMap.putAll(options);
    }

    @Override
    public ObjectMapper get()
    {
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);

        // Set the features
        for (Map.Entry<Enum<?>, Boolean> entry : featureMap.entrySet()) {
            final Enum<?> key = entry.getKey();

            if (key instanceof JsonGenerator.Feature) {
                mapper.configure(((JsonGenerator.Feature) key), entry.getValue());
            }
            else if (key instanceof JsonParser.Feature) {
                mapper.configure(((JsonParser.Feature) key), entry.getValue());
            }
            else if (key instanceof SerializationFeature) {
                mapper.configure(((SerializationFeature) key), entry.getValue());
            }
            else if (key instanceof DeserializationFeature) {
                mapper.configure(((DeserializationFeature) key), entry.getValue());
            } else if (key instanceof MapperFeature) {
                mapper.configure(((MapperFeature) key), entry.getValue());
            }
            else {
                throw new IllegalArgumentException("Can not configure ObjectMapper with " + key.name());
            }
        }

        for (Module module : modules) {
            mapper.registerModule(module);
        }
        // by default, don't serialize null values.
        mapper.setSerializationInclusion(Include.NON_NULL);


        return mapper;
    }
}
