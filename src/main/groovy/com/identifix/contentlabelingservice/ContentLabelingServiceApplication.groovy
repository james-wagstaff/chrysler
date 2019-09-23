package com.identifix.contentlabelingservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication(scanBasePackages = ['com.identifix'])
@EnableCaching
class ContentLabelingServiceApplication {

	static void main(String[] args) {
		SpringApplication.run(ContentLabelingServiceApplication, args)
	}

}
