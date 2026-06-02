package com.github.livingwithhippos.torbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// todo: this probably has more nullable variables, test it
@Serializable
data class CreatedStreamDto(
    val id: Int,
    @SerialName("file_id") val fileId: Int,
    @SerialName("auth_id") val authId: String,
    val name: String,
    @SerialName("short_name") val shortName: String,
    @SerialName("s3_path") val s3Path: String,
    val size: Long,
    val mimetype: String,
    val hash: String,
    @SerialName("open_subtitles_hash") val openSubtitlesHash: String,
    val type: String,
    val region: String,
    val domain: String,
    @SerialName("user_token") val userToken: String,
    @SerialName("webdav_url") val webDavUrl: String,
    @SerialName("intro_information") val introInformation: IntroInformationDto?,
    @SerialName("scrobbling_enabled") val scrobblingEnabled: Boolean,
    @SerialName("hls_url") val hlsUrl: String,
    @SerialName("presigned_token") val presignedToken: String,
    @SerialName("subtitle_index") val subtitleIndex: Int?,
    @SerialName("audio_index") val audioIndex: Int,
    @SerialName("resolution_index") val resolutionIndex: Int,
    @SerialName("is_transcoding") val isTranscoding: Boolean,
    @SerialName("needs_transcoding") val needsTranscoding: Boolean,
    val metadata: StreamMetadataDto,
    @SerialName("search_metadata") val searchMetadata: SearchMetadataDto,
)

@Serializable
data class StreamDto(
    @SerialName("hls_url") val hlsUrl: String,
    val domain: String,
    @SerialName("presigned_token") val presignedToken: String,
    @SerialName("subtitle_index") val subtitleIndex: Int,
    @SerialName("audio_index") val audioIndex: Int,
    @SerialName("resolution_index") val resolutionIndex: Int,
    @SerialName("file_token") val fileToken: String,
    val token: String,
    @SerialName("is_transcoding") val isTranscoding: Boolean,
    @SerialName("needs_transcoding") val needsTranscoding: Boolean,
    val metadata: StreamMetadataDto,
    @SerialName("search_metadata") val searchMetadata: SearchMetadataDto,
)

@Serializable
data class IntroInformationDto(
    @SerialName("start_time") val startTime: Int,
    @SerialName("end_time") val endTime: Int,
    val title: String,
)

@Serializable
data class StreamMetadataDto(
    val video: StreamVideoDto,
    val audios: List<StreamAudioDto>,
    val subtitles: List<StreamSubtitleDto>,
    val thumbnail: String,
    val chapters: String,
)

@Serializable
data class StreamVideoDto(
    val index: Int,
    @SerialName("codec_type") val codecType: String,
    val codec: String,
    val width: Int,
    val height: Int,
    @SerialName("frame_rate") val frameRate: String,
    @SerialName("pixel_format") val pixelFormat: String,
    val bitrate: String,
    val duration: String,
    @SerialName("total_chunks") val totalChunks: Int,
    @SerialName("file_name") val fileName: String,
    @SerialName("opensubtitles_hash") val opensubtitlesHash: String,
    val size: Long,
    val title: String,
    @SerialName("title_data") val titleData: Map<String, String> = emptyMap(),
)

@Serializable
data class StreamAudioDto(
    val index: Int,
    @SerialName("codec_type") val codecType: String,
    val codec: String,
    val default: Boolean,
    @SerialName("sample_rate") val sampleRate: String,
    val channels: Int,
    @SerialName("channel_layout") val channelLayout: String,
    val language: String,
    @SerialName("language_full") val languageFull: String,
    val title: String,
)

@Serializable
data class StreamSubtitleDto(
    val index: Int = 0,
)

@Serializable
data class SearchMetadataDto(
    val title: String,
    val titles: List<String>,
    val keywords: List<String>,
    val genres: List<String>,
    val actors: List<String>,
    @SerialName("releaseYears") val releaseYears: String,
    @SerialName("mediaType") val mediaType: String,
    val characters: List<String>,
    @SerialName("globalID") val globalID: String,
    val id: String,
    val link: String,
    val description: String,
    val rating: Int,
    val languages: List<String>,
    @SerialName("contentRating") val contentRating: String,
    val trailer: Map<String, String> = emptyMap(),
    val image: String,
    @SerialName("isAdult") val isAdult: Boolean,
    val type: String,
    @SerialName("releasedDate") val releasedDate: String,
    @SerialName("episodesNumber") val episodesNumber: Int,
    val runtime: String,
    @SerialName("titles_full") val titlesFull: List<SearchMetadataTitleDto>,
    val backdrop: String,
)

@Serializable
data class SearchMetadataTitleDto(
    val language: String,
    val title: String,
)
