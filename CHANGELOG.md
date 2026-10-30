# CHANGELOG

**Version 0.3.0**

- fixed `noAuth()` being ignored: public endpoints (stats, changelogs, speedtest, device auth...) no longer send the bearer token
- malformed response bodies are now reported as `ApiResult.Failure.Serialization` instead of `ApiResult.Failure.Network`
- breaking: internal helpers are no longer public (`BASE_URL`, `NoAuth`, `noAuth()`, `logLevelToEnum`, `createHttpClient`, `platformEngineFactory` and the `*ApiImpl` classes). Use `TorboxClient.create(...)` and the API interfaces instead
- `ktor-client-mock` is no longer a runtime dependency

**Version 0.2.0**

- API calls are now main-safe: requests and response decoding run on `TorboxClientConfig.dispatcher` (default `Dispatchers.Default`)

**Version 0.1.5**

- fixed nullable server key
- updated other models default values
- all lists are not nullable but empty by default
- updated libraries

**Version 0.1.3**

- breaking: changed configuration structure
- fixed bearer token change
- fixed wrong serialization of some fields
- updated dependencies

**Version 0.1.2**

- bearer token is now a var so it can be changed without recreating the client
- fixed nullable field marked as not nullable
- updated libraries