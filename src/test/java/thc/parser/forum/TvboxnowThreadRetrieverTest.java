package thc.parser.forum;

import org.junit.Test;

/**
 * Created by wongtim on 19/07/2016.
 */
public class TvboxnowThreadRetrieverTest {
    @Test(expected = IllegalArgumentException.class)
    public void withoutUsername_ShouldGivenException() {
        TvboxnowThreadParser.PASSWORD = "xyz";
        TvboxnowThreadParser.USERNAME = null;
        new TvboxnowThreadParser("test", "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void withoutPassword_ShouldGivenException() {
        TvboxnowThreadParser.USERNAME = "xyz";
        TvboxnowThreadParser.PASSWORD = null;
        new TvboxnowThreadParser("test", "test");
    }
}