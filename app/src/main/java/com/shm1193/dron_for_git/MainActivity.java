package com.shm1193.dron_for_git;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView displayData;
    EditText customerName, customerHP, materialInfo, materialWeight;
    Button save, clear, next;

    String getName, getMtInfo, getMtWgt, getHP;

    static final int PORT = 3000;
    public static Server server = new Server(PORT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 각종 View들과 DB를 만들기
        displayData = (TextView) findViewById(R.id.displayData);
        displayData.setMovementMethod(new ScrollingMovementMethod());

        customerName = (EditText) findViewById(R.id.customerName);
        customerHP = (EditText) findViewById(R.id.customerHP);
        materialInfo = (EditText) findViewById(R.id.materialInfo);
        materialWeight = (EditText) findViewById(R.id.materialWeight);

        save = (Button) findViewById(R.id.save);
        clear = (Button) findViewById(R.id.clear);
        next = (Button) findViewById(R.id.next);

        /**
         * 서버 통신 쪽 코드 (접속용 메인 서버와 데이터 교환을 위한 Solid 서버)
         *
         * < 메인 서버 구성 > - 메인 서버에서 port로 접속하는 것은 형식일뿐, 실은 동작의 선택을 할 뿐이다
         *  :3000/autologin
         *      -> 상대측 서버에서 드론의 WebID를 얻기 위해 접속하는 port
         *  :3030/ds
         *      -> 드론이 가져온 정보를 보내달라고 요청하는 port
         */
        //TextView text = findViewById(R.id.server);
        //String startTxt = "Server is now started";

        //Server
        try {
            server.start();

            //Log.i("", startTxt);
            //text.setText(startTxt);
            Log.i("CANREAD", String.valueOf(Environment.getExternalStorageDirectory().canRead()));
            Log.i("PATH", Environment.getExternalStorageDirectory().getAbsolutePath() + "/www/");

        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * SQLite DB를 이용해 드론이 데이터를 수정/교환하는 부분
         * <현재 존재하는 정보>
         * - 고객 이름, 고객 전화번호, 물품 종류, 물품 무게
         */
        final MySQLiteDB mydb = new MySQLiteDB(getApplicationContext(), "Customer.db", null, 1);

        // 처음에 정보 있는지 없는지 보여주기 위해
        displayData.setText(mydb.getResult());

        // -------------------------- 버튼 액션 처리 부분 -------------------------------
        // 입력한 값들을 DB에 저장, 이미 정보가 존재한다면 수정
        save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty())
                    Toast.makeText(MainActivity.this, "아직 작성되지 않은 란이 존재합니다", Toast.LENGTH_SHORT).show();
                else {
                    getName = customerName.getText().toString();
                    getHP = customerHP.getText().toString();
                    getMtInfo = materialInfo.getText().toString();
                    getMtWgt = materialWeight.getText().toString();

                    mydb.insert(getName, getHP, getMtInfo, getMtWgt);

                    // 왜 이전 출력값들이 안사라지지???
                    displayData.setText("");
                    displayData.setText(mydb.getResult());

                    customerName.setText("");
                    customerHP.setText("");
                    materialInfo.setText("");
                    materialWeight.setText("");

                    Toast.makeText(MainActivity.this, "저장했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 이미 입력된 값들을 초기화
        clear.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerName.setText("");
                customerHP.setText("");
                materialInfo.setText("");
                materialWeight.setText("");

                mydb.delete();

                displayData.setText(mydb.getResult());

                Toast.makeText(MainActivity.this, "삭제했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        /** 수정해야 함 -> 지우던지 고치던지/ 고칠 가능성이 농후  */
        // 모든 데이터 저장 후 다음 액티비티로 넘어감
        next.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 넘어갈 액티비티 추가하고 여기에 설정해주기!!! */
                //Intent intent = new Intent(MainActivity.this, );
            }
        });
        // ---------------------------------------------------------------

        // 데이터들을 URL에 쿼리 형식으로 만들어 접속함


    }

    // Server의 연결을 끊어주기 위함
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (server != null)
            server.stop();
    }

    /** --------------------------------------- 함수 시작 --------------------------------------- */

    // 데이터 값 입력란이 비어있지 않도록 하기 위한 '빈칸 검사 함수'
    public boolean isEmpty() {
        if (customerName.getText().toString().equals("")) return true;
        else if (customerHP.getText().toString().equals("")) return true;
        else if (materialInfo.getText().toString().equals("")) return true;
        else if (materialWeight.getText().toString().equals("")) return true;

        return false;
    }

}