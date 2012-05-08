package ness.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.smile.SmileFactory;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.nesscomputing.config.ConfigProvider;

public class NessJacksonModule extends AbstractModule
{
    @Override
    public void configure()
    {
        // Annotated version (@Json) is also bound to json.
        bind(ObjectMapper.class).annotatedWith(Json.class).toProvider(NessObjectMapperProvider.class).in(Scopes.SINGLETON);

        // Annotated version (@Smile) is bound to the smile factory.
        bind(ObjectMapper.class).annotatedWith(Smile.class).toProvider(new NessObjectMapperProvider(new SmileFactory())).in(Scopes.SINGLETON);

        // Default (not annotated) instance is bound to json.
        bind(ObjectMapper.class).toProvider(NessObjectMapperProvider.class).in(Scopes.SINGLETON);

        bind(NessJacksonConfig.class).toProvider(ConfigProvider.of(NessJacksonConfig.class)).in(Scopes.SINGLETON);

        NessObjectMapperBinder.bindJacksonModule(binder()).toInstance(new GuavaModule());
    }
}
