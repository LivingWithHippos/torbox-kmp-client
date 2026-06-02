# torbox-kmp-client

This is a kotlin multi platform library, created to easily interact with [Torbox](https://torbox.app). It's still in beta, until 1.0.0 breaking changes and/or bugs may occur.

This is my first library so I cannot talk about the quality of the work, but it's small enough that with some testing. AI has been used in small amounts to quickly refactor code after changes.

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

The syntax is similar to [the official APIs](https://api-docs.torbox.app/), the categories are the same, so `General -> Get Up Status` becomes `client.general.upStatus()`

## Notes

You can generate/retrieve [the API token here](https://torbox.app/settings?section=account)

## Donate

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/E1E412NFX7)

Subscribe to Torbox [with my referral link](https://torbox.app/subscription?referral=dba208d7-f53e-4711-96db-97c7051799bc)

... more will follow