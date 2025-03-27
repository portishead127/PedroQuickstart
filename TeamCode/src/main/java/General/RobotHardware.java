package General;

import android.content.Context;
import android.media.SoundPool;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.R;

import java.net.ContentHandler;

enum ViperSlideDirections { UPWARDS, DOWNWARDS, NONE }
enum IntakeMotorStates{ IN, OUT, NONE }

public class RobotHardware {
    private final LinearOpMode myOpMode;   // gain access to methods in the calling OpMode.
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;
    private DcMotor intakeMotor = null;
    private Servo rightSlideMotor = null;
    private Servo leftSlideMotor = null;
    private Servo rightFlipMotor = null;
    private Servo leftFlipMotor = null;
    private DcMotor leftViperSlide = null;
    private DcMotor rightViperSlide = null;
    private Servo viperSlideClaw = null;
    public static final int ViperSlideMotorEncoderResolution = 752;
    public static final double CircumferenceOfWheelInMeters = 0.2356;
    public static final double WheelMotorEncoderResolution = 336;
    public static final double WheelbaseInInches = 0.388;
    public static final int TopRungRevsRight = 3230;
    public static final int BottomEncoders = 20;
    public SoundPool sounds;
    // Define a constructor that allows the OpMode to pass a reference to itself.
    public RobotHardware(LinearOpMode opmode)
    {
        myOpMode = opmode;
    }

