package com.mjk.fastdfs.client;

/**
 * FastDFS 上传下载异常信息
 *
 * @author ma-jk
 * @date 2018-05-11 11:07
 **/
public class FastdfsException extends Exception{

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误消息
     */
    private String message;

    FastdfsException(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
