package org.firstinspires.ftc.teamcode.opmode.auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.command.AutoRunner;
import org.firstinspires.ftc.teamcode.command.OptimizeStuff;
import org.firstinspires.ftc.teamcode.subsystem.Drive;
import org.firstinspires.ftc.teamcode.subsystem.Vision;
import org.firstinspires.ftc.teamcode.util.PoseStorage;

public abstract class RoadRunnerAuto extends LinearOpMode {
    public enum Position {
        LEFT, MID, RIGHT
    }

    protected Drive drive;
    protected Vision vision;
    private Position position = Position.MID;

    protected abstract boolean usingVision();

    public abstract void initialize();

    public abstract void onStart();

    public abstract void run();

    public Position getPosition() {
        return position;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        drive = new Drive(this);
        if (usingVision()) vision = new Vision(this);
        new OptimizeStuff(this);

        initialize();

        while (!isStarted() && !isStopRequested() && usingVision()) {
            vision.updateTagOfInterest();
            vision.printTagData();
            telemetry.update();
            if (vision.getTagOfInterest() == null) continue;
            switch (vision.getTagOfInterest().id) {
                case 1:
                    position = Position.LEFT;
                    break;
                case 2:
                    position = Position.RIGHT;
                    break;
                case 3:
                default:
                    position = Position.MID;
                    break;
            }
        }
        if (isStopRequested()) return;
        waitForStart();
        if (isStopRequested()) return;
        onStart();

        while (!isStopRequested()) {
            run();
        }
        PoseStorage.currentPose = drive.getPoseEstimate();
    }
}
