package com.example.tcmapp.service;

import com.example.tcmapp.entity.User;
import com.example.tcmapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class AvatarService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
            MediaType.IMAGE_JPEG_VALUE, ".jpg",
            MediaType.IMAGE_PNG_VALUE, ".png",
            "image/webp", ".webp"
    );

    private final UserRepository userRepository;
    private final Path uploadDirectory;

    public AvatarService(UserRepository userRepository,
                         @Value("${app.avatar.upload-dir:uploads/avatars}") String uploadDirectory) {
        this.userRepository = userRepository;
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    public String saveAvatar(String username, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        String contentType = file.getContentType();
        String extension = EXTENSIONS.get(contentType);
        if (file.isEmpty() || extension == null || file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("仅支持不超过 5MB 的 JPG、PNG 或 WebP 图片");
        }
        if (!hasValidSignature(file, contentType)) {
            throw new IllegalArgumentException("图片文件格式无效");
        }

        Files.createDirectories(uploadDirectory);
        String filename = UUID.randomUUID() + extension;
        Path target = safePath(filename);
        Path temporary = Files.createTempFile(uploadDirectory, "avatar-", ".tmp");

        try {
            file.transferTo(temporary);
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            String previousFilename = user.getAvatarFilename();
            user.setAvatarFilename(filename);
            userRepository.save(user);
            deletePreviousAvatar(previousFilename, filename);
            return filename;
        } catch (RuntimeException | IOException exception) {
            Files.deleteIfExists(temporary);
            Files.deleteIfExists(target);
            throw exception;
        }
    }

    public AvatarFile getAvatar(String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        String filename = user.getAvatarFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("用户尚未上传头像");
        }

        Path path = safePath(filename);
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("头像文件不存在");
        }
        Resource resource = new UrlResource(path.toUri());
        String detectedType = Files.probeContentType(path);
        MediaType mediaType = detectedType == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(detectedType);
        return new AvatarFile(resource, mediaType);
    }

    private Path safePath(String filename) {
        Path path = uploadDirectory.resolve(filename).normalize();
        if (!path.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("头像路径无效");
        }
        return path;
    }

    private void deletePreviousAvatar(String previousFilename, String currentFilename) {
        if (previousFilename == null || previousFilename.equals(currentFilename)) {
            return;
        }
        try {
            Files.deleteIfExists(safePath(previousFilename));
        } catch (IOException ignored) {
            // A stale old avatar should not make the successful upload fail.
        }
    }

    private boolean hasValidSignature(MultipartFile file, String contentType) throws IOException {
        byte[] header = new byte[12];
        int length;
        try (InputStream input = file.getInputStream()) {
            length = input.read(header);
        }
        if (MediaType.IMAGE_JPEG_VALUE.equals(contentType)) {
            return length >= 3 && (header[0] & 0xff) == 0xff && (header[1] & 0xff) == 0xd8 && (header[2] & 0xff) == 0xff;
        }
        if (MediaType.IMAGE_PNG_VALUE.equals(contentType)) {
            return length >= 8 && (header[0] & 0xff) == 0x89 && header[1] == 'P' && header[2] == 'N' && header[3] == 'G';
        }
        return length >= 12 && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
    }

    public record AvatarFile(Resource resource, MediaType mediaType) {}
}
