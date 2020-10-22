package cn.cloud.ycl.controller;

import cn.cloud.ycl.config.oss.MinIoProperties;
import cn.cloud.ycl.config.oss.MinIoUtils;
import io.minio.ObjectStat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import cn.cloud.ycl.model.ObjectMeta;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * @author 叶成浪
 * 文件处理
 */
@Slf4j
@Controller("/minio/oss")
@Api(tags = {"文件相关接口"})
public class MinioOssController {


    @Autowired
    private MinIoProperties minIoProperties;
    @Autowired
    private MinIoUtils minIoUtils;

    /**
     * 获取list
     *
     * @return
     */
    @GetMapping("/list")
    @ResponseBody
    @ApiOperation("获取所有文件列表")
    public List<Object> list() {
        List<Object> list = minIoUtils.list(minIoProperties.getDefaultBucket());
        return list;
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    @ApiOperation("上传文件")
    public ObjectMeta upload(MultipartFile file) {
        if (file == null) {
            throw new RuntimeException("文件不能为空");
        }
        if (!minIoUtils.bucketExists(minIoProperties.getDefaultBucket())) {
            minIoUtils.makeBucket(minIoProperties.getDefaultBucket());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());
        ObjectMeta objectMeta = new ObjectMeta();
        objectMeta.setSize(file.getSize());
        objectMeta.setContentType(file.getContentType());
        // 获取真实文件名
        String oldFileName = file.getOriginalFilename();
        objectMeta.setOriginalName(oldFileName);
        // 重命名文件
        String fileNewName = UUID.randomUUID().toString() + oldFileName.substring(oldFileName.lastIndexOf('.'));
        // 文件存储的目录结构
        String objectName = format + File.separator + fileNewName;
        InputStream in = null;
        try {
            in = file.getInputStream();
            minIoUtils.putObject(minIoProperties.getDefaultBucket(), objectName, in);
            objectMeta.setBucketName(minIoProperties.getDefaultBucket());
            //完成上传以后再保存文件完整路径
            objectMeta.setFileName(objectName);
        } catch (IOException e) {
            log.info("上传失败");
            e.printStackTrace();
            throw  new RuntimeException("创建失败");
        } finally {
            try {
                IOUtils.close(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return objectMeta;
    }

    /**
     * 文件下载
     *
     * @param request
     * @param response
     */
    @ApiOperation("文件下载")
    @RequestMapping(value = "/download", method = {RequestMethod.GET, RequestMethod.POST})
    public void download(HttpServletRequest request,
                         HttpServletResponse response,
                         @RequestParam("fileName") String fileName,
                         @RequestParam("oldFileName") String oldFileName) {
        InputStream in = null;
        String name = null;
        if (fileName.contains(minIoProperties.getEndpoint() + File.separator + minIoProperties.getDefaultBucket())) {
            name = fileName.replace(minIoProperties.getEndpoint() + File.separator + minIoProperties.getDefaultBucket(), "");
        } else {
            name = fileName;
        }
        ObjectStat objectStat = minIoUtils.statObject(minIoProperties.getDefaultBucket(), name);
        response.setContentType(objectStat.contentType());
        try {
            if (request.getHeader("User-Agent").toLowerCase().contains("firefox")) {
                oldFileName = new String(oldFileName.getBytes("GB2312"),
                        "ISO-8859-1");
            } else {
                oldFileName = URLEncoder.encode(oldFileName, "UTF-8");
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + oldFileName);
            in = minIoUtils.getObject(minIoProperties.getDefaultBucket(), name);
            OutputStream out = response.getOutputStream();
            IOUtils.copy(in, out);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                IOUtils.close(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    @DeleteMapping("/delete")
    public void delete(@RequestParam("fileName") String fileName) {
        try {
            minIoUtils.removeObject(minIoProperties.getDefaultBucket(), fileName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
