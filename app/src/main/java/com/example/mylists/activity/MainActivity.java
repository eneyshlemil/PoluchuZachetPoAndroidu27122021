package com.example.mylists.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylists.R;
import com.example.mylists.adapter.FacultetListAdapter;
import com.example.mylists.adapter.StudentListAdapter;
import com.example.mylists.db.BackgroundTask;
import com.example.mylists.model.Facultet;
import com.example.mylists.model.Student;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private int mPosition;
    private ActivityResultLauncher<Intent> mIntentActivityResultLauncher;
    private int checkedItemPosition;
    public static ArrayList<Student> mStudents;
    @SuppressLint("StaticFieldLeak")
    public static StudentListAdapter mStudentListAdapter;
    FacultetListAdapter facultetListAdapter;
    SharedPreferences mPreferences;
    public static ArrayList<Facultet> mFacultets;
    Menu menu;
    public static int currentId = -1;
    public static Map<Integer, ArrayList<Student>> mapIdFacultyToStudents;
    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private static boolean was_updated = false;
    public void phone_calling(View view,int position) {
        String phone_number = mStudents.get(position).getTelephone();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone_number));
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CALL_PHONE
            }, 1);
        } else {
            try {
                startActivity(callIntent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

    }
    public static void loadFacultyIntoNavigationView() {
        Menu drawerMenu = navigationView.getMenu();
        if(mFacultets != null && !mFacultets.isEmpty()) {
            if(was_updated) return;
            for (Facultet facultet : mFacultets) {

                drawerMenu.add(facultet.getName());
            }
            was_updated = true;
        }
    }
    // TODO
    public void loadStudents() {
        if(mFacultets == null) return;
        Facultet currentFacultet = getById(currentId);
        if(currentFacultet != null) {
            loadStudentsFromDB();
            System.out.println("currentFacultet");
        }
        mStudentListAdapter.notifyDataSetChanged();
    }

    @SuppressLint("ResourceType")
    public void hideMenuForStudents(Menu menu, boolean hidden) {
        menu.findItem(R.id.miAdd).setVisible(!hidden);
        menu.findItem(R.id.miDelete).setVisible(!hidden);
        menu.findItem(R.id.miEdit).setVisible(!hidden);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(currentId != -1) {
            hideMenuForStudents(menu, false);
        } else {
            hideMenuForStudents(menu, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ll);
        checkedItemPosition = -1;
        if(mapIdFacultyToStudents == null) {
            mapIdFacultyToStudents = new HashMap<>();
        }
        was_updated = false;
        //если программа открывется в первый раз, создаём список студентов
        //если список пустой, проверяем, нет ли сохранённых данных о студентах. Если есть - добавляем в список.
        createFacultetList();
        if(mStudents == null) createStudentList();
        loadStudents();

        ActionBar toolbar = getSupportActionBar();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.app_name, R.string.app_name);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        loadFacultyIntoNavigationView();
        navigationView.setNavigationItemSelectedListener( this);
        // disply home button for actionbar
        toolbar.setDisplayHomeAsUpEnabled(true);


        mIntentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            Student s = intent.getParcelableExtra("student");
                            if(mPosition  == mStudents.size() + 1) {
                                if(s.getIdFaculty() != currentId) {
                                    ArrayList<Student> oldStudent = mapIdFacultyToStudents.get(s.getIdFaculty());
                                    if(oldStudent == null) {
                                        oldStudent = new ArrayList<Student>();
                                    }
                                    oldStudent.add(s);
                                    mapIdFacultyToStudents.put(s.getIdFaculty(), oldStudent);
                                }
                                else mStudents.add(s);
                            }
                            else {
                                if(s.getIdFaculty() != currentId) {
                                    ArrayList<Student> oldStudent = mapIdFacultyToStudents.get(s.getIdFaculty());
                                    if(oldStudent == null) {
                                        oldStudent = new ArrayList<Student>();
                                    }
                                    oldStudent.add(s);
                                    mapIdFacultyToStudents.put(s.getIdFaculty(), oldStudent);
                                    mStudents.remove(mPosition);
                                }
                                else mStudents.set(mPosition, s);
                            }
                            mStudentListAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),
                                    "Студент: " + s.getFIO().toString() + "\nУспешно сохранён", Toast.LENGTH_SHORT).show();
                        }else if (result.getResultCode() == Activity.RESULT_CANCELED){

                        }
                    }
                }
        );
    }
    public static Facultet getById(int id) {
        for(Facultet facultet: mFacultets) {
            if(facultet.getId() == id) {
                return facultet;
            }
        }
       return null;
    }
    public Facultet getByName(String name) {
        for(Facultet facultet : mFacultets) {
            if(facultet.getName().equals(name)) {
                return facultet;
            }
        }
        return null;
    }
    public void createFacultetList() {
        if(mFacultets == null) mFacultets = new ArrayList<>();
        loadFacultets();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ListView listView = findViewById(R.id.lvList2);
        int position = listView.getCheckedItemPosition();

        switch (item.getItemId()){
            case android.R.id.home:
                if(drawer.isOpen()) {
                  drawer.close();
                }
                else {
                    drawer.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.miEdit:{
                if(listView.isSelected()){
                    Intent intent = new Intent(MainActivity.this, StudentInfoActivity.class);
                    intent.putExtra("student", mStudents.get(position));
                    intent.putParcelableArrayListExtra("facultets", mFacultets);
                    intent.putExtra("currentFacultet", currentId);
                    mPosition=position;
                    mIntentActivityResultLauncher.launch(intent);
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Студент не выбран", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.miAdd:{
                Intent intent = new Intent(MainActivity.this, StudentInfoActivity.class);

                //mStudents.add(new Student("",getById(currentId),"", ""));
                //intent.putExtra("student", mStudents.get(mStudents.size()-1));
                Student s = new Student();
                s.setIdFaculty(currentId);
                s.setNameFaculty(getById(currentId).getName());
                intent.putExtra("student", s);
                intent.putExtra("currentFacultet", currentId);
                intent.putParcelableArrayListExtra("facultets", mFacultets);
                mPosition=mStudents.size() + 1;
                mIntentActivityResultLauncher.launch(intent);
                return true;
            }
            case R.id.miDelete:{
                if(listView.isSelected()) {
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                            MainActivity.this);
                    quitDialog.setTitle("Удалить студента \"" + mStudents.get(position).getFIO() + "\"?");

                    quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO
                            // удалить студента в БД
                            deleteData(mStudents.get(position));
                            mStudents.remove(position);
                            mStudentListAdapter.notifyDataSetChanged();
                        }
                    })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    quitDialog.show();
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Студент не выбран", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.miAbout:{
                AlertDialog.Builder infoDialog = new AlertDialog.Builder(MainActivity.this);
                infoDialog.setTitle("О программе");
                infoDialog.setMessage("Задание на зачет.\n2021г\nКраснодар");
                infoDialog.setCancelable(false);
                infoDialog.setPositiveButton("Прочитано", null);
                infoDialog.show();
                return true;
            }
            case R.id.miExit:{
                finish();
                return true;
            }
            default:{}
        }
        return super.onOptionsItemSelected(item);
    }

    public void createStudentList() {
        mStudents=new ArrayList<>();
        //mStudents.add(new Student("ABC", new Facultet("ghghg", 5), "3"));
        ListView listView = findViewById(R.id.lvList2);
        mStudentListAdapter=new StudentListAdapter(mStudents,this);
        listView.setAdapter(mStudentListAdapter);
        AdapterView.OnItemClickListener clStudent = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((((LinearLayout) findViewById(R.id.ll_info_student)).getVisibility()==View.GONE) || checkedItemPosition != position){
                    listView.setItemChecked(position,true);
                    listView.setSelected(true);
                    ((LinearLayout) findViewById(R.id.ll_info_student)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tvInfo_FIO)).setText(mStudents.get(position).getFIO());
                    ((TextView) findViewById(R.id.tvInfo_Fac)).setText(mStudents.get(position).getNameFaculty());
                    ((TextView) findViewById(R.id.tvInfo_Group)).setText(mStudents.get(position).getGroup());
//  del                  ((TextView) findViewById(R.id.tvInfo_Phone)).setText(mStudents.get(position).getNameFaculty());
                    checkedItemPosition = position;
                }else {
                    ((LinearLayout) findViewById(R.id.ll_info_student)).setVisibility(View.GONE);
                    listView.setItemChecked(position,false);
                    listView.setSelected(false);
                    checkedItemPosition = -1;
                }
                mStudentListAdapter.colorChecked(position,parent);
            }
        };
