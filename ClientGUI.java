package com.example.kalgr_projekt_bat;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.kalgr_projekt_bat.Client_login.pixel;

public class ClientGUI extends Stage {
    private ArrayList<String> users10, users8, users6, statstring;
    protected Socket clientSocket;
    protected BufferedReader serverInput;
    protected PrintWriter serverOutput;
    private String username;
    private ObservableList<String> items10, items8, items6, itemsstat;
    private int prefered;
    private AnchorPane ablak;
    private boolean updateplayers = true;
    LinkedBlockingQueue<String> queue10OUT = new LinkedBlockingQueue<>(), queue10IN = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<String> queue8OUT = new LinkedBlockingQueue<>(), queue8IN = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<String> queue6OUT = new LinkedBlockingQueue<>(), queue6IN = new LinkedBlockingQueue<>();
    boolean tizesres = false, nyolcasres = false, hatosres = false;

    public ClientGUI(Socket clientSocket, BufferedReader serverInput, PrintWriter serverOutput) {
        this.clientSocket = clientSocket;
        this.serverInput = serverInput;
        this.serverOutput = serverOutput;
        prefered = 10;
        statstring = new ArrayList<>();
    }

    public void start(Stage stage) throws IOException {
        stage.setTitle("Battleship menü");
        stage.setMinHeight(750);
        stage.setMaxHeight(750);
        stage.setMinWidth(1470);
        stage.setMaxWidth(1470);
        stage.setResizable(false);
        ablak = new AnchorPane();
        sendLine("Users please");
        items10 = FXCollections.observableArrayList();
        items8 = FXCollections.observableArrayList();
        items6 = FXCollections.observableArrayList();
        itemsstat = FXCollections.observableArrayList();
        saveUsers();

        sendLine("Stats plese");
        String statok = serverInput.readLine();
        String[] parts = statok.split(" ");
        Label statwin = new Label("Wins:  " + parts[0]);
        Label statlos = new Label("Loses: " + parts[1]);
        Label statkid = new Label("K/D:   " + parts[2]);
        statok = serverInput.readLine();
        while (!statok.equals("That's all folks")) {
            statstring.add(statok);
            statok = serverInput.readLine();
        }
        items10.setAll(users10);
        items8.setAll(users8);
        items6.setAll(users6);
        itemsstat.setAll(statstring);
        stage.setTitle("Battleship menü :" + username);
        ListView<String> opponents10, opponents8, opponents6, stat;
        opponents10 = new ListView<>();
        opponents8 = new ListView<>();
        opponents6 = new ListView<>();
        stat = new ListView<>();
        AtomicReference<String> lastSelectedItem = new AtomicReference<>();
        opponents10.setItems(items10);
        opponents8.setItems(items8);
        opponents6.setItems(items6);
        stat.setItems(itemsstat);
        opponents10.setMaxSize(180, 200);
        opponents8.setMaxSize(180, 200);
        opponents6.setMaxSize(180, 200);
        stat.setMaxSize(400, 140);
        stat.setMinSize(400, 140);
        Label player10 = new Label("10-esen játszók");
        Label player8 = new Label("8-ason játszók");
        Label player6 = new Label("6-oson játszók");
        Label statup = new Label("Stat belépésenként frissül.");
        AnchorPane.setLeftAnchor(opponents10, 850d);
        AnchorPane.setLeftAnchor(player10, 850d);
        AnchorPane.setLeftAnchor(opponents8, 1050d);
        AnchorPane.setLeftAnchor(player8, 1050d);
        AnchorPane.setLeftAnchor(opponents6, 1250d);
        AnchorPane.setLeftAnchor(stat, 50d);
        AnchorPane.setLeftAnchor(player6, 1250d);
        AnchorPane.setLeftAnchor(statwin, 650d);
        AnchorPane.setLeftAnchor(statlos, 650d);
        AnchorPane.setLeftAnchor(statkid, 650d);
        AnchorPane.setLeftAnchor(statup, 470d);
        AnchorPane.setTopAnchor(opponents10, 500d);
        AnchorPane.setTopAnchor(player10, 480d);
        AnchorPane.setTopAnchor(opponents8, 500d);
        AnchorPane.setTopAnchor(player8, 480d);
        AnchorPane.setTopAnchor(opponents6, 500d);
        AnchorPane.setTopAnchor(player6, 480d);
        AnchorPane.setTopAnchor(stat, 560d);
        AnchorPane.setTopAnchor(statwin, 550d);
        AnchorPane.setTopAnchor(statlos, 600d);
        AnchorPane.setTopAnchor(statkid, 650d);
        AnchorPane.setTopAnchor(statup, 560d);
        Canvas canvtiz = new Canvas();
        Canvas canvnyolc = new Canvas();
        Canvas canvhat = new Canvas();
        canvamaker(canvtiz, 10);
        canvamaker(canvnyolc, 8);
        canvamaker(canvhat, 6);
        AnchorPane.setTopAnchor(canvtiz, 0d);
        AnchorPane.setTopAnchor(canvnyolc, 0d);
        AnchorPane.setTopAnchor(canvhat, 0d);
        AnchorPane.setLeftAnchor(canvtiz, 0d);
        AnchorPane.setLeftAnchor(canvnyolc, 580d);
        AnchorPane.setLeftAnchor(canvhat, 1070d);
        Button bUpdate = new Button("Update players");
        Button PLAY = new Button("PLAY");
        PLAY.setPrefSize(100d, 100d);
        PLAY.setStyle("-fx-font-size:20");
        bUpdate.setSkin((new MyButtonSkin(bUpdate)));
        PLAY.setSkin((new MyButtonSkin(PLAY)));
        AnchorPane.setTopAnchor(bUpdate, 490d);
        AnchorPane.setTopAnchor(PLAY, 490d);
        AnchorPane.setLeftAnchor(bUpdate, 590d);
        AnchorPane.setLeftAnchor(PLAY, 740d);
        playgui tizesjatek = new playgui(queue10OUT, queue10IN, 10);
        playgui nyolcasjatek = new playgui(queue8OUT, queue8IN, 8);
        playgui hatosjatek = new playgui(queue6OUT, queue6IN, 6);
        Stage tizesstage = new Stage();
        Stage nyolvasstage = new Stage();
        Stage hatosstage = new Stage();

        tizesstage.setOnCloseRequest(windowEvent -> {
            if (!tizesres)
                sendLine("Stage1");
            try {
                queue10OUT.put("Stage");
            } catch (InterruptedException e) {
                System.out.println("Nem resetelődött a pálya");
            }
            tizesres = false;
        });
        nyolvasstage.setOnCloseRequest(windowEvent -> {
            if (!nyolcasres)
                sendLine("Stage8");
            try {
                queue8OUT.put("Stage");
            } catch (InterruptedException e) {
                System.out.println("Nem resetelődött a pálya");
            }
            nyolcasres = false;
        });
        hatosstage.setOnCloseRequest(windowEvent -> {
            if (!hatosres)
                sendLine("Stage6");
            try {
                queue6OUT.put("Stage");
            } catch (InterruptedException e) {
                System.out.println("Nem resetelődött a pálya");
            }
            hatosres = false;
        });
        ablak.getChildren().addAll(opponents10, opponents8, opponents6
                , bUpdate, player10, player8, player6
                , canvtiz, canvnyolc, canvhat, PLAY, stat, statwin, statlos, statkid, statup
        );
        stage.setScene(new Scene(ablak));
        stage.sizeToScene();
        stage.show();
        /**
         * itt lent a listáknak a kiválasztása van
         */
        opponents10.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
                    lastSelectedItem.set(newVal);
                    prefered = 10;
                });
        opponents8.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
                    lastSelectedItem.set(newVal);
                    prefered = 8;
                });
        opponents6.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String oldVal, String newVal) -> {
                    lastSelectedItem.set(newVal);
                    prefered = 6;
                });

        Thread t3 = new Thread(() -> { //Csukd össze és úgy látszik h ugyan az mint a kövi kettő
            String ez = "";
            while (!clientSocket.isClosed()) {
                try {
                    ez = queue10IN.take();//a take ráül a szálra és akkor megy tovább ha van üzi
                } catch (InterruptedException e) {
                    System.out.println("Nem sikerült kivenni az üzenetet");
                }
                if (ez.startsWith("SHOO"))
                    sendLine("SHOOT");
                else if (ez.startsWith("Move"))
                    sendLine("Move");
                else sendLine("CHAT");
                sendLine(ez.substring(4));//megadja: hajotrerak a x méretű táblára: A 1 true hjelyre
            }
        });//10-es pálya beszéllgetés - bejövő
        t3.setDaemon(true);
        t3.start();
        Thread t4 = new Thread(() -> {
            String ez = "";
            while (!clientSocket.isClosed()) {
                try {
                    ez = queue8IN.take();//a take ráül a szálra és akkor megy tovább ha van üzi
                } catch (InterruptedException e) {
                    System.out.println("Nem sikerült kivenni az üzenetet");
                }
                if (ez.startsWith("SHOO"))
                    sendLine("SHOOT");
                else if (ez.startsWith("Move"))
                    sendLine("Move");
                else sendLine("CHAT");
                sendLine(ez.substring(4));//megadja: hajotrerak a x méretű táblára: A 1 true hjelyre
            }
        });//8-es pálya beszéllgetés - bejövő
        t4.setDaemon(true);
        t4.start();
        Thread t5 = new Thread(() -> {
            String ez = "";
            while (!clientSocket.isClosed()) {
                try {
                    ez = queue6IN.take();//a take ráül a szálra és akkor megy tovább ha van üzi
                } catch (InterruptedException e) {
                    System.out.println("Nem sikerült kivenni az üzenetet");
                }
                if (ez.startsWith("SHOO"))
                    sendLine("SHOOT");
                else if (ez.startsWith("Move"))
                    sendLine("Move");
                else sendLine("CHAT");
                sendLine(ez.substring(4));//megadja: hajotrerak a x méretű táblára: A 1 true hjelyre
            }
        });//6-es pálya beszéllgetés - bejövő
        t5.setDaemon(true);
        t5.start();
        /**
         * play gomb megnyomására indítja el a megfelelő méretű játékot, ha az nincs még megnyitva
         */
        PLAY.setOnAction(e -> {
            if (lastSelectedItem.get() == null) {
                System.out.println("Nyomj rá egy játékra a listákból!");
            } else {
                playgui ezenmegy = null;
                Stage ezenfut = null;
                switch (prefered) {
                    case 10 -> {
                        ezenmegy = tizesjatek;
                        ezenfut = tizesstage;
                        tizesres = false;
                    }
                    case 8 -> {
                        ezenmegy = nyolcasjatek;
                        ezenfut = nyolvasstage;
                        nyolcasres = false;
                    }
                    case 6 -> {
                        ezenmegy = hatosjatek;
                        ezenfut = hatosstage;
                        hatosres = false;
                    }
                    default -> System.out.println("Unexpected value (10 / 8 / 6): " + prefered);
                }
                if (ezenfut != null && !ezenfut.isShowing()) {
                    //lehet indítani a játékot
                    String jatek = lastSelectedItem.get(), szabad = " - Szabad játék"; //különleges felhasználónevek
                    sendLine("PLAY"); //servernek flag
                    if (jatek.startsWith("Új játék - ")) //új játék
                        sendLine("Új játék");
                    else if (jatek.endsWith(szabad))
                        sendLine(jatek.substring(0, jatek.length() - szabad.length())); //meglévő: le kell vágni a végét a felhasználóhoz
                    sendLine(String.valueOf(prefered)); //pályaméret
                    try {
                        ezenmegy.start(ezenfut);//játék indítása
                    } catch (IOException ex) {
                        System.out.println("Nem sikerült elindítani a játékot");
                    }
                }
                opponents10.getSelectionModel().clearSelection();
                opponents8.getSelectionModel().clearSelection();
                opponents6.getSelectionModel().clearSelection();
            }

        });
        bUpdate.setOnAction(e -> updateplayers = true);
        /**
         * Folytonos kommunikáció a szerverrel: Update-et kérünk
         */
        final int[] updatesec = {0};
        ScheduledService updater;
        updater = new ScheduledService<Void>() {// NE bántsd!
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        ///////////////////////////////
                        String bejovo = "";
                        if (!clientSocket.isClosed()) {
                            try {
                                sendLine("UPDATE");
                                bejovo = serverInput.readLine();
                            } catch (IOException e) {
                                System.out.println("Nem érkezett meg az üzenet");
                            }
                            updatesec[0]++;
                            if (updatesec[0] == 70) { //7 secenként updateli magától is a playerlistát--már ha 1 secenként fut a frissítés
                                updatesec[0] = 0;
                                updateplayers = true;
                            }
                            if (bejovo.equals("Users please")) {
                                saveUsers();
                                if (updateplayers)
                                    Platform.runLater(() -> {
                                        items10.setAll(users10);
                                        items8.setAll(users8);
                                        items6.setAll(users6);
                                        updateplayers = false;
                                    });
                            } else if (bejovo.startsWith("SHOOT")) {
                                //továbbküldöm a táblának
                                try {
                                    switch (bejovo.substring(0, 6)) {
                                        case "SHOOT1" -> queue10OUT.put("SHOOT" + bejovo.substring(7));
                                        case "SHOOT8" -> queue8OUT.put("SHOOT" + bejovo.substring(6));
                                        case "SHOOT6" -> queue6OUT.put("SHOOT" + bejovo.substring(6));
                                        default -> System.out.println("bejövő üzenet nem felismerhető: " + bejovo);
                                    }
                                } catch (InterruptedException e) {
                                    System.out.println("Nem sikerült átadni az üzenetet");
                                }
                            } else {
                                if (bejovo.startsWith("RES")) {
                                    String kezdhetsz = "*Az ellenfél offline. Kiléphetsz a játékból és kezdhetsz másikat.*";
                                    switch (bejovo) {
                                        case "RES1" -> bejovo = "CHAT10" + kezdhetsz;
                                        case "RES8" -> bejovo = "CHAT8" + kezdhetsz;
                                        case "RES6" -> bejovo = "CHAT6" + kezdhetsz;
                                    }
                                } else if (bejovo.startsWith("WIN")) {
                                    String kezdhetsz = "*Nyertél! A felírt eredményetek: ";
                                    switch (bejovo.substring(0, 4)) {
                                        case "WIN1" ->  bejovo = "CHAT10" + kezdhetsz + "10 " + bejovo.substring(5) + "*";
                                        case "WIN8" ->  bejovo = "CHAT8" + kezdhetsz + "8 " + bejovo.substring(4) + "*";
                                        case "WIN6" ->  bejovo = "CHAT6" + kezdhetsz + "6 " + bejovo.substring(4) + "*";
                                    }
                                } else if (bejovo.startsWith("LOS")) {
                                    String kezdhetsz = "*Vesztettél! A felírt eredményetek: ";
                                    switch (bejovo.substring(0, 4)) {
                                        case "LOS1" -> {
                                            bejovo = "CHAT10" + kezdhetsz + "10 " + bejovo.substring(5) + "*";
                                            tizesres = true;
                                        }
                                        case "LOS8" -> {
                                            bejovo = "CHAT8" + kezdhetsz + "8 " + bejovo.substring(4) + "*";
                                            nyolcasres = true;
                                        }
                                        case "LOS6" -> {
                                            bejovo = "CHAT6" + kezdhetsz + "6 " + bejovo.substring(4) + "*";
                                            hatosres = true;
                                        }
                                    }
                                }
                                try {
                                    switch (bejovo.substring(0, 5)) {
                                        case "CHAT1" -> queue10OUT.put("CHAT" + bejovo.substring(6));
                                        case "CHAT8" -> queue8OUT.put("CHAT" + bejovo.substring(5));
                                        case "CHAT6" -> queue6OUT.put("CHAT" + bejovo.substring(5));
                                        default -> System.out.println("bejövő üzenet beolvasva: " + bejovo);
                                    }
                                } catch (InterruptedException e) {
                                    System.out.println("Nem sikerült elküldeni az üzenetet");
                                }
                            }
                        }
                        ///////////////////////////////
                        return null;
                    }
                };
            }
        };
        updater.setPeriod(Duration.seconds(0.1));//--------------------------------------------------------
        updater.start();
        //run() vége
    }

    private void canvamaker(Canvas ez, int meret) {
        ez.setHeight(pixel * (meret + 1));
        ez.setWidth(pixel * (meret + 1));
        GraphicsContext gcEN = ez.getGraphicsContext2D();
        gcEN.setFill(Color.rgb(0, 94, 184));
        gcEN.setStroke(Color.BLACK);
        for (int i = 0; i < meret; i++)
            for (int j = 0; j < meret; j++) {
                gcEN.fillRect(pixel * (i + 1), pixel * (j + 1), pixel, pixel);
                gcEN.strokeRect(pixel * (i + 1), pixel * (j + 1), pixel, pixel);
            }
        gcEN.setFill(Color.BLACK);
        String[] betuk = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        for (int i = 0; i < meret; i++) {
            int nagyszam = 0;
            gcEN.strokeText(betuk[i], pixel * (i + 1) + pixel / 2, pixel / 2);
            if (i > 8)
                nagyszam = 4; //10-es szám arrébb kezdődjön
            gcEN.strokeText(Integer.toString(i + 1), pixel / 2 - nagyszam, pixel * (i + 1) + pixel / 2);
        }
    }

    private void saveUsers() {
        try {
            String buffer = serverInput.readLine();
            if (buffer.equals("Users please"))
                username = serverInput.readLine();
            else
                username = buffer;

            String ServerLine = serverInput.readLine();
            users10 = new ArrayList<>();
            users8 = new ArrayList<>();
            users6 = new ArrayList<>();
            while (!ServerLine.equals("That's all folks")) {//ez a minden updatenek a vége
                String temp1 = ServerLine, temp2 = serverInput.readLine();
                ServerLine = serverInput.readLine();
                if (temp2.equals("") && !temp1.equals("Új játék"))
                    temp2 = "Szabad játék";
                if (ServerLine.equals("10"))
                    users10.add(temp1 + " - " + temp2);
                else if (ServerLine.equals("8"))
                    users8.add(temp1 + " - " + temp2);
                else
                    users6.add(temp1 + " - " + temp2);
                ServerLine = serverInput.readLine();
            }
        } catch (IOException e) {
            System.out.println("Nem érkezett meg az üzenet");
        }
    }

    protected void sendLine(String line) {
        try {
            serverOutput.print(line + "\r\n");
            serverOutput.flush();
        } catch (Exception e) {
            System.out.println("Nem tudta elküldeni a client az üzenetet");
        }
    }
}
