package ru.mirea.webflux;

import io.micrometer.common.lang.NonNullApi;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@NonNullApi
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentRepositoryStub implements StudentRepository {

    Map<Long, Student> storage = new HashMap<>();
    AtomicLong nextId = new AtomicLong(1);

    public void reset() {
        storage.clear();
    }

    @Override
    public <S extends Student> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            entity.setId(nextId.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return Mono.just(entity);
    }

    @Override
    public <S extends Student> Flux<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Student> Flux<S> saveAll(Publisher<S> entityStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Student> findById(Long aLong) {
        Student s = storage.get(aLong);
        return (s == null) ? Mono.empty() : Mono.just(s);
    }

    @Override
    public Mono<Student> findById(Publisher<Long> id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Boolean> existsById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Boolean> existsById(Publisher<Long> id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flux<Student> findAll() {
        return Flux.fromIterable(storage.values());
    }

    @Override
    public Flux<Student> findAllById(Iterable<Long> longs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flux<Student> findAllById(Publisher<Long> idStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Long> count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Void> deleteById(Long aLong) {
        storage.remove(aLong);
        return Mono.empty();
    }

    @Override
    public Mono<Void> deleteById(Publisher<Long> id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Void> delete(Student entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Void> deleteAllById(Iterable<? extends Long> longs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends Student> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends Student> entityStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Void> deleteAll() {
        throw new UnsupportedOperationException();
    }
}
