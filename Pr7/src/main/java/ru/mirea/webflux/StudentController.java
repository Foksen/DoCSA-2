package ru.mirea.webflux;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentController {

    StudentRepository studentRepository;

    @GetMapping
    public Flux<Student> getStudents() {
        return studentRepository.findAll()
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")));
    }

    @GetMapping("/{id}")
    public Mono<Student> getStudent(@PathVariable Long id) {
        return studentRepository.findById(id);
    }

    @PostMapping
    public Mono<Student> createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @PutMapping("/{id}")
    public Mono<Student> updateStudent(@PathVariable Long id, @RequestBody Student updated) {
        return studentRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")))
                .flatMap(student -> {
                    student.setName(updated.getName());
                    student.setAge(updated.getAge());
                    return studentRepository.save(student);
                });
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteStudent(@PathVariable Long id) {
        return studentRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")))
                .flatMap(student -> studentRepository.deleteById(id));
    }
}

