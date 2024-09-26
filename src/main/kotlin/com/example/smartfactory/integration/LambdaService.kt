package com.example.smartfactory.integration

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvocationType
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import software.amazon.awssdk.services.lambda.model.InvokeResponse

@Service
class LambdaService(private val lambdaClient: LambdaClient, private val awsProperties: AwsProperties) {

    @Bean
    fun invokeLambda(payload: String): String? {

        val requestPayload = SdkBytes.fromUtf8String(payload);
        val invokeRequest = InvokeRequest.builder()
            .functionName(awsProperties.lambdaFunctionName)
            .payload(requestPayload)
            .invocationType(InvocationType.EVENT)
            .build()
        val response: InvokeResponse = lambdaClient.invoke(invokeRequest)

        return response.payload().asUtf8String()
    }

    fun getS3BucketName(): String {
        return awsProperties.s3BucketName
    }
}
