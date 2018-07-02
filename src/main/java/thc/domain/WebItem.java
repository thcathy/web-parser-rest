package thc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class WebItem {
    public final String url;
    public final String mime;
    public final int imageHeight;
    public final int imageWidth;
    public final String thumbnailUrl;

    public WebItem(String url, String mime, int imageHeight, int imageWidth, String thumbnailUrl) {
        this.url = url;
        this.mime = mime;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("url", url)
                .append("mime", mime)
                .append("imageHeight", imageHeight)
                .append("imageWidth", imageWidth)
                .append("thumbnailUrl", thumbnailUrl)
                .toString();
    }


}
