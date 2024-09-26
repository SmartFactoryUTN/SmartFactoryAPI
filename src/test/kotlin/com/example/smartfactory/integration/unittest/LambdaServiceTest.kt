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
        val payload = "{\"key1\":\"value1\"}"
        val expectedResponsePayload = "response from lambda"

        // Create a mock response
        val mockResponse: InvokeResponse = mockk {
            every { payload() } returns SdkBytes.fromUtf8String(expectedResponsePayload)
        }

        // Mock the invoke call
        every { mockLambdaClient.invoke(any<InvokeRequest>()) } returns mockResponse

        // When
        val response = lambdaService.invokeLambda(payload)

        // Then
        assertEquals(expectedResponsePayload, response)

        // Verify that the lambdaClient.invoke method was called once
        verify(exactly = 1) { mockLambdaClient.invoke(any<InvokeRequest>()) }
    }

}
