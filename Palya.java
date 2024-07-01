package com.example.kalgr_projekt_bat;

import java.util.ArrayList;
import java.util.Arrays;

/*
    -1=nem lehet rakni
    0= víz
    1= hajó
    2= nem talált
    3= talált
    10x10: 5, 4, 3, 3, 2
    8x8  : 4, 3, 3, 2, 2
    6x6  : 3, 3, 2, 1
     */
public class Palya {
    private static int ID = 0;
    public int id;
    public ServerMatrix[] jatekosok;
    public int[][] palya1;
    public int[][] palya2;
    public int meret, maxpont;
    public int[] pontok;

    public int[] getBabuMeretek() {
        return babuMeretek;
    }

    private int[] babuMeretek;
    public ServerMatrix kijon;
    public ArrayList<String> lovesekS1, lovesekS2;
    public ArrayList<int[]> loveseki1, loveseki2;
    public boolean egyeskesz = false, ketteskesz = false;
    private boolean nincsvege = true;

    public Palya(int meret, ServerMatrix jatekos1, ServerMatrix jatekos2) {
        this.meret = meret;
        this.palya1 = new int[meret][meret];
        this.palya2 = new int[meret][meret];
        this.jatekosok = new ServerMatrix[]{jatekos1, jatekos2};
        initBabuMeretek(meret);
        this.id = Palya.ID++;
        this.kijon = jatekos1;
        lovesekS1 = new ArrayList<>();
        lovesekS2 = new ArrayList<>();
        loveseki1 = new ArrayList<>();
        loveseki2 = new ArrayList<>();
        pontok = new int[]{0, 0};
    }

    //palya1--jatekos[0];
    //palya2--jatekos[1]
    private int meglottek(ServerMatrix jatekos, int sor, int osz) { //házon belüli
        int[][] matrix;
        sor--;
        osz--;//indexelés
        if (jatekosok[0].equals(jatekos)) //lecserélhetjük a kijön-re vagy hagyjuk a threadben?
            matrix = palya2;
        else
            matrix = palya1;
        int eredmeny = switch (matrix[sor][osz]) {
            case -1, 0, 2 -> 2;
            case 1, 3 -> 3;
            default -> 0;
        };
        matrix[sor][osz] = eredmeny;
        pontok[0] = eredmenyszamoto(palya2);
        pontok[1] = eredmenyszamoto(palya1);
        return eredmeny;
    }

    public int eredmenyszamoto(int[][] matrix) {
        int pontsazma = 0;
        for (int[] ez : matrix)
            for (int az : ez)
                if (az == 3)
                    pontsazma++;
        return pontsazma;
    }

    public void ralovok(String jatekosnev, int sor, int osz) {
        int[] ezt = new int[]{sor, osz};
        if (jatekosok[0].username.equals(jatekosnev)) {
            synchronized (loveseki1) {
                loveseki1.add(ezt);
            }
        } else {
            synchronized (loveseki2) {
                loveseki2.add(ezt);
            }
        }
    }

    private void initBabuMeretek(int meret) {
        if (meret == 10) {
            maxpont = 17;
            babuMeretek = new int[]{5, 4, 3, 3, 2};
        } else if (meret == 8) {
            maxpont = 14;
            babuMeretek = new int[]{4, 3, 3, 2, 2};
        } else /*if (meret == 6)*/ {
            maxpont = 9;
            babuMeretek = new int[]{3, 3, 2, 1};
        }
    }

    public boolean babufelrak(ServerMatrix user, int sor, int oszlop, boolean vizszint, int babuIndex) {
        int[][] matrix;
        if (jatekosok[0] == user) {
            matrix = palya1;
            if (babuIndex + 1 == babuMeretek.length) egyeskesz = true;
        } else {
            matrix = palya2;
            if (babuIndex + 1 == babuMeretek.length) ketteskesz = true;
        }
        sor--;
        oszlop--; //idexelés végett
        int babuhossz = babuMeretek[babuIndex];
        int babuvegesor, babuvegeosz;
        if (vizszint) {
            babuvegesor = sor + babuhossz - 1;
            babuvegeosz = oszlop;
        } else {
            babuvegesor = sor;
            babuvegeosz = oszlop + babuhossz - 1;
        }
        int[][] koordinatak = new int[2][babuhossz];
        if (babuvegesor <= meret - 1 && babuvegeosz <= meret - 1) {
            //good - bent van
            if (vizszint) {
                for (int i = 0; i < babuhossz; i++) {
                    koordinatak[0][i] = oszlop;
                    koordinatak[1][i] = sor + i;
                }
            } else {
                for (int i = 0; i < babuhossz; i++) {
                    koordinatak[0][i] = oszlop + i;
                    koordinatak[1][i] = sor;
                }
            }
            //koordináták kiírva
            if (uresAhely(matrix, koordinatak)) {
                System.out.println("helyes báburakás");
                babufelpakol(matrix, koordinatak);
            } else {
                System.out.println("helytelen báburakás");
                return false;
            }
        } else {
            //bad - kint van
            return false;
        }
        checkMatrixNeighbors(matrix);
        //printout(); //<<<----------------
        return true;
    }

