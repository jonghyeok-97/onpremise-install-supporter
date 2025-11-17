package jonghyeok.onpremiseinstallsupporter.controller;

import jonghyeok.onpremiseinstallsupporter.DockerImage;
import jonghyeok.onpremiseinstallsupporter.domain.OperatingSystemService;
import jonghyeok.onpremiseinstallsupporter.scheduler.DockerImageStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/docker")
public class InstallSupporterApiController {
    private final DockerImageStore dockerImageStore;
    private final OperatingSystemService osService;

    @GetMapping("/images")
    public ResponseEntity<List<DockerImage>> getDockerImages() {
        List<DockerImage> dockerImages = dockerImageStore.getDockerImages();

        return ResponseEntity.ok(dockerImages);
    }

    @GetMapping("/os")
    public ResponseEntity<List<String>> getSupportedOSNames() {
        return ResponseEntity.ok(osService.getOSNames());
    }
}
