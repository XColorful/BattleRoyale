package xiao.battleroyale.api.config.common.server.profile;

public interface IConfigProfileEntry extends IProfileEntry {

    int applyConfigProfile();
    @Override default int applyProfile() {
        return applyConfigProfile();
    }
}
