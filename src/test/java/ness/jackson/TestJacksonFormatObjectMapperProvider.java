package ness.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import com.nesscomputing.config.ConfigModule;

public class TestJacksonFormatObjectMapperProvider
{
    @Inject @Json
    private ObjectMapper jsonMapper;

    @Inject @Smile
    private ObjectMapper smileMapper;

    @Inject
    private ObjectMapper defaultMapper;

    @Test
    public void testBasics()
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule());
        injector.injectMembers(this);
        Assert.assertNotNull(defaultMapper);
        Assert.assertNotNull(smileMapper);
        Assert.assertNotNull(jsonMapper);
        Assert.assertNotSame(defaultMapper, jsonMapper);
        Assert.assertNotSame(defaultMapper, smileMapper);
        Assert.assertNotSame(smileMapper, jsonMapper);
    }

    @Test
    public void testDefault()
    {
        final JacksonFormatObjectMapperProvider defaultProvider = new JacksonFormatObjectMapperProvider(new JacksonFormatConfig() {});

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule(),
                                                       new AbstractModule() {
                                                           @Override
                                                           public void configure()
                                                           {
                                                               bind(ObjectMapper.class).annotatedWith(Names.named("default")).toProvider(defaultProvider).in(Scopes.SINGLETON);
                                                           }
                                                       });

        injector.injectMembers(this);
        final ObjectMapper defaultMapper = injector.getInstance(Key.get(ObjectMapper.class, Names.named("default")));

        Assert.assertNotNull(defaultMapper);
        Assert.assertSame(jsonMapper, defaultMapper);
        Assert.assertNotSame(smileMapper, defaultMapper);
    }

    @Test
    public void testJson()
    {
        final JacksonFormatObjectMapperProvider defaultProvider = new JacksonFormatObjectMapperProvider(new JacksonFormatConfig() {
            @Override
            public DataFormat getDataFormat()
            {
                return DataFormat.JSON;
            }
        });

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule(),
                                                       new AbstractModule() {
                                                           @Override
                                                           public void configure()
                                                           {
                                                               bind(ObjectMapper.class).annotatedWith(Names.named("default")).toProvider(defaultProvider).in(Scopes.SINGLETON);
                                                           }
                                                       });

        injector.injectMembers(this);
        final ObjectMapper defaultMapper = injector.getInstance(Key.get(ObjectMapper.class, Names.named("default")));

        Assert.assertNotNull(defaultMapper);
        Assert.assertSame(jsonMapper, defaultMapper);
        Assert.assertNotSame(smileMapper, defaultMapper);
    }

    @Test
    public void testSmile()
    {
        final JacksonFormatObjectMapperProvider defaultProvider = new JacksonFormatObjectMapperProvider(new JacksonFormatConfig() {
            @Override
            public DataFormat getDataFormat()
            {
                return DataFormat.SMILE;
            }
        });

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule(),
                                                       new AbstractModule() {
                                                           @Override
                                                           public void configure()
                                                           {
                                                               bind(ObjectMapper.class).annotatedWith(Names.named("default")).toProvider(defaultProvider).in(Scopes.SINGLETON);
                                                           }
                                                       });

        injector.injectMembers(this);
        final ObjectMapper defaultMapper = injector.getInstance(Key.get(ObjectMapper.class, Names.named("default")));

        Assert.assertNotNull(defaultMapper);
        Assert.assertNotSame(jsonMapper, defaultMapper);
        Assert.assertSame(smileMapper, defaultMapper);
    }
}

