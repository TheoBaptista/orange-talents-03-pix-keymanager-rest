package br.com.zup.edu.testesEndpoint

import br.com.zup.edu.*
import br.com.zup.edu.endpoint.dto.PixDetailResponse
import br.com.zup.edu.endpoint.dto.RegisterPixKeyRequest
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Status
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.client.netty.ConnectTTLHandler
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class CarregaChaveTeste {

    @field:Inject
    lateinit var clientLoadGrpc: LoadKeyGrpcServiceGrpc.LoadKeyGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var clientHttp: HttpClient

    @AfterEach
    internal fun setUp() {
        Mockito.reset(clientLoadGrpc)
    }

    @Test
    internal fun `deve carregar um chave pix`() {
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()


        Mockito.`when`(
            clientLoadGrpc.load(
                LoadKeyRequest.newBuilder()
                    .setPixId(LoadKeyRequest.WhitPixId.newBuilder().setPixId(pixId).setClientId(clientId).build())
                    .build()
            )
        ).thenReturn(
            LoadKeyResponse.newBuilder().setClienteId(clientId).setKey(
                LoadKeyResponse.PixKey.newBuilder().setType(KeyType.CPF).setKey("04405094030")
                    .build()
            ).setPixId(pixId).build()
        )

        val request = HttpRequest.GET<PixDetailResponse>("/api/v1/clientes/$clientId/pix/$pixId")
        //acao
        val response = clientHttp.toBlocking().exchange(request, PixDetailResponse::class.java)

        assertEquals(HttpStatus.OK.code, response.status.code)
        with(response.body()) {
            assertEquals(clientId, this.clientId)
            assertEquals(pixId, this.pixId)
            assertEquals("CPF", this.type)
            assertEquals("04405094030", this.key)
        }
    }

    @Test
    internal fun `nao deve carregar uma chave pix pois o servidor retornara not permited`() {
        //cenario
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()


        Mockito.`when`(
            clientLoadGrpc.load(
                LoadKeyRequest.newBuilder()
                    .setPixId(LoadKeyRequest.WhitPixId.newBuilder().setPixId(pixId).setClientId(clientId).build())
                    .build()
            )
        ).thenThrow(io.grpc.Status.PERMISSION_DENIED.asRuntimeException())

        //acao e validacao
        val request = HttpRequest.GET<PixDetailResponse>("/api/v1/clientes/$clientId/pix/$pixId")
        val erro = org.junit.jupiter.api.assertThrows<HttpClientResponseException> {
            clientHttp.toBlocking().exchange(request, PixDetailResponse::class.java)
        }

        //validacao
        assertEquals(HttpStatus.FORBIDDEN.code, erro.status.code)
    }

    @Singleton
    @Replaces(bean = LoadKeyGrpcServiceGrpc.LoadKeyGrpcServiceBlockingStub::class)
    fun blockingStub() = Mockito.mock(LoadKeyGrpcServiceGrpc.LoadKeyGrpcServiceBlockingStub::class.java)
}