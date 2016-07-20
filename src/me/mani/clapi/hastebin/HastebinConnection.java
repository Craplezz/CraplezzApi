package me.mani.clapi.hastebin;

import com.google.gson.JsonObject;
import me.mani.clapi.http.HttpConnection;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by Schuckmann on 19.05.2016.
 */
public class HastebinConnection extends HttpConnection {

    public HastebinConnection() {
        super("http://hastebin.com/");
    }

    public void pushHastebin(String content, Consumer<String> consumer) {
        executeAsync(new PostRequest(url(url, "documents")).withBody(content, ContentType.TEXT_PLAIN).build(), (response) -> {
            try {
                JsonObject jsonObject = read(response, JsonObject.class);
                if (jsonObject.has("key")) {
                    consumer.accept(jsonObject.get("key").getAsString());
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            consumer.accept(null);
        });
    }

    @Override
    public boolean onSuccess(ByteArrayOutputStream outputStream, int statusCode) {
        try {
            System.out.println(IOUtils.toString(new ByteArrayInputStream(outputStream.toByteArray())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onError(IOException e) {}

    /**
     * Little test application right here
     */
    public static void main(String... args) {
        HastebinConnection hastebinConnection = new HastebinConnection();
        hastebinConnection.pushHastebin(String.join(" ", args), (key) -> System.out.println("http://hastebin.com/" + key));
    }

}
