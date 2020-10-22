import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * start class
 *
 * @author 叶成浪
 * @date 2020.10.22
 */
@EnableOpenApi
@SpringBootApplication
@Slf4j
public class OssMinioApplication {

    public static void main(String[] args) {
        SpringApplication.run(OssMinioApplication.class, args);
        log.info("SUCCESS");
    }

}
