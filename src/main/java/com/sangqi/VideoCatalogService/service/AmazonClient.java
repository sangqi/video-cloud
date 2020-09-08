package com.sangqi.VideoCatalogService.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.ByteStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class AmazonClient {

    private static final String VIDEO_FOLDER_PATH = "videos/";
    private final Path tempFolder = Paths.get("temp/" + VIDEO_FOLDER_PATH);
    private final AmazonS3 amazonS3Client;

    @Value("${amazon.s3.bucket_name}")
    private String bucketName;

    @Autowired
    AmazonClient(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
        if (!Files.exists(tempFolder)) {
            try {
                Files.createDirectories(tempFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String uploadFile(String fileKey, MultipartFile multipartFile) throws Exception {
        Path filePath = convertDataToFile(fileKey, multipartFile);
        String s3FileKey = VIDEO_FOLDER_PATH + fileKey;
        amazonS3Client.putObject(bucketName, s3FileKey, filePath.toFile());
        Files.delete(filePath);
        amazonS3Client.setObjectAcl(bucketName, s3FileKey, CannedAccessControlList.PublicRead);
        return amazonS3Client.getUrl(bucketName, s3FileKey).toExternalForm();
    }

    public ByteArrayOutputStream downloadFile(String fileKey) throws Exception {
        String s3FileKey = VIDEO_FOLDER_PATH + fileKey;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        S3Object s3Object = amazonS3Client.getObject(bucketName, s3FileKey);
        ByteStreams.copy(s3Object.getObjectContent(), outputStream);
        return outputStream;
    }

    private Path convertDataToFile(String fileKey, MultipartFile multipartFile) throws IOException {
        Path filePath = tempFolder.resolve(fileKey);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath;
    }
}
