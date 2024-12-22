public class Client implements IConsolePrinter
{
    public static void main(String[] args)
    {
        if (args.length < 1) return;

        ConsolePrinter.setVerbosePrintMode(true);

        try
        {
            int remotePort = Integer.parseInt(args[0]);

            new Client(remotePort);
        }
        catch (Exception e)
        {
            ConsolePrinter.printCriticalErrLn("Failed to initiate client !", e);
        }
    }

    public Client(int remotePort)
    {
        super();
        java.net.InetAddress remoteAddress = this.discoverCCS(remotePort);

        if (remoteAddress != null)
        {
            this.printDebugInfoLn("Successfully discovered CCS[" + remoteAddress.getHostAddress() + ':' + remotePort + "] !");

            this.connectToCCS(remoteAddress, remotePort);
        }
        else throw new Error("There is no CCS that would be reachable at port [" + remotePort + "] !");
    }

    public java.net.InetAddress discoverCCS(int remotePort)
    {
        try (java.net.DatagramSocket socket = new java.net.DatagramSocket())
        {
            java.net.InetAddress remoteAddress = java.net.InetAddress.getByName("255.255.255.255");

            socket.setBroadcast(true);
            socket.setSoTimeout(10000);

            final byte[] sBuff = "CCS DISCOVER".getBytes();

            for (int i = 0; i < 5; i++)
            {
                java.net.DatagramPacket sPacket = new java.net.DatagramPacket(sBuff, sBuff.length, remoteAddress, remotePort);
                this.printDebugInfoLn("Sent packet[CCS DISCOVER] to host[" + remoteAddress.getHostAddress() + ':' + remotePort + "] !");

                socket.send(sPacket);

                final byte[] rBuff = new byte[1500];
                java.net.DatagramPacket rPacket = new java.net.DatagramPacket(rBuff, rBuff.length);
                try
                {
                    socket.receive(rPacket);

                    return new String(rPacket.getData()).trim().equals("CCS FOUND") ? rPacket.getAddress() : null;
                }
                catch (Exception e) { this.printWarningLn("Timeout for discovery UDP packet reached !"); }
            }
        }
        catch (Exception e)
        {
            this.printErrLn("Failed to connect to CCS !", e);
        }

        return null;
    }

    public void connectToCCS(java.net.InetAddress remoteAddress, int remotePort)
    {
        final java.util.Random random = new java.util.Random();
        String command = "";

        try (java.net.Socket socket = new java.net.Socket(remoteAddress, remotePort))
        {
            socket.setSoTimeout(10000);

            final java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(socket.getOutputStream()));
            final java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

            for (int i = (int)(random.nextFloat() * 100) + 1; i >= 0; i--)
            {
                switch ((int)(random.nextFloat() * 4))
                {
                    case 0:
                    {
                        command = "ADD";
                    }
                    break;

                    case 1:
                    {
                        command = "SUB";
                    }
                    break;

                    case 2:
                    {
                        command = "MUL";
                    }
                    break;

                    case 3:
                    {
                        command = "DIV";
                    }
                    break;
                }

                for (int iArg = 1; iArg >= 0; iArg--) command += " " + (int)(random.nextFloat() * 201 - 100);

                writer.append(command).append('\n').flush();
                this.printDebugInfoLn("Sent command[" + command + "] to CCS[" + remoteAddress.getHostAddress() + ':' + remotePort + "] !");

                String answer = reader.readLine();
                this.printInfoLn("Received answer[" + answer + "] from CCS[" + remoteAddress.getHostAddress() + ':' + remotePort + "] !");
            }

            socket.close();
        }
        catch (Exception e)
        {
            this.printErrLn("Failed to connect to CCS[" + remoteAddress.getHostAddress() + ':' + remotePort + "] !", e);
        }
    }

    @Override
    public String getProgramPrinterPrefix()
    {
        return "[\033[38;5;154mClient\033[0m]";
    }
}
