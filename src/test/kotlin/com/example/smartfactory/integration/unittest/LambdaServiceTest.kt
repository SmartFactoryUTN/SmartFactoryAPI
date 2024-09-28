package com.example.smartfactory.integration.unittest

import com.example.smartfactory.integration.AwsProperties
import com.example.smartfactory.integration.InvokeTizadaResponse
import com.example.smartfactory.integration.LambdaService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import software.amazon.awssdk.services.lambda.model.InvokeResponse

class LambdaServiceTest {
    private val mockLambdaClient: LambdaClient = mockk()
    private val awsProperties: AwsProperties = mockk()

    private val lambdaService = LambdaService(mockLambdaClient, awsProperties)

    @Test
    fun `should invoke lambda function and return InvokeTizadaResponse`() {
        // Arrange
        val payload = """{
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

}
