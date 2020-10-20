package com.example.mobile_project_important_memo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/*커스텀 다이얼로그 기능 클래스*/
public class CustomDialog implements View.OnClickListener {
    private Context context;
    private  String touch_memo_getText;
    private  int touch_memo_getStarNum;
    private  String touch_memo_getSaveTime;
    Dialog dialog;
    Button modifyBtn, deleteBtn, previousBtn;

    int arraylistRemoveValues;// 현재 터치된 메모의 Arraylist의 index와 같아질 값 (리사이클러뷰의 삭제할 인덱스)
    int recyclerviewRemoveValues;// 리사이클러뷰에서 삭제할 인덱스

    /*생성자*/
    public CustomDialog(Context context, String memoText, int starnum, String timeData){
        this.context = context;
        this.touch_memo_getText = memoText;
        this.touch_memo_getStarNum = starnum;
        this.touch_memo_getSaveTime = timeData;
    }

    public void createDig(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.alertdialog);
        dialog.show();

        modifyBtn = dialog.findViewById(R.id.alertModifyBtn);
        deleteBtn = dialog.findViewById(R.id.alertDeleteBtn);
        previousBtn = dialog.findViewById(R.id.alertPreviousBtn);

        modifyBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
    }

    /*다이얼로그 터치 이벤트*/
    public void onClick(View view){
        switch (view.getId()){

                /*수정버튼 클릭*/
            case R.id.alertModifyBtn:
                MainActivity.showToast(MainActivity.mainContext,"수정");
                Intent intent = new Intent(context,MemoCreateActivity.class);
                intent.putExtra("memoflag",1);
                intent.putExtra("RatingbarNum",touch_memo_getStarNum);
                intent.putExtra("Memotext",touch_memo_getText);
                intent.putExtra("TimeData", touch_memo_getSaveTime);
                search_ArrayList_DB_index();//적용할 위치 찾기
                intent.putExtra("arraylistRemoveValues",arraylistRemoveValues);
                intent.putExtra("recyclerviewRemoveValues",recyclerviewRemoveValues);
                context.startActivity(intent);
                dialog.dismiss();
                break;

                /*삭제버튼 클릭*/
            case R.id.alertDeleteBtn:
                dialog.dismiss();
                ask_delete();
                break;

                /*취소버튼 클릭*/
            case R.id.alertPreviousBtn:
                MainActivity.showToast(MainActivity.mainContext,"취소");
                dialog.dismiss();
                break;
        }

    }

    /*삭제 확인 다이얼로그*/
    private  void ask_delete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mainContext);
        builder.setTitle("정말로 삭제하시겠습니까?");

        /*삭제 부분*/
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                search_ArrayList_DB_index();//인덱스 찾기
                MainActivity.global_load_data.remove(arraylistRemoveValues);// ArrayList에서 데이터 삭제
                MainActivity.delete_values(touch_memo_getSaveTime);// 데이터베이스에서 해당 행 삭제
                MainActivity.adapter.notifyItemRemoved(recyclerviewRemoveValues);// 리사이클러뷰에서 데이터 삭제
                MainActivity.showToast(context,"삭제 완료");
                if(MainActivity.CURRENTSORTMODE==MainActivity.SORTORDERUSER&&MainActivity.RECYCLERVIEW_PRINT_COUNT!=-1){//유저모드에서 중요도 선택했을때 표시할 리스트 갯수 줄여주기
                    MainActivity.RECYCLERVIEW_PRINT_COUNT--;
                }
            }
        });
        /*취소 부분*/
        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.showToast(context,"취소");
            }
        });
        builder.show();
    }

    /*변경할 ArrayList인덱스와 디비 인덱스 찾기*/
    public void search_ArrayList_DB_index(){

        /*arraylistRemoveValues 현재 터치된 메모의 Arraylist의 index와 같아질 값 (리사이클러뷰의 삭제할 인덱스)
        recyclerviewRemoveValues 리사이클러뷰에서 삭제할 인덱스
        ArrayList의 savaTime이랑 리사이클러뷰의 터치된 뷰에 saveTime 비교해서 터치된 뷰의 위치 인덱스 찾기*/

        for(int i=MainActivity.global_load_data.size()-1;i>=0;i--){
            if(MainActivity.global_load_data.get(i).SAVETIME.equals(touch_memo_getSaveTime)){
                arraylistRemoveValues = i;
                recyclerviewRemoveValues=(MainActivity.global_load_data.size()-1)-i;
                break;
            }

        }
    }

}
