package com.travel.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 * @author jianping5
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;

    private static final long serialVersionUID = 1L;
}