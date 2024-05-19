package ua.kpi.its.lab.security.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "products")
class Product(
    @Column
    var name: String,

    @Column
    var developer: String,

    @Column
    var version: String,

    @Column
    var releaseDate: Date,

    @Column
    var distributionSize: Long,

    @Column
    var bitness: Int,

    @Column
    var crossPlatform: Boolean,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    var module: Module
) : Comparable<Product> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: Product): Int {
        val equal = this.name == other.name && this.releaseDate.time == other.releaseDate.time
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "Vehicle(model=$name, manufactureDate=$releaseDate, battery=$module)"
    }
}

@Entity
@Table(name = "modules")
class Module(
    @Column
    var description: String,

    @Column
    var author: String,

    @Column
    var language: String,

    @Column
    var size: Long,

    @Column
    var lastEditDate: Date,

    @Column
    var linesOfCode: Int,

    @Column
    var crossPlatform: Boolean,

    @OneToOne(mappedBy = "module")
    var product: Product? = null,
): Comparable<Module> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: Module): Int {
        val equal = this.description == other.description && this.language == other.language
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "Battery(model=$description, capacity=$language)"
    }
}