public class App implements IConsolePrinter
{
    public static void main(String[] args)
    {
        final java.util.Scanner hiScanner = new java.util.Scanner(System.in);
        int port;

        try
        {
            port = Integer.parseInt(args[0]);
        }
        catch (Exception e)
        {
            System.out.print("No arguments passed or the arguments weren't appropriate !\nWhat port to run on?\n\033[38;5;46m>\033[0m ");
            port = hiScanner.nextInt();
        }

        ConsolePrinter.setVerbosePrintMode(true);

        new App(port);

        hiScanner.close();
    }

    public App(int port)
    {
        final StatisticsReporter reporter = new StatisticsReporter();

        try (NetBridge bridge = new NetBridge(port, reporter))
        {
            final ALU alu = new ALU(reporter);
            final Thread currentThread = Thread.currentThread();

            while (!currentThread.isInterrupted())
            {
                final NetBridge.ClientHandler clientHandler = bridge.nextUnhandledConnection();

                if (clientHandler != null) clientHandler.setOnMessageReceived((command, handler) -> handler.message = alu.execute(command));
                else try { Thread.sleep(0, 50); } catch (Exception e) { }

                if (System.currentTimeMillis() - reporter.timeOfLastCheck > 10000)
                {
                    final StringBuilder consoleOutputBuilder = new StringBuilder();

                    consoleOutputBuilder.append("[\033[38;5;99mStatisticsReport\033[0m]\nStats since launch ↓");
                    reporter.getStatsSinceLaunch().stream().forEachOrdered(e -> consoleOutputBuilder.append('\n').append(" · ").append(e.getKey()).append(" : ").append(e.getValue()));
                    consoleOutputBuilder.append('\n').append("Stats gathered throughout last 10 seconds ↓");
                    reporter.getStatsSinceLastCheck().stream().forEachOrdered(e -> consoleOutputBuilder.append('\n').append(" · ").append(e.getKey()).append(" : ").append(e.getValue()));

                    this.printInfoLn(consoleOutputBuilder.toString());
                }
            }
        }
        catch (Exception e) { this.printErrLn("Sth went wrong !!!", e); }
    }

    @Override
    public String getConsolePrinterPrefix()
    {
        return "[\033[38;5;154mApp\033[0m]";
    }
}
