package gameClient;

import gameClient.strategy.StrategyType;

public class Ex2 implements Runnable {
    private int id;
    private int scenario;

    public Ex2(int id, int scenario) {
        this.id = id;
        this.scenario = scenario;
    }

    static void printUsage() {
        System.out.println("Usage: <name>.jar <id> [<scenarioNum>]");
    }

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                printUsage();
                return;
            }

            int id = Integer.parseInt(args[0]);
            int scenario = args.length > 1 ? Integer.parseInt(args[1]) : GameInstance.INVALID_SCENARIO;

            Thread client = new Thread(new Ex2(id, scenario));
            client.setDaemon(true);
            client.start();
            client.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (java.lang.NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        GameInstance gc = new GameInstance(this.id, this.scenario, StrategyType.HEAT_MAP);
        Thread t = new Thread(gc);
        t.setDaemon(true);
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
