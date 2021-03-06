package io.sentry.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.protocol.Contexts;
import java.lang.reflect.Type;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class ContextsSerializerAdapter implements JsonSerializer<Contexts> {

  private final @NotNull SentryOptions options;

  public ContextsSerializerAdapter(final @NotNull SentryOptions options) {
    this.options = options;
  }

  @Override
  public JsonElement serialize(Contexts src, Type typeOfSrc, JsonSerializationContext context) {
    if (src == null) {
      return null;
    }

    final JsonObject object = new JsonObject();
    for (final Map.Entry<String, Object> entry : src.entrySet()) {
      try {
        final JsonElement element = context.serialize(entry.getValue(), Object.class);
        if (element != null) {
          object.add(entry.getKey(), element);
        }
      } catch (JsonParseException e) {
        options.getLogger().log(SentryLevel.ERROR, "%s context key isn't serializable.");
      }
    }
    return object;
  }
}
