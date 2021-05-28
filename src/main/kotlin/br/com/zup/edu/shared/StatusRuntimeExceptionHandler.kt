package br.com.zup.edu.shared

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus


fun resolver(e: StatusRuntimeException): HttpResponse<Any> {
   return when(e.status.code){
           Status.INVALID_ARGUMENT.code -> HttpResponse.badRequest(e.status)
           Status.ALREADY_EXISTS.code -> HttpResponse.unprocessableEntity<Any>().body(e.status)
           Status.NOT_FOUND.code -> HttpResponse.notFound<Any>().body(e.status)
           Status.PERMISSION_DENIED.code -> HttpResponse.status(HttpStatus.FORBIDDEN)
       else -> HttpResponse.serverError()
   }
}

