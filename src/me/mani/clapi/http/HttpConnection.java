package me.mani.clapi.http;

import com.google.gson.Gson;
import me.mani.clapi.http.music.MusicRequests;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Created by Schuckmann on 16.05.2016.
 */
public abstract class HttpConnection {

    protected static Gson gson = new Gson();
    protected static ExecutorService service = Executors.newCachedThreadPool();

    protected String url;
    protected HttpClient httpClient;

    protected HttpConnection(String url) {
        this.url = url;
        httpClient = HttpClients.createDefault();
    }

    protected String url(String baseUrl, MusicRequests endPoint, Object... args) {
        return url(baseUrl, endPoint.getPath(), args);
    }

    protected String url(String baseUrl, String endPoint, Object... args) {
        return String.format(baseUrl.concat(endPoint), args);
    }

    protected <T> T read(ByteArrayOutputStream byteArrayOutputStream, Class<T> expectedType) throws IOException {
        return gson.fromJson(new InputStreamReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())), expectedType);
    }

    public void execute(HttpUriRequest httpRequest, Consumer<ByteArrayOutputStream> consumer) {
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpRequest);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            IOUtils.copy(httpResponse.getEntity().getContent(), outputStream);

            if (onSuccess(outputStream, httpResponse.getStatusLine().getStatusCode()))
                consumer.accept(outputStream);
            else
                consumer.accept(null);

            EntityUtils.consume(httpResponse.getEntity());
        } catch (IOException e) {
            onError(e);

            consumer.accept(null);
        }
    }

    public void listen(HttpUriRequest httpRequest, Consumer<HttpResponse> consumer, Consumer<Runnable> cancel) {
        AtomicReference<HttpResponse> httpResponse = new AtomicReference<>();
        try {
            httpResponse.set(httpClient.execute(httpRequest));
            cancel.accept(() -> {
                try {
                    EntityUtils.consume(httpResponse.get().getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            consumer.accept(httpResponse.get());
        } catch (IOException e) {
            onError(e);

            consumer.accept(null);
        }
    }

    public void executeAsync(HttpUriRequest httpRequest, Consumer<ByteArrayOutputStream> consumer) {
        service.execute(() -> execute(httpRequest, consumer));
    }

    public void listenAsync(HttpUriRequest httpRequest, Consumer<HttpResponse> consumer, Consumer<Runnable> cancel) {
        service.execute(() -> listen(httpRequest, consumer, cancel));
    }

    public static Gson getGson() {
        return gson;
    }

    public static ExecutorService getService() {
        return service;
    }

    public abstract boolean onSuccess(ByteArrayOutputStream outputStream, int statusCode);

    public abstract void onError(IOException e);

    public abstract class Request {

        protected String url;
        protected String body;
        protected ContentType contentType;
        protected String token;
        protected Header header;

        public Request(String url) {
            this.url = url;
        }

        public Request withBody(String body, ContentType contentType) {
            this.body = body;
            this.contentType = contentType;
            return this;
        }

        public Request withAuth(String token) {
            this.token = token;
            return this;
        }

        public Request withHeader(String header, String value) {
            this.header = new BasicHeader(header, value);
            return this;
        }

        public abstract HttpUriRequest build();

    }

    public class GetRequest extends Request {

        public GetRequest(String url) {
            super(url);
        }

        @Override
        public HttpUriRequest build() {
            HttpGet httpGet = new HttpGet(url);
            if (token != null)
                httpGet.addHeader("Authorization", "bearer " + token);
            if (header != null)
                httpGet.addHeader(header);
            return httpGet;
        }

    }

    public class PostRequest extends Request {

        public PostRequest(String url) {
            super(url);
        }

        @Override
        public HttpUriRequest build() {
            HttpPost httpPost = new HttpPost(url);
            if (body != null && contentType != null) {
                StringEntity entity = new StringEntity(body, contentType);
                httpPost.setEntity(entity);
            }
            if (token != null)
                httpPost.addHeader("Authorization", "bearer " + token);
            if (header != null)
                httpPost.addHeader(header);
            return httpPost;
        }

    }

}
