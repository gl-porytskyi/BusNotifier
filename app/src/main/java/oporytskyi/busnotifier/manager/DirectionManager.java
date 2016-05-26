package oporytskyi.busnotifier.manager;

import android.content.res.AssetManager;
import android.util.Log;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import oporytskyi.busnotifier.TheApplication;
import oporytskyi.busnotifier.dto.Direction;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by oleksandr.porytskyi on 5/26/2016.
 */
public class DirectionManager {
    private static final String TAG = DirectionManager.class.getName();

    private List<Direction> directions;

    public DirectionManager() {
        load();
    }

    private void load() {
        AssetManager assetManager = TheApplication.get().getAssetManager();
        try (InputStream inputStream = assetManager.open("schedule.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null))
                    .addDeserializer(LocalTime.class, new LocalTimeDeserializer());
            objectMapper.registerModule(testModule);
            directions = objectMapper.readValue(inputStream, new TypeReference<List<Direction>>() {
            });
            Log.d(TAG, "loaded " + directions.size());
        } catch (IOException e) {
            Log.e(TAG, "e", e);
        }
    }

    public List<Direction> getDirections() {
        return directions;
    }

    private static class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
        private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("HH:mm");

        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return LocalTime.parse(p.getText(), dateTimeFormatter);
        }
    }
}
