package com.obd2.dgt.ui.MainListActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.MessageInfoTable;
import com.obd2.dgt.dbManage.TableInfo.TroubleTable;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.InfoActivity.MessageActivity;
import com.obd2.dgt.ui.ListAdapter.MainList.MainListAdapter;
import com.obd2.dgt.ui.ListAdapter.MainList.MainListItem;
import com.obd2.dgt.ui.ListAdapter.MessageList.MessageItem;
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
    ImageView current_trouble_search_btn;
    ImageView past_trouble_search_btn;
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

/*
        current_trouble_search_btn = findViewById(R.id.current_trouble_search_btn);
        current_trouble_search_btn.setOnClickListener(view -> onCurrentTroubleSearchClick());
*/

/*
        past_trouble_search_btn = findViewById(R.id.past_trouble_search_btn);
        past_trouble_search_btn.setOnClickListener(view -> onPastTroubleSearchClick());
*/

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
            item = new PTroubleItem(Integer.getInteger(past_code[0]), past_code[1], past_code[2]);
            pTroubleItems.add(item);
        }
        pTroubleAdapter = new PTroubleAdapter(getContext(), pTroubleItems);
        past_trouble_recycle_view.setAdapter(pTroubleAdapter);


        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(5);
        progressBar.setVisibility(View.GONE);
    }

    private void setCurrentTroubleCodeList(String findCodes) {
        String[] codes = findCodes.split(",");
        CTroubleItem item;
        for (int i = 0; i < codes.length; i++) {
            item = new CTroubleItem(TroubleTable.max_cid, codes[i], "");
            cTroubleItems.add(item);

            String[][] fields = new String[][]{
                    {"code", codes[i]},
                    {"description", ""}
            };
            TroubleTable.insertTroubleTable(fields);
        }
        cTroubleAdapter = new CTroubleAdapter(getContext(), cTroubleItems, troubleCodeListListener);
        current_trouble_recycle_view.setAdapter(cTroubleAdapter);
    }

    private CTroubleAdapter.ItemClickListener troubleCodeListListener = new CTroubleAdapter.ItemClickListener() {
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
    private void onDiagnosisClick() {
        //MyUtils.btService.getFaultCodes();
        isTest = true;
        diagnosis_btn.setBackgroundResource(R.drawable.button4_prog);
        progressBar.setVisibility(View.VISIBLE);
        new GetTroubleCodesTask().execute("GetTroubleCodes");

        TroubleTable.getTroubleCodeTable();
    }
/*
    private void onCurrentTroubleSearchClick() {
        String codes = current_trouble_content.getText().toString();
        if (!codes.isEmpty()) {
            String url = "https://m.search.naver.com/search.naver?query=";
            url += codes;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }
    private void onPastTroubleSearchClick() {

    }
*/

    private void onPrevActivityClick() {
        onLRChangeLayount(DiagnosisActivity.this, MainActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    boolean isTest = true;
    int prog = 0;
    class GetTroubleCodesTask extends AsyncTask<String, Integer, Boolean> {
        @SuppressLint({"SetTextI18n", "WrongThread"})
        protected Boolean doInBackground(String... str) {
            String result = "";
            while (isTest) {
                try {
                    prog++;
                    if (prog == 1) {
                        new ObdResetCommand().run(MyUtils.btSocket.getInputStream(), MyUtils.btSocket.getOutputStream());
                    } else if (prog == 2) {
                        new EchoOffCommand().run(MyUtils.btSocket.getInputStream(), MyUtils.btSocket.getOutputStream());
                    } else if (prog == 3) {
                        new LineFeedOffCommand().run(MyUtils.btSocket.getInputStream(), MyUtils.btSocket.getOutputStream());
                    } else if (prog == 4) {
                        new SelectProtocolCommand(ObdProtocols.AUTO).run(MyUtils.btSocket.getInputStream(), MyUtils.btSocket.getOutputStream());
                    } else if (prog == 5) {
                        ModifiedTroubleCodesObdCommand tcoc = new ModifiedTroubleCodesObdCommand();
                        tcoc.run(MyUtils.btSocket.getInputStream(), MyUtils.btSocket.getOutputStream());
                        result = tcoc.getFormattedResult();
                        isTest = false;
                    }
                    progressBar.setProgress(prog);
                    SystemClock.sleep(1000);
                } catch (Exception e) {
                    isTest = false;
                    e.printStackTrace();
                }
            }

            if (result.contains("P")) {
                String codes = "";
                String[] res_codes = result.split("\n");
                ArrayList<String> arrayList = new ArrayList<>();
                int i = 0;
                for(String item : res_codes){
                    if(!arrayList.contains(item) && !item.isEmpty()) {
                        arrayList.add(item);
                        if (i == 0)
                            codes = item;
                        else
                            codes += ", " + item;
                        i++;
                    }
                }
                dlg_text = codes;
                setCurrentTroubleCodeList(codes);
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

    public class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            // remove unwanted response from output since this results in erroneous error codes
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }

    @SuppressLint("ResourceType")
    private void showDialog() {
        dialog = new Dialog(DiagnosisActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dlg_normal);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
}