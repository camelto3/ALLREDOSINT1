package com.example.network.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

// =========================================================================
// 1. IP INTELLIGENCE DATA MODELS (Moshi Models)
// =========================================================================

@JsonClass(generateAdapter = true)
data class IpApiRetrofitResponse(
    @Json(name = "status") val status: String?,
    @Json(name = "message") val message: String?,
    @Json(name = "query") val query: String?,
    @Json(name = "country") val country: String?,
    @Json(name = "countryCode") val countryCode: String?,
    @Json(name = "regionName") val regionName: String?,
    @Json(name = "region") val region: String?,
    @Json(name = "city") val city: String?,
    @Json(name = "zip") val zip: String?,
    @Json(name = "lat") val lat: Double?,
    @Json(name = "lon") val lon: Double?,
    @Json(name = "timezone") val timezone: String?,
    @Json(name = "isp") val isp: String?,
    @Json(name = "org") val org: String?,
    @Json(name = "as") val `as`: String?,
    @Json(name = "reverse") val reverse: String?,
    @Json(name = "proxy") val proxy: Boolean?,
    @Json(name = "hosting") val hosting: Boolean?
)

@JsonClass(generateAdapter = true)
data class IpWhoIsRetrofitResponse(
    @Json(name = "ip") val ip: String?,
    @Json(name = "success") val success: Boolean?,
    @Json(name = "type") val type: String?,
    @Json(name = "continent") val continent: String?,
    @Json(name = "country") val country: String?,
    @Json(name = "country_code") val countryCode: String?,
    @Json(name = "region") val region: String?,
    @Json(name = "city") val city: String?,
    @Json(name = "latitude") val latitude: Double?,
    @Json(name = "longitude") val longitude: Double?,
    @Json(name = "asn") val asn: String?,
    @Json(name = "org") val org: String?,
    @Json(name = "isp") val isp: String?,
    @Json(name = "timezone") val timezone: Map<String, Any>?
)

// =========================================================================
// 2. DOMAIN FOOTPRINT & DNS DATA MODELS (Moshi Models)
// =========================================================================

@JsonClass(generateAdapter = true)
data class CloudflareDohQuestion(
    @Json(name = "name") val name: String,
    @Json(name = "type") val type: Int
)

@JsonClass(generateAdapter = true)
data class CloudflareDohAnswer(
    @Json(name = "name") val name: String,
    @Json(name = "type") val type: Int,
    @Json(name = "TTL") val TTL: Int?,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class CloudflareDohResponse(
    @Json(name = "Status") val Status: Int,
    @Json(name = "TC") val TC: Boolean?,
    @Json(name = "RD") val RD: Boolean?,
    @Json(name = "RA") val RA: Boolean?,
    @Json(name = "AD") val AD: Boolean?,
    @Json(name = "CD") val CD: Boolean?,
    @Json(name = "Question") val Question: List<CloudflareDohQuestion>?,
    @Json(name = "Answer") val Answer: List<CloudflareDohAnswer>?,
    @Json(name = "Authority") val Authority: List<CloudflareDohAnswer>?
)

@JsonClass(generateAdapter = true)
data class CrtShCertificateEntry(
    @Json(name = "issuer_ca_id") val issuerCaId: Long?,
    @Json(name = "issuer_name") val issuerName: String?,
    @Json(name = "common_name") val commonName: String?,
    @Json(name = "name_value") val nameValue: String?,
    @Json(name = "id") val id: Long?,
    @Json(name = "entry_timestamp") val entryTimestamp: String?,
    @Json(name = "not_before") val notBefore: String?,
    @Json(name = "not_after") val notAfter: String?,
    @Json(name = "serial_number") val serialNumber: String?
)

@JsonClass(generateAdapter = true)
data class RdapDomainResponse(
    @Json(name = "handle") val handle: String?,
    @Json(name = "ldhName") val ldhName: String?,
    @Json(name = "status") val status: List<String>?,
    @Json(name = "events") val events: List<RdapEvent>?,
    @Json(name = "entities") val entities: List<RdapEntity>?
)

@JsonClass(generateAdapter = true)
data class RdapEvent(
    @Json(name = "eventAction") val eventAction: String?,
    @Json(name = "eventDate") val eventDate: String?
)

@JsonClass(generateAdapter = true)
data class RdapEntity(
    @Json(name = "handle") val handle: String?,
    @Json(name = "roles") val roles: List<String>?
)

// =========================================================================
// 3. RETROFIT API INTERFACES
// =========================================================================

interface IpApiService {
    @GET("json/{query}")
    suspend fun lookupIp(
        @Path("query") ipOrHost: String,
        @Query("fields") fields: String = "status,message,country,countryCode,regionName,city,zip,lat,lon,timezone,isp,org,as,query,reverse,proxy,hosting"
    ): Response<IpApiRetrofitResponse>

    @GET("json/")
    suspend fun lookupSelfIp(
        @Query("fields") fields: String = "status,message,country,countryCode,regionName,city,zip,lat,lon,timezone,isp,org,as,query,reverse,proxy,hosting"
    ): Response<IpApiRetrofitResponse>
}

interface IpWhoIsService {
    @GET("json/{ip}")
    suspend fun lookupIp(
        @Path("ip") ip: String
    ): Response<IpWhoIsRetrofitResponse>
}

interface CloudflareDnsService {
    @Headers("Accept: application/dns-json")
    @GET("dns-query")
    suspend fun queryDns(
        @Query("name") domain: String,
        @Query("type") type: Int
    ): Response<CloudflareDohResponse>
}

interface CrtShTransparencyService {
    @Headers("Accept: application/json")
    @GET("/")
    suspend fun searchCertificates(
        @Query("q") query: String,
        @Query("output") output: String = "json"
    ): Response<List<CrtShCertificateEntry>>
}

interface RdapDomainService {
    @Headers("Accept: application/rdap+json, application/json")
    @GET("domain/{domain}")
    suspend fun lookupDomain(
        @Path("domain") domain: String
    ): Response<RdapDomainResponse>
}
