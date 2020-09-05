package com.bnorm.react

import kotlinx.datetime.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class ItemStatus {
  Pending,
  Paused,
  Completed,
}

@Serializable
data class TodoItem(
  val text: String,
  val key: String = Clock.System.now().toString(),
  val status: ItemStatus = ItemStatus.Pending,
)

@Serializable
data class AppState(
  val items: List<TodoItem> = emptyList(),
  @Serializable(LocalDateAsStringSerializer::class) val date: LocalDate = Clock.System.todayAt(TimeZone.currentSystemDefault()),
)

private object LocalDateAsStringSerializer : KSerializer<LocalDate> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.toString())
  override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}
