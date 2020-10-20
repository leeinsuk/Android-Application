package com.example.mobile_project_important_memo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    static ContactDBHelper dbHelper = null;
    FloatingActionButton memoCreateBtn;// 밑에 플러스 버튼
    static RecyclerView recyclerView;
    static ArrayList<DataArray> global_load_data = new ArrayList<DataArray>();// 디비에서 받아온 데이터 저장하는 배열
    static RecyclerView_Adapter adapter = new RecyclerView_Adapter(); // 어뎁터 생성
    static Context mainContext ;  //전달할 메인 컨텍스트
    static RatingBar ratingBar;
    private static Toast sToast;

    static int NO; //각 메모의 고유 넘버링
    final static int SORTORDERUSER=2, SORTORDERUP = 1, SORTORDERDOWN = 0;// 정렬 모드 정의
    static int CURRENTSORTMODE=SORTORDERUSER;// 처음 시작할때 정렬기본값 유저모드
    static  int RECYCLERVIEW_PRINT_COUNT=-1;// 유저모드에서 기본정렬일때 -1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("중요도 메모장");
        mainContext = this;
        global_load_data.clear();//처음 리스트 초기화
        init_tables();//dbHelper 초기화 함수
        load_values();//데이터 불러오기

        /*리사이클러뷰 생성부분*/
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        /*플로팅 버튼 눌렸을때 메모 생성창으로 넘어가는부분*/
        memoCreateBtn = findViewById(R.id.FloatingBtn);
        memoCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 메모 생성 엑티비티로 이동하기
                showToast(mainContext, "메모 생성");
                Intent intent = new Intent(MainActivity.this,MemoCreateActivity.class);
                startActivity(intent);

            }
        });

        /*레이팅바 클릭시 이벤트 (정렬하기)*/
        ratingBar = findViewById(R.id.activityMain_ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float rating, boolean fromUser) {

                if(ratingBar.getVisibility()==View.VISIBLE)TouchAnimationRatingBar();//레이팅바 Touch Animation

                if ((int)rating==0){// 중요도 0개 선택 기본보기
                    RECYCLERVIEW_PRINT_COUNT=-1;
                    ArrayListSortOrder(global_load_data,SORTORDERUSER);
                    recyclerView.smoothScrollToPosition(0);// 스크롤 맨 위로 올리기
                    showToast(mainContext, "정렬:기본 보기");
                }else{//선택한 중요도에 따라 분류 보기
                    showToast(mainContext,"정렬:중요도 "+(int)rating+"개");
                    RatingBarArraySort((int)rating);
                    recyclerView.smoothScrollToPosition(0);// 스크롤 맨 위로 올리기
                }
            }
        });

    }

    /*메뉴 생성 부분*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    /*메뉴 아이템 클릭 이벤트*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_user:

                ratingBar.setRating(0);// 처음 기본모드로 별 갯수 맞추기
                CURRENTSORTMODE = SORTORDERUSER;// 정렬 모드 유저모드로 변경
                /*정렬 함수 Comparator 실행 기본순*/
                ArrayListSortOrder(global_load_data, SORTORDERUSER);
                recyclerView.smoothScrollToPosition(0);// 스크롤 맨 위로 올리기
                item.setChecked(true);
                if(ratingBar.getVisibility()==View.INVISIBLE)VisibleAnimationRatingBar();//레이팅바 VISIBLE Animation
                ratingBar.setVisibility(View.VISIBLE);
                showToast(mainContext, "정렬:사용자 설정");
                break;
            case R.id.menu_order_high:

                if(ratingBar.getVisibility()==View.VISIBLE)InvisibleAnimationRatingBar();//레이팅바 INVISIBLE Animation

                CURRENTSORTMODE = SORTORDERUP;// 정렬 모드 오름차순으로 변경
                /*정렬 함수 Comparator 실행 오름차순*/
                ArrayListSortOrder(global_load_data, SORTORDERUP);
                recyclerView.smoothScrollToPosition(0);// 스크롤 맨 위로 올리기
                item.setChecked(true);
                ratingBar.setVisibility(View.INVISIBLE);
                showToast(mainContext, "정렬:중요도 높은순");
                break;
            case R.id.menu_order_low:

                if(ratingBar.getVisibility()==View.VISIBLE)InvisibleAnimationRatingBar();//레이팅바 INVISIBLE Animation

                CURRENTSORTMODE = SORTORDERDOWN;// 정렬 모드 내림차순으로 변경
                /*정렬 함수 Comparator 실행 내림차순*/
                ArrayListSortOrder(global_load_data, SORTORDERDOWN);
                recyclerView.smoothScrollToPosition(0);// 스크롤 맨 위로 올리기
                item.setChecked(true);
                ratingBar.setVisibility(View.INVISIBLE);
                showToast(mainContext, "정렬:중요도 낮은순");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*헬퍼 객체 생성 초기화*/
    private void init_tables(){
        dbHelper = new ContactDBHelper(this);
    }


    /*디비 데이터 저장 함수*/
    protected static void save_values(int numstar, String memotext, String currentTime){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sqlInsert = ContractDBCtrct.SQL_INSERT +
                    " (" + numstar + ", " + "'" + memotext + "',  '" + currentTime + "')";   //데이터 넣기
        db.execSQL(sqlInsert);

        db.close();

    }

    /*디비 데이터 조회하기*/
    protected static void load_values(){
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(ContractDBCtrct.SQL_SELECT, null);
            cursor.moveToFirst();

            /*마지막 행까지 다 읽어오기*/
            do{
                int load_memo_no = cursor.getInt(0);
                int load_memo_starnum = cursor.getInt(1);
                String load_memo_text = cursor.getString(2);
                String load_memo_saveTime = cursor.getString(3);

                global_load_data.add(new DataArray(load_memo_no, load_memo_starnum, load_memo_text, load_memo_saveTime));//ArrayList에 데이터 넣기

                if(cursor.isLast())NO=load_memo_no;//디비에서 메모새로생성할때 붙여줄 고유 넘버링 받아오기
            }while(cursor.moveToNext());

            cursor.close();
            db.close();

        } catch (RuntimeException e){//데이터베이스가 없거나 데이터값이 존재하지 않을때
            Log.d("insuk", "load_values: database not exist");
            Toast.makeText(mainContext, "메모가 존재하지 않습니다\n새로 생성해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    /*디비 데이터 삭제하기*/
    protected  static void delete_values(String saveTime){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sqlDelete = ContractDBCtrct.SQL_DELETE + "'" + saveTime + "'" ;
        db.execSQL(sqlDelete);

        db.close();
    }


    /*디비 데이터 업데이트*/
    protected  static void update_values(int starnum, String text, String timedata, String compareSavaTime){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sqlUpdate = ContractDBCtrct.SQL_UPDATE + "STARNUM = '" + starnum + "', MEMOTEXT = '" + text + "', SAVETIME = '" + timedata  +"' WHERE SAVETIME = '" + compareSavaTime + "'";
        db.execSQL(sqlUpdate);

        db.close();
    }

    /*toast 중복제거(한번만 띄우기)*/
    public static void showToast(Context context, String message){
        if(sToast == null){
            sToast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        }else{
            sToast.setText(message);
        }
        sToast.show();
    }

    /*현재 시간 출력*/
    public static String currentTime(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentTime = sdfNow.format(date);

        return currentTime;
    }

    /*ArrayList 정렬 함수*/
    public static void ArrayListSortOrder(ArrayList<DataArray> global_load_data, int option){

        switch (option){
            case SORTORDERUSER://유저 정렬
                Collections.sort(global_load_data, new Comparator<DataArray>() {
                    @Override
                    public int compare(DataArray o1, DataArray o2) {
                        if(o1.NO < o2.NO){
                            return -1;
                        }else if(o1.NO > o2.NO){
                            return 1;
                        }
                        return 0;
                    }
                });
                break;
            case SORTORDERUP:// 오름차순 정렬
                Collections.sort(global_load_data, new Comparator<DataArray>() {
                    @Override
                    public int compare(DataArray o1, DataArray o2) {
                        if(o1.STARNUM < o2.STARNUM){
                            return -1;
                        }else if(o1.STARNUM > o2.STARNUM){
                            return 1;
                        }
                        return 0;
                    }
                });
                break;
            case SORTORDERDOWN:// 내림차순 정렬
                Collections.sort(global_load_data, new Comparator<DataArray>() {
                    @Override
                    public int compare(DataArray o1, DataArray o2) {
                        if(o1.STARNUM > o2.STARNUM){
                            return -1;
                        }else if(o1.STARNUM < o2.STARNUM){
                            return 1;
                        }
                        return 0;
                    }
                });
                break;
        }
        adapter.notifyDataSetChanged();// 리사이클러뷰에 데이터 변경 알림
    }

    /*선택된 중요도에 따라 리스트 정렬하기*/
    public static void RatingBarArraySort(int getRatingBarNum){
        RECYCLERVIEW_PRINT_COUNT=0; // 표시할 갯수 다시 초기화
        ArrayList<DataArray> tempArray = new ArrayList<DataArray>();//임시 저장공간
        /*나머지 중요도에 해당하는 메모 임시저장*/// 역순으로 출력되기 때문
        for (int i=1; i<getRatingBarNum; i++){//선택된 중요도보다 작은것
            for (int j=0; j<=global_load_data.size()-1; j++){
                if(global_load_data.get(j).STARNUM==i){
                    tempArray.add(new DataArray(global_load_data.get(j).NO, global_load_data.get(j).STARNUM,
                            global_load_data.get(j).MEMOTEXT, global_load_data.get(j).SAVETIME));
                }
            }
        }
        for (int i=getRatingBarNum+1; i<=5; i++){//선택된 중요도 보다 큰것
            for (int j=0; j<=global_load_data.size()-1; j++){
                if(global_load_data.get(j).STARNUM==i){
                    tempArray.add(new DataArray(global_load_data.get(j).NO, global_load_data.get(j).STARNUM,
                            global_load_data.get(j).MEMOTEXT, global_load_data.get(j).SAVETIME));
                }
            }
        }
        /*현재 선택된 중요도에 해당하는 메모 찾아서 임시저장*/
        for (int i=0; i<=global_load_data.size()-1;i++){
            if(global_load_data.get(i).STARNUM==getRatingBarNum){
                tempArray.add(new DataArray(global_load_data.get(i).NO, global_load_data.get(i).STARNUM,
                        global_load_data.get(i).MEMOTEXT, global_load_data.get(i).SAVETIME));
                RECYCLERVIEW_PRINT_COUNT++;
            }
        }
        /*만약 해당되는 메모가 없을시 알림*/
        if(RECYCLERVIEW_PRINT_COUNT==0){
            showToast(mainContext,"해당되는 메모가 없습니다.");
        }
        global_load_data=tempArray;// 임시저장소에 넣은것 옮기기
        adapter.notifyDataSetChanged();// 리사이클러뷰에 데이터 변경 알림
    }


    /*레이팅바 터치 애니메이션*/
    public void TouchAnimationRatingBar(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ratingbar_up_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ratingbar_down_animation);
                ratingBar.startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ratingBar.startAnimation(animation);
    }

    /*레이팅바 없어지는 애니메이션*/
    public void InvisibleAnimationRatingBar(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ratingbar_invisible_animation);
        ratingBar.startAnimation(animation);
    }

    /*레이팅바 나타나는 애니메이션*/
    public void VisibleAnimationRatingBar(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ratingbar_visibile_animation);
        ratingBar.startAnimation(animation);
    }
}
