public class ALU
{
    protected static java.util.Map<String, java.util.function.BiFunction<Long, Long, Long>> operationsMap = null;

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

    public String execute(String command)
    {
        String[] bits = command.split(" ");

        try
        {
            return ALU.operationsMap.get(bits[0]).apply(Long.parseLong(bits[1]), Long.parseLong(bits[2])).toString();
        }
        catch (Exception e) { return "ERROR"; }
    }
}
