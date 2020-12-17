package gameClient;

import gameClient.strategy.StrategyType;
import gameClient.visual.VisualLayer;

import javax.swing.*;
import java.awt.*;

public class GameInstance implements Runnable {
    public static int INVALID_SCENARIO = -1;

    private int loginId;

    private VisualLayer visualLayer;
    private int currentScenario;
    private StrategyType currentStrategy;

    private GameEngine runningEngine;
    private Thread runningEngineThread;


    /**
     * Main Game controller
     */
    GameInstance() {
        this.loginId = 999;
        this.currentScenario = INVALID_SCENARIO;
        this.runningEngine = null;
        this.runningEngineThread = null;
        this.currentStrategy = StrategyType.HEAT_MAP;
    }

    GameInstance(int loginId, int currentScenario, StrategyType strategyType) {
        this();
        this.loginId = loginId;

        // We allow all scenarios since boaz said they will use different scenarios for testing.
        this.currentScenario = currentScenario;
        this.currentStrategy = strategyType;
    }

    public int getLoginId() {
        return loginId;
    }

    public int getCurrentScenario() {
        return currentScenario;
    }

    /**
     * Setup the visual layer if it doesn't exist.
     */
    void setupVisualLayer() {
        if (this.visualLayer == null) {
            this.visualLayer = new VisualLayer("EX2", this);
            GridLayout layout = new GridLayout(2, 1);
            this.visualLayer.setLayout(layout);
            this.visualLayer.setSize(1000, 700);
            this.visualLayer.init();
            this.visualLayer.setVisible(true);
            this.visualLayer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
    }

    /**
     * Interrupts engines.
     */
    public void stopEngine() {
        if (this.runningEngineThread != null) {
            this.runningEngineThread.interrupt();
        }
    }

    void startEngine(int scenario) {
        this.runningEngine = new GameEngine(this.loginId, scenario, this.currentStrategy);
        this.runningEngineThread = new Thread(this.runningEngine);
        this.runningEngineThread.setDaemon(true);
        this.runningEngineThread.start();
    }

    /**
     * @param newScenario New scenario ID.
     * @param restart Should resrtart engine after the change.
     */
    public void changeScenario(int newScenario, boolean restart) {
        this.currentScenario = newScenario;
        if (restart) {
            this.restart();
        }

    }

    public void changeStrategy(StrategyType s) {
        this.currentStrategy = s;
    }

    private void restart() {
        this.stopEngine();
        this.startEngine(this.currentScenario);
        if (this.visualLayer != null) {
            this.visualLayer.init();
        }
    }

    public StrategyType getCurrentStrategy() {
        return currentStrategy;
    }

    public GameEngine getRunningEngine() {
        return runningEngine;
    }

    @Override
    public void run() {
        this.setupVisualLayer();

        while (true) {
            try {
                this.visualLayer.repaint();
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }

    }
}
