package thc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.StringUtils;

public class DictionaryResult {
    public final String word;
    public final String pronunciationUrl;
    public final String pronunciationLang;
    public final String IPA;
    public final String definition;

    public DictionaryResult(String word, String pronunciationUrl, String pronunciationLang, String IPA, String definition) {
        this.word = word;
        this.pronunciationUrl = pronunciationUrl;
        this.pronunciationLang = pronunciationLang;
        this.IPA = IPA;
        this.definition = definition;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("word", word)
                .append("pronunciationUrl", pronunciationUrl)
                .append("pronunciationLang", pronunciationLang)
                .append("IPA", IPA)
                .append("definition", definition)
                .toString();
    }

    public boolean hasPronunciation() {
        return !StringUtils.isEmpty(pronunciationUrl);
    }
}
