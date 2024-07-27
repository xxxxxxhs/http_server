package Transmuters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StrictTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, typeToken);
        TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter jsonWriter, T t) throws IOException {
                delegate.write(jsonWriter, t);
            }

            @Override
            public T read(JsonReader jsonReader) throws IOException {
                JsonElement jsonElement = elementAdapter.read(jsonReader);
                if (jsonElement.isJsonObject()) {
                    // Если элемент является JsonObject, проверяем поля
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    // Получаем все поля класса
                    Field[] fields = typeToken.getRawType().getDeclaredFields();
                    // Создаем набор имен полей
                    Set<String> fieldNames = new HashSet<>();
                    for (Field field : fields) {
                        fieldNames.add(field.getName());
                    }
                    // Проверяем наличие неизвестных полей в JSON
                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                        if (!fieldNames.contains(entry.getKey())) {
                            throw new JsonParseException("Unknown field: " + entry.getKey());
                        }
                    }
                }
                // Десериализуем объект, используя адаптер по умолчанию
                return delegate.fromJsonTree(jsonElement);
            }
        };
    }
}
