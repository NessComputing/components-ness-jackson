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

import com.nesscomputing.callback.Callback;

/**
 * A builder that configures and creates Function instances that transform data using Jackson.
 *
 * Up to four Function instances will be bound for each serializer or deserializer built.
 * <ul>
 * <li> @Json Function&lt;T, String&gt; -- serialize T into a String</li>
 * <li> @Json Function&lt;? super T, String&gt; -- serialize T into a String</li>
 * <li> @Json / @Smile Function&lt;T, byte[]&gt; -- serialize T into a byte[]</li>
 * <li> @Json / @Smile Function&lt;? super T, byte[]&gt; -- serialize T into a byte[]</li>
 * </ul>
 *
 * Deserialization is symmetric except the wildcards are now <code>? extends T</code>.
 *
 * The bound transformers have configurable error actions.
 *
 * @param <T>
 */
public interface SerializerBinderBuilder<T> {

    /**
     * Configure the action to take upon encountering an unexpected exception
     */
    SerializerBinderBuilder<T> onError(Callback<Throwable> errorCallback);

    /**
     * Bind all serialization and deserialization functions
     */
    void bind();
}
