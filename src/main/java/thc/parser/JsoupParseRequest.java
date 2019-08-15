package thc.parser;

import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.Map;

public interface JsoupParseRequest<U> {
    String url();

    default Map<String, String> headers() {
        return Collections.emptyMap();
    }

    U parseResponse(Document doc);

    U defaultValue();
}
