package oporytskyi.busnotifier.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import oporytskyi.busnotifier.TheApplication;
import oporytskyi.busnotifier.dto.Direction;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by oleksandr.porytskyi on 5/26/2016.
 */
public class DirectionManager {
    private static final String TAG = DirectionManager.class.getName();
    private static final String FILE_NAME = "schedule.json";

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("HH:mm");
    private final ObjectMapper objectMapper;
    private List<Direction> directions;
    private DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Kiev");

    public DirectionManager() {
        objectMapper = new ObjectMapper();
        SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null, null, null))
                .addDeserializer(LocalTime.class, new LocalTimeDeserializer())
                .addSerializer(LocalTime.class, new LocalTimeSerializer())
                .addDeserializer(Period.class, new PeriodDeserializer())
                .addSerializer(Period.class, new PeriodSerializer());
        objectMapper.registerModule(testModule);

        if (!load()) {
            loadDefault();
        }
    }

    private boolean load() {
        Context context = TheApplication.get().getApplicationContext();
        try (FileInputStream fileInputStream = context.openFileInput(FILE_NAME)) {
            load(fileInputStream);
        } catch (IOException e) {
            Log.e(TAG, "e", e);
            return false;
        }
        return true;
    }

    private void loadDefault() {
        AssetManager assetManager = TheApplication.get().getAssetManager();
        try (InputStream inputStream = assetManager.open(FILE_NAME)) {
            load(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "e", e);
        }
    }

    private void load(InputStream inputStream) throws IOException {
        directions = objectMapper.readValue(inputStream, new TypeReference<List<Direction>>() {
        });
        Log.d(TAG, "loaded " + directions.size());
    }

    public List<Direction> getDirections() {
        return directions;
    }

    public DateTimeZone getDateTimeZone() {
        return dateTimeZone;
    }

    public void save() {
        Context context = TheApplication.get().getApplicationContext();
        try (FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            objectMapper.writeValue(fileOutputStream, directions);
        } catch (IOException e) {
            Log.e(TAG, "e", e);
        }
    }

    private static class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return LocalTime.parse(p.getText(), dateTimeFormatter);
        }
    }

    private static class LocalTimeSerializer extends JsonSerializer<LocalTime> {

        @Override
        public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeString(value.toString(dateTimeFormatter));
        }
    }

    private static class PeriodDeserializer extends JsonDeserializer<Period> {

        @Override
        public Period deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return Period.parse(p.getText());
        }
    }

    private static class PeriodSerializer extends JsonSerializer<Period> {

        @Override
        public void serialize(Period value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeString(value.toString());
        }
    }
}
