/*
 * Copyright 2020 gRPC authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.shop

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import io.grpc.shop.ShopGrpcKt.ShopCoroutineStub
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ShopClient(val channel: ManagedChannel) : Closeable {
    private val stub: ShopCoroutineStub = ShopCoroutineStub(channel)

    fun getPoints(jsonString: String) = runBlocking {

        val request = pointsRequest {
            datetime = JsonPath.parse(jsonString)?.read<String>("$.datetime").toString()
            priceModifier= JsonPath.parse(jsonString)?.read<Double>("$.priceModifier")!!
            price= JsonPath.parse(jsonString)?.read<String>("$.price").toString()
            paymentMethod = JsonPath.parse(jsonString)?.read<String>("$.paymentMethod").toString()  }
        try {
            val response = stub.getPoints(request)
            println("Greeter client received: ${response.points}")
        } catch (e: StatusException) {
            println("RPC failed: ${e.status}")
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

/**
 * Greeter, uses first argument as name to greet if present;
 * greets "world" otherwise.
 */
fun main(args: Array<String>) {
    val isRemote = args.size == 1

    Executors.newFixedThreadPool(10).asCoroutineDispatcher().use { dispatcher ->
        val builder = if (isRemote)
            ManagedChannelBuilder.forTarget(args[0].removePrefix("https://") + ":443").useTransportSecurity()
        else
            ManagedChannelBuilder.forTarget("localhost:50051").usePlaintext()

        ShopClient(
            builder.executor(dispatcher.asExecutor()).build()
        ).use {
            val testClientRequest = args.singleOrNull() ?: ("{\n" +
                    "\"price\": \"100.00\",\n" +
                    "\"priceModifier\": 0.95,\n" +
                    "\"paymentMethod\": \"MASTERCARD\",\n" +
                    "\"datetime\": \"2022-09-01T00:00:00Z\"}")
            it.getPoints(testClientRequest)
        }
    }
}
