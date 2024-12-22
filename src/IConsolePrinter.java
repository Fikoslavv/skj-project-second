public interface IConsolePrinter
{
    String getProgramPrinterPrefix();

    default void printOutputLn(String msg)
    {
        ConsolePrinter.printOutputLn(this.getProgramPrinterPrefix() + ' ' + msg);
    }

    default void printDebugInfoLn(String msg)
    {
        ConsolePrinter.printDebugInfoLn(this.getProgramPrinterPrefix() + ' ' + msg);
    }

    default void printInfoLn(String msg)
    {
        ConsolePrinter.printInfoLn(this.getProgramPrinterPrefix() + ' ' + msg);
    }

    default void printMsgLn(String msg)
    {
        ConsolePrinter.printMsgLn(this.getProgramPrinterPrefix() + ' ' + msg);
    }

    default void printWarningLn(String msg)
    {
        ConsolePrinter.printWarningLn(this.getProgramPrinterPrefix() + ' ' + msg);
    }

    default void printErrLn(String msg)
    {
        ConsolePrinter.printErrLn(this.getProgramPrinterPrefix() + ' ' + msg);
    }

    default void printErrLn(String msg, Throwable e)
    {
        ConsolePrinter.printErrLn(this.getProgramPrinterPrefix() + ' ' + msg, e);
    }

    default void printCriticalErrLn(String msg)
    {
        ConsolePrinter.printCriticalErrLn(this.getProgramPrinterPrefix() + ' ' + msg);
    }

    default void printCriticalErrLn(String msg, Throwable e)
    {
        ConsolePrinter.printCriticalErrLn(this.getProgramPrinterPrefix() + ' ' + msg, e);
    }
}