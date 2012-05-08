package ness.jackson;

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
    void build();
}
