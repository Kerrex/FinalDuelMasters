package pl.kerrex.duelmasters.common.handler

import io.ktor.application.ApplicationCall

interface RequestHandler {
    suspend fun handle(call: ApplicationCall)
}