//  del     AdapterView.OnItemLongClickListener longClickListenerStudent = new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
//               phone_calling(view, position);
//                return true;
//            }
//        };
        listView.setOnItemClickListener(clStudent);
//  del      listView.setOnItemLongClickListener(longClickListenerStudent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
      if (mStudents != null){
           for(Student student: mStudents) {
               System.out.println("saveData");
               saveData(student);
           }
        }

    }
    // TODO выбор элементов
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Facultet facultet = getByName((String) item.getTitle());
        if(facultet != null) {
            System.out.println("facultet not null");
            ArrayList<Student> oldStudents = new ArrayList<>();
            oldStudents.addAll(mStudents);
            mapIdFacultyToStudents.put(currentId, oldStudents);
            currentId = facultet.getId();
            loadStudents();
        }
        else {
            System.out.println("facultet is null");
        }
        return false;
    }
   //TODO
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    // TODO сохранить общую информацию о студенте
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
    public void loadStudentsFromDB() {
        if(currentId != -1) {
            mStudents.clear();
            if(mapIdFacultyToStudents.containsKey(currentId)) {
                ArrayList<Student> oldStudents = mapIdFacultyToStudents.get(currentId);
                if(!oldStudents.isEmpty()) {
                    mStudents.addAll(oldStudents);
                    mStudentListAdapter.notifyDataSetChanged();
                }

            }
            else {
                BackgroundTask backgroundTask = new BackgroundTask(this);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                backgroundTask.execute("get_students", String.valueOf(getById(currentId).getId()));
                //mStudentListAdapter.notifyDataSetChanged();
            }
        }
    }
    public void deleteData(Student student) {
        BackgroundTask backgroundTask = new BackgroundTask(this);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        backgroundTask.execute("delete_student", gson.toJson(student));
    }
    public void loadFacultets() {
        if(mFacultets.isEmpty()) {
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("get_facultets");
        }
    }
}