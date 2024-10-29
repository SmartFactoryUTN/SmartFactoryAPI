package com.example.smartfactory.integration

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import aws.smithy.kotlin.runtime.content.ByteStream
import com.example.smartfactory.Exceptions.UploadMoldeException
import com.example.smartfactory.application.Molde.CreateMoldeRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvocationType
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import software.amazon.awssdk.services.lambda.model.InvokeResponse
import java.nio.charset.StandardCharsets
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

    suspend fun uploadFile(molde: CreateMoldeRequest, uuid: UUID): String {
        val bucketName = getS3BucketName()
        val fileName = "${molde.userUUID}/molde-${uuid}.svg"

        return try {
            // Sanitize SVG content
            val sanitizedSvgContent = sanitizeSvg(String(molde.svg.bytes)).toByteArray()

            // Convert the MultipartFile to a byte array for upload
            val fileBytes = ByteStream.fromBytes(sanitizedSvgContent)

            // Build the PutObjectRequest
            val putObjectRequest = PutObjectRequest {
                bucket = bucketName
                key = fileName
                body = fileBytes
                contentType = "image/svg+xml"
            }

            // Upload the file to S3
            s3Client.putObject(putObjectRequest)

            // Return the file URL
            "https://${bucketName}.s3.amazonaws.com/${fileName}"

        } catch (e: S3Exception) {
            e.printStackTrace()
            throw UploadMoldeException("Error uploading file to S3")
        } catch (e: RuntimeException) {
            throw UploadMoldeException("Error ocurred")
        }
    }

    suspend fun uploadContainer(uuid: UUID, svg: String): String {
        val bucketName = getS3BucketName()
        val fileName = "14bd6578-0436-420d-9c64-2beda866fcf0/$uuid.svg"

        return try {
            val byteArray = svg.toByteArray(StandardCharsets.UTF_8)

            // Build the PutObjectRequest
            val putObjectRequest = PutObjectRequest {
                bucket = bucketName
                key = fileName
                contentType = "image/svg+xml"
                body = ByteStream.fromBytes(byteArray)
            }

            // Upload the file to S3
            s3Client.putObject(putObjectRequest)

            // Return the file URL
            "https://${bucketName}.s3.amazonaws.com/${fileName}"

        } catch (e: S3Exception) {
            throw UploadMoldeException("Error uploading file to S3")
        } catch (e: RuntimeException) {
            throw UploadMoldeException("Error ocurred")
        }
    }

    // SVG sanitization logic
    private fun sanitizeSvg(svgContent: String): String {
        val doc: Document = Jsoup.parse(svgContent, "", org.jsoup.parser.Parser.xmlParser())

        val safelist = Safelist()
            .addTags("svg", "path", "circle", "rect", "g", "line", "polygon", "polyline", "text")
            .addAttributes("svg", "xmlns", "viewBox", "width", "height")
            .addAttributes("path", "d", "fill", "stroke", "stroke-width")
            .addAttributes("circle", "cx", "cy", "r", "fill", "stroke")
            .addAttributes("rect", "x", "y", "width", "height", "fill", "stroke")
            .addAttributes("g", "transform")
            .addAttributes("line", "x1", "y1", "x2", "y2", "stroke")
            .addAttributes("polygon", "points", "fill", "stroke")
            .addAttributes("polyline", "points", "fill", "stroke")
            .addAttributes("text", "x", "y", "font-size", "fill")

        return Jsoup.clean(doc.html(), "", safelist, Document.OutputSettings().prettyPrint(false))
    }

}

data class InvokeTizadaResponse(val statusCode: String, val payload: String)
