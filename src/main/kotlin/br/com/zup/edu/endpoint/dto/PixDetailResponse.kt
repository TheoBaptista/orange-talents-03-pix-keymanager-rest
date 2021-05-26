package br.com.zup.edu.endpoint.dto

import br.com.zup.edu.LoadKeyResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


data class PixDetailResponse(
    val clientId: String,
    val pixId: String,
    val type: String,
    val key: String,
    val createdAt: LocalDateTime,
    val account: Map<String,String>,
) {
    companion object {
        fun getResponse(loadKeyResponse: LoadKeyResponse): PixDetailResponse {
            return PixDetailResponse(
                clientId = loadKeyResponse.clienteId,
                pixId = loadKeyResponse.pixId,
                key = loadKeyResponse.key.key,
                createdAt = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(
                        loadKeyResponse.key.criadaEm.seconds,
                        loadKeyResponse.key.criadaEm.nanos.toLong()
                    ),ZoneOffset.UTC
                ),
                type = loadKeyResponse.key.type.name,
                account = mapOf(
                    Pair("tipo de conta",loadKeyResponse.key.account.type.name),
                    Pair("instituição",loadKeyResponse.key.account.institution),
                    Pair("nome do titular",loadKeyResponse.key.account.ownerName),
                    Pair("cpf do titular",loadKeyResponse.key.account.cpf),
                    Pair("agencia",loadKeyResponse.key.account.agency),
                    Pair("numero",loadKeyResponse.key.account.accountNumber)
                )
            )
        }

    }
}