    private boolean uresAhely(int[][] matrix, int[][] koord) {
        for (int i = 0; i < koord[0].length; i++)
            if (matrix[koord[0][i]][koord[1][i]] == -1 || matrix[koord[0][i]][koord[1][i]] == 1)
                return false;
        return true;
    }

    private void babufelpakol(int[][] matrix, int[][] koord) {
        for (int i = 0; i < koord[0].length; i++)
            matrix[koord[0][i]][koord[1][i]] = 1;
    }

    public void printout() {
        for (int i = 0; i < meret; i++) {
            System.out.println(Arrays.toString(palya1[i]));
        }
        System.out.println("volt a palya1 és palya2 pedig:");
        for (int i = 0; i < meret; i++) {
            System.out.println(Arrays.toString(palya2[i]));
        }
    }

    public void checkMatrixNeighbors(int[][] matrix) { //this shit somehow works
        for (int i = 0; i < meret; i++) {
            for (int j = 0; j < meret; j++) {
                // Ha az elem értéke 1 vagy -1, nincs teendő
                if (matrix[i][j] == 1 || matrix[i][j] == -1 || matrix[i][j] == 2 || matrix[i][j] == 3)
                    continue;
                boolean hasPositiveNeighbor = false;
                // Vizszintes
                for (int k = i - 1; k <= i + 1; k++) {
                    if (k < 0 || k >= meret)
                        continue;
                    if (matrix[k][j] == 1)
                        hasPositiveNeighbor = true;
                }
                // Függőleges
                for (int k = j - 1; k <= j + 1; k++) {
                    if (k < 0 || k >= meret)
                        continue;
                    if (matrix[i][k] == 1)
                        hasPositiveNeighbor = true;
                }
                // Átlós
                for (int k = i - 1; k <= i + 1; k++) {
                    for (int l = j - 1; l <= j + 1; l++) {
                        if (k < 0 || k >= meret || l < 0 || l >= meret)
                            continue;
                        if (matrix[k][l] == 1)
                            hasPositiveNeighbor = true;
                    }
                }
                if (hasPositiveNeighbor)
                    matrix[i][j] = -1;
            }
        }
    }

    public void runPls() {
        String uzenet;
        if (egyeskesz && ketteskesz && nincsvege) {
            if (pontok[0] != maxpont || pontok[1] != maxpont) {
                if (kijon == jatekosok[0]) {
                    //egyes lő
                    synchronized (lovesekS1) {
                        synchronized (lovesekS2) {
                            if (loveseki1.size() > 0) {
                                int[] lovet = loveseki1.remove(0);
                                int vissza = meglottek(jatekosok[0], lovet[0], lovet[1]);
                                uzenet = "SHOOT" + meret + " " + lovet[0] + " " + lovet[1] + " " + vissza;
                                lovesekS1.add(uzenet + " true");
                                lovesekS2.add(uzenet + " false");
                                kijon = jatekosok[1];
                            }
                        }
                    }
                } else {
                    //kettes lő
                    synchronized (lovesekS1) {
                        synchronized (lovesekS2) {
                            if (loveseki2.size() > 0) {
                                int[] lovet = loveseki2.remove(0);
                                int vissza = meglottek(jatekosok[1], lovet[0], lovet[1]);
                                uzenet = "SHOOT" + meret + " " + lovet[0] + " " + lovet[1] + " " + vissza; //lov= koordinata, vissza= mire változtassa a kockát
                                lovesekS2.add(uzenet + " true");
                                lovesekS1.add(uzenet + " false");
                                kijon = jatekosok[0];
                            }
                        }
                    }
                }
            }
            if (pontok[0] == maxpont || pontok[1] == maxpont) {
                String uzike = meret + " méretű pályán a pontjai: " + pontok[0] + " - " + pontok[1] + " a maximális: " + maxpont + "-ből";
                if (pontok[0] == maxpont) {
                    //egyes nyert
                    lovesekS1.add("WIN" + uzike + " ellene: " + jatekosok[1].username);
                } else {
                    //egyes vesztett
                    lovesekS1.add("LOS" + uzike + " ellene: " + jatekosok[1].username);
                }
                if (pontok[1] == maxpont) {
                    //kettes nyert
                    lovesekS2.add("WIN" + uzike + " ellene: " + jatekosok[0].username);
                } else {
                    //kettes vesztett
                    lovesekS2.add("LOS" + uzike + " ellene: " + jatekosok[0].username);
                }
                nincsvege = false;
            }
        }
    }
}
