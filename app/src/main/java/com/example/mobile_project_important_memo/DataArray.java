package com.example.mobile_project_important_memo;

/*디비에서 불러온 데이터값 저장하는 클래스*/
public class DataArray{
    final  int NO;
    final int STARNUM;
    final String MEMOTEXT;
    final String SAVETIME;

    public DataArray(int no, int starnum, String memotext, String savetime) {
        this.NO = no;
        this.STARNUM = starnum;
        this.MEMOTEXT = memotext;
        this.SAVETIME = savetime;
    }
}
