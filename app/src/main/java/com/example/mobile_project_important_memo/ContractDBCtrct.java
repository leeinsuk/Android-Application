package com.example.mobile_project_important_memo;

/*데이터베이스 계약 클래스*/
public class ContractDBCtrct {

    private  ContractDBCtrct(){};

    /*테이블 변수 정의*/
    public static final String DB_NAME = "MEMO_DATA";
    public static final String COL_NO = "NO";//기본키 자동증가
    public static final String COL_STARNUM = "STARNUM";
    public static final String COL_MEMOTEXT="MEMOTEXT";
    public static final String COL_SAVETIME = "SAVETIME";


    /*만약 DB가 만들어 있지 않으면 DB 생성*/// 기본키 COL_NO, COL_SVAETIME
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS "+ DB_NAME+" "+
            "("+
                COL_NO+             " INTEGER PRIMARY KEY AUTOINCREMENT"  + ", "+
                COL_STARNUM+        " INTEGER NOT NULL"  + ", "+
                COL_MEMOTEXT+       " TEXT"              + ", "+
                COL_SAVETIME+       " TEXT NOT NULL"     + ", "+
                " UNIQUE (" + COL_NO + ", " + COL_SAVETIME + ")"+
            ")";

    /*특정 데이터 조회 COL_NO에 해당하는 데이터 전부*/
    public static final String SQL_SELECT = "SELECT * FROM " + DB_NAME;

    /*데이터 입력*/// 기본키는 자동증가
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO "+ DB_NAME + " "+
            "(" + COL_STARNUM + ", " + COL_MEMOTEXT +", " + COL_SAVETIME + ") VALUES ";

    /*특정 데이터 로우 삭제*/
    public static final String  SQL_DELETE = "DELETE FROM "+ DB_NAME +" WHERE "+COL_SAVETIME+" = ";

    /*데이터 업데이트*/
    public static final String SQL_UPDATE = "UPDATE "+ DB_NAME + " SET ";



}
