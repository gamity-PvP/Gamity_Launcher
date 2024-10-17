package fr.gamity.launcher.thomas260913;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Launcher.getInstance().showErrorDialog(new Exception(e));
        Launcher.getInstance().getLogger().printStackTrace(e);
    }
}