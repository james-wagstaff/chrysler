package com.identifix.contentlabelingservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ['com.identifix'])
class ContentLabelingServiceApplication {

	static void main(String[] args) {
		SpringApplication.run(ContentLabelingServiceApplication, args)
	}

}
