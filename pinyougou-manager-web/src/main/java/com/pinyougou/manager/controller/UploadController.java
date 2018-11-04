package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;


@RestController
@RequestMapping()
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String fileServerUrl;

	@RequestMapping("/upload")
	public Result search(@RequestBody MultipartFile file){
        try {
            //截取扩展名
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.indexOf(".") + 1);
            //创建fastDFS客户端
            FastDFSClient fastDFSClient=new FastDFSClient("classpath:config/fdfs_client.conf");
            //上传文件
            String uploadFile = fastDFSClient.uploadFile(file.getBytes(), extName);
            //获得文件全路径（网络路径）
            uploadFile=fileServerUrl+uploadFile;
            return new Result(true, uploadFile);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
	}
	
}
