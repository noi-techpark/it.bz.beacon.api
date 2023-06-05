// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.service.image;

import it.bz.beacon.api.config.FileStorageProperties;
import it.bz.beacon.api.exception.db.BeaconImageNotFoundException;
import it.bz.beacon.api.exception.storage.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {

    @Autowired
    private FileStorageProperties fileStorageProperties;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(fileStorageLocation);
            } catch (Exception ex) {
                throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
            }
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = generateFilename(getExtensionByStringHandling(StringUtils.cleanPath(file.getOriginalFilename())));

        try {
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new BeaconImageNotFoundException();
            }
        } catch (MalformedURLException ex) {
            throw new BeaconImageNotFoundException();
        }
    }

    private String generateFilename(String extension) {
        UUID uuid = UUID.randomUUID();
        String fileName = Optional.ofNullable(extension).map(s -> uuid.toString() + "." + extension).orElse(uuid.toString());
        if (Files.exists(fileStorageLocation.resolve(fileName))) {
            return generateFilename(extension);
        }

        return fileName;
    }

    private String getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse(null);
    }
}
