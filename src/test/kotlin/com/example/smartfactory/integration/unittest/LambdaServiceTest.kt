package com.example.smartfactory.integration.unittest

import com.example.smartfactory.integration.AwsProperties
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
    fun `should invoke lambda function and return payload`() {
        // Given
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
        val expectedResponsePayload = "response from lambda"

        // Create a mock response
        val mockResponse: InvokeResponse = mockk {
            every { payload() } returns SdkBytes.fromUtf8String(expectedResponsePayload)
        }

        every { awsProperties.lambdaFunctionName } returns "arn:aws:lambda:us-east-2:593749507085:function:servicio-de-tizada-test-lambda"

        // Mock the invoke call
        every { mockLambdaClient.invoke(any<InvokeRequest>()) } returns mockResponse

        // When
        val response = lambdaService.invokeLambdaAsync(payload)

        // Then
        assertEquals(expectedResponsePayload, response)

        // Verify that the lambdaClient.invoke method was called once
        verify(exactly = 1) { mockLambdaClient.invoke(any<InvokeRequest>()) }
    }

}
