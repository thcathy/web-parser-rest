package thc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.StringUtils;

import static thc.constant.ContentConstants.NOT_AVAILABLE;

public class DictionaryResult {
    public final String word;   // meta > id
    public final String pronunciationUrl;   //
    public final String pronunciationLang; // "en"
    public final String IPA;    // hwi > prs > mw
    public final String definition;

    public DictionaryResult(String word, String pronunciationUrl, String pronunciationLang, String IPA, String definition) {
        this.word = word;
        this.pronunciationUrl = pronunciationUrl;
        this.pronunciationLang = pronunciationLang;
        this.IPA = IPA;
        this.definition = definition;
    }

    public DictionaryResult(String word) {
        this(word, null, NOT_AVAILABLE, NOT_AVAILABLE, NOT_AVAILABLE);
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
