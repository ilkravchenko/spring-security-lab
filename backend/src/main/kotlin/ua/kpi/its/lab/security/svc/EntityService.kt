package ua.kpi.its.lab.security.svc

import ua.kpi.its.lab.security.dto.SoftwareProductsRequest
import ua.kpi.its.lab.security.dto.SoftwareProductsResponse

interface SoftwareProductService {
    /**
     * Creates a new SoftwareProduct record.
     *
     * @param product: The SoftwareProductsRequest instance to be inserted
     * @return: The recently created SoftwareProductsResponse instance
     */
    fun create(product: SoftwareProductsRequest): SoftwareProductsResponse

    /**
     * Reads all created Products records.
     *
     * @return: List of created SoftwareProductsResponse records
     */
    fun read(): List<SoftwareProductsResponse>

    /**
     * Reads a Product record by its id.
     * The order is determined by the order of creation.
     *
     * @param id: The id of SoftwareProductsRequest record
     * @return: The SoftwareProductsResponse instance at index
     */
    fun readById(id: Long): SoftwareProductsResponse

    /**
     * Updates a SoftwareProductsRequest record data.
     *
     * @param id: The id of the Product instance to be updated
     * @param product: The SoftwareProductsRequest with new Product values
     * @return: The updated SoftwareProductsResponse record
     */
    fun updateById(id: Long, vehicle: SoftwareProductsRequest): SoftwareProductsResponse

    /**
     * Deletes a SoftwareProductsRequest record by its index.
     * The order is determined by the order of creation.
     *
     * @param id: The id of Product record to delete
     * @return: The deleted SoftwareProductsResponse instance at index
     */
    fun deleteById(id: Long): SoftwareProductsResponse
}