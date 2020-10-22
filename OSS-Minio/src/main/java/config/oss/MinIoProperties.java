package config.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author 叶成浪
 * @date 2020.10.22
 */
@Data
@ConfigurationProperties(prefix = "min.io")
public class MinIoProperties {

    /**
     * 是否开启minio
     */
    private String enable;
    /**
     * 桶
     */
    private String endpoint;
    /**
     * 账号
     */
    private String accessKey;
    /**
     * 密码
     */
    private String secretKey;
    /**
     * 默认桶
     */
    private String defaultBucket;

}
