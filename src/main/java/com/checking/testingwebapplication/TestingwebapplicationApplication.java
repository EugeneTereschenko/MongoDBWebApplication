package com.checking.testingwebapplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class TestingwebapplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestingwebapplicationApplication.class, args);
	}

	String email = "test@test.com";

	@Bean
	CommandLineRunner runner(StudentRepository repository, MongoTemplate mongoTemplate) {
		return args -> {
			Address address = new Address(
					"England",
					"London",
					"NE9"
			);
			Student student = new Student(
					"James",
					"Rout",
					email,
					Gender.FEMALE,
					address,
					List.of("Computer Science", "Maths"),
					BigDecimal.TEN,
					LocalDateTime.now()
			);

			//usingMongoTempAndQuery(repository, mongoTemplate, email, student);
			repository.findStudentByEmail(email)
					.ifPresentOrElse(s -> {
						System.out.println(s + " already exists");
					}, () -> {
						System.out.println("Inserting student " + student);
						repository.insert(student);
					});

		};
	}

	private void usingMongoTempAndQuery(StudentRepository repository, MongoTemplate mongoTemplate, String email, Student student){
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(email));

		List<Student> students = mongoTemplate.find(query, Student.class);

		if (students.size() > 1) {
			throw new IllegalStateException(
					"found many students with email " + email);
		}

		if (students.isEmpty()) {
			System.out.println("Inserting student " + student);
			repository.insert(student);
		} else {
			System.out.println(student + " already exists");
		}
	}
}
