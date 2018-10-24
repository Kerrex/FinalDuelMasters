package pl.riscosoftware.cache

import com.nimbusds.jose.util.Base64URL
import java.io.Serializable

data class CertificateInfo(val companyName: String, val sha256Thumbprint: Base64URL) : Serializable