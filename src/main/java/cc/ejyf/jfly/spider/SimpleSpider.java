package cc.ejyf.jfly.spider;


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;


public class SimpleSpider extends AbstractSpider {
    //    private Logger logger = LoggerFactory.getLogger(AbstractSpider.class);
    public static final String[] HEAD_FIREFOX_NORMAL = {
            "User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0",
    };
    public static final String[] HEAD_CHROME_NORMAL = {
            "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36",
    };

    public SimpleSpider() {
        super();
    }

    public SimpleSpider(int timeOutSeconds) {
        super(timeOutSeconds);
    }

    public SimpleSpider(ClientType type) {
        super(type);
    }

    public SimpleSpider(ClientType type, int timeOutSeconds) {
        super(type, timeOutSeconds);
    }

    public String get(String uri) throws URISyntaxException {
        return get(new URI(uri));
    }

    public String get(URI uri) {
        HttpRequest request = HttpRequest.newBuilder(uri).headers(HEAD_FIREFOX_NORMAL).GET().build();
        return fetchStringResponse(request);
    }

    public String get(URI uri, String... headers) {
        HttpRequest request = HttpRequest.newBuilder(uri).headers(HEAD_FIREFOX_NORMAL).headers(headers).GET().build();
        return fetchStringResponse(request);
    }

    public String get(HttpRequest request) {
        return fetchStringResponse(request);
    }

    public String get(HttpRequest.Builder requestBuilder, URI uri) {
        return fetchStringResponse(requestBuilder.uri(uri).build());
    }

    public String post(URI uri, String body, String... headers) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .headers(HEAD_FIREFOX_NORMAL)
                .headers(headers)
                .POST(BodyPublishers.ofString(body)).build();
        return fetchStringResponse(request);
    }

    public String post(String uri, String body, String... headers) throws URISyntaxException {
        return post(new URI(uri), body, headers);
    }

    public String post(String uri, String body) throws URISyntaxException {
        return post(new URI(uri), body);
    }

    public String post(URI uri, String body) {
        HttpRequest request = HttpRequest.newBuilder(uri).headers(HEAD_FIREFOX_NORMAL)
                .POST(BodyPublishers.ofString(body)).build();
        return fetchStringResponse(request);
    }

    public <T> HttpResponse<T> fetchResponse(HttpRequest request, BodyHandler<T> handler) {
        HttpResponse<T> response;
        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return response;
    }

    /**
     * String ShortCut for {@link #fetchResponse}
     *
     * @param request
     * @return String value of the Response, or <code>null</code> if error
     */
    public String fetchStringResponse(HttpRequest request) {
        return fetchResponse(request, BodyHandlers.ofString()).body();
    }
}
