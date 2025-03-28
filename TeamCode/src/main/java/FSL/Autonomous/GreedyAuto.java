package FSL.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import FSL.HardwareMaps.RobotHardware;

@Autonomous(name = "Greedy Auto", group = "FSL")
public class GreedyAuto extends LinearOpMode {

    RobotHardware robot = new RobotHardware(this);
    @Override
    public void runOpMode() {
        robot.init(true);
        waitForStart();

        new Thread(() -> robot.SetViperSlidePos(RobotHardware.TopRungEncoders)).start();
        robot.DriveByEncoderTicks(-563, -696, -728, -713, 0.3);
        HangingSequence();
        new Thread(() -> robot.SetViperSlidePos(RobotHardware.BottomEncoders)).start();

        Rotate180();
        HangingToBullCharge();
        BullChargeToPickup();
        robot.SetClawPos(true);
        PickToOutALittle();
        Rotate180();

        OutALittleToHang();
        robot.SetViperSlidePos(RobotHardware.TopRungEncoders);
        HangingSequence();

        Ending();
    }

    void HangingToBullCharge(){
        robot.DriveByEncoderTicks(841, -842, 931, -905, 0.3);
    }

    void BullChargeToPickup(){
        robot.DriveByEncoderTicks(-605, -705, -709, -713, 0.6);
    }

    void PickToOutALittle(){
        robot.DriveByEncoderTicks(208, 280, 252, 266, 0.25);
    }

    void OutALittleToHang(){
        robot.DriveByEncoderTicks(588, -1350, 597, -1369, 0.3);
    }

    void ForwardALittle(){
        robot.DriveByEncoderTicks(-57,-71,-91, -81, 0.2);
    }

    void Rotate180(){
        robot.DriveByEncoderTicks(1328, -1385, -1442, 1403, 0.35);
    }

    void Ending(){
        robot.DriveByEncoderTicks(-1131, 1725, -1139, 1671, 0.9);
    }

    void HangingSequence(){
        ForwardALittle();
        robot.SetViperSlidePos(RobotHardware.TopRungEncoders - 200);
        robot.SetClawPos(false);
    }
}
