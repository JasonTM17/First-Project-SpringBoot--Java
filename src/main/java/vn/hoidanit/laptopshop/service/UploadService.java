package vn.hoidanit.laptopshop.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;

@Service
public class UploadService {

    private final ServletContext servletContext;

    private final ProductService productService;

    public UploadService(ServletContext servletContext, ProductService productService) {
        this.servletContext = servletContext;
        this.productService = productService;
    }

    public String handleSaveUploadFile(MultipartFile[] files, String targetFolder) {
        String rootPath = this.servletContext.getRealPath("/resources/images");
        String finalName = "";
        if (files == null || files.length == 0) {
            return "";
        }
        try {
            File dir = new File(rootPath + File.separator + targetFolder);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                byte[] bytes = file.getBytes();

                finalName = System.currentTimeMillis()
                        + "-" + file.getOriginalFilename();
                File serverFile = new File(dir.getAbsolutePath() + File.separator + finalName);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalName;
    }

}
