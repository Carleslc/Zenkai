package ai.zenkai.zenkai.common

class HttpError(
        val code: Int,
        override val message: String
) : Throwable()