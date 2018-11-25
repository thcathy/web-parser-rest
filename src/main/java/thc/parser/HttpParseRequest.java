package thc.parser;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public interface HttpParseRequest<U> {
    String url();

    default Map<String, String> headers() {
        return Collections.emptyMap();
    }

    default Map<String, String> queryParams() {
        return Collections.emptyMap();
    }

    U parseResponse(InputStream responseInputStream);
}
