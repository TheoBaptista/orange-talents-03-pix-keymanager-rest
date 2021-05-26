package br.com.zup.edu.endpoint

import br.com.zup.edu.*
import br.com.zup.edu.endpoint.dto.ListAllResponse
import br.com.zup.edu.endpoint.dto.PixDetailResponse
import br.com.zup.edu.endpoint.dto.PixResponse
import br.com.zup.edu.endpoint.dto.RegisterPixKeyRequest
import br.com.zup.edu.shared.resolver
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller("/api/v1/clientes/{clientId}/pix")
class KeyPixManagerController(
    private val registerKeyStub: RegisterKeyGrpcServiceGrpc.RegisterKeyGrpcServiceBlockingStub,
    private val deleteKeyStub: DeleteKeyGrpcServiceGrpc.DeleteKeyGrpcServiceBlockingStub,
    private val loadKeyStub: LoadKeyGrpcServiceGrpc.LoadKeyGrpcServiceBlockingStub,
    private val listAllKeysStub: ListAllKeysGrpcServiceGrpc.ListAllKeysGrpcServiceBlockingStub,
) {

    @Post
    fun registerKey(
        @Valid @Body registerPixKeyRequest: RegisterPixKeyRequest,
        @PathVariable clientId: String
    ): HttpResponse<Any> {
        return try {
            val response = registerKeyStub.register(registerPixKeyRequest.convert(clientId))
            val location = HttpResponse.uri("/api/v1/clientes/${response.clientId}/pix/${response.pixId}")
            HttpResponse.created(location)
        } catch (e: StatusRuntimeException) {
            resolver(e)
        }
    }

    @Delete("/{pixId}")
    fun deleteKey(@PathVariable clientId: String, @PathVariable pixId: String): HttpResponse<Any> {
        return try {
            deleteKeyStub.delete(DeleteKeyGrpcRequest.newBuilder().setPixId(pixId).setClientId(clientId).build())
            HttpResponse.ok()
        } catch (e: StatusRuntimeException) {
            resolver(e)
        }
    }

    @Get("/{pixId}")
    fun load(@PathVariable pixId: String, @PathVariable clientId: String): HttpResponse<Any> {
        return try {
            HttpResponse.ok(
                PixDetailResponse.getResponse(
                    loadKeyStub.load(
                        LoadKeyRequest.newBuilder()
                            .setPixId(
                                LoadKeyRequest.WhitPixId.newBuilder().setClientId(clientId).setPixId(pixId).build()
                            ).build()
                    )
                )
            )
        } catch (e: StatusRuntimeException) {
            resolver(e)
        }
    }

    @Get
    fun listAll(@PathVariable clientId: String): HttpResponse<Any>  {
        return try {
        val response = listAllKeysStub.listAll(ListAllRequest.newBuilder().setClientId(clientId).build())
        val chaves = response.keysList.map { PixResponse.getResponse(it) }
        HttpResponse.ok(ListAllResponse(clientId = response.clientId, chaves = chaves))
        }catch (e: StatusRuntimeException){
            resolver(e)
        }
    }
}


