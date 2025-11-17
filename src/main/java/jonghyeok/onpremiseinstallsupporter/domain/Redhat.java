package jonghyeok.onpremiseinstallsupporter.domain;

import java.util.List;

/**
 * @see "https://hub.docker.com/u/redhat?page=1&search="
 */
public class Redhat implements OperatingSystem {

    private final String name = "redhat";
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
