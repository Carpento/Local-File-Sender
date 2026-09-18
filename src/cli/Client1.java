import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.Scanner;


public class Client1 {
    private static final Scanner scanner = new Scanner(System.in);
    private static int serverPort = 65535;
    private static int clientPort = 53556;

    public static String getCurrentClientIpv4() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String localIP = localHost.getHostAddress();
            return localIP;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void reciver() throws IOException {
        String serverIp = getCurrentClientIpv4();

        System.out.println("\n[ ! ] Starting socket server, IP: " + serverIp);
        Socket client1;
        try (ServerSocket server = new ServerSocket(serverPort)) {
            System.out.println("[ ! ] Server started without exceptions...\n[ ! ] Listening on port " + serverPort);
            client1 = server.accept();
        }
        System.out.println("[ ! ] " + client1.getInetAddress() + " connected");

        InputStream input = client1.getInputStream();
        DataInputStream reader = new DataInputStream(input);

        String filename = reader.readUTF();

        File file = new File(filename);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                System.out.println(buffer);
            }
        }
        System.out.println("[ ! ] File received and saved: " + filename + "\n");
    }

    public static void sender(String ip, String absoluteFilePath) throws IOException {
        System.out.println("[ ! ] Creating socket connection");
        Socket client = new Socket(ip, clientPort);

        System.out.println("[ ! ] Setting streams");
        OutputStream output = client.getOutputStream();
        DataOutputStream writer = new DataOutputStream(output);
        System.out.println("[ ! ] Getting path");
        Path path = Path.of(absoluteFilePath);
        System.out.println("[ ! ] Getting file name");
        String filename = path.getFileName().toString();
        System.out.println("[ ! ] Sending a packet, content: " + filename);

        writer.writeUTF(filename);

        System.out.println("[ ! ] Sending a packet, content:");
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
                System.out.println(buffer);
            }
        }
        System.out.println("[ ! ] Ensuring that all data is sent");
        writer.flush();
        System.out.println("[ ! ] Closing connections\n");
        writer.close();
        client.close();
    }

    public static void main(String[] args) {
        String menu = """
                #----------LHFS-LocalHost-File-Sender----------#
                #          1. Server management                #
                #          2. Send file                        #
                #          3. Credits                          #
                #                                         V1.0 #
                #----------------------------------------------#
                """;
        String credits = """
                #----------LHFS-LocalHost-File-Sender----------#
                #                    CREDITS                   #
                #                Dev: Carpento                 #
                #       Bug resolver: Carpento                 #
                #----------------------------------------------#
                """;
        String serverManagementMenu = """
                #----------LHFS-LocalHost-File-Sender----------#
                #                SERVER-CONFIG                 #
                #                1. Set port                   #
                #                2. Run server                 #
                #                                              #
                #----------------------------------------------#
                """;

        while (true) {
            try {
                System.out.println(menu);
                System.out.print(getCurrentClientIpv4() + "$-");
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 1) {
                    System.out.println(serverManagementMenu);
                    System.out.print(getCurrentClientIpv4() + "$-");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice == 1) {
                        System.out.print("\nType a new port:");
                        serverPort = scanner.nextInt();
                        System.out.println("Press enter to continue");
                        scanner.nextLine();
                        scanner.nextLine();
                    } else if (choice == 2) {
                        try {
                            reciver();
                        } catch (IOException e) {
                            e.getStackTrace();
                        }
                    } else {
                        System.out.println("Enter a valid option");
                    }
                } else if (choice == 2) {
                    System.out.print("\nType server IPv4 address:");
                    String ip = scanner.nextLine();
                    System.out.print("\nType file absolute path:");
                    String filepath = scanner.nextLine();
                    try {
                        sender(ip, filepath);
                    } catch (IOException e) {
                        e.getStackTrace();
                    }
                } else if (choice == 3) {
                    System.out.println(credits);
                    System.out.println("Press enter to continue");
                    scanner.nextLine();
                } else {
                    System.out.println("Enter a valid option");
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}