    /**
     * Initialize all the robot's hardware.
     * This method must be called ONCE when the OpMode is initialized.
     * <p>
     * All of the hardware devices are accessed via the hardware map, and initialized.
     */
    public void init(boolean resetEncoders) {
        // Define and Initialize Motors
        frontLeft  = myOpMode.hardwareMap.get(DcMotor.class, "front_left");
        frontRight = myOpMode.hardwareMap.get(DcMotor.class, "front_right");
        backLeft = myOpMode.hardwareMap.get(DcMotor.class, "back_left");
        backRight = myOpMode.hardwareMap.get(DcMotor.class, "back_right");
        intakeMotor = myOpMode.hardwareMap.get(DcMotor.class, "intake");
        rightSlideMotor = myOpMode.hardwareMap.get(Servo.class, "rSlide");
        leftSlideMotor = myOpMode.hardwareMap.get(Servo.class, "lSlide");
        rightFlipMotor = myOpMode.hardwareMap.get(Servo.class, "rFlip");
        leftFlipMotor = myOpMode.hardwareMap.get(Servo.class, "lFlip");
        leftViperSlide = myOpMode.hardwareMap.get(DcMotor.class, "lvs");
        rightViperSlide = myOpMode.hardwareMap.get(DcMotor.class, "rvs");
        viperSlideClaw = myOpMode.hardwareMap.get(Servo.class, "vsclaw");

        rightSlideMotor.scaleRange(0.5, 0.75);
        leftSlideMotor.scaleRange(0.25, 0.5);

        leftViperSlide.setDirection(DcMotorSimple.Direction.FORWARD);
        rightViperSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        if (resetEncoders) {
            SetDriveChainMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            SetDriveChainMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
            SetViperSlideModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            SetViperSlideModes(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        else{
            SetDriveChainMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            SetViperSlideModes(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftViperSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightViperSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        SetClawPos(true);
        IntakeSystem(false, false);
        myOpMode.telemetry.addData(">", "Hardware Initialized");
    }

    public void SetViperSlideModes(DcMotor.RunMode runMode){
        leftViperSlide.setMode(runMode);
        rightViperSlide.setMode(runMode);
    }
    public void SetDriveChainMotorMode(DcMotor.RunMode runMode){
        frontLeft.setMode(runMode);
        backLeft.setMode(runMode);
        frontRight.setMode(runMode);
        backRight.setMode(runMode);
    }

    public void DriveChain(double slowModeMult, double y, double x, double rx){
        //AP: Don't even ask me how this works, I'm not a vectors wizard.... gm0.com
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;


        frontLeftPower *= slowModeMult;
        backLeftPower *= slowModeMult;
        frontRightPower *= slowModeMult;//bad
        backRightPower *= slowModeMult;


        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);
        myOpMode.telemetry.update();
    }
    public void DriveByEncoderTicks(int flTicks, int frTicks, int brTicks, int blTicks, double speed) {
        // Set target positions
        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + flTicks);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + blTicks);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() + frTicks);
        backRight.setTargetPosition(backRight.getCurrentPosition() + brTicks);

        // Set mode to RUN_TO_POSITION
        SetDriveChainMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set power (ensure all wheels move at the same rate)
        frontLeft.setPower(speed);
        backLeft.setPower(speed);
        frontRight.setPower(speed);
        backRight.setPower(speed);

        // Wait until all motors reach their target
        while (myOpMode.opModeIsActive() &&
                (frontLeft.isBusy() || backLeft.isBusy() || frontRight.isBusy() || backRight.isBusy())) {
            myOpMode.telemetry.addData("FL Target", frontLeft.getTargetPosition());
            myOpMode.telemetry.addData("BL Target", backLeft.getTargetPosition());
            myOpMode.telemetry.addData("FR Target", frontRight.getTargetPosition());
            myOpMode.telemetry.addData("BR Target", backRight.getTargetPosition());
            myOpMode.telemetry.update();
        }

        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
    }
    public void SetClawPos(boolean clawClosed){
        if(clawClosed){
            viperSlideClaw.setPosition(0.525);
        }
        else{
            viperSlideClaw.setPosition(0.35);
        }
    }
    public void SetViperSlideMovement(double slowModeMult, ViperSlideDirections viperSlideMovement){
        SetViperSlideModes(DcMotor.RunMode.RUN_USING_ENCODER);
        switch(viperSlideMovement) {
            case UPWARDS:
                if(rightViperSlide.getCurrentPosition() < TopRungRevsRight + 500) { //SOFTWARE STOP
                    leftViperSlide.setDirection(DcMotorSimple.Direction.FORWARD);
                    rightViperSlide.setDirection(DcMotorSimple.Direction.REVERSE);

                    leftViperSlide.setPower(1);
                    rightViperSlide.setPower(1);
                }
                else{
                    leftViperSlide.setPower(0);
                    rightViperSlide.setPower(0);
                }
                break;
            case DOWNWARDS:
                leftViperSlide.setDirection(DcMotorSimple.Direction.REVERSE);
                rightViperSlide.setDirection(DcMotorSimple.Direction.FORWARD);

                leftViperSlide.setPower(1);
                rightViperSlide.setPower(1);
                break;
            case NONE:
                leftViperSlide.setDirection(DcMotorSimple.Direction.FORWARD);
                rightViperSlide.setDirection(DcMotorSimple.Direction.REVERSE);
                leftViperSlide.setPower(0.0);
                rightViperSlide.setPower(0.0);
                break;
        }
        myOpMode.telemetry.update();
    }
    public void SetIntakeMotorMovement(IntakeMotorStates intakeMotorMovement){
        switch(intakeMotorMovement){
            case IN:
                intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
                intakeMotor.setPower(0.3);
                break;
            case OUT:
                intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
                intakeMotor.setPower(0.4);
                break;
            case NONE:
                intakeMotor.setPower(0);
                break;
        }
    }
    public void SetDrawerSlidePos(boolean slideOut){ //RIGHT SLIDE MAXXED -> MAX EXTENSION
        if(slideOut){
            rightSlideMotor.setPosition(1);
            leftSlideMotor.setPosition(0);
        }else{
            rightSlideMotor.setPosition(0);
            leftSlideMotor.setPosition(1);
        }
    }
    public void SetFlipMotorPos(boolean flipMotorOut){
        if(flipMotorOut){
            rightFlipMotor.setPosition(0);
            leftFlipMotor.setPosition(1);
        }
        else{
            rightFlipMotor.setPosition(0.72);
            leftFlipMotor.setPosition(0.28);
        }
    }

    public void SetFinalFlipMotorPos(){
        rightFlipMotor.setPosition(0.9);
        leftFlipMotor.setPosition(0.1);
    }
    public void SetViperSlidePos(int encoderCounts){
        rightViperSlide.setTargetPosition(encoderCounts);
        leftViperSlide.setTargetPosition(encoderCounts);
        SetViperSlideModes(DcMotor.RunMode.RUN_TO_POSITION);

        rightViperSlide.setPower(1.0);
        leftViperSlide.setPower(1.0);
    }
    public void IntakeSystem(boolean slideOut, boolean flipMotorOut) {
        SetFlipMotorPos(flipMotorOut);
        SetDrawerSlidePos(slideOut);
    }

    public void FinalFold() throws InterruptedException {
        SetFinalFlipMotorPos();
        Thread.sleep(500);
        SetDrawerSlidePos(false);
    }
}
