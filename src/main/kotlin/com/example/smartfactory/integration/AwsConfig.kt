package com.example.smartfactory.integration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.regions.Region

@Service
@Configuration
class AwsConfig(private val awsProperties: AwsProperties) {

    @Bean
    fun lambdaClient(): LambdaClient {
        val credentials = AwsBasicCredentials.create(awsProperties.accessKey, awsProperties.secretKey)

        return LambdaClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(awsProperties.region))
            .build()
    }

}

@Service
@Configuration
@ConfigurationProperties(prefix = "aws")
class AwsProperties {
    lateinit var accessKey: String
    lateinit var secretKey: String
    lateinit var region: String
    lateinit var lambdaFunctionName: String
    lateinit var s3BucketName: String
}
