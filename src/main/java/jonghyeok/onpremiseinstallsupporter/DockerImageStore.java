package jonghyeok.onpremiseinstallsupporter;

import java.io.IOException;
import java.util.List;

public interface DockerImageStore {

    void store(List<DockerGithubImage> images) throws IOException;
}
