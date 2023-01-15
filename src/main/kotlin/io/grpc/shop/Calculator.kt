package io.grpc.shop

object Calculator {
    //The example is wrong, as it is using MASTERCARD, it should be 0.03
    fun calculate(pointsRequest: PointsRequest): PointsReply {
        when (pointsRequest.paymentMethod) {
            "CASH" -> return calculate(pointsRequest, 0.9, 1.0, 0.05)
            "CASH_ON_DELIVERY" -> return calculate(pointsRequest, 1.0, 1.02, 0.05)
            "VISA" -> return calculate(pointsRequest, 0.95, 1.0, 0.03)
            "MASTERCARD" -> return calculate(pointsRequest, 0.95, 1.0, 0.03)
            "AMEX" -> return calculate(pointsRequest, 0.98, 1.0, 0.03)
            "JCB" -> return calculate(pointsRequest, 0.95, 1.0, 0.05)
        }
        return pointsReply { } //TODO("Return exception, Invalid payment method")//HandleException
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
            TODO("Return exception, price modifier out of range")
        }
    }
}