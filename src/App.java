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
        super();

        try (NetBridge bridge = new NetBridge(port))
        {
            ALU alu = new ALU();
            Thread currentThread = Thread.currentThread();

            while (!currentThread.isInterrupted())
            {
                NetBridge.ClientHandler clientHandler = bridge.nextUnhandledConnection();

                if (clientHandler != null) clientHandler.setOnMessageReceived((command, handler) -> handler.message = alu.execute(command));
                else try { Thread.sleep(0, 50); } catch (Exception e) { }
            }
        }
        catch (Exception e) { this.printErrLn("Sth went wrong !!!", e); }
    }

    @Override
    public String getProgramPrinterPrefix()
    {
        return "[\033[38;5;154mApp\033[0m]";
    }
}
