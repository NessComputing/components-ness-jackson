package ness.jackson;

import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

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
        featureMap.put(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Inject(optional=true)
    void injectConfig(final NessJacksonConfig config)
    {
        switch(config.getTimeFormat()) {
        case MILLIS:
            featureMap.put(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, Boolean.TRUE);
            break;
        case ISO8601:
            featureMap.put(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, Boolean.FALSE);
            break;
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
            else if (key instanceof SerializationConfig.Feature) {
                mapper.configure(((SerializationConfig.Feature) key), entry.getValue());
            }
            else if (key instanceof DeserializationConfig.Feature) {
                mapper.configure(((DeserializationConfig.Feature) key), entry.getValue());
            }
            else {
                throw new IllegalArgumentException("Can not configure ObjectMapper with " + key.name());
            }
        }

        for (Module module : modules) {
            mapper.registerModule(module);
        }
        // by default, don't serialize null values.
        mapper.setSerializationInclusion(Inclusion.NON_NULL);


        return mapper;
    }
}
