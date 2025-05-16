package com.resChk.serviceClient;

import com.resChk.dto.AnalysisResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

    AnalysisResponseDTO analyzeResume(MultipartFile file) throws Exception;
}
