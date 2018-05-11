package com.mjk.fastdfs.controllers;

import com.mjk.fastdfs.client.ErrorCode;
import com.mjk.fastdfs.client.FastdfsClient;
import com.mjk.fastdfs.client.FastdfsException;
import com.mjk.fastdfs.client.FileCheck;
import com.mjk.fastdfs.client.FileResponseData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件接口
 *
 * @author ma-jk
 * @date 2018-05-11 11:29
 **/
@Controller
@RequestMapping("/fastdfs")
public class FileController {
    private FastdfsClient fastdfsClient = new FastdfsClient();

    /**
     * 文件服务器地址
     */
    @Value("${file_server_addr}")
    private String fileServerAddr;

    /**
     * FastDFS秘钥
     */
    @Value("${fastdfs.http_secret_key}")
    private String fastDFSHttpSecretKey;

    @RequestMapping("/test")
    @ResponseBody
    public FileResponseData test(){
        return new FileResponseData(true);
    }

    /**
     * 上传文件通用，只上传文件到服务器，不会保存记录到数据库
     *
     * @param file 文件
     * @param request 请求
     * @return 返回文件路径等信息
     */
    @RequestMapping("/upload/file/sample")
    @ResponseBody
    public FileResponseData uploadFileSample(@RequestParam MultipartFile file, HttpServletRequest request){
        return uploadSample(file, request);
    }

    /**
     * 只能上传图片，只上传文件到服务器，不会保存记录到数据库. <br>
     * 会检查文件格式是否正确，默认只能上传 ['png', 'gif', 'jpeg', 'jpg'] 几种类型.
     *
     * @param file 文件
     * @param request 请求
     * @return 返回文件路径等信息
     */
    @RequestMapping("/upload/image/sample")
    @ResponseBody
    public FileResponseData uploadImageSample(@RequestParam MultipartFile file, HttpServletRequest request){
        // 检查文件类型
        if(!FileCheck.checkImage(file.getOriginalFilename())){
            FileResponseData responseData = new FileResponseData(false);
            responseData.setCode(ErrorCode.FILE_TYPE_ERROR_IMAGE.code);
            responseData.setMessage(ErrorCode.FILE_TYPE_ERROR_IMAGE.message);
            return responseData;
        }

        return uploadSample(file, request);
    }

    /**
     * 只能上传文档，只上传文件到服务器，不会保存记录到数据库. <br>
     * 会检查文件格式是否正确，默认只能上传 ['pdf', 'ppt', 'xls', 'xlsx', 'pptx', 'doc', 'docx'] 几种类型.
     *
     * @param file 文件
     * @param request 请求
     * @return 返回文件路径等信息
     */
    @RequestMapping("/upload/doc/sample")
    @ResponseBody
    public FileResponseData uploadDocSample(@RequestParam MultipartFile file, HttpServletRequest request){
        // 检查文件类型
        if(!FileCheck.checkDoc(file.getOriginalFilename())){
            FileResponseData responseData = new FileResponseData(false);
            responseData.setCode(ErrorCode.FILE_TYPE_ERROR_DOC.code);
            responseData.setMessage(ErrorCode.FILE_TYPE_ERROR_DOC.message);
            return responseData;
        }

        return uploadSample(file, request);
    }

    /**
     * 以附件形式下载文件
     *
     * @param filePath 文件地址 group1/M00/00/00/CgENTVr0M0qAYDM7AAA8e7dSQCI54.jpeg
     * @param response 响应
     */
    @RequestMapping("/download/file")
    public void downloadFile(@RequestParam String filePath, HttpServletResponse response) throws FastdfsException {
        try {
            fastdfsClient.downloadFile(filePath, response);
        } catch (FastdfsException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 获取图片 使用输出流输出字节码，可以使用< img>标签显示图片<br>
     *
     * @param filePath 图片地址
     * @param response 响应
     */
    @RequestMapping("/download/image")
    public void downloadImage(@RequestParam String filePath, HttpServletResponse response) throws FastdfsException {
        filePath = "group1/M00/00/00/CgENTVr0M0qAYDM7AAA8e7dSQCI54.jpeg";
        try {
            fastdfsClient.downloadFile(filePath, response.getOutputStream());
        } catch (FastdfsException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据指定的路径删除服务器文件，适用于没有保存数据库记录的文件
     *
     * @param filePath 文件地址
     */
    @RequestMapping("/delete/file")
    public FileResponseData deleteFile(@RequestParam String filePath) {
        FileResponseData responseData = new FileResponseData();
        try {
            fastdfsClient.deleteFile(filePath);
        } catch (FastdfsException e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    /**
     * 获取访问文件的token
     *
     * @param filePath 文件地址
     * @return token
     */
    @RequestMapping("/get/token")
    @ResponseBody
    public FileResponseData getToken(String filePath){
        filePath = "group1/M00/00/00/CgENTVrrtkCAD9lkAAA8e7dSQCI69.jpeg";
        FileResponseData responseData = new FileResponseData();
        // 设置访文件的Http地址. 有时效性.
        String token = FastdfsClient.getToken(filePath, fastDFSHttpSecretKey);
        responseData.setToken(token);
        responseData.setHttpUrl(fileServerAddr+"/"+filePath+"?"+token);

        return responseData;
    }

    /**
     * 上传通用方法，只上传到服务器，不保存记录到数据库
     *
     * @param file 文件
     * @param request 请求
     * @return 结果集
     */
    public FileResponseData uploadSample(MultipartFile file, HttpServletRequest request){
        FileResponseData responseData = new FileResponseData();
        try {
            // 上传到服务器
            String filepath = fastdfsClient.uploadFileWithMultipart(file);

            responseData.setFileName(file.getOriginalFilename());
            responseData.setFilePath(filepath);
            responseData.setFileType(FastdfsClient.getFilenameSuffix(file.getOriginalFilename()));
            // 设置访文件的Http地址. 有时效性.
            String token = FastdfsClient.getToken(filepath, fastDFSHttpSecretKey);
            responseData.setToken(token);
            responseData.setHttpUrl(fileServerAddr+"/"+filepath+"?"+token);
        } catch (FastdfsException e) {
            responseData.setSuccess(false);
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
        }

        return responseData;
    }
}
