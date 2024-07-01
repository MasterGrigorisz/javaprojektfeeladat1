package com.example.kalgr_projekt_bat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class ServerThr extends Thread {
    protected Socket clientSocket;
    protected BufferedReader clientReader;
    protected PrintWriter clientWriter;
    private static int ID = 0;
    protected ArrayList<ServerMatrix> onlineusers;
    protected ArrayList<Palya> Palya10, Palya8, Palya6; //bejövő, közös pályák
    protected ServerMatrix ownuser;
    protected Palya tizespalya = null, nyolcaspalya = null, hatospalya = null;
    protected ArrayList<String> uzenetek = new ArrayList<>(), stats;
    private double kd;
    private int id;
    private int win = 0, los = 0;

    public ServerThr(Socket clientSocket, ArrayList<ServerMatrix> onlineusers,
                     ArrayList<Palya> Palya10, ArrayList<Palya> Palya8, ArrayList<Palya> Palya6)
            throws IOException {
        this.clientSocket = clientSocket;
        this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.clientWriter = new PrintWriter(clientSocket.getOutputStream());
        this.id = ServerThr.ID++;
        this.onlineusers = onlineusers;
        synchronized (onlineusers) {
            for (ServerMatrix ez : onlineusers)
                if (ez.socket.equals(clientSocket))
                    ownuser = ez;
        }
        this.Palya10 = Palya10;
        this.Palya8 = Palya8;
        this.Palya6 = Palya6;
        stats = new ArrayList<>();
    }

    protected void sendLine(String line) {
        try {
            clientWriter.print(line + "\r\n");
            clientWriter.flush();
            //System.out.println(id + " Server out: " + line);
        } catch (Exception e) {
            System.out.println("Nem tudta elküldeni a szerver az üzenetet");
        }
    }

    public boolean readUsersFromFile(String user, String pass) {
        String reg = "Registrate please";
        try {
            if (user.startsWith(reg)) {
                registratepls(user.substring(reg.length()), pass);
                user = user.substring(reg.length());
            }
            if (already_reg(user, pass)) {
                System.out.println(id + user + " " + pass);
                sendLine("Welcome");
                System.out.println(id + " Welcome user " + user);
                return true;
            } else {
                sendLine("Dunno");
                System.out.println(id + " Dunno user " + user);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Gond volt a fájlokkal");
        }
        return false;
    }

    public void registratepls(String user, String pass) {
        faljbaIrat("Users.txt", user);
        faljbaIrat("Users.txt", pass);
        System.out.println(id + " user registrated " + user);
        System.out.println(id + " " + user);
    }

    public boolean already_reg(String user, String pass) throws IOException {
        FileReader fr;
        try {
            fr = new FileReader("Users.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Nem sikerült megnyitni a Users.txt fájlt");
            return false;
        }
        BufferedReader br = new BufferedReader(fr);
        outerloop:
        while (br.ready()) {
            if (br.readLine().equals(user))
                if (br.readLine().equals(pass)) {
                    synchronized (onlineusers) {
                        for (ServerMatrix ez : onlineusers) {
                            if (ez.username.equals(user))//ha már be van lépve elutasítom
                                break outerloop;
                            if (ez.socket.equals(clientSocket)) {
                                ownuser = ez;
                                ownuser.username = user;
                                ownuser.szal = this;
                            }
                        }
                    }
                    br.close();
                    fr.close();
                    statolvas(user);
                    return true;
                }
        }
        br.close();
        fr.close();
        return false;
    }

    private void statolvas(String user) {
        FileReader fr;
        try {
            File file = new File(user + ".txt");
            file.createNewFile();//létrehozva tuti
            fr = new FileReader(user + ".txt");
            BufferedReader br = new BufferedReader(fr);
            double k = 0, d = 0;
            while (br.ready()) {
                String sor = br.readLine();
                if (!sor.equals("")) {
                    stats.add(sor);
                    String[] parts = sor.split(" ");
                    int elso = 0, masodik = 0;
                    if (parts.length > 9) {
                        elso = Integer.parseInt(parts[6]);
                        masodik = Integer.parseInt(parts[8]);
                    }
                    if (sor.startsWith("WIN")) {
                        win++;
                        if (elso > masodik) {
                            k += elso;
                            d += masodik;
                        } else {
                            k += masodik;
                            d += elso;
                        }
                    } else if (sor.startsWith("LOS")) {
                        los++;
                        if (elso < masodik) {
                            k += elso;
                            d += masodik;
                        } else {
                            k += masodik;
                            d += elso;
                        }
                    }
                }
            }
            if (d == 0)
                d = 1;
            kd = k / d;
        } catch (IOException e) {
            System.out.println("Nem sikerült megnyitni a felhasználó fájlját");
        }
    }

    public void faljbaIrat(String txt, String data) {
        try {
            File file = new File(txt);
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(txt, true));
            writer.write(data + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            System.out.println("Hiba a fájl írása közben");
        }
    }

    public void run() {
        try {
            while (!clientSocket.isClosed() && !readUsersFromFile(clientReader.readLine(), clientReader.readLine())) {
                System.out.println("Azonosítatlan felhasználó");
            }
            //Azonosított felhasználó
            String clientLine;
            /**
             * Ez adja oda a megüzent üzit a felhasználónak
             */
            Thread t_que = new Thread(() -> {
                String blocking = "";
                while (!clientSocket.isClosed()) {
                    try {
                        blocking = ownuser.uzenetneki_que.take();//a take ráül a szálra és akkor megy tovább ha van üzi
                    } catch (InterruptedException e) {
                        System.out.println("Nem sikerült kivenni az üzenetet");
                    }
                    if (blocking.startsWith("RES")) {
                        ServerMatrix ellen;
                        switch (blocking) {
                            case "RES1" -> {
                                if (tizespalya.jatekosok[0] == ownuser)
                                    ellen = tizespalya.jatekosok[1];
                                else
                                    ellen = tizespalya.jatekosok[0];
                                if (tizespalya.egyeskesz && tizespalya.ketteskesz) {//ez akkor is jó ha az ellen null
                                    faljbaIrat(ownuser.username + ".txt", "LEFT 10 méretű pályán a pontjai: " + tizespalya.pontok[0] + " - " + tizespalya.pontok[1] + " a maximális " + tizespalya.maxpont + "-ből ellene: " + ellen.username);
                                }
                                tizespalya = null;
                            }
                            case "RES8" -> {
                                if (nyolcaspalya.jatekosok[0] == ownuser)
                                    ellen = nyolcaspalya.jatekosok[1];
                                else
                                    ellen = nyolcaspalya.jatekosok[0];
                                if (nyolcaspalya.egyeskesz && nyolcaspalya.ketteskesz) {//ez akkor is jó ha az ellen null
                                    faljbaIrat(ownuser.username + ".txt", "LEFT 8 méretű pályán a pontjai: " + nyolcaspalya.pontok[0] + " - " + nyolcaspalya.pontok[1] + " a maximális " + nyolcaspalya.maxpont + "-ből ellene: " + ellen.username);
                                }
                                nyolcaspalya = null;
                            }
                            case "RES6" -> {
                                if (hatospalya.jatekosok[0] == ownuser)
                                    ellen = hatospalya.jatekosok[1];
                                else
                                    ellen = hatospalya.jatekosok[0];
                                if (hatospalya.egyeskesz && hatospalya.ketteskesz) {//ez akkor is jó ha az ellen null
                                    faljbaIrat(ownuser.username + ".txt", "LEFT 6 méretű pályán a pontjai: " + hatospalya.pontok[0] + " - " + hatospalya.pontok[1] + " a maximális " + hatospalya.maxpont + "-ből ellene: " + ellen.username);
                                }
                                hatospalya = null;
                            }
                        }
                    }
                    synchronized (uzenetek) {
                        uzenetek.add(blocking);
                    }
                }
            });
            t_que.start();
            while (!clientSocket.isClosed()) {
                /**
                 * Ez a fő futó ciklus, ami figyeli a felhasználótól bejövő adatokat és egyebeket
                 */
                clientLine = clientReader.readLine();
                switch (clientLine) {
                    case "Stats plese" -> {
                        sendLine(win + " " + los + " " + kd);
                        while (stats.size() != 0)
                            sendLine(stats.remove(0));
                        sendLine("That's all folks");
                    }
                    case "Users please" -> sendusers();
                    case "PLAY" -> {
                        clientLine = clientReader.readLine();
                        jatekotindit(clientLine, clientReader.readLine());
                    }
                    case "Move" -> jatekkezelo(clientReader.readLine());
                    case "UPDATE" -> {
                        sendusers();
                        if (tizespalya != null) IntStream.range(0, 3).forEach(i -> tizespalya.runPls());
                        if (nyolcaspalya != null) for (int i = 0; i < 3; i++) nyolcaspalya.runPls();
                        if (hatospalya != null) for (int i = 0; i < 3; i++) hatospalya.runPls();
                        synchronized (uzenetek) { //ezek a sima sreveren belüli üzenetek, nem pálya
                            for (String uzi : uzenetek) {
                                sendLine(uzi);
                            }
                            uzenetek = new ArrayList<>();
                        }
                        //////// 10 Pályákórl kiküldjük az üzeneteket
                        if (tizespalya != null)
                            if (tizespalya.jatekosok[0] == ownuser)
                                synchronized (tizespalya.lovesekS1) {
                                    for (String uzi : tizespalya.lovesekS1) {
                                        if (uzi.startsWith("WIN") || uzi.startsWith("LOS")) {
                                            faljbaIrat(ownuser.username + ".txt", uzi.substring(0, 3) + " " + uzi.substring(3));
                                            palyaeldobo(1);
                                            tizespalya = null;
                                        }
                                        sendLine(uzi);
                                    }
                                    if (tizespalya != null)
                                        tizespalya.lovesekS1 = new ArrayList<>();
                                }
                            else
                                synchronized (tizespalya.lovesekS2) {
                                    for (String uzi : tizespalya.lovesekS2) {
                                        if (uzi.startsWith("WIN") || uzi.startsWith("LOS")) {
                                            faljbaIrat(ownuser.username + ".txt", uzi.substring(0, 3) + " " + uzi.substring(3));
                                            palyaeldobo(1);
                                            tizespalya = null;
                                        }
                                        sendLine(uzi);
                                    }
                                    if (tizespalya != null)
                                        tizespalya.lovesekS2 = new ArrayList<>();
                                }
                        //////// 8
                        if (nyolcaspalya != null)
                            if (nyolcaspalya.jatekosok[0] == ownuser)
                                synchronized (nyolcaspalya.lovesekS1) {
                                    for (String uzi : nyolcaspalya.lovesekS1) {
                                        if (uzi.startsWith("WIN") || uzi.startsWith("LOS")) {
                                            faljbaIrat(ownuser.username + ".txt", uzi.substring(0, 3) + " " + uzi.substring(3));
                                            palyaeldobo(8);
                                            nyolcaspalya = null;
                                        }
                                        sendLine(uzi);
                                    }
                                    if (nyolcaspalya != null)
                                        nyolcaspalya.lovesekS1 = new ArrayList<>();
                                }
                            else
                                synchronized (nyolcaspalya.lovesekS2) {
                                    for (String uzi : nyolcaspalya.lovesekS2) {
                                        if (uzi.startsWith("WIN") || uzi.startsWith("LOS")) {
                                            faljbaIrat(ownuser.username + ".txt", uzi.substring(0, 3) + " " + uzi.substring(3));
                                            palyaeldobo(8);
                                            nyolcaspalya = null;
                                        }
                                        sendLine(uzi);
                                    }
                                    if (nyolcaspalya != null)
                                        nyolcaspalya.lovesekS2 = new ArrayList<>();
                                }
                        //////// 6
                        if (hatospalya != null)
                            if (hatospalya.jatekosok[0] == ownuser)
                                synchronized (hatospalya.lovesekS1) {
                                    for (String uzi : hatospalya.lovesekS1) {
                                        if (uzi.startsWith("WIN") || uzi.startsWith("LOS")) {
                                            faljbaIrat(ownuser.username + ".txt", uzi.substring(0, 3) + " " + uzi.substring(3));
                                            palyaeldobo(6);
                                            hatospalya = null;
                                        }
                                        sendLine(uzi);
                                    }
                                    if (hatospalya != null)
                                        hatospalya.lovesekS1 = new ArrayList<>();
                                }
                            else
                                synchronized (hatospalya.lovesekS2) {
                                    for (String uzi : hatospalya.lovesekS2) {
                                        if (uzi.startsWith("WIN") || uzi.startsWith("LOS")) {
                                            faljbaIrat(ownuser.username + ".txt", uzi.substring(0, 3) + " " + uzi.substring(3));
                                            palyaeldobo(6);
                                            hatospalya = null;
                                        }
                                        sendLine(uzi);
                                    }
                                    if (hatospalya != null)
                                        hatospalya.lovesekS2 = new ArrayList<>();
                                }
                    }
                    case "SHOOT" -> {
                        clientLine = clientReader.readLine();
                        String[] parts = clientLine.split(" ");
                        String palyameret = parts[0];
                        String sorsrting = parts[1];
                        String oszsrting = parts[2];
                        //int[] ezt= new int[]{Integer.parseInt(parts[1]),Integer.parseInt(parts[2])};
                        switch (palyameret) {
                            case "10" -> {
                                if (tizespalya != null)
                                    tizespalya.ralovok(ownuser.username, Integer.parseInt(sorsrting), Integer.parseInt(oszsrting));
                            }
                            case "8" -> {
                                if (nyolcaspalya != null)
                                    nyolcaspalya.ralovok(ownuser.username, Integer.parseInt(sorsrting), Integer.parseInt(oszsrting));
                            }
                            case "6" -> {
                                if (hatospalya != null)
                                    hatospalya.ralovok(ownuser.username, Integer.parseInt(sorsrting), Integer.parseInt(oszsrting));
                            }
                            default -> System.out.println("Unexpected value (10 / 8 / 6): " + palyameret);
                        }

                    }
                    case "CHAT" -> {
                        String chatuzi = clientReader.readLine();
                        try {
                            if (chatuzi.charAt(0) == '1') {
                                ownuser.enemy10.uzenetneki_que.put("CHAT10: " + chatuzi.substring(2));
                            } else if (chatuzi.charAt(0) == '8') {
                                ownuser.enemy8.uzenetneki_que.put("CHAT8: " + chatuzi.substring(1));
                            } else {
                                ownuser.enemy6.uzenetneki_que.put("CHAT6: " + chatuzi.substring(1));

                            }
                        } catch (InterruptedException e) {
                            System.out.println("A felhasználó üzenete nem lett továbbytva");
                        }
                    }
                    case "Stage1" -> {
                        if (tizespalya != null) {
                            ServerMatrix ellen;
                            if (tizespalya.jatekosok[0] == ownuser)
                                ellen = tizespalya.jatekosok[1];
                            else
                                ellen = tizespalya.jatekosok[0];
                            if (tizespalya.egyeskesz && tizespalya.ketteskesz) {//ez akkor is jó ha az ellen null
                                faljbaIrat(ownuser.username + ".txt", "LEFT 10 méretű pályán a pontjai: " + tizespalya.pontok[0] + " - " + tizespalya.pontok[1] + " a maximális " + tizespalya.maxpont + "-ből ellene: " + ellen.username);
                            }
                            if (ellen != null) {
                                ellen.uzenetneki_que.put("RES1");
                            }
                            palyaeldobo(1);
                            tizespalya = null;
                        }
                    }
                    case "Stage8" -> {
                        if (nyolcaspalya != null) {
                            ServerMatrix ellen;
                            if (nyolcaspalya.jatekosok[0] == ownuser)
                                ellen = nyolcaspalya.jatekosok[1];
                            else
                                ellen = nyolcaspalya.jatekosok[0];
                            if (nyolcaspalya.egyeskesz && nyolcaspalya.ketteskesz) {//ez akkor is jó ha az ellen null
                                faljbaIrat(ownuser.username + ".txt", "LEFT 8 méretű pályán a pontjai: " + nyolcaspalya.pontok[0] + " - " + nyolcaspalya.pontok[1] + " a maximális " + nyolcaspalya.maxpont + "-ből ellene: " + ellen.username);
                            }
                            if (ellen != null) {
                                ellen.uzenetneki_que.put("RES8");
                            }
                            palyaeldobo(8);
                            nyolcaspalya = null;
                        }
                    }
                    case "Stage6" -> {
                        if (hatospalya != null) {
                            ServerMatrix ellen;
                            if (hatospalya.jatekosok[0] == ownuser)
                                ellen = hatospalya.jatekosok[1];
                            else
                                ellen = hatospalya.jatekosok[0];
                            if (hatospalya.egyeskesz && hatospalya.ketteskesz) {//ez akkor is jó ha az ellen null
                                faljbaIrat(ownuser.username + ".txt", "LEFT 6 méretű pályán a pontjai: " + hatospalya.pontok[0] + " - " + hatospalya.pontok[1] + " a maximális " + hatospalya.maxpont + "-ből ellene: " + ellen.username);
                            }
                            if (ellen != null) {
                                ellen.uzenetneki_que.put("RES6");
                            }
                            palyaeldobo(6);
                            hatospalya = null;
                        }
                    }
                    default -> System.out.println("Unexpected value: " + clientLine);
                }
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Connection reset"))
                System.out.println(id + " Client sudoku");
            else
                e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (onlineusers) {
            for (int i = 0; i < onlineusers.size(); i++)
                if (onlineusers.get(i).equals(ownuser))
                    onlineusers.remove(i);
        }

        //küldök üzit az enemynek hogy reset, dobd el a pályát
        try {
            if (tizespalya != null) {
                if (ownuser.enemy10 != null)
                    ownuser.enemy10.uzenetneki_que.put("RES1");
            }
            if (nyolcaspalya != null) {
                if (ownuser.enemy8 != null)
                    ownuser.enemy8.uzenetneki_que.put("RES8");
            }
            if (hatospalya != null) {
                if (ownuser.enemy6 != null)
                    ownuser.enemy6.uzenetneki_que.put("RES6");

            }
        } catch (InterruptedException e) {
            System.out.println("Az enemy nem lett tájékoztatva a kilépésről");
        }
        palyaeldobo(1);
        palyaeldobo(8);
        palyaeldobo(6);
        System.out.println(id + " Server Thread sudoku");
    }

    private void palyaeldobo(int sorszam) {
        if (sorszam == 1)
            synchronized (Palya10) {
                Palya10.removeIf(ez -> ez.jatekosok[0].equals(ownuser) || (ez.jatekosok[1] != null && ez.jatekosok[1].equals(ownuser)));
            }
        else if (sorszam == 8)
            synchronized (Palya8) {
                Palya8.removeIf(ez -> ez.jatekosok[0].equals(ownuser) || (ez.jatekosok[1] != null && ez.jatekosok[1].equals(ownuser)));
            }
        else
            synchronized (Palya6) {
                Palya6.removeIf(ez -> ez.jatekosok[0].equals(ownuser) || (ez.jatekosok[1] != null && ez.jatekosok[1].equals(ownuser)));
            }
    }

    private void jatekkezelo(String uzenet) {
        /**
         * A bejövő lépést kell feldolgoznia ---Move---
         */
        String[] parts = uzenet.split(" ");
        String palyameret = parts[0];
        String sorsrting = parts[1];
        String oszsrting = parts[2];
        boolean flag = Boolean.parseBoolean(parts[3]);
        String hajoszama = parts[4];
        switch (palyameret) {
            case "10" ->
                // tizes pályán lép
                    tizespalya.babufelrak(ownuser, Integer.parseInt(sorsrting), Integer.parseInt(oszsrting), flag, Integer.parseInt(hajoszama));
            case "8" ->
                //nyolcas pályán lép
                    nyolcaspalya.babufelrak(ownuser, Integer.parseInt(sorsrting), Integer.parseInt(oszsrting), flag, Integer.parseInt(hajoszama));
            case "6" ->
                //hatos pályán lép
                    hatospalya.babufelrak(ownuser, Integer.parseInt(sorsrting), Integer.parseInt(oszsrting), flag, Integer.parseInt(hajoszama));
        }
    }

    private void jatekotindit(String clientLine, String meret) {
        /**
         * kikeresi hogy melyik játékba akar csatlakozni és belerakja:
         * felírja az enemyt, felírja a pályát amin játszik
         */
        ServerMatrix ures = new ServerMatrix(null, null);
        ures.username = "Új játék";
        ArrayList<Palya> ezenmegy = null;
        Palya ezendolgozik = null;
        int igaimeret = 10;
        ServerMatrix enemy = null;

        synchronized (onlineusers) {
            for (ServerMatrix azt : onlineusers) {
                if (azt.username.equals(clientLine)) {
                    enemy = azt;
                }
            }
        }
        switch (meret) {
            case "10" -> {
                ezenmegy = Palya10;
                ezendolgozik = tizespalya;
                ownuser.enemy10 = enemy;
                if (enemy != null)
                    enemy.enemy10 = ownuser;
            }
            case "8" -> {
                ezenmegy = Palya8;
                ezendolgozik = nyolcaspalya;
                igaimeret = 8;
                ownuser.enemy8 = enemy;
                if (enemy != null)
                    enemy.enemy8 = ownuser;
            }
            case "6" -> {
                ezenmegy = Palya6;
                ezendolgozik = hatospalya;
                igaimeret = 6;
                ownuser.enemy6 = enemy;
                if (enemy != null)
                    enemy.enemy6 = ownuser;
            }
            default -> System.out.println("Unexpected value (10 / 8 / 6): " + meret);
        }
        if (ezenmegy != null)
            synchronized (ezenmegy) {
                for (Palya ez : ezenmegy) {
                    if (ez.jatekosok[0].username.equals(clientLine) && ez.jatekosok[1] == null && ezendolgozik == null) {
                        if (ezendolgozik == tizespalya && meret.equals("10"))
                            tizespalya = ez;
                        else if (ezendolgozik == nyolcaspalya && meret.equals("8"))
                            nyolcaspalya = ez;
                        else
                            hatospalya = ez;
                        ezendolgozik = ez;
                    }
                }
                if (ezendolgozik != null) {
                    if (ezendolgozik.jatekosok[0].username.equals("Új játék")) {
                        ezendolgozik.jatekosok[0] = ownuser;
                        System.out.println(ezendolgozik.jatekosok[0].username + " az első játékos");
                        ezenmegy.add(new Palya(igaimeret, ures, null));
                    } else {
                        ezendolgozik.jatekosok[1] = ownuser;
                        ezendolgozik.kijon = ezendolgozik.jatekosok[0];
                        System.out.println(ezendolgozik.jatekosok[1].username + " a második játékos");
                        try {
                            ezendolgozik.jatekosok[0].uzenetneki_que.put("CHAT" + igaimeret + " *" + ownuser.username + " csatlakozott a játékodhoz*");
                            ezendolgozik.jatekosok[1].uzenetneki_que.put("CHAT" + igaimeret + " *Csatlakoztál a játékhoz*");
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    }

    public void sendusers() {
        sendLine("Users please");
        sendLine(ownuser.username);
        synchronized (Palya10) {
            sendhelper(Palya10);
        }
        synchronized (Palya8) {
            sendhelper(Palya8);
        }
        synchronized (Palya6) {
            sendhelper(Palya6);
        }
        sendLine("That's all folks");
    }

    private void sendhelper(ArrayList<Palya> ezen) {
        for (Palya ez : ezen)
            if (!ez.jatekosok[0].equals(ownuser) && !(ez.jatekosok[1] != null && ez.jatekosok[1].equals(ownuser))) {
                if (ez.jatekosok[1] == null) {
                    sendLine(ez.jatekosok[0].username);
                    sendLine("");
                    String m = String.valueOf(ez.meret);
                    sendLine(m);
                } else if (!ez.jatekosok[1].equals(ownuser)) {
                    sendLine(ez.jatekosok[0].username);
                    sendLine(ez.jatekosok[1].username);
                    String m = String.valueOf(ez.meret);
                    sendLine(m);
                }
            }
    }
}
