package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.service.IFirebaseStorageService;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class FirebaseStorageServiceImpl implements IFirebaseStorageService {


    @Override
    public String subirArchivo(MultipartFile archivo, Long recursoId) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            String fileName = "librosService/" + recursoId + ".pdf";

            Blob blob = bucket.create(
                    fileName,
                    archivo.getInputStream(),
                    archivo.getContentType()
            );

            // 🔥 Hacer el archivo público
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

            // URL pública directa más limpia
            String url = String.format(
                    "https://storage.googleapis.com/%s/%s",
                    bucket.getName(),
                    fileName
            );

            log.info("Archivo subido y hecho público: {}", fileName);
            return url;

        } catch (IOException e) {
            log.error("Error al subir archivo a Firebase Storage: {}", e.getMessage());
            throw new RuntimeException("Error al subir el archivo PDF", e);
        }
    }

    @Override
    public String subirPortada(MultipartFile imagen, Long recursoId) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            String contentType = imagen.getContentType();
            String extension = "jpg";
            if (contentType != null) {
                if (contentType.contains("png")) extension = "png";
                else if (contentType.contains("webp")) extension = "webp";
                else if (contentType.contains("gif")) extension = "gif";
            }
            String fileName = "portadas/" + recursoId + "." + extension;

            Blob blob = bucket.create(
                    fileName,
                    imagen.getInputStream(),
                    imagen.getContentType()
            );
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

            String url = String.format(
                    "https://storage.googleapis.com/%s/%s",
                    bucket.getName(),
                    fileName
            );
            log.info("Portada subida: {}", fileName);
            return url;
        } catch (IOException e) {
            log.error("Error al subir portada a Firebase Storage: {}", e.getMessage());
            throw new RuntimeException("Error al subir la imagen de portada", e);
        }
    }

    @Override
    public void eliminarArchivo(Long recursoId) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            String fileName = "librosService/" + recursoId + ".pdf";
            Blob blob = bucket.get(fileName);
            if (blob != null) {
                blob.delete();
                log.info("Archivo eliminado de Firebase Storage: {}", fileName);
            }
        } catch (Exception e) {
            log.error("Error al eliminar archivo de Firebase Storage: {}", e.getMessage());
        }
    }

    @Override
    public void eliminarPortada(Long recursoId) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            for (String ext : new String[] { "jpg", "jpeg", "png", "webp", "gif" }) {
                String fileName = "portadas/" + recursoId + "." + ext;
                Blob blob = bucket.get(fileName);
                if (blob != null) {
                    blob.delete();
                    log.info("Portada eliminada: {}", fileName);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Error al eliminar portada de Firebase Storage: {}", e.getMessage());
        }
    }
}
