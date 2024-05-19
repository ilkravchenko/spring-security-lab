package ua.kpi.its.lab.security.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.kpi.its.lab.security.dto.SoftwareProductsResponse
import ua.kpi.its.lab.security.dto.SoftwareProductsRequest
import ua.kpi.its.lab.security.svc.SoftwareProductService
import java.time.Instant

@RestController
@RequestMapping("/products")
class ProductController @Autowired constructor(
    private val SoftwareProductService: SoftwareProductService
) {
    /**
     * Gets the list of all products
     *
     * @return: List of SoftwareProductResponse
     */
    @GetMapping(path = ["", "/"])
    fun products(): List<SoftwareProductsResponse> = SoftwareProductService.read()

    /**
     * Reads the product by its id
     *
     * @param id: id of the product
     * @return: SoftwareProductResponse for the given id
     */
    @GetMapping("{id}")
    fun readProduct(@PathVariable("id") id: Long): ResponseEntity<SoftwareProductsResponse> {
        return wrapNotFound { SoftwareProductService.readById(id) }
    }

    /**
     * Creates a new product instance
     *
     * @param product: SoftwareProductRequest with set properties
     * @return: SoftwareProductResponse for the created product
     */
    @PostMapping(path = ["", "/"])
    fun createProduct(@RequestBody product: SoftwareProductsRequest): SoftwareProductsResponse {
        return SoftwareProductService.create(product)
    }

    /**
     * Updates existing product instance
     *
     * @param product: SoftwareProductRequest with properties set
     * @return: SoftwareProductResponse of the updated product
     */
    @PutMapping("{id}")
    fun updateProduct(
        @PathVariable("id") id: Long,
        @RequestBody product: SoftwareProductsRequest
    ): ResponseEntity<SoftwareProductsResponse> {
        return wrapNotFound { SoftwareProductService.updateById(id, product)}
    }

    /**
     * Deletes existing product instance
     *
     * @param id: id of the product
     * @return: SoftwareProductResponse of the deleted product
     */
    @DeleteMapping("{id}")
    fun deleteProduct(
        @PathVariable("id") id: Long
    ): ResponseEntity<SoftwareProductsResponse> {
        return wrapNotFound { SoftwareProductService.deleteById(id) }
    }

    fun <T>wrapNotFound(call: () -> T): ResponseEntity<T> {
        return try {
            // call function for result
            val result = call()
            // return "ok" response with result body
            ResponseEntity.ok(result)
        }
        catch (e: IllegalArgumentException) {
            // catch not-found exception
            // return "404 not-found" response
            ResponseEntity.notFound().build()
        }
    }
}


@RestController
@RequestMapping("/auth")
class AuthenticationTokenController @Autowired constructor(
    private val encoder: JwtEncoder
) {
    private val authTokenExpiry: Long = 3600L // in seconds

    @PostMapping("token")
    fun token(auth: Authentication): String {
        val now = Instant.now()
        val scope = auth
            .authorities
            .joinToString(" ", transform = GrantedAuthority::getAuthority)
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(authTokenExpiry))
            .subject(auth.name)
            .claim("scope", scope)
            .build()
        return encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}