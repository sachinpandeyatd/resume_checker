package com.resChk.serviceClient;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

    String analyzeResume(MultipartFile file) throws Exception;
}
