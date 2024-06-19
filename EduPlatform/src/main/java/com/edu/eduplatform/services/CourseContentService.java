package com.edu.eduplatform.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
public class CourseContentService {

    private static final String BUCKET_NAME = "gs://eduplatform-ed3a0.appspot.com";
    private final Storage storage;

    public CourseContentService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public String uploadFile(String courseId, String folderName, MultipartFile file) throws IOException {
        String fileName = folderName + "/" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(BUCKET_NAME, "courses/" + courseId + "/" + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
        return fileName;
    }

    public URL getFileUrl(String courseId, String fileName) {
        BlobId blobId = BlobId.of(BUCKET_NAME, "courses/" + courseId + "/" + fileName);
        Blob blob = storage.get(blobId);
        if (blob != null) {
            return blob.signUrl(1, TimeUnit.HOURS); // URL valid for 1 hour
        }
        return null;
    }

    public boolean deleteFile(String courseId, String fileName) {
        BlobId blobId = BlobId.of(BUCKET_NAME, "courses/" + courseId + "/" + fileName);
        return storage.delete(blobId);
    }
}
