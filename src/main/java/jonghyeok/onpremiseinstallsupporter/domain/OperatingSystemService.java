package jonghyeok.onpremiseinstallsupporter.domain;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperatingSystemService {

    private final List<OperatingSystem> osList;

    public OperatingSystemService() {
        osList = List.of(new Redhat(), new RockyLinux(), new Ubuntu());
    }

    public List<String> getOSNames() {
        return osList.stream()
                .map(OperatingSystem::getName)
                .toList();
    }
}
