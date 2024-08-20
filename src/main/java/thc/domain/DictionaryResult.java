package thc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

import static thc.constant.ContentConstants.NOT_AVAILABLE;

public class DictionaryResult {
    public final String word;
    public final String pronunciationUrl;
    public final String pronunciationLang;
    public final String IPA;
    public final String definition;
    public final List<String> meanings;

    public DictionaryResult(String word, String pronunciationUrl, String pronunciationLang, String IPA, String definition, List<String> meanings) {
        this.word = word;
        this.pronunciationUrl = pronunciationUrl;
        this.pronunciationLang = pronunciationLang;
        this.IPA = IPA;
        this.definition = definition;
        this.meanings = meanings;
    }

    public DictionaryResult(String word) {
        this(word, null, NOT_AVAILABLE, NOT_AVAILABLE, NOT_AVAILABLE, Collections.emptyList());
    }

    public DictionaryResult(String word, List<String> meanings) {
        this(word, null, NOT_AVAILABLE, NOT_AVAILABLE, NOT_AVAILABLE, meanings);
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
