package gameClient.visual;

import gameClient.GameInstance;
import gameClient.strategy.StrategyType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Main frame for visuals.
 */
public class VisualLayer extends JFrame {
    public GameInstance gameInstance;
    private WorldPanel p;
    private ControlPanel c;

    public VisualLayer(String title, GameInstance gameInstance) {
        super(title);
        this.gameInstance = gameInstance;
    }

    /**
     * Sets event handlers on visual objects.
     */
    private void setEventListeners() {

        this.c.getGo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    gameInstance.changeScenario(Integer.parseInt(c.getScenarioSelector().getText()), true);
                } catch (Exception ex) {
                    c.getError().setText(ex.toString());
                }
            }
        });

        this.c.getStop().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    gameInstance.stopEngine();
                    c.getStop().setEnabled(false);
                } catch (Exception ex) {
                    c.getError().setText(ex.toString());
                }
            }
        });

        this.c.getStrategySelector().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StrategyType strategy = StrategyType.valueOf((String) c.getStrategySelector().getSelectedItem());
                gameInstance.changeStrategy(strategy);
            }
        });

    }

    public void init() {
        this.getContentPane().removeAll();
        this.p = new WorldPanel(gameInstance);
        this.c = new ControlPanel(gameInstance);
        this.c.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.p.setBorder(BorderFactory.createEmptyBorder(100, 100, 10, 10));
        this.setEventListeners();

        this.add(this.c);
        this.add(this.p);
        this.revalidate();
        this.repaint();

    }
}