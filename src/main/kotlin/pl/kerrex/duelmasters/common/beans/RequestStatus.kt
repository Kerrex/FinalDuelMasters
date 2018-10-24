package pl.kerrex.duelmasters.common.beans

enum class RequestStatus(val status: Boolean) {
    SUCCESS(true),
    FAILURE(false);


    companion object {
        fun ofBoolean(status: Boolean?): RequestStatus {
            return if (status == true) SUCCESS else FAILURE
        }
    }
}