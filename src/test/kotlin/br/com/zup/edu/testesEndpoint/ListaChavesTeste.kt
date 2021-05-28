package br.com.zup.edu.testesEndpoint

import br.com.zup.edu.*
import br.com.zup.edu.endpoint.dto.PixDetailResponse
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.management.ListenerNotFoundException

@MicronautTest
class ListaChavesTeste {

    @field:Inject
    lateinit var clientListAllGrpc: ListAllKeysGrpcServiceGrpc.ListAllKeysGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var clientHttp: HttpClient

    private val clientId = UUID.randomUUID().toString()
    private val pixId = UUID.randomUUID().toString()

    @AfterEach
    internal fun setUp() {
        Mockito.reset(clientListAllGrpc)
    }

    @Test
    internal fun `deve carregar uma lista de chaves pix`() {


        Mockito.`when`(clientListAllGrpc.listAll(ListAllRequest.newBuilder().setClientId(clientId).build())).thenReturn(
            ListAllResponse.newBuilder().setClientId(clientId).addAllKeys(getPixKey()).build()
        )

        val request = HttpRequest.GET<PixDetailResponse>("/api/v1/clientes/$clientId/pix")

        val response = clientHttp.toBlocking().exchange(request, br.com.zup.edu.endpoint.dto.ListAllResponse::class.java)

        assertEquals(HttpStatus.OK.code, response.status.code)
        with(response.body()){
            assertEquals(2,this.chaves.size)
            assertEquals(clientId,this.clientId)
            assertEquals(pixId,this.chaves[1].id)
            assertEquals("CPF",this.chaves[1].type)
            assertEquals("04455060090",this.chaves[1].key)
            assertEquals("CONTA_CORRENTE",this.chaves[1].accountType)
        }

    }


    private fun getPixKey(): List<ListAllResponse.PixKey> {
       return listOf(ListAllResponse.PixKey.newBuilder().setKey("joao@joao.com.br").setPixId(pixId)
            .setAccountType(AccountType.CONTA_POUPANCA).setType(KeyType.EMAIL).build(),ListAllResponse.PixKey.newBuilder().setKey("04455060090").setPixId(pixId)
            .setAccountType(AccountType.CONTA_CORRENTE).setType(KeyType.CPF).build())
    }

    @Singleton
    @Replaces(bean = ListAllKeysGrpcServiceGrpc.ListAllKeysGrpcServiceBlockingStub::class)
    fun blockingStub() = Mockito.mock(ListAllKeysGrpcServiceGrpc.ListAllKeysGrpcServiceBlockingStub::class.java)
}