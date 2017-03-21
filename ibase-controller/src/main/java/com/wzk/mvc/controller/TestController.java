package com.wzk.mvc.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/test")
public class TestController {

	@Autowired
    private ServletContext servletContext;
	
	/**
     * 上传单个文件的页面
     * @return 页面的路径
     */
    @RequestMapping(value = "/upload-file", method = RequestMethod.GET)
    public String uploadFilePage() {
        return "/test/upload-file";
    }
    /**
     * 上传单个文件
     *
     * @param file 上传文件 MultipartFile 的对象
     * @return 上传的结果
     */
    @RequestMapping(value = "/upload-file", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        saveFile(file);
        return "Success";
    }
    /**
     * 把 HTTP 请求中的文件流保存到本地
     *
     * @param file MultipartFile 的对象
     */
    private boolean saveFile(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // getRealPath() 取得 WEB-INF 所在文件夹路径
                // 如果参数是 "/temp", 当 temp 存在时返回 temp 的本地路径, 不存在时返回 null/temp (无效路径)
                String path = servletContext.getRealPath("") + "upload" +File.separator + file.getOriginalFilename();
                FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(path));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * 上传多个文件的页面
     * @return 页面的路径
     */
    @RequestMapping(value = "/upload-files", method = RequestMethod.GET)
    public String uploadFilesPage() {
        return "/test/upload-files";
    }
    /**
     * 上传多个文件
     *
     * @param files 上传文件 MultipartFile 的对象数组
     * @return 上传的结果
     */
    @RequestMapping(value = "/upload-files", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFiles(@RequestParam("files") MultipartFile[] files) {
        for (MultipartFile file : files) {
            saveFile(file);
        }
        return "Success";
    }
}
