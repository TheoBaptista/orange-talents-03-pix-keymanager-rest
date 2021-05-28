package br.com.zup.edu.testesEndpoint

import br.com.zup.edu.RegisterKeyGrpcResponse
import br.com.zup.edu.RegisterKeyGrpcServiceGrpc
import br.com.zup.edu.endpoint.dto.RegisterPixKeyRequest
import io.grpc.Status
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistrarChaveTeste {

    @field:Inject lateinit var  clientRegisterGrpc: RegisterKeyGrpcServiceGrpc.RegisterKeyGrpcServiceBlockingStub
    @field:Inject @field:Client("/") lateinit var clientHttp: HttpClient

    @AfterEach
    internal fun setUp() {
        Mockito.reset(clientRegisterGrpc)
    }

    @Test
    internal fun `deve registar uma chave pix no servidor grpc`() {
        //cenario
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        val novaChaveRequest = RegisterPixKeyRequest("04456080090", "CPF", "CONTA_CORRENTE")
        val novaChaveResponse = RegisterKeyGrpcResponse.newBuilder().setPixId(pixId).setClientId(clientId).build()

        Mockito.`when`(clientRegisterGrpc.register(Mockito.any())).thenReturn(novaChaveResponse)
        val request = HttpRequest.POST("/api/v1/clientes/${clientId}/pix", novaChaveRequest)

        //acao
        val response = clientHttp.toBlocking().exchange(request, RegisterPixKeyRequest::class.java)

        //validacao
        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(pixId).and(response.header("Location")!!.contains(clientId)))

    }

    @Test
    internal fun `deve retornar um codigo 422 ao tentar cadastar uma chave ja existente`() {
        //cenario
        val clientId = UUID.randomUUID().toString()
        val novaChaveRequest = RegisterPixKeyRequest("04456080090", "CPF", "CONTA_CORRENTE")
        Mockito.`when`(clientRegisterGrpc.register(Mockito.any())).thenThrow(Status.ALREADY_EXISTS.asRuntimeException())
        val request = HttpRequest.POST("/api/v1/clientes/${clientId}/pix", novaChaveRequest)

        //acao e validacao
        val erro = assertThrows<HttpClientResponseException> { clientHttp.toBlocking().exchange(request, RegisterPixKeyRequest::class.java) }

        //validacao
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.code, erro.status.code)

    }

    @Test
    internal fun `deve retornar um codigo 404 ao tentar cadastar um cliente que nao existe`() {
        //cenario
        val clientId = UUID.randomUUID().toString()
        val novaChaveRequest = RegisterPixKeyRequest("04456080090", "CPF", "CONTA_CORRENTE")
        Mockito.`when`(clientRegisterGrpc.register(Mockito.any())).thenThrow(Status.NOT_FOUND.asRuntimeException())
        val request = HttpRequest.POST("/api/v1/clientes/${clientId}/pix", novaChaveRequest)

        //acao e validacao
        val erro = assertThrows<HttpClientResponseException> { clientHttp.toBlocking().exchange(request, RegisterPixKeyRequest::class.java) }

        //validacao
        assertEquals(HttpStatus.NOT_FOUND.code, erro.status.code)

    }

    @Test
    internal fun `deve retornar um codigo 400 ao tentar cadastar com dados invalidos`() {
        //cenario
        val clientId = UUID.randomUUID().toString()
        val novaChaveRequest = RegisterPixKeyRequest("04456080090", "dsadasdsad", "CONTAAASSS_CORRENTE")
        Mockito.`when`(clientRegisterGrpc.register(Mockito.any())).thenThrow(Status.INVALID_ARGUMENT.asRuntimeException())
        val request = HttpRequest.POST("/api/v1/clientes/${clientId}/pix", novaChaveRequest)

        //acao e validacao
        val erro = assertThrows<HttpClientResponseException> { clientHttp.toBlocking().exchange(request, RegisterPixKeyRequest::class.java) }

        //validacao
        assertEquals(HttpStatus.BAD_REQUEST.code, erro.status.code)

    }

    @Singleton
    @Replaces(bean = RegisterKeyGrpcServiceGrpc.RegisterKeyGrpcServiceBlockingStub::class)
    fun stub() = Mockito.mock(RegisterKeyGrpcServiceGrpc.RegisterKeyGrpcServiceBlockingStub::class.java)

}