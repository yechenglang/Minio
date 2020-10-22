package cn.cloud.ycl.model;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶成浪
 * @date 2020.10.22
 */
@Data
@ApiModel("响应参数:文件描述对象")
public class ObjectMeta implements Serializable {

    @ApiModelProperty("桶名称")
    private String bucketName;

    /**
     * 存储名称
     */
    @ApiModelProperty("存储名称")
    private String fileName;

    /**
     * 原始名称
     */
    @ApiModelProperty("原始真实文件名称")
    private String originalName;
    /**
     * 对象大小
     */
    @ApiModelProperty("文件大小")
    private Long size;
    /**
     * 内容类型
     */
    @ApiModelProperty("文件类型")
    private String contentType;
}
