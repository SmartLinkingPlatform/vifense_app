package com.obd2.dgt.ui.MainListActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
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

import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.obd2.dgt.R;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

public class DiagnosisActivity extends AppBaseActivity {
    ImageView diag_prev_btn;
    ImageView diagnosis_btn;
    TextView diagnosis_btn_text;
    ImageView current_trouble_search_btn;
    ImageView past_trouble_search_btn;
    @SuppressLint("StaticFieldLeak")
    static TextView current_trouble_content;
    @SuppressLint("StaticFieldLeak")
    static TextView past_trouble_content;
    ProgressBar progressBar;
    Dialog dialog;
    String dlg_text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);
        MyUtils.currentActivity = this;
        initLayout();
    }

    private void initLayout() {
        diag_prev_btn = findViewById(R.id.diag_prev_btn);
        diag_prev_btn.setOnClickListener(view -> onPrevActivityClick());

        diagnosis_btn = findViewById(R.id.diagnosis_btn);
        diagnosis_btn.setOnClickListener(view -> onDiagnosisClick());

        diagnosis_btn_text = findViewById(R.id.diagnosis_btn_text);
        diagnosis_btn_text.setText(R.string.diagnosing_text);

        current_trouble_search_btn = findViewById(R.id.current_trouble_search_btn);
        current_trouble_search_btn.setOnClickListener(view -> onCurrentTroubleSearchClick());

        past_trouble_search_btn = findViewById(R.id.past_trouble_search_btn);
        past_trouble_search_btn.setOnClickListener(view -> onPastTroubleSearchClick());

        current_trouble_content = findViewById(R.id.current_trouble_content);
        current_trouble_content.setText("");
        past_trouble_content = findViewById(R.id.past_trouble_content);
        current_trouble_content.setText("");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(5);
        progressBar.setVisibility(View.GONE);
    }

    private void onDiagnosisClick() {
        //MyUtils.btService.getFaultCodes();
        isTest = true;
        diagnosis_btn.setBackgroundResource(R.drawable.button4_prog);
        progressBar.setVisibility(View.VISIBLE);
        new GetTroubleCodesTask().execute("GetTroubleCodes");
    }
    private void onCurrentTroubleSearchClick() {

    }
    private void onPastTroubleSearchClick() {

    }

    private void onPrevActivityClick() {
        onLRChangeLayount(DiagnosisActivity.this, MainActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        onLRChangeLayount(DiagnosisActivity.this, MainActivity.class);
        finish();
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
            if (result.isEmpty()) {
                dlg_text = getString(R.string.non_trouble_codes);
            } else {
                dlg_text = result;
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                //DB 저장
                //TableInfo.updateDeviceInfoTable(pairedDevice.getName(), pairedDevice.getAddress(), "1", "1");
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