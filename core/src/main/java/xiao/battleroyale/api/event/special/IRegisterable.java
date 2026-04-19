package xiao.battleroyale.api.event.special;

import xiao.battleroyale.util.StringUtils;

public interface IRegisterable {

    boolean isCorrectProtocol(StringUtils.ProtocolString protocol);

    StringUtils.ProtocolString getCorrectProtocol();

    String[] getProtocolSuggests();
}
