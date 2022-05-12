package com.nttdata.project.creditBank.controller;

import com.google.gson.Gson;
import com.nttdata.project.creditBank.model.BootCoin;
import com.nttdata.project.creditBank.model.BootCoinTransaction;
import com.nttdata.project.creditBank.model.Person;
import com.nttdata.project.creditBank.service.PersonService;
import com.nttdata.project.creditBank.service.kafkaImpl.BootCoinProducer;
import com.nttdata.project.creditBank.util.KafkaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/people")
public class PersonController {

    @Autowired
    private PersonService PersonService;

    @Autowired
    private BootCoinProducer producer;

    @PostMapping
    public ResponseEntity<Mono<Person>> addPerson(@RequestBody Person person) {
        producer.publish(new Gson().toJson(person), KafkaUtils.BOOT_COIN_KAFKA_TOPIC);
        return ResponseEntity.status(HttpStatus.CREATED).body(PersonService.addPerson(person));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<Person>> getPerson(@PathVariable String id) {
        return ResponseEntity.ok(PersonService.getPerson(id));
    }

    @GetMapping
    public ResponseEntity<Flux<Person>> getAllPersons() {
        return ResponseEntity.ok().body(PersonService.getAllPersons());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePerson(@PathVariable String id) {
        return PersonService.deletePerson(id)
                .map(r -> ResponseEntity.ok().<Void> build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<Person>> update(@PathVariable String id, @RequestBody Person person) {
        return ResponseEntity.status(HttpStatus.CREATED).body(PersonService.updatePerson(person, id));
    }
}
