package jonghyeok.onpremiseinstallsupporter.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jonghyeok.onpremiseinstallsupporter.DockerImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class LocalFileStore implements DockerImageStore {

    private final String filePath;
    private final ObjectMapper objectMapper;

    public LocalFileStore(@Value("${docker-sync.file.path}") String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void store(List<DockerImage> images) {
        if (!StringUtils.hasText(filePath)) {
            throw new IllegalArgumentException("docker-sync.file.path should has text");
        }

        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        try (OutputStream os = fileSystemResource.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))
        ) {
            for (DockerImage image : images) {
                String json = objectMapper.writeValueAsString(image);
                writer.write(json);
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("[{}] {}", this.getClass().getName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DockerImage> getDockerImages() {
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        try (InputStream inputStream = fileSystemResource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            return reader.lines()
                    .map(line -> {
                        try {
                            return objectMapper.readValue(line, DockerImage.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            log.error("[{}] {}", this.getClass().getName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
