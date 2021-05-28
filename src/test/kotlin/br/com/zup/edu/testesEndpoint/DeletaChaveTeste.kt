package br.com.zup.edu.testesEndpoint

import br.com.zup.edu.DeleteKeyGrpcRequest
import br.com.zup.edu.DeleteKeyGrpcResponse
import br.com.zup.edu.DeleteKeyGrpcServiceGrpc
import br.com.zup.edu.LoadKeyGrpcServiceGrpc
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class DeletaChaveTeste {

    @field:Inject
    lateinit var clientDeleteGrpc: DeleteKeyGrpcServiceGrpc.DeleteKeyGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var clientHttp: HttpClient

    @AfterEach
    internal fun setUp() {
        Mockito.reset(clientDeleteGrpc)
    }


    @Test
    internal fun `deve deletar uma chave pix criada no servidor grpc`() {
        //cenario
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()


        Mockito.`when`(
            clientDeleteGrpc.delete(
                DeleteKeyGrpcRequest.newBuilder().setPixId(pixId).setClientId(clientId).build()
            )
        ).thenReturn(DeleteKeyGrpcResponse.newBuilder().setPixId(pixId).setClientId(clientId).build())

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clientId/pix/$pixId")
        //acao
        val response = clientHttp.toBlocking().exchange(request, DeleteKeyGrpcResponse::class.java)

        assertEquals(HttpStatus.OK.code, response.status().code)

    }

    @Test
    internal fun `nao deve deletar uma chave pix pois o retorno do servidor sera not found`() {
        //cenario
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()


        Mockito.`when`(
            clientDeleteGrpc.delete(
                DeleteKeyGrpcRequest.newBuilder().setPixId(pixId).setClientId(clientId).build()
            )
        ).thenThrow(Status.NOT_FOUND.asRuntimeException())

        //acao e validacao
        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clientId/pix/$pixId")
        val erro = assertThrows<HttpClientResponseException> { clientHttp.toBlocking().exchange(request, RegisterPixKeyRequest::class.java) }

        //validacao
        assertEquals(HttpStatus.NOT_FOUND.code, erro.status.code)
    }

    @Test
    internal fun `nao deve deletar uma chave pix pois o retorno do servidor será 500`() {
        //cenario
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()


        Mockito.`when`(
            clientDeleteGrpc.delete(
                DeleteKeyGrpcRequest.newBuilder().setPixId(pixId).setClientId(clientId).build()
            )
        ).thenThrow(Status.RESOURCE_EXHAUSTED.asRuntimeException())

        //acao e validacao
        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clientId/pix/$pixId")
        val erro = assertThrows<HttpClientResponseException> { clientHttp.toBlocking().exchange(request, RegisterPixKeyRequest::class.java) }

        //validacao
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, erro.status.code)
    }

    @Singleton
    @Replaces(bean = DeleteKeyGrpcServiceGrpc.DeleteKeyGrpcServiceBlockingStub::class)
    fun blockingStub() = Mockito.mock(DeleteKeyGrpcServiceGrpc.DeleteKeyGrpcServiceBlockingStub::class.java)
}