package xyz.gonzapico.codemotion2017.data

/**
 * Created by gonzapico on 30/10/2017.
 */
class CodemotionAPIResponse(
    val id: Long,
    val published: Boolean,
    val feedBackEnabled: Boolean,
    val days: List<AgendaDay>
)

class AgendaDay(
    val id: Long,
    val name: String,
    val tracks: List<Track>
)

class Track(
    val id: Long,
    val name: String,
    val slots: List<Slot>
)

class Slot(
    val id: Long,
    val name: String,
    val start: String,
    val end: String,
    val userId: Long,
    val trackId: Long,
    val contents: Contents)

class Contents(
    val type : String,
    val id : Long,
    val title : String,
    val description : String,
    val creationDate : Long,
    val authors : List<Author>,
    val state : String,
    val totalVote : Int,
    val totalLikes : Int,
    val tags: List<Tag>,
    val feedback: Feedback
)

class Tag(
    val format : List<String>
)

class Author(
    val id : Long,
    val uuid : String,
    val name : String,
    val avatar : String,
    val twitterAccount : String,
    val feedback : Feedback
)

class Feedback(
    val ratingAverage : Int,
    val entriesCount : Int
)