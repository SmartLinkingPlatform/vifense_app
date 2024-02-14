package com.obd2.dgt.ui.MainListActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.obd2.dgt.R;
import com.obd2.dgt.btManage.Trouble;
import com.obd2.dgt.btManage.TroubleCodes;
import com.obd2.dgt.dbManage.TableInfo.TroubleTable;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.ListAdapter.TroubleLis.CTroubleAdapter;
import com.obd2.dgt.ui.ListAdapter.TroubleLis.CTroubleItem;
import com.obd2.dgt.ui.ListAdapter.TroubleLis.PTroubleAdapter;
import com.obd2.dgt.ui.ListAdapter.TroubleLis.PTroubleItem;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class DiagnosisActivity extends AppBaseActivity {
    ImageView diag_prev_btn;
    ImageView diagnosis_btn;
    TextView diagnosis_btn_text;
    RecyclerView current_trouble_recycle_view;
    CTroubleAdapter cTroubleAdapter;
    ArrayList<CTroubleItem> cTroubleItems = new ArrayList<>();
    RecyclerView past_trouble_recycle_view;
    PTroubleAdapter pTroubleAdapter;
    ArrayList<PTroubleItem> pTroubleItems = new ArrayList<>();
    ProgressBar progressBar;
    Dialog dialog;
    String dlg_text = "";
    private static DiagnosisActivity instance;
    public static DiagnosisActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);
        instance = this;

        TroubleTable.getTroubleCodeTable();

        initLayout();
    }

    private void initLayout() {
        diag_prev_btn = findViewById(R.id.diag_prev_btn);
        diag_prev_btn.setOnClickListener(view -> onPrevActivityClick());

        diagnosis_btn = findViewById(R.id.diagnosis_btn);
        diagnosis_btn.setOnClickListener(view -> onDiagnosisClick());

        diagnosis_btn_text = findViewById(R.id.diagnosis_btn_text);
        diagnosis_btn_text.setText(R.string.diagnosing_text);

        current_trouble_recycle_view = findViewById(R.id.current_trouble_recycle_view);
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        current_trouble_recycle_view.setLayoutManager(verticalLayoutManager);



        past_trouble_recycle_view = findViewById(R.id.past_trouble_recycle_view);
        LinearLayoutManager pastLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        past_trouble_recycle_view.setLayoutManager(pastLayoutManager);
        PTroubleItem item;
        for (int i = 0; i < MyUtils.troubleCodes.size(); i++) {
            String[] past_code = MyUtils.troubleCodes.get(i);
            int cid = Integer.parseInt(past_code[0]);
            String str_code = past_code[1];
            String str_description = past_code[2];
            item = new PTroubleItem(cid, str_code, str_description);
            pTroubleItems.add(item);
        }
        pTroubleAdapter = new PTroubleAdapter(getContext(), pTroubleItems, pTroubleCodeListListener);
        past_trouble_recycle_view.setAdapter(pTroubleAdapter);


        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(5);
        progressBar.setVisibility(View.GONE);
    }

    private void setCurrentTroubleCodeList(String findCodes) {
        String[] codes = findCodes.split(",");
        CTroubleItem item;
        for (int i = 0; i < codes.length; i++) {
            String tDesc = "";
            tDesc = Trouble.TCodes.get(codes[i]);
            item = new CTroubleItem(TroubleTable.max_cid, codes[i], tDesc);
            cTroubleItems.add(item);

            String[][] fields = new String[][]{
                    {"code", codes[i]},
                    {"description", tDesc}
            };
            TroubleTable.insertTroubleTable(fields);
        }
        cTroubleAdapter = new CTroubleAdapter(getContext(), cTroubleItems, cTroubleCodeListListener);
        current_trouble_recycle_view.setAdapter(cTroubleAdapter);
    }

    private CTroubleAdapter.ItemClickListener cTroubleCodeListListener = new CTroubleAdapter.ItemClickListener() {
        @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
        @Override
        public void onItemClick(View v, int position) {
            String url = "https://m.search.naver.com/search.naver?query=";
            url += cTroubleItems.get(position).code_num;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };
    private PTroubleAdapter.ItemClickListener pTroubleCodeListListener = new PTroubleAdapter.ItemClickListener() {
        @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
        @Override
        public void onItemClick(View v, int position) {
            String url = "https://m.search.naver.com/search.naver?query=";
            url += pTroubleItems.get(position).code_num;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };
    private void onDiagnosisClick() {
        if (MyUtils.btSocket == null) {
            Toast.makeText(MyUtils.mContext, R.string.non_connecting_text, Toast.LENGTH_SHORT).show();
            return;
        }

        isTest = true;
        MyUtils.isDiagnosis = true;
        diagnosis_btn.setBackgroundResource(R.drawable.button4_prog);
        progressBar.setVisibility(View.VISIBLE);
        new GetTroubleCodesTask().execute("GetTroubleCodes");

        TroubleTable.getTroubleCodeTable();
    }

    private void onPrevActivityClick() {
        onLRChangeLayout(DiagnosisActivity.this, MainActivity.class);
        finish();
    }

    private void sendCommand(String command) {
        try {
            MyUtils.btSocket.getOutputStream().write((command).getBytes());
            MyUtils.btSocket.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readResponse() {
        try {
            byte[] buffer = new byte[4096];
            int bytesRead = MyUtils.btSocket.getInputStream().read(buffer);
            final String rawResponse = new String(buffer, 0, bytesRead);
            Log.d("Trouble code", rawResponse);
            return rawResponse;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    boolean isTest = true;
    int prog = 0;
    class GetTroubleCodesTask extends AsyncTask<String, Integer, Boolean> {
        @SuppressLint({"SetTextI18n", "WrongThread"})
        protected Boolean doInBackground(String... str) {
            String result = "";
            while (isTest) {
                try {
                    if (MyUtils.btSocket == null)
                        break;

                    String command = "03\r\n";
                    prog++;
                    if (prog == 1) {
                        new ObdResetCommand().run(MyUtils.btSocket.getInputStream(), MyUtils.btSocket.getOutputStream());
                    } else if (prog == 2) {
                        new EchoOffCommand().run(MyUtils.btSocket.getInputStream(), MyUtils.btSocket.getOutputStream());
                    } else if (prog == 3) {
                        new LineFeedOffCommand().run(MyUtils.btSocket.getInputStream(), MyUtils.btSocket.getOutputStream());
                    } else if (prog == 4) {
                        sendCommand(command);
                        result = readResponse();
                    } else if (prog == 5) {
                        TroubleCodes tcodes = new TroubleCodes();
                        sendCommand(command);
                        result = tcodes.getFormattedResult(readResponse());
                        isTest = false;
                    }
                    runOnUiThread(() -> {
                        progressBar.setProgress(prog);
                    });
                    SystemClock.sleep(1000);
                } catch (Exception e) {
                    isTest = false;
                    e.printStackTrace();
                }
            }

            MyUtils.isDiagnosis = false;

            if (!result.isEmpty()) {
                String codes = "";
                String[] res_codes = result.split("\n");
                ArrayList<String> arrayList = new ArrayList<>();
                int i = 0;
                for (String item : res_codes) {
                    if (!arrayList.contains(item) && !item.isEmpty()) {
                        arrayList.add(item);
                        if (i == 0)
                            codes = item;
                        else
                            codes += "," + item;
                        i++;
                    }
                }
                dlg_text = codes;
                if (!codes.isEmpty()) {
                    runOnUiThread(() -> {
                        setCurrentTroubleCodeList(dlg_text);
                    });
                }
            } else {
                dlg_text = getString(R.string.non_trouble_codes);
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                progressBar.setProgress(prog);
                progressBar.setVisibility(View.GONE);
                diagnosis_btn.setBackgroundResource(R.drawable.button4_comp);
                diagnosis_btn_text.setText(R.string.diagnosing_completed);
                prog = 0;
                showDialog();
                handler.removeMessages(0);
            }, 1000);

            GetTroubleCodesTask.this.cancel(true);
            return false;
        }
    }

    public void onPastTroubleDeleteClick(int id){
        TroubleTable.deleteTroubleTable(id);
        TroubleTable.getTroubleCodeTable();

        PTroubleItem item;
        pTroubleItems.clear();
        for (int i = 0; i < MyUtils.troubleCodes.size(); i++) {
            String[] info = MyUtils.troubleCodes.get(i);
                item = new PTroubleItem(Integer.parseInt(info[0]), info[1], info[2]);
            pTroubleItems.add(item);
        }
        pTroubleAdapter.setData(pTroubleItems);
    }

    public static class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            // remove unwanted response from output since this results in erroneous error codes
            rawData = rawData.replace("SEARCHING...", "").replace("NODATA", "");
            if (rawData.length() < 5)
                rawData = "";
            return rawData;
        }
    }

    @SuppressLint("ResourceType")
    private void showDialog() {
        dialog = new Dialog(DiagnosisActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dlg_normal);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialog_normal_text = dialog.findViewById(R.id.dialog_normal_text);
        dialog_normal_text.setText(dlg_text);
        ImageView dialog_normal_btn = dialog.findViewById(R.id.dialog_normal_btn);
        dialog_normal_btn.setImageResource(R.drawable.confirm_off);
        dialog_normal_btn.setOnClickListener(view -> {
            dialog.dismiss();
            diagnosis_btn.setBackgroundResource(R.drawable.button4_off);
            diagnosis_btn_text.setText(R.string.diagnosing_text);
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onLRChangeLayout(DiagnosisActivity.this, MainActivity.class);
        finish();
    }

}