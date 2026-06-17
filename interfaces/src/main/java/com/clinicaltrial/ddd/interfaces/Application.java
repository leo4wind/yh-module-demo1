package com.clinicaltrial.ddd.interfaces;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clinical Trial DDD Demo Application entry point.
 * Scans across all bounded context modules.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.clinicaltrial.ddd.common",
    "com.clinicaltrial.ddd.trial",
    "com.clinicaltrial.ddd.subject",
    "com.clinicaltrial.ddd.datacollection",
    "com.clinicaltrial.ddd.query",
    "com.clinicaltrial.ddd.dataexport",
    "com.clinicaltrial.ddd.statistics",
    "com.clinicaltrial.ddd.interfaces"
})
@EnableJpaRepositories(basePackages = {
    "com.clinicaltrial.ddd.trial.infrastructure.persistence",
    "com.clinicaltrial.ddd.subject.infrastructure.persistence",
    "com.clinicaltrial.ddd.datacollection.infrastructure.persistence",
    "com.clinicaltrial.ddd.query.infrastructure.persistence",
    "com.clinicaltrial.ddd.dataexport.infrastructure.persistence",
    "com.clinicaltrial.ddd.statistics.infrastructure.persistence"
})
@EntityScan(basePackages = {
    "com.clinicaltrial.ddd.trial.infrastructure.persistence",
    "com.clinicaltrial.ddd.subject.infrastructure.persistence",
    "com.clinicaltrial.ddd.datacollection.infrastructure.persistence",
    "com.clinicaltrial.ddd.query.infrastructure.persistence",
    "com.clinicaltrial.ddd.dataexport.infrastructure.persistence",
    "com.clinicaltrial.ddd.statistics.infrastructure.persistence"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
