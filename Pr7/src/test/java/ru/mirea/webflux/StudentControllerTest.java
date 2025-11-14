package ru.mirea.webflux;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    StudentRepositoryStub repository;

    Student studentIgor;

    Student studentMatvei;

    @BeforeEach
    void setUp() {
        repository.reset();
        studentIgor = repository.save(Student.builder()
                .name("Igor Zholobov")
                .age(21)
                .build())
                .block();
        studentMatvei = repository.save(Student.builder()
                .name("Matvei Frolov")
                .age(21)
                .build())
                .block();
    }

    @Test
    void whenGetStudentsThenSuccess() {
        List<Student> students = webTestClient.get().uri("/students")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Student.class)
                .getResponseBody()
                .collectList()
                .block();

        assertNotNull(students);
        assertEquals(2, students.size());
        assertTrue(students.containsAll(List.of(studentIgor, studentMatvei)));
    }

    @Test
    void whenGetStudentThenSuccess() {
        Student student = webTestClient.get().uri("/students/" + studentIgor.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Student.class)
                .getResponseBody()
                .blockFirst();

        assertNotNull(student);
        assertEquals(studentIgor, student);
    }

    @Test
    void whenGetNonExistentStudentThenNotFound() {
        long nonExistentId = 999L;
        webTestClient.get().uri("/students/" + nonExistentId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreateStudentThenSuccess() {
        Student studentIvan = Student.builder().name("Ivan Ivanov").age(22).build();

        Student created = webTestClient.post().uri("/students")
                .bodyValue(studentIvan)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Student.class)
                .getResponseBody()
                .blockFirst();

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(studentIvan.getName(), created.getName());
        assertEquals(studentIvan.getAge(), created.getAge());

        List<Student> students = webTestClient.get().uri("/students")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Student.class)
                .getResponseBody()
                .collectList()
                .block();

        assertNotNull(students);
        assertTrue(students.stream().anyMatch(s -> s.getId().equals(created.getId())));
    }

    @Test
    void whenUpdateStudentThenSuccess() {
        Student updatedInfo = Student.builder().name("Updated Igor Zholobov").age(22).build();

        Student updatedStudent = webTestClient.put()
                .uri("/students/" + studentIgor.getId())
                .bodyValue(updatedInfo)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Student.class)
                .getResponseBody()
                .blockFirst();

        assertNotNull(updatedStudent);
        assertEquals(studentIgor.getId(), updatedStudent.getId());
        assertEquals(updatedInfo.getName(), updatedStudent.getName());
        assertEquals(updatedInfo.getAge(), updatedStudent.getAge());

        Student studentWithIgorId = webTestClient.get().uri("/students/" + studentIgor.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Student.class)
                .getResponseBody()
                .blockFirst();

        assertNotNull(studentWithIgorId);
        assertEquals(updatedInfo.getName(), studentWithIgorId.getName());
        assertEquals(updatedInfo.getAge(), studentWithIgorId.getAge());
    }

    @Test
    void whenUpdateStudentThenNotFound() {
        long nonExistentId = 999L;
        Student updatedInfo = Student.builder().name("Updated Unknown Student").age(25).build();

        webTestClient.put()
                .uri("/students/" + nonExistentId)
                .bodyValue(updatedInfo)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenDeleteStudentThenSuccess() {
        webTestClient.delete().uri("/students/" + studentIgor.getId())
                .exchange()
                .expectStatus().isOk();

        List<Student> students = webTestClient.get().uri("/students")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Student.class)
                .getResponseBody()
                .collectList()
                .block();

        assertNotNull(students);
        assertEquals(1, students.size());
        assertFalse(students.contains(studentIgor));
    }

    @Test
    void whenDeleteStudentThenNotFound() {
        long nonExistentId = 999L;
        webTestClient.delete().uri("/students/" + nonExistentId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @TestConfiguration
    static class StubRepoTestConfig {

        @Bean
        @Primary
        public StudentRepositoryStub studentRepositoryStub() {
            return new StudentRepositoryStub();
        }
    }
}
