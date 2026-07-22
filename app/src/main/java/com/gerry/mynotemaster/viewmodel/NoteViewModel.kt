package com.gerry.mynotemaster.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gerry.mynotemaster.data.NoteStorage
import com.gerry.mynotemaster.model.Note
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * NoteViewModel = "otak" aplikasi yang menyimpan dan mengelola semua catatan menggunakan SharedPreferences.
 */
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = NoteStorage(application)

    // notes bersifat PUBLIC dan READ-ONLY — UI hanya boleh MEMBACA.
    val notes: StateFlow<List<Note>> = storage.notes

    /** Mencari catatan berdasarkan id — dipakai EditorScreen saat mode edit. */
    fun getNoteById(id: Long): Note? {
        return storage.getNoteById(id)
    }

    /**
     * Menyimpan catatan ke SharedPreferences.
     */
    fun saveNote(
        id: Long? = null,
        content: String,
        color: Long = 0xFFFFF9C4,
        isPinned: Boolean = false
    ) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val existingNote = id?.let { storage.getNoteById(it) }
            val note = Note(
                id = id ?: 0,
                content = content.trim(),
                color = color,
                isPinned = isPinned,
                createdAt = existingNote?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            storage.saveNote(note)
        }
    }

    /** Menghapus catatan. */
    fun deleteNote(id: Long) {
        viewModelScope.launch {
            storage.deleteNote(id)
        }
    }
}
