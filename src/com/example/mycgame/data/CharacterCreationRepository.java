package com.example.mycgame.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CharacterCreationRepository extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "olredine.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_DRAFTS = "creation_drafts";
    private static final String TABLE_SAVES = "character_saves";

    public CharacterCreationRepository(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_DRAFTS + " (" +
            "id INTEGER PRIMARY KEY CHECK(id = 1), " +
            "data_json TEXT NOT NULL, " +
            "updated_at INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_SAVES + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "data_json TEXT NOT NULL, " +
            "created_at INTEGER NOT NULL, " +
            "updated_at INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Keep existing player data. Future schema changes must use explicit migrations.
    }

    public synchronized void saveDraft(CharacterCreationData data) {
        if (data == null) return;
        try {
            long now = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("data_json", data.toJson().toString());
            values.put("updated_at", now);
            getWritableDatabase().insertWithOnConflict(
                TABLE_DRAFTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to encode character creation draft", e);
        }
    }

    public synchronized CharacterCreationData loadDraft() {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_DRAFTS, new String[]{"data_json"}, "id = 1",
                null, null, null, null)) {
            if (!cursor.moveToFirst()) return null;
            return CharacterCreationData.fromJson(cursor.getString(0));
        } catch (JSONException e) {
            return null;
        }
    }

    public synchronized void deleteDraft() {
        getWritableDatabase().delete(TABLE_DRAFTS, "id = 1", null);
    }

    public synchronized long saveCharacter(CharacterCreationData data) {
        if (data == null) throw new IllegalArgumentException("Character data is required");
        try {
            long now = System.currentTimeMillis();
            String name = data.playerName == null ? "" : data.playerName.trim();
            if (name.isEmpty()) name = "شخصیت بدون نام";
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("data_json", data.toJson().toString());
            values.put("created_at", now);
            values.put("updated_at", now);
            return getWritableDatabase().insertOrThrow(TABLE_SAVES, null, values);
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to encode saved character", e);
        }
    }

    public synchronized CharacterCreationData loadCharacter(long id) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_SAVES, new String[]{"data_json"}, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (!cursor.moveToFirst()) return null;
            return CharacterCreationData.fromJson(cursor.getString(0));
        } catch (JSONException e) {
            return null;
        }
    }

    public synchronized List<SaveSlot> listSaves() {
        ArrayList<SaveSlot> result = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_SAVES, new String[]{"id", "name", "created_at", "updated_at"},
                null, null, null, null, "updated_at DESC")) {
            while (cursor.moveToNext()) {
                result.add(new SaveSlot(
                    cursor.getLong(0), cursor.getString(1),
                    cursor.getLong(2), cursor.getLong(3)));
            }
        }
        return result;
    }

    public synchronized boolean deleteCharacter(long id) {
        return getWritableDatabase().delete(
            TABLE_SAVES, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public static class SaveSlot {
        public final long id;
        public final String name;
        public final long createdAt;
        public final long updatedAt;

        public SaveSlot(long id, String name, long createdAt, long updatedAt) {
            this.id = id;
            this.name = name;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }
}
