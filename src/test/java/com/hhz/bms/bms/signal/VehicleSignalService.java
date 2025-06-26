package com.hhz.bms.bms.signal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VehicleSignalService {

    private static Random rand = new Random();

    // 根据电池类型生成信号数据
    public VehicleSignal generateSignalData(int carId, String batteryType) {
        double Mx, Mi, Ix, Ii;

        if ("三元电池".equals(batteryType)) {
            Mx = 0.5 + (10.0 - 0.5) * rand.nextDouble();  // Mx 范围 0.5 到 10.0
            Mi = 0.5 + (Mx - 0.5) * rand.nextDouble();   // Mi 范围 0.5 到 Mx
            Ix = 1.0 + (6.0 - 1.0) * rand.nextDouble();   // Ix 范围 1.0 到 6.0
            Ii = 1.0 + (Ix - 1.0) * rand.nextDouble();    // Ii 范围 1.0 到 Ix
        } else {  // 铁锂电池
            Mx = 0.5 + (6.0 - 0.5) * rand.nextDouble();  // Mx 范围 0.5 到 6.0
            Mi = 0.5 + (Mx - 0.5) * rand.nextDouble();   // Mi 范围 0.5 到 Mx
            Ix = 1.0 + (4.5 - 1.0) * rand.nextDouble();   // Ix 范围 1.0 到 4.5
            Ii = 1.0 + (Ix - 1.0) * rand.nextDouble();    // Ii 范围 1.0 到 Ix
        }

        // 创建信号数据对象
        Signal signal = new Signal(Mx, Mi, Ix, Ii);

        // 创建警告ID，部分数据没有警告ID
        Integer warnId = null;

        return new VehicleSignal(carId, warnId, signal);
    }

    // 生成车牌号为 10001 到 10006 的信号数据，生成 1000 条数据
    public List<VehicleSignal> generateVehicleSignals() {
        List<VehicleSignal> vehicleData = new ArrayList<>();

        // 生成 1000 条数据，每个车牌号重复一定次数
        for (int i = 0; i < 1000; i++) {
            int carId = 10001 + (i % 6);  // 车牌号范围为 10001 到 10006
            String batteryType = (carId % 2 == 0) ? "三元电池" : "铁锂电池";  // 交替设置电池类型
            vehicleData.add(generateSignalData(carId, batteryType));
        }

        return vehicleData;
    }

    public static void main(String[] args) {
        VehicleSignalService service = new VehicleSignalService();
        List<VehicleSignal> vehicleSignals = service.generateVehicleSignals();

        // 输出为 JSON 数组格式
        System.out.println("[");
        for (int i = 0; i < vehicleSignals.size(); i++) {
            System.out.print(vehicleSignals.get(i));  // 输出每条记录
            if (i < vehicleSignals.size() - 1) {
                System.out.println(",");  // 如果不是最后一条，输出逗号
            }
        }
        System.out.println("\n]");  // 最后输出 `]`
    }
}