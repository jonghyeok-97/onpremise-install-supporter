package jonghyeok.onpremiseinstallsupporter.domain;

import java.util.List;

/**
 * docker pull ubuntu:26.04
 *
 * @see "https://hub.docker.com/_/ubuntu"
 */
public class Ubuntu implements OperatingSystem {

    private final String name = "ubuntu";
    private final List<String> versions = List.of("22.04", "24.04", "26.04");



    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getVersions() {
        return versions;
    }
}
