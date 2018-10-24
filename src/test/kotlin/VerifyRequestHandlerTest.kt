import com.google.gson.Gson
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.Kodein
import org.kodein.di.conf.KodeinGlobalAware
import org.kodein.di.conf.global
import org.kodein.di.generic.instance
import org.mockito.junit.MockitoJUnitRunner
import pl.riscosoftware.bean.Status
import pl.riscosoftware.cache.CertificateInfo
import pl.kerrex.duelmasters.initContext
import pl.riscosoftware.service.AllowedCertificatesService
import pl.riscosoftware.service.KnownCertificatesService
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class VerifyRequestHandlerTest: KodeinGlobalAware {

    @Before
    fun setUp() {
        if (Kodein.global.canConfigure) {
            initContext()
        }
    }

    private val certificateService: KnownCertificatesService by instance()
    private val allowedCertificatesService: AllowedCertificatesService by instance()

    @Test
    fun should_beUnauthorized_when_missingXJwtToken() = withTestApplication(::app) {
        //when
        with(handleRequest(HttpMethod.Post, "/verify")) {

            //then
            assertEquals(this.response.status(), HttpStatusCode.Unauthorized)
            assertEquals("Missing X-JWS-SIGNATURE", this.response.content)
        }
    }

    @Test
    fun should_beStatusFalse_when_certificateHasNoSha256Thumbprint() = withTestApplication(::app) {
        //given
        val call = handleRequest(HttpMethod.Post, "/verify") {
            addHeader("X-JWS-SIGNATURE", JWT_WITHOUT_SHA256)
        }

        //when
        with(call) {

            //then
            val response = parseResponseStatus(this.response.content)
            assertEquals(Status(false), response)
        }

    }

    @Test
    fun should_beStatusFalse_when_certificateNotWhitelisted() = withTestApplication(::app) {
        //given
        val jws = JWT_WITH_SHA256_NOT_WHITELISTED
        allowedCertificatesService.getAllowedCertificates().clear()

        //when
        with(handleRequest(HttpMethod.Post, "/verify") {
            addHeader("X-JWS-SIGNATURE", jws)
        }) {

            //then
            val response = parseResponseStatus(this.response.content)
            assertEquals(Status(false), response)
        }

    }

    @Test
    fun should_beStatusFalse_when_certVerificationFailed() = withTestApplication(::app) {
        //given
        val jws = JWT_WITH_WRONG_SHA256
        val certSha256 =  Base64URL("jKlWJ-YjVT-HgfID-5s6L2LbDlpzI2O-PxwuOmzD2LY")

        allowedCertificatesService.getAllowedCertificates().add(CertificateInfo("Tomek Co.", certSha256))
        certificateService.getKnownCertificates()[certSha256] = RSAKey.parse(CORRECT_JWK)

        //when
        with(handleRequest(HttpMethod.Post, "/verify") {
            addHeader("X-JWS-SIGNATURE", jws)
        }) {

            //then
            val response = parseResponseStatus(this.response.content)
            assertEquals(Status(false), response)
        }
    }

    @Test
    fun should_beStatusTrue_when_verificationSuccessful() = withTestApplication(::app) {
        //given
        val jws = CORRECT_JWS
        val certSha256 =  Base64URL("jKlWJ-YjVT-HgfID-5s6L2LbDlpzI2O-PxwuOmzD2LY")

        allowedCertificatesService.getAllowedCertificates().add(CertificateInfo("Tomek Co.", certSha256))
        certificateService.getKnownCertificates()[certSha256] = RSAKey.parse(CORRECT_JWK)

        //when
        with(handleRequest(HttpMethod.Post, "/verify") {
            addHeader("X-JWS-SIGNATURE", jws)
        }) {

            //then
            val response = parseResponseStatus(this.response.content)
            assertEquals(Status(true), response)
        }
    }

    private fun parseResponseStatus(response: String?): Status = Gson().fromJson(response, Status::class.java)

    companion object {
        private const val CORRECT_JWK = "{\"kty\":\"RSA\",\"n\":\"3haRwXISP_-po2-Xxn7QdplI0cbtp6j_WmBgPtMbiEB7L7O_MMB3z4wu5U4NaJa-DQ7DBRz-WxR0jkw6JXXEj10kRHqpDbVXE991yJ4hQj1J9Y4c82mV3qiccz8h5MVPL_CXXk4llwKdCSADFzceNp1lLsBU8Jd6eaaqQnwlVu2wpv1mk6FerVGw3mKApFy-k4MhwakdUF_E-XUFcDSzeIjh_pwthXBkyy2SX0N8JDozYw61xmUNliHU8C6_NTKzBBLDGsMn8Rhgrwi8HLymMysDfUWSOXu_oCBaXuKnLIUDU_EtSLI8gNaeNEt5kTS0iGjTBZN3E6-MXgqzOqS87AepgpcswTw3W21DJClnZPSYdqyljdapkp8421f1nK24b7-8M8nHSxtFpdDj99bSQDlXdMLkN6iY8WKD8_X-UMLulkT0i0zgOjY5mtVMMnKYSzOM2i6TLReLc3NKwe9gJ2S6I5vSe49il0PuW7dymuANqArqX-HI79PcR5lQAcP9Vt6gMIYePKmF9F5PeGfCUGQXGevjzEjKrNnzZp49K_bHGYfap07fbgZvKSTo8-l7d7TJyxEBkchpz8ma_Tcmy4Y-2cl-44Sr3snABzrKfHQd7TALt9odm0FuF19pfO9kv1bspXo0VxQ2PoeNQXPxzr9la0s2OdiaxNYPuX-ZZg0\",\"e\":\"AQAB\", \"x5t#S256\": \"jKlWJ-YjVT-HgfID-5s6L2LbDlpzI2O-PxwuOmzD2LY\"}"
        private const val CORRECT_SHA256 = "jKlWJ-YjVT-HgfID-5s6L2LbDlpzI2O-PxwuOmzD2LY"
        private const val CORRECT_JWS = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCNTMjU2IjoiaktsV0otWWpWVC1IZ2ZJRC01czZMMkxiRGxwekkyTy1QeHd1T216RDJMWSIsIng1dSI6Imh0dHA6Ly9nb29nbGUucGwifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.mVnlKeENpSJIoCXx_fClIcoC5TaRe8eWOg29G_tI1hFwC95YQDpQ3iziVdshmCna4cRiUcqyqR5Q2Gi9BwOwBTzABOMIvj9VtoVi7F-4EPYVTlG91hJ5_DAv6c7p5giQuQx1LDecn08xHwrXO1yjDVvUYVqIYej1R9p2Bw-ylZUu2BbSUF7UWQ5ov52PKb8SRWAXIIS06HSJNsDTQ6IUPYF7WDLUN8ipXAbQt_56OBk6OFQLKqt6pU8jW0uFs8z6dX4xj1uYjdAMtGXeJLB5p2Al1mU2gq3Gysw2AAo7SUBAtZj9qx83Xi87_rO2OBnCT-aWF9dE9X8_IWaNZX2HIUx8KhXR4EZo-1y5FbLkIySIJ2AZ_7X2iSvRBnEfYAM3cQPhgI1sMlC6_5Fns9aFBY0kGPe5GHhZ_ASchpGFgv1hyTbPg08ZQubPgo4qlaBk89BeVCpaENgpoa0IYuhPMEqlh2PaNrkaYyaBreIaM3vYxV7vq0bwsqCKxHDFc_MEvGDnz1t-38lmSaZ6t5G5_0p9HZS7rQAwr8znyhvPLdMQC16IZenpUAf7eAPiPCHNrX53QhsgFRV3NIBe9hnmYaCse3pA1zNCQt3kkZrYPoex3YicfdlMFSw8cBv59jIAv_naQxZQ3sJWDx_DevDPFfPQ5V8qllQRZMmiWIgegr8"

        private const val JWT_WITHOUT_SHA256 = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.TCYt5XsITJX1CxPCT8yAV-TVkIEq_PbChOMqsLfRoPsnsgw5WEuts01mq-pQy7UJiN5mgRxD-WUcX16dUEMGlv50aqzpqh4Qktb3rk-BuQy72IFLOqV0G_zS245-kronKb78cPN25DGlcTwLtjPAYuNzVBAh4vGHSrQyHUdBBPM"
        private const val JWT_WITH_SHA256_NOT_WHITELISTED = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCNTMjU2IjoiMTExMTExMTExIn0.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.2AraWSY_fFODqZjERLmUHhOudcnjpxU-o98ujXiUTBWH2fhOvBSPdrhmx17PkvJNKHUvpYlzyVhmqcR4MpO8ARxzwoE1wCg2VUjzisjOFm1MOiM_suXUcDTH1zdDK6N7LjpQqoVz5jXwY5OR-KKznvdIjoavGGZGUs2LH4-YzI8"
        private const val JWT_WITH_WRONG_SHA256 = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCNTMjU2IjoiaktsV0otWWpWVC1IZ2ZJRC01czZMMkxiRGxwekkyTy1QeHd1T216RDJMWSIsIng1dSI6Imh0dHA6Ly9nb29nbGUucGwifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.xL_i5PHpb9YtdQJMGuln3LAyQsu4P3BDrHU7M8MIe4YVUGVIrvxzu8fmQI8VDThjHxiMs7Jq0TLHX95zxfz5gdN3HH9jNfeEBzk8G6ZfPnYfLB9yroqdNEQjYm9xR33JZNKAwZ-jsrsfdnJqqBiPfTaEpm_KEegM1XL7OcNrIfk"
    }

}