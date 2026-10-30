# torbox-kmp-client

This is a Kotlin multi platform library, created to easily interact with [Torbox](https://torbox.app). It's still in beta; until `1.0.0` breaking changes and/or bugs may occur.

!! The library is currently working only for Kotlin/JVM targets, other targets are either broken or untested !!

This is my first library, please provide feedback if possible. AI has been used in small amounts to quickly refactor code after changes.

## Importing the library

[https://central.sonatype.com/artifact/io.github.livingwithhippos/torbox-kmp-client](https://central.sonatype.com/artifact/io.github.livingwithhippos/torbox-kmp-client)

**build.gradle.kts**

```kts
dependencies {
    implementation("io.github.livingwithhippos:torbox-kmp-client:0.3.0")
}
```

## Usage

```kt
suspend fun main() {
    val client = TorboxClient.create(TorboxClientConfig(), TorboxAuthState(bearerToken = "YOUR-API-TOKEN"))

    // you can also create one without token
    // val client = TorboxClient.create(TorboxClientConfig())
    // and update it when you retrieve it
    // client.updateBearerToken("YOUR-API-TOKEN")

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

The syntax is similar to [the official APIs](https://api-docs.torbox.app/), the categories are the same, so `General -> Get Up Status` becomes `client.general.upStatus()`, usually if no verb is specified it's a request to `get` the resource.

The response objects are mostly modeled like this:

```kt
// class modeling the http call result

sealed interface ApiResult<out T> {
    data class Success<T>(
        val value: T,
    ) : ApiResult<T>

    sealed interface Failure : ApiResult<Nothing> {
        ...
        // failure classes
    }
}

interface ApiSuccessResponse {
    val success: Boolean
}

// class modeling the basic response, practically always returned by the service
@Serializable
data class ApiResponseDto<T>(
    override val success: Boolean,
    val error: String?,
    val detail: String,
    val data: T? = null,
) : ApiSuccessResponse
```

and `val data: T` has the requested data, depending on the requested API

A successful response to the `torrent.get()` function returns `ApiResult<ApiResponseDto<TorrentDto>>`, and the `magnet` field inside `TorrentDto` can be accessed like this:

```kt
if (response is ApiResult.Success) {
    println(response.value.data.magnet)
}
```

## Notes

You can generate/retrieve [the API token here](https://torbox.app/settings?section=account)

A test file, without the `@Test` keyword since it's using the real API, is available under `jvmTest`

## Donate

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/E1E412NFX7)

Subscribe to Torbox [with my referral link](https://torbox.app/subscription?referral=dba208d7-f53e-4711-96db-97c7051799bc)

... more will follow
