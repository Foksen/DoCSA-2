package ru.mirea.file_storage;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileController {

    private Path path;

    @Value("${FILE_STORAGE_PATH}")
    private String pathStringEnv;

    @PostConstruct
    public void init() {
        path = Paths.get(pathStringEnv).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories for path: " + pathStringEnv);
        }
    }

    @GetMapping("/")
    public String getFiles(Model model) throws IOException {
        List<String> files = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        model.addAttribute("files", files);
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (!file.isEmpty()) {
            Files.copy(file.getInputStream(), path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
        }
        return "redirect:/";
    }

    @GetMapping("/download/{fileName:.+}")
    public void downloadFile(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        Path file = path.resolve(fileName).normalize();
        if (!file.startsWith(path) || !Files.exists(file)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("File not found");
            return;
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        Files.copy(file, response.getOutputStream());
        response.getOutputStream().flush();
    }
}
