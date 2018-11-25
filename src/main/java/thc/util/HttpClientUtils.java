package thc.util;

import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {
    protected static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    public static Response nullResponseOnError(String url, Throwable t) {
        log.error("Error when querying: {}", url, t);
        return null;
    }
}
