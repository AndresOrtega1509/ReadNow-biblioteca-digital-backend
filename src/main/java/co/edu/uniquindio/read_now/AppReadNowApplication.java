package co.edu.uniquindio.read_now;

import co.edu.uniquindio.read_now.config.StripeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StripeProperties.class)
public class AppReadNowApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppReadNowApplication.class, args);
	}

}
