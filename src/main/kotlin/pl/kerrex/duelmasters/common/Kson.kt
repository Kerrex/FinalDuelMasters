package pl.kerrex.duelmasters.common

import com.google.gson.Gson

object Kson {
    inline fun<reified T : Any> fromJson(str: String): T {
        return Gson().fromJson(str, T::class.java)
    }

    fun toJson(obj: Any): String {
        return Gson().toJson(obj)
    }
}