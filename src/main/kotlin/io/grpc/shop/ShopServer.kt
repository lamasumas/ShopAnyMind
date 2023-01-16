package io.grpc.shop

import io.grpc.Server
import io.grpc.ServerBuilder

class ShopServer(val port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(GetPointsService())
        .addService(GetSalesService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class GetPointsService : ShopGrpcKt.ShopCoroutineImplBase() {
        override suspend fun getPoints(request: PointsRequest): PointsReply {
            //calculate points and reply
            return Calculator.calculate(request)
        }
    }
    private class GetSalesService : ShopGrpcKt.ShopCoroutineImplBase() {
        override suspend fun getSales(request: SalesRequest): SalesReply {
            DatabaseManager().getData(request)
            return salesReply {  }
        }
    }
}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val server = ShopServer(port)
    server.start()
    server.blockUntilShutdown()
}
