package com.example.mylists.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylists.R;
import com.example.mylists.adapter.FacultetListAdapter;
import com.example.mylists.adapter.SubjectListAdapter;
import com.example.mylists.db.BackgroundTask;
import com.example.mylists.model.Facultet;
import com.example.mylists.model.Student;
import com.example.mylists.model.Subject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class StudentInfoActivity extends AppCompatActivity {

    private SubjectListAdapter mSubjectListAdapter;
    private Student s;
    private ArrayList<Facultet> facultets;
    private int checkedItemPosition;
    private Spinner mySpinner;
    private FacultetListAdapter facultetListAdapter;
    private int currentIdFacultetChose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        checkedItemPosition = -1;
        currentIdFacultetChose = getIntent().getIntExtra("currentFacultet", -1);
        s = getIntent().getParcelableExtra("student");
        facultets = getIntent().getParcelableArrayListExtra("facultets");
        System.out.println("facultets in StudentInfoActivity " + facultets);
        ((EditText) findViewById(R.id.editFIO)).setText(s.getFIO());
        /**
         *  Спиннер для выбора факультета
         */
        mySpinner = (Spinner) findViewById(R.id.editFaculty);
        facultetListAdapter = new FacultetListAdapter(this, android.R.layout.simple_spinner_item, facultets);
        mySpinner.setAdapter(facultetListAdapter);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Facultet currentFacultet = facultetListAdapter.getItem(position);
                s.setIdFaculty(currentFacultet.getId());
                s.setNameFaculty(currentFacultet.getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        ((EditText) findViewById(R.id.editGroup)).setText(s.getGroup());
        mSubjectListAdapter = new SubjectListAdapter(s.getSubjects(),StudentInfoActivity.this);
        ((ListView) findViewById(R.id.lvASI_Subjects)).setAdapter(mSubjectListAdapter);

        ListView listView = findViewById(R.id.lvASI_Subjects);
        AdapterView.OnItemClickListener clSubject = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(checkedItemPosition != position){
                            listView.setItemChecked(position,true);
                            listView.setSelected(true);
                            checkedItemPosition = position;
                        }else {
                            listView.setItemChecked(position,false);
                            listView.setSelected(false);
                            checkedItemPosition = -1;
                        }
                        mSubjectListAdapter.colorChecked(position,parent);
                    }
                };
        listView.setOnItemClickListener(clSubject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ListView listView = findViewById(R.id.lvASI_Subjects);
        int position = listView.getCheckedItemPosition();
        long[] id = listView.getCheckedItemIds();
        View view = listView.getSelectedView();

        switch (item.getItemId()){
            case R.id.miEditSub:{
                if(listView.isSelected()){
                    //editSubject(view.findViewById(R.id.llElementSub));

                    AlertDialog.Builder inputDialog = new AlertDialog.Builder(StudentInfoActivity.this);
                    inputDialog.setTitle("Информация о дисциплине");
                    inputDialog.setCancelable(false);
                    View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.subject_input, null);
                    inputDialog.setView(vv);
                    final EditText mName = vv.findViewById(R.id.editDialog_SubjectName);
                    final Spinner mMark = vv.findViewById(R.id.sDialog_Mark);
                    ((EditText) vv.findViewById(R.id.editDialog_SubjectName)).setText(
                            s.getSubjects().get(position).getName()//subjName
                    );

                    inputDialog.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(TextUtils.isEmpty(((EditText) vv.findViewById(R.id.editDialog_SubjectName)).getText().toString())){
                                Toast.makeText(getApplicationContext(),"Дисциплина не указана", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                s.changeSubject(position,mName.getText().toString(),Integer.parseInt(mMark.getSelectedItem().toString()));
                                mSubjectListAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                            .setNegativeButton("Отмена",null);
                    inputDialog.show();

                }else {
                    Toast.makeText(getApplicationContext(),
                            "Дисциплина не выбрана", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.miAddSub:{
                addSubject(view);
                return true;
            }
            case R.id.miDeleteSub:{
                if(listView.isSelected()) {
                    deleteSubject(position);
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Дисциплина не выбрана", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            default:{}
        }
        return super.onOptionsItemSelected(item);
    }

    public void addSubject(View view) {
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(StudentInfoActivity.this);
        inputDialog.setTitle("Информация о дисциплине");
        inputDialog.setCancelable(false);
        View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.subject_input, null);
        inputDialog.setView(vv);
        final EditText mName = vv.findViewById(R.id.editDialog_SubjectName);
        final Spinner mMark = vv.findViewById(R.id.sDialog_Mark);

        inputDialog.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(((EditText) vv.findViewById(R.id.editDialog_SubjectName)).getText().toString())){
                    Toast.makeText(getApplicationContext(),"Дисциплина не указана", Toast.LENGTH_SHORT).show();
                }else{
                    s.addSubject(new Subject(
                            s.getId(),
                            mName.getText().toString(),
                            Integer.parseInt(mMark.getSelectedItem().toString())
                    ));
                    mSubjectListAdapter.notifyDataSetChanged();
                }

            }
        })
                .setNegativeButton("Отмена",null);
        inputDialog.show(); //NEW1011 end
    }

    public void editSubject(View view) {
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(StudentInfoActivity.this);
        inputDialog.setTitle("Информация о дисциплине");
        inputDialog.setCancelable(false);
        View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.subject_input, null);
        inputDialog.setView(vv);
        final EditText mName = vv.findViewById(R.id.editDialog_SubjectName);
        final Spinner mMark = vv.findViewById(R.id.sDialog_Mark);
        ((EditText) vv.findViewById(R.id.editDialog_SubjectName)).setText(((TextView)view.findViewById(R.id.tvSubjeсtName)).toString());

        inputDialog.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mName.toString().equals("")){
                    ((EditText) findViewById(R.id.editDialog_SubjectName)).setError("Не указана дисциплина");
                }
                s.addSubject(new Subject(
                        s.getId(),
                        mName.getText().toString(),
                        Integer.parseInt(mMark.getSelectedItem().toString())
                ));
                mSubjectListAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton("Отмена",null);
        inputDialog.show(); //NEW1011 end
    }

    public void deleteSubject(int position){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                StudentInfoActivity.this);
        quitDialog.setTitle("Удалить дисциплину \"" + s.getSubjects().get(position).getName() + "\"?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                s.getSubjects().remove(position);
                mSubjectListAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        quitDialog.show();
    }

    public void clSave(View view) {
        s.setFIO(((EditText) findViewById(R.id.editFIO)).getText().toString());
        s.setGroup(((EditText) findViewById(R.id.editGroup)).getText().toString());
        Facultet facultet = facultetListAdapter.getItem(mySpinner.getSelectedItemPosition());
        s.setIdFaculty(facultet.getId());
        s.setNameFaculty(facultet.getName());
        saveData(s);
        Intent intent = new Intent();
        intent.putExtra("student",s);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void clExit(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    public int getIdFaculty(String name) {
        for(Facultet facultet: facultets) {
            if(facultet.getName().equals(name)) {
                return facultet.getId();
            }
        }
        return -1;
    }
    public String getNameFaculty() {
        String nameFaculty = "";
        for(Facultet facultet: facultets) {
            if(facultet.getId() == currentIdFacultetChose) {
                nameFaculty = facultet.getName();
            }
        }
        return nameFaculty;
    }
    @Override
    public void onBackPressed() {
        boolean err = false;
        if(TextUtils.isEmpty(((EditText) findViewById(R.id.editFIO)).getText().toString())){
            ((EditText) findViewById(R.id.editFIO)).setError("Не указано ФИО");
            err = true;
        }
        if(!mySpinner.isSelected()){
            s.setIdFaculty(currentIdFacultetChose);
            s.setNameFaculty(getNameFaculty());
        }
        if(TextUtils.isEmpty(((EditText) findViewById(R.id.editGroup)).getText().toString())){
            ((EditText) findViewById(R.id.editGroup)).setError("Не указана группа");
            err = true;
        }
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        if(!err) {


            quitDialog.setTitle("Сохранить изменения?");
            quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clSave(null);
                }
            })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clExit(null);
                        }
                    })
            ;

        }
        else {
            quitDialog = new AlertDialog.Builder(
                    this);
            quitDialog.setTitle("Закрыть дополнительную информацию о студенте?")
                    .setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clExit(null);
                        }
                    })
                     .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                         }
                     })

            ;

        }
        quitDialog.show();

    }
    public void saveData(Student student) {
        // проверка на существование записи о студенте
        // если есть запись, то изменить студента
        // если нет то этот блок
        System.out.println("student " + student);
        BackgroundTask backgroundTask = new BackgroundTask(this);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        backgroundTask.execute("add_info", gson.toJson(student));

    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}