package br.com.zup.edu.endpoint.dto

import br.com.zup.edu.ListAllResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class PixResponse(
    val id: String,
    val key: String,
    val type: String,
    val accountType: String,
    val createdAt: LocalDateTime,
){
    companion object{
        fun getResponse(keyPix: ListAllResponse.PixKey): PixResponse {
            return PixResponse(
                id = keyPix.pixId,
                key = keyPix.key,
                type = keyPix.type.name,
                accountType = keyPix.accountType.name,
                createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(keyPix.createdAt.seconds,keyPix.createdAt.nanos.toLong()),
                    ZoneOffset.UTC)
            )
        }
    }
}

data class ListAllResponse(
    val clientId: String,
    val chaves: List<PixResponse>
){}

