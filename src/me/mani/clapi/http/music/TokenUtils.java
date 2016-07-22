package me.mani.clapi.http.music;

import com.google.gson.JsonObject;
import me.mani.clapi.http.HttpConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

/**
 * Created by Schuckmann on 16.05.2016.
 */
public class TokenUtils {

    public static String prepareAuth(String username, String password, String botId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("botId", botId);
        return jsonObject.toString();
    }

    public static String readToken(ByteArrayOutputStream byteArrayOutputStream) {
        JsonObject jsonObject = HttpConnection.getGson().fromJson(new InputStreamReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())), JsonObject.class);
        if (jsonObject == null && !jsonObject.has("token"))
            return null;
        return jsonObject.get("token").getAsString();
    }

}
