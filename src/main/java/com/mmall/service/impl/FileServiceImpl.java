package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Service("iFileService")
public class FileServiceImpl implements IFileService {

    //要将上传后的文件名返回
    public String upload(MultipartFile file,String path){

        Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

        //获取上传时原始的文件名，上传之后因为可能存在重名的情况，所以采用UUID来进行命名这样可避免重名
        String fileName = file.getOriginalFilename();
        //获取获取扩展名,如abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);//这句的结果就是jpg

        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;//用UUID和原上传文件的扩展名生成上传文件的新名字

        //打印日志，通过{}来进行占位
        logger.info("开始上传文件,上传文件的文件名：{}，上传的路径是：{}，新文件名：{}",fileName,path,uploadFileName);

        //创建目录的file
        File fileDir = new File(path);
        //如果目录不存在，就要创建他，同时给他写权限
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        //创建文件
        File targetFile = new File(path,uploadFileName);

        //用spring mvc封装的file调用transferTo
        try {
            file.transferTo(targetFile);
            //此时文件已经上传成功,就已经传到upload这个文件夹下,下面要将他上传到FTP服务器上

            //将targetFile上传到我们的FTP服务器上，这里为什么用List呢，是因为用list可以装多个文件，可以进行批量上传
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            //上传完之后，删除upload下面的文件。因为这是一个tomcat服务的一个文件夹，上传到FTP之后就没必要存了，不然会越来越大
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }



}
