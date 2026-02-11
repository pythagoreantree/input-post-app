package ru.post.PostApp;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.post.PostApp.publisher.EventPublisher;

import java.util.List;

@SpringBootApplication
public class PostAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostAppApplication.class, args);
	}

	@Autowired
	private List<EventPublisher> publishers;

	@PostConstruct
	void checkPublishers() {
		System.out.println(">>>> Found " + publishers.size() + " EventPublisher beans:");
		publishers.forEach(p -> System.out.println("  - " + p.getClass().getSimpleName()));
	}

}
