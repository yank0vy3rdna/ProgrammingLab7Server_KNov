package application;

import network.AddressedRequest;
import network.AddressedResponse;
import network.HandlerClient;
import collection.TicketList;
import commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.BindException;
import java.sql.SQLException;
import java.util.concurrent.*;

public class Context {
    public final int quantityThread = 4;

    public HandlerClient handlerClient;
    public HandlerCommands handlerCommands;
    public TicketList ticketList;
    public HandlerDatabase handlerDatabase;

    public Logger logger;

    public BlockingQueue<AddressedRequest> queueAddressedRequests;
    public BlockingQueue<AddressedResponse> queueAddressedResponse;

    public Context() {
        handlerClient = new HandlerClient();
        handlerCommands = new HandlerCommands(this);
        handlerDatabase = new HandlerDatabase(this);

        logger = LoggerFactory.getLogger("logger");
        logger.info("Logger configured");
        queueAddressedRequests = new LinkedBlockingQueue<>();
        queueAddressedResponse = new LinkedBlockingQueue<>();

        handlerCommands.setCommand(new CommandAverageOfPrice())
                       .setCommand(new CommandClear())
                       .setCommand(new CommandCountGreaterThanPrice())
                       .setCommand(new CommandHelp())
                       .setCommand(new CommandInfo())
                       .setCommand(new CommandInsert())
                       .setCommand(new CommandLogin())
                       .setCommand(new CommandRegistration())
                       .setCommand(new CommandRemoveKey())
                       .setCommand(new CommandRemoveLower())
                       .setCommand(new CommandRemoveLowerKey())
                       .setCommand(new CommandShow())
                       .setCommand(new CommandSumOfPrice())
                       .setCommand(new CommandUpdateById());
    }

    private static boolean isSolaris(String OS) {
        return (OS.contains("sunos"));
    }
    public void initialization(String[] args) {
        if(args.length != 1) {
            logger.error("Некорректный ввод порта!");
            System.exit(1);
        }

        int port = 0;
        try {
           port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            logger.error("Некорректный ввод порта!");
            System.exit(1);
        }
        try {
            String url;
            String os = System.getProperty("os.name").toLowerCase();
            if (isSolaris(os)) {
                url = "jdbc:postgresql://pg:5432/studs";
            } else {
                url = "jdbc:postgresql://127.0.0.1:5432/studs";
            }
            handlerDatabase.initialization(url, "s282335", "atw840"); //handlerDatabase.initialization("jdbc:postgresql://localhost:5432/lab6_prob");
        } catch (Exception e) {
            logger.error("Не удалось подключиться к базе данных!");
            System.exit(1);
        }

        try {
            ticketList = handlerDatabase.getTicketList();
        } catch (SQLException e) {
            logger.error("Не удалось прочесть коллекцию из базы данных!");
            System.exit(0);
        }
        logger.info("Коллекция заполнена.");

        try {
            handlerClient.bind(port);
            logger.info("Сервер инициализирован.");
        }
        catch (BindException e) {
            logger.error("Этот порт занят! Выберите другой порт и перезапустите программу.");
            System.exit(0);
        }
        catch (IOException e) {
            logger.error("Запустить сервер не удалось: " + e.getMessage());
            System.exit(0);
        }
    }

    public void run() {
        logger.info("Работа сервера запущенна.");

        ForkJoinPool pool = new ForkJoinPool();

        for (int i = 0; i < quantityThread; ++i) {
            pool.submit(new Receiver(this));
        }

        while (true) {
            if(!queueAddressedRequests.isEmpty()) {
                pool.execute(new HandlerRequest(this));
            }

            if(!queueAddressedResponse.isEmpty()) {
                pool.submit(new Transmitter(this));
            }
        }
    }
}
