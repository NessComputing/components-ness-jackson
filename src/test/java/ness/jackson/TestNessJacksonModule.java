package ness.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.nesscomputing.config.ConfigModule;

public class TestNessJacksonModule
{
    @Test
    public void testSimple()
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule());

        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);

        Assert.assertNotNull(mapper);
    }

    @Ignore // This isn't supported...
    @Test
    public void testSafeToMultiplyInject()
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule(),
            new AbstractModule() {
                @Override
                protected void configure() {
                    install(new NessJacksonModule());
                }
            });

        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);

        Assert.assertNotNull(mapper);
    }
}
