package fr.gamity.updater.thomas260913;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Updater.getInstance().showErrorDialog(new Exception(e));
        Updater.getInstance().getLogger().printStackTrace(e);
    }
}