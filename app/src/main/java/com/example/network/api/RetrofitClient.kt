package com.example.network.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit network client manager configured with Moshi converters
 * and resilient timeout/logging configurations for OSINT endpoints.
 */
object RetrofitClient {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    private val defaultOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", "Spectre-OSINT-Sentinel/2.0 (Android; Intelligence; Security)")
                    .build()
                chain.proceed(requestWithUserAgent)
            }
            .build()
    }

    // IP-API Retrofit Client
    val ipApiService: IpApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://ip-api.com/")
            .client(defaultOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(IpApiService::class.java)
    }

    // IPWhois Retrofit Client (HTTPS Backup)
    val ipWhoIsService: IpWhoIsService by lazy {
        Retrofit.Builder()
            .baseUrl("https://ipwho.is/")
            .client(defaultOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(IpWhoIsService::class.java)
    }

    // Cloudflare DNS over HTTPS (DoH) Retrofit Client
    val cloudflareDnsService: CloudflareDnsService by lazy {
        Retrofit.Builder()
            .baseUrl("https://cloudflare-dns.com/")
            .client(defaultOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CloudflareDnsService::class.java)
    }

    // crt.sh Certificate Transparency Subdomain & Footprint Service
    val crtShService: CrtShTransparencyService by lazy {
        Retrofit.Builder()
            .baseUrl("https://crt.sh/")
            .client(
                defaultOkHttpClient.newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CrtShTransparencyService::class.java)
    }

    // ICANN RDAP Domain Footprint Service
    val rdapService: RdapDomainService by lazy {
        Retrofit.Builder()
            .baseUrl("https://rdap.org/")
            .client(defaultOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RdapDomainService::class.java)
    }
}
