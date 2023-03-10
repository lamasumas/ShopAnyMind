package io.grpc.shop.server

import com.google.rpc.Code
import com.google.rpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.shop.PointsReply
import io.grpc.shop.PointsRequest
import io.grpc.shop.pointsReply

object Calculator {
    //The example is wrong, as it is using MASTERCARD, it should be 0.03
    /**
     * Funciton that return the points and the final price depending on the payment type and two values.
     */
    fun calculate(pointsRequest: PointsRequest): PointsReply {
        when (pointsRequest.paymentMethod) {
            "CASH" -> return calculate(pointsRequest, 0.9, 1.0, 0.05)
            "CASH_ON_DELIVERY" -> return calculate(pointsRequest, 1.0, 1.02, 0.05)
            "VISA" -> return calculate(pointsRequest, 0.95, 1.0, 0.03)
            "MASTERCARD" -> return calculate(pointsRequest, 0.95, 1.0, 0.03)
            "AMEX" -> return calculate(pointsRequest, 0.98, 1.0, 0.03)
            "JCB" -> return calculate(pointsRequest, 0.95, 1.0, 0.05)
        }
        val errorStatus = Status.newBuilder()
            .setCode(Code.OUT_OF_RANGE_VALUE)
            .setMessage("The payment method is not a valid one")
            .build()
        throw StatusProto.toStatusException(errorStatus)
    }

    private fun calculate(
        pointsRequest: PointsRequest,
        lowerModifierBound: Double,
        higherModifierBound: Double,
        pointModifier: Double
    ): PointsReply {
        if (pointsRequest.priceModifier in lowerModifierBound..higherModifierBound) {
            return pointsReply {
                finalPrice = (pointsRequest.price * pointsRequest.priceModifier).toString()
                points = (pointsRequest.price * pointModifier).toInt()
            }
        } else {
            val errorStatus = Status.newBuilder()
                .setCode(Code.OUT_OF_RANGE_VALUE)
                .setMessage("The price modifier is not in the correct range. Check the documentation")
                .build()
            throw StatusProto.toStatusException(errorStatus)
        }
    }
}