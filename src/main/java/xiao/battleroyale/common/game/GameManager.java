package xiao.battleroyale.common.game;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GameManager {
    private UUID gameId;
    private boolean inGame = false;
    private static GameManager instance;

    private GameManager() {
        generateGameId();
    }

    public static void init() {
        if (instance == null) {
            instance = new GameManager();
        }
    }

    @NotNull
    public static GameManager get() {
        if (instance == null) {
            GameManager.init();
        }
        return instance;
    }

    @NotNull
    public UUID getGameId() {
        if (inGame) {
            return this.gameId;
        } else {
            return UUID.randomUUID();
        }
    }

    private void generateGameId() {
        setGameId(UUID.randomUUID());
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public void startGame() {
        this.inGame = true;
    }

    public void stopGame() {
        this.inGame = false;
    }
}
