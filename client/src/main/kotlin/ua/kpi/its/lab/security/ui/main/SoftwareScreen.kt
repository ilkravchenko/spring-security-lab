package ua.kpi.its.lab.security.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import ua.kpi.its.lab.security.dto.SoftwareModuleRequest
import ua.kpi.its.lab.security.dto.SoftwareProductsRequest
import ua.kpi.its.lab.security.dto.SoftwareProductsResponse

@Composable
fun SoftwareScreen(
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    snackbarHostState: SnackbarHostState
) {
    var products by remember { mutableStateOf<List<SoftwareProductsResponse>>(listOf()) }
    var loading by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<SoftwareProductsResponse?>(null) }

    LaunchedEffect(token) {
        loading = true
        delay(1000)
        products = withContext(Dispatchers.IO) {
            try {
                val response = client.get("http://localhost:8080/products") {
                    bearerAuth(token)
                }
                loading = false
                response.body()
            } catch (e: Exception) {
                val msg = e.toString()
                snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                products
            }
        }
    }

    if (loading) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedProduct = null
                    openDialog = true
                },
                content = {
                    Icon(Icons.Filled.Add, "add product")
                }
            )
        }
    ) {
        if (products.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text("No products to show", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onEdit = {
                            selectedProduct = product
                            openDialog = true
                        },
                        onRemove = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    try {
                                        val response = client.delete("http://localhost:8080/products/${product.id}") {
                                            bearerAuth(token)
                                        }
                                        require(response.status.isSuccess())
                                    } catch (e: Exception) {
                                        val msg = e.toString()
                                        snackbarHostState.showSnackbar(
                                            msg,
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                }

                                loading = true

                                products = withContext(Dispatchers.IO) {
                                    try {
                                        val response = client.get("http://localhost:8080/products") {
                                            bearerAuth(token)
                                        }
                                        loading = false
                                        response.body()
                                    } catch (e: Exception) {
                                        val msg = e.toString()
                                        snackbarHostState.showSnackbar(
                                            msg,
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Indefinite
                                        )
                                        products
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        if (openDialog) {
            ProductDialog(
                product = selectedProduct,
                token = token,
                scope = scope,
                client = client,
                onDismiss = {
                    openDialog = false
                },
                onError = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            it,
                            withDismissAction = true,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                },
                onConfirm = {
                    openDialog = false
                    loading = true
                    scope.launch {
                        products = withContext(Dispatchers.IO) {
                            try {
                                val response = client.get("http://localhost:8080/products") {
                                    bearerAuth(token)
                                }
                                loading = false
                                response.body()
                            } catch (e: Exception) {
                                loading = false
                                products
                            }
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun ProductDialog(
    product: SoftwareProductsResponse?,
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    onDismiss: () -> Unit,
    onError: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    val module = product?.module

    var name by remember { mutableStateOf(product?.name ?: "") }
    var developer by remember { mutableStateOf(product?.developer ?: "") }
    var version by remember { mutableStateOf(product?.version ?: "") }
    var releaseDate by remember { mutableStateOf(product?.releaseDate ?: "") }
    var distributionSize by remember { mutableStateOf(product?.distributionSize?.toString() ?: "") }
    var bitness by remember { mutableStateOf(product?.bitness?.toString() ?: "") }
    var crossPlatform by remember { mutableStateOf(product?.crossPlatform ?: false) }
    var moduleDescription by remember { mutableStateOf(module?.description ?: "") }
    var moduleAuthor by remember { mutableStateOf(module?.author ?: "") }
    var moduleLanguage by remember { mutableStateOf(module?.language ?: "") }
    var moduleLastEditDate by remember { mutableStateOf(module?.lastEditDate ?: "") }
    var moduleSize by remember { mutableStateOf(module?.size?.toString() ?: "") }
    var moduleLinesOfCode by remember { mutableStateOf(module?.linesOfCode?.toString() ?: "") }
    var moduleIsCrossPlatform by remember { mutableStateOf(module?.crossPlatform ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp).wrapContentSize()) {
            Column(
                modifier = Modifier.padding(16.dp, 8.dp).width(IntrinsicSize.Max).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (product == null) {
                    Text("Create product")
                } else {
                    Text("Update product")
                }

                HorizontalDivider()
                Text("Product info")
                TextField(name, { name = it }, label = { Text("Name") })
                TextField(developer, { developer = it }, label = { Text("Developer") })
                TextField(version, { version = it }, label = { Text("Version") })
                TextField(releaseDate, { releaseDate = it }, label = { Text("Release date") })
                TextField(distributionSize, { distributionSize = it }, label = { Text("Distribution Size") })
                TextField(bitness, { bitness = it }, label = { Text("Bitness") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(crossPlatform, { crossPlatform = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Cross Platform")
                }

                HorizontalDivider()
                Text("Module info")
                TextField(moduleDescription, { moduleDescription = it }, label = { Text("Description") })
                TextField(moduleAuthor, { moduleAuthor = it }, label = { Text("Author") })
                TextField(moduleLanguage, { moduleLanguage = it }, label = { Text("Language") })
                TextField(moduleLastEditDate, { moduleLastEditDate = it }, label = { Text("Last Edit Date") })
                TextField(moduleSize, { moduleSize = it }, label = { Text("Size") })
                TextField(moduleLinesOfCode, { moduleLinesOfCode = it }, label = { Text("Lines Of Code") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(moduleIsCrossPlatform, { moduleIsCrossPlatform = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Cross Platform")
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.fillMaxWidth(0.1f))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                try {
                                    val request = SoftwareProductsRequest(
                                        name, developer, version, releaseDate, distributionSize.toLong(), bitness.toInt(), crossPlatform,
                                        SoftwareModuleRequest(
                                            moduleDescription, moduleAuthor, moduleLanguage, moduleLastEditDate,
                                            moduleSize.toLong(), moduleLinesOfCode.toInt(), moduleIsCrossPlatform
                                        )
                                    )
                                    val response = if (product == null) {
                                        client.post("http://localhost:8080/products") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    } else {
                                        client.put("http://localhost:8080/products/${product.id}") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    }
                                    require(response.status.isSuccess())
                                    onConfirm()
                                } catch (e: Exception) {
                                    val msg = e.toString()
                                    onError(msg)
                                }
                            }
                        }
                    ) {
                        if (product == null) {
                            Text("Create")
                        } else {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: SoftwareProductsResponse, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(shape = CardDefaults.elevatedShape, elevation = CardDefaults.elevatedCardElevation()) {
        ListItem(
            overlineContent = {
                Text(product.name)
            },
            headlineContent = {
                Text(product.developer)
            },
            supportingContent = {
                Text("bitness - ${product.bitness}")
            },
            trailingContent = {
                Row(modifier = Modifier.padding(0.dp, 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(CircleShape).clickable(onClick = onEdit)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(CircleShape).clickable(onClick = onRemove)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        )
    }
}