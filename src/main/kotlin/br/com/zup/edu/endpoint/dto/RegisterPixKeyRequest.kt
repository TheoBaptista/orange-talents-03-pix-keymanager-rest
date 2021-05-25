package br.com.zup.edu.endpoint.dto

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.RegisterKeyGrpcRequest
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class RegisterPixKeyRequest(
    @field:Size(max = 77) val key: String?,
    @field:NotBlank val keyType: String?,
    @field:NotBlank val accountType: String?,
){

    fun convert(id: String): RegisterKeyGrpcRequest? {
        return RegisterKeyGrpcRequest.newBuilder().setClientId(id).setKeyValue(this.key).setClientAccountType(toAccountType())
            .setKeyType(toKeyType()).build()
    }

    private fun toAccountType(): AccountType? {
        return try {
            this.accountType?.let { AccountType.valueOf(it) }
        }catch (e: Exception){
            AccountType.UNKNOWN_ACCOUNT_TYPE
        }
    }

    private fun toKeyType(): KeyType? {
        return try {
            this.keyType?.let { KeyType.valueOf(it) }
        }catch (e: Exception){
            KeyType.UNKNOWN_KEY_TYPE
        }
    }

}