package config.oss;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 叶成浪
 * @date 2020.10.22
 */
@Component
@Configuration
@EnableConfigurationProperties(MinIoProperties.class)
public class MinIoUtils {

    private MinIoProperties minIo;


    public MinIoUtils(MinIoProperties minIo) {
        this.minIo = minIo;
    }

    private MinioClient instance;


    @PostConstruct
    public void init() {
        try {
            instance = new MinioClient(minIo.getEndpoint(), minIo.getAccessKey(), minIo.getSecretKey());
        } catch (InvalidPortException e) {
            e.printStackTrace();
        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断 bucket是否存在
     *
     * @param bucketName
     * @return
     */
    public boolean bucketExists(String bucketName) {
        try {
            return instance.bucketExists(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 创建 bucket
     *
     * @param bucketName
     */
    public void makeBucket(String bucketName) {
        try {
            boolean isExist = instance.bucketExists(bucketName);
            if (!isExist) {
                instance.makeBucket(bucketName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取bucket对应的文件列表
     *
     * @param bucketName
     * @return
     */
    public List<Object> list(String bucketName) {
        try {
            Iterable<Result<Item>> results = instance.listObjects(bucketName);
            Iterator<Result<Item>> iterator = results.iterator();
            List<Object> items = new ArrayList<>();
            String format = "{'fileName':'%s','fileSize':'%s'}";
            while (iterator.hasNext()) {
                Item item = iterator.next().get();
                items.add(JSON.parse(String.format(format, item.objectName(), formatFileSize(item.size()))));
            }
            return items;
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (InvalidBucketNameException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * 文件上传
     *
     * @param bucketName
     * @param objectName
     * @param filename
     */
    public void putObject(String bucketName, String objectName, String filename) {
        try {
            instance.putObject(bucketName, objectName, filename, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     *
     * @param bucketName
     * @param objectName
     * @param stream
     */
    public void putObject(String bucketName, String objectName, InputStream stream) {
        try {
            instance.putObject(bucketName, objectName, stream, new PutObjectOptions(stream.available(), -1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param objectName
     */
    public void removeObject(String bucketName, String objectName) {
        try {
            instance.removeObject(bucketName, objectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    public InputStream getObject(String bucketName, String objectName) {
        try {
            InputStream object = instance.getObject(bucketName, objectName);
            return object;
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidBucketNameException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取文件统计
     *
     * @param bucket
     * @param fileName
     * @return
     */
    public ObjectStat statObject(String bucket, String fileName) {
        try {
            ObjectStat stat = instance.statObject(bucket, fileName);
            return stat;
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidBucketNameException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 显示文件大小信息单位
     *
     * @param fileS
     * @return
     */
    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }

        final Long b = 1024L;
        final Long kb = 1048576L;
        final Long gb = 1073741824L;

        if (fileS < b) {
            fileSizeString = df.format((double) fileS) + " B";
        } else if (fileS < kb) {
            fileSizeString = df.format((double) fileS / b) + " KB";
        } else if (fileS < gb) {
            fileSizeString = df.format((double) fileS / kb) + " MB";
        } else {
            fileSizeString = df.format((double) fileS / gb) + " GB";
        }
        return fileSizeString;
    }


}
