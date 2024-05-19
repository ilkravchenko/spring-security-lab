package ua.kpi.its.lab.security.dto

import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class SoftwareProductsRequest(
    var name: String,
    var developer: String,
    var version: String,
    var releaseDate: String,
    var distributionSize: Long,
    var bitness: Int,
    var crossPlatform: Boolean,
    var module: SoftwareModuleRequest
)

@Serializable
data class SoftwareProductsResponse(
    var id: Long,
    var name: String,
    var developer: String,
    var version: String,
    var releaseDate: String,
    var distributionSize: Long,
    var bitness: Int,
    var crossPlatform: Boolean,
    var module: SoftwareModuleResponse
)

@Serializable
data class SoftwareModuleRequest(
    var description: String,
    var author: String,
    var language: String,
    var lastEditDate: String,
    var size: Long,
    var linesOfCode: Int,
    var crossPlatform: Boolean
)

@Serializable
data class SoftwareModuleResponse(
    var id: Long,
    var description: String,
    var author: String,
    var language: String,
    var lastEditDate: String,
    var size: Long,
    var linesOfCode: Int,
    var crossPlatform: Boolean
)