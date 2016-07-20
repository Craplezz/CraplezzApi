package me.mani.clapi.various;

import me.mani.clapi.http.HttpConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * Created by Schuckmann on 20.05.2016.
 */
public class AudioStreamConnection extends HttpConnection {

    public AudioStreamConnection(String url) {
        super(url);
    }

    public void listenForMetadata(String stream, Consumer<String> consumer, Consumer<Runnable> cancel) {
        listenAsync(new GetRequest(url(url, stream)).withHeader("Icy-MetaData", "1").build(), (response) -> {
            try {
                int audioByteCount = Integer.valueOf(response.getHeaders("icy-metaint")[0].getValue());
                InputStream inputStream = response.getEntity().getContent();

                while (true) {
                    if (inputStream.skip(audioByteCount) != audioByteCount)
                        break;
                    int metadataLength = inputStream.read() * 16;
                    char[] chars = new char[metadataLength];
                    for (int i = 0; i < chars.length; i++)
                        chars[i] = (char) inputStream.read();
                    if (chars.length > 0)
                        consumer.accept(new String(chars));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            consumer.accept(null);
        }, cancel);
    }

    @Override
    public boolean onSuccess(ByteArrayOutputStream outputStream, int statusCode) {
        return false;
    }

    @Override
    public void onError(IOException e) {

    }

}
