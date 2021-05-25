package br.com.zup.edu.shared

import br.com.zup.edu.DeleteKeyGrpcServiceGrpc
import br.com.zup.edu.ListAllKeysGrpcServiceGrpc
import br.com.zup.edu.LoadKeyGrpcServiceGrpc
import br.com.zup.edu.RegisterKeyGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class KeyManagerGrpcFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun registerKey() = RegisterKeyGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun deleteKey() = DeleteKeyGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun loadKey() = LoadKeyGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listKeys() = ListAllKeysGrpcServiceGrpc.newBlockingStub(channel)

}