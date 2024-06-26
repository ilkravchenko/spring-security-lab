package ua.kpi.its.lab.security.dto

data class SoftwareProductsRequest(
    var name: String,
    var developer: String,
    var version: String,
    var releaseDate: String,
    var distributionSize: Long,
    var bitness: Int,
    var crossPlatform: Boolean,
    var module: SotwareModuleRequest
)

data class SoftwareProductsResponse(
    var id: Long,
    var name: String,
    var developer: String,
    var version: String,
    var releaseDate: String,
    var distributionSize: Long,
    var bitness: Int,
    var crossPlatform: Boolean,
    var module: SotwareModuleResponse
)

data class SotwareModuleRequest(
    var description: String,
    var author: String,
    var language: String,
    var lastEditDate: String,
    var size: Long,
    var linesOfCode: Int,
    var crossPlatform: Boolean
)

data class SotwareModuleResponse(
    var id: Long,
    var description: String,
    var author: String,
    var language: String,
    var lastEditDate: String,
    var size: Long,
    var linesOfCode: Int,
    var crossPlatform: Boolean
)