package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.byted.camp.todolist.db.TodoDbHelper.DATABASE_NAME;
import static com.byted.camp.todolist.db.TodoDbHelper.DATABASE_VERSION;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper todoDbHelper;
    private SQLiteDatabase db;

    public MainActivity() {
        todoDbHelper = new TodoDbHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        db = todoDbHelper.getReadableDatabase();
        List<Note> noteList = new ArrayList<Note>();
        String[] projection = {
                BaseColumns._ID,
                TodoContract.Todo.COLUMN_NAME_TIME,
                TodoContract.Todo.COLUMN_NAME_PLACE,
                TodoContract.Todo.COLUMN_NAME_ISSUE,
                TodoContract.Todo.COLUMN_NAME_RANK
        };
        Cursor cursor = db.query(
                TodoContract.Todo.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                TodoContract.Todo.COLUMN_NAME_RANK, TodoContract.Todo.COLUMN_NAME_TIME
        );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        while (cursor.moveToNext()) {
            Note note = new Note(cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.Todo._ID)));
            note.setContent(cursor.getString(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_ISSUE)));
            try {
                note.setDate(simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_TIME))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            State state = State.from(Integer.parseInt(cursor.getString(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_PLACE))));
            note.setState(state);
            note.setRank(Integer.parseInt(cursor.getString(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_RANK))));
            noteList.add(note);
        }
        cursor.close();
        return noteList;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        db = todoDbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TodoContract.Todo.TABLE_NAME + " WHERE " + TodoContract.Todo.COLUMN_NAME_ISSUE + " = '" + note.getContent() + "';");
        try {
            notesAdapter.refresh(loadNotesFromDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNode(Note note) {
        // 更新数据
        db = todoDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoContract.Todo.COLUMN_NAME_PLACE, Integer.toString(note.getState().intValue));
        db.update(TodoContract.Todo.TABLE_NAME, contentValues, TodoContract.Todo._ID+"="+note.id, null);
        try {
            notesAdapter.refresh(loadNotesFromDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
