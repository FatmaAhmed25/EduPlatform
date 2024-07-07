package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.MultipartFileDTO;
import com.edu.eduplatform.utils.CustomMultipartFile;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class CourseContentService {

    @Value("${bucket.name}")
    private String bucketName;
    private final Storage storage;

    public CourseContentService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public String uploadFile(String courseId, String folderName, MultipartFile file) throws IOException {
        System.out.println("inside upload function");
        try {

            String fileName = folderName + "/" + file.getOriginalFilename();
            BlobId blobId = BlobId.of(bucketName, "courses/courseId-" + courseId + "/" + fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            storage.create(blobInfo, file.getBytes());
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to upload file to Google Cloud Storage", e);
        }
    }

    public URL getFileUrl(String courseId, String fileName) throws IOException {
        try {
            BlobId blobId = BlobId.of(bucketName, "courses/courseId-" + courseId + "/" + fileName);
            Blob blob = storage.get(blobId);
            if (blob != null) {
                return blob.signUrl(3, TimeUnit.HOURS); // URL valid for 1 hour
            } else {
                throw new IOException("File not found in Google Cloud Storage");
            }
        } catch (StorageException e) {
            // Log or handle the exception appropriately
            throw new IOException("Failed to fetch file URL from Google Cloud Storage", e);
        }
    }

    public boolean deleteFile(String courseId, String fileName) throws IOException {
        try {
            BlobId blobId = BlobId.of(bucketName, "courses/courseId-" + courseId + "/" + fileName);
            return storage.delete(blobId);
        } catch (StorageException e) {
            // Log or handle the exception appropriately
            throw new IOException("Failed to delete file from Google Cloud Storage", e);
        }
    }

//    public List<MultipartFileDTO> getPdfFiles(String courseId, String folderName) throws IOException {
//        List<MultipartFileDTO> pdfFiles = new ArrayList<>();
//        String prefix = "courses/courseId-" + courseId + "/" + folderName + "/";
//
//        storage.list(bucketName, Storage.BlobListOption.prefix(prefix)).iterateAll().forEach(blob -> {
//            if ("application/pdf".equals(blob.getContentType())) {
//                byte[] content = blob.getContent();
//                MultipartFile multipartFile = new CustomMultipartFile(blob.getName(), blob.getName(), "application/pdf", content);
//                MultipartFileDTO dto = new MultipartFileDTO(multipartFile.getName(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getSize());
//                pdfFiles.add(dto);
//            }
//        });
//        return pdfFiles;
//    }
    public List<String> listFiles(String courseId, String folderName) {
        List<String> fileList = new ArrayList<>();
        String prefix = "courses/courseId-" + courseId + "/" + folderName + "/";
        storage.list(bucketName, Storage.BlobListOption.prefix(prefix)).iterateAll()
                .forEach(blob -> fileList.add(blob.getName()));
        return fileList;
    }
}