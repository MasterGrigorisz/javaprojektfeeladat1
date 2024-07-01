package com.example.kalgr_projekt_bat;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.kalgr_projekt_bat.Client_login.pixel;

public class playgui extends Stage {
    private int meret;
    public LinkedBlockingQueue<String> commIN, commOUT;
    public Palya ownpalya;
    public Canvas canvENE, canvOWN;
    public String[] okes;

    public playgui(LinkedBlockingQueue<String> commin, LinkedBlockingQueue<String> commOUT, int meret) {
        this.meret = meret;
        this.commIN = commin;
        this.commOUT = commOUT;
        ownpalya = new Palya(meret, null, null);
        if (meret == 10) {
            okes = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        } else if (meret == 8) {
            okes = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "a", "b", "c", "d", "e", "f", "g", "h", "1", "2", "3", "4", "5", "6", "7", "8"};
        } else
            okes = new String[]{"A", "B", "C", "D", "E", "a", "b", "c", "d", "e", "f", "F", "1", "2", "3", "4", "5", "6"};
    }

    public void start(Stage stage) throws IOException {
        System.out.println("playgui start: " + this.meret);
        String cim = meret + " méretű battleship";
        stage.setTitle(cim);
        stage.setMinHeight(pixel * (meret + 1) + 100);
        stage.setMaxHeight(pixel * (meret + 1) + 100);
        stage.setMinWidth(((meret + 1) + (meret + 1) + 2) * pixel);
        stage.setMaxHeight(((meret + 1) + (meret + 1) + 2) * pixel);
        stage.setResizable(false);
        AnchorPane ablak = new AnchorPane();
        canvENE = new Canvas((meret + 1) * pixel, (meret + 1) * pixel);
        canvOWN = new Canvas((meret + 1) * pixel, (meret + 1) * pixel);
        canvamaker(canvOWN, true);
        canvamaker(canvENE, false);
        AnchorPane.setLeftAnchor(canvENE, 0d);
        AnchorPane.setLeftAnchor(canvOWN, ((meret + 1) * pixel + 50d));
        AnchorPane.setTopAnchor(canvENE, 0d);
        AnchorPane.setTopAnchor(canvOWN, 0d);
        Button shoot = new Button("SHOOT!");
        shoot.setSkin(new MyButtonSkin(shoot));
        shoot.setDisable(true);
        Label labsor = new Label("Oszlop (A):");
        Label labosz = new Label("Sor    (1):");
        TextField sor = new TextField();
        TextField oszl = new TextField();
        TextField chatin = new TextField();
        ListView<String> chathub = new ListView<>();
        Button chatcommit = new Button("Üzenet küldése");
        chatcommit.setSkin(new MyButtonSkin(chatcommit));
        chathub.setMaxSize(300 + (meret - 6) * pixel, 120);
        sor.setMaxWidth(75);
        oszl.setMaxWidth(75);

        double alja = (meret + 1) * pixel;
        AnchorPane.setLeftAnchor(shoot, 20d);
        AnchorPane.setLeftAnchor(labsor, 20d);
        AnchorPane.setLeftAnchor(labosz, 20d);
        AnchorPane.setLeftAnchor(sor, 120d);
        AnchorPane.setLeftAnchor(oszl, 120d);
        AnchorPane.setLeftAnchor(chatin, 300d);
        AnchorPane.setLeftAnchor(chathub, 450d);
        AnchorPane.setLeftAnchor(chatcommit, 300d);

        AnchorPane.setTopAnchor(shoot, alja + 80);
        AnchorPane.setTopAnchor(labsor, alja + 20);
        AnchorPane.setTopAnchor(labosz, alja + 55);
        AnchorPane.setTopAnchor(sor, alja + 55);
        AnchorPane.setTopAnchor(oszl, alja + 20);
        AnchorPane.setTopAnchor(chatin, alja + 20);
        AnchorPane.setTopAnchor(chathub, alja + 20);
        AnchorPane.setTopAnchor(chatcommit, alja + 50);

        canvENE.setVisible(false);
        int[] hajok = ownpalya.getBabuMeretek();
        AtomicInteger lerakotthajo = new AtomicInteger(0);
        AtomicBoolean vizszint = new AtomicBoolean(true);
        Label labhajosor = new Label("Oszlop :");
        Label labhajoosz = new Label("Sor    :");
        Label utasit = new Label("Írd be a következő hajód orrának koordinátáját!");
        Label merete = new Label("Hajó mérete: " + hajok[lerakotthajo.get()] + " egység.");
        TextField hajosor = new TextField();
        TextField hajoosz = new TextField();
        hajosor.setMaxWidth(75);
        hajoosz.setMaxWidth(75);
        Button lerak = new Button("Lerak");
        Button fordit = new Button("Fordít");
        lerak.setSkin(new MyButtonSkin(lerak));
        fordit.setSkin(new MyButtonSkin(fordit));
        ImageView image = new ImageView(new Image("hajo.png", 200, 71, true, true));

        AnchorPane.setLeftAnchor(image, 200d);
        AnchorPane.setLeftAnchor(labhajosor, 20d);
        AnchorPane.setLeftAnchor(labhajoosz, 20d);
        AnchorPane.setLeftAnchor(utasit, 20d);
        AnchorPane.setLeftAnchor(hajosor, 100d);
        AnchorPane.setLeftAnchor(hajoosz, 100d);
        AnchorPane.setLeftAnchor(lerak, 20d);
        AnchorPane.setLeftAnchor(fordit, 100d);
        AnchorPane.setLeftAnchor(merete, 20d);

        AnchorPane.setTopAnchor(image, 150d);
        AnchorPane.setTopAnchor(labhajosor, 40d);
        AnchorPane.setTopAnchor(labhajoosz, 80d);
        AnchorPane.setTopAnchor(utasit, 10d);
        AnchorPane.setTopAnchor(hajosor, 40d);
        AnchorPane.setTopAnchor(hajoosz, 80d);
        AnchorPane.setTopAnchor(lerak, 120d);
        AnchorPane.setTopAnchor(fordit, 120d);
        AnchorPane.setTopAnchor(merete, 150d);
        /*  enemy helyére felrakok 2 irhatót, 2 labelt, 2 gombot:
            1.gomb: hajó irányának változtatása
            2. felrakja a hajót a kiválasztott helyre(irható : sor, -oszlop)
         */
        fordit.setOnAction(e -> {
            if (vizszint.get()) {
                image.setRotate(-90.0);
                vizszint.set(false);
            } else {
                image.setRotate(0.0);
                vizszint.set(true);
            }
        });
        lerak.setOnAction(e -> {
            if (okes_e(hajosor.getText()) && okes_e(hajoosz.getText())) {
                //feldolgozás
                int babusor, babuosz;
                babusor = betuToSzam(hajosor.getText());
                babuosz = betuToSzam(hajoosz.getText());
                boolean helyesbabu = ownpalya.babufelrak(ownpalya.jatekosok[0], babusor, babuosz, vizszint.get(), lerakotthajo.get());
                if (helyesbabu) {
                    try {
                        commOUT.put("Move" + meret + " " + babusor + " " + babuosz + " " + vizszint + " " + lerakotthajo.get());
                        canvamaker(canvOWN, true);
                    } catch (InterruptedException ex) {
                        System.out.println("Nem sikerült elküldeni az üzenetet");
                    }
                    System.out.println("lerak: " + meret + " " + hajosor.getText() + " " + hajoosz.getText() + " " + vizszint);
                    if (lerakotthajo.addAndGet(1) < hajok.length)
                        merete.setText("Hajó mérete: " + hajok[lerakotthajo.get()] + " egység.");

                }
                if (lerakotthajo.get() == hajok.length) {
                    ablak.getChildren().remove(lerak);
                    ablak.getChildren().remove(fordit);
                    ablak.getChildren().remove(labhajosor);
                    ablak.getChildren().remove(labhajoosz);
                    ablak.getChildren().remove(utasit);
                    ablak.getChildren().remove(merete);
                    ablak.getChildren().remove(hajoosz);
                    ablak.getChildren().remove(hajosor);
                    ablak.getChildren().remove(image);
                    canvENE.setVisible(true);
                    shoot.setDisable(false);
                }
                hajosor.setText("");
                hajoosz.setText("");
            }
        });
        ////////////////////////////////////////
        shoot.setOnAction(e -> {
            int sorin = betuToSzam(sor.getText());
            int oszin = betuToSzam(oszl.getText());
            if (sorin <= 0 || sorin > meret || oszin <= 0 || oszin > meret) {
                //hülye a felhasználó
                sor.setText("");
                oszl.setText("");
            } else {
                //helyesen lőtt
                try {
                    commOUT.put("SHOO" + meret + " " + sorin + " " + oszin);
                } catch (InterruptedException ex) {
                    System.out.println("Nem sikerült elküldeni az üzenetet");
                }
                shoot.setDisable(true);
            }
        });
        ////////////////////////////////////////
        chatcommit.setOnAction(e -> {
            if (!chatin.getText().equals("")) {
                try {
                    commOUT.put("CHAT" + meret + " " + chatin.getText());
                } catch (InterruptedException ex) {
                    System.out.println("Nem sikerült elküldeni az üzenetet");
                }
                chathub.getItems().add("Te: " + chatin.getText()); //listview item hozzáadása
                chatin.setText("");
            }
        });
        ////////////////////////////////////////
        /*Thread t = */new Thread(() -> {
            while (true) {
                String bejovouzi = "";
                try {
                    bejovouzi = commIN.take(); //a take ráül a szálra és akkor megy tovább ha van üzi
                } catch (InterruptedException e) {
                    System.out.println("Nem sikerült kivenni az üzenetet");
                }
                if (bejovouzi.startsWith("CHAT")) {
                    String finalBejovouzi = bejovouzi;
                    Platform.runLater(() -> {
                        chathub.getItems().add("Ő: " + finalBejovouzi.substring(4)); //bejövő CHAT
                    });
                } else if (bejovouzi.startsWith("SHOOT")) {
                    //ide lőnek
                    String[] parts = bejovouzi.split(" ");
                    String sorlovet = parts[1];
                    String oszlovet = parts[2];
                    String eredmeny = parts[3];
                    String onmagaloevete = parts[4];
                    if (onmagaloevete.equals("true")) {
                        //a saját lövése, úgyhogy az enemy pályát kell módosítani
                        ownpalya.palya2[Integer.parseInt(sorlovet) - 1][Integer.parseInt(oszlovet) - 1] = Integer.parseInt(eredmeny);
                    } else {
                        //ez nem a saját lövésem, az én pályámon kell jelölni
                        ownpalya.palya1[Integer.parseInt(sorlovet) - 1][Integer.parseInt(oszlovet) - 1] = Integer.parseInt(eredmeny);
                    }
                    ownpalya.printout();
                    Platform.runLater(() -> {
                        //újra kell rajzolni a canvakat;
                        canvamaker(canvOWN, true); //palya1 alapján
                        canvamaker(canvENE, false);//palya2 alapján
                    });
                    shoot.setDisable(false);
                } else if (bejovouzi.startsWith("Stage")) {
                    ownpalya = new Palya(meret, null, null); //resetelem a mályát
                }
                ownpalya.eredmenyszamoto(ownpalya.palya1);
                ownpalya.eredmenyszamoto(ownpalya.palya2);
                if (ownpalya.pontok[0] == ownpalya.maxpont || ownpalya.pontok[1] == ownpalya.maxpont)
                    shoot.setDisable(true);
            }
        }).start();
        //t.start();
        ////////////////////////////////////////
        ablak.getChildren().addAll(canvENE, canvOWN, shoot, labsor, labosz, sor, oszl, chatin, chathub, chatcommit,
                image, labhajosor, labhajoosz, utasit, hajosor, hajoosz, lerak, fordit, merete);
        stage.setScene(new Scene(ablak));
        stage.sizeToScene();
        stage.show();
    }

    private int betuToSzam(String betu) {
        if (betu.equals("10")) betu = "j";
        if (betu.length() == 1)
            return switch (betu) {
                case "A", "a" -> 1;
                case "B", "b" -> 2;
                case "C", "c" -> 3;
                case "D", "d" -> 4;
                case "E", "e" -> 5;
                case "F", "f" -> 6;
                case "G", "g" -> 7;
                case "H", "h" -> 8;
                case "I", "i" -> 9;
                case "J", "j" -> 10;
                default -> Integer.parseInt(betu);
            };
        return 0;
    }

    private void canvamaker(Canvas ez, boolean witch) {
        int[][] palya;
        if (witch)
            palya = ownpalya.palya1;
        else
            palya = ownpalya.palya2;
        ez.setHeight(pixel * (meret + 1));
        ez.setWidth(pixel * (meret + 1));
        GraphicsContext gcEN = ez.getGraphicsContext2D();
        gcEN.setFill(Color.rgb(0, 94, 184));
        gcEN.setStroke(Color.BLACK);
        for (int i = 0; i < meret; i++)
            for (int j = 0; j < meret; j++) {
                if (palya[j][i] == 0 || palya[j][i] == -1)
                    gcEN.setFill(Color.rgb(0, 94, 184));
                else if (palya[j][i] == 1) {
                    gcEN.setFill(Color.rgb(90, 90, 90));
                } else if (palya[j][i] == 2) {
                    gcEN.setFill(Color.rgb(31, 41, 55));
                } else if (palya[j][i] == 3) {
                    gcEN.setFill(Color.rgb(111, 0, 0));
                }
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

    public boolean okes_e(String input) {
        for (String betuk : okes) {
            if (betuk.equals(input)) {
                return true;
            }
        }
        return false;
    }
}
