package br.com.zup.edu.endpoint

import br.com.zup.edu.DeleteKeyGrpcRequest
import br.com.zup.edu.DeleteKeyGrpcServiceGrpc
import br.com.zup.edu.LoadKeyGrpcServiceGrpc
import br.com.zup.edu.RegisterKeyGrpcServiceGrpc
import br.com.zup.edu.endpoint.dto.RegisterPixKeyRequest
import br.com.zup.edu.shared.resolver
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller("/api/v1/clientes/{id}/pix")
class KeyPixManagerController(
    private val registerKeyStub: RegisterKeyGrpcServiceGrpc.RegisterKeyGrpcServiceBlockingStub,
    private val deleteKeyStub: DeleteKeyGrpcServiceGrpc.DeleteKeyGrpcServiceBlockingStub,
    private val loadKeyStub: LoadKeyGrpcServiceGrpc.LoadKeyGrpcServiceBlockingStub,
) {

    @Post
    fun registerKey(
        @Valid @Body registerPixKeyRequest: RegisterPixKeyRequest,
        @PathVariable id: String
    ): HttpResponse<Any> {
        return try {
            val response = registerKeyStub.register(registerPixKeyRequest.convert(id))
            val location = HttpResponse.uri("/api/v1/clientes/${response.clientId}/pix/${response.pixId}")
            HttpResponse.created(location)
        } catch (e: StatusRuntimeException) {
            resolver(e)
        }
    }

    @Delete("/{pixId}")
    fun deleteKey(@PathVariable id: String, @PathVariable pixId: String): HttpResponse<Any> {
        return try {
            deleteKeyStub.delete(DeleteKeyGrpcRequest.newBuilder().setPixId(pixId).setClientId(id).build())
            HttpResponse.accepted<Any>()
        }catch (e: StatusRuntimeException){
            resolver(e)
        }
    }
}


