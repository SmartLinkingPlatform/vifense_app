package com.obd2.dgt.btManage;

import android.bluetooth.BluetoothSocket;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.control.IgnitionMonitorCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.*;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdRawCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.ResetTroubleCodesCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.NoDataException;
import com.github.pires.obd.exceptions.UnsupportedCommandException;
import com.obd2.dgt.utils.MyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;


public class OBD2ApiCommand {
    static BluetoothSocket obdSocket;

    public OBD2ApiCommand(BluetoothSocket obdSocket) {
        this.obdSocket = obdSocket;
    }

    public void getFuelConsumption() {
        try {
            // 연료 소모량과 연료 수준 명령 실행
            ConsumptionRateCommand consumptionRateCommand = new ConsumptionRateCommand();
            FuelLevelCommand fuelLevelCommand = new FuelLevelCommand();

            consumptionRateCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());
            fuelLevelCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());

            // 데이터 가져오기
            double consumptionRate = consumptionRateCommand.getLitersPerHour();
            //순간 연료 소모량이 0이면 "PID 0110 - 스로틀 위치" 와 "PID 010D - 연료 압력"으로 계산한다.
            if (consumptionRate == 0) {
                consumptionRate = Double.parseDouble(MyUtils.ecu_throttle_position) * Double.parseDouble(MyUtils.ecu_maf);
            }
            MyUtils.ecu_fuel_rate = String.valueOf(consumptionRate);

            double fuelLevel = fuelLevelCommand.getFuelLevel();
            if (fuelLevel > 0) {
                // 연료 소모량 계산 (차량의 연료 소모율은 시간당 소비되는 연료량이므로 주의)
                double fuelConsumption = consumptionRate / fuelLevel;
                MyUtils.ecu_fuel_consume = String.valueOf(Math.round(fuelConsumption * 10) / 10.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getConsumablesStatus() {
        try {
            // RPM 값 읽기
            //RPMCommand rpmCommand = new RPMCommand();
            //rpmCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());
            int rpm = Integer.parseInt(MyUtils.ecu_engine_rpm); //rpmCommand.getRPM();

            // 엔진 냉각수 온도 읽기
            EngineCoolantTemperatureCommand coolantCommand = new EngineCoolantTemperatureCommand();
            coolantCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());
            float coolantTemperature = coolantCommand.getTemperature();
            // 연료 압력 읽기
            FuelPressureCommand fuelPressureCommand = new FuelPressureCommand();
            fuelPressureCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());
            float fuelPressure = fuelPressureCommand.getMetricUnit();
            // 트러블 코드 읽기
            //ObdCommand command = new TroubleCodesCommand();
            //command.run(obdSocket.getInputStream(), obdSocket.getOutputStream());
            //String troubleCodes = command.getFormattedResult();
            TroubleCodesCommand troubleCodesCommand = new TroubleCodesCommand();
            String troubleCodes = troubleCodesCommand.getFormattedResult();

            // RPM이 높고 엔진 냉각수 온도가 정상이며 연료 압력이 낮은 경우
            if (!MyUtils.is_consume) {
                if (rpm > 3000 && coolantTemperature < 100 && fuelPressure < 200) {
                    MyUtils.ecu_consume_warning += "break";
                }
            }

            // 트러블 코드가 있는 경우
            if (!MyUtils.is_trouble) {
                if (!troubleCodes.isEmpty()) {
                    MyUtils.ecu_trouble_code += troubleCodes;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //차량의 사용 중 상태
    public String getIgnitionMonitorStatus() {
        String status = "off";
        try {
            EchoOffCommand echoOffCommand = new EchoOffCommand();
            echoOffCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());

            LineFeedOffCommand lineFeedOffCommand = new LineFeedOffCommand();
            lineFeedOffCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());

            TimeoutCommand timeoutCommand = new TimeoutCommand(125);
            timeoutCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());

            SelectProtocolCommand selectProtocolCommand = new SelectProtocolCommand(ObdProtocols.AUTO);
            selectProtocolCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());

            IgnitionMonitorCommand ignitionCommand = new IgnitionMonitorCommand();
            ignitionCommand.run(obdSocket.getInputStream(), obdSocket.getOutputStream());
            ignitionCommand.isIgnitionOn();
            status = ignitionCommand.getFormattedResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

}
