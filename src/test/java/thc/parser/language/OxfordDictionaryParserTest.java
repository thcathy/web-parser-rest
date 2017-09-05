package thc.parser.language;


import com.mashape.unirest.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class OxfordDictionaryParserTest {

    @Test
    public void parse_givenFailedResponse_shouldReturnOptionalEmpty() {
        HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
        when(mockResponse.getStatus()).thenReturn(HttpStatus.SC_BAD_REQUEST);

        Optional result = new OxfordDictionaryParser("").parse(mockResponse);
        assertFalse(result.isPresent());
    }
}