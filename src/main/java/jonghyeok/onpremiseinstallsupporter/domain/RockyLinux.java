package jonghyeok.onpremiseinstallsupporter.domain;

import java.util.List;

/**
 *
 * @see "https://hub.docker.com/r/rockylinux/rockylinux"
 */
public class RockyLinux implements OperatingSystem {

    private final String name = "rockylinux";
    private final List<String> versions = List.of("8", "9", "10");

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getVersions() {
        return versions;
    }
}
