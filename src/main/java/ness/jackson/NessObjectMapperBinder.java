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



