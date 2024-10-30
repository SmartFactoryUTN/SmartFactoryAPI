package com.example.smartfactory.unittest.integration

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectResponse
import aws.sdk.kotlin.services.s3.model.S3Exception
import com.example.smartfactory.Exceptions.UploadMoldeException
import com.example.smartfactory.application.Molde.CreateMoldeRequest
import com.example.smartfactory.integration.AwsProperties
import com.example.smartfactory.integration.InvokeTizadaResponse
import com.example.smartfactory.integration.LambdaService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import software.amazon.awssdk.services.lambda.model.InvokeResponse
import java.util.*

class LambdaServiceTest {
    private val mockLambdaClient: LambdaClient = mockk()
    private val awsProperties: AwsProperties = mockk()
    private val s3Client: S3Client = mockk()

    private val lambdaService = LambdaService(mockLambdaClient, awsProperties, s3Client)

    @Test
    fun `should invoke lambda function and return InvokeTizadaResponse`() = runBlocking {
        // Arrange
        val payload = """{
        "tizadaUUID": "46054849-3c5c-435b-8fa2-9fb4ee390ba4",
        "user": "a038a4d2-8502-455f-a154-aa87b1cc3fec",
        "parts": [
            {
                "uuid": "moldeA",
                "quantity": 5
            },
            {
                "uuid": "moldeB",
                "quantity": 10
            },
            {
                "uuid": "moldeC",
                "quantity": 10
            }
        ],
        "bin": {
            "uuid": "contenedorA",
            "quantity": 1
        },
        "configuration": {
            "maxIterations": 20,
            "materialUtilization": 50,
            "timeout": 0
        }
    }"""

        val expectedResponsePayload = InvokeTizadaResponse("202", "''")
        val mockResponse: InvokeResponse = mockk {
            every { payload() } returns SdkBytes.fromUtf8String("''")
            every { statusCode() } returns 202
        }

        every {
            awsProperties.lambdaFunctionName
        } returns "arn:aws:lambda:us-east-2:593749507085:function:servicio-de-tizada-test-lambda"


        every { mockLambdaClient.invoke(any<InvokeRequest>()) } returns mockResponse

        // Act
        val response = lambdaService.invokeLambdaAsync(payload)

        // Assert
        assertEquals(expectedResponsePayload, response)

        verify(exactly = 1) { mockLambdaClient.invoke(any<InvokeRequest>()) }
    }

    @Test
    fun `should sanitize SVG and upload to S3`(): Unit = runBlocking {
        // Unsanitized SVG input
        val unsafeSvgContent = """
            <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                <script>alert('unsafe')</script>
                <circle cx="50" cy="50" r="40" fill="blue"/>
            </svg>
        """.trimIndent().toByteArray()

        // Mock CreateMoldeRequest and UUID
        val moldeRequest = mockk<CreateMoldeRequest> {
            every { svg.bytes } returns unsafeSvgContent
            every { userUUID } returns UUID.randomUUID()
        }
        val uuid = UUID.randomUUID()

        // Mock PutObjectRequest call to S3
        every {
            runBlocking { s3Client.putObject(any<PutObjectRequest>()) }
        } returns PutObjectResponse.invoke {  }

        every { lambdaService.getS3BucketName() } returns "servicio-de-tizada"

        // Act
        val result = lambdaService.uploadFile(moldeRequest, uuid)

        // Assertions
        assert(result.contains("s3.amazonaws.com"))
    }

    @Test
    fun `should throw UploadMoldeException on S3Exception`(): Unit = runBlocking {
        // Mock CreateMoldeRequest and UUID
        val moldeRequest = mockk<CreateMoldeRequest> {
            every { svg.bytes } returns "<svg></svg>".toByteArray()
            every { userUUID } returns UUID.randomUUID()
        }
        val uuid = UUID.randomUUID()

        // Simulate an S3Exception
        every {
            runBlocking { s3Client.putObject(any<PutObjectRequest>()) }
        } throws S3Exception("S3 error")

        every { lambdaService.getS3BucketName() } returns "servicio-de-tizada"

        // Act & Assert
        assertThrows<UploadMoldeException> {
            lambdaService.uploadFile(moldeRequest, uuid)
        }
    }

}
