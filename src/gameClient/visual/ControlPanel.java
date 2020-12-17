package gameClient.visual;

import gameClient.EngineStatus;
import gameClient.GameEngine;
import gameClient.GameInstance;
import gameClient.strategy.StrategyFactory;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * Control panel for visuals.
 */
public class ControlPanel extends JPanel {
    private static int REPAINT_INTEVAL_MS = 100;
    private final GameInstance gameInstance;
    private final JFormattedTextField scenarioSelector;
    private final JComboBox strategySelector;
    private final JLabel timeLeft;
    private final JLabel score;
    private final JTextArea error;
    private final JButton go;
    private final JButton stop;
    private EngineStatus lastStatus;
    private long lastUpdate;

    public ControlPanel(GameInstance c) {
        this.lastStatus = null;
        this.lastUpdate = System.currentTimeMillis();
        this.gameInstance = c;
        this.setLayout(new GridLayout(7, 2));

        this.go = new JButton("GO");
        this.stop = new JButton("STOP");

        this.error = new JTextArea();
        this.error.setLineWrap(true);
        this.error.setBackground(new Color(0, 0, 0, 0));

        this.scenarioSelector = new JFormattedTextField(getNumberOnlyFormatter());
        this.scenarioSelector.setValue(this.gameInstance.getCurrentScenario());

        this.add(new JLabel("Scenario: (Press enter to start)"));
        this.add(this.scenarioSelector);

        this.add(new JLabel("Strategy:"));
        this.strategySelector = new JComboBox<>(new Vector<>(StrategyFactory.getNames()));
        this.add(strategySelector);

        this.add(new JLabel("Time Left:"));
        this.timeLeft = new JLabel("");
        this.add(timeLeft);

        this.add(new JLabel("Score:"));
        this.score = new JLabel("0");
        this.add(score);

        this.add(new JLabel("Login"));
        this.add(new JLabel(Integer.toString(this.gameInstance.getLoginId())));

        this.add(this.go);
        this.add(this.stop);

        this.add(this.error);

    }

    /**
     * @return A formatter to attach to a JComponent.
     */
    public static NumberFormat getNumberOnlyFormatter() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        return format;
    }

    /**
     * @param millis seconds in milliseconds.
     * @return A Human readable time string.
     */
    private static String millisToHuman(long millis) {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public JTextArea getError() {
        return error;
    }

    public JButton getGo() {
        return go;
    }

    public JButton getStop() {
        return stop;
    }

    public JComboBox getStrategySelector() {
        return strategySelector;
    }

    @Override
    protected void paintComponent(Graphics g) {
        GameEngine e = this.gameInstance.getRunningEngine();
        this.strategySelector.setSelectedItem(this.gameInstance.getCurrentStrategy().toString());

        // Manipulate control availability during different phases.
        if (e != null &&
                ((this.lastStatus != null && this.lastStatus != e.getStatus())
                        || (System.currentTimeMillis() - this.lastUpdate > REPAINT_INTEVAL_MS))) {
            this.lastStatus = e.getStatus();
            this.lastUpdate = System.currentTimeMillis();
            if (e != null && e.getStatus() == EngineStatus.STOPPING) {
                this.go.setEnabled(false);
                this.stop.setEnabled(false);
            } else if (e.getStatus() == EngineStatus.RUNNING) {
                this.go.setEnabled(false);
                this.stop.setEnabled(true);
                this.scenarioSelector.setEnabled(false);
                this.strategySelector.setEnabled(false);
                this.timeLeft.setText(millisToHuman(this.gameInstance.getRunningEngine().getGameService().timeToEnd()));
                this.score.setText(Integer.toString(this.gameInstance.getRunningEngine().getScore()));
            } else {
                this.scenarioSelector.setEnabled(true);
                this.strategySelector.setEnabled(true);
                this.go.setEnabled(true);
                this.stop.setEnabled(false);
            }
        }

        super.paintComponent(g);
    }

    public JFormattedTextField getScenarioSelector() {
        return scenarioSelector;
    }
}
