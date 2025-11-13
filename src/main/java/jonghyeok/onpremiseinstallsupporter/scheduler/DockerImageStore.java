package jonghyeok.onpremiseinstallsupporter.scheduler;

import jonghyeok.onpremiseinstallsupporter.DockerImage;

import java.util.List;

public interface DockerImageStore {

    void store(List<DockerImage> images);

    List<DockerImage> getDockerImages();
}
