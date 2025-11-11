package jonghyeok.onpremiseinstallsupporter;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DockerImage(
        String name,
        String path,
        String sha,
        String size,
        String url,
        String htmlUrl,
        String gitUrl,
        String downloadUrl,
        String type,
        @JsonProperty("_links")
        Link link
) {

    public record Link(
        String self,
        String git,
        String html
    ) {
    }
}
