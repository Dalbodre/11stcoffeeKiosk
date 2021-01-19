package sb.yoon.kiosk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import sb.yoon.kiosk.libs.Util;

public class ResultActivity extends Activity
{
    Intent mIntent;
    byte[] mResData;
    TransactionData trData;
    String mTotAmt = "";
    String mVat = "";
    String mSupAmt = "";
    String mPersonalNo = "";
    String mTraderType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getIntentData();
        parseData();
        try
        {
            makeReceipt();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    void getIntentData()
    {
        mIntent = getIntent();
        mResData = mIntent.getByteArrayExtra("resData");
        mTotAmt = mIntent.getStringExtra("totAmt");
        mVat = mIntent.getStringExtra("VAT");
        mSupAmt = mIntent.getStringExtra("supplyAmt");
        mPersonalNo = mIntent.getStringExtra("personalNo");
        mTraderType = mIntent.getStringExtra("traderType");
    }

    void parseData()
    {
        trData = new TransactionData();
        trData.SetData(mResData);
    }

    void makeReceipt() throws UnsupportedEncodingException
    {
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        StringBuffer sb4 = new StringBuffer();

        TextView tv = findViewById(R.id.headerText);

        if(Arrays.equals(trData.transactionCode, "IC".getBytes()) || Arrays.equals(trData.transactionCode, "MS".getBytes()))
        {
            if(Arrays.equals(trData.transferCode, "0210".getBytes()))
                tv.setText("[신용 승인]");
            else if(Arrays.equals(trData.transferCode, "0430".getBytes()))
                tv.setText("[신용 승인 취소]");
            /////////////////////////////////////////////////////////////
            sb1.append("카드번호 : ");
            sb1.append(new String(trData.filler, "EUC-KR"));
            sb1.append("\n");

            sb1.append("카드명칭 : ");
            sb1.append(new String(trData.cardCategoryName, "EUC-KR"));
            sb1.append("\n");

            sb1.append("매입사명 : ");
            sb1.append(new String(trData.purchaseCompanyName, "EUC-KR"));
            /////////////////////////////////////////////////////////////
            sb2.append("단말번호 : ");
            sb2.append(new String(trData.deviceNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("가맹번호 : ");
            sb2.append(new String(trData.merchantNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("승인일시 : ");
            sb2.append(new String(trData.transferDate, "EUC-KR"));
            sb2.append("\n");

            sb2.append("승인번호 : ");
            sb2.append(new String(trData.approvalNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("거래번호 : ");
            sb2.append(new String(trData.transactionUniqueNumber, "EUC-KR"));
            /////////////////////////////////////////////////////////////
            sb3.append("승인금액 : ");
            sb3.append(Util.formatCurrency(mTotAmt) + "원");
            sb3.append("\n");

            sb3.append("부가세액 : ");
            sb3.append(Util.formatCurrency(mVat) + "원");
            sb3.append("\n");

            sb3.append("공급가액 : ");
            sb3.append(Util.formatCurrency(mSupAmt) + "원");
            sb3.append("\n");

            sb3.append("잔액 : ");        // 선불카드인 경우만 출력
            sb3.append(Util.formatCurrency(trData.balance.toString().trim()) + "원");
            /////////////////////////////////////////////////////////////
            sb4.append(new String(trData.message1,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.message2,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.notice1,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.notice2,"EUC-KR"));
            /////////////////////////////////////////////////////////////

            tv = findViewById(R.id.text1);
            tv.setText(sb1.toString());
            tv = findViewById(R.id.text2);
            tv.setText(sb2.toString());
            tv = findViewById(R.id.text3);
            tv.setText(sb3.toString());
            tv = findViewById(R.id.text4);
            tv.setText(sb4.toString());
        }
        else if(Arrays.equals(trData.transactionCode, "HK".getBytes()))
        {
            if(Arrays.equals(trData.transferCode, "0210".getBytes()))
                tv.setText("[현금영수증 승인]");
            else if(Arrays.equals(trData.transferCode, "0430".getBytes()))
                tv.setText("[현금영수증 승인 취소]");

            /////////////////////////////////////////////////////////////
            sb1.append("고객정보 : ");
            sb1.append(mPersonalNo);
            /////////////////////////////////////////////////////////////
            sb2.append("단말번호 : ");
            sb2.append(new String(trData.deviceNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("승인일시 : ");
            sb2.append(new String(trData.transferDate, "EUC-KR"));
            sb2.append("\n");

            sb2.append("승인번호 : ");
            sb2.append(new String(trData.approvalNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("거래번호 : ");
            sb2.append(new String(trData.transactionUniqueNumber, "EUC-KR"));
            /////////////////////////////////////////////////////////////
            sb3.append("승인금액 : ");
            sb3.append(Util.formatCurrency(mTotAmt) + "원");
            if(mTraderType.equals("0"))
                sb3.append("[소득공제]");
            else if(mTraderType.equals("1"))
                sb3.append("[지출증빙]");
            sb3.append("\n");

            sb3.append("부가세액 : ");
            sb3.append(Util.formatCurrency(mVat) + "원");
            sb3.append("\n");

            sb3.append("공급가액 : ");
            sb3.append(Util.formatCurrency(mSupAmt) + "원");
            sb3.append("\n");

            /////////////////////////////////////////////////////////////
            sb4.append(new String(trData.message1,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.message2,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.notice1,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.notice2,"EUC-KR"));
            /////////////////////////////////////////////////////////////

            tv = findViewById(R.id.text1);
            tv.setText(sb1.toString());
            tv = findViewById(R.id.text2);
            tv.setText(sb2.toString());
            tv = findViewById(R.id.text3);
            tv.setText(sb3.toString());
            tv = findViewById(R.id.text4);
            tv.setText(sb4.toString());
        }
    }

    public void mOnClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeBtn:
                finish();
                break;
        }
    }

    public class TransactionData
    {
        public byte[] dataLength = new byte[4];
        public byte[] transactionCode = new byte[2];
        public byte[] operationCode = new byte[2];
        public byte[] transferCode = new byte[4];
        public byte[] transferType = new byte[1];
        public byte[] deviceNumber = new byte[10];
        public byte[] companyInfo = new byte[4];
        public byte[] transferSerialNumber = new byte[12];
        public byte[] status = new byte[1];
        public byte[] standardCode = new byte[4];
        public byte[] cardCompanyCode = new byte[4];
        public byte[] transferDate = new byte[12];
        public byte[] cardType = new byte[1];
        public byte[] message1 = new byte[16];
        public byte[] message2 = new byte[16];
        public byte[] approvalNumber = new byte[12];
        public byte[] transactionUniqueNumber = new byte[20];

        public byte[] merchantNumber = new byte[15];
        public byte[] IssuanceCode = new byte[2];
        public byte[] cardCategoryName = new byte[16];
        public byte[] purchaseCompanyCode = new byte[2];
        public byte[] purchaseCompanyName = new byte[16];
        public byte[] workingKeyIndex = new byte[2];
        public byte[] workingKey = new byte[16];
        public byte[] balance = new byte[9];
        public byte[] point1 = new byte[9];
        public byte[] point2 = new byte[9];
        public byte[] point3 = new byte[9];
        public byte[] notice1 = new byte[20];
        public byte[] notice2 = new byte[40];
        public byte[] reserved = new byte[5];
        public byte[] KSNETreserved = new byte[40];
        public byte[] filler = new byte[30];

        public void SetData(byte[] readData)
        {
            int readIdx = 1; // length 4, stx 1

            System.arraycopy(readData, 0, dataLength, 0, 4);
            readIdx += 4;
            System.arraycopy(readData, readIdx, transactionCode, 0, 2);
            readIdx += 2;
            System.arraycopy(readData, readIdx, operationCode, 0, 2);
            readIdx += 2;
            System.arraycopy(readData, readIdx, transferCode, 0, 4);
            readIdx += 4;
            System.arraycopy(readData, readIdx, transferType, 0, 1);
            readIdx += 1;
            System.arraycopy(readData, readIdx, deviceNumber, 0, 10);
            readIdx += 10;
            System.arraycopy(readData, readIdx, companyInfo, 0, 4);
            readIdx += 4;
            System.arraycopy(readData, readIdx, transferSerialNumber, 0, 12);
            readIdx += 12;
            System.arraycopy(readData, readIdx, status, 0, 1);
            readIdx += 1;
            System.arraycopy(readData, readIdx, standardCode, 0, 4);
            readIdx += 4;
            System.arraycopy(readData, readIdx, cardCompanyCode, 0, 4);
            readIdx += 4;
            System.arraycopy(readData, readIdx, transferDate, 0, 12);
            readIdx += 12;
            System.arraycopy(readData, readIdx, cardType, 0, 1);
            readIdx += 1;
            System.arraycopy(readData, readIdx, message1, 0, 16);
            readIdx += 16;
            System.arraycopy(readData, readIdx, message2, 0, 16);
            readIdx += 16;
            System.arraycopy(readData, readIdx, approvalNumber, 0, 12);
            readIdx += 12;
            System.arraycopy(readData, readIdx, transactionUniqueNumber, 0, 20);
            readIdx += 20;
            System.arraycopy(readData, readIdx, merchantNumber, 0, 15);
            readIdx += 15;
            System.arraycopy(readData, readIdx, IssuanceCode, 0, 2);
            readIdx += 2;
            System.arraycopy(readData, readIdx, cardCategoryName, 0, 16);
            readIdx += 16;
            System.arraycopy(readData, readIdx, purchaseCompanyCode, 0, 2);
            readIdx += 2;
            System.arraycopy(readData, readIdx, purchaseCompanyName, 0, 16);
            readIdx += 16;
            System.arraycopy(readData, readIdx, workingKeyIndex, 0, 2);
            readIdx += 2;
            System.arraycopy(readData, readIdx, workingKey, 0, 16);
            readIdx += 16;
            System.arraycopy(readData, readIdx, balance, 0, 9);
            readIdx += 9;
            System.arraycopy(readData, readIdx, point1, 0, 9);
            readIdx += 9;
            System.arraycopy(readData, readIdx, point2, 0, 9);
            readIdx += 9;
            System.arraycopy(readData, readIdx, point3, 0, 9);
            readIdx += 9;
            System.arraycopy(readData, readIdx, notice1, 0, 20);
            readIdx += 20;
            System.arraycopy(readData, readIdx, notice2, 0, 40);
            readIdx += 40;
            System.arraycopy(readData, readIdx, reserved, 0, 5);
            readIdx += 5;
            System.arraycopy(readData, readIdx, KSNETreserved, 0, 40);
            readIdx += 40;
            System.arraycopy(readData, readIdx, filler, 0, 30);
            readIdx += 30;
        }
    }
}
