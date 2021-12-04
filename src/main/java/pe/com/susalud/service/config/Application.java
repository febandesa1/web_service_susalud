package pe.com.susalud.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ComponentScan(basePackages = "pe.com.susalud")
@Configuration
@EnableWebMvc
@ImportResource("classpath:/spring/applicationContext.xml")
public class Application {

}
