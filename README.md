# torbox-kmp-client

This is a kotlin multi platform library, created to easily interact with [Torbox](https://torbox.app)

This library is still in beta, until 1.0.0 breaking changes and/or bugs may occur.

## Importing the library

**build.gradle.kts**

```kts
dependencies {
    ...
    implementation("io.github.livingwithhippos:torbox-kmp-client:0.1.0")
}
```

## Usage

```kt
suspend fun main() {
    val client = TorboxClient.create(RestClientConfig(bearerToken = "YOUR-API-TOKEN"))

    // retrieve the torrent list
    val response = client.torrent.list()
    if (response is ApiResult.Success) {
        println(response.value)
    } else {
        println("Error: $response")
    }
}
```

## Structure

The syntax is similar to [the official APIs](https://api-docs.torbox.app/), the categories are the same and they contain the relative functions, so `General -> Get Up Status` becomes `client.general.upStatus()`

## Notes

You can generate/retrieve [the API token here](https://torbox.app/settings?section=account)