public class NetBridge implements java.io.Closeable, IConsolePrinter
{
    protected final static String DISCOVERY_SERVICE_DISCOVER_MSG = "CCS DISCOVER";
    protected final static String DISCOVERY_SERVICE_FOUND_MSG = "CCS FOUND";
    protected final static int TCP_CLIENT_SO_TIMEOUT = 50;

    protected static class ClientHandler implements Runnable, IConsolePrinter
    {
        protected final java.net.Socket socket;

        protected String message = null;

        protected java.util.function.BiConsumer<String, ClientHandler> onMessageReceived = null;

        public ClientHandler(java.net.Socket socket)
        {
            super();

            this.socket = socket;
        }

        @Override
        public void run()
        {
            boolean isClientConnected = true;
            final java.io.BufferedReader reader;
            final java.io.BufferedWriter writer;
            final String recipientAddress = this.socket.getInetAddress().getHostAddress() + ':' + this.socket.getPort();

            try
            {
                this.socket.setSoTimeout(NetBridge.TCP_CLIENT_SO_TIMEOUT);
                reader = new java.io.BufferedReader(new java.io.InputStreamReader(this.socket.getInputStream()));
                writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(this.socket.getOutputStream()));
            }
            catch (Exception e)
            {
                this.printCriticalErrLn("Failed to fetch streams from socket of host[" + recipientAddress + "]", e);
                return;
            }

            this.printDebugInfoLn("Client handler of host[" + recipientAddress + "] has been initiated !");

            while (isClientConnected)
            {
                try
                {
                    this.message = reader.readLine();

                    if (message != null)
                    {
                        synchronized (this)
                        {
                            this.printDebugInfoLn("Received message[" + this.message + "] from host[" + this.socket.getInetAddress().getHostAddress() + ':' + this.socket.getPort() + "] !");

                            if (this.onMessageReceived == null) try { this.wait(500); } catch (Exception e) { this.onMessageReceived = (l, h) -> { }; }

                            this.onMessageReceived.accept(this.message, this);
                        }

                        writer.append(this.message).append('\n').flush();

                        this.printDebugInfoLn("Sent message[" + this.message + "] to host[" + recipientAddress + "] !");
                    }
                    else
                    {
                        try { writer.append("ERROR").append('\n').flush(); } catch (java.io.IOException e) { break; }
                    }
                }
                catch (java.net.SocketTimeoutException e) { }
                catch (Exception e)
                {
                    this.printErrLn("There was an exception raised during runtime of client handler thread !", e);
                }

                synchronized (this) { isClientConnected = !this.socket.isClosed(); }
            }

            this.printDebugInfoLn("Client handler of host[" + recipientAddress + "] has ended its work !");
        }

        public synchronized void setOnMessageReceived(java.util.function.BiConsumer<String, ClientHandler> value)
        {
            this.onMessageReceived = value;
            this.notify();
        }

        @Override
        public String getConsolePrinterPrefix()
        {
            return "[\033[38;5;10mClientHandler\033[0m]";
        }
    }

    private final boolean isInitiated;

    protected java.util.concurrent.ExecutorService threadExecutor = java.util.concurrent.Executors.newCachedThreadPool();

    protected java.util.Queue<ClientHandler> unhandledClients = new java.util.LinkedList<>();

    protected StatisticsReporter statisticsReporter;

    public NetBridge(int localPort)
    {
        super();

        this.init(localPort);

        this.isInitiated = true;
    }

    public NetBridge(int localPort, StatisticsReporter statisticsReporter)
    {
        super();

        this.init(localPort);

        this.isInitiated = true;
        this.statisticsReporter = statisticsReporter;
    }

    protected synchronized void init(int localPort)
    {
        if (this.isInitiated) throw new InstantiationError("Cannot initiate " + this.toString() + " because it has already been initiated !");

        this.threadExecutor.submit
        (
            //#region discovery service
            () ->
            {
                final Thread currentThread = Thread.currentThread();
                final byte[] buff = new byte[1500];
                final byte[] ansBuff = NetBridge.DISCOVERY_SERVICE_FOUND_MSG.getBytes();

                try (java.net.DatagramSocket socket = new java.net.DatagramSocket(localPort))
                {
                    this.printDebugInfoLn("Discovery sevice initiated at [" + socket.getLocalAddress().getHostAddress() + ':' + socket.getLocalPort() + "] !");
                    socket.setSoTimeout(0);

                    while (!currentThread.isInterrupted())
                    {
                        java.util.Arrays.fill(buff, (byte)0);
                        java.net.DatagramPacket packet = new java.net.DatagramPacket(buff, buff.length);
                        socket.receive(packet);

                        String message = new String(packet.getData()).trim();
                        this.printDebugInfoLn("Received UDP packet[" + message + "] from host[" + packet.getAddress().getHostAddress() + ':' + packet.getPort() + "] !");

                        if (message.equals(NetBridge.DISCOVERY_SERVICE_DISCOVER_MSG))
                        {
                            socket.send(new java.net.DatagramPacket(ansBuff, ansBuff.length, packet.getAddress(), packet.getPort()));
                            this.printDebugInfoLn("Send UDP packet [" + NetBridge.DISCOVERY_SERVICE_FOUND_MSG + "] to host[" + packet.getAddress().getHostAddress() + ':' + packet.getPort() + "] !");
                        }
                    }
                }
                catch (Exception e)
                {
                    this.printCriticalErrLn("A critical error was raised during runtime of the discovery service thread !", e);
                }

                this.printDebugInfoLn("Discovery service went down !");
            }
            //#endregion discovery service
        );

        this.threadExecutor.submit
        (
            () ->
            {
                Thread currentThread = Thread.currentThread();

                try (java.net.ServerSocket socket = new java.net.ServerSocket(localPort))
                {
                    socket.setSoTimeout(0);

                    this.printDebugInfoLn("Server ALU thread initiated !");

                    while (!currentThread.isInterrupted())
                    {
                        try
                        {
                            final java.net.Socket clientSocket = socket.accept();
                            ClientHandler handler = new ClientHandler(clientSocket);

                            this.printDebugInfoLn("New client[" + clientSocket.getInetAddress().getHostAddress().toString() + ':' + clientSocket.getPort() + "] connected !");
                            if (this.statisticsReporter != null) this.statisticsReporter.report(StatisticsReporter.STATISTIC_NEW_CLIENT_CONNECTIONS);

                            synchronized (this)
                            {
                                this.unhandledClients.add(handler);
                                this.threadExecutor.submit(handler);
                            }
                        }
                        catch (java.net.SocketTimeoutException e) { }
                        catch (Exception e) { throw e; }
                    }
                }
                catch (Exception e)
                {
                    this.printCriticalErrLn("There was an exception raised during runtime of tcp server thread !", e);
                }

                this.printDebugInfoLn("Server ALU thread went down !");
            }
        );
    }

    public synchronized boolean hasUnhandledConnections()
    {
        return !this.unhandledClients.isEmpty();
    }

    public synchronized ClientHandler nextUnhandledConnection()
    {
        return this.unhandledClients.poll();
    }

    @Override
    public void close()
    {
        this.printDebugInfoLn("NetBridge.close() was called !  Awaiting death of all threads in the pool !");

        synchronized (this)
        {
            this.threadExecutor.shutdownNow();
            try { this.threadExecutor.awaitTermination(0, null); } catch (Exception e) { }
        }
    }

    @Override
    public String getConsolePrinterPrefix()
    {
        return "[\033[38;5;159mNetBridge\033[0m]";
    }
}