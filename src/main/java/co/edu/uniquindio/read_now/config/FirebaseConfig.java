package co.edu.uniquindio.read_now.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Configuration
public class FirebaseConfig {

    private static final String CONFIG_DIR = "/config";

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    @Value("${firebase.credentials.path:firebase-service-account.json}")
    private String credentialsPath;

    @PostConstruct
    public void initFirebase() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }
        try (InputStream serviceAccount = abrirCredenciales()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(storageBucket)
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Firebase inicializado correctamente con bucket: {}", storageBucket);
        } catch (IOException e) {
            log.error(
                    "No se pudo inicializar Firebase (ruta configurada: {}). "
                            + "En el servidor monta firebase-service-account.json en {}/firebase-service-account.json "
                            + "y usa firebase.credentials.path=/config/firebase-service-account.json en application.properties. "
                            + "Detalle: {}",
                    credentialsPath,
                    CONFIG_DIR,
                    e.getMessage()
            );
        }
    }

    /**
     * Orden: variable de entorno GOOGLE_APPLICATION_CREDENTIALS → ruta absoluta/archivo en disco
     * → /config/ → classpath (desarrollo local).
     */
    private InputStream abrirCredenciales() throws IOException {
        String envPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (envPath != null && !envPath.isBlank()) {
            Path p = Path.of(envPath.trim());
            if (Files.isRegularFile(p)) {
                log.info("Credenciales Firebase desde GOOGLE_APPLICATION_CREDENTIALS: {}", p);
                return Files.newInputStream(p);
            }
        }

        if (credentialsPath != null && !credentialsPath.isBlank()) {
            Path direct = Path.of(credentialsPath.trim());
            if (direct.isAbsolute() && Files.isRegularFile(direct)) {
                log.info("Credenciales Firebase desde archivo: {}", direct);
                return Files.newInputStream(direct);
            }
            if (Files.isRegularFile(direct)) {
                log.info("Credenciales Firebase desde archivo relativo: {}", direct.toAbsolutePath());
                return Files.newInputStream(direct);
            }
        }

        String fileName = credentialsPath != null && credentialsPath.contains("/")
                ? Path.of(credentialsPath.trim()).getFileName().toString()
                : (credentialsPath != null ? credentialsPath.trim() : "firebase-service-account.json");
        Path enConfig = Path.of(CONFIG_DIR, fileName);
        if (Files.isRegularFile(enConfig)) {
            log.info("Credenciales Firebase desde {}", enConfig);
            return Files.newInputStream(enConfig);
        }

        String classpathEntry = credentialsPath != null && !credentialsPath.isBlank()
                ? credentialsPath.trim()
                : "firebase-service-account.json";
        log.info("Credenciales Firebase desde classpath: {}", classpathEntry);
        return new ClassPathResource(classpathEntry).getInputStream();
    }
}
