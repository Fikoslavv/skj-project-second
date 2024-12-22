public interface IConsolePrinter
{
    String getConsolePrinterPrefix();

    default void printOutputLn(String msg)
    {
        ConsolePrinter.printOutputLn(this.getConsolePrinterPrefix() + ' ' + msg);
    }

    default void printDebugInfoLn(String msg)
    {
        ConsolePrinter.printDebugInfoLn(this.getConsolePrinterPrefix() + ' ' + msg);
    }

    default void printInfoLn(String msg)
    {
        ConsolePrinter.printInfoLn(this.getConsolePrinterPrefix() + ' ' + msg);
    }

    default void printMsgLn(String msg)
    {
        ConsolePrinter.printMsgLn(this.getConsolePrinterPrefix() + ' ' + msg);
    }

    default void printWarningLn(String msg)
    {
        ConsolePrinter.printWarningLn(this.getConsolePrinterPrefix() + ' ' + msg);
    }

    default void printErrLn(String msg)
    {
        ConsolePrinter.printErrLn(this.getConsolePrinterPrefix() + ' ' + msg);
    }

    default void printErrLn(String msg, Throwable e)
    {
        ConsolePrinter.printErrLn(this.getConsolePrinterPrefix() + ' ' + msg, e);
    }

    default void printCriticalErrLn(String msg)
    {
        ConsolePrinter.printCriticalErrLn(this.getConsolePrinterPrefix() + ' ' + msg);
    }

    default void printCriticalErrLn(String msg, Throwable e)
    {
        ConsolePrinter.printCriticalErrLn(this.getConsolePrinterPrefix() + ' ' + msg, e);
    }
}