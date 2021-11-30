package cc.ejyf.jfly.spider;

import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public abstract class AbstractSpider {
    public final static String[] ACCEPT_GZIP_DEFLATE = {"Accept-Encoding", "gzip, deflate"};
    public final static String[] CONTENT_JSON_UTF8 = {"Content-Type", "application/json;charset=utf-8"};
    public final static String[] CONTENT_X_WWW_FORM_URLENCODED_UTF8 = {"Content-Type", "application/x-www-form-urlencoded;charset=utf-8"};
    public final static String[] ACCEPT_JSON_TEXT = {"Accept", "application/json,text/html;q=0.5"};
    public final static String[] ACCEPT_CHARSET_UTF8 = {"Accept-Charset", "utf-8"};
    public final static String[] ACCEPT_TEXT_HTML_XML = {"Accept", "text/html,application/xhtml+xml;q=0.7,application/xml;q=0.5"};

    static {
        final Properties props = System.getProperties();
        props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
    }

    protected transient HttpClient client;
    private CookieManager manager = new CookieManager();
    private ClientType type;
    private int timeoutSeconds;
    private SSLContext sslContext;

    public AbstractSpider() {
        this(ClientType.STANDARD, 60);
    }

    public AbstractSpider(int timeOutSeconds) {
        this(ClientType.STANDARD, timeOutSeconds);
    }

    public AbstractSpider(ClientType type) {
        this(type, 60);
    }

    public AbstractSpider(ClientType type, int timeOutSeconds) {
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, (x, y) -> true).build();
        } catch (Exception ignored) {
        }
        resetClient(type, timeOutSeconds);
    }

    public HttpClient getClient() {
        return client;
    }

    public synchronized void setClient(HttpClient client) {
        this.client = client;
    }

    public CookieManager getManager() {
        return manager;
    }

    public synchronized void setManager(CookieManager manager) {
        this.manager = manager;
    }

    public ClientType getType() {
        return type;
    }

    public synchronized void setType(ClientType type) {
        this.type = type;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public synchronized void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    protected synchronized void resetClient(ClientType type, int timeOutSeconds) {

        this.type = type;
        this.timeoutSeconds = timeOutSeconds;
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        switch (type) {
            case STANDARD:
                client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                        .sslContext(sslContext)
                        .connectTimeout(Duration.ofSeconds(timeOutSeconds)).build();
                break;
            case COOKIED:
                client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                        .sslContext(sslContext)
                        .connectTimeout(Duration.ofSeconds(timeOutSeconds)).cookieHandler(manager).build();
                break;
            case REDIRECTED:
                client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                        .sslContext(sslContext)
                        .connectTimeout(Duration.ofSeconds(timeOutSeconds)).followRedirects(HttpClient.Redirect.NORMAL).build();
                break;
            case ALL:
                client = HttpClient.newBuilder()
                        /*.version(HttpClient.Version.HTTP_1_1)*/
                        .sslContext(sslContext)
                        .connectTimeout(Duration.ofSeconds(timeOutSeconds)).cookieHandler(manager)
                        .followRedirects(HttpClient.Redirect.NORMAL).build();
                break;
        }
    }

    protected abstract <T> HttpResponse<T> fetchResponse(HttpRequest request, HttpResponse.BodyHandler<T> handler);

    public enum ClientType {
        STANDARD, COOKIED, REDIRECTED, ALL
    }
}
