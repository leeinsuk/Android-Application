package com.example.mobile_project_important_memo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*메모 생성 화면 엑티비티*/
public class MemoCreateActivity extends AppCompatActivity {

    RatingBar setStar;
    Button cancelBtn, okayBtn;
    EditText memoText;
    private int memo_create_or_modify;//생성일때 0 수정일때 1
    private String loadTimeData;// 디비 저장시간이랑 비교 할 변수
    private int arraylistRemoveValues, recyclerviewRemoveValues;// intent로 받아올값


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_create);

        setStar = findViewById(R.id.ratingBar2);
        memoText = findViewById(R.id.memoText);
        cancelBtn = findViewById(R.id.cancelBtn);
        okayBtn = findViewById(R.id.okayBtn);

        /*수정일때 정보 받아오는 부분*/
        Intent intent = getIntent();// 정보 받아오기
        if(intent!=null){
            memo_create_or_modify=intent.getIntExtra("memoflag",0);
            setStar.setRating(intent.getIntExtra("RatingbarNum",0));
            memoText.setText(intent.getStringExtra("Memotext"));
            loadTimeData = intent.getStringExtra("TimeData");
            arraylistRemoveValues = intent.getIntExtra("arraylistRemoveValues",0);
            recyclerviewRemoveValues = intent.getIntExtra("recyclerviewRemoveValues",0);
        }

        /*메모 생성인지 수정인지 판단*/
        if(memo_create_or_modify==1){
            setTitle("메모 수정");
        }else{
            setTitle("메모 생성");
            setStar.setRating(1);
        }

        /*수정할때 내용없음이면 텍스트 지워주기*/
        if(memoText.getText().toString().equals("내용 없음")){
            memoText.setText("");
        }

        /*취소 버튼*/
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*확인버튼 눌렸을때 수정일때는 원래있던거 삭제후 다시저장 새로만들때는 그냥 저장*/
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(memo_create_or_modify==1){//메모 수정하기 일때

                    if(memoText.getText().toString().equals("")){// 텍스트에 아무것도 적지않으면 내용없음으로 저장
                        memoText.setText("내용 없음");
                        MainActivity.showToast(MainActivity.mainContext,"작성한 내용이 없습니다");
                    }else{
                        MainActivity.showToast(MainActivity.mainContext,"수정 완료");
                    }
                    MainActivity.update_values((int)setStar.getRating(),memoText.getText().toString(),MainActivity.currentTime(),loadTimeData);// 디비에 데이터 업데이트 시간 수정
                    MainActivity.global_load_data.set(arraylistRemoveValues,
                            new DataArray(MainActivity.global_load_data.get(arraylistRemoveValues).NO, (int)setStar.getRating(), memoText.getText().toString(), MainActivity.currentTime()));//리스트 데이터 수정
                    MainActivity.adapter.notifyItemChanged(recyclerviewRemoveValues);// 리사이클러뷰 아이템 변경 알림
                    MainActivity.recyclerView.smoothScrollToPosition(recyclerviewRemoveValues);//변경된 아이템 위치로 스크롤 이동

                }else{// 메모 생성하기 일때

                    if(memoText.getText().toString().equals("")){// 텍스트에 아무것도 적지않으면 내용없음으로 저장
                        memoText.setText("내용 없음");
                        MainActivity.showToast(MainActivity.mainContext,"작성한 내용이 없습니다");
                    }else{
                        MainActivity.showToast(MainActivity.mainContext,"생성 완료");
                    }
                    MainActivity.save_values((int)setStar.getRating(),memoText.getText().toString(),MainActivity.currentTime());// 디비에 데이터 입력하기
                    MainActivity.global_load_data.add(
                            new DataArray(++MainActivity.NO,(int)setStar.getRating(), memoText.getText().toString(), MainActivity.currentTime()));//전역 arraylist에 데이터넣기 NO에 1더하고 넣기
                    MainActivity.adapter.notifyItemInserted(0);//리사이클러뷰 맨위 인덱스에 데이터 추가
                    MainActivity.recyclerView.smoothScrollToPosition(0);// 스크롤 맨 위로 올리기
                }

                /*현재 모드에 따른 정렬 하기*/
                switch (MainActivity.CURRENTSORTMODE){
                    case MainActivity.SORTORDERUSER:
                        if((int)MainActivity.ratingBar.getRating()==0){// 선택한 중요도에 맞게 정렬
                            /*기본 정렬
                            *
                            * */
                        }else{// 중요도 갯수 정렬
                            MainActivity.RatingBarArraySort((int)MainActivity.ratingBar.getRating());
                        }
                        break;
                    case MainActivity.SORTORDERUP:
                        MainActivity.ArrayListSortOrder(MainActivity.global_load_data, MainActivity.SORTORDERUP);
                        break;
                    case MainActivity.SORTORDERDOWN:
                        MainActivity.ArrayListSortOrder(MainActivity.global_load_data, MainActivity.SORTORDERDOWN);
                        break;
                }
                finish();
            }
        });

        /*레이팅바 중요도를 꼭 선택하게*/
        setStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating==0){
                    MainActivity.showToast(MainActivity.mainContext,"중요도를 꼭 선택해주세요");
                    setStar.setRating(1);
                }
            }
        });


    }
}
