package fr.gamity.launcher.thomas260913.utils;

import fr.theshark34.openlauncherlib.minecraft.AuthInfos;

public class MCAccount {
    private final AuthInfos authInfos;
    private final boolean crack;
    private final boolean alreadyLogin;
    public MCAccount(AuthInfos authInfos, boolean crack, boolean alreadyLogin){
        this.authInfos = authInfos;
        this.crack = crack;
        this.alreadyLogin = alreadyLogin;
    }

    public AuthInfos getAuthInfos() {
        return authInfos;
    }

    public boolean isCrack() {
        return crack;
    }

    public boolean isAlreadyLogin(){
        return alreadyLogin;
    }
}