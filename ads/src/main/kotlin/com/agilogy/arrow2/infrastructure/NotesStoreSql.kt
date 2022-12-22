package com.agilogy.arrow2.infrastructure

import arrow.core.continuations.Raise
import arrow.core.continuations.effect
import com.agilogy.arrow2.domain.NoteAlreadyExists
import com.agilogy.arrow2.domain.NotesStore
import com.agilogy.arrow2.domain.CategoryId
import com.agilogy.arrow2.domain.Note
import com.agilogy.arrow2.sql.SqlPrimitive.Companion.sql
import com.agilogy.arrow2.sql.executeUpdate
import com.agilogy.arrow2.sql.executeUpdateCatchDuplicateKey
import com.agilogy.arrow2.sql.withConnection
import java.sql.Connection
import javax.sql.DataSource

class NotesStoreSql(private val dataSource: DataSource) : NotesStore {

    context(Raise<NoteAlreadyExists>)
    override suspend fun insert(note: Note): Unit = dataSource.withConnection {
        insertIntoAds(note)
        increasePublisherAdCount(note.category)
    }

    context(Connection, Raise<Nothing>)
    private suspend fun increasePublisherAdCount(categoryId: CategoryId) {
        // WARNING: For the sake of having a simple example only. Don't actually use upserts like this:
        val updated = executeUpdate(
            "update publisher_ads set ads_count = ads_count + 1 where publisher_id = ?",
            categoryId.value.sql
        )
        if (updated == 0) {
            executeUpdate(
                "insert into publisher_ads(publisher_id, ads_count) values (?, ?)",
                categoryId.value.sql, 1.sql
            )
        }
    }

    context(Connection, Raise<NoteAlreadyExists>)
    private suspend fun insertIntoAds(note: Note) {
        effect {
            executeUpdateCatchDuplicateKey(
                "insert into ads(id, title, description) values (?, ?, ?)",
                note.id.value.sql, note.title.value.sql, note.category.value.sql
            )
        } recover { raise(NoteAlreadyExists(note.id)) }
    }
}