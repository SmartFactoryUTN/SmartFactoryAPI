package com.example.smartfactory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class SmartfactoryApplication

fun main(args: Array<String>) {
	runApplication<SmartfactoryApplication>(*args)
}
