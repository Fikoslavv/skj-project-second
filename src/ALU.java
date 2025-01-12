public class ALU implements IConsolePrinter
{
    protected static java.util.Map<String, java.util.function.BiFunction<Integer, Integer, Integer>> operationsMap = null;

    protected StatisticsReporter statisticsReporter = null;

    public ALU()
    {
        super();

        if (ALU.operationsMap == null)
        {
            ALU.operationsMap = new java.util.HashMap<>();
            ALU.operationsMap.put("ADD", (left, right) -> left + right);
            ALU.operationsMap.put("SUB", (left, right) -> left - right);
            ALU.operationsMap.put("MUL", (left, right) -> left * right);
            ALU.operationsMap.put("DIV", (left, right) -> left / right);
        }
    }

    public ALU(StatisticsReporter statisticsReporter)
    {
        this();

        this.statisticsReporter = statisticsReporter;
    }

    public String execute(String command)
    {
        this.printDebugInfoLn("Executing command[" + command + "] !");

        String output = "ERROR";
        
        try
        {
            String[] bits = command.split(" ");

            output = ALU.operationsMap.get(bits[0]).apply(Integer.parseInt(bits[1]), Integer.parseInt(bits[2])).toString();
        }
        catch (java.util.regex.PatternSyntaxException e) { this.printCriticalErrLn("Cannot split command[" + command + "] because the split regex is invalid !", e); }
        catch (Exception e) { }

        this.printInfoLn("The result of command[" + command + "] is [" + output + "] !");

        if (this.statisticsReporter != null)
        {
            this.statisticsReporter.report("ALU_COMMAND_" + command + ' ' + output);
            if (output.equals("ERROR")) this.statisticsReporter.report("ALU_COMMAND_ERRORS " + ' ' + output);
        }

        return output;
    }

    @Override
    public String getConsolePrinterPrefix()
    {
        return "[\033[38;5;52mALU\033[0m]";
    }
}
