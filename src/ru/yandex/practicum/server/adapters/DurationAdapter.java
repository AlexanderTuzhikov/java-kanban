package ru.yandex.practicum.server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(duration.toString());
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        Objects.requireNonNull(jsonReader, "JsonReader не может быть null");
        String value = jsonReader.nextString();
        return value == null ? null : Duration.parse(value);
    }
}
