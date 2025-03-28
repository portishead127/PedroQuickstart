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

        robot.SetViperSlidePos(RobotHardware.TopRungEncoders);
        sleep(500);
        robot.DriveByEncoderTicks(-550, -550, -550, -550, 0.2);
        HangingSequence();
        robot.SetViperSlidePos(RobotHardware.BottomEncoders);

        HangingToBullCharge();
        Rotate180();
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
        robot.DriveByEncoderTicks(1283, -1783, -1783, -1369, 0.3);
    }

    void BullChargeToPickup(){
        robot.DriveByEncoderTicks(-672, -675, -684, -687, 0.4);
    }

    void PickToOutALittle(){
        robot.DriveByEncoderTicks(272, 284, 284, 266, 0.25);
    }

    void OutALittleToHang(){
        robot.DriveByEncoderTicks(1072, -1844, -1783, -1369, 0.3);
    }

    void ForwardALittle(){
        robot.DriveByEncoderTicks(-57,-71,-91, -81, 0.25);
    }

    void Rotate180(){
        robot.DriveByEncoderTicks(1328, -1385, -1442, 1403, 0.2);
    }

    void Ending(){
        robot.DriveByEncoderTicks(-1131, 1725, -1139, 1671, 0.9);
    }

    void HangingSequence(){
        robot.SetViperSlidePos(RobotHardware.TopRungEncoders - 800);
        sleep(750);
        robot.SetClawPos(false);
    }
}
