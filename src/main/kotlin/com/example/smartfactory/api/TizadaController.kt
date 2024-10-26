package com.example.smartfactory.api

import com.example.smartfactory.Domain.GenericResponse
import com.example.smartfactory.Exceptions.TizadaNotFoundException
import com.example.smartfactory.Repository.UsuarioRepository
import com.example.smartfactory.application.Tizada.Request.CreateTizadaRequest
import com.example.smartfactory.application.Tizada.Request.InvokeTizadaRequest
import com.example.smartfactory.application.Tizada.Request.TizadaNotificationRequest
import com.example.smartfactory.application.Tizada.Request.UpdateTizadaRequest
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.application.Tizada.TizadaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@PreAuthorize("hasAuthority('SCOPE_read:tizada')")
@RequestMapping("api/tizada")
@Tag(name = "Tizadas", description = "Endpoints para tizada")
class TizadaController(
    private val tizadaService: TizadaService,
    private val usuarioRepository: UsuarioRepository
) {

    @PostMapping("/invoke")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Solicitud de creación de tizada",
        description = "Invoca la ejecución de una nueva tizada"
    )
    @ApiResponse(responseCode = "204", description = "Tizada Invocada")
    @ApiResponse(responseCode = "401", description = "No autorizado")
    @ApiResponse(responseCode = "500", description = "Ocurrió un error. Intente nuevamente más tarde.")
    fun invokeTizada(@Valid @RequestBody request: InvokeTizadaRequest): ResponseEntity<TizadaResponse<Any>> {

        return try {
            val response = tizadaService.invokeTizada(request)
            return ResponseEntity.status(204).body(
                TizadaResponse(
                    status = "success",
                    data = mapOf("tizada" to response)
                )
            )
        } catch (e: Exception){
            ResponseEntity.status(500).body(
                TizadaResponse(
                    status = "error",
                    message = e.localizedMessage,
                    code = 500
                )
            )
        }
    }

    @PostMapping("/notification")
    @Operation(
        summary = "Webhook notificación de tizada finalizada",
        description = "Recibe la notificación de que finalizó una tizada y guarda el resultado en la base de datos"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tizada finalizada"),
            ApiResponse(responseCode = "401", description = "No autorizado"),
            ApiResponse(responseCode = "404", description = "Tizada no encontrada"),
            ApiResponse(responseCode = "400", description = "Notificación inválida")
        ]
    )
    fun notificationTizada(@RequestBody request: TizadaNotificationRequest): ResponseEntity<TizadaResponse<Any>> {

        tizadaService.saveTizadaFinalizada(request)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            TizadaResponse(
                status = "success",
                data = mapOf(
                    "tizadaUUID" to request.tizadaUUID,
                    "userUUID" to request.userUUID,
                    "url" to request.url
                )
            )
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Crea una nueva tizada",
        description = "Persiste una tizada que luego será consumida por el servicio de tizada"
    )
    @ApiResponse(responseCode = "201", description = "Tizada creada correctamente")
    @ApiResponse(responseCode = "400", description = "Tizada inválida")
    @ApiResponse(responseCode = "500", description = "Ocurrió un error")
    suspend fun createTizada(
        @RequestBody request: CreateTizadaRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<GenericResponse<Any>> {

        val extId = jwt.claims["sub"] as String
        val owner = withContext(Dispatchers.IO) {
            usuarioRepository.findByExternalId(extId) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        val tizadaResponse = tizadaService.createTizada(request, owner.uuid)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            GenericResponse(status = "success", data = tizadaResponse)
        )
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Modificar una tizada",
        description = "Dado un ID, actualiza/modifica la tizada"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tizada actualizada"),
            ApiResponse(responseCode = "401", description = "No autorizado para obtener esta tizada"),
            ApiResponse(responseCode = "404", description = "Tizada no encontrada"),
            ApiResponse(responseCode = "500", description = "Ocurrió un error. Intente nuevamente más tarde.")
        ]
    )
    fun updateTizada(
        @PathVariable id: UUID,
        @RequestBody request: UpdateTizadaRequest
    ): ResponseEntity<GenericResponse<Any>> {
        return try {
            val response = tizadaService.updateTizada(id, request)
            ResponseEntity.status(HttpStatus.OK.value()).body(
                GenericResponse(
                    status = "success",
                    data = response
                )
            )
        } catch (ex: TizadaNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse(
                status = "fail",
                data = mapOf("exception" to ex.javaClass.simpleName, "message" to ex.message)
            ))
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Eliminar una tizada",
        description = "Dado un ID, elimina de forma lógica la tizada"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204"),
            ApiResponse(responseCode = "401", description = "No autorizado para obtener esta tizada"),
            ApiResponse(responseCode = "404", description = "Tizada no encontrada"),
            ApiResponse(responseCode = "500", description = "Ocurrió un error. Intente nuevamente más tarde.")
        ]
    )
    fun deleteTizada(@PathVariable id: UUID): ResponseEntity<Any> {
        return try {
            tizadaService.deleteTizada(id)
            ResponseEntity.noContent().build()
        } catch (ex: TizadaNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse(
                status = "fail",
                data = mapOf("exception" to ex.javaClass.simpleName, "message" to ex.message)
            ))
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener una tizada única",
        description = "Dado un UUID, obtiene la tizada correspondiente a el"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200"),
            ApiResponse(responseCode = "401", description = "No autorizado para obtener esta tizada"),
            ApiResponse(responseCode = "404", description = "Tizada no encontrada", content = []),
            ApiResponse(responseCode = "500", description = "Ocurrió un error. Intente nuevamente más tarde.")
        ]
    )
    fun getTizada(@PathVariable id: UUID): ResponseEntity<GenericResponse<Any>> {
        return try {
            val tizada = tizadaService.getTizadaByUUID(id)
            ResponseEntity.status(HttpStatus.OK.value()).body(
                GenericResponse(status = "success", data = tizada)
            )
        } catch (ex: TizadaNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse(
                status = "fail", data = mapOf("exception" to ex.javaClass.simpleName, "message" to ex.message)
            ))
        }
    }
}
