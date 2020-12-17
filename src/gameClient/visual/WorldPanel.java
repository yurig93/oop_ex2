package gameClient.visual;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.GameInstance;
import gameClient.GameEngine;
import gameClient.models.Agent;
import gameClient.models.Pokemon;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Map;

/**
 * World frame container.
 */
public class WorldPanel extends JPanel {
    private static int PADDING_PX = 10;

    private final GameEngine gameEngine;
    private final GameInstance gameInstance;

    private gameClient.util.Range2Range world2FrameConverter;

    WorldPanel(GameInstance gameInstance) {
        this.gameEngine = gameInstance.getRunningEngine();
        this.gameInstance = gameInstance;
    }

    /**
     * Inits conversion tools between frame and world.
     */
    private void initWorld2Frame() {
        Range rx = new Range(PADDING_PX, this.getWidth() - PADDING_PX);
        Range ry = new Range(this.getHeight() - PADDING_PX, PADDING_PX);
        Range2D frame = new Range2D(rx, ry);
        directed_weighted_graph ggg = this.gameEngine.getGraph();
        world2FrameConverter = Range2Range.w2f(ggg, frame);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (this.gameInstance.getRunningEngine() != null) {
            this.initWorld2Frame();
            super.paintComponent(g);
            int w = this.getWidth();
            int h = this.getHeight();
            g.clearRect(0, 0, w, h);
            drawPokemons(g);
            drawGraph(g);
            drawAgents(g);
            drawBorders(g);
        }

    }

    private void drawBorders(Graphics g) {
        Map<String, Range2D> borders = this.gameEngine.getGameStrategy().getBorders();
        borders.values().forEach(r -> {
            double x0 = r.get_x_range().get_min();
            double y0 = r.get_y_range().get_min();

            double x1 = r.get_x_range().get_max();
            double y1 = r.get_y_range().get_max();

            geo_location x0y0Portion = world2FrameConverter.getWorld().getPortion(new Point3D(x0, y0));
            geo_location x1y10Portion = world2FrameConverter.getWorld().getPortion(new Point3D(x1, y1));
            geo_location x0y0Frame = world2FrameConverter.getFrame().fromPortion(x0y0Portion);
            geo_location x1y1Frame = world2FrameConverter.getFrame().fromPortion(x1y10Portion);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g.setColor(Color.red);

            g.fillRect((int) x0y0Frame.x(),
                    (int) x1y1Frame.y(),
                    (int) (x1y1Frame.x() - x0y0Frame.x()),
                    (int) (x0y0Frame.y() - x1y1Frame.y()));
        });
    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = this.gameEngine.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while (iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.blue);
            drawNode(n, 5, g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while (itr.hasNext()) {
                edge_data e = itr.next();
                g.setColor(Color.gray);
                drawEdge(e, g);
            }
        }
    }

    private void drawPokemons(Graphics g) {
        Map<String, Pokemon> pokemons = this.gameEngine.getPokemons();
        Iterator<Pokemon> itr = pokemons.values().iterator();

        while (itr.hasNext()) {
            Pokemon f = itr.next();
            Point3D c = f.getLocation();
            int r = 10;

            g.setColor(Color.green);
            if (f.isBeingHandled()) {
                g.setColor(Color.red);
            }
            if (c != null) {
                geo_location fp = this.world2FrameConverter.world2frame(c);
                g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
            }
        }
    }

    private void drawAgents(Graphics g) {
        Iterator<Agent> itr = this.gameEngine.getAgents().values().iterator();

        while (itr.hasNext()) {
            Agent a = itr.next();
            geo_location c = a.getPos();
            int r = 8;
            if (c != null) {
                geo_location fp = this.world2FrameConverter.world2frame(c);
                g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
                g.drawString(String.format("%d (%d)", a.getId(), (int) a.getValue()), (int) fp.x(), (int) fp.y() - r);
            }
        }
    }

    private void drawNode(node_data n, int r, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this.world2FrameConverter.world2frame(pos);
        g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
        g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - 4 * r);
    }

    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = this.gameEngine.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this.world2FrameConverter.world2frame(s);
        geo_location d0 = this.world2FrameConverter.world2frame(d);
        g.drawLine((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
    }
}
