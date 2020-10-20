package com.example.mobile_project_important_memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*리사이클러뷰 어뎁터 클래스*/
public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerView_Adapter.ViewHolder> {

    /* 뷰홀더 클래스 따로 안만들고 여기에*/
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        RatingBar ratingBar;
        TextView timeTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recyclerview_item_textView);
            ratingBar = itemView.findViewById(R.id.recyclerview_item_ratingbar);
            timeTextView = itemView.findViewById(R.id.currentTimeText);

        }
    }

    @NonNull
    @Override
    public RecyclerView_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_item,parent,false);
        RecyclerView_Adapter.ViewHolder vh = new RecyclerView_Adapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView_Adapter.ViewHolder holder, final int position) { // position start index =0

        holder.ratingBar.setRating(MainActivity.global_load_data.get(MainActivity.global_load_data.size()-position-1).STARNUM);
        holder.textView.setText(MainActivity.global_load_data.get(MainActivity.global_load_data.size()-position-1).MEMOTEXT);
        holder.timeTextView.setText(MainActivity.global_load_data.get(MainActivity.global_load_data.size()-position-1).SAVETIME);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showToast(MainActivity.mainContext, "길게 누르면 속성");
            }
        });

        /*리사이클러뷰 텍스트 롱클릭시 이벤트*/
        holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                /*커스텀 다이얼로그 생성*/
                CustomDialog customDialog = new CustomDialog(MainActivity.mainContext, holder.textView.getText().toString(), (int)holder.ratingBar.getRating(), holder.timeTextView.getText().toString());
                customDialog.createDig();

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (MainActivity.CURRENTSORTMODE==MainActivity.SORTORDERUSER&&MainActivity.RECYCLERVIEW_PRINT_COUNT!=-1){//유저모드의 중요도 갯수 정렬일 때 그만큼만 출력
            return MainActivity.RECYCLERVIEW_PRINT_COUNT;
        }else {
            return MainActivity.global_load_data.size();//데이터
        }
    }
}
