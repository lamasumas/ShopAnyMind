package io.grpc.shop

import com.google.rpc.Code
import com.google.rpc.Status
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import io.grpc.StatusException
import io.grpc.protobuf.StatusProto

object JsonManager {

    fun getJsonStringData(jsonString: String, jsonParam: String): String {
        val stringData = JsonPath.parse(jsonString)?.read<String>(jsonParam)
        if (stringData != null) {
            return stringData
        } else {
            throw throwStatusException(jsonParam)
        }
    }

    fun getJsonDoubleData(jsonString: String, jsonParam: String): Double {
        val stringData = JsonPath.parse(jsonString)?.read<Double>(jsonParam)
        if (stringData != null) {
            return stringData
        } else {
            throw throwStatusException(jsonParam)
        }
    }

    private fun throwStatusException(jsonParam: String): StatusException {
        val errorStatus = Status.newBuilder()
            .setCode(Code.FAILED_PRECONDITION_VALUE)
            .setMessage("The parameter $jsonParam is not in the request")
            .build()
        return StatusProto.toStatusException(errorStatus)
    }

}