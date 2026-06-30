package com.lapuja.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/imagenes")
@CrossOrigin(origins = "*")
public class ImagenController {

    private final String uploadDir = "uploads";

    @PostMapping(
            value = "/subasta",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Object subirImagenSubasta(
            @RequestParam("file") MultipartFile file
    ) {
        return guardarImagen(file, "subastas");
    }

    @PostMapping(
            value = "/perfil",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Object subirImagenPerfil(
            @RequestParam("file") MultipartFile file
    ) {
        return guardarImagen(file, "perfiles");
    }

    @PostMapping(
            value = "/chat",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Object subirImagenChat(
            @RequestParam("file") MultipartFile file
    ) {
        return guardarImagen(file, "chat");
    }

    private Object guardarImagen(MultipartFile file, String carpeta) {
        try {
            if (file == null || file.isEmpty()) {
                return Map.of(
                        "ok", false,
                        "mensaje", "El archivo está vacío"
                );
            }

            String nombreOriginal = file.getOriginalFilename();
            String extension = ".jpg";

            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }

            String nombreArchivo = UUID.randomUUID() + extension;

            Path carpetaDestino = Path.of(uploadDir, carpeta);
            Files.createDirectories(carpetaDestino);

            Path rutaArchivo = carpetaDestino.resolve(nombreArchivo);
            Files.copy(file.getInputStream(), rutaArchivo);

            String url = "http://10.0.2.2:8080/uploads/" + carpeta + "/" + nombreArchivo;

            return Map.of(
                    "ok", true,
                    "mensaje", "Imagen subida correctamente",
                    "url", url
            );

        } catch (Exception e) {
            return Map.of(
                    "ok", false,
                    "mensaje", "No se pudo guardar la imagen"
            );
        }
    }
}