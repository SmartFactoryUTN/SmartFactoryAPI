package com.example.smartfactory.integration

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectResponse
import aws.sdk.kotlin.services.s3.model.S3Exception
import aws.smithy.kotlin.runtime.content.ByteStream
import com.example.smartfactory.Exceptions.UploadMoldeException
import com.example.smartfactory.application.Molde.CreateMoldeRequest
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvocationType
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import software.amazon.awssdk.services.lambda.model.InvokeResponse
import java.util.*

@Service
class LambdaService(
    private val lambdaClient: LambdaClient,
    private val awsProperties: AwsProperties,
    private val s3Client: S3Client,
    ) {

    fun invokeLambdaAsync(payload: String): InvokeTizadaResponse {

        val requestPayload = SdkBytes.fromUtf8String(payload);
        val invokeRequest = InvokeRequest.builder()
            .functionName(awsProperties.lambdaFunctionName)
            .payload(requestPayload)
            .invocationType(InvocationType.EVENT)
            .build()
        val response: InvokeResponse = lambdaClient.invoke(invokeRequest)

        return InvokeTizadaResponse(response.statusCode().toString(), response.payload().asUtf8String())
    }

    fun getS3BucketName(): String {
        return awsProperties.s3BucketName
    }

    suspend fun uploadFile(molde: CreateMoldeRequest): String {
        val bucketName = getS3BucketName()
        val fileName = "${molde.userUUID}/molde-${UUID.randomUUID()}.svg"

        return try {
            // Convert the MultipartFile to a byte array for upload
            val fileBytes = ByteStream.fromBytes(molde.svg.bytes)

            // Build the PutObjectRequest
            val putObjectRequest = PutObjectRequest {
                bucket = bucketName
                key = fileName
                body = fileBytes
                contentType = "image/svg+xml"
            }

            // Upload the file to S3
            val response: PutObjectResponse = s3Client.putObject(putObjectRequest)

            // Return the file URL
            "https://${bucketName}.s3.amazonaws.com/${fileName}"

        } catch (e: S3Exception) {
            throw UploadMoldeException("Error uploading file to S3")
        }catch (e: RuntimeException){
            throw UploadMoldeException("Error ocurred")
        }
    }

}

data class InvokeTizadaResponse(val statusCode: String, val payload: String)
