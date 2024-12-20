public class ConsolePrinter
{
    protected static boolean verbosePrintMode = false;
    protected static boolean quietPrintMode = false;
    protected static boolean releasePrintMode = false;

    public static synchronized void printOutputLn(String msg)
    {
        ConsolePrinter.printOutputLn(msg, System.out);
    }

    public static synchronized void printOutputLn(String msg, java.io.PrintStream out)
    {
        if (!ConsolePrinter.quietPrintMode) out.println(msg);
    }

    public static synchronized void printDebugInfoLn(String msg)
    {
        if (!ConsolePrinter.releasePrintMode && ConsolePrinter.verbosePrintMode) ConsolePrinter.printOutputLn("[\033[38;5;208mDBUG\033[0m] " + msg);
    }

    public static synchronized void printInfoLn(String msg)
    {
        if (!ConsolePrinter.releasePrintMode) ConsolePrinter.printOutputLn("[\033[38;5;14mINFO\033[0m] " + msg);
    }

    public static synchronized void printMsgLn(String msg)
    {
        if (!ConsolePrinter.releasePrintMode) ConsolePrinter.printOutputLn("[\033[38;5;81mMSSG\033[0m] " + msg);
    }

    public static synchronized void printWarningLn(String msg)
    {
        if (!ConsolePrinter.releasePrintMode) ConsolePrinter.printOutputLn("[\033[38;5;226mWARN\033[0m] " + msg, System.err);
    }

    public static synchronized void printErrLn(String msg)
    {
        if (!ConsolePrinter.releasePrintMode) ConsolePrinter.printOutputLn("[\033[38;5;160mERR\033[0m] " + msg, System.err);
    }

    public static synchronized void printErrLn(String msg, Throwable e)
    {
        if (ConsolePrinter.quietPrintMode) return;
        System.err.println("[\033[38;5;160mERR\033[0m] " + msg);
        e.printStackTrace();
    }

    public static synchronized void printCriticalErrLn(String msg)
    {
        if (!ConsolePrinter.releasePrintMode) ConsolePrinter.printOutputLn("[\033[38;5;196mCERR\033[0m] " + msg, System.err);
    }

    public static synchronized void printCriticalErrLn(String msg, Throwable e)
    {
        if (!ConsolePrinter.quietPrintMode) return;
        System.err.println("[\033[38;5;196mCERR\033[0m] " + msg);
        e.printStackTrace();
    }

    public static synchronized boolean getQuietPrintMode()
    {
        return ConsolePrinter.quietPrintMode;
    }

    public static synchronized void setQuietPrintMode(boolean value)
    {
        ConsolePrinter.quietPrintMode = value;
    }

    public static synchronized boolean getVerbosePrintMode()
    {
        return ConsolePrinter.verbosePrintMode;
    }

    public static synchronized void setVerbosePrintMode(boolean value)
    {
        ConsolePrinter.verbosePrintMode = value;
    }

    public static synchronized boolean getReleasePrintMode()
    {
        return ConsolePrinter.releasePrintMode;
    }

    public static synchronized void setReleasePrintMode(boolean value)
    {
        ConsolePrinter.releasePrintMode = value;
    }
}