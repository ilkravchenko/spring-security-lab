package ua.kpi.its.lab.security.svc.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.kpi.its.lab.security.dto.SoftwareProductsRequest
import ua.kpi.its.lab.security.dto.SoftwareProductsResponse
import ua.kpi.its.lab.security.dto.SotwareModuleResponse
import ua.kpi.its.lab.security.entity.Module
import ua.kpi.its.lab.security.entity.Product
import ua.kpi.its.lab.security.repo.SoftwareProductRepository
import ua.kpi.its.lab.security.svc.SoftwareProductService
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class SoftwareProductServiceImpl @Autowired constructor(
    private val repository: SoftwareProductRepository
): SoftwareProductService {
    override fun create(product: SoftwareProductsRequest): SoftwareProductsResponse {
        val module = product.module
        val newModule = Module(
            description = module.description,
            author = module.author,
            language = module.language,
            size = module.size,
            lastEditDate = this.stringToDate(module.lastEditDate),
            linesOfCode = module.linesOfCode,
            crossPlatform = module.crossPlatform
        )
        var newProduct = Product(
            name = product.name,
            developer = product.developer,
            version = product.version,
            releaseDate = this.stringToDate(product.releaseDate),
            distributionSize = product.distributionSize,
            bitness = product.bitness,
            crossPlatform = product.crossPlatform,
            module = newModule
        )
        newModule.product = newProduct
        newProduct = this.repository.save(newProduct)
        val productResponse = this.productEntityToDto(newProduct)
        return productResponse
    }

    override fun read(): List<SoftwareProductsResponse> {
        return this.repository.findAll().map(this::productEntityToDto)
    }

    override fun readById(id: Long): SoftwareProductsResponse {
        val product = this.getProductById(id)
        val productResponse = this.productEntityToDto(product)
        return productResponse
    }

    override fun updateById(id: Long, product: SoftwareProductsRequest): SoftwareProductsResponse {
        val oldProduct = this.getProductById(id)
        val module = product.module

        oldProduct.apply {
            name = product.name
            developer = product.developer
            version = product.version
            releaseDate = this@SoftwareProductServiceImpl.stringToDate(product.releaseDate)
            distributionSize = product.distributionSize
            bitness = product.bitness
            crossPlatform = product.crossPlatform
        }
        oldProduct.module.apply {
            description = module.description
            author = module.author
            language = module.language
            size = module.size
            lastEditDate = this@SoftwareProductServiceImpl.stringToDate(module.lastEditDate)
            linesOfCode = module.linesOfCode
            crossPlatform = module.crossPlatform
        }
        val newProduct = this.repository.save(oldProduct)
        val productResponse = this.productEntityToDto(newProduct)
        return productResponse
    }

    override fun deleteById(id: Long): SoftwareProductsResponse {
        val product = this.getProductById(id)
        this.repository.delete(product)
        val SoftwareProductsResponse = productEntityToDto(product)
        return SoftwareProductsResponse
    }

    private fun getProductById(id: Long): Product {
        return this.repository.findById(id).getOrElse {
            throw IllegalArgumentException("Product not found by id = $id")
        }
    }

    private fun productEntityToDto(product: Product): SoftwareProductsResponse {
        return SoftwareProductsResponse(
            id = product.id,
            name = product.name,
            developer = product.developer,
            version = product.version,
            releaseDate = this.dateToString(product.releaseDate),
            distributionSize = product.distributionSize,
            bitness = product.bitness,
            crossPlatform = product.crossPlatform,
            module = this.moduleEntityToDto(product.module)
        )
    }

    private fun moduleEntityToDto(module: Module): SotwareModuleResponse {
        return SotwareModuleResponse(
            id = module.id,
            description = module.description,
            author = module.author,
            language = module.language,
            size = module.size,
            lastEditDate = this.dateToString(module.lastEditDate),
            linesOfCode = module.linesOfCode,
            crossPlatform = module.crossPlatform
        )
    }

    private fun dateToString(date: Date): String {
        val instant = date.toInstant()
        val dateTime = instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    }

    private fun stringToDate(date: String): Date {
        val dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val instant = dateTime.toInstant(ZoneOffset.UTC)
        return Date.from(instant)
    }
}