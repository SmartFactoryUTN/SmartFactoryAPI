package com.example.smartfactory.unittest.application.Molde

import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.application.Molde.MoldeService
import com.example.smartfactory.integration.LambdaService
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile

class MoldeServiceTest {

    private lateinit var moldeService: MoldeService

    @MockK
    private lateinit var moldeRepository: MoldeRepository

    @MockK
    private lateinit var lambdaService: LambdaService

    @BeforeEach
    fun setUp() {
        moldeRepository = mockk()
        lambdaService = mockk()
        moldeService = MoldeService(moldeRepository, lambdaService)
    }

    @Test
    fun `should return true for valid SVG file`() {
        val svgFile = MockMultipartFile("file", "test.svg", "image/svg+xml", "<svg></svg>".toByteArray())
        assertTrue(moldeService.isSvgFile(svgFile))
    }

    @Test
    fun `should return false for non-SVG MIME type`() {
        val nonSvgFile = MockMultipartFile("file", "test.svg", "image/png", ByteArray(0))
        assertFalse(moldeService.isSvgFile(nonSvgFile))
    }

    @Test
    fun `should return false for non-SVG file extension`() {
        val nonSvgFile = MockMultipartFile("file", "test.png", "image/svg+xml", ByteArray(0))
        assertFalse(moldeService.isSvgFile(nonSvgFile))
    }
}
