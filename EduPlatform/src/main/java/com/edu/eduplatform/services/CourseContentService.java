package com.edu.eduplatform.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CourseContentService {

    private static final String BUCKET_NAME = "eduplatform-ed3a0.appspot.com";
    private final Storage storage;

    public CourseContentService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public String uploadFile(String courseId, String folderName, MultipartFile file) throws IOException {
        System.out.println("inside upload function");
        try {

            String fileName = folderName + "/" + file.getOriginalFilename();
            BlobId blobId = BlobId.of(BUCKET_NAME, "courses/" + courseId + "/" + fileName);
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
            BlobId blobId = BlobId.of(BUCKET_NAME, "courses/" + courseId + "/" + fileName);
            Blob blob = storage.get(blobId);
            if (blob != null) {
                return blob.signUrl(1, TimeUnit.HOURS); // URL valid for 1 hour
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
            BlobId blobId = BlobId.of(BUCKET_NAME, "courses/" + courseId + "/" + fileName);
            return storage.delete(blobId);
        } catch (StorageException e) {
            // Log or handle the exception appropriately
            throw new IOException("Failed to delete file from Google Cloud Storage", e);
        }
    }
    public List<String> listFiles(String courseId, String folderName) {
        List<String> fileList = new ArrayList<>();
        String prefix = "courses/" + courseId + "/" + folderName + "/";
        storage.list(BUCKET_NAME, Storage.BlobListOption.prefix(prefix)).iterateAll()
                .forEach(blob -> fileList.add(blob.getName()));
        return fileList;
    }
}