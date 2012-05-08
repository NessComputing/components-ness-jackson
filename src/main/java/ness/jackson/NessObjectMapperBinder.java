package ness.jackson;

import org.codehaus.jackson.map.Module;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;


public final class NessObjectMapperBinder
{
    public static final String JACKSON_NAME = "_jackson";
    public static final Named JACKSON_NAMED = Names.named(JACKSON_NAME);

    private NessObjectMapperBinder()
    {
    }

    /**
     * Bind a Jackson module to the object mapper.
     */
    public static LinkedBindingBuilder<Module> bindJacksonModule(final Binder binder)
    {
        final Multibinder<Module> moduleBinder = Multibinder.newSetBinder(binder, Module.class, JACKSON_NAMED);
        return moduleBinder.addBinding();
    }

    /**
     * Set a Jackson feature on the Object Mapper.
     *
     * @see {@link JsonGenerator.Feature}, {@link JsonParser.Feature}, {@link SerializationConfig.Feature} and {@link DeserializationConfig.Feature} for available features.
     */
    public static LinkedBindingBuilder<Boolean> bindJacksonOption(final Binder binder, final Enum<?> option)
    {
        final MapBinder<Enum<?>, Boolean> optionBinder = MapBinder.newMapBinder(binder, new TypeLiteral<Enum<?>>() {}, new TypeLiteral<Boolean>() {}, JACKSON_NAMED);
        return optionBinder.addBinding(option);
    }
}



