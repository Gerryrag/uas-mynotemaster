package com.gerry.mynotemaster.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gerry.mynotemaster.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteStorage(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_notes_pref", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        val json = sharedPreferences.getString("notes_key", null)
        val type = object : TypeToken<List<Note>>() {}.type
        val noteList: List<Note> = if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
        _notes.value = noteList.sortedWith(compareByDescending<Note> { it.isPinned }.thenByDescending { it.updatedAt })
    }

    fun saveNote(note: Note) {
        val currentList = _notes.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == note.id }
        
        val noteToSave = if (note.id == 0L) {
            // New note, generate ID
            val newId = (currentList.maxOfOrNull { it.id } ?: 0L) + 1
            note.copy(id = newId)
        } else {
            note
        }

        if (index != -1) {
            currentList[index] = noteToSave
        } else {
            currentList.add(noteToSave)
        }
        
        persist(currentList)
    }

    fun deleteNote(id: Long) {
        val currentList = _notes.value.filter { it.id != id }
        persist(currentList)
    }

    fun getNoteById(id: Long): Note? {
        return _notes.value.find { it.id == id }
    }

    private fun persist(list: List<Note>) {
        val sortedList = list.sortedWith(compareByDescending<Note> { it.isPinned }.thenByDescending { it.updatedAt })
        val json = gson.toJson(sortedList)
        sharedPreferences.edit().putString("notes_key", json).apply()
        _notes.value = sortedList
    }
}
