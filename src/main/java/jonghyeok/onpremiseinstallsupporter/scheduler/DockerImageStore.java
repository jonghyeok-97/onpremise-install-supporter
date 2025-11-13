package jonghyeok.onpremiseinstallsupporter.scheduler;

import jonghyeok.onpremiseinstallsupporter.DockerImage;

import java.io.IOException;
import java.util.List;

public interface DockerImageStore {

    void store(List<DockerImage> images) throws IOException;
}
