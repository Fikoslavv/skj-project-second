public class StatisticsReporter
{
    public static final String STATISTIC_NEW_CLIENT_CONNECTIONS = "NEW_CLIENT_CONNECTIONS";
    public static final String STATISTIC_ALU_OPERATIONS_COUNT = "ALU_OPERATIONS_COUNT";
    public static final String STATISTIC_ALU_COMMANDS_SUM = "SUM_OF_ALU_COMMANDS";
    protected static final String[] STATISTICS_DEFAULT_KEYS = { "ALU_COMMAND_ADD", "ALU_COMMAND_SUB", "ALU_COMMAND_MUL", "ALU_COMMAND_DIV", StatisticsReporter.STATISTIC_ALU_COMMANDS_SUM, StatisticsReporter.STATISTIC_ALU_OPERATIONS_COUNT, StatisticsReporter.STATISTIC_NEW_CLIENT_CONNECTIONS };

    protected java.util.Map<String, Long> statsSinceLaunch = new java.util.concurrent.ConcurrentHashMap<>();
    protected java.util.Map<String, Long> statsSinceLastCheck = new java.util.concurrent.ConcurrentHashMap<>();
    protected long timeOfLastCheck;

    public StatisticsReporter()
    {
        this.initMap(this.statsSinceLaunch);
        this.initMap(this.statsSinceLastCheck);
        this.timeOfLastCheck = System.currentTimeMillis();
    }

    public /* synchronized */ void initMap(java.util.Map<String, Long> statsMap)
    {
        statsMap.clear();
        for (String stat : StatisticsReporter.STATISTICS_DEFAULT_KEYS) statsMap.put(stat, 0L);
    }

    public /* synchronized */ java.util.List<java.util.Map.Entry<String, Long>> getStatsSinceLaunch()
    {
        return this.statsSinceLaunch.entrySet().stream().collect(java.util.stream.Collectors.toList());
    }

    public /* synchronized */ java.util.List<java.util.Map.Entry<String, Long>> getStatsSinceLastCheck()
    {
        java.util.List<java.util.Map.Entry<String, Long>> output = this.statsSinceLastCheck.entrySet().stream().collect(java.util.stream.Collectors.toList());

        this.initMap(this.statsSinceLastCheck);
        this.timeOfLastCheck = System.currentTimeMillis();

        return output;
    }

    public /* synchronized */ long getTimeOfLastCheck()
    {
        return this.timeOfLastCheck;
    }

    public /* synchronized */ void report(String value)
    {
        String[] words = value.split(" ");

        this.reportCount(words[0], this.statsSinceLaunch);
        this.reportCount(words[0], this.statsSinceLastCheck);

        if (ALU.operationsMap.containsKey(words[0].substring(words[0].length() - 3, words[0].length())))
        {
            try
            {
                Long result = Long.parseLong(words[1]);

                this.reportSum(StatisticsReporter.STATISTIC_ALU_COMMANDS_SUM, result, this.statsSinceLaunch);
                this.reportSum(StatisticsReporter.STATISTIC_ALU_COMMANDS_SUM, result, this.statsSinceLastCheck);
            }
            catch (Exception e) { }

            this.reportCount(StatisticsReporter.STATISTIC_ALU_OPERATIONS_COUNT, this.statsSinceLaunch);
            this.reportCount(StatisticsReporter.STATISTIC_ALU_OPERATIONS_COUNT, this.statsSinceLastCheck);
        }
    }

    protected void reportSum(String value, Long result, java.util.Map<String, Long> statsMap)
    {
        Long sum = statsMap.get(value);

        if (sum == null) sum = 0L;
        else sum += result;

        statsMap.put(value, sum);
    }

    protected void reportCount(String value, java.util.Map<String, Long> statsMap)
    {
        Long count = statsMap.get(value);

        if (count == null) count = 1L;
        else count++;

        statsMap.put(value, count);
    }
}
