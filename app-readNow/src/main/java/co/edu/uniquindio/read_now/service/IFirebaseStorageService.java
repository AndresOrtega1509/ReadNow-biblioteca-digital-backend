package co.edu.uniquindio.read_now.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFirebaseStorageService {

    String subirArchivo(MultipartFile archivo, Long recursoId);

    String subirPortada(MultipartFile imagen, Long recursoId);

    void eliminarArchivo(Long recursoId);

    void eliminarPortada(Long recursoId);
}
