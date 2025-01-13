package fr.gamity.launcher.thomas260913.utils;

import fr.theshark34.openlauncherlib.minecraft.AuthInfos;

public class MCAccount {
    private final AuthInfos authInfos;
    private final boolean crack;
    public MCAccount(AuthInfos authInfos, boolean crack){
        this.authInfos = authInfos;
        this.crack = crack;
    }

    public AuthInfos getAuthInfos() {
        return authInfos;
    }

    public boolean isCrack() {
        return crack;
    }
}
