# S3 File Management Service

This is a **Spring Boot application** built using **Java 17** that provides REST APIs to upload, download, and delete files from an **Amazon S3 bucket**.  

## 🚀 Features
- Upload a file to S3 bucket  
- Download a file from S3 bucket by file name  
- Delete a file from S3 bucket by file name  

## 📌 APIs

### 1. Upload File
- **Endpoint:** `POST /file/upload`  
- **Request:** `multipart/form-data`  
- **Body Parameter:**  
  - `file`: The file to be uploaded  

### 2. Download File
- **Endpoint:** `GET /file/download/{fileName}`  
- **Path Variable:**  
  - `fileName`: The name of the file stored in S3  

### 3. Delete File
- **Endpoint:** `DELETE /file/delete/{fileName}`  
- **Path Variable:**  
  - `fileName`: The name of the file stored in S3  

---

## ⚙️ Tech Stack
- **Java:** 17  
- **Spring Boot:** 3.x (Spring Web Starter)  
- **AWS SDK v2** (S3 Client)  

## 📦 Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.23.10</version>
</dependency>